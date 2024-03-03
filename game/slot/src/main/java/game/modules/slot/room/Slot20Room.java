
package game.modules.slot.room;

import bitzero.server.BitZeroServer;
import bitzero.server.entities.User;
import bitzero.util.common.business.Debug;

import com.google.gson.Gson;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import com.vinplay.dal.service.impl.BroadcastMessageServiceImpl;
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

import game.modules.slot.Slot20Module;
import game.modules.slot.SlotModule;
import game.modules.slot.cmd.Slot20CommandCollection;
import game.modules.slot.cmd.send.slot20line.*;
import game.modules.slot.entities.slot.*;
import game.modules.slot.entities.slot.line20basic.Slot20Line;
import game.modules.slot.entities.slot.line20basic.Slot20Award;
import game.modules.slot.entities.slot.line20basic.Slot20Item;
import game.modules.slot.entities.slot.line20basic.Slot20Lines;
import game.modules.slot.entities.slot.line25basic.Slot25BasicAward;
import game.modules.slot.listener.SlotLogListener;
import game.modules.slot.utils.Slot20Utils;
import game.modules.slot.utils.SlotUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Slot20Room extends SlotRoom {
    private final Runnable gameLoopTask = new GameLoopTask();
    private final Runnable checkResetPotTask = new CheckResetPot();
    private final Slot20Lines lines = new Slot20Lines();
    private long lastTimeUpdatePotToRoom = 0L;
    private long lastTimeUpdateFundToRoom = 0L;
    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    private final List<Integer> boxValues = new ArrayList<>();
    private final String gn;

    private final SlotLogListener slotLogListener;
    private final Slot20CommandCollection commandCollection;

    // FORCE - R
    int resultState = 0;
    int MAX_STATE = 5;

    public Slot20Room(SlotModule module, Slot20CommandCollection commandCollection, SlotLogListener slotLogListener,
                      String gameName, byte id, String room, short moneyType, long pot, long fund, int betValue, long initPotValue) {
        // FORCE - R
//        super(id, room, betValue, moneyType, pot, fund - 1000000000, initPotValue);
        super(id, room, betValue, moneyType, pot, fund, initPotValue);
        this.commandCollection = commandCollection;
        this.slotLogListener = slotLogListener;
        this.gameName = gameName;
        this.module = module;
        this.moneyTypeStr = this.moneyType == 1 ? "vin" : "xu";
        gn = gameName;
        this.cacheFreeSpinName = this.gameName + betValue;
        CacheServiceImpl cacheService = new CacheServiceImpl();
        cacheService.setValue(room, (int) pot);
        this.betValue = betValue;
        this.initJackpotValues = initPotValue;

        try {
            this.cachePercentFeeName = gameName + "PERCENT_FEE";
            this.percentFee = cacheService.getValueInt(cachePercentFeeName);
        } catch (Exception ex) {
            this.percentFee = 2;
            cacheService.setValue(cachePercentFeeName, percentFee);
        }

        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.gameLoopTask, 10, 1, TimeUnit.SECONDS);
        this.boxValues.add(10);
        this.boxValues.add(10);
        this.boxValues.add(10);
        this.boxValues.add(15);
        this.boxValues.add(20);
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.checkResetPotTask, 10, 10, TimeUnit.SECONDS);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void forceStopAutoPlay(User user) {
        super.forceStopAutoPlay(user);
        synchronized (usersAuto) {
            this.usersAuto.remove(user.getName());
            Slot20ForceStopAutoPlayMsg msg = new Slot20ForceStopAutoPlayMsg(commandCollection.FORCE_AUTO_PLAY_MESSAGE);
            SlotUtils.sendMessageToUser(msg, user);
        }
    }

    public synchronized SLot20ResultMsg play(String username, String linesStr) {

        // FORCE - R
//        int forceResult = ResultSlot.MISSED;
//        switch (resultState) {
//            case 0:
//                forceResult = ResultSlot.WIN;
//                break;
//            case 1:
//                forceResult = ResultSlot.BIG_WIN;
//                break;
//            case 2:
//                forceResult = ResultSlot.FREE_SPIN;
//                break;
//            case 3:
//                forceResult = ResultSlot.JACKPOT;
//                break;
//            case 4:
//                forceResult = ResultSlot.BONUS_GAME;
//        }

        // FORCE - R
//        resultState++;
//        if (resultState >= MAX_STATE) {
//            resultState = 0;
//        }

//        long startTime = System.currentTimeMillis();
        // kết quả mặc định
        short result = ResultSlot.MISSED;
        // thời điểm hiện tại
        String currentTimeStr = DateTimeUtils.getCurrentTime();
        // response message
        SLot20ResultMsg playResponse = new SLot20ResultMsg(commandCollection.RESULT_MESSAGE);
        // mã tham chiếu giao dịch
        long referenceId = this.module.getNewReferenceId();
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
            usernameForce = cacheService.getValueStr(CACHE_NAME_USER_SPOT + this.gn);
            roomForce = cacheService.getValueStr(CACHE_BET_VALUE_SLOT + gameName);
        } catch (Exception e) {
            usernameForce = "";
            roomForce = "";
        }
        // thông tin user
        UserCacheModel u = this.userService.getUser(username);
        // số dư hiện tại của user
        long currentMoney = this.userService.getMoneyUserCache(username, this.moneyTypeStr);
        // thông tin free spin
        int numOfFreeSpin = getNumOfFreeSpin(username);
        boolean isSpinningFree = numOfFreeSpin > 0;
        // số lines được chọn > 0
        if (selectedLines.length > 0 && !linesStr.isEmpty()) {
            if (totalBetValue > 0L) {
                if (totalBetValue <= currentMoney || isSpinningFree) {
                    long fee = !isSpinningFree ? totalBetValue * percentFee / 100L : 0;
                    MoneyResponse moneyRes = new MoneyResponse(false, "1001");
                    if (!u.isBot()) {
                        long changeMoney = isSpinningFree ? 0 : totalBetValue;
                        String desc = isSpinningFree ? "Lượt quay miễn phí " + gameName : "Đặt cược " + gameName;
                        moneyRes = this.userService.updateMoney(username, -changeMoney, this.moneyTypeStr, this.gameName, "Quay " + gn, desc, fee, referenceId, TransType.START_TRANS);
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
                        ArrayList<AwardsOnLine> awardsOnLines = new ArrayList<>();

                        block4:
                        while (!enoughPair) {
                            result = 0;
                            awardsOnLines.clear();
                            totalPrizes = 0L;
                            String linesWin;
                            String prizesOnLine;
                            String haiSao = "";
                            boolean isForceJackpot = false;
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

                            // FORCE - R
//                            Slot20Item[][] matrix = forceResult == ResultSlot.JACKPOT ||  isForceJackpot == ResultSlot.JACKPOT
//                                    ? Slot20Utils.generateJackpotMatrix(selectedLines)
//                                    : Slot20Utils.generateMatrix();
                            // sinh Matrix
                            Slot20Item[][] matrix = isForceJackpot
                                    ? Slot20Utils.generateJackpotMatrix(selectedLines)
                                    : Slot20Utils.generateMatrix();
                            // Duyệt toàn bộ Lines được chọn bởi người chơi để tính toán giải thưởng trên từng Line
                            boolean hasFreeSpinAward = false;
                            boolean hasBonusAward = false;
                            int countFreeSpin = 0;
                            for (String selectedLine : selectedLines) {
                                ArrayList<Slot20Award> awardList = new ArrayList<>();
                                Slot20Line line = Slot20Utils.getLine(this.lines, matrix, Integer.parseInt(selectedLine));
                                Slot20Utils.calculateAwardInLine(line, awardList);
                                for (Slot20Award award : awardList) {
                                    long moneyOnLine = 0;
                                    // Phần thưởng bình thường
                                    // phần thưởng bình thường là phần thưởng được tính bằng mức cược * hệ số
                                    // betValue * award.getRatio
                                    if (award.getRatio() > 0.0f) {
                                        moneyOnLine = (long) (award.getRatio() * this.betValue);
                                    }
                                    // phần thưởng cho JACKPOT
                                    else if (award == Slot20Award.PENTA_JACKPOT) {
                                        // đảm bảo chỉ duy nhất 1 dòng trúng JACKPOT
                                        // nếu trùng lặp, bắt đầu lại dòng vòng lặp while (continue block4)
                                        for (AwardsOnLine e : awardsOnLines) {
                                            if (e.getAward() != Slot20Award.PENTA_JACKPOT) {
                                                continue;
                                            }
                                            continue block4;
                                        }
                                        moneyOnLine = this.pot;
                                        result = ResultSlot.JACKPOT;
                                    }
                                    // phần thưởng FREE SPIN
                                    else if (award == Slot20Award.TRIPLE_FREE_SPIN
                                            || award == Slot20Award.QUADRA_FREE_SPIN
                                            || award == Slot20Award.PENTA_FREE_SPIN) {
                                        hasFreeSpinAward = true;
                                        countFreeSpin += (int) (award.getRatio() * -1);
                                        // do something relate to FREE SPINS
                                    }
                                    // phần thưởng còn lại là BONUS
                                    else {
                                        hasBonusAward = true;
                                        MiniGameSlotResponse response = this.generateBonusData();
                                        moneyOnLine = response.getTotalPrize();
                                        haiSao = response.getPrizes();
                                        if (result != 3) {
                                            result = 5;
                                        }
                                    }
                                    AwardsOnLine aol = new AwardsOnLine(award, moneyOnLine, line.getName());
                                    awardsOnLines.add(aol);
                                }
                            }
                            // không cho phép nổ hũ và (BONUS hoặc FREE SPIN) xảy ra đồng thời
                            if (isForceJackpot && (hasBonusAward || hasFreeSpinAward)) {
                                continue;
                            }
                            // Không cho phép đồng thời cả BONUS và FREE SPIN
                            if (hasFreeSpinAward && hasBonusAward) {
                                continue;
                            }

                            // FORCE: uncomment đoạn dưới
                            // ở chế độ Free Spin, không cho phép trúng BONUS hoặc SCATTER
                            if (isSpinningFree && (hasFreeSpinAward || hasBonusAward)) {
                                continue;
                            }

                            // FORCE - R
//                            switch (forceResult) {
//                                case ResultSlot.JACKPOT:
//                                    if (result != ResultSlot.JACKPOT) continue;
//                                    break;
//                                case ResultSlot.BONUS_GAME:
//                                    if (!hasBonusAward) continue;
//                                    break;
//                                case ResultSlot.FREE_SPIN:
//                                    if (!hasFreeSpinAward) continue;
//                                    break;
//                            }

                            // Tiếp theo, tính toán toàn bộ giải thưởng
                            boolean isGetJackpotNaturally = false;
                            StringBuilder builderLinesWin = new StringBuilder();
                            StringBuilder builderPrizesOnLine = new StringBuilder();
                            for (AwardsOnLine award : awardsOnLines) {
//                                if ((entry2.getAward() == Slot20Award.PENTA_JACKPOT
//                                        || entry2.getAward() == Slot20Award.QUADRA_JACKPOT
//                                        || entry2.getAward() == Slot20Award.TRIPLE_JACKPOT) && !isForceJackpot)
//                                    continue block4;

//                                if (betValue == 100) {
//                                    if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_100") == 0) // cho cả người và bot nổ hũ
//                                    {
//                                        if ((entry2.getAward() == Slot20Award.PENTA_JACKPOT
//                                                || entry2.getAward() == Slot20Award.QUADRA_JACKPOT
//                                                || entry2.getAward() == Slot20Award.TRIPLE_JACKPOT))
//                                            continue block4;
//                                    } else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_100") == 1) // chỉ cho bot nổ hũ
//                                    {
//                                        if ((entry2.getAward() == Slot20Award.PENTA_JACKPOT
//                                                || entry2.getAward() == Slot20Award.QUADRA_JACKPOT
//                                                || entry2.getAward() == Slot20Award.TRIPLE_JACKPOT) && !u.isBot())
//                                            continue block4;
//                                    } else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_100") == -1) // chỉ cho người nổ hũ
//                                    {
//                                        if ((entry2.getAward() == Slot20Award.PENTA_JACKPOT
//                                                || entry2.getAward() == Slot20Award.QUADRA_JACKPOT
//                                                || entry2.getAward() == Slot20Award.TRIPLE_JACKPOT) && u.isBot())
//                                            continue block4;
//                                    } else {
//                                        if ((entry2.getAward() == Slot20Award.PENTA_JACKPOT
//                                                || entry2.getAward() == Slot20Award.QUADRA_JACKPOT
//                                                || entry2.getAward() == Slot20Award.TRIPLE_JACKPOT) && !u.isBot())
//                                            continue block4;
//                                    }
//                                } else if (betValue == 1000) {
//                                    if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_1000") == 0) // cho cả người và bot nổ hũ
//                                    {
//                                        if ((entry2.getAward() == Slot20Award.PENTA_JACKPOT
//                                                || entry2.getAward() == Slot20Award.QUADRA_JACKPOT
//                                                || entry2.getAward() == Slot20Award.TRIPLE_JACKPOT))
//                                            continue block4;
//                                    } else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_1000") == 1) // chỉ cho bot nổ hũ
//                                    {
//                                        if ((entry2.getAward() == Slot20Award.PENTA_JACKPOT
//                                                || entry2.getAward() == Slot20Award.QUADRA_JACKPOT
//                                                || entry2.getAward() == Slot20Award.TRIPLE_JACKPOT) && !u.isBot())
//                                            continue block4;
//                                    } else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_1000") == -1) // chỉ cho người nổ hũ
//                                    {
//                                        if ((entry2.getAward() == Slot20Award.PENTA_JACKPOT
//                                                || entry2.getAward() == Slot20Award.QUADRA_JACKPOT
//                                                || entry2.getAward() == Slot20Award.TRIPLE_JACKPOT) && u.isBot())
//                                            continue block4;
//                                    } else {
//                                        if ((entry2.getAward() == Slot20Award.PENTA_JACKPOT
//                                                || entry2.getAward() == Slot20Award.QUADRA_JACKPOT
//                                                || entry2.getAward() == Slot20Award.TRIPLE_JACKPOT) && !u.isBot())
//                                            continue block4;
//                                    }
//                                } else {
//                                    if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_10000") == 0) // cho cả người và bot nổ hũ
//                                    {
//                                        if ((entry2.getAward() == Slot20Award.PENTA_JACKPOT
//                                                || entry2.getAward() == Slot20Award.QUADRA_JACKPOT
//                                                || entry2.getAward() == Slot20Award.TRIPLE_JACKPOT))
//                                            continue block4;
//                                    } else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_10000") == 1) // chỉ cho bot nổ hũ
//                                    {
//                                        if ((entry2.getAward() == Slot20Award.PENTA_JACKPOT
//                                                || entry2.getAward() == Slot20Award.QUADRA_JACKPOT
//                                                || entry2.getAward() == Slot20Award.TRIPLE_JACKPOT) && !u.isBot())
//                                            continue block4;
//                                    } else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_10000") == -1) // chỉ cho người nổ hũ
//                                    {
//                                        if ((entry2.getAward() == Slot20Award.PENTA_JACKPOT
//                                                || entry2.getAward() == Slot20Award.QUADRA_JACKPOT
//                                                || entry2.getAward() == Slot20Award.TRIPLE_JACKPOT) && u.isBot())
//                                            continue block4;
//                                    } else {
//                                        if ((entry2.getAward() == Slot20Award.PENTA_JACKPOT
//                                                || entry2.getAward() == Slot20Award.QUADRA_JACKPOT
//                                                || entry2.getAward() == Slot20Award.TRIPLE_JACKPOT) && !u.isBot())
//                                            continue block4;
//                                    }
//                                }
                                totalPrizes += award.getMoney();
                                builderLinesWin.append(",");
                                builderLinesWin.append(award.getLineId());
                                builderPrizesOnLine.append(",");
                                builderPrizesOnLine.append(award.getMoney());
                                if (result != ResultSlot.JACKPOT && award.getAward() == Slot25BasicAward.PENTA_JACKPOT) {
                                    result = ResultSlot.JACKPOT;
                                    isGetJackpotNaturally = true;
                                }
                            }

                            // FORCE - R
//                            if (forceResult == ResultSlot.BIG_WIN) {
//                                if (hasBonusAward || hasFreeSpinAward) {
//                                    continue;
//                                }
//                                if (result == ResultSlot.MISSED) {
//                                    result = totalPrizes >= (this.betValue * 175L) ? ResultSlot.BIG_WIN : ResultSlot.WIN;
//                                    if (result != ResultSlot.BIG_WIN) {
//                                        continue;
//                                    }
//                                }
//                            }

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
                                    // FORCE - R - Bỏ đoạn này và sử dụng lại đoạn mã trên
//                                    if ((totalPrizes - totalBetValue > 0 && totalPrizes > fund))
//                                        continue;
                                }
                            }
                            // điều kiện trúng thưởng đã thỏa mãn, dừng vòng lặp
                            enoughPair = true;
//                            // cập nhật lượt quay miễn phí
//                            if (isSpinningFree) {
//                                slotService.updateLuotQuaySlotFree(cacheFreeSpinName, username);
//                            }

                            // BẮT ĐẦU QUÁ TRÌNH LƯU TRỮ THÔNG TIN VÀ TRẢ THƯỞNG
                            String matrixStr = Slot20Utils.matrixToString(matrix);
                            if (totalPrizes > 0L) {
                                if (result == ResultSlot.JACKPOT) {
                                    this.pot = this.initJackpotValues;
                                    this.fund -= initJackpotValues;

                                    // get usercache
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
                                            cacheService.removeKey(CACHE_NAME_USER_SPOT + this.gn);
                                            cacheService.removeKey(CACHE_BET_VALUE_SLOT + gameName);
                                        } catch (Exception ignored) {
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
                            playResponse.freeSpin = (byte) this.setFreeSpin(username, linesStr, countFreeSpin, slotFreeSpin.getNum());
                            if (countFreeSpin >= 3) {
                                playResponse.isFreeSpin = true;
                                if (result != ResultSlot.BONUS_GAME && result != ResultSlot.JACKPOT) {
                                    result = ResultSlot.FREE_SPIN;
                                }
                            }

                            // only save real user
                            long moneyExchange = totalPrizes;
                            if (totalPrizes != 0 && !u.isBot()) {
                                if ((moneyRes = this.userService.updateMoney(username, totalPrizes, this.moneyTypeStr, this.gameName, "Quay " + gn, this.buildDescription(totalBetValue, totalPrizes, result), 0L, referenceId, TransType.END_TRANS)) != null && moneyRes.isSuccess()) {
                                    currentMoney = moneyRes.getCurrentMoney();
                                    // thông báo tới toàn hệ thống số tiền thắng của người chơi
                                    if (this.moneyType == 1 && moneyExchange >= (long) BroadcastMessageServiceImpl.MIN_MONEY) {
                                        this.broadcastMsgService.putMessage(Games.findGameByName(gameName).getId(), username, moneyExchange - totalBetValue);
                                    }
                                }
                            }
                            linesWin = builderLinesWin.toString();
                            prizesOnLine = builderPrizesOnLine.toString();
                            playResponse.referenceId = referenceId;
                            playResponse.matrix = Slot20Utils.matrixToString(matrix);
                            playResponse.linesWin = linesWin;
                            playResponse.prize = totalPrizes;
                            playResponse.haiSao = haiSao;
                            try {
                                // lưu nhật ký chơi
                                if (!u.isBot()) {
                                    slotLogListener.log(referenceId, username, this.betValue, linesStr, linesWin, prizesOnLine, result, totalPrizes, currentTimeStr, matrixStr);
                                }
                                // lưu nhật ký nổ hũ
                                if (result == ResultSlot.JACKPOT) {
                                    this.slotService.addTop(gn, username, this.betValue, totalPrizes, currentTimeStr, result);
                                }
                                // thông báo tới toàn bộ người chơi trong game (trong MODULE)
                                if (result == ResultSlot.JACKPOT || result == ResultSlot.BIG_WIN) {
                                    Slot20BigWinMsg bigWinMsg = new Slot20BigWinMsg(commandCollection.BIG_WIN_MESSAGE);
                                    bigWinMsg.username = username;
                                    bigWinMsg.type = (byte) result;
                                    bigWinMsg.betValue = (short) this.betValue;
                                    bigWinMsg.totalPrizes = totalPrizes;
                                    bigWinMsg.timestamp = DateTimeUtils.getCurrentTime();
                                    this.module.sendMsgToAllUsers(bigWinMsg);
                                }
                            } catch (InterruptedException | TimeoutException | IOException ignored) {
                            }
                            // lưu thông tin quỹ
                            this.saveFund();
                            // lưu thông tin HŨ
                            this.savePot();

                            System.out.println("Total Prize: " + totalPrizes + " - Fun: " + fund);
                        }
                    } else {
                        result = ResultSlot.NOT_ENOUGH_MONEY;
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
//        long endTime = System.currentTimeMillis();
//        long handleTime = endTime - startTime;
//        String ratioTime = CommonUtils.getRatioTime(handleTime);
//        SlotUtils.logKhoBau(referenceId, username, this.betValue, msg.matrix, msg.haiSao, result, handleTime, ratioTime, currentTimeStr);
        if (!u.isBot()) {
            System.out.println(new Gson().toJson(playResponse));
        }

        // FORCE - R
//        if (fund < 1000000000) {
//            fund = Long.MAX_VALUE - 1000000000L;
//        }

//        System.out.println(new Gson().toJson(playResponse));
        return playResponse;
    }

    private int setFreeSpin(String nickName, String lines, int countFreeSpin, int remainAmountOfFreeSpin) {
        if (countFreeSpin > 0) {
            countFreeSpin = countFreeSpin + remainAmountOfFreeSpin;
            slotService.setLuotQuayFreeSlot(this.cacheFreeSpinName, nickName, lines, countFreeSpin, 1, betValue);
        }
        return countFreeSpin > 0 ? countFreeSpin + remainAmountOfFreeSpin : remainAmountOfFreeSpin;
    }

    public boolean isBot(String nickName) {
        try {
            UserCacheModel u = this.userService.getUser(nickName);
            return u.isBot();
        } catch (Exception e) {
            return true;
        }
    }

    private MiniGameSlotResponse generateBonusData() {
        MiniGameSlotResponse response = new MiniGameSlotResponse();
        int totalMoney = 0;
        ArrayList<PickStarGift> gifts = new ArrayList<>();
        PickStarGifts pickStarGifts = new PickStarGifts();
        StringBuilder responsePickStars = new StringBuilder();
        int totalKeys = 1;
        for (int numPicks = 10; numPicks > 0; --numPicks) {
            PickStarGiftItem gift = pickStarGifts.pickRandomAndRandomGift();
            switch (gift) {
                case GOLD: {
                    totalMoney += 4 * this.betValue * totalKeys;
                    gifts.add(new PickStarGift(PickStarGiftItem.GOLD, 0));
                    continue;
                }
                case KEY: {
                    ++numPicks;
                    ++totalKeys;
                    gifts.add(new PickStarGift(PickStarGiftItem.KEY, 0));
                    continue;
                }
                case BOX: {
                    int boxValue = this.randomBoxValue();
                    totalMoney += boxValue * this.betValue * totalKeys;
                    gifts.add(new PickStarGift(PickStarGiftItem.BOX, boxValue));
                    break;
                }
            }
        }
        for (PickStarGift pickStarGift : gifts) {
            if (responsePickStars.length() == 0) {
                responsePickStars = new StringBuilder(pickStarGift.getName());
                continue;
            }
            responsePickStars.append(",").append(pickStarGift.getName());
        }
        response.setTotalPrize(totalMoney);
        response.setPrizes(responsePickStars.toString());
        return response;
    }

    private int randomBoxValue() {
        Random rd = new Random();
        int n = rd.nextInt(this.boxValues.size());
        return this.boxValues.get(n);
    }

    public synchronized short play(User user, String linesStr) {
//        String username = user.getName();
//        int numFree = 0;
//        if (user.getProperty("numFreeDaily") != null) {
//            numFree = (Integer) user.getProperty("numFreeDaily");
//        }
//        SLot20ResultMsg msg;
//        Slot20FreeDailyMsg freeDailyMsg = new Slot20FreeDailyMsg(commandCollection.FREE_DAILY_MESSAGE);
//        if (numFree > 0) {
//            msg = this.playFreeDaily(username);
//            freeDailyMsg.remain = (byte) (--numFree);
//            if (numFree > 0) {
//                user.setProperty("numFreeDaily", numFree);
//            } else {
//                user.removeProperty("numFreeDaily");
//            }
//        } else {
//            msg = this.play(username, linesStr);
//        }
        String username = user.getName();
        SLot20ResultMsg msg = this.play(username, linesStr);
        if (this.isUserMinimize(user)) {
            Slot20MinimizeResultMsg miniMsg = new Slot20MinimizeResultMsg(commandCollection.MINIMIZE_RESULT_MESSAGE);
            miniMsg.prize = msg.prize;
            miniMsg.currentMoney = msg.currentMoney;
            miniMsg.result = msg.result;
            SlotUtils.sendMessageToUser(miniMsg, user);
        } else {
            SlotUtils.sendMessageToUser(msg, user);
//            SlotUtils.sendMessageToUser(freeDailyMsg, user);
        }
        return msg.result;
    }

    private void saveFund() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - this.lastTimeUpdateFundToRoom >= 60000L) {
            try {
                this.miniGameService.saveFund(this.name, this.fund);
            } catch (IOException | InterruptedException | TimeoutException ex2) {
                Debug.trace(gameName + ": update fund " + gameName + " bau error ", ex2.getMessage());
            }
            this.lastTimeUpdateFundToRoom = currentTime;
        }
    }

    protected void checkResetPot() {
        try {
            int isReset = cacheService.getValueInt("reset_pot_" + this.gn + "_" + this.betValue);
            if (isReset == 1) {
                this.pot = this.initJackpotValues;
                this.fund = 0;
                this.savePot();
                this.saveFund();
                this.cacheService.removeKey("reset_pot_" + this.gn + "_" + this.betValue);
            }
        } catch (Exception ignored) {
        }
    }

    private void resetPotFund() {
        try {
            this.fund = this.initJackpotValues;
            this.pot = this.initJackpotValues;
            this.saveFund();
            this.saveFund();
        } catch (Exception e) {
            Debug.trace(e);
        }
    }

    private void savePot() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - this.lastTimeUpdatePotToRoom >= 1000L) {
            this.lastTimeUpdatePotToRoom = currentTime;
            try {
                this.miniGameService.savePot(this.name, this.pot, this.huX2);
            } catch (IOException | InterruptedException | TimeoutException ex2) {
                Debug.trace(this.gameName + ": update pot poker error ", ex2.getMessage());
            }
        }

        byte x2 = (byte) (this.huX2 ? 1 : 0);
        ((Slot20Module) this.module).updatePot(this.id, this.pot, x2);
    }

    private boolean checkDieuKienNo(String username) {
        try {
            UserModel u = this.userService.getUserByUserName(username);
            return u.isBot();
        } catch (Exception u) {
            return false;
        }
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
    protected void playListAuto(List<AutoUser> users) {
        for (AutoUser user : users) {
            short result = this.play(user.getUser(), user.getLines());
            if (result == 3 || result == 4 || result == 101 || result == 102 || result == 100) {
                this.forceStopAutoPlay(user.getUser());
                continue;
            }
            if (result == 0) {
                user.setMaxCount(4);
                continue;
            }
            if (result == 5) {
                user.setMaxCount(15);
                continue;
            }
            user.setMaxCount(8);
        }
        users.clear();
    }

    @Override
    public boolean joinRoom(User user) {
        boolean result = super.joinRoom(user);
        SlotFreeDaily model = this.slotService.getLuotQuayFreeDaily(this.gameName, user.getName(), this.betValue);
        if (model != null && model.getRotateFree() > 0) {
            user.setProperty("numFreeDaily", model.getRotateFree());
            Slot20FreeDailyMsg freeDailyMsg = new Slot20FreeDailyMsg(commandCollection.FREE_DAILY_MESSAGE);
            freeDailyMsg.remain = (byte) model.getRotateFree();
            SlotUtils.sendMessageToUser(freeDailyMsg, user);
        } else {
            user.removeProperty("numFreeDaily");
        }
        if (result) {
            user.setProperty("MGROOM_" + this.gameName + "_INFO", this);
        }
        return result;
    }

}

