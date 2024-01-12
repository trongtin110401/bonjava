
package game.modules.slot.room;

import bitzero.server.BitZeroServer;
import bitzero.server.entities.User;
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
import com.vinplay.vbee.common.response.MoneyResponse;
import com.vinplay.vbee.common.statics.TransType;
import com.vinplay.vbee.common.utils.DateTimeUtils;
import game.modules.slot.BenleyModule;
import game.modules.slot.cmd.send.benley.*;
import game.modules.slot.entities.slot.AutoUser;
import game.modules.slot.entities.slot.AwardsOnLine;
import game.modules.slot.entities.slot.Line;
import game.modules.slot.entities.slot.MiniGameSlotResponse;
import game.modules.slot.entities.slot.avengers.AvengersAward;
import game.modules.slot.entities.slot.avengers.AvengersAwards;
import game.modules.slot.entities.slot.avengers.AvengersItem;
import game.modules.slot.entities.slot.avengers.AvengersLines;
import game.modules.slot.utils.AvengersUtils;
import game.modules.slot.utils.SlotUtils;
import game.util.ConfigGame;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Slot25BasicRoom extends SlotRoom {
    // Room Lifecycle
    private final Runnable gameLoopTask = new GameLoopTask();
    private final Runnable checkResetPotTask = new CheckResetPot();
    private final AvengersLines lines = new AvengersLines();
    private long lastTimeUpdatePotToRoom = 0L;
    private long lastTimeUpdateFundToRoom = 0L;
    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    private int countNoHu = 0;
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("slot");

    public Slot25BasicRoom(BenleyModule module, byte id, String gameName, short moneyType, long pot, long fund, int betValue, long initJackpotValue) {

        super(id, gameName, betValue, moneyType, pot, fund, initJackpotValue);

        this.module = module;
        this.moneyType = moneyType;
        this.gameName = Games.BENLEY.getName();
        this.cacheFreeName = this.gameName + betValue;

        // init jackpot value
        CacheServiceImpl cacheService = new CacheServiceImpl();
        cacheService.setValue(gameName, (int) pot);

        this.betValue = betValue;
        this.initJackpotValues = initJackpotValue;

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
            ForceStopAutoPlayBenleyMsg msg = new ForceStopAutoPlayBenleyMsg();
            SlotUtils.sendMessageToUser(msg, user);
        }
    }

    public ResultBenleyMsg play(String username, String linesStr) {
        long referenceId = this.module.getNewReferenceId();
        return this.playNormal(username, linesStr, referenceId);
    }

    public ResultBenleyMsg playNormal(String username, String linesStr, long referenceId) {

        short result = 0;
        String currentTimeStr = DateTimeUtils.getCurrentTime();
        ResultBenleyMsg resultBenleyMsg = new ResultBenleyMsg();

        String[] selectedLines = linesStr.split(",");
        long totalBetValue = (long) selectedLines.length * this.betValue;

        boolean forceJackpotToUser = false;

        // get force user jackpot
        CacheServiceImpl cacheService = new CacheServiceImpl();
        String userForce;
        String betValueCache;
        try {
            userForce = cacheService.getValueStr(CACHE_NAME_USER_SPOT + gameName);
            betValueCache = cacheService.getValueStr(CACHE_BET_VALUE_SLOT + gameName);
        } catch (Exception e) {
            userForce = "";
            betValueCache = "";
        }

        UserCacheModel u = userService.getUser(username);
        long currentMoney = userService.getMoneyUserCache(username, this.moneyTypeStr);

        // số lines được chọn > 0
        if (selectedLines.length > 0 && !linesStr.isEmpty()) {
            // check tiền đặt cược hợp lệ
            if (totalBetValue > 0L) {
                // check đủ tiền
                if (totalBetValue <= currentMoney) {
                    // trừ phế 2%
                    long fee = totalBetValue * 2L / 100L;
                    MoneyResponse moneyRes = new MoneyResponse(false, "1001");
                    // Không phải lả BOT => Cập nhật tiền
                    if (!u.isBot()) {
                        moneyRes = this.userService.updateMoney(username, -totalBetValue, this.moneyTypeStr, this.gameName, "Quay " + gameName, "Đặt cược " + gameName, fee, referenceId, TransType.START_TRANS);
                    } else {
                        moneyRes.setSuccess(true);
                    }
                    if (moneyRes != null && moneyRes.isSuccess()) {
                        // một phần trăm cho vào hũ JACKPOT
                        long moneyToPot = totalBetValue / 100L;
                        this.pot += moneyToPot;

                        // số tiền còn lại sau khi trừ phế và 1% POT cho vào quỹ thưởng
                        long moneyToFund = totalBetValue - fee - moneyToPot;
                        if (!u.isBot()) {
                            this.fund += moneyToFund;
                        }
                        // sử dụng để check matrix data
                        boolean enoughPair = false;

                        long totalPrizes;
                        long tienThuongX2;

                        int countScatter;
                        int countBonus;

                        MiniGameSlotResponse miniGameSlot;
                        ArrayList<AwardsOnLine<AvengersAward>> awardsOnLines = new ArrayList<>();
                        block4:
                        while (!enoughPair) {
                            int soLanNoHu;
                            result = 0;
                            awardsOnLines.clear();
                            totalPrizes = 0L;
                            tienThuongX2 = 0L;
                            String linesWin;
                            String prizesOnLine;
                            miniGameSlot = null;
                            countScatter = 0;
                            countBonus = 0;
                            boolean forceNoHu = false;
//                            if (lineArr.length >= 5 && (soLanNoHu = ConfigGame.getIntValue(String.valueOf(this.gameName) + "_so_lan_no_hu")) > 0 && this.fund > this.initPotValue * 2L && (n = (rd = new Random()).nextInt(soLanNoHu)) == 0) {
//                                forceNoHu = true;
//                            }
                            if (betValue == 100) {
                                soLanNoHu = ConfigGame.getIntValue(this.gameName + "_so_lan_no_hu_100");
//                                if (lineArr.length >= 20 && soLanNoHu > 0 && this.fund > this.pot * 2L && (n = (rd = new Random()).nextInt(soLanNoHu)) == 0 && countNoHu >= soLanNoHu) {
//                                    forceNoHu = true;
//                                }
//                                if (userForce.equals(username) && lineArr.length >= 20 && soLanNoHu > 0 && this.fund > this.pot * 2L  && countNoHu >= soLanNoHu) {
//                                    forceNoHu = true;
//                                    forceJackpotByUser = true;
//                                }
                                if (userForce.equals(username) && betValueCache.equals(String.valueOf(100))) {
                                    forceNoHu = true;
                                    forceJackpotToUser = true;
                                }
                            } else if (betValue == 1000) {
                                soLanNoHu = ConfigGame.getIntValue(this.gameName + "_so_lan_no_hu_1000");
//                                if (lineArr.length >= 25 && soLanNoHu > 0 && this.fund > this.pot * 3L && (n = (rd = new Random()).nextInt(soLanNoHu)) == 0 && countNoHu >= soLanNoHu) {
//                                    forceNoHu = true;
//                                }
//                                if (userForce.equals(username) && lineArr.length >= 25 && soLanNoHu > 0 && this.fund > this.pot * 3L  && countNoHu >= soLanNoHu) {
//                                    forceNoHu = true;
//                                    forceJackpotByUser = true;
//                                }
                                if (userForce.equals(username) && betValueCache.equals(String.valueOf(1000))) {
                                    forceNoHu = true;
                                    forceJackpotToUser = true;
                                }
                            } else {
                                soLanNoHu = ConfigGame.getIntValue(this.gameName + "_so_lan_no_hu_10000");
//                                if (lineArr.length >= 25 && soLanNoHu > 0 && this.fund > this.pot * 3L && (n = (rd = new Random()).nextInt(soLanNoHu)) == 0 && countNoHu >= soLanNoHu) {
//                                    forceNoHu = true;
//                                }
//                                if (userForce.equals(username) && lineArr.length >= 25 && soLanNoHu > 0 && this.fund > this.pot * 3L  && countNoHu >= soLanNoHu) {
//                                    forceNoHu = true;
//                                    forceJackpotByUser = true;
//                                }
                                if (userForce.equals(username) && betValueCache.equals(String.valueOf(10000))) {
                                    forceNoHu = true;
                                    forceJackpotToUser = true;
                                }
                            }
                            //logger.info(gn+" username: "+username + " forceNoHu:"+forceNoHu);
                            // kiem tra neu no hu
//                            if (forceNoHu)
//                            {
//                                try
//                                {
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
//                                    List<LogUserMoneyResponse> resulReceive = logService.searchAllLogMoneyUser(username, "RECEIVE",false);
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
//                                    long total_recharge_card_money = 0;//total_agency_receive - userModel.getRechargeMoney();
//                                    List<LogUserMoneyResponse> resultCard = logService.searchAllLogMoneyUser(username, "CARD",false);
//                                    if (resultCard != null && resultCard.size() > 0) {
//
//
//                                        for (LogUserMoneyResponse trans : resultCard) {
//                                            total_recharge_card_money += trans.moneyExchange;
//                                        }
//
//                                    }
//
//                                    long total_deposit_bank = 0;
//                                    long total_deposit_momo = 0;
//
//                                    //search total deposit bank
//                                    List<LogUserMoneyResponse> resultBank = logService.searchAllLogMoneyUser(username, "BANK",false);
//                                    if (resultBank != null && resultBank.size() > 0) {
//
//                                        for (LogUserMoneyResponse trans : resultBank) {
//                                            total_deposit_bank += trans.moneyExchange;
//                                        }
//                                    }
//                                    //search total deposit momo
//
//                                    List<LogUserMoneyResponse> resultMomo = logService.searchAllLogMoneyUser(username, "MOMO",false);
//                                    if (resultMomo != null && resultMomo.size() > 0) {
//
//                                        for (LogUserMoneyResponse trans : resultMomo) {
//                                            total_deposit_momo += trans.moneyExchange;
//                                        }
//
//                                    }
//
//                                    if ((total_agency_receive == 0 && total_recharge_card_money == 0 && total_deposit_momo == 0 && total_deposit_bank == 0)
//                                            || (total_agency_receive + total_recharge_card_money + total_deposit_momo + total_deposit_bank) < 1000000)
//                                    {
//                                        forceNoHu = false;
//                                    }
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

                            // sinh Matrix
                            AvengersItem[][] matrix = forceNoHu ? AvengersUtils.generateMatrixNoHu(selectedLines) : AvengersUtils.generateMatrix();

                            // Đếm số lượng BONUS và SCATTER
                            for (int i = 0; i < 3; ++i) {
                                // cột
                                for (int j = 0; j < 5; ++j) {
                                    if (matrix[i][j] == AvengersItem.SCATTER) {
                                        ++countScatter;
                                        continue;
                                    }
                                    if (matrix[i][j] == AvengersItem.BONUS) {
                                        ++countBonus;
                                        continue;
                                    }
                                }
                            }

                            // không cho phép xảy ra đồng thời cả bonus và free spin
                            if (countBonus >= 3 && countScatter >= 3) {
                                continue;
                            }

                            // sau khi Matrix được sinh
                            // trong trường hợp thỏa mãn BONUS VÀ SCATTER,
                            // ràng buộc thêm điều kiện để giảm tỷ lệ ăn BONUS và SCATTER xuống
                            // nếu không thỏa mãn điều kiện, tiếp tục vòng lặp để sinh lại Matrix
                            if (countBonus >= 3 || countScatter >= 3) {
                                Random rd2 = new Random();
                                int tiLeAn = selectedLines.length * 100 / 25;
                                int n2 = rd2.nextInt(100);
                                if (n2 >= tiLeAn)
                                    continue;
                            }

                            // Tính toán phần thưởng cho BONUS GAME
                            if (countBonus >= 3) {
                                miniGameSlot = AvengersUtils.addMiniGameSlot(this.betValue, countBonus);
                                AvengersAward award = AvengersAwards.getAward(AvengersItem.BONUS, countBonus);
                                AwardsOnLine<AvengersAward> aol = new AwardsOnLine<>(award, miniGameSlot.getTotalPrize(), "line0");
                                awardsOnLines.add(aol);
                                result = 5;
                            }

                            // MÃ LỆNH NÀY ÁP ỤNG CHO SLOT MACHINE 25LINE EXTENDS.
                            // Trường hợp 1 WHEEL có xuất hiện item WILD, toàn bộ WHEEL đó sẽ được thay thế bởi nó
                            // Trong trường hợp này (Slot Machine 25Line Basic thì không áp dụng)

                            /* AvengersItem[][] matrixWild = AvengersUtils.revertMatrix(matrix); */
                            AvengersItem[][] matrixWild = matrix;

                            // Duyệt toàn bộ Lines được chọn bởi người chơi để tính toán giải thưởng trên từng Line
                            for (String selectedLine : selectedLines) {
                                ArrayList<AvengersAward> awardList = new ArrayList<>();
                                Line line = AvengersUtils.getLine(this.lines, matrixWild, Integer.parseInt(selectedLine));
                                AvengersUtils.calculateAward(line, awardList);
                                for (AvengersAward award2 : awardList) {
                                    long moneyOnLine = 0L;
                                    if (award2.getRatio() > 0.0f) {
                                        moneyOnLine = (long) (award2.getRatio() * (float) this.betValue);
                                    } else if (award2 == AvengersAward.QUADAR_JACKPOT) {
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
                                    AwardsOnLine<AvengersAward> aol2 = new AwardsOnLine<>(award2, moneyOnLine, line.getName());
                                    awardsOnLines.add(aol2);
                                }
                            }

                            StringBuilder builderLinesWin = new StringBuilder();
                            StringBuilder builderPrizesOnLine = new StringBuilder();
                            for (AwardsOnLine entry2 : awardsOnLines) {
                                if ((entry2.getAward() == AvengersAward.PENTA_JACKPOT
                                        || entry2.getAward() == AvengersAward.QUADAR_JACKPOT
                                        || entry2.getAward() == AvengersAward.TRIPLE_JACKPOT)
                                        && !forceNoHu)
                                    continue block4;

//                                if (betValue == 100)
//                                {
//                                    if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_100") == 0) // cho cả người và bot nổ hũ
//                                    {
//                                        if ((entry2.getAward() == AvengersAward.PENTA_JACK_POT || entry2.getAward() == AvengersAward.QUADAR_JACK_POT || entry2.getAward() == AvengersAward.TRIPLE_JACK_POT)) continue block4;
//                                    }
//                                    else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_100") == 1) // chỉ cho bot nổ hũ
//                                    {
//                                        if ((entry2.getAward() == AvengersAward.PENTA_JACK_POT || entry2.getAward() == AvengersAward.QUADAR_JACK_POT || entry2.getAward() == AvengersAward.TRIPLE_JACK_POT) && !u.isBot()) continue block4;
//                                    }
//                                    else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_100") == -1) // chỉ cho người nổ hũ
//                                    {
//                                        if ((entry2.getAward() == AvengersAward.PENTA_JACK_POT || entry2.getAward() == AvengersAward.QUADAR_JACK_POT || entry2.getAward() == AvengersAward.TRIPLE_JACK_POT) && u.isBot()) continue block4;
//                                    }
//                                    else
//                                    {
//                                        if ((entry2.getAward() == AvengersAward.PENTA_JACK_POT || entry2.getAward() == AvengersAward.QUADAR_JACK_POT || entry2.getAward() == AvengersAward.TRIPLE_JACK_POT) && !u.isBot()) continue block4;
//                                    }
//                                }
//                                else if (betValue == 1000)
//                                {
//                                    if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_1000") == 0) // cho cả người và bot nổ hũ
//                                    {
//                                        if ((entry2.getAward() == AvengersAward.PENTA_JACK_POT || entry2.getAward() == AvengersAward.QUADAR_JACK_POT || entry2.getAward() == AvengersAward.TRIPLE_JACK_POT)) continue block4;
//                                    }
//                                    else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_1000") == 1) // chỉ cho bot nổ hũ
//                                    {
//                                        if ((entry2.getAward() == AvengersAward.PENTA_JACK_POT || entry2.getAward() == AvengersAward.QUADAR_JACK_POT || entry2.getAward() == AvengersAward.TRIPLE_JACK_POT) && !u.isBot()) continue block4;
//                                    }
//                                    else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_1000") == -1) // chỉ cho người nổ hũ
//                                    {
//                                        if ((entry2.getAward() == AvengersAward.PENTA_JACK_POT || entry2.getAward() == AvengersAward.QUADAR_JACK_POT || entry2.getAward() == AvengersAward.TRIPLE_JACK_POT) && u.isBot()) continue block4;
//                                    }
//                                    else
//                                    {
//                                        if ((entry2.getAward() == AvengersAward.PENTA_JACK_POT || entry2.getAward() == AvengersAward.QUADAR_JACK_POT || entry2.getAward() == AvengersAward.TRIPLE_JACK_POT) && !u.isBot()) continue block4;
//                                    }
//                                }
//                                else
//                                {
//                                    if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_10000") == 0) // cho cả người và bot nổ hũ
//                                    {
//                                        if ((entry2.getAward() == AvengersAward.PENTA_JACK_POT || entry2.getAward() == AvengersAward.QUADAR_JACK_POT || entry2.getAward() == AvengersAward.TRIPLE_JACK_POT)) continue block4;
//                                    }
//                                    else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_10000") == 1) // chỉ cho bot nổ hũ
//                                    {
//                                        if ((entry2.getAward() == AvengersAward.PENTA_JACK_POT || entry2.getAward() == AvengersAward.QUADAR_JACK_POT || entry2.getAward() == AvengersAward.TRIPLE_JACK_POT) && !u.isBot()) continue block4;
//                                    }
//                                    else if (ConfigGame.getIntValue(this.gameName + "_cho_bot_no_hu_10000") == -1) // chỉ cho người nổ hũ
//                                    {
//                                        if ((entry2.getAward() == AvengersAward.PENTA_JACK_POT || entry2.getAward() == AvengersAward.QUADAR_JACK_POT || entry2.getAward() == AvengersAward.TRIPLE_JACK_POT) && u.isBot()) continue block4;
//                                    }
//                                    else
//                                    {
//                                        if ((entry2.getAward() == AvengersAward.PENTA_JACK_POT || entry2.getAward() == AvengersAward.QUADAR_JACK_POT || entry2.getAward() == AvengersAward.TRIPLE_JACK_POT) && !u.isBot()) continue block4;
//                                    }
//                                }
                                // bot or not if (entry2.getAward() == AvengersAward.PENTA_JACK_POT && !u.isBot()) continue block4;
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
//                            if (result == 3 ? this.fund - (totalPrizes - soTienNoHuKhongTruQuy) < 0L : this.fund - totalPrizes < this.pot * 2L && totalPrizes - totalBetValue >= 0L) continue;

                            enoughPair = true;
                            String matrixStr = AvengersUtils.matrixToString(matrix);
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
                                            cacheService.removeKey(CACHE_NAME_USER_SPOT + gameName);
                                            cacheService.removeKey(CACHE_BET_VALUE_SLOT + gameName);
                                        } catch (Exception e) {
                                            logger.error(" Reset cache BenLey - Cung Hỷ Phát Tài error with : " + e.getMessage());
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
                            resultBenleyMsg.freeSpin = 0;//(byte)this.setFreeSpin(username, linesStr, countScatter);
                            long moneyExchange = totalPrizes - tienThuongX2;
                            //update when x2
                            // only save real user
                            if (tienThuongX2 > 0L && !u.isBot()) {
                                this.userService.updateMoney(username, tienThuongX2, this.moneyTypeStr, this.gameName, "Quay " + gameName, "Thưởng hũ X2", 0L, null, TransType.NO_VIPPOINT);
                            }
                            // only save real user
                            if (moneyExchange != 0 && !u.isBot()) {
                                moneyRes = this.userService.updateMoney(username, moneyExchange, this.moneyTypeStr, this.gameName, "Quay " + gameName, this.buildDescription(totalBetValue, totalPrizes, result), 0L, referenceId, TransType.END_TRANS);
                                if (moneyRes != null && moneyRes.isSuccess()) {
                                    currentMoney = moneyRes.getCurrentMoney();
                                    if (this.moneyType == 1 && moneyExchange - (long) this.betValue >= (long) BroadcastMessageServiceImpl.MIN_MONEY) {
                                        this.broadcastMsgService.putMessage(Games.BENLEY.getId(), username, moneyExchange - (long) this.betValue);
                                    }
                                }
                            }
                            linesWin = builderLinesWin.toString();
                            prizesOnLine = builderPrizesOnLine.toString();
                            resultBenleyMsg.referenceId = referenceId;
                            resultBenleyMsg.matrix = AvengersUtils.matrixToString(matrix);
                            resultBenleyMsg.linesWin = linesWin;
                            resultBenleyMsg.prize = totalPrizes;
                            resultBenleyMsg.isFreeSpin = false;
                            if (miniGameSlot != null) {
                                resultBenleyMsg.haiSao = miniGameSlot.getPrizes();
                            }
                            try {
                                if (!u.isBot()) {
                                    this.slotService.logBenley(referenceId, username, this.betValue, linesStr, linesWin, prizesOnLine, result, totalPrizes, currentTimeStr);
                                }
                                if (result == 3 || result == 4) {
                                    this.slotService.addTop(gameName, username, this.betValue, totalPrizes, currentTimeStr, result);
                                }
                                if (result == 3 || result == 2 || result == 4) {
                                    BigWinBenleyMsg bigWinMsg = new BigWinBenleyMsg();
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
        resultBenleyMsg.result = (byte) result;
        resultBenleyMsg.currentMoney = currentMoney;
        //Update cache tien hu
        cacheService.setValue(CACHE_JACK_POT_VALUE_SLOT + "_" + this.betValue + "_" + gameName, String.valueOf(this.pot));
        if (forceJackpotToUser) {
            this.sendNotifyNoHu(username, (byte) 1, resultBenleyMsg.prize, "BENLEY");
        }
        // SlotUtils.logAvengers(referenceId, username, this.betValue, msg.matrix, msg.haiSao, result, handleTime, ratioTime, currentTimeStr);
        return resultBenleyMsg;
    }

    private int setFreeSpin(String nickName, String lines, int countFreeSpin) {
        int soLuot = 0;
        switch (countFreeSpin) {
            case 3: {
                soLuot = 8;
                this.slotService.setLuotQuayFreeSlot(this.cacheFreeName, nickName, lines, soLuot, 1);
                break;
            }
            case 4: {
                soLuot = 8;
                this.slotService.setLuotQuayFreeSlot(this.cacheFreeName, nickName, lines, soLuot, 2);
                break;
            }
            case 5: {
                soLuot = 8;
                this.slotService.setLuotQuayFreeSlot(this.cacheFreeName, nickName, lines, soLuot, 3);
            }
        }
        return soLuot;
    }

    public short play(User user, String linesStr) throws Exception {
        String username = user.getName();
        ResultBenleyMsg msg;
        BenleyFreeDailyMsg freeDailyMsg = new BenleyFreeDailyMsg();
        freeDailyMsg.remain = 0;
        msg = this.play(username, linesStr);
        if (this.isUserMinimize(user)) {
            MinimizeResultBenleyMsg miniMsg = new MinimizeResultBenleyMsg();
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
        if (currentTime - this.lastTimeUpdatePotToRoom >= 3000L) {
            this.lastTimeUpdatePotToRoom = currentTime;
            try {
                this.miniGameService.savePot(this.name, this.pot, this.huX2);
            } catch (IOException | InterruptedException | TimeoutException e) {
                Debug.trace(this.gameName + ": update pot error ", e.getMessage());
            }
            UpdatePotBenleyMsg msg = new UpdatePotBenleyMsg();
            msg.value = this.pot;
            msg.x2 = (byte) (this.huX2 ? 1 : 0);
            this.sendMessageToRoom(msg);
        }
    }

    public void updatePot(User user) {
        UpdatePotBenleyMsg msg = new UpdatePotBenleyMsg();
        msg.value = this.pot;
        msg.x2 = (byte) (this.huX2 ? 1 : 0);
        SlotUtils.sendMessageToUser(msg, user);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void gameLoop() {
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
                Logger.getLogger(Slot25BasicRoom.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        users.clear();
    }

    @Override
    public boolean joinRoom(User user) {
        boolean result = super.joinRoom(user);
        SlotFreeDaily model = this.slotService.getLuotQuayFreeDaily(this.gameName, user.getName(), this.betValue);
        BenleyFreeDailyMsg freeDailyMsg = new BenleyFreeDailyMsg();
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

