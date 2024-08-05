
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

import game.modules.slot.SlotModule;
import game.modules.slot.cmd.Slot25CommandCollection;

import game.modules.slot.cmd.send.slot25linebasic.*;
import game.modules.slot.entities.slot.AutoUser;
import game.modules.slot.entities.slot.AwardsOnLine;
import game.modules.slot.entities.slot.Line;
import game.modules.slot.entities.slot.MiniGameSlotResponse;
import game.modules.slot.entities.slot.line20basic.Slot20Award;
import game.modules.slot.entities.slot.line25basic.Slot25BasicAward;
import game.modules.slot.entities.slot.line25basic.Slot25BasicAwards;
import game.modules.slot.entities.slot.line25basic.SlotBasic25Item;
import game.modules.slot.entities.slot.line25basic.Slot25BasicLines;
import game.modules.slot.listener.SlotLogListener;
import game.modules.slot.utils.Slot25BasicUtil;
import game.modules.slot.utils.SlotUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Slot25BasicRoom extends SlotRoom {
    private final Runnable gameLoopTask = new GameLoopTask();
    private final Runnable checkResetPotTask = new CheckResetPot();
    private final Slot25BasicLines lines = new Slot25BasicLines();
    private long lastTimeUpdatePotToRoom = 0L;
    private long lastTimeUpdateFundToRoom = 0L;
    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    private final Slot25CommandCollection commandCollection;
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("slot");

    private SlotLogListener logListener;

    public Slot25BasicRoom(SlotModule module, Slot25CommandCollection commandCollection, SlotLogListener logListener, String gameName, byte id, String room, short moneyType, long pot, long fund, int betValue, long initJackpotValue) {

        super(id, gameName, room, betValue, moneyType, pot, fund, initJackpotValue);

        this.module = module;
        this.commandCollection = commandCollection;
        this.logListener = logListener;
        this.moneyType = moneyType;
        this.gameName = gameName;
        this.cacheFreeSpinName = this.gameName + betValue;

        this.betValue = betValue;
        this.initJackpotValues = initJackpotValue;

        setPercentFee();
        setPercentJackpot();

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
            Slot25ForceStopAutoPlayMsg msg = new Slot25ForceStopAutoPlayMsg(commandCollection.FORCE_AUTO_PLAY_MESSAGE);
            SlotUtils.sendMessageToUser(msg, user);
        }
    }

    public Slot25ResultMsg play(String username, String linesStr) {
        long referenceId = this.module.getNewReferenceId();
        Slot25ResultMsg msg = this.playNormal(username, linesStr, referenceId);

        afterPlay(msg);
        return msg;
    }


    /**
     * @param username    tên hiển thị người chơi
     * @param linesStr    số line được chọn
     * @param referenceId mã tham chiếu giao dịch
     * @return ResultBenleyMsg model kết quả
     */
    public Slot25ResultMsg playNormal(String username, String linesStr, long referenceId) {
        // kết quả mặc định
        short result = ResultSlot.MISSED;
        // thời điểm hiện tại
        String currentTimeStr = DateTimeUtils.getCurrentTime();
        // response message
        Slot25ResultMsg playResponse = new Slot25ResultMsg(commandCollection.RESULT_MESSAGE);
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
                    // trừ phế 2%
                    long fee = !isSpinningFree ? totalBetValue * percentFee / 100L : 0;
                    MoneyResponse moneyRes = new MoneyResponse(false, "1001");
                    // Không phải lả BOT => Cập nhật tiền
                    if (!u.isBot()) {
                        long changeMoney = isSpinningFree ? 0 : totalBetValue;
                        String desc = isSpinningFree ? "Lượt quay miễn phí " + gameName : "Đặt cược " + gameName;
                        moneyRes = this.userService.updateMoney(username, -changeMoney, this.moneyTypeStr, this.gameName, "Quay " + gameName, desc, fee, referenceId, TransType.START_TRANS);
                    } else {
                        moneyRes.setSuccess(true);
                    }

                    if (moneyRes != null && moneyRes.isSuccess()) {
                        // 2 phần trăm cho vào hũ JACKPOT
                        long moneyToPot = !isSpinningFree ? totalBetValue * 1 / 100L : 0;
                        this.pot += moneyToPot;

                        // số tiền còn lại sau khi trừ phế và 2% POT cho vào quỹ thưởng
                        long moneyToFund = !isSpinningFree ? totalBetValue - fee - moneyToPot : 0;
                        if (!u.isBot()) {
                            updateFunValue(moneyToFund);
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
                        ArrayList<AwardsOnLine<Slot25BasicAward>> awardsOnLines = new ArrayList<>();

                        synchronized (this) {
                            block4:
                            while (!enoughPair) {
                                // khởi tạo lại các giá trị mặc định sau mỗi lần lặp
                                result = ResultSlot.MISSED;
                                awardsOnLines.clear();
                                totalPrizes = 0L;
                                bonusGameResponse = null;
                                countScatter = 0;
                                countBonus = 0;
                                boolean isForceJackpot = false;

                                // kiểm tra force nổ hũ
                                if (betValue == 100) {
                                    if ((usernameForce.equals(username) && roomForce.equals(String.valueOf(100)))
                                            || (!u.isBot() && randomJackpot(percentJackpot))) {
                                        isForceJackpot = true;
                                        forceJackpotToUser = true;
                                        result = ResultSlot.JACKPOT;
                                    }
                                } else if (betValue == 1000) {
                                    if ((usernameForce.equals(username) && roomForce.equals(String.valueOf(1000)))
                                            || (!u.isBot() && randomJackpot(percentJackpot))) {
                                        isForceJackpot = true;
                                        forceJackpotToUser = true;
                                        result = ResultSlot.JACKPOT;
                                    }
                                } else {
                                    if ((usernameForce.equals(username) && roomForce.equals(String.valueOf(10000)))
                                            || (!u.isBot() && randomJackpot(percentJackpot))) {
                                        isForceJackpot = true;
                                        forceJackpotToUser = true;
                                        result = ResultSlot.JACKPOT;
                                    }
                                }

                                // sinh Matrix
                                SlotBasic25Item[][] matrix = isForceJackpot
                                        ? Slot25BasicUtil.generateMatrixNoHu(selectedLines)
                                        : Slot25BasicUtil.generateMatrix();
                                // Đếm số lượng BONUS và SCATTER
                                for (int i = 0; i < 3; ++i) {
                                    for (int j = 0; j < 5; ++j) {
                                        if (matrix[i][j] == SlotBasic25Item.SCATTER) {
                                            ++countScatter;
                                            continue;
                                        }
                                        if (matrix[i][j] == SlotBasic25Item.BONUS) {
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
                                    bonusGameResponse = Slot25BasicUtil.buildBonusGameData(this.betValue, countBonus);
                                    Slot25BasicAward bonusAward = Slot25BasicAwards.getAward(SlotBasic25Item.BONUS, countBonus);
                                    AwardsOnLine<Slot25BasicAward> aol = new AwardsOnLine<>(bonusAward, bonusGameResponse.getTotalPrize(), "line0");
                                    awardsOnLines.add(aol);
                                    result = ResultSlot.BONUS_GAME;
                                }

                                SlotBasic25Item[][] matrixWild = matrix;

                                // Duyệt toàn bộ Lines được chọn bởi người chơi để tính toán giải thưởng trên từng Line
                                for (String selectedLine : selectedLines) {
                                    ArrayList<Slot25BasicAward> awardList = new ArrayList<>();
                                    int lineNumber = Integer.parseInt(selectedLine);
                                    Line line = Slot25BasicUtil.getLine(this.lines, matrixWild, lineNumber);
                                    Slot25BasicUtil.calculateMoneyAwardInLine(line, awardList);
                                    for (Slot25BasicAward award : awardList) {
                                        long moneyOnLine;
                                        if (award == Slot25BasicAward.PENTA_JACKPOT) {
                                            // đảm bảo chỉ duy nhất 1 dòng trúng JACKPOT
                                            // nếu trùng lặp, bắt đầu lại dòng vòng lặp while (continue block4)
                                            for (AwardsOnLine e : awardsOnLines) {
                                                if (e.getAward() != Slot25BasicAward.PENTA_JACKPOT) {
                                                    continue;
                                                }
                                                continue block4;
                                            }
                                            moneyOnLine = this.pot;
                                            result = ResultSlot.JACKPOT;
                                        } else {
                                            moneyOnLine = (long) (award.getRatio() * this.betValue);
                                        }
                                        AwardsOnLine aol = new AwardsOnLine(award, moneyOnLine, line.getName());
                                        awardsOnLines.add(aol);
                                    }
                                }

                                // Tiếp theo, tính toán toàn bộ giải thưởng
                                boolean isGetJackpotNaturally = false;
                                StringBuilder builderLinesWin = new StringBuilder();
                                StringBuilder builderPrizesOnLine = new StringBuilder();
                                for (AwardsOnLine<Slot25BasicAward> award : awardsOnLines) {
                                    totalPrizes += award.getMoney();

                                    builderLinesWin.append(",");
                                    builderLinesWin.append(award.getLineId());

                                    builderPrizesOnLine.append(",");
                                    builderPrizesOnLine.append(award.getMoney());

                                    if (!isForceJackpot && award.getAward() == Slot25BasicAward.PENTA_JACKPOT) {
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
                                    if (isGetJackpotNaturally && getFunValue() < initJackpotValues) {
                                        continue;
                                    }
                                    // Tuy không trúng JACKPOT nhưng trúng Line to quá cũng cần sinh lại MATRIX
                                    if (!isGetJackpotNaturally) {
//                                    if ((totalPrizes - totalBetValue > 0 && totalPrizes > getFunValue()) || totalPrizes >= totalBetValue * 25)
                                        if ((totalPrizes - totalBetValue > 0 && totalPrizes > getFunValue()))
                                            continue;
                                    }
                                }
                                // điều kiện trúng thưởng đã thỏa mãn, dừng vòng lặp
                                enoughPair = true;
                                // BẮT ĐẦU QUÁ TRÌNH LƯU TRỮ THÔNG TIN VÀ TRẢ THƯỞNG
                                String matrixStr = Slot25BasicUtil.matrixToString(matrix);
                                if (totalPrizes > 0L) {
                                    if (result == ResultSlot.JACKPOT) {
                                        this.pot = this.initJackpotValues;
                                        if (!u.isBot()) updateFunValue(-initJackpotValues);

                                        // get user cache
                                        String displayName = username;
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
                                            updateFunValue(-totalPrizes);
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
                                        Slot25BigWinMsg bigWinMsg = new Slot25BigWinMsg(commandCollection.BIG_WIN_MESSAGE);
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
        return playResponse;
    }

    protected int setFreeSpin(String nickName, String lines, int countFreeSpin, int remainAmountOfFreeSpin) {
        int soLuot = 0;
        switch (countFreeSpin) {
            case 3: {
                soLuot = 4 + remainAmountOfFreeSpin;
                slotService.setLuotQuayFreeSlot(this.cacheFreeSpinName, nickName, lines, soLuot, 1, betValue);
                break;
            }
            case 4: {
                soLuot = 8 + remainAmountOfFreeSpin;
                slotService.setLuotQuayFreeSlot(this.cacheFreeSpinName, nickName, lines, soLuot, 2, betValue);
                break;
            }
            case 5: {
                soLuot = 22 + remainAmountOfFreeSpin;
                slotService.setLuotQuayFreeSlot(this.cacheFreeSpinName, nickName, lines, soLuot, 3, betValue);
            }
        }
        return Math.max(soLuot, remainAmountOfFreeSpin);
    }

    public short play(User user, String linesStr) throws Exception {
        String username = user.getName();
        Slot25ResultMsg msg;
        Slot25FreeDailyMsg freeDailyMsg = new Slot25FreeDailyMsg(commandCollection.FREE_DAILY_MESSAGE);
        freeDailyMsg.remain = 0;
        msg = this.play(username, linesStr);
        if (this.isUserMinimize(user)) {
            Slot25MinimizeResultMsg miniMsg = new Slot25MinimizeResultMsg(commandCollection.MINIMIZE_RESULT_MESSAGE);
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
        if (currentTime - this.lastTimeUpdateFundToRoom >= 3000L) {
            try {
                this.miniGameService.saveFund(this.name, getFunValue());
            } catch (IOException | InterruptedException | TimeoutException e) {
                Debug.trace(this.gameName + ": update fund error ", e.getMessage());
            }
            this.lastTimeUpdateFundToRoom = currentTime;
        }
    }

    private void savePot() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - this.lastTimeUpdatePotToRoom >= 3000L) {
            this.lastTimeUpdatePotToRoom = currentTime;
            try {
                this.miniGameService.savePot(this.name, CACHE_JACK_POT_VALUE_SLOT + "_" + this.betValue + "_" + gameName, this.pot, this.huX2);
            } catch (IOException | InterruptedException | TimeoutException e) {
                Debug.trace(this.gameName + ": update pot error ", e.getMessage());
            }
        }

        Slot25UpdatePotMsg msg = new Slot25UpdatePotMsg(commandCollection.UPDATE_POT_MESSAGE);
        msg.value = this.pot;
        msg.x2 = (byte) (this.huX2 ? 1 : 0);
        this.sendMessageToRoom(msg);
    }

    public void updatePot(User user) {
        Slot25UpdatePotMsg msg = new Slot25UpdatePotMsg(commandCollection.UPDATE_POT_MESSAGE);
        msg.value = this.pot;
        msg.x2 = (byte) (this.huX2 ? 1 : 0);
        SlotUtils.sendMessageToUser(msg, user);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void gameLoop() {

        setPercentFee();
        setPercentJackpot();

        ArrayList<AutoUser> usersPlay = new ArrayList<>();
        Map map = this.usersAuto;
        synchronized (map) {
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
//                updateFunValue(-getFunValue());
                this.savePot();
//                this.saveFund();
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
    public void playListAuto(List<AutoUser> users) {
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
                if (result == 0) {
                    user.setMaxCount(5);
                    continue;
                }
                if (result == 5) {
                    user.setMaxCount(20);
                    continue;
                }
                user.setMaxCount(8);
            } catch (Exception ex) {
                Logger.getLogger(Slot25BasicRoom.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        users.clear();
    }

    @Override
    public boolean joinRoom(User user) {
        boolean result = super.joinRoom(user);
        SlotFreeDaily model = this.slotService.getLuotQuayFreeDaily(this.gameName, user.getName(), this.betValue);
        Slot25FreeDailyMsg freeDailyMsg = new Slot25FreeDailyMsg(commandCollection.FREE_DAILY_MESSAGE);
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

    protected void afterPlay(Slot25ResultMsg msg) {
        // do nothing by default
    }
}

