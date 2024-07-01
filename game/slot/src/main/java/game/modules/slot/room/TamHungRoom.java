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
 *  com.vinplay.vbee.common.models.UserModel
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
import game.modules.slot.TamHungModule;
import game.modules.slot.cmd.send.tamhung.*;
import game.modules.slot.entities.slot.AutoUser;
import game.modules.slot.entities.slot.MiniGameSlotResponse;
import game.modules.slot.entities.slot.PickStarGift;
import game.modules.slot.entities.slot.PickStarGiftItem;
import game.modules.slot.entities.slot.PickStarGifts;
import game.modules.slot.entities.slot.vqv.AwardsOnLine;
import game.modules.slot.entities.slot.vqv.Line;
import game.modules.slot.entities.slot.vqv.VQVAward;
import game.modules.slot.entities.slot.vqv.VQVItem;
import game.modules.slot.entities.slot.vqv.VQVLines;
import game.modules.slot.utils.SlotUtils;
import game.modules.slot.utils.VQVUtils;
import game.util.ConfigGame;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

// todo : game tay du ky
public class TamHungRoom
        extends SlotRoom {
    private final Runnable gameLoopTask = new SlotRoom.GameLoopTask();
    private final Runnable checkResetPotTask = new CheckResetPot();
    private VQVLines lines = new VQVLines();
    private long lastTimeUpdatePotToRoom = 0L;
    private long lastTimeUpdateFundToRoom = 0L;
    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    private List<Integer> boxValues = new ArrayList<Integer>();
    private String gn;
    private int countNoHu = 0;
    private static final org.apache.log4j.Logger logger = Logger.getLogger((String) "slot");

    public TamHungRoom(TamHungModule module, byte id, String name, short moneyType, long pot, long fund, int betValue, long initPotValue) {
        super(id, Games.TAMHUNG.getName(), name, betValue, moneyType, pot, fund, initPotValue);
        this.gameName = Games.TAMHUNG.getName();
        this.cacheFreeSpinName = String.valueOf(this.gameName) + betValue;
        this.module = module;
        this.moneyTypeStr = this.moneyType == 1 ? "vin" : "xu";
        CacheServiceImpl cacheService = new CacheServiceImpl();
        cacheService.setValue(name, (int) pot);
        this.betValue = betValue;
        this.initJackpotValues = initPotValue;
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.gameLoopTask, 10, 1, TimeUnit.SECONDS);
        this.boxValues.add(10);
        this.boxValues.add(10);
        this.boxValues.add(10);
        this.boxValues.add(15);
        this.boxValues.add(20);
        this.gn = this.gameName;
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
            ForceStopAutoPlayTamHungMsg msg = new ForceStopAutoPlayTamHungMsg();
            SlotUtils.sendMessageToUser((BaseMsg) msg, user);
        }
    }

    private void broadcastBigWin(String username, byte result, long totalPrizes) {
        BigWinTamHungMsg bigWinMsg = new BigWinTamHungMsg();
        bigWinMsg.username = username;
        bigWinMsg.type = result;
        bigWinMsg.betValue = (short) this.betValue;
        bigWinMsg.totalPrizes = totalPrizes;
        bigWinMsg.timestamp = DateTimeUtils.getCurrentTime();
        this.module.sendMsgToAllUsers(bigWinMsg);
    }

    public synchronized ResultSlotMsg play(String username, String linesStr) {
        long referenceId = this.module.getNewReferenceId();
        SlotFreeSpin freeSpin = this.slotService.getLuotQuayFreeSlot(this.cacheFreeSpinName, username);
        int luotQuayFree = freeSpin.getNum();
        int ratioFree = freeSpin.getRatio();
        if (luotQuayFree > 0) {
            linesStr = freeSpin.getLines();
            return this.playFree(username, linesStr, ratioFree, referenceId);
        }
        return this.playNormal(username, linesStr, referenceId);
    }

    public synchronized ResultSlotMsg playNormal(String username, String linesStr, long referenceId) { // chơi bình thường
        long startTime = System.currentTimeMillis();
        String currentTimeStr = DateTimeUtils.getCurrentTime();
        short result = 0;
        String[] lineArr = linesStr.split(",");
        long currentMoney = this.userService.getMoneyUserCache(username, this.moneyTypeStr);
        UserCacheModel u = this.userService.getUser(username);
        long totalBetValue = lineArr.length * this.betValue;
        ResultSlotMsg msg = new ResultSlotMsg();
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
                    if (moneyRes != null && moneyRes.isSuccess()) { // check nếu trừ tiền thành công
                        long moneyToPot = totalBetValue * 1L / 100L;
                        long moneyToFund = totalBetValue - fee - moneyToPot;
                        if (!u.isBot()) {
//                            this.fund += moneyToFund;
                        }

                        this.pot += moneyToPot;
                        boolean enoughPair = false;
                        ArrayList<AwardsOnLine> awardsOnLines = new ArrayList<AwardsOnLine>();
                        long totalPrizes = 0L;
                        long soTienNoHuKhongTruQuy = 0L;
                        long tienThuongX2 = 0L;
                        int countFreeSpin = 0;
                        int ratio = 0;
                        int ratioBonus = 0;
                        block4:
                        while (!enoughPair) {
                            Random rd;
                            int soLanNoHu;
                            int n;
                            result = 0;
                            awardsOnLines.clear();
                            totalPrizes = 0L;
                            soTienNoHuKhongTruQuy = 0L;
                            tienThuongX2 = 0L;
                            String linesWin = "";
                            String prizesOnLine = "";
                            String haiSao = "";
                            countFreeSpin = 0;
                            ratio = 0;
                            ratioBonus = 0;
                            boolean forceNoHu = false;
//                            if (lineArr.length >= 5 && (soLanNoHu = ConfigGame.getIntValue(String.valueOf(this.gameName) + "_so_lan_no_hu")) > 0 && this.fund > this.initPotValue * 2L && (n = (rd = new Random()).nextInt(soLanNoHu)) == 0) {
//                                forceNoHu = true;
//                            }
                            if (betValue == 100) {
                                soLanNoHu = ConfigGame.getIntValue(this.gameName + "_so_lan_no_hu_100");
                                //int nrd = soLanNoHu > 0 ? (rd = new Random()).nextInt(soLanNoHu) : -1;
//                                int nrd = 0;
//
//                                if (lineArr.length == 15 && soLanNoHu > 0 && this.fund > this.pot * 2L && nrd == 0 && countNoHu >= soLanNoHu) {
//                                    forceNoHu = true;
//                                }
//                                if (userForce.equals(username) && lineArr.length >= 15 && soLanNoHu > 0 && this.fund > this.pot * 2L && countNoHu >= soLanNoHu) {
//                                    forceNoHu = true;
//                                    forceJackpotByUser = true;
//                                }

                                if (userForce.equals(username) && betValueCache.equals(String.valueOf(100))) {
                                    forceNoHu = true;
                                    forceJackpotByUser = true;
                                }
                            } else if (betValue == 1000) {
                                soLanNoHu = ConfigGame.getIntValue(this.gameName + "_so_lan_no_hu_1000");
//                                int nrd = 0;
                                if (userForce.equals(username) && betValueCache.equals(String.valueOf(1000))) {
                                    forceNoHu = true;
                                    forceJackpotByUser = true;
                                }
//                                if (lineArr.length == 20 && soLanNoHu > 0 && this.fund > this.pot * 3L && nrd == 0 && countNoHu >= soLanNoHu) {
//                                    forceNoHu = true;
//                                }
//                                if (userForce.equals(username) && lineArr.length >= 20 && soLanNoHu > 0 && this.fund > this.pot * 3L && countNoHu >= soLanNoHu) {
//                                    forceNoHu = true;
//                                    forceJackpotByUser = true;
//                                }

                            } else {
                                soLanNoHu = ConfigGame.getIntValue(this.gameName + "_so_lan_no_hu_10000");
//                                int nrd = 0;
                                if (userForce.equals(username) && betValueCache.equals(String.valueOf(10000))) {
                                    forceNoHu = true;
                                    forceJackpotByUser = true;
                                }
//                                if (lineArr.length == 20 && soLanNoHu > 0 && this.fund > this.pot * 3L && nrd == 0 && countNoHu >= soLanNoHu) {
//                                    forceNoHu = true;
//                                }
//                                if (userForce.equals(username) && lineArr.length >= 20 && soLanNoHu > 0 && this.fund > this.pot * 3L && countNoHu >= soLanNoHu) {
//                                    forceNoHu = true;
//                                    forceJackpotByUser = true;
//                                }
                            }
                            // kiem tra neu no hu

//                            if (forceNoHu)
//                            {
//                                try
//                                {   // check lịch sử giao dịch
//                                    LogMoneyUserDao logService = new LogMoneyUserDaoImpl();
//                                    long total_user_receive = 0;
//                                    long total_agency_receive = 0;
//
//                                    AgentServiceImpl service = new AgentServiceImpl();
//                                    List<AgentResponse> agents = service.listAgent();
//                                    ArrayList<String> agentNames = new ArrayList<String>();
//                                    if (agents != null && agents.size() > 0) {
//                                        for (AgentResponse agent : agents) {
//                                            agentNames.add(agent.nickName);
//                                        }
//                                    }
//                                    List<LogUserMoneyResponse> resulReceive = logService.searchAllLogMoneyUser(username, "RECEIVE",false); // tổng tiền nhận
//                                    if (resulReceive != null && resulReceive.size() > 0) {
//                                        for (LogUserMoneyResponse trans : resulReceive) {
//                                            boolean matchAgent = false;
//                                            for (String s : agentNames) {
//                                                if (trans.description.contains(s)) {
//                                                    matchAgent = true;
//                                                }
//                                            }
//                                            if (matchAgent) {
//                                                total_agency_receive += trans.moneyExchange;
//                                            } else {
//                                                total_user_receive += trans.moneyExchange;
//                                            }
//                                        }
//                                    }
//
//                                    long total_recharge_card_money = 0;//total_agency_receive - userModel.getRechargeMoney(); // tính tổng tiền nạp card
//                                    List<LogUserMoneyResponse> resultCard = logService.searchAllLogMoneyUser(username, "CARD",false);
//                                    if (resultCard != null && resultCard.size() > 0) {
//                                        total_recharge_card_money = resultCard.stream().map((trans) -> trans.moneyExchange).reduce(total_recharge_card_money, (accumulator, _item) -> accumulator + _item);
//
//                                        total_recharge_card_money = 0;
//                                        for (LogUserMoneyResponse trans : resultCard) {
//                                            total_recharge_card_money += trans.moneyExchange;
//                                        }
//
//                                    }
//                                    long total_deposit_bank = 0;
//                                    long total_deposit_momo = 0;
//
//                                    //search total deposit bank
//                                    // tổng tiền nạp bank
//                                    List<LogUserMoneyResponse> resultBank = logService.searchAllLogMoneyUser(username, "BANK",false);
//                                    if (resultBank != null && resultBank.size() > 0) {
//                                        //total_deposit_bank = resultBank.stream().map((trans) -> trans.moneyExchange).reduce(total_deposit_bank, (accumulator, _item) -> accumulator + _item);
//
//
//                                        for (LogUserMoneyResponse trans : resultBank) {
//                                            total_deposit_bank += trans.moneyExchange;
//                                        }
//                                    }
//                                    //search total deposit momo
//                                    // tổng nạp momo
//                                    List<LogUserMoneyResponse> resultMomo = logService.searchAllLogMoneyUser(username, "MOMO",false);
//                                    if (resultMomo != null && resultMomo.size() > 0) {
//                                        //total_deposit_momo = resultMomo.stream().map((trans) -> trans.moneyExchange).reduce(total_deposit_momo, (accumulator, _item) -> accumulator + _item);
//
//
//                                        for (LogUserMoneyResponse trans : resultMomo) {
//                                            total_deposit_momo += trans.moneyExchange;
//                                        }
//
//                                    }
//
//                                    if ((total_agency_receive == 0 && total_recharge_card_money == 0 && total_deposit_momo == 0 && total_deposit_bank == 0)
//                                            || (total_user_receive + total_recharge_card_money + total_deposit_momo + total_deposit_bank) < 500000)
//                                    {
//                                        forceNoHu = false;
//                                    } // nếu chưa nạp gì thì éo cho nổ
//                                }
//                                catch (Exception ex)
//                                {
//                                    Debug.trace(ex.getMessage());
//                                    StringWriter sw = new StringWriter();
//                                    PrintWriter pw = new PrintWriter(sw);
//                                    ex.printStackTrace(pw);
//                                    String sStackTrace = sw.toString(); // stack trace as a string
//                                    Debug.trace((Object)sStackTrace);
//                                    forceNoHu = false;
//                                }
//                            }

                            VQVItem[][] matrix = null;
                            if (forceNoHu) {
                                matrix = VQVUtils.generateMatrixNoHu(lineArr);
                            } else {
                                matrix = VQVUtils.generateMatrix();
                            }
                            // = forceNoHu ? VQVUtils.generateMatrixNoHu(lineArr) : VQVUtils.generateMatrix();
                            for (String entry2 : lineArr) {
                                ArrayList<VQVAward> awardList = new ArrayList<VQVAward>();
                                Line line = VQVUtils.getLine(this.lines, matrix, Integer.parseInt(entry2));
                                VQVUtils.calculateLine(line, awardList);
                                for (VQVAward award : awardList) {
                                    long moneyOnLine = 0L;
                                    if (award.getRatio() > 0.0f) {
                                        moneyOnLine = (long) (award.getRatio() * (float) this.betValue);
                                    } else if (award == VQVAward.PENTA_JACKPOT) {
                                        if (result != 3) {
                                            if (this.huX2) {
                                                moneyOnLine = this.pot * 2L;
                                                tienThuongX2 = this.pot;
                                                soTienNoHuKhongTruQuy += this.pot;
                                            } else {
                                                moneyOnLine = this.pot;
                                            }
                                            result = 3;
                                            soTienNoHuKhongTruQuy += this.pot - this.initJackpotValues;
                                        } else {
                                            moneyOnLine = this.initJackpotValues;
                                        }
                                    } else if (award.getRatio() == -2.0f) {
                                        if (ratioBonus > 0) continue block4;
                                        ratioBonus = 1;
                                        if (award == VQVAward.QUADRA_BONUS) {
                                            ratioBonus = 5;
                                        }
                                        MiniGameSlotResponse response = this.generatePickStars(ratioBonus);
                                        moneyOnLine = response.getTotalPrize();
                                        haiSao = response.getPrizes();
                                        if (result != 3) {
                                            result = 5;
                                        }
                                    } else if (award.getRatio() == -3.0f) {
                                        if (countFreeSpin > 0) continue block4;
                                        ++countFreeSpin;
                                        ratio = 1;
                                        if (award.getDuplicate() == VQVAward.PENTA_SCATTER.getDuplicate()) {
                                            ratio = 15;
                                        } else if (award.getDuplicate() == VQVAward.QUADRA_SCATTER.getDuplicate()) {
                                            ratio = 5;
                                        }
                                    }
                                    AwardsOnLine aol = new AwardsOnLine(award, moneyOnLine, line.getName());
                                    awardsOnLines.add(aol);
                                }
                            }
                            StringBuilder builderLinesWin = new StringBuilder();
                            StringBuilder builderPrizesOnLine = new StringBuilder();
                            for (AwardsOnLine entry2 : awardsOnLines) {
                                if (!forceNoHu && (entry2.getAward() == VQVAward.PENTA_JACKPOT || entry2.getAward() == VQVAward.QUADRA_JACKPOT || entry2.getAward() == VQVAward.TRIPLE_JACKPOT))
                                    continue block4;
                                if (forceNoHu == false || forceJackpotByUser == false) {
                                    if ((entry2.getAward() == VQVAward.PENTA_JACKPOT || entry2.getAward() == VQVAward.QUADRA_JACKPOT || entry2.getAward() == VQVAward.TRIPLE_JACKPOT)) {
                                        continue block4;
                                    }
                                }
//                                if (betValue == 100) {
//                                    if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_100") == 0) // cho cả người và bot nổ hũ
//                                    {
//                                        if ((entry2.getAward() == VQVAward.PENTA_JACKPOT || entry2.getAward() == VQVAward.QUADRA_JACKPOT )) //|| entry2.getAward() == VQVAward.TRIPLE_JACKPOT
//                                            continue block4;
//                                    } else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_100") == 1) // chỉ cho bot nổ hũ
//                                    {
//                                        if ((entry2.getAward() == VQVAward.PENTA_JACKPOT || entry2.getAward() == VQVAward.QUADRA_JACKPOT || entry2.getAward() == VQVAward.TRIPLE_JACKPOT) && !u.isBot())
//                                            continue block4;
//                                    } else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_100") == -1) // chỉ cho người nổ hũ
//                                    {
//                                        if ((entry2.getAward() == VQVAward.PENTA_JACKPOT || entry2.getAward() == VQVAward.QUADRA_JACKPOT || entry2.getAward() == VQVAward.TRIPLE_JACKPOT) && u.isBot())
//                                            continue block4;
//                                    } else {
//                                        if ((entry2.getAward() == VQVAward.PENTA_JACKPOT || entry2.getAward() == VQVAward.QUADRA_JACKPOT || entry2.getAward() == VQVAward.TRIPLE_JACKPOT) && !u.isBot())
//                                            continue block4;
//                                    }
//                                } else if (betValue == 1000) {
//                                    if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_1000") == 0) // cho cả người và bot nổ hũ
//                                    {
//                                        if ((entry2.getAward() == VQVAward.PENTA_JACKPOT || entry2.getAward() == VQVAward.QUADRA_JACKPOT || entry2.getAward() == VQVAward.TRIPLE_JACKPOT))
//                                            continue block4;
//                                    } else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_1000") == 1) // chỉ cho bot nổ hũ
//                                    {
//                                        if ((entry2.getAward() == VQVAward.PENTA_JACKPOT || entry2.getAward() == VQVAward.QUADRA_JACKPOT || entry2.getAward() == VQVAward.TRIPLE_JACKPOT) && !u.isBot())
//                                            continue block4;
//                                    } else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_1000") == -1) // chỉ cho người nổ hũ
//                                    {
//                                        if ((entry2.getAward() == VQVAward.PENTA_JACKPOT || entry2.getAward() == VQVAward.QUADRA_JACKPOT || entry2.getAward() == VQVAward.TRIPLE_JACKPOT) && u.isBot())
//                                            continue block4;
//                                    } else {
//                                        if ((entry2.getAward() == VQVAward.PENTA_JACKPOT || entry2.getAward() == VQVAward.QUADRA_JACKPOT || entry2.getAward() == VQVAward.TRIPLE_JACKPOT) && !u.isBot())
//                                            continue block4;
//                                    }
//                                } else {
//                                    if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_10000") == 0) // cho cả người và bot nổ hũ
//                                    {
//                                        if ((entry2.getAward() == VQVAward.PENTA_JACKPOT || entry2.getAward() == VQVAward.QUADRA_JACKPOT || entry2.getAward() == VQVAward.TRIPLE_JACKPOT))
//                                            continue block4;
//                                    } else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_10000") == 1) // chỉ cho bot nổ hũ
//                                    {
//                                        if ((entry2.getAward() == VQVAward.PENTA_JACKPOT || entry2.getAward() == VQVAward.QUADRA_JACKPOT || entry2.getAward() == VQVAward.TRIPLE_JACKPOT) && !u.isBot())
//                                            continue block4;
//                                    } else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_10000") == -1) // chỉ cho người nổ hũ
//                                    {
//                                        if ((entry2.getAward() == VQVAward.PENTA_JACKPOT || entry2.getAward() == VQVAward.QUADRA_JACKPOT || entry2.getAward() == VQVAward.TRIPLE_JACKPOT) && u.isBot())
//                                            continue block4;
//                                    } else {
//                                        if ((entry2.getAward() == VQVAward.PENTA_JACKPOT || entry2.getAward() == VQVAward.QUADRA_JACKPOT || entry2.getAward() == VQVAward.TRIPLE_JACKPOT) && !u.isBot())
//                                            continue block4;
//                                    }
//                                }
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
//                            if (result == 3 ? this.fund - (totalPrizes - soTienNoHuKhongTruQuy) < 0L : this.fund - totalPrizes < this.initPotValue * 2L && totalPrizes - totalBetValue >= 0L)
//                                continue;
                            enoughPair = true;
                            String matrixStr = VQVUtils.matrixToString(matrix);
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
                                        //GameUtils.sendSMSToUser(username, "Chuc mung " + username + " da no hu game " + gn + " phong " + this.betValue + ". So tien no hu: " + totalPrizes + " " + "GS");
                                    }
                                    // get usercache
                                    HazelcastInstance client = HazelcastClientFactory.getInstance();
                                    IMap<String, UserModel> userMap = client.getMap("users");
                                    UserModel model = null;
                                    String displayName = username;
                                    if (userMap.containsKey((Object) username)) {
                                        model = (UserModel) userMap.get((Object) displayName);
                                        if (model.getClient() != null && model.getClient() != "") {
                                            displayName = "[" + model.getClient() + "] " + username;
                                        } else {
                                            displayName = "[X] " + username;
                                        }
                                    } else {
                                        UserDaoImpl dao = new UserDaoImpl();
                                        try {
                                            model = dao.getUserByNickName(username);
                                            if (model.getClient() != null && model.getClient() != "") {
                                                displayName = "[" + model.getClient() + "] " + username;
                                            } else {
                                                displayName = "[X] " + username;
                                            }
                                        } catch (SQLException ex) {

                                        }
                                    }

                                    if (forceJackpotByUser) {
                                        try {
                                            cacheService.removeKey(CACHE_NAME_USER_SPOT + this.gn);
                                            cacheService.removeKey(CACHE_BET_VALUE_SLOT + this.gn);
                                        } catch (Exception e) {
                                            logger.error(" Reset cache TAMHUNG - TayDUKY error with : " + e.getMessage());
                                        }
                                    }
                                    this.slotService.logNoHu(referenceId, this.gameName, displayName, this.betValue, linesStr, matrixStr, builderLinesWin.toString(), builderPrizesOnLine.toString(), totalPrizes, result, currentTimeStr);
                                } else {
                                    countNoHu++;
                                    if (!u.isBot()) {
//                                        this.fund -= totalPrizes;
                                    }

                                    if (result == 0) {
                                        result = totalPrizes >= (long) (this.betValue * 100) ? (short) 2 : 1;
                                    }
                                }
                            }
                            long moneyExchange = totalPrizes - tienThuongX2;
                            String des = "Quay " + gn;
                            if (tienThuongX2 > 0L && !u.isBot()) {
                                this.userService.updateMoney(username, tienThuongX2, this.moneyTypeStr, this.gameName, des, "Th\u01b0\u1edfng h\u0169 X2", 0L, (Long) null, TransType.NO_VIPPOINT);
                            }
                            if (totalPrizes != 0 && !u.isBot()) {
                                if ((moneyRes = this.userService.updateMoney(username, totalPrizes, this.moneyTypeStr, this.gameName, des, this.buildDescription(totalBetValue, totalPrizes, result), 0L, Long.valueOf(referenceId), TransType.END_TRANS)) != null && moneyRes.isSuccess()) {
                                    currentMoney = moneyRes.getCurrentMoney();
                                    if (this.moneyType == 1 && moneyExchange >= (long) BroadcastMessageServiceImpl.MIN_MONEY) {
                                        this.broadcastMsgService.putMessage(Games.TAMHUNG.getId(), username, moneyExchange - totalBetValue);
                                    }
                                }
                            }

                            linesWin = builderLinesWin.toString();
                            prizesOnLine = builderPrizesOnLine.toString();
                            msg.referenceId = referenceId;
                            msg.matrix = matrixStr;
                            msg.linesWin = linesWin;
                            msg.prize = totalPrizes;
                            msg.haiSao = haiSao;
                            if (countFreeSpin > 0) {
                                msg.isFreeSpin = 1;
                                msg.ratio = (byte) ratio;
                                this.slotService.setLuotQuayFreeSlot(this.cacheFreeSpinName, username, linesStr, countFreeSpin, ratio, betValue);
                            } else {
                                msg.isFreeSpin = 0;
                            }
                            try {
                                if (!u.isBot()) {

                                    this.slotService.logTamHung(referenceId, username, (long) this.betValue, linesStr, linesWin, prizesOnLine, result, totalPrizes, currentTimeStr, matrixStr);

                                }
                                if (result == 3 || result == 4) {
                                    this.slotService.addTop(gn, username, this.betValue, totalPrizes, currentTimeStr, (int) result);
                                }
                                if (result == 3 || result == 2 || result == 4) {
                                    this.broadcastBigWin(username, (byte) result, totalPrizes);
                                }
                            } catch (InterruptedException award) {
                            } catch (TimeoutException award) {
                            } catch (IOException award) {
                                // empty catch block
                            }
                            this.saveFund();
                            this.savePot();
                        }
                    } else {
                        result = 102;
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
        long endTime = System.currentTimeMillis();
        long handleTime = endTime - startTime;
        String ratioTime = CommonUtils.getRatioTime((long) handleTime);
        //Update cache tien hu
        cacheService.setValue(CACHE_JACK_POT_VALUE_SLOT + "_" + this.betValue + "_" + this.gn, String.valueOf(this.pot));
        if (forceJackpotByUser) {
            this.sendNotifyNoHu(username, (byte) 1, msg.prize, "TAMHUNG");
        }
        return msg;
    }

    public synchronized ResultSlotMsg playFree(String username, String linesStr, int ratio, long referenceId) {
        long startTime = System.currentTimeMillis();
        String currentTimeStr = DateTimeUtils.getCurrentTime();
        short result = 0;
        String[] lineArr = linesStr.split(",");
        long currentMoney = this.userService.getMoneyUserCache(username, this.moneyTypeStr);
        long totalBetValue = lineArr.length * this.betValue;
        ResultSlotMsg msg = new ResultSlotMsg();
        if (lineArr.length > 0 && !linesStr.isEmpty()) {
            if (totalBetValue > 0L) {
                boolean enoughPair = false;
                ArrayList<AwardsOnLine> awardsOnLines = new ArrayList<AwardsOnLine>();
                long totalPrizes = 0L;
                block4:
                while (!enoughPair) {
                    Random rd;
                    MoneyResponse moneyRes;
                    int soLanNoHu;
                    String des232;
                    long moneyExchange;
                    int n;
                    result = 0;
                    awardsOnLines.clear();
                    totalPrizes = 0L;
                    String linesWin = "";
                    String prizesOnLine = "";
                    String haiSao = "";
                    boolean forceNoHu = false;
//                    if (lineArr.length >= 5 && (soLanNoHu = ConfigGame.getIntValue(String.valueOf(this.gameName) + "_so_lan_no_hu")) > 0 && this.fund > this.initPotValue * 2L && (n = (rd = new Random()).nextInt(soLanNoHu)) == 0) {
//                        forceNoHu = true;
//                    }
                    VQVItem[][] matrix = forceNoHu ? VQVUtils.generateMatrixNoHu(lineArr) : VQVUtils.generateMatrix();
                    for (String entry2 : lineArr) {
                        ArrayList<VQVAward> awardList = new ArrayList<VQVAward>();
                        Line line = VQVUtils.getLine(this.lines, matrix, Integer.parseInt(entry2));
                        VQVUtils.calculateLine(line, awardList);
                        for (VQVAward award : awardList) {
                            long moneyOnLine = 0L;
                            if (award.getRatio() <= 0.0f) continue block4;
                            moneyOnLine = (long) (award.getRatio() * (float) this.betValue);
                            AwardsOnLine aol = new AwardsOnLine(award, moneyOnLine, line.getName());
                            awardsOnLines.add(aol);
                        }
                    }
                    StringBuilder builderLinesWin = new StringBuilder();
                    StringBuilder builderPrizesOnLine = new StringBuilder();
                    for (AwardsOnLine entry2 : awardsOnLines) {
                        if (entry2.getAward() == VQVAward.PENTA_JACKPOT && !this.isBot(username)) continue block4;
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
//                    if (this.fund - (totalPrizes *= (long) ratio) < this.initPotValue * 2L && totalPrizes - totalBetValue >= 0L)
//                        continue;
                    enoughPair = true;
                    if (totalPrizes > 0L) {
                        if (result == 3) {
                            if (this.huX2) {
                                result = 4;
                            }
                            this.noHuX2();
                            this.pot = this.initJackpotValues;
//                            this.fund -= totalPrizes;
                        } else {
//                            this.fund -= totalPrizes;
                            if (result == 0) {
                                result = totalPrizes >= (long) (this.betValue * 100) ? (short) 2 : 1;
                            }
                        }
                    }
                    if ((moneyExchange = totalPrizes) > 0L && (moneyRes = this.userService.updateMoney(username, totalPrizes, this.moneyTypeStr, this.gameName, des232 = gn + " - Free", this.buildDescription(totalBetValue, totalPrizes, result), 0L, (Long) null, TransType.VIPPOINT)) != null && moneyRes.isSuccess()) {
                        currentMoney = moneyRes.getCurrentMoney();
                        if (this.moneyType == 1 && moneyExchange >= (long) BroadcastMessageServiceImpl.MIN_MONEY) {
                            this.broadcastMsgService.putMessage(Games.TAMHUNG.getId(), username, moneyExchange - totalBetValue);
                        }
                    }
                    this.slotService.updateLuotQuaySlotFree(this.cacheFreeSpinName, username);
                    linesWin = builderLinesWin.toString();
                    prizesOnLine = builderPrizesOnLine.toString();
                    msg.referenceId = referenceId;
                    msg.matrix = VQVUtils.matrixToString(matrix);
                    msg.linesWin = linesWin;
                    msg.prize = totalPrizes / (long) ratio;
                    msg.haiSao = "";
                    msg.isFreeSpin = 0;
                    msg.ratio = (byte) ratio;
                    try {
                        if (!isBot(username)) {
                            this.slotService.logTamHung(referenceId, username, (long) this.betValue, linesStr, linesWin, prizesOnLine, result, totalPrizes, currentTimeStr, msg.matrix);

                        }
                        if (result == 3 || result == 4) {
                            this.slotService.addTop(this.gameName, username, this.betValue, totalPrizes, currentTimeStr, (int) result);
                        }
                        if (result == 3 || result == 2 || result == 4) {
                            this.broadcastBigWin(username, (byte) result, totalPrizes);
                        }
                    } catch (InterruptedException des232_0) {
                    } catch (TimeoutException des232_1) {
                    } catch (IOException des232_2) {
                        // empty catch block
                    }
                    this.saveFund();
                    this.savePot();
                }
            } else {
                result = 101;
            }
        } else {
            result = 101;
        }
        msg.result = (byte) result;
        msg.currentMoney = currentMoney;
        long endTime = System.currentTimeMillis();
        long handleTime = endTime - startTime;
        String ratioTime = CommonUtils.getRatioTime((long) handleTime);
        return msg;
    }

    public synchronized ResultSlotMsg playFreeDaily(String username) {
        long startTime = System.currentTimeMillis();
        String currentTimeStr = DateTimeUtils.getCurrentTime();
        long refernceId = this.module.getNewReferenceId();
        short result = 0;
        String linesStr = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20";
        String[] lineArr = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20".split(",");
        long currentMoney = this.userService.getMoneyUserCache(username, this.moneyTypeStr);
        ResultSlotMsg msg = new ResultSlotMsg();
        boolean enoughPair = false;
        ArrayList<AwardsOnLine> awardsOnLines = new ArrayList<AwardsOnLine>();
        long totalPrizes = 0L;
        block4:
        while (!enoughPair) {
            result = 0;
            awardsOnLines.clear();
            totalPrizes = 0L;
            String linesWin = "";
            String prizesOnLine = "";
            String haiSao = "";
            VQVItem[][] matrix = VQVUtils.generateMatrix();
            for (String entry2 : lineArr) {
                ArrayList<VQVAward> awardList = new ArrayList<VQVAward>();
                Line line = VQVUtils.getLine(this.lines, matrix, Integer.parseInt(entry2));
                VQVUtils.calculateLine(line, awardList);
                for (VQVAward award : awardList) {
                    long money = 0L;
                    if (award.getRatio() <= 0.0f) continue block4;
                    money = (long) (award.getRatio() * (float) this.betValue);
                    AwardsOnLine aol = new AwardsOnLine(award, money, line.getName());
                    awardsOnLines.add(aol);
                }
            }
            StringBuilder builderLinesWin = new StringBuilder();
            StringBuilder builderPrizesOnLine = new StringBuilder();
            for (AwardsOnLine entry2 : awardsOnLines) {
                if ((entry2.getAward() == VQVAward.PENTA_JACKPOT || entry2.getAward() == VQVAward.QUADRA_JACKPOT || entry2.getAward() == VQVAward.TRIPLE_JACKPOT))
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
//            if (this.fund - totalPrizes < 0L || totalPrizes > (long) ConfigGame.getIntValue("max_prize_free_daily", 2000))
//                continue;
            enoughPair = true;
            boolean updated = this.slotService.updateLuotQuayFreeDaily(this.gameName, username, this.betValue);
            if (!updated) {
                result = 103;
            } else {
                MoneyResponse moneyRes;
                String des;
                long moneyExchange = totalPrizes;
                if (moneyExchange > 0L && (moneyRes = this.userService.updateMoney(username, totalPrizes, this.moneyTypeStr, "ThoDanVqFree", des = gn + " Free", "C\u01b0\u1ee3c: 0, Th\u1eafng: " + totalPrizes, 0L, (Long) null, TransType.NO_VIPPOINT)) != null && moneyRes.isSuccess()) {
                    currentMoney = moneyRes.getCurrentMoney();
                }
            }
            linesWin = builderLinesWin.toString();
            prizesOnLine = builderPrizesOnLine.toString();
            msg.referenceId = refernceId;
            msg.matrix = VQVUtils.matrixToString(matrix);
            msg.linesWin = linesWin;
            msg.prize = totalPrizes;
            msg.haiSao = "";
            try {
                if (!checkDieuKienNo(username))
                    this.slotService.logVQV(refernceId, username, (long) this.betValue, "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20", linesWin, prizesOnLine, result, totalPrizes, currentTimeStr, msg.matrix);
            } catch (InterruptedException moneyExchange) {
            } catch (TimeoutException moneyExchange) {
            } catch (IOException moneyExchange) {
            }
        }
        msg.result = (byte) result;
        msg.currentMoney = currentMoney;
        long endTime = System.currentTimeMillis();
        long handleTime = endTime - startTime;
        String ratioTime = CommonUtils.getRatioTime((long) handleTime);
        //SlotUtils.logVQV(refernceId, username, this.betValue, msg.matrix, msg.haiSao, result, handleTime, ratioTime, currentTimeStr);
        return msg;
    }

    private MiniGameSlotResponse generatePickStars(int ratio) {
        MiniGameSlotResponse response = new MiniGameSlotResponse();
        int totalMoney = 0;
        ArrayList<PickStarGift> gifts = new ArrayList<PickStarGift>();
        PickStarGifts pickStarGifts = new PickStarGifts();
        String responsePickStars = "";
        boolean totalKeys = true;
        block5:
        for (int numPicks = 10; numPicks > 0; --numPicks) {
            PickStarGiftItem gift = pickStarGifts.pickRandomAndRandomGift();
            switch (gift) {
                case GOLD: {
                    totalMoney += 4 * this.betValue * 1;
                    gifts.add(new PickStarGift(PickStarGiftItem.GOLD, 0));
                    continue block5;
                }
                case KEY: {
                    gifts.add(new PickStarGift(PickStarGiftItem.KEY, 0));
                    continue block5;
                }
                case BOX: {
                    int boxValue = this.randomBoxValue();
                    totalMoney += boxValue * this.betValue * 1;
                    gifts.add(new PickStarGift(PickStarGiftItem.BOX, boxValue));
                    break;
                }
            }
        }
        totalMoney *= ratio;
        responsePickStars = String.valueOf(responsePickStars) + ratio;
        for (PickStarGift pickStarGift : gifts) {
            if (responsePickStars.length() == 0) {
                responsePickStars = String.valueOf(responsePickStars) + pickStarGift.getName();
                continue;
            }
            responsePickStars = String.valueOf(responsePickStars) + "," + pickStarGift.getName();
        }
        response.setTotalPrize(totalMoney);
        response.setPrizes(responsePickStars);
        return response;
    }

    private int randomBoxValue() {
        Random rd = new Random();
        int n = rd.nextInt(this.boxValues.size());
        return this.boxValues.get(n);
    }

    public short play(User user, String linesStr) { // play with line selected from fe
        String username = user.getName();
        int numFree = 0;
        if (user.getProperty((Object) "numFreeDaily") != null) {
            numFree = (Integer) user.getProperty((Object) "numFreeDaily");
        }
        ResultSlotMsg msg = null;
        TamHungFreeDailyMsg freeDailyMsg = new TamHungFreeDailyMsg();
        if (numFree > 0) {
            msg = this.playFreeDaily(username);
            freeDailyMsg.remain = (byte) (--numFree);
            if (numFree > 0) {
                user.setProperty((Object) "numFreeDaily", (Object) numFree);
            } else {
                user.removeProperty((Object) "numFreeDaily");
            }
        } else {
            msg = this.play(username, linesStr);
        }
        if (this.isUserMinimize(user)) {
            MinimizeResultTamHungMsg miniMsg = new MinimizeResultTamHungMsg();
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
        if (currentTime - this.lastTimeUpdateFundToRoom >= 60000L) {
//            try {
//                this.miniGameService.saveFund(this.name, this.fund);
//            } catch (IOException | InterruptedException | TimeoutException ex2) {
//                Exception ex;
//                Exception e = ex = ex2;
//                Debug.trace((Object[]) new Object[]{String.valueOf(this.gameName) + ": update fund error ", e.getMessage()});
//            }
//            this.lastTimeUpdateFundToRoom = currentTime;
        }
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
                Debug.trace((Object[]) new Object[]{String.valueOf(this.gameName) + ": update pot poker error ", e.getMessage()});
            }
            byte x2 = (byte) (this.huX2 ? 1 : 0);
            ((TamHungModule) this.module).updatePot(this.id, this.pot, x2);
        }
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
            SlotRoom.PlayListAutoUserTask task = new SlotRoom.PlayListAutoUserTask(tmp);
            this.executor.execute(task);
        }
        usersPlay.clear();
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
            user.setProperty((Object) "numFreeDaily", (Object) model.getRotateFree());
            TamHungFreeDailyMsg freeDailyMsg = new TamHungFreeDailyMsg();
            freeDailyMsg.remain = (byte) model.getRotateFree();
            SlotUtils.sendMessageToUser((BaseMsg) freeDailyMsg, user);
        } else {
            user.removeProperty((Object) "numFreeDaily");
        }
        if (result) {
            user.setProperty((Object) ("MGROOM_" + this.gameName + "_INFO"), (Object) this);
        }
        return result;
    }


}

