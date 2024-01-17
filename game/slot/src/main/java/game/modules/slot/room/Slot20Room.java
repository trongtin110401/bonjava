
package game.modules.slot.room;

import bitzero.server.BitZeroServer;
import bitzero.server.entities.User;

import bitzero.util.common.business.Debug;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import com.vinplay.dal.dao.LogMoneyUserDao;
import com.vinplay.dal.dao.impl.LogMoneyUserDaoImpl;
import com.vinplay.dal.service.impl.AgentServiceImpl;
import com.vinplay.dal.service.impl.BroadcastMessageServiceImpl;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.usercore.dao.impl.UserDaoImpl;
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.models.cache.SlotFreeDaily;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.response.AgentResponse;
import com.vinplay.vbee.common.response.LogUserMoneyResponse;
import com.vinplay.vbee.common.response.MoneyResponse;
import com.vinplay.vbee.common.statics.TransType;
import com.vinplay.vbee.common.utils.CommonUtils;
import com.vinplay.vbee.common.utils.DateTimeUtils;

import game.modules.slot.Slot20Module;
import game.modules.slot.SlotModule;
import game.modules.slot.cmd.Slot20CommandCollection;
import game.modules.slot.cmd.send.slot20line.*;
import game.modules.slot.entities.slot.*;

import game.modules.slot.entities.slot.line20basic.Line20;
import game.modules.slot.entities.slot.line20basic.Line20Award;
import game.modules.slot.entities.slot.line20basic.Line20Item;
import game.modules.slot.entities.slot.line20basic.Line20Lines;
import game.modules.slot.listener.SlotLogListener;

import game.modules.slot.utils.Slot20Utils;
import game.modules.slot.utils.SlotUtils;

