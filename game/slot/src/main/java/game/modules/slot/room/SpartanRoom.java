/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  bitzero.server.BitZeroServer
 *  bitzero.server.entities.User
 *  bitzero.server.extensions.data.BaseMsg
 *  bitzero.server.util.TaskScheduler
 *  bitzero.util.common.business.Debug
 *  com.vinplay.dal.service.BroadcastMessageService
 *  com.vinplay.dal.service.MiniGameService
 *  com.vinplay.dal.service.SlotMachineService
 *  com.vinplay.dal.service.impl.BroadcastMessageServiceImpl
 *  com.vinplay.dal.service.impl.CacheServiceImpl
 *  com.vinplay.usercore.service.UserService
 *  com.vinplay.vbee.common.enums.Games
 *  com.vinplay.vbee.common.models.cache.SlotFreeDaily
 *  com.vinplay.vbee.common.models.cache.UserCacheModel
 *  com.vinplay.vbee.common.models.slot.SlotFreeSpin
 *  com.vinplay.vbee.common.response.MoneyResponse
 *  com.vinplay.vbee.common.statics.TransType
 *  com.vinplay.vbee.common.utils.CommonUtils
 *  com.vinplay.vbee.common.utils.DateTimeUtils
 */
package game.modules.slot.room;

import bitzero.server.BitZeroServer;
import bitzero.server.entities.User;
import bitzero.server.extensions.data.BaseMsg;
import bitzero.util.common.business.Debug;
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
import com.vinplay.vbee.common.utils.CommonUtils;
import com.vinplay.vbee.common.utils.DateTimeUtils;
import game.modules.slot.SpartanModule;
import game.modules.slot.cmd.send.spartan.*;
import game.modules.slot.entities.slot.AutoUser;
import game.modules.slot.entities.slot.AwardsOnLine;
import game.modules.slot.entities.slot.Line;
import game.modules.slot.entities.slot.MiniGameSlotResponse;
import game.modules.slot.entities.slot.spartan.*;
import game.modules.slot.utils.SlotUtils;
import game.modules.slot.utils.SpartanUtils;
import game.util.ConfigGame;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SpartanRoom
        extends SlotRoom {
    private final Runnable gameLoopTask = new GameLoopTask();
    private final Runnable checkResetPotTask = new CheckResetPot();
    private SpartanLines lines = new SpartanLines();
    private long lastTimeUpdatePotToRoom = 0L;
    private long lastTimeUpdateFundToRoom = 0L;
    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    private String gn;
    private int countNoHu = 0;
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger((String) "slot");

    public SpartanRoom(SpartanModule module, byte id, String name, short moneyType, long pot, long fund, int betValue, long initPotValue) {
        super(id, name, betValue, moneyType, pot, fund, initPotValue);
        this.module = module;
        this.moneyType = moneyType;
        this.gameName = Games.LADY_NIGHT.getName();
        this.cacheFreeSpinName = String.valueOf(this.gameName) + betValue;
        CacheServiceImpl cacheService = new CacheServiceImpl();
        cacheService.setValue(name, (int) pot);
        this.betValue = betValue;
        this.initJackpotValues = initPotValue;
        gn = this.gameName;
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.gameLoopTask, 10, 1, TimeUnit.SECONDS);
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.checkResetPotTask, 10, 10, TimeUnit.SECONDS);

    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void forceStopAutoPlay(User user) {
        super.forceStopAutoPlay(user);
        Map map = this.usersAuto;
        synchronized (map) {
            this.usersAuto.remove(user.getName());
            ForceStopAutoPlaySpartanMsg msg = new ForceStopAutoPlaySpartanMsg();
            SlotUtils.sendMessageToUser((BaseMsg) msg, user);
        }
    }

    public ResultSpartanMsg play(String username, String linesStr) {
        long referenceId = this.module.getNewReferenceId();
        return this.playFull(username, linesStr, referenceId);
    }


    public ResultSpartanMsg playFull(String username, String linesStr, long referenceId) {
        String currentTimeStr = DateTimeUtils.getCurrentTime();
        short result = 0;
        String[] lineArr = linesStr.split(",");
        long currentMoney = this.userService.getMoneyUserCache(username, this.moneyTypeStr);
        UserCacheModel u = this.userService.getUser(username);
        long totalBetValue = (long) lineArr.length * this.betValue;
        ResultSpartanMsg msg = new ResultSpartanMsg();

        boolean forceJackpotByUser = false;
        //get force user jackpot
        CacheServiceImpl cacheService = new CacheServiceImpl();
        String userForce = "";
        String betValueCache = "";
        try {
            userForce = cacheService.getValueStr(CACHE_NAME_USER_SPOT + this.gn);
            betValueCache = cacheService.getValueStr(CACHE_BET_VALUE_SLOT + this.gn);
        } catch (Exception e) {
            userForce = "";
            betValueCache = "";
        }
        if (lineArr.length > 0 && !linesStr.isEmpty()) {
            if (totalBetValue > 0L) {
                if (totalBetValue <= currentMoney) {
                    long fee = totalBetValue * 2L / 100L;
                    MoneyResponse moneyRes = new MoneyResponse(false, "1001");
                    if (!u.isBot()) {
                        moneyRes = this.userService.updateMoney(username, -totalBetValue, this.moneyTypeStr, this.gameName, "Quay " + gn, "\u0110\u1eb7t c\u01b0\u1ee3c " + gn, fee, Long.valueOf(referenceId), TransType.START_TRANS);
                    } else {
                        moneyRes.setSuccess(true);
                    }
                    if (moneyRes != null && moneyRes.isSuccess()) {
                        long moneyToPot = totalBetValue / 100L;
                        long moneyToFund = totalBetValue - fee - moneyToPot;
                        if (!u.isBot()) {
//                            this.fund += moneyToFund;
                        }
                        this.pot += moneyToPot;
                        boolean enoughPair = false;
                        ArrayList<AwardsOnLine<SpartanAward>> awardsOnLines = new ArrayList<>();
                        long totalPrizes = 0L;
                        long tienThuongX2 = 0L;
                        int countScatter = 0;
                        int countBonus = 0;
                        MiniGameSlotResponse miniGameSlot;
                        block4:
                        while (!enoughPair) {
                            int soLanNoHu;
                            result = 0;
                            awardsOnLines.clear();
                            totalPrizes = 0L;
                            tienThuongX2 = 0L;
                            String linesWin = "";
                            String prizesOnLine = "";
                            miniGameSlot = null;
                            countScatter = 0;
                            countBonus = 0;
                            boolean forceNoHu = false;

                            if (betValue == 100) {
                                soLanNoHu = ConfigGame.getIntValue(this.gameName + "_so_lan_no_hu_100");
//                                if (userForce.equals(username) && betValueCache.equals(String.valueOf(100))) {
//                                    forceNoHu = true;
//                                    forceJackpotByUser = true;
//                                }
                            } else if (betValue == 1000) {
                                soLanNoHu = ConfigGame.getIntValue(this.gameName + "_so_lan_no_hu_1000");
//                                if (userForce.equals(username) && betValueCache.equals(String.valueOf(1000))) {
//                                    forceNoHu = true;
//                                    forceJackpotByUser = true;
//                                }
                            } else {
                                soLanNoHu = ConfigGame.getIntValue(this.gameName + "_so_lan_no_hu_10000");
//                                if (userForce.equals(username) && betValueCache.equals(String.valueOf(10000))) {
//                                    forceNoHu = true;
//                                    forceJackpotByUser = true;
//                                }
                            }
                            SpartanItem[][] matrix = forceNoHu ? SpartanUtils.generateMatrixNoHu(lineArr) : SpartanUtils.generateMatrix();
                            for (int i = 0; i < 3; ++i) {
                                for (int j = 0; j < 5; ++j) {
                                    if (matrix[i][j] == SpartanItem.SCATTER) {
                                        ++countScatter;
                                        continue;
                                    }
                                    if (matrix[i][j] != SpartanItem.BONUS) continue;
                                    ++countBonus;
                                }
                            }
                            if (countBonus >= 3 || countScatter >= 3) {
                                Random rd2 = new Random();
                                int tiLeAn = lineArr.length * 100 / 25;
                                int n2 = rd2.nextInt(100);
                                if (n2 >= tiLeAn) continue;
                            }
                            if (countBonus >= 3) {
                                miniGameSlot = SpartanUtils.addMiniGameSlot(this.betValue, countBonus);
                                SpartanAward award = SpartanAwards.getAward(SpartanItem.BONUS, countBonus);
                                AwardsOnLine<SpartanAward> aol = new AwardsOnLine<>(award, miniGameSlot.getTotalPrize(), "line0");
                                awardsOnLines.add(aol);
                                result = 5;
                            }
                            SpartanItem[][] matrixWild = SpartanUtils.revertMatrix(matrix);
                            for (String entry2 : lineArr) {
                                ArrayList<SpartanAward> awardList = new ArrayList<>();
                                Line line = SpartanUtils.getLine(this.lines, matrixWild, Integer.parseInt(entry2));
                                SpartanUtils.calculateLine(line, awardList);
                                for (SpartanAward award2 : awardList) {
                                    long moneyOnLine = 0L;
                                    if (award2.getRatio() > 0.0f) {
                                        moneyOnLine = (long) (award2.getRatio() * (float) this.betValue);
                                    } else if (award2 == SpartanAward.PENTA_JACK_POT) {
                                        if (result == 3) {
                                            moneyOnLine = this.initJackpotValues;
                                        } else {
                                            if (this.huX2) {
                                                moneyOnLine = this.pot * 2L;
                                                tienThuongX2 = this.pot;
                                            } else {
                                                moneyOnLine = this.pot;
                                            }
                                            result = 3;
                                        }
                                    }
                                    AwardsOnLine<SpartanAward> aol2 = new AwardsOnLine<>(award2, moneyOnLine, line.getName());
                                    awardsOnLines.add(aol2);
                                }
                            }
                            StringBuilder builderLinesWin = new StringBuilder();
                            StringBuilder builderPrizesOnLine = new StringBuilder();
                            for (AwardsOnLine entry2 : awardsOnLines) {
                                if ((entry2.getAward() == SpartanAward.PENTA_JACK_POT || entry2.getAward() == SpartanAward.QUADAR_JACK_POT || entry2.getAward() == SpartanAward.TRIPLE_JACK_POT) && !forceNoHu)
                                    continue block4;
                                totalPrizes += entry2.getMoney();
                                builderLinesWin.append(",");
                                builderLinesWin.append(entry2.getLineId());
                                builderPrizesOnLine.append(",");
                                builderPrizesOnLine.append(entry2.getMoney());
                            }
                            if (builderLinesWin.length() > 0) {
                                builderLinesWin.deleteCharAt(0);
                            }
                            if (builderPrizesOnLine.length() > 0) {
                                builderPrizesOnLine.deleteCharAt(0);
                            }
                            enoughPair = true;
                            String matrixStr = SpartanUtils.matrixToString(matrix);
                            if (totalPrizes > 0L) {
                                if (result == 3) {
                                    if (this.huX2) {
                                        if (countNoHu < soLanNoHu * 2) continue;
                                        result = 4;
                                    } else {
                                        if (countNoHu < soLanNoHu) continue;
                                    }
                                    countNoHu = 0;
                                    this.noHuX2();
                                    this.pot = this.initJackpotValues;
                                    //this.fund -= totalPrizes - soTienNoHuKhongTruQuy;
//                                    this.fund = 0;
                                    if (this.moneyType == 1) {
                                        //GameUtils.sendSMSToUser(username, "Chuc mung " + username + " da no hu game " + gn + " phong " + this.betValue + ". So tien no hu: " + totalPrizes + " vin");
                                    }
                                    // get usercache
                                    HazelcastInstance client = HazelcastClientFactory.getInstance();
                                    IMap<String, UserModel> userMap = client.getMap("users");
                                    UserModel model;
                                    String displayName = username;
                                    if (userMap.containsKey(username)) {
                                        model = userMap.get(displayName);
                                        if (model.getClient() != null && !model.getClient().equals("")) {
                                            displayName = "[" + model.getClient() + "] " + username;
                                        } else {
                                            displayName = "[X] " + username;
                                        }
                                    } else {
                                        UserDaoImpl dao = new UserDaoImpl();
                                        try {
                                            model = dao.getUserByNickName(username);
                                            if (model.getClient() != null && !model.getClient().equals("")) {
                                                displayName = "[" + model.getClient() + "] " + username;
                                            } else {
                                                displayName = "[X] " + username;
                                            }
                                        } catch (SQLException ex) {
                                            ex.printStackTrace();
                                        }
                                    }

                                    if (forceJackpotByUser) {
                                        try {
                                            cacheService.removeKey(CACHE_NAME_USER_SPOT + this.gn);
                                            cacheService.removeKey(CACHE_BET_VALUE_SLOT + this.gn);
                                        } catch (Exception e) {
                                            logger.error(" Reset cache Spartan - Thần Tài error with : " + e.getMessage());
                                        }
                                    }
                                    this.slotService.logNoHu(referenceId, this.gameName, displayName, this.betValue, linesStr, matrixStr, builderLinesWin.toString(), builderPrizesOnLine.toString(), totalPrizes, result, currentTimeStr);
                                } else {
                                    countNoHu++;
                                    if (!u.isBot()) {
//                                        this.fund -= totalPrizes;
                                    }
                                    if (result == 0) {
                                        result = totalPrizes >= (this.betValue * 100L) ? (short) 2 : 1;
                                    }
                                }
                            }
                            msg.freeSpin = 0;//(byte)this.setFreeSpin(username, linesStr, countScatter);
                            long moneyExchange = totalPrizes - tienThuongX2;
                            //update when x2
                            // only save real user
                            if (tienThuongX2 > 0L && !u.isBot()) {
                                this.userService.updateMoney(username, tienThuongX2, this.moneyTypeStr, this.gameName, "Quay " + gn, "Th\u01b0\u1edfng h\u0169 X2", 0L, null, TransType.NO_VIPPOINT);
                            }
                            // only save real user
                            if (moneyExchange != 0 && !u.isBot()) {
                                moneyRes = this.userService.updateMoney(username, moneyExchange, this.moneyTypeStr, this.gameName, "Quay " + gn, this.buildDescription(totalBetValue, totalPrizes, result), 0L, Long.valueOf(referenceId), TransType.END_TRANS);
                                if (moneyRes != null && moneyRes.isSuccess()) {
                                    currentMoney = moneyRes.getCurrentMoney();
                                    if (this.moneyType == 1 && moneyExchange - (long) this.betValue >= (long) BroadcastMessageServiceImpl.MIN_MONEY) {
                                        this.broadcastMsgService.putMessage(Games.COWBOY.getId(), username, moneyExchange - (long) this.betValue);
                                    }
                                }
                            }
                            linesWin = builderLinesWin.toString();
                            prizesOnLine = builderPrizesOnLine.toString();
                            msg.referenceId = referenceId;
                            msg.matrix = SpartanUtils.matrixToString(matrix);
                            msg.linesWin = linesWin;
                            msg.prize = totalPrizes;
                            msg.isFreeSpin = false;
                            if (miniGameSlot != null) {
                                msg.haiSao = miniGameSlot.getPrizes();
                            }
                            try {
                                if (!u.isBot()) {
                                    this.slotService.logSpartan(referenceId, username, this.betValue, linesStr, linesWin, prizesOnLine, result, totalPrizes, currentTimeStr, matrixStr);

                                }
                                if (result == 3 || result == 4) {
                                    this.slotService.addTop(gn, username, this.betValue, totalPrizes, currentTimeStr, result);
                                }
                                if (result == 3 || result == 2 || result == 4) {
                                    BigWinSpartanMsg bigWinMsg = new BigWinSpartanMsg();
                                    bigWinMsg.username = username;
                                    bigWinMsg.type = (byte) result;
                                    bigWinMsg.betValue = (short) this.betValue;
                                    bigWinMsg.totalPrizes = totalPrizes;
                                    bigWinMsg.timestamp = DateTimeUtils.getCurrentTime();
                                    this.module.sendMsgToAllUsers(bigWinMsg);
                                }
                            } catch (InterruptedException | IOException | TimeoutException bigException) {
                                bigException.printStackTrace();
                            }
                            this.saveFund();
                            this.savePot();
                        }
                    }
                } else {
                    result = 102;
                }
            } else {
                result = 101;
            }
        } else {
            result = 101;
        }
        msg.result = (byte) result;
        msg.currentMoney = currentMoney;
        //Update cache tien hu
        cacheService.setValue(CACHE_JACK_POT_VALUE_SLOT + "_" + this.betValue + "_" + this.gn, String.valueOf(this.pot));
        if (forceJackpotByUser) {
            this.sendNotifyNoHu(username, (byte) 1, msg.prize, this.gameName.trim().toUpperCase());
        }
        return msg;
    }

    public ResultSpartanMsg playFreeDaily(String username, long referenceId) throws Exception {
        try {
            String linesStr = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25";
            long startTime = System.currentTimeMillis();
            String currentTimeStr = DateTimeUtils.getCurrentTime();
            short result = 0;
            String[] lineArr = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25".split(",");
            long currentMoney = this.userService.getMoneyUserCache(username, this.moneyTypeStr);
            UserCacheModel u = this.userService.getUser(username);
            ResultSpartanMsg msg = new ResultSpartanMsg();
            boolean enoughPair = false;
            ArrayList<AwardsOnLine<SpartanAward>> awardsOnLines = new ArrayList<AwardsOnLine<SpartanAward>>();
            long totalPrizes = 0L;
            long tienThuongX2 = 0L;
            int countScatter = 0;
            int countBonus = 0;

            block4:
            while (!enoughPair) {
                result = 0;
                awardsOnLines.clear();
                totalPrizes = 0L;
                String linesWin = "";
                String prizesOnLine = "";
                countScatter = 0;
                countBonus = 0;
                SpartanItem[][] matrix = SpartanUtils.generateMatrix();
                for (int i = 0; i < 3; ++i) {
                    for (int j = 0; j < 5; ++j) {
                        if (matrix[i][j] == SpartanItem.SCATTER) {
                            ++countScatter;
                            continue;
                        }
                        if (matrix[i][j] != SpartanItem.BONUS) continue;
                        ++countBonus;
                    }
                }
                if (countBonus >= 3 || countScatter >= 3) continue;
                SpartanItem[][] matrixWild = SpartanUtils.revertMatrix(matrix);
                for (String entry2 : lineArr) {
                    ArrayList<SpartanAward> awardList = new ArrayList<SpartanAward>();
                    Line line = SpartanUtils.getLine(this.lines, matrixWild, Integer.parseInt(entry2));
                    SpartanUtils.calculateLine(line, awardList);
                    for (SpartanAward award : awardList) {
                        long moneyOnLine = 0L;
                        if (award.getRatio() > 0.0f) {
                            moneyOnLine = (long) (award.getRatio() * (float) this.betValue);
                        } else if (award == SpartanAward.PENTA_JACK_POT) continue block4;
                        AwardsOnLine<SpartanAward> aol = new AwardsOnLine<SpartanAward>(award, moneyOnLine, line.getName());
                        awardsOnLines.add(aol);
                    }
                }
                StringBuilder builderLinesWin = new StringBuilder();
                StringBuilder builderPrizesOnLine = new StringBuilder();
                for (AwardsOnLine entry2 : awardsOnLines) {
                    if (entry2.getAward() == SpartanAward.PENTA_JACK_POT && !u.isBot()) continue block4;
                    totalPrizes += entry2.getMoney();
                    builderLinesWin.append(",");
                    builderLinesWin.append(entry2.getLineId());
                    builderPrizesOnLine.append(",");
                    builderPrizesOnLine.append(entry2.getMoney());
                }
                if (builderLinesWin.length() > 0) {
                    builderLinesWin.deleteCharAt(0);
                }
                if (builderPrizesOnLine.length() > 0) {
                    builderPrizesOnLine.deleteCharAt(0);
                }
                if (totalPrizes > (long) ConfigGame.getIntValue("max_prize_free_daily", 2000)) continue;
                enoughPair = true;
                boolean updated = this.slotService.updateLuotQuayFreeDaily(this.gameName, username, this.betValue);
                if (!updated) {
                    Debug.trace((Object) (String.valueOf(username) + " luot quay free " + this.gameName + " khong hop le"));
                    result = 103;
                    continue;
                }
                long moneyExchange = totalPrizes - 0L;
                if (moneyExchange > 0L) {
                    String des = "Quay " + gn + " Free ";
                    MoneyResponse moneyRes = this.userService.updateMoney(username, moneyExchange, this.moneyTypeStr, "SpartanVqFree", "Quay " + gn + " Free ", "C\u01b0\u1ee3c: 0, Th\u1eafng: " + totalPrizes, 0L, (Long) null, TransType.NO_VIPPOINT);
                    if (moneyRes != null && moneyRes.isSuccess()) {
                        currentMoney = moneyRes.getCurrentMoney();
                    }
                }
                String matrixStr = SpartanUtils.matrixToString(matrix);
                linesWin = builderLinesWin.toString();
                prizesOnLine = builderPrizesOnLine.toString();
                msg.referenceId = referenceId;
                msg.matrix = matrixStr;
                msg.linesWin = linesWin;
                msg.prize = totalPrizes;
                msg.isFreeSpin = false;
                try {
                    this.slotService.logSpartan(referenceId, username, (long) this.betValue, "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25", linesWin, prizesOnLine, result, totalPrizes, currentTimeStr, matrixStr);
                } catch (InterruptedException des) {
                } catch (TimeoutException des) {
                } catch (IOException des) {
                }
            }
            msg.result = (byte) result;
            msg.currentMoney = currentMoney;
            long endTime = System.currentTimeMillis();
            long handleTime = endTime - startTime;
            String ratioTime = CommonUtils.getRatioTime((long) handleTime);
            SlotUtils.logSpartan(referenceId, username, this.betValue, msg.matrix, msg.haiSao, result, handleTime, ratioTime, currentTimeStr);
            return msg;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public ResultSpartanMsg playFree(String username, String linesStr, String itemsWild, int ratio, long referenceId) {
        long startTime = System.currentTimeMillis();
        String currentTimeStr = DateTimeUtils.getCurrentTime();
        short result = 0;
        String[] lineArr = linesStr.split(",");
        long currentMoney = this.userService.getMoneyUserCache(username, this.moneyTypeStr);
        long totalBetValue = 0L;
        ResultSpartanMsg msg = new ResultSpartanMsg();
        long fee = 0L;
        long moneyToPot = 0L;
        long moneyToFund = 0L;
//        this.fund += 0L;
        this.pot += 0L;
        boolean enoughPair = false;
        ArrayList<AwardsOnLine<SpartanFreeSpinAward>> awardsOnLines = new ArrayList<AwardsOnLine<SpartanFreeSpinAward>>();
        long totalPrizes = 0L;
        while (!enoughPair) {
            SlotFreeSpin freeSpin;
            result = 0;
            awardsOnLines.clear();
            totalPrizes = 0L;
            String linesWin = "";
            String prizesOnLine = "";
            String haiSao = "";
            SpartanItem[][] matrix = SpartanUtils.generateMatrixFreeSpin(itemsWild);
            for (String entry : lineArr) {
                ArrayList<SpartanFreeSpinAward> awardList = new ArrayList<SpartanFreeSpinAward>();
                Line line = SpartanUtils.getLine(this.lines, matrix, Integer.parseInt(entry));
                SpartanUtils.calculateFreeSpinLine(line, awardList);
                for (SpartanFreeSpinAward award : awardList) {
                    long moneyOnLine = 0L;
                    if (!(award.getRatio() > 0.0f)) continue;
                    moneyOnLine = (long) (award.getRatio() * (float) this.betValue);
                    AwardsOnLine<SpartanFreeSpinAward> aol = new AwardsOnLine<SpartanFreeSpinAward>(award, moneyOnLine, line.getName());
                    awardsOnLines.add(aol);
                }
            }
            StringBuilder builderLinesWin = new StringBuilder();
            StringBuilder builderPrizesOnLine = new StringBuilder();
            for (AwardsOnLine entry2 : awardsOnLines) {
                totalPrizes += entry2.getMoney();
                builderLinesWin.append(",");
                builderLinesWin.append(entry2.getLineId());
                builderPrizesOnLine.append(",");
                builderPrizesOnLine.append(entry2.getMoney());
            }
            if (builderLinesWin.length() > 0) {
                builderLinesWin.deleteCharAt(0);
            }
            if (builderPrizesOnLine.length() > 0) {
                builderPrizesOnLine.deleteCharAt(0);
            }
            int tmpPrizes = (int) totalPrizes;
//            if (result == 3 ? this.fund - totalPrizes < 0L : this.fund - (totalPrizes *= (long) ratio) < this.initJackpotValues * 2L && totalPrizes - 0L >= 0L)
//                continue;
            enoughPair = true;
            if (totalPrizes > 0L) {
//                this.fund -= totalPrizes;
                if (result == 0) {
                    result = 1;
                }
            }
            long moneyExchange = totalPrizes - 0L;
            String des = gn + " - Free";
            MoneyResponse moneyRes = this.userService.updateMoney(username, moneyExchange, this.moneyTypeStr, this.gameName, gn + " - Free", this.buildDescription(0L, totalPrizes, result), 0L, (Long) null, TransType.VIPPOINT);
            if (moneyRes != null && moneyRes.isSuccess()) {
                currentMoney = moneyRes.getCurrentMoney();
                if (this.moneyType == 1 && moneyExchange >= (long) BroadcastMessageServiceImpl.MIN_MONEY) {
                    this.broadcastMsgService.putMessage(Games.LADY_NIGHT.getId(), username, moneyExchange);
                }
                this.slotService.addPrizes(this.cacheFreeSpinName, username, tmpPrizes);
            }
            if ((freeSpin = this.slotService.updateLuotQuaySlotFree(this.cacheFreeSpinName, username)).getNum() == 0) {
                SpartanTotalFreeSpin totalFreeSpinMsg = new SpartanTotalFreeSpin();
                totalFreeSpinMsg.prize = freeSpin.getPrizes();
                totalFreeSpinMsg.ratio = (byte) ratio;
                SlotUtils.sendMessageToUser((BaseMsg) totalFreeSpinMsg, username);
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 5; ++j) {
                    if (matrix[i][j] != SpartanItem.WILD) continue;
                    sb.append(String.valueOf(i) + "," + j + ",");
                }
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            this.slotService.setItemsWild(this.cacheFreeSpinName, username, sb.toString());
            linesWin = builderLinesWin.toString();
            prizesOnLine = builderPrizesOnLine.toString();
            msg.referenceId = referenceId;
            msg.matrix = SpartanUtils.matrixToString(matrix);
            msg.linesWin = linesWin;
            msg.prize = totalPrizes;
            msg.haiSao = "";
            msg.freeSpin = (byte) freeSpin.getNum();
            msg.isFreeSpin = true;
            msg.itemsWild = sb.toString();
            msg.ratioFree = (byte) ratio;
            try {
                this.slotService.logSpartan(referenceId, username, this.betValue, linesStr, linesWin, prizesOnLine, result, totalPrizes, currentTimeStr, msg.matrix);
            } catch (InterruptedException i) {
            } catch (TimeoutException i) {
            } catch (IOException i) {
                // empty catch block
            }
            this.saveFund();
            this.savePot();
        }
        msg.result = (byte) result;
        msg.currentMoney = currentMoney;
        long endTime = System.currentTimeMillis();
        long handleTime = endTime - startTime;
        String ratioTime = CommonUtils.getRatioTime((long) handleTime);
        SlotUtils.logSpartan(referenceId, username, this.betValue, msg.matrix, msg.haiSao, result, handleTime, ratioTime, currentTimeStr);
        return msg;
    }

    private int setFreeSpin(String nickName, String lines, int countFreeSpin) {
        int soLuot = 0;
        switch (countFreeSpin) {
            case 3: {
                soLuot = 8;
                this.slotService.setLuotQuayFreeSlot(this.cacheFreeSpinName, nickName, lines, soLuot, 1, betValue);
                break;
            }
            case 4: {
                soLuot = 8;
                this.slotService.setLuotQuayFreeSlot(this.cacheFreeSpinName, nickName, lines, soLuot, 2, betValue);
                break;
            }
            case 5: {
                soLuot = 8;
                this.slotService.setLuotQuayFreeSlot(this.cacheFreeSpinName, nickName, lines, soLuot, 3, betValue);
            }
        }
        return soLuot;
    }

    public short play(User user, String linesStr) throws Exception {
        String username = user.getName();
        int numFree = 0;
//        if (user.getProperty((Object)"numFreeDaily") != null) {
//            numFree = (Integer)user.getProperty((Object)"numFreeDaily");
//        }
        ResultSpartanMsg msg = null;
        SpartanFreeDailyMsg freeDailyMsg = new SpartanFreeDailyMsg();
        freeDailyMsg.remain = 0;
//        if (numFree > 0) {
//            Debug.trace("numFree:" + numFree);
//            try
//            {
//                msg = this.playFreeDaily(username, this.module.getNewReferenceId());
//                freeDailyMsg.remain = (byte)(--numFree);
//                if (numFree > 0) {
//                    user.setProperty((Object)"numFreeDaily", (Object)numFree);
//                } else {
//                    user.removeProperty((Object)"numFreeDaily");
//                }
//            }
//            catch (Exception ex)
//            {
//                throw ex;       
//            }
//        } else {
//            msg = this.play(username, linesStr);
//        }        
        msg = this.play(username, linesStr);
        if (this.isUserMinimize(user)) {
            MinimizeResultSpartanMsg miniMsg = new MinimizeResultSpartanMsg();
            miniMsg.prize = msg.prize;
            miniMsg.curretMoney = msg.currentMoney;
            miniMsg.result = msg.result;
            SlotUtils.sendMessageToUser((BaseMsg) miniMsg, user);
        } else {
            SlotUtils.sendMessageToUser((BaseMsg) msg, user);
            SlotUtils.sendMessageToUser((BaseMsg) freeDailyMsg, user);
        }
        return msg.result;
    }

    private void saveFund() {
        long currentTime = System.currentTimeMillis();
        /*if (currentTime - this.lastTimeUpdateFundToRoom >= 60000L) {
            try {
                this.mgService.saveFund(this.name, this.fund);
            } catch (IOException | InterruptedException | TimeoutException ex2) {
                Exception ex;
                Exception e = ex = ex2;
                Debug.trace((Object[]) new Object[]{String.valueOf(this.gameName) + ": update fund error ", e.getMessage()});
            }
            this.lastTimeUpdateFundToRoom = currentTime;
        }*/
//        try {
////            this.miniGameService.saveFund(this.name, this.fund);
//        } catch (IOException | InterruptedException | TimeoutException ex2) {
//            Exception ex;
//            Exception e = ex = ex2;
//            Debug.trace((Object[]) new Object[]{String.valueOf(this.gameName) + ": update fund error ", e.getMessage()});
//        }
//        this.lastTimeUpdateFundToRoom = currentTime;
    }

    private void savePot() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - this.lastTimeUpdatePotToRoom >= 3000L) {
            this.lastTimeUpdatePotToRoom = currentTime;
            try {
                this.miniGameService.savePot(this.name, this.pot, this.huX2);
            } catch (IOException | InterruptedException | TimeoutException ex2) {
                Exception ex;
                Exception e = ex = ex2;
                Debug.trace((Object[]) new Object[]{String.valueOf(this.gameName) + ": update pot error ", e.getMessage()});
            }
            UpdatePotSpartanMsg msg = new UpdatePotSpartanMsg();
            msg.value = this.pot;
            msg.x2 = (byte) (this.huX2 ? 1 : 0);
            this.sendMessageToRoom(msg);
        }
    }

    public void updatePot(User user) {
        UpdatePotSpartanMsg msg = new UpdatePotSpartanMsg();
        msg.value = this.pot;
        msg.x2 = (byte) (this.huX2 ? 1 : 0);
        SlotUtils.sendMessageToUser((BaseMsg) msg, user);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void gameLoop() {
        ArrayList<AutoUser> usersPlay = new ArrayList<AutoUser>();
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
            ArrayList<AutoUser> tmp = new ArrayList<AutoUser>(usersPlay.subList(fromIndex, toIndex));
            PlayListAutoUserTask task = new PlayListAutoUserTask(tmp);
//            PlayListAutoUserTask task = new PlayListAutoUserTask(this, tmp);
            this.executor.execute(task);
        }
        usersPlay.clear();
    }

    @Override
    protected void checkResetPot() {
        try {

            int isReset = cacheService.getValueInt("reset_pot_" + this.gn + "_" + this.betValue);
            if (isReset == 1) {
                this.pot = this.initJackpotValues;
//                this.fund = 0;
                this.savePot();
                this.saveFund();
                this.cacheService.removeKey("reset_pot_" + this.gn + "_" + this.betValue);

            }
        } catch (Exception e) {

        }
    }

    @Override
    protected void playListAuto(List<AutoUser> users) {
        for (AutoUser user : users) {
            try {
                short result = this.play(user.getUser(), user.getLines());
                if (result == 3 || result == 4 || result == 101 || result == 102 || result == 100) {
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
                Logger.getLogger(SpartanRoom.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        users.clear();
    }

    @Override
    public boolean joinRoom(User user) {
        boolean result = super.joinRoom(user);
        SlotFreeDaily model = this.slotService.getLuotQuayFreeDaily(this.gameName, user.getName(), this.betValue);
        SpartanFreeDailyMsg freeDailyMsg = new SpartanFreeDailyMsg();
        if (model != null && model.getRotateFree() > 0) {
            user.setProperty((Object) "numFreeDaily", (Object) model.getRotateFree());
            freeDailyMsg.remain = (byte) model.getRotateFree();
        } else {
            user.removeProperty((Object) "numFreeDaily");
        }
        SlotUtils.sendMessageToUser((BaseMsg) freeDailyMsg, user);
        if (result) {
            user.setProperty((Object) ("MGROOM_" + this.gameName + "_INFO"), (Object) this);
        }
        return result;
    }

    //    public ResultSpartanMsg playNormal(String username, String linesStr, long referenceId) {
//        long startTime = System.currentTimeMillis();
//        String currentTimeStr = DateTimeUtils.getCurrentTime();
//        short result = 0;
//        String[] lineArr = linesStr.split(",");
//        long currentMoney = this.userService.getMoneyUserCache(username, this.moneyTypeStr);
//        UserCacheModel u = this.userService.getUser(username);
//        long totalBetValue = lineArr.length * this.betValue;
//        ResultSpartanMsg msg = new ResultSpartanMsg();
//
//        boolean forceJackpotByUser = false;
//        //get force user jackpot
//        CacheServiceImpl cacheService = new CacheServiceImpl();
//        String userForce = "";
//        try {
//            userForce = cacheService.getValueStr(CACHE_NAME_USER_SPOT + this.gn);
//        } catch (Exception e) {
//            userForce = "";
//        }
//
//        if (lineArr.length > 0 && !linesStr.isEmpty()) {
//            if (totalBetValue > 0L) {
//                //kiểm tra số tiền cược vẫn đủ trong tk
//                if (totalBetValue <= currentMoney) {
//                    //phí sàn 2%
//                    long fee = totalBetValue * 2L / 100L;
//                    MoneyResponse moneyRes = new MoneyResponse(false, "1001");
//                    if (!u.isBot()) {
//                        moneyRes = this.userService.updateMoney(username, -totalBetValue, this.moneyTypeStr, this.gameName, "Quay " + gn, "\u0110\u1eb7t c\u01b0\u1ee3c " + gn, fee, Long.valueOf(referenceId), TransType.START_TRANS);
//                    } else {
//                        moneyRes.setSuccess(true);
//                    }
//                    //MoneyResponse moneyRes = this.userService.updateMoney(username, -totalBetValue, this.moneyTypeStr, this.gameName, "Quay " + gn, "\u0110\u1eb7t c\u01b0\u1ee3c " + gn, fee, Long.valueOf(referenceId), TransType.START_TRANS);
//                    if (moneyRes != null && moneyRes.isSuccess()) {
//                        //cắt 1% trên tổng tiền cược cho vào hũ
//                        long moneyToPot = totalBetValue * 1L / 100L;
//                        //Tiền thực tế cược còn lại sẽ trừ phí sàn và phí nhét vào hũ
//                        long moneyToFund = totalBetValue - fee - moneyToPot;
//                        if (!u.isBot()) {
//                            //đối user thật cộng dồn tiền cược
//                            this.fund += moneyToFund;
//                        }
//
//                        this.pot += moneyToPot;
//                        boolean enoughPair = false;
//                        ArrayList<AwardsOnLine<SpartanAward>> awardsOnLines = new ArrayList<AwardsOnLine<SpartanAward>>();
//                        long totalPrizes = 0L;
//                        long soTienNoHuKhongTruQuy = 0L;
//                        long tienThuongX2 = 0L;
//                        int countScatter = 0;
//                        int countBonus = 0;
//                        MiniGameSlotResponse miniGameSlot = null;
//                        block4:
//                        while (!enoughPair) {
//                            int n;
//                            int soLanNoHu;
//                            Random rd;
//                            result = 0;
//                            awardsOnLines.clear();
//                            totalPrizes = 0L;
//                            soTienNoHuKhongTruQuy = 0L;
//                            tienThuongX2 = 0L;
//                            String linesWin = "";
//                            String prizesOnLine = "";
//                            miniGameSlot = null;
//                            countScatter = 0;
//                            countBonus = 0;
//                            boolean forceNoHu = false;
//
//                            //todo : check thêm điều kiện loại hũ trước khi nổ
//                            if (betValue == 100) {
//                                soLanNoHu = ConfigGame.getIntValue(this.gameName + "_so_lan_no_hu_100");
//                                if (lineArr.length >= 20 && soLanNoHu > 0 && this.fund > this.pot * 2L && (n = (rd = new Random()).nextInt(soLanNoHu)) == 0 && countNoHu >= soLanNoHu) {
//                                    forceNoHu = true;
//                                }
//                                if (userForce.equals(username) && lineArr.length >= 20 && soLanNoHu >= 0) {
//                                    forceNoHu = true;
//                                    forceJackpotByUser = true;
//                                }
//
//                            } else if (betValue == 1000){
//                                soLanNoHu = ConfigGame.getIntValue(this.gameName + "_so_lan_no_hu_1000");
//                                if (lineArr.length >= 25 && soLanNoHu > 0 && this.fund > this.pot * 3L && (n = (rd = new Random()).nextInt(soLanNoHu)) == 0 && countNoHu >= soLanNoHu) {
//                                    forceNoHu = true;
//                                }
//                                if (userForce.equals(username) && lineArr.length >= 25 && soLanNoHu > 0 && this.fund > this.pot * 2L && countNoHu >= soLanNoHu) {
//                                    forceNoHu = true;
//                                    forceJackpotByUser = true;
//                                }
//                            } else if (betValue == 5000) {
//                                soLanNoHu = ConfigGame.getIntValue(this.gameName + "_so_lan_no_hu_5000");
//                                if (lineArr.length >= 25 && soLanNoHu > 0 && this.fund > this.pot * 2L && (n = (rd = new Random()).nextInt(soLanNoHu)) == 0 && countNoHu >= soLanNoHu) {
//                                    forceNoHu = true;
//                                }
//                                if (userForce.equals(username) && lineArr.length >= 25 && soLanNoHu > 0 && this.fund > this.pot * 2L && countNoHu >= soLanNoHu) {
//                                    forceNoHu = true;
//                                    forceJackpotByUser = true;
//                                }
//
//                            } else {
//                                soLanNoHu = ConfigGame.getIntValue(this.gameName + "_so_lan_no_hu_10000");
//                                if (lineArr.length >= 25 && soLanNoHu > 0 && this.fund > this.pot * 3L && (n = (rd = new Random()).nextInt(soLanNoHu)) == 0 && countNoHu >= soLanNoHu) {
//                                    forceNoHu = true;
//                                }
//                                if (userForce.equals(username) && lineArr.length >= 25 && soLanNoHu > 0 && this.fund > this.pot * 2L && countNoHu >= soLanNoHu) {
//                                    forceNoHu = true;
//                                    forceJackpotByUser = true;
//                                }
//                            }
//                            //logger.info(gn + " username: " + username + " forceNoHu:" + forceNoHu);
//                            // kiem tra neu no hu
//                            if (forceNoHu && !u.isBot()) {
////                                try {
////                                    LogMoneyUserDao logService = new LogMoneyUserDaoImpl();
////                                    long total_user_receive = 0;
////                                    long total_agency_receive = 0;
////
////                                    AgentServiceImpl service = new AgentServiceImpl();
////                                    List<AgentResponse> agents = service.listAgent();
////                                    ArrayList<String> agentNames = new ArrayList<String>();
////                                    if (agents != null && agents.size() > 0) {
////                                        for (AgentResponse agent : agents) {
////                                            agentNames.add(agent.nickName);
////                                        }
////                                    }
////                                    List<LogUserMoneyResponse> resulReceive = logService.searchAllLogMoneyUser(username, "RECEIVE", false);
////                                    if (resulReceive != null && resulReceive.size() > 0) {
////                                        for (LogUserMoneyResponse trans : resulReceive) {
////                                            boolean matchAgent = false;
////                                            for (String s : agentNames) {
////                                                if (trans.description.contains(s)) {
////                                                    matchAgent = true;
////                                                }
////                                            }
////                                            if (matchAgent) {
////                                                total_agency_receive += trans.moneyExchange;
////                                            } else {
////                                                total_user_receive += trans.moneyExchange;
////                                            }
////                                        }
////                                    }
////
////                                    long total_recharge_card_money = 0;//total_agency_receive - userModel.getRechargeMoney();
////                                    List<LogUserMoneyResponse> resultCard = logService.searchAllLogMoneyUser(username, "CARD", false);
////                                    if (resultCard != null && resultCard.size() > 0) {
////                                        total_recharge_card_money = resultCard.stream().map((trans) -> trans.moneyExchange).reduce(total_recharge_card_money, (accumulator, _item) -> accumulator + _item);
////                                        //logger.info(gn + " username: " + username + " total_recharge_card_money:" + total_recharge_card_money
////                                        //);
////                                        total_recharge_card_money = 0;
////                                        for (LogUserMoneyResponse trans : resultCard) {
////                                            total_recharge_card_money += trans.moneyExchange;
////                                        }
////                                        //logger.info(gn + " username: " + username + " total_recharge_card_money 2:" + total_recharge_card_money
////                                        //);
////                                    }
////                                    long total_deposit_bank = 0;
////                                    long total_deposit_momo = 0;
////
////                                    //search total deposit bank
//////                                    List<LogUserMoneyResponse> resultBank = logService.searchAllLogMoneyUser(username, "BANK", false);
//////                                    if (resultBank != null && resultBank.size() > 0) {
//////                                        total_deposit_bank = resultBank.stream().map((trans) -> trans.moneyExchange).reduce(total_deposit_bank, (accumulator, _item) -> accumulator + _item);
//////
//////                                        total_deposit_bank = 0;
//////                                        for (LogUserMoneyResponse trans : resultBank) {
//////                                            total_deposit_bank += trans.moneyExchange;
//////                                        }
//////                                    }
////                                    //search total deposit momo
////
//////                                    List<LogUserMoneyResponse> resultMomo = logService.searchAllLogMoneyUser(username, "MOMO", false);
//////                                    if (resultMomo != null && resultMomo.size() > 0) {
//////                                        total_deposit_momo = resultMomo.stream().map((trans) -> trans.moneyExchange).reduce(total_deposit_momo, (accumulator, _item) -> accumulator + _item);
//////
//////                                        total_deposit_momo = 0;
//////                                        for (LogUserMoneyResponse trans : resultMomo) {
//////                                            total_deposit_momo += trans.moneyExchange;
//////                                        }
//////
//////                                    }
////
//////                                    if ((total_agency_receive == 0 && total_recharge_card_money == 0 && total_deposit_momo == 0 && total_deposit_bank == 0)
//////                                            || (total_agency_receive + total_recharge_card_money + total_deposit_momo + total_deposit_bank) < 500000) {
//////                                        forceNoHu = false;
//////                                    }
////                                } catch (Exception ex) {
////                                    Debug.trace(ex.getMessage());
////                                    StringWriter sw = new StringWriter();
////                                    PrintWriter pw = new PrintWriter(sw);
////                                    ex.printStackTrace(pw);
////                                    String sStackTrace = sw.toString(); // stack trace as a string
////                                    Debug.trace((Object) sStackTrace);
////                                    forceNoHu = false;
////                                }
//                            }
//
//                            SpartanItem[][] matrix = forceNoHu ? SpartanUtils.generateMatrixNoHu(lineArr) : SpartanUtils.generateMatrix();
//                            for (int i = 0; i < 3; ++i) {
//                                for (int j = 0; j < 5; ++j) {
//                                    if (matrix[i][j] == SpartanItem.SCATTER) {
//                                        ++countScatter;
//                                        continue;
//                                    }
//                                    if (matrix[i][j] != SpartanItem.BONUS) continue;
//                                    ++countBonus;
//                                }
//                            }
//                            if (countBonus >= 3 || countScatter >= 3) {
//                                Random rd2 = new Random();
//                                int tiLeAn = lineArr.length * 100 / 25;
//                                int n2 = rd2.nextInt(100);
//                                if (n2 >= tiLeAn) continue;
//                            }
//                            if (countBonus >= 3) {
//                                miniGameSlot = SpartanUtils.addMiniGameSlot(this.betValue, countBonus);
//                                SpartanAward award = SpartanAwards.getAward(SpartanItem.BONUS, countBonus);
//                                AwardsOnLine<SpartanAward> aol = new AwardsOnLine<SpartanAward>(award, miniGameSlot.getTotalPrize(), "line0");
//                                awardsOnLines.add(aol);
//                                result = 5;
//                            }
//                            SpartanItem[][] matrixWild = SpartanUtils.revertMatrix(matrix);
//                            for (String entry2 : lineArr) {
//                                ArrayList<SpartanAward> awardList = new ArrayList<SpartanAward>();
//                                Line line = SpartanUtils.getLine(this.lines, matrixWild, Integer.parseInt(entry2));
//                                SpartanUtils.calculateAward(line, awardList);
//                                for (SpartanAward award2 : awardList) {
//                                    long moneyOnLine = 0L;
//                                    if (award2.getRatio() > 0.0f) {
//                                        moneyOnLine = (long) (award2.getRatio() * (float) this.betValue);
//                                    } else if (award2 == SpartanAward.PENTA_JACK_POT) {
//                                        if (result == 3) {
//                                            moneyOnLine = this.initPotValue;
//                                        } else {
//                                            if (this.huX2) {
//                                                moneyOnLine = this.pot * 2L;
//                                                tienThuongX2 = this.pot;
//                                                soTienNoHuKhongTruQuy += this.pot;
//                                            } else {
//                                                moneyOnLine = this.pot;
//                                            }
//                                            result = 3;
//                                            soTienNoHuKhongTruQuy += this.pot - this.initPotValue;
//                                        }
//                                    }
//                                    AwardsOnLine<SpartanAward> aol2 = new AwardsOnLine<SpartanAward>(award2, moneyOnLine, line.getName());
//                                    awardsOnLines.add(aol2);
//                                }
//                            }
//                            StringBuilder builderLinesWin = new StringBuilder();
//                            StringBuilder builderPrizesOnLine = new StringBuilder();
//                            for (AwardsOnLine entry2 : awardsOnLines) {
//                                if ((entry2.getAward() == SpartanAward.PENTA_JACK_POT || entry2.getAward() == SpartanAward.QUADAR_JACK_POT || entry2.getAward() == SpartanAward.TRIPLE_JACK_POT) && !forceNoHu)
//                                    continue block4;
//                              /*  if (betValue == 100) {
//
//                                    if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_100") == 0) // cho cả người và bot nổ hũ
//                                    {
////                                        if ((entry2.getAward() == SpartanAward.PENTA_JACK_POT || entry2.getAward() == SpartanAward.QUADAR_JACK_POT || entry2.getAward() == SpartanAward.TRIPLE_JACK_POT))
////                                            continue block4;
//                                    } else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_100") == 1) // chỉ cho bot nổ hũ
//                                    {
//                                        if ((entry2.getAward() == SpartanAward.PENTA_JACK_POT || entry2.getAward() == SpartanAward.QUADAR_JACK_POT || entry2.getAward() == SpartanAward.TRIPLE_JACK_POT) && !u.isBot())
//                                            continue block4;
//                                    } else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_100") == -1) // chỉ cho người nổ hũ
//                                    {
//                                        if ((entry2.getAward() == SpartanAward.PENTA_JACK_POT || entry2.getAward() == SpartanAward.QUADAR_JACK_POT || entry2.getAward() == SpartanAward.TRIPLE_JACK_POT) && u.isBot())
//                                            continue block4;
//                                    } else {
//                                        if ((entry2.getAward() == SpartanAward.PENTA_JACK_POT || entry2.getAward() == SpartanAward.QUADAR_JACK_POT || entry2.getAward() == SpartanAward.TRIPLE_JACK_POT) && !u.isBot())
//                                            continue block4;
//                                    }
//                                }
//                                else if (betValue == 1000) {
//                                    if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_1000") == 0) // cho cả người và bot nổ hũ
//                                    {
//                                        if ((entry2.getAward() == SpartanAward.PENTA_JACK_POT || entry2.getAward() == SpartanAward.QUADAR_JACK_POT || entry2.getAward() == SpartanAward.TRIPLE_JACK_POT))
//                                            continue block4;
//                                    } else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_1000") == 1) // chỉ cho bot nổ hũ
//                                    {
//                                        if ((entry2.getAward() == SpartanAward.PENTA_JACK_POT || entry2.getAward() == SpartanAward.QUADAR_JACK_POT || entry2.getAward() == SpartanAward.TRIPLE_JACK_POT) && !u.isBot())
//                                            continue block4;
//                                    } else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_1000") == -1) // chỉ cho người nổ hũ
//                                    {
//                                        if ((entry2.getAward() == SpartanAward.PENTA_JACK_POT || entry2.getAward() == SpartanAward.QUADAR_JACK_POT || entry2.getAward() == SpartanAward.TRIPLE_JACK_POT) && u.isBot())
//                                            continue block4;
//                                    } else {
//                                        if ((entry2.getAward() == SpartanAward.PENTA_JACK_POT || entry2.getAward() == SpartanAward.QUADAR_JACK_POT || entry2.getAward() == SpartanAward.TRIPLE_JACK_POT) && !u.isBot())
//                                            continue block4;
//                                    }
//                                }
//                                else if (betValue == 5000) {
//                                    if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_5000") == 0) // cho cả người và bot nổ hũ
//                                    {
//                                        if ((entry2.getAward() == SpartanAward.PENTA_JACK_POT || entry2.getAward() == SpartanAward.QUADAR_JACK_POT || entry2.getAward() == SpartanAward.TRIPLE_JACK_POT))
//                                            continue block4;
//                                    } else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_5000") == 1) // chỉ cho bot nổ hũ
//                                    {
//                                        if ((entry2.getAward() == SpartanAward.PENTA_JACK_POT || entry2.getAward() == SpartanAward.QUADAR_JACK_POT || entry2.getAward() == SpartanAward.TRIPLE_JACK_POT) && !u.isBot())
//                                            continue block4;
//                                    } else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_5000") == -1) // chỉ cho người nổ hũ
//                                    {
//                                        if ((entry2.getAward() == SpartanAward.PENTA_JACK_POT || entry2.getAward() == SpartanAward.QUADAR_JACK_POT || entry2.getAward() == SpartanAward.TRIPLE_JACK_POT) && u.isBot())
//                                            continue block4;
//                                    } else {
//                                        if ((entry2.getAward() == SpartanAward.PENTA_JACK_POT || entry2.getAward() == SpartanAward.QUADAR_JACK_POT || entry2.getAward() == SpartanAward.TRIPLE_JACK_POT) && !u.isBot())
//                                            continue block4;
//                                    }
//                                }
//                                else {
//                                    if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_10000") == 0) // cho cả người và bot nổ hũ
//                                    {
//                                        if ((entry2.getAward() == SpartanAward.PENTA_JACK_POT || entry2.getAward() == SpartanAward.QUADAR_JACK_POT || entry2.getAward() == SpartanAward.TRIPLE_JACK_POT))
//                                            continue block4;
//                                    } else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_10000") == 1) // chỉ cho bot nổ hũ
//                                    {
//                                        if ((entry2.getAward() == SpartanAward.PENTA_JACK_POT || entry2.getAward() == SpartanAward.QUADAR_JACK_POT || entry2.getAward() == SpartanAward.TRIPLE_JACK_POT) && !u.isBot())
//                                            continue block4;
//                                    } else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_10000") == -1) // chỉ cho người nổ hũ
//                                    {
//                                        if ((entry2.getAward() == SpartanAward.PENTA_JACK_POT || entry2.getAward() == SpartanAward.QUADAR_JACK_POT || entry2.getAward() == SpartanAward.TRIPLE_JACK_POT) && u.isBot())
//                                            continue block4;
//                                    } else {
//                                        if ((entry2.getAward() == SpartanAward.PENTA_JACK_POT || entry2.getAward() == SpartanAward.QUADAR_JACK_POT || entry2.getAward() == SpartanAward.TRIPLE_JACK_POT) && !u.isBot())
//                                            continue block4;
//                                    }
//                                }*/
//                                totalPrizes += entry2.getMoney();
//                                builderLinesWin.append(",");
//                                builderLinesWin.append(entry2.getLineId());
//                                builderPrizesOnLine.append(",");
//                                builderPrizesOnLine.append(entry2.getMoney());
//                            }
//                            if (builderLinesWin.length() > 0) {
//                                builderLinesWin.deleteCharAt(0);
//                            }
//                            if (builderPrizesOnLine.length() > 0) {
//                                builderPrizesOnLine.deleteCharAt(0);
//                            }
////                            if (result == 3 ? this.fund - (totalPrizes - soTienNoHuKhongTruQuy) < 0L : this.fund - totalPrizes < this.pot * 2L && totalPrizes - totalBetValue >= 0L)
////                                continue;
//
//                            enoughPair = true;
//                            String matrixStr = SpartanUtils.matrixToString(matrix);
//                            // if user win
//                            if (totalPrizes > 0L) {
//                                if (result == 3) {
//                                    if (this.huX2) {
//                                        if (countNoHu < soLanNoHu * 2) continue;
//                                        result = 4;
//                                    } else {
//                                        if (countNoHu < soLanNoHu) continue;
//                                    }
//                                    countNoHu = 0;
//                                    this.noHuX2();
//                                    this.pot = this.initPotValue;
//                                    //this.fund -= totalPrizes - soTienNoHuKhongTruQuy;
//                                    this.fund = 0;
//                                    if (this.moneyType == 1) {
//                                        //GameUtils.sendSMSToUser(username, "Chuc mung " + username + " da no hu game " + gn + " phong " + this.betValue + ". So tien no hu: " + totalPrizes + " vin");
//                                    }
//                                    // get usercache
//                                    HazelcastInstance client = HazelcastClientFactory.getInstance();
//                                    IMap<String, UserModel> userMap = client.getMap("users");
//                                    UserModel model = null;
//                                    String displayName = username;
//                                    if (userMap.containsKey(username)) {
//                                        model = userMap.get(displayName);
//                                        if (model.getClient() != null && model.getClient() != "") {
//                                            displayName = "[" + model.getClient() + "] " + username;
//                                        } else {
//                                            displayName = "[X] " + username;
//                                        }
//                                    } else {
//                                        UserDaoImpl dao = new UserDaoImpl();
//                                        try {
//                                            model = dao.getUserByNickName(username);
//                                            if (model.getClient() != null && model.getClient() != "") {
//                                                displayName = "[" + model.getClient() + "] " + username;
//                                            } else {
//                                                displayName = "[X] " + username;
//                                            }
//                                        } catch (SQLException ex) {
//
//                                        }
//                                    }
//
//                                    if (forceJackpotByUser) {
//                                        try {
//                                            cacheService.removeKey(CACHE_NAME_USER_SPOT + this.gn);
//                                        } catch (Exception e) {
//
//                                        }
//                                    }
//
//                                    this.slotService.logNoHu(referenceId, this.gameName, displayName, this.betValue, linesStr, matrixStr, builderLinesWin.toString(), builderPrizesOnLine.toString(), totalPrizes, result, currentTimeStr);
//                                } else {
//                                    countNoHu++;
//                                    if (!u.isBot()) {
//                                        this.fund -= totalPrizes;
//                                    }
//                                    if (result == 0) {
//                                        result = totalPrizes >= (long) (this.betValue * 100) ? (short) 2 : 1;
//                                    }
//                                }
//                            }
//                            msg.freeSpin = 0;//(byte)this.setFreeSpin(username, linesStr, countScatter);
//                            long moneyExchange = totalPrizes - tienThuongX2;
//                            //String des = "Quay " + gn;
//                            //update when x2
//                            // only save real user
//                            if (tienThuongX2 > 0L && !u.isBot()) {
//                                this.userService.updateMoney(username, tienThuongX2, this.moneyTypeStr, this.gameName, "Quay " + gn, "Th\u01b0\u1edfng h\u0169 X2", 0L, (Long) null, TransType.NO_VIPPOINT);
//                            }
//                            // only save real user
//                            if (moneyExchange != 0 && !u.isBot()) {
//                                moneyRes = this.userService.updateMoney(username, moneyExchange, this.moneyTypeStr, this.gameName, "Quay " + gn, this.buildDescription(totalBetValue, totalPrizes, result), 0L, Long.valueOf(referenceId), TransType.END_TRANS);
//                                if (moneyRes != null && moneyRes.isSuccess()) {
//                                    currentMoney = moneyRes.getCurrentMoney();
//                                    if (this.moneyType == 1 && moneyExchange - (long) this.betValue >= (long) BroadcastMessageServiceImpl.MIN_MONEY) {
//                                        this.broadcastMsgService.putMessage(Games.SPARTAN.getId(), username, moneyExchange - (long) this.betValue);
//                                    }
//                                }
//                            }
//
//                            linesWin = builderLinesWin.toString();
//                            prizesOnLine = builderPrizesOnLine.toString();
//                            msg.referenceId = referenceId;
//                            msg.matrix = SpartanUtils.matrixToString(matrix);
//                            msg.linesWin = linesWin;
//                            msg.prize = totalPrizes;
//                            msg.isFreeSpin = false;
//                            if (miniGameSlot != null) {
//                                msg.haiSao = miniGameSlot.getPrizes();
//                            }
//                            try {
//                                // only save real user
//                                if (!u.isBot()) {
//                                    this.slotService.logSpartan(referenceId, username, (long) this.betValue, linesStr, linesWin, prizesOnLine, result, totalPrizes, currentTimeStr);
//                                }
//                                if (result == 3 || result == 4) {
//                                    this.slotService.addTop(gn, username, this.betValue, totalPrizes, currentTimeStr, (int) result);
//                                }
//                                if (result == 3 || result == 2 || result == 4) {
//                                    BigWinSpartanMsg bigWinMsg = new BigWinSpartanMsg();
//                                    bigWinMsg.username = username;
//                                    bigWinMsg.type = (byte) result;
//                                    bigWinMsg.betValue = (short) this.betValue;
//                                    bigWinMsg.totalPrizes = totalPrizes;
//                                    bigWinMsg.timestamp = DateTimeUtils.getCurrentTime();
//                                    this.module.sendMsgToAllUsers(bigWinMsg);
//                                }
//                            } catch (InterruptedException bigWinMsg) {
//                            } catch (TimeoutException bigWinMsg) {
//                            } catch (IOException bigWinMsg) {
//                                // empty catch block
//                            }
//                            this.saveFund();
//                            this.savePot();
//                        }
//                    }
//                } else {
//                    result = 102;
//                }
//            } else {
//                result = 101;
//            }
//        } else {
//            result = 101;
//        }
//        msg.result = (byte) result;
//        msg.currentMoney = currentMoney;
//        long endTime = System.currentTimeMillis();
//        long handleTime = endTime - startTime;
//        String ratioTime = CommonUtils.getRatioTime((long) handleTime);
//        SlotUtils.logSpartan(referenceId, username, this.betValue, msg.matrix, msg.haiSao, result, handleTime, ratioTime, currentTimeStr);
//       //update cache tien hu
//        cacheService.setValue(CACHE_JACK_POT_VALUE_SLOT + "_" + this.betValue + "_"  + this.gn , String.valueOf(this.pot));
//        if (forceJackpotByUser) {
//            this.sendNotifyNoHu(username, (byte) 1, msg.prize,"SPARTAN");
//        }
//        return msg;
//    }
}

