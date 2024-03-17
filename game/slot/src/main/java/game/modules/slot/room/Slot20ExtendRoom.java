
package game.modules.slot.room;

import bitzero.server.BitZeroServer;
import bitzero.server.entities.User;
import bitzero.util.common.business.Debug;
import com.google.gson.Gson;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.usercore.dao.impl.UserDaoImpl;
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.models.cache.SlotFreeDaily;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.models.slot.SlotFreeSpin;
import com.vinplay.vbee.common.response.MoneyResponse;
import com.vinplay.vbee.common.statics.TransType;
import com.vinplay.vbee.common.utils.DateTimeUtils;
import game.modules.slot.Slot20ExtendModule;
import game.modules.slot.SlotModule;
import game.modules.slot.cmd.Slot25CommandCollection;
import game.modules.slot.cmd.send.slot20extend.*;
import game.modules.slot.entities.slot.AutoUser;
import game.modules.slot.entities.slot.AwardsOnLine;
import game.modules.slot.entities.slot.Line;
import game.modules.slot.entities.slot.MiniGameSlotResponse;
import game.modules.slot.entities.slot.line20extend.Slot20ExtendAward;
import game.modules.slot.entities.slot.line20extend.Slot20ExtendAwards;
import game.modules.slot.entities.slot.line20extend.Slot20ExtendItem;
import game.modules.slot.entities.slot.line20extend.Slot20ExtendLines;
import game.modules.slot.listener.SlotLogListener;
import game.modules.slot.utils.Slot20ExtendUtil;
import game.modules.slot.utils.SlotUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Slot20ExtendRoom extends SlotRoom {
    private final Runnable gameLoopTask = new GameLoopTask();
    private final Runnable checkResetPotTask = new CheckResetPot();
    private final Slot20ExtendLines lines = new Slot20ExtendLines();
    private long lastTimeUpdatePotToRoom = 0L;
    private long lastTimeUpdateFundToRoom = 0L;
    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    private final Slot25CommandCollection commandCollection;
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("slot");

    private SlotLogListener logListener;

    int ROW = 3;
    int COLUMN = 5;

    public Slot20ExtendRoom(SlotModule module, Slot25CommandCollection commandCollection, SlotLogListener logListener, String gameName, byte id, String room, short moneyType, long pot, long fund, int betValue, long initJackpotValue) {

        super(id, room, betValue, moneyType, pot, fund, initJackpotValue);

        this.module = module;
        this.commandCollection = commandCollection;
        this.logListener = logListener;
        this.moneyType = moneyType;
        this.gameName = gameName;
        this.cacheFreeSpinName = this.gameName + betValue;

        // init jackpot value
        CacheServiceImpl cacheService = new CacheServiceImpl();
        cacheService.setValue(room, (int) pot);

        this.betValue = betValue;
        this.initJackpotValues = initJackpotValue;

        try {
            this.cachePercentFeeName = gameName + "PERCENT_FEE";
            this.percentFee = cacheService.getValueInt(cachePercentFeeName);
        } catch (Exception ex) {
            this.percentFee = 2;
            cacheService.setValue(cachePercentFeeName, percentFee);
        }

        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.gameLoopTask, 10, 1, TimeUnit.SECONDS);
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.checkResetPotTask, 10, 10, TimeUnit.SECONDS);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void forceStopAutoPlay(User user) {
        super.forceStopAutoPlay(user);
        synchronized (this.usersAuto) {
            this.usersAuto.remove(user.getName());
            Slot20ExtendForceStopAutoPlayMsg msg = new Slot20ExtendForceStopAutoPlayMsg(commandCollection.FORCE_AUTO_PLAY_MESSAGE);
            SlotUtils.sendMessageToUser(msg, user);
        }
    }

    public Slot20ExtendResultMsg play(String username, String linesStr) {
        long referenceId = this.module.getNewReferenceId();
        return this.playNormal(username, linesStr, referenceId);
    }

    /**
     * @param username    tên hiển thị người chơi
     * @param linesStr    số line được chọn
     * @param referenceId mã tham chiếu giao dịch
     * @return ResultBenleyMsg model kết quả
     */
    public synchronized Slot20ExtendResultMsg playNormal(String username, String linesStr, long referenceId) {
        // kết quả mặc định
        short result = ResultSlot.MISSED;
        // thời điểm hiện tại
        String currentTimeStr = DateTimeUtils.getCurrentTime();
        // response message
        Slot20ExtendResultMsg playResponse = new Slot20ExtendResultMsg(commandCollection.RESULT_MESSAGE);
        // số line người chơi chọn
        String[] selectedLines = linesStr.split(",");
        // tổng cược
        long totalBetValue = (long) selectedLines.length * this.betValue;
        // từ PHP admin, cài đặt cho một người chơi trúng JACKPOT
        boolean forceJackpotToUser = false;
        // người chơi được set nổ hũ
        String usernameForce;
        // phòng được set nổ hũ
        String roomForce;
        // Lớp dịch vụ caching
        CacheServiceImpl cacheService = new CacheServiceImpl();
        try {
            usernameForce = cacheService.getValueStr(CACHE_NAME_USER_SPOT + gameName);
            roomForce = cacheService.getValueStr(CACHE_BET_VALUE_SLOT + gameName);
        } catch (Exception e) {
            usernameForce = "";
            roomForce = "";
        }
        // thông tin user
        UserCacheModel u = userService.getUser(username);
        // số dư hiện tại của user
        long currentMoney = userService.getMoneyUserCache(username, this.moneyTypeStr);
        // thông tin free spin
        int numOfFreeSpin = getNumOfFreeSpin(username);
        boolean isSpinningFree = numOfFreeSpin > 0;
        // số lines được chọn > 0
        if (selectedLines.length > 0 && !linesStr.isEmpty()) {
            // check tiền đặt cược hợp lệ
            if (totalBetValue > 0L) {
                // check đủ tiền
                if (totalBetValue <= currentMoney || isSpinningFree) {
                    // trừ phế theo số % được cấu hình
                    long fee = !isSpinningFree ? totalBetValue * percentFee / 100L : 0;
                    MoneyResponse moneyRes = new MoneyResponse(false, "1001");
                    // trừ tiền cược
                    // chỉ trừ tiền người chơi thật
                    if (!u.isBot()) {
                        long changeMoney = isSpinningFree ? 0 : totalBetValue;
                        String desc = isSpinningFree ? "Lượt quay miễn phí " + gameName : "Đặt cược " + gameName;
                        moneyRes = this.userService.updateMoney(username, -changeMoney, this.moneyTypeStr, this.gameName, "Quay " + gameName, desc, fee, referenceId, TransType.START_TRANS);
                    } else {
                        moneyRes.setSuccess(true);
                    }

                    if (moneyRes != null && moneyRes.isSuccess()) {
                        // 2 phần trăm cho vào hũ JACKPOT
                        long moneyToPot = !isSpinningFree ? totalBetValue * 2 / 100L : 0;
                        this.pot += moneyToPot;

                        // số tiền còn lại sau khi trừ phế và 2% POT cho vào quỹ thưởng
                        long moneyToFund = !isSpinningFree ? totalBetValue - fee - moneyToPot : 0;
                        if (!u.isBot()) {
                            this.fund += moneyToFund;
                        }
                        // cờ này được sử dụng để check liệu có tiếp tục vòng lặp để sinh Matrix hay không
                        boolean enoughPair = false;
                        // tổng tiền thắng được tính toán trên toàn bộ Lines được chọn
                        long totalPrizes;
                        // đếm số lượt xuất hiện Scatter
                        int countScatter;
                        // đếm số lượt bonus
                        int countBonus;
                        // Bonus Game Info model
                        MiniGameSlotResponse bonusGameResponse;
                        // danh sách giải thưởng được tính toán trên toàn bộ Lines được chọn
                        ArrayList<AwardsOnLine<Slot20ExtendAward>> awardsOnLines = new ArrayList<>();

                        // BẮT ĐẦU QUÁ TRÌNH SINH MA TRẬN KẾT QUẢ VÀ TÍNH TOÁN GIẢI THƯỞNG
                        while (!enoughPair) {
                            // khởi tạo lại các giá trị mặc định sau mỗi lần lặp
                            result = ResultSlot.MISSED;
                            awardsOnLines.clear();
                            totalPrizes = 0L;
                            bonusGameResponse = null;
                            countScatter = 0;
                            countBonus = 0;
                            boolean isForceJackpot = false;

                            // xử lý FORCE nổ hũ
                            if (betValue == 100) {
                                if (usernameForce.equals(username) && roomForce.equals(String.valueOf(100))) {
                                    isForceJackpot = true;
                                    forceJackpotToUser = true;
                                    result = ResultSlot.JACKPOT;
                                }
                            } else if (betValue == 1000) {
                                if (usernameForce.equals(username) && roomForce.equals(String.valueOf(1000))) {
                                    isForceJackpot = true;
                                    forceJackpotToUser = true;
                                    result = ResultSlot.JACKPOT;
                                }
                            } else {
                                if (usernameForce.equals(username) && roomForce.equals(String.valueOf(10000))) {
                                    isForceJackpot = true;
                                    forceJackpotToUser = true;
                                    result = ResultSlot.JACKPOT;
                                }
                            }

                            // sinh Matrix
                            Slot20ExtendItem[][] matrix = isForceJackpot
                                    ? Slot20ExtendUtil.generateMatrixNoHu(selectedLines)
                                    : Slot20ExtendUtil.generateMatrix();

                            // MÃ LỆNH NÀY ÁP ỤNG CHO SLOT MACHINE 20LINE EXTENDS.
                            // Trường hợp 1 WHEEL có xuất hiện item WILD, toàn bộ WHEEL đó sẽ được thay thế bởi nó
                            // Trong trường hợp này (Slot Machine 25Line Basic thì không áp dụng)
                            Slot20ExtendItem[][] matrixWild = Slot20ExtendUtil.revertMatrix(matrix);


                            // Đếm số lượng BONUS và SCATTER
                            for (int i = 0; i < ROW; ++i) {
                                for (int j = 0; j < COLUMN; ++j) {
                                    if (matrix[i][j] == Slot20ExtendItem.SCATTER) {
                                        ++countScatter;
                                        continue;
                                    }
                                    if (matrix[i][j] == Slot20ExtendItem.BONUS) {
                                        ++countBonus;
                                    }
                                }
                            }

                            // không cho phép JACKPOT và (BONUS hoặc FREE SPIN) xảy ra đồng thời
                            if (isForceJackpot && (countBonus >= 3 || countScatter >= 3)) {
                                continue;
                            }


                            // không cho phép BONUS và FREE SPIN xảy ra đồng thời
                            if (countBonus >= 3 && countScatter >= 3) {
                                continue;
                            }

                            // ở chế độ Free Spin, không cho phép trúng BONUS hoặc SCATTER
                            if (isSpinningFree && (countScatter >= 3 || countBonus >= 3)) {
                                continue;
                            }

                            // Tính toán phần thưởng cho BONUS GAME
                            if (countBonus >= 3) {
                                bonusGameResponse = Slot20ExtendUtil.buildBonusGameData(this.betValue, countBonus);
                                Slot20ExtendAward bonusAward = Slot20ExtendAwards.getAward(Slot20ExtendItem.BONUS, countBonus);
                                AwardsOnLine<Slot20ExtendAward> aol = new AwardsOnLine<>(bonusAward, bonusGameResponse.getTotalPrize(), "line0");
                                awardsOnLines.add(aol);
                                result = ResultSlot.BONUS_GAME;
                            }

                            // Duyệt toàn bộ Lines được chọn bởi người chơi để tính toán giải thưởng trên từng Line
                            for (String selectedLine : selectedLines) {
                                ArrayList<Slot20ExtendAward> awardList = new ArrayList<>();
                                int lineNumber = Integer.parseInt(selectedLine);
                                Line line = Slot20ExtendUtil.getLine(this.lines, matrixWild, lineNumber);
                                Slot20ExtendUtil.calculateMoneyAwardInLine(line, awardList);
                                for (Slot20ExtendAward award : awardList) {
                                    long moneyOnLine = (long) (award.getRatio() * this.betValue);
                                    AwardsOnLine<Slot20ExtendAward> aol2 = new AwardsOnLine<>(award, moneyOnLine, line.getName());
                                    awardsOnLines.add(aol2);
                                }
                            }


                            // Tiếp theo, tính toán toàn bộ giải thưởng
                            boolean isGetJackpotNaturally = false;
                            StringBuilder builderLinesWin = new StringBuilder();
                            StringBuilder builderPrizesOnLine = new StringBuilder();
                            for (AwardsOnLine<Slot20ExtendAward> award : awardsOnLines) {
                                totalPrizes += award.getMoney();

                                builderLinesWin.append(",");
                                builderLinesWin.append(award.getLineId());

                                builderPrizesOnLine.append(",");
                                builderPrizesOnLine.append(award.getMoney());

                                if (result != ResultSlot.JACKPOT && award.getAward() == Slot20ExtendAward.JACKPOT) {
                                    result = ResultSlot.JACKPOT;
                                    isGetJackpotNaturally = true;
                                }
                            }

                            if (builderLinesWin.length() > 0) {
                                builderLinesWin.deleteCharAt(0);
                            }
                            if (builderPrizesOnLine.length() > 0) {
                                builderPrizesOnLine.deleteCharAt(0);
                            }


                            // Kiểm tra xem giải thưởng có LỚN hay không.
                            // Lớn quá thì sinh lại MATRIX kết quả kẻo anh em NPH vỡ nợ
                            if (!isForceJackpot) {
                                // Trúng nổ hũ một cách ngẫu nhiên nhưng qũy thưởng lại không đủ bù lỗ
                                if (isGetJackpotNaturally && fund < initJackpotValues) {
                                    continue;
                                }
                                // Tuy không trúng JACKPOT nhưng trúng Line to quá cũng cần sinh lại MATRIX
                                if (!isGetJackpotNaturally) {
                                    if ((totalPrizes - totalBetValue > 0 && totalPrizes > fund) || totalPrizes >= totalBetValue * 25)
                                        continue;
                                }
                            }

                            // điều kiện trúng thưởng đã thỏa mãn, dừng vòng lặp
                            enoughPair = true;

                            // BẮT ĐẦU QUÁ TRÌNH LƯU TRỮ THÔNG TIN VÀ TRẢ THƯỞNG
                            String matrixStr = Slot20ExtendUtil.matrixToString(matrix);
                            if (totalPrizes > 0L) {
                                if (result == ResultSlot.JACKPOT) {
                                    this.pot = this.initJackpotValues;
                                    this.fund -= initJackpotValues;

                                    // get user cache
                                    HazelcastInstance client = HazelcastClientFactory.getInstance();
                                    IMap<String, UserModel> userMap = client.getMap("users");
                                    UserModel model;
                                    String displayName = username;
                                    if (userMap.containsKey(username)) {
                                        model = userMap.get(displayName);
                                        if (model.getClient() != null && !Objects.equals(model.getClient(), "")) {
                                            displayName = "[" + model.getClient() + "] " + username;
                                        } else {
                                            displayName = "[X] " + username;
                                        }
                                    } else {
                                        UserDaoImpl dao = new UserDaoImpl();
                                        try {
                                            model = dao.getUserByNickName(username);
                                            if (model.getClient() != null && !Objects.equals(model.getClient(), "")) {
                                                displayName = "[" + model.getClient() + "] " + username;
                                            } else {
                                                displayName = "[X] " + username;
                                            }
                                        } catch (SQLException ignored) {
                                        }
                                    }
                                    if (forceJackpotToUser) {
                                        try {
                                            cacheService.removeKey(CACHE_NAME_USER_SPOT + gameName);
                                            cacheService.removeKey(CACHE_BET_VALUE_SLOT + gameName);
                                        } catch (Exception e) {
                                            logger.error(" Reset cache " + gameName + " error with : " + e.getMessage());
                                        }
                                    }
                                    this.slotService.logNoHu(referenceId, this.gameName, displayName, this.betValue, linesStr, matrixStr, builderLinesWin.toString(), builderPrizesOnLine.toString(), totalPrizes, result, currentTimeStr);
                                } else {
                                    if (!u.isBot()) {
                                        this.fund -= totalPrizes;
                                    }
                                    if (result == ResultSlot.MISSED) {
                                        result = totalPrizes >= (this.betValue * 175L) ? ResultSlot.BIG_WIN : ResultSlot.WIN;
                                    }
                                }
                            }


                            // cập nhật và tính toán lượt quay miễn phí
                            SlotFreeSpin slotFreeSpin = slotService.updateLuotQuaySlotFree(cacheFreeSpinName, username);
                            playResponse.freeSpin = (byte) this.setFreeSpin(username, linesStr, countScatter, slotFreeSpin.getNum());
                            if (countScatter >= 3) {
                                playResponse.isFreeSpin = true;
                                if (result != ResultSlot.BONUS_GAME && result != ResultSlot.JACKPOT) {
                                    result = ResultSlot.FREE_SPIN;
                                }
                            }


                            // only save real user
                            long moneyExchange = totalPrizes;
                            if (moneyExchange != 0 && !u.isBot()) {
                                moneyRes = this.userService.updateMoney(username, moneyExchange, this.moneyTypeStr, this.gameName, "Quay " + gameName, this.buildDescription(totalBetValue, totalPrizes, result), 0L, referenceId, TransType.END_TRANS);
                                if (moneyRes != null && moneyRes.isSuccess()) {
                                    currentMoney = moneyRes.getCurrentMoney();
                                    // thông báo tới toàn hệ thống số tiền thắng của người chơi
                                    if (this.moneyType == 1 && moneyExchange - this.betValue >= (totalBetValue * 1.5)) {
                                        this.broadcastMsgService.putMessage(Games.findGameByName(gameName).getId(), username, moneyExchange - (long) this.betValue);
                                    }
                                }
                            }


                            String linesWin = builderLinesWin.toString();
                            String prizesOnLine = builderPrizesOnLine.toString();
                            playResponse.referenceId = referenceId;
                            playResponse.matrix = matrixStr;
                            playResponse.linesWin = linesWin;
                            playResponse.prize = totalPrizes;
                            if (bonusGameResponse != null) {
                                playResponse.haiSao = bonusGameResponse.getPrizes();
                            }
                            try {
                                // lưu nhật ký chơi
                                if (!u.isBot()) {
                                    this.logListener.log(referenceId, username, this.betValue, linesStr, linesWin, prizesOnLine, result, totalPrizes, currentTimeStr, matrixStr);
                                }
                                // lưu nhật ký nổ hũ
                                if (result == ResultSlot.JACKPOT) {
                                    this.slotService.addTop(gameName, username, this.betValue, totalPrizes, currentTimeStr, result);
                                }
                                // thông báo tới toàn bộ người chơi trong game (trong MODULE)
                                if (result == ResultSlot.JACKPOT || result == ResultSlot.BIG_WIN) {
                                    Slot20ExtendBigWinMsg bigWinMsg = new Slot20ExtendBigWinMsg(commandCollection.BIG_WIN_MESSAGE);
                                    bigWinMsg.username = username;
                                    bigWinMsg.type = (byte) result;
                                    bigWinMsg.betValue = (short) this.betValue;
                                    bigWinMsg.totalPrizes = totalPrizes;
                                    bigWinMsg.timestamp = DateTimeUtils.getCurrentTime();
                                    this.module.sendMsgToAllUsers(bigWinMsg);
                                }
                            } catch (InterruptedException | TimeoutException | IOException ignored) {
                                // empty catch block
                            }
                            // lưu thông tin quỹ
                            this.saveFund();
                            // lưu thông tin HŨ
                            this.savePot();
                        }
                    }
                } else {
                    result = ResultSlot.NOT_ENOUGH_MONEY;
                }
            } else {
                result = ResultSlot.INVALID_BET_VALUE;
            }
        } else {
            result = ResultSlot.INVALID_BET_VALUE;
        }
        playResponse.result = (byte) result;
        playResponse.currentMoney = currentMoney;

        // update cache tien hu
        cacheService.setValue(CACHE_JACK_POT_VALUE_SLOT + "_" + this.betValue + "_" + gameName, String.valueOf(this.pot));
        if (result == ResultSlot.JACKPOT) {
            this.sendNotifyNoHu(username, (byte) 1, playResponse.prize, gameName);
        }
        if (!u.isBot()) {
            System.out.println(new Gson().toJson(playResponse));
        }
        return playResponse;
    }

    private int setFreeSpin(String nickName, String lines, int countFreeSpin, int remainAmountOfFreeSpin) {
        int soLuot = 0;
        switch (countFreeSpin) {
            case 3: {
                soLuot = 5 + remainAmountOfFreeSpin;
                slotService.setLuotQuayFreeSlot(this.cacheFreeSpinName, nickName, lines, 5, 1, betValue);
                break;
            }
            case 4: {
                soLuot = 10 + remainAmountOfFreeSpin;
                slotService.setLuotQuayFreeSlot(this.cacheFreeSpinName, nickName, lines, 10, 2, betValue);
                break;
            }
            case 5: {
                soLuot = 20 + remainAmountOfFreeSpin;
                slotService.setLuotQuayFreeSlot(this.cacheFreeSpinName, nickName, lines, 20, 3, betValue);
                break;
            }
        }
        return Math.max(soLuot, remainAmountOfFreeSpin);
    }


    public short play(User user, String linesStr) throws Exception {
        String username = user.getName();
        Slot20ExtendResultMsg msg;
        Slot20ExtendFreeDailyMsg freeDailyMsg = new Slot20ExtendFreeDailyMsg(commandCollection.FREE_DAILY_MESSAGE);
        freeDailyMsg.remain = 0;
        msg = this.play(username, linesStr);
        if (this.isUserMinimize(user)) {
            Slot20ExtendMinimizeResultMsg miniMsg = new Slot20ExtendMinimizeResultMsg(commandCollection.MINIMIZE_RESULT_MESSAGE);
            miniMsg.prize = msg.prize;
            miniMsg.curretMoney = msg.currentMoney;
            miniMsg.result = msg.result;
            SlotUtils.sendMessageToUser(miniMsg, user);
        } else {
            SlotUtils.sendMessageToUser(msg, user);
            SlotUtils.sendMessageToUser(freeDailyMsg, user);
        }
        return msg.result;
    }

    private void saveFund() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - this.lastTimeUpdateFundToRoom >= 60000L) {
            try {
                this.miniGameService.saveFund(this.name, this.fund);
            } catch (IOException | InterruptedException | TimeoutException e) {
                Debug.trace(this.gameName + ": update fund error ", e.getMessage());
            }
            this.lastTimeUpdateFundToRoom = currentTime;
        }
    }

    private void savePot() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - this.lastTimeUpdatePotToRoom >= 1000L) {
            this.lastTimeUpdatePotToRoom = currentTime;
            try {
                this.miniGameService.savePot(this.name, this.pot, this.huX2);
            } catch (IOException | InterruptedException | TimeoutException e) {
                Debug.trace(this.gameName + ": update pot error ", e.getMessage());
            }
        }

        byte x2 = (byte) (this.huX2 ? 1 : 0);
        ((Slot20ExtendModule) this.module).updatePot(this.id, this.pot, x2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void gameLoop() {
        ArrayList<AutoUser> usersPlay = new ArrayList<>();
        synchronized (this.usersAuto) {
            for (AutoUser user : this.usersAuto.values()) {
                boolean play = user.incCount();
                if (!play) continue;
                usersPlay.add(user);
            }
        }
        int numThreads = usersPlay.size() / 100 + 1;
        for (int i = 1; i <= numThreads; ++i) {
            int fromIndex = (i - 1) * 100;
            int toIndex = i * 100;
            if (toIndex > usersPlay.size()) {
                toIndex = usersPlay.size();
            }
            ArrayList<AutoUser> tmp = new ArrayList<>(usersPlay.subList(fromIndex, toIndex));
            PlayListAutoUserTask task = new PlayListAutoUserTask(tmp);
            this.executor.execute(task);
        }
        usersPlay.clear();
    }

    @Override
    protected void checkResetPot() {
        try {

            int isReset = cacheService.getValueInt("reset_pot_" + gameName + "_" + this.betValue);
            if (isReset == 1) {
                this.pot = this.initJackpotValues;
                this.fund = 0;
                this.savePot();
                this.saveFund();
                this.cacheService.removeKey("reset_pot_" + gameName + "_" + this.betValue);

            }
        } catch (Exception ignored) {
        }
    }

    public boolean isBot(String nickName) {
        try {
            UserCacheModel u = this.userService.getUser(nickName);
            return u.isBot();
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    protected void playListAuto(List<AutoUser> users) {
        for (AutoUser user : users) {
            try {
                short result = this.play(user.getUser(), user.getLines());
                if (result == ResultSlot.JACKPOT
                        || result == ResultSlot.JACKPOT_X2
                        || result == ResultSlot.INVALID_BET_VALUE
                        || result == ResultSlot.NOT_ENOUGH_MONEY
                        || result == ResultSlot.SYSTEM_ERROR) {
                    this.forceStopAutoPlay(user.getUser());
                    continue;
                }
                if (result == ResultSlot.MISSED) {
                    user.setMaxCount(5);
                    continue;
                }
                if (result == ResultSlot.BONUS_GAME) {
                    user.setMaxCount(20);
                    continue;
                }
                user.setMaxCount(8);
            } catch (Exception ex) {
                Logger.getLogger(Slot20ExtendRoom.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        users.clear();
    }

    @Override
    public boolean joinRoom(User user) {
        boolean result = super.joinRoom(user);
        SlotFreeDaily model = this.slotService.getLuotQuayFreeDaily(this.gameName, user.getName(), this.betValue);
        Slot20ExtendFreeDailyMsg freeDailyMsg = new Slot20ExtendFreeDailyMsg(commandCollection.FREE_DAILY_MESSAGE);
        if (model != null && model.getRotateFree() > 0) {
            user.setProperty("numFreeDaily", model.getRotateFree());
            freeDailyMsg.remain = (byte) model.getRotateFree();
        } else {
            user.removeProperty("numFreeDaily");
        }
        SlotUtils.sendMessageToUser(freeDailyMsg, user);
        if (result) {
            user.setProperty("MGROOM_" + this.gameName + "_INFO", this);
        }
        return result;
    }
}