import game.util.ConfigGame;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Slot20Room extends SlotRoom {
    private final Runnable gameLoopTask = new GameLoopTask();
    private final Runnable checkResetPotTask = new CheckResetPot();
    private final Line20Lines lines = new Line20Lines();
    private long lastTimeUpdatePotToRoom = 0L;
    private long lastTimeUpdateFundToRoom = 0L;
    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    private final List<Integer> boxValues = new ArrayList<>();
    private String gn;
    private int countNoHu = 0;

    private SlotLogListener slotLogListener;
    private Slot20CommandCollection commandCollection;

    public Slot20Room(SlotModule module, Slot20CommandCollection commandCollection, SlotLogListener slotLogListener, String gameName, byte id, String room, short moneyType, long pot, long fund, int betValue, long initPotValue) {
        super(id, room, betValue, moneyType, pot, fund, initPotValue);
        this.commandCollection = commandCollection;
        this.slotLogListener = slotLogListener;
        this.gameName = gameName;
        this.module = module;
        this.moneyTypeStr = this.moneyType == 1 ? "vin" : "xu";
        CacheServiceImpl cacheService = new CacheServiceImpl();
        cacheService.setValue(room, (int) pot);
        this.betValue = betValue;
        this.initJackpotValues = initPotValue;
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.gameLoopTask, 10, 1, TimeUnit.SECONDS);
        this.boxValues.add(10);
        this.boxValues.add(10);
        this.boxValues.add(10);
        this.boxValues.add(15);
        this.boxValues.add(20);
        gn = gameName;
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
            Slot20ForceStopAutoPlayMsg msg = new Slot20ForceStopAutoPlayMsg(commandCollection.FORCE_AUTO_PLAY_MESSAGE);
            SlotUtils.sendMessageToUser(msg, user);
        }
    }

    public synchronized SLot20ResultMsg play(String username, String linesStr) {

        boolean forceJackpotByUser = false;

        long startTime = System.currentTimeMillis();

        String currentTimeStr = DateTimeUtils.getCurrentTime();
        long referenceId = this.module.getNewReferenceId();

        short result = 0;
        String[] lineArr = linesStr.split(",");
        long currentMoney = this.userService.getMoneyUserCache(username, this.moneyTypeStr);
        UserCacheModel u = this.userService.getUser(username);
        long totalBetValue = (long) lineArr.length * this.betValue;
        SLot20ResultMsg msg = new SLot20ResultMsg(commandCollection.RESULT_MESSAGE);
        CacheServiceImpl cacheService = new CacheServiceImpl();
        String userForce;
        try {
            userForce = cacheService.getValueStr(CACHE_NAME_USER_SPOT + this.gn);
        } catch (Exception e) {
            userForce = "";
        }
        if (lineArr.length > 0 && !linesStr.isEmpty()) {
            if (totalBetValue > 0L) {
                if (totalBetValue <= currentMoney) {
                    long fee = totalBetValue * 2L / 100L;
                    MoneyResponse moneyRes = new MoneyResponse(false, "1001");
                    if (!u.isBot()) {
                        moneyRes = this.userService.updateMoney(username, -totalBetValue, this.moneyTypeStr, this.gameName, "Quay " + gn, "Đăt cược " + gn, fee, referenceId, TransType.START_TRANS);
                    } else {
                        moneyRes.setSuccess(true);
                    }
                    if (moneyRes != null && moneyRes.isSuccess()) {
                        long moneyToPot = totalBetValue / 100L;
                        long moneyToFund = totalBetValue - fee - moneyToPot;
                        if (!u.isBot()) {
                            this.fund += moneyToFund;
                        }
                        this.pot += moneyToPot;
                        boolean enoughPair = false;
                        ArrayList<AwardsOnLine> awardsOnLines = new ArrayList<>();
                        long totalPrizes;
                        long soTienNoHuKhongTruQuy;
                        long tienThuongX2;
                        block4:
                        while (!enoughPair) {
                            int soLanNoHu;
                            result = 0;
                            awardsOnLines.clear();
                            totalPrizes = 0L;
                            soTienNoHuKhongTruQuy = 0L;
                            tienThuongX2 = 0L;
                            String linesWin;
                            String prizesOnLine;
                            String haiSao = "";
                            boolean forceNoHu = false;
//                            if (lineArr.length >= 5 && (soLanNoHu = ConfigGame.getIntValue("KhoBau_so_lan_no_hu")) > 0 && this.fund > this.initPotValue * 2L && (n = (rd = new Random()).nextInt(soLanNoHu)) == 0) {
//                                forceNoHu = true;
//                            }
                            if (betValue == 100) {
                                soLanNoHu = ConfigGame.getIntValue(this.gameName + "_so_lan_no_hu_100");
                                if (lineArr.length >= 15 && soLanNoHu > 0 && this.fund > this.pot * 2L && new Random().nextInt(soLanNoHu) == 0 && countNoHu >= soLanNoHu) {
                                    forceNoHu = true;
                                }
                                if (userForce.equals(username) && lineArr.length >= 15 && soLanNoHu > 0 && this.fund > this.pot * 2L && countNoHu >= soLanNoHu) {
                                    forceNoHu = true;
                                    forceJackpotByUser = true;
                                }

                            } else if (betValue == 1000) {
                                soLanNoHu = ConfigGame.getIntValue(this.gameName + "_so_lan_no_hu_1000");
                                if (lineArr.length >= 20 && soLanNoHu > 0 && this.fund > this.pot * 3L && new Random().nextInt(soLanNoHu) == 0 && countNoHu >= soLanNoHu) {
                                    forceNoHu = true;

                                }
                                //force user jackpot
                                if (userForce.equals(username) && lineArr.length >= 20 && soLanNoHu > 0 && this.fund > this.pot * 3L && countNoHu >= soLanNoHu) {
                                    forceNoHu = true;
                                    forceJackpotByUser = true;
                                }
                            } else {
                                soLanNoHu = ConfigGame.getIntValue(this.gameName + "_so_lan_no_hu_10000");
                                if (lineArr.length >= 20 && soLanNoHu > 0 && this.fund > this.pot * 3L && new Random().nextInt(soLanNoHu) == 0 && countNoHu >= soLanNoHu) {
                                    forceNoHu = true;
                                }
                                if (userForce.equals(username) && lineArr.length >= 20 && soLanNoHu > 0 && this.fund > this.pot * 3L && countNoHu >= soLanNoHu) {
                                    forceNoHu = true;
                                    forceJackpotByUser = true;
                                }
                            }
                            // check user deposit or received money from agency
                            if (forceNoHu) {
                                try {
                                    LogMoneyUserDao logService = new LogMoneyUserDaoImpl();
                                    long total_agency_receive = 0;
                                    long total_deposit_bank = 0;
                                    long total_deposit_momo = 0;

                                    AgentServiceImpl service = new AgentServiceImpl();
                                    List<AgentResponse> agents = service.listAgent();
                                    ArrayList<String> agentNames = new ArrayList<String>();
                                    if (agents != null && agents.size() > 0) {
                                        for (AgentResponse agent : agents) {
                                            agentNames.add(agent.nickName);
                                        }
                                    }
                                    List<LogUserMoneyResponse> resulReceive = logService.searchAllLogMoneyUser(username, "RECEIVE", false);
                                    if (resulReceive != null && resulReceive.size() > 0) {
                                        for (LogUserMoneyResponse trans : resulReceive) {
                                            boolean matchAgent = false;
                                            for (String s : agentNames) {
                                                if (trans.description.contains(s)) {
                                                    matchAgent = true;
                                                }
                                            }
                                            if (matchAgent) {
                                                total_agency_receive += trans.moneyExchange;
                                            }
                                        }
                                    }

                                    long total_recharge_card_money = 0;//total_agency_receive - userModel.getRechargeMoney();                
                                    List<LogUserMoneyResponse> resultCard = logService.searchAllLogMoneyUser(username, "CARD", false);
                                    if (resultCard != null && resultCard.size() > 0) {


                                        for (LogUserMoneyResponse trans : resultCard) {
                                            total_recharge_card_money += trans.moneyExchange;
                                        }
                                    }
                                    //search total deposit bank
                                    List<LogUserMoneyResponse> resultBank = logService.searchAllLogMoneyUser(username, "BANK", false);
                                    if (resultBank != null && resultBank.size() > 0) {

                                        for (LogUserMoneyResponse trans : resultBank) {
                                            total_deposit_bank += trans.moneyExchange;
                                        }
                                    }
                                    //search total deposit momo

                                    List<LogUserMoneyResponse> resultMomo = logService.searchAllLogMoneyUser(username, "MOMO", false);
                                    if (resultMomo != null && resultMomo.size() > 0) {

                                        for (LogUserMoneyResponse trans : resultMomo) {
                                            total_deposit_momo += trans.moneyExchange;
                                        }

                                    }

                                    if ((total_agency_receive == 0 && total_recharge_card_money == 0 && total_deposit_momo == 0 && total_deposit_bank == 0)
                                            || (total_agency_receive + total_recharge_card_money + total_deposit_momo + total_deposit_bank) < 500000) {
                                        forceNoHu = false;
                                    }


                                } catch (Exception ex) {
                                    Debug.trace(ex.getMessage());
                                    StringWriter sw = new StringWriter();
                                    PrintWriter pw = new PrintWriter(sw);
                                    ex.printStackTrace(pw);
                                    String sStackTrace = sw.toString(); // stack trace as a string
                                    Debug.trace(sStackTrace);
                                    forceNoHu = false;
                                }
                            }

                            Line20Item[][] matrix = forceNoHu ? Slot20Utils.generateMatrixNoHu(lineArr) : Slot20Utils.generateMatrix();
                            for (String entry2 : lineArr) {
                                ArrayList<Line20Award> awardList = new ArrayList<>();
                                Line20 line = Slot20Utils.getLine(this.lines, matrix, Integer.parseInt(entry2));
                                Slot20Utils.calculateLine(line, awardList);
                                for (Line20Award award : awardList) {
                                    long moneyOnLine = 0L;
                                    if (award.getRatio() > 0.0f) {
                                        moneyOnLine = (long) (award.getRatio() * (float) this.betValue);
                                    } else if (award == Line20Award.PENTA_POUCH) {
                                        for (AwardsOnLine e : awardsOnLines) {
                                            if (e.getAward() != Line20Award.PENTA_POUCH) continue;
                                            continue block4;
                                        }
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
                                        MiniGameSlotResponse response = this.generatePickStars();
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

                            StringBuilder builderLinesWin = new StringBuilder();
                            StringBuilder builderPrizesOnLine = new StringBuilder();
                            for (AwardsOnLine entry2 : awardsOnLines) {
//                                if ((entry2.getAward() == KhoBauAward.PENTA_POUCH || entry2.getAward() == KhoBauAward.QUADRA_POUCH || entry2.getAward() == KhoBauAward.TRIPLE_POUCH) && !u.isBot()) continue block4;
                                if ((entry2.getAward() == Line20Award.PENTA_POUCH || entry2.getAward() == Line20Award.QUADRA_POUCH || entry2.getAward() == Line20Award.TRIPLE_POUCH) && !forceNoHu)
                                    continue block4;

                                if (betValue == 100) {
                                    if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_100") == 0) // cho cả người v bot nổ hũ
                                    {
                                        if ((entry2.getAward() == Line20Award.PENTA_POUCH || entry2.getAward() == Line20Award.QUADRA_POUCH || entry2.getAward() == Line20Award.TRIPLE_POUCH))
                                            continue block4;
                                    } else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_100") == 1) // chỉ cho bot nổ hũ
                                    {
                                        if ((entry2.getAward() == Line20Award.PENTA_POUCH || entry2.getAward() == Line20Award.QUADRA_POUCH || entry2.getAward() == Line20Award.TRIPLE_POUCH) && !u.isBot())
                                            continue block4;
                                    } else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_100") == -1) // chỉ cho người nổ hũ
                                    {
                                        if ((entry2.getAward() == Line20Award.PENTA_POUCH || entry2.getAward() == Line20Award.QUADRA_POUCH || entry2.getAward() == Line20Award.TRIPLE_POUCH) && u.isBot())
                                            continue block4;
                                    } else {
                                        if ((entry2.getAward() == Line20Award.PENTA_POUCH || entry2.getAward() == Line20Award.QUADRA_POUCH || entry2.getAward() == Line20Award.TRIPLE_POUCH) && !u.isBot())
                                            continue block4;
                                    }
                                } else if (betValue == 1000) {
                                    if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_1000") == 0) // cho cả người và bot nổ hũ
                                    {
                                        if ((entry2.getAward() == Line20Award.PENTA_POUCH || entry2.getAward() == Line20Award.QUADRA_POUCH || entry2.getAward() == Line20Award.TRIPLE_POUCH))
                                            continue block4;
                                    } else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_1000") == 1) // chỉ cho bot nổ hũ
                                    {
                                        if ((entry2.getAward() == Line20Award.PENTA_POUCH || entry2.getAward() == Line20Award.QUADRA_POUCH || entry2.getAward() == Line20Award.TRIPLE_POUCH) && !u.isBot())
                                            continue block4;
                                    } else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_1000") == -1) // chỉ cho người nổ hũ
                                    {
                                        if ((entry2.getAward() == Line20Award.PENTA_POUCH || entry2.getAward() == Line20Award.QUADRA_POUCH || entry2.getAward() == Line20Award.TRIPLE_POUCH) && u.isBot())
                                            continue block4;
                                    } else {
                                        if ((entry2.getAward() == Line20Award.PENTA_POUCH || entry2.getAward() == Line20Award.QUADRA_POUCH || entry2.getAward() == Line20Award.TRIPLE_POUCH) && !u.isBot())
                                            continue block4;
                                    }
                                } else {
                                    if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_10000") == 0) // cho cả người và bot nổ hũ
                                    {
                                        if ((entry2.getAward() == Line20Award.PENTA_POUCH || entry2.getAward() == Line20Award.QUADRA_POUCH || entry2.getAward() == Line20Award.TRIPLE_POUCH))
                                            continue block4;
                                    } else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_10000") == 1) // chỉ cho bot nổ hũ
                                    {
                                        if ((entry2.getAward() == Line20Award.PENTA_POUCH || entry2.getAward() == Line20Award.QUADRA_POUCH || entry2.getAward() == Line20Award.TRIPLE_POUCH) && !u.isBot())
                                            continue block4;
                                    } else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_10000") == -1) // chỉ cho người nổ hũ
                                    {
                                        if ((entry2.getAward() == Line20Award.PENTA_POUCH || entry2.getAward() == Line20Award.QUADRA_POUCH || entry2.getAward() == Line20Award.TRIPLE_POUCH) && u.isBot())
                                            continue block4;
                                    } else {
                                        if ((entry2.getAward() == Line20Award.PENTA_POUCH || entry2.getAward() == Line20Award.QUADRA_POUCH || entry2.getAward() == Line20Award.TRIPLE_POUCH) && !u.isBot())
                                            continue block4;
                                    }
                                }
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
                            if (result == 3 ? this.fund - (totalPrizes - soTienNoHuKhongTruQuy) < 0L : this.fund - totalPrizes < this.pot * 2L && totalPrizes - totalBetValue >= 0L)
                                continue;
                            enoughPair = true;
                            String matrixStr = Slot20Utils.matrixToString(matrix);
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
                                    this.fund = 0;
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
                                        if (model.getClient() != null && !Objects.equals(model.getClient(), "")) {
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
                                        } catch (Exception e) {

                                        }
                                    }
                                    this.slotService.logNoHu(referenceId, this.gameName, displayName, this.betValue, linesStr, matrixStr, builderLinesWin.toString(), builderPrizesOnLine.toString(), totalPrizes, result, currentTimeStr);
                                } else {
                                    countNoHu++;
                                    if (!u.isBot()) {
                                        this.fund -= totalPrizes;
                                    }

                                    if (result == 0) {
                                        result = totalPrizes >= (this.betValue * 100L) ? (short) 2 : 1;
                                    }
                                }
                            }
                            long moneyExchange = totalPrizes - tienThuongX2;
                            if (tienThuongX2 > 0L && !u.isBot()) {
                                this.userService.updateMoney(username, tienThuongX2, this.moneyTypeStr, this.gameName, "Quay " + gn, "Thưởng Hũ X2", 0L, null, TransType.NO_VIPPOINT);
                            }
                            if (totalPrizes != 0 && !u.isBot()) {
                                if ((moneyRes = this.userService.updateMoney(username, totalPrizes, this.moneyTypeStr, this.gameName, "Quay " + gn, this.buildDescription(totalBetValue, totalPrizes, result), 0L, referenceId, TransType.END_TRANS)) != null && moneyRes.isSuccess()) {
                                    currentMoney = moneyRes.getCurrentMoney();
                                    if (this.moneyType == 1 && moneyExchange >= (long) BroadcastMessageServiceImpl.MIN_MONEY) {
                                        this.broadcastMsgService.putMessage(Games.findGameByName(gameName).getId(), username, moneyExchange - totalBetValue);
                                    }
                                }
                            }

                            linesWin = builderLinesWin.toString();
                            prizesOnLine = builderPrizesOnLine.toString();
                            msg.referenceId = referenceId;
                            msg.matrix = Slot20Utils.matrixToString(matrix);
                            msg.linesWin = linesWin;
                            msg.prize = totalPrizes;
                            msg.haiSao = haiSao;
                            try {
                                if (!u.isBot()) {
                                    slotLogListener.log(referenceId, username, this.betValue, linesStr, linesWin, prizesOnLine, result, totalPrizes, currentTimeStr);
                                }
                                if (result == 3 || result == 4) {
                                    this.slotService.addTop(gn, username, this.betValue, totalPrizes, currentTimeStr, result);
                                }
                                if (result == 3 || result == 2 || result == 4) {
                                    Slot20BigWinMsg bigWinMsg = new Slot20BigWinMsg(commandCollection.BIG_WIN_MESSAGE);
                                    bigWinMsg.username = username;
                                    bigWinMsg.type = (byte) result;
                                    bigWinMsg.betValue = (short) this.betValue;
                                    bigWinMsg.totalPrizes = totalPrizes;
                                    bigWinMsg.timestamp = DateTimeUtils.getCurrentTime();
                                    this.module.sendMsgToAllUsers(bigWinMsg);
                                }
                            } catch (InterruptedException | TimeoutException ignored) {
                            } catch (IOException bigWinMsg) {
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
        String ratioTime = CommonUtils.getRatioTime(handleTime);
        SlotUtils.logKhoBau(referenceId, username, this.betValue, msg.matrix, msg.haiSao, result, handleTime, ratioTime, currentTimeStr);
        return msg;
    }

    public synchronized SLot20ResultMsg playFreeDaily(String username) {
        String currentTimeStr = DateTimeUtils.getCurrentTime();
        long refernceId = this.module.getNewReferenceId();
        short result = 0;
        String[] lineArr = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20".split(",");
        long currentMoney = this.userService.getMoneyUserCache(username, this.moneyTypeStr);
        SLot20ResultMsg msg = new SLot20ResultMsg(commandCollection.RESULT_MESSAGE);
        boolean enoughPair = false;
        ArrayList<AwardsOnLine> awardsOnLines = new ArrayList<>();
        long totalPrizes;
        block4:
        while (!enoughPair) {
            awardsOnLines.clear();
            totalPrizes = 0L;
            String linesWin;
            String prizesOnLine;
            Line20Item[][] matrix = Slot20Utils.generateMatrix();
            for (String entry2 : lineArr) {
                ArrayList<Line20Award> awardList = new ArrayList<>();
                Line20 line = Slot20Utils.getLine(this.lines, matrix, Integer.parseInt(entry2));
                Slot20Utils.calculateLine(line, awardList);
                for (Line20Award award : awardList) {
                    long money;
                    if (award.getRatio() <= 0.0f) continue block4;
                    money = (long) (award.getRatio() * (float) this.betValue);
                    AwardsOnLine aol = new AwardsOnLine(award, money, line.getName());
                    awardsOnLines.add(aol);
                }
            }
            StringBuilder builderLinesWin = new StringBuilder();
            StringBuilder builderPrizesOnLine = new StringBuilder();
            for (AwardsOnLine entry2 : awardsOnLines) {
                if ((entry2.getAward() == Line20Award.PENTA_POUCH || entry2.getAward() == Line20Award.QUADRA_POUCH || entry2.getAward() == Line20Award.TRIPLE_POUCH))
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
            if (this.fund - totalPrizes < 0L || totalPrizes > (long) ConfigGame.getIntValue("max_prize_free_daily", 2000))
                continue;
            enoughPair = true;
            boolean updated = this.slotService.updateLuotQuayFreeDaily(this.gameName, username, this.betValue);
            if (!updated) {
                result = 103;
            } else {
                if (totalPrizes > 0L) {
                    MoneyResponse moneyRes = this.userService.updateMoney(username, totalPrizes, this.moneyTypeStr, this.gameName + "_Free", "Quay " + gn + " Free", "C\u01b0\u1ee3c: 0, Th\u1eafng: " + totalPrizes, 0L, null, TransType.NO_VIPPOINT);
                    if (moneyRes != null && moneyRes.isSuccess()) {
                        currentMoney = moneyRes.getCurrentMoney();
                    }
                }
            }
            linesWin = builderLinesWin.toString();
            prizesOnLine = builderPrizesOnLine.toString();
            msg.referenceId = refernceId;
            msg.matrix = Slot20Utils.matrixToString(matrix);
            msg.linesWin = linesWin;
            msg.prize = totalPrizes;
            msg.haiSao = "";
            try {
                if (!isBot(username)) {
                    slotLogListener.log(refernceId, username, this.betValue, "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20", linesWin, prizesOnLine, result, totalPrizes, currentTimeStr);
                }
            } catch (InterruptedException | TimeoutException | IOException ignored) {
            }
        }
        msg.result = (byte) result;
        msg.currentMoney = currentMoney;
        return msg;
    }

    public boolean isBot(String nickName) {
        try {
            UserCacheModel u = this.userService.getUser(nickName);
            return u.isBot();
        } catch (Exception e) {
            return true;
        }
    }

    private MiniGameSlotResponse generatePickStars() {
        MiniGameSlotResponse response = new MiniGameSlotResponse();
        int totalMoney = 0;
        ArrayList<PickStarGift> gifts = new ArrayList<PickStarGift>();
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

    public short play(User user, String linesStr) {
        String username = user.getName();
        int numFree = 0;
        if (user.getProperty("numFreeDaily") != null) {
            numFree = (Integer) user.getProperty("numFreeDaily");
        }
        SLot20ResultMsg msg;
        Slot20FreeDailyMsg freeDailyMsg = new Slot20FreeDailyMsg(commandCollection.FREE_DAILY_MESSAGE);
        if (numFree > 0) {
            msg = this.playFreeDaily(username);
            freeDailyMsg.remain = (byte) (--numFree);
            if (numFree > 0) {
                user.setProperty("numFreeDaily", numFree);
            } else {
                user.removeProperty("numFreeDaily");
            }
        } else {
            msg = this.play(username, linesStr);
        }
        if (this.isUserMinimize(user)) {
            Slot20MinimizeResultMsg miniMsg = new Slot20MinimizeResultMsg(commandCollection.MINIMIZE_RESULT_MESSAGE);
            miniMsg.prize = msg.prize;
            miniMsg.currentMoney = msg.currentMoney;
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
        if (currentTime - this.lastTimeUpdatePotToRoom >= 3000L) {
            this.lastTimeUpdatePotToRoom = currentTime;
            try {
                this.miniGameService.savePot(this.name, this.pot, this.huX2);
            } catch (IOException | InterruptedException | TimeoutException ex2) {
                Debug.trace(this.gameName + ": update pot poker error ", ex2.getMessage());
            }
            byte x2 = (byte) (this.huX2 ? 1 : 0);
            ((Slot20Module) this.module).updatePot(this.id, this.pot, x2);
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

