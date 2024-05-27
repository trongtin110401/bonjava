/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  bitzero.server.entities.User
 *  bitzero.server.extensions.data.BaseMsg
 *  bitzero.util.common.business.Debug
 *  com.vinplay.dal.service.BauCuaService
 *  com.vinplay.dal.service.BroadcastMessageService
 *  com.vinplay.dal.service.MiniGameService
 *  com.vinplay.dal.service.impl.BauCuaServiceImpl
 *  com.vinplay.dal.service.impl.BroadcastMessageServiceImpl
 *  com.vinplay.dal.service.impl.MiniGameServiceImpl
 *  com.vinplay.usercore.service.UserService
 *  com.vinplay.usercore.service.impl.UserServiceImpl
 *  com.vinplay.vbee.common.enums.Games
 *  com.vinplay.vbee.common.models.minigame.baucua.ResultBauCua
 *  com.vinplay.vbee.common.models.minigame.baucua.TransactionBauCua
 *  com.vinplay.vbee.common.models.minigame.baucua.TransactionBauCuaDetail
 *  com.vinplay.vbee.common.response.MoneyResponse
 *  com.vinplay.vbee.common.statics.TransType
 *  com.vinplay.vbee.common.utils.CommonUtils
 */
package game.modules.minigame.room;

import bitzero.server.entities.User;
import bitzero.server.extensions.data.BaseMsg;
import bitzero.util.common.business.Debug;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinplay.dal.service.BauCuaService;
import com.vinplay.dal.service.BroadcastMessageService;
import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.MiniGameService;
import com.vinplay.dal.service.impl.BauCuaServiceImpl;
import com.vinplay.dal.service.impl.BroadcastMessageServiceImpl;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.dal.service.impl.MiniGameServiceImpl;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.exceptions.KeyNotFoundException;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.models.minigame.baucua.ResultBauCua;
import com.vinplay.vbee.common.models.minigame.baucua.TransactionBauCua;
import com.vinplay.vbee.common.models.minigame.baucua.TransactionBauCuaDetail;
import com.vinplay.vbee.common.response.BauCuaTo2.BauCuaUserInfomation;
import com.vinplay.vbee.common.response.BauCuaTo2.SetBauCuaKetqua;
import com.vinplay.vbee.common.response.MoneyResponse;
import com.vinplay.vbee.common.statics.TransType;
import com.vinplay.vbee.common.utils.CommonUtils;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import game.entities.PlayerInfo;
import game.modules.minigame.BauCuaModuleTo2;
import game.modules.minigame.cmd.send.baucua.*;
import game.modules.minigame.entities.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

public class MGRoomBauCuaTo2 extends MGRoom {
    private static final double RATE_NO_HU = 0.3;
    public byte id;
    public long fund;

    private long jackPot;
    private List<PotBauCua> pots = new ArrayList<PotBauCua>();
    private List<PotBauCua> potsUser = new ArrayList<PotBauCua>();
    private byte moneyType;
    private String moneyTypeStr;
    private long referenceId;
    private float tax = MinigameConstant.MINIGAME_TAX_VIN;
    private int minBetValue;
    private UserService userService = new UserServiceImpl();
    private BauCuaService bcService = new BauCuaServiceImpl();
    private MiniGameService mgService = new MiniGameServiceImpl();
    private CacheService cacheService = new CacheServiceImpl();
    private Map<String, TransactionBauCua> transactionsMap = new HashMap<String, TransactionBauCua>();
    private byte[] dices;
    private byte xPot;
    private byte xValue;
    private ResultBauCua resultBC = new ResultBauCua();
    private List<ResultBauCua> lichSuPhien = new ArrayList<ResultBauCua>();
    private List<BotBauCua> botBC = new ArrayList<BotBauCua>();
    private List<BotBauCua> OldBotBC = new ArrayList<BotBauCua>();
    private BroadcastMessageService broadcastMsgService = new BroadcastMessageServiceImpl();
    public static final String GAME_NAME = "BauCuaTo";
    private static final byte BETTING_SUCCESS = 1;
    private static final byte BETTING_FAIL = 100;
    private static final byte INVALID_BETTING_STATE = 101;
    private static final byte NOT_ENOUGH_MONEY = 102;
    private List<BauCuaRealtimeTransaction> transactionsMapRealtime = Collections.synchronizedList(new ArrayList<>());
    private List<BauCuaRealtimeTransaction> allTransactionsMapRealtime = Collections.synchronizedList(new ArrayList<>());
    private Map<String, UserRoomInfo> userRoomInfoList = new HashMap<>();
    private List<HuBauCuaWinTransaction> list50WinHu = new ArrayList<>();

    private Map<Integer, Long> mapReportBet = new HashMap<>();
    private Map<Integer, Long> mapBotReportBet = new HashMap<>();
    private Map<String, BauCuaUserInfomation> listBauCuaInformation = new HashMap<>();
    private ObjectMapper objectMapper = new ObjectMapper();
    private ReentrantLock lock = new ReentrantLock();

    public MGRoomBauCuaTo2(String name, int minBetValue, byte moneyType, byte id, long fund, long jackPot) {
        super(name);
        this.id = id;
        this.fund = fund;
        this.jackPot = jackPot;
        this.moneyType = moneyType;
        if (moneyType == 1) {
            this.moneyTypeStr = "vin";
        } else if (moneyType == 0) {
            this.moneyTypeStr = "xu";
        }
        this.minBetValue = minBetValue;
        if (this.moneyType == 0) {
            this.tax = MinigameConstant.MINIGAME_TAX_XU;
        }
        for (int i = 0; i < 6; ++i) {
            this.pots.add(new PotBauCua(i));
            this.potsUser.add(new PotBauCua(i));
            this.mapReportBet.put(i, 0L);
            this.mapBotReportBet.put(i, 0L);

        }
        cacheService.setObject("mapBotReportBet", mapBotReportBet);
        cacheService.setObject("mapReportBet", mapReportBet);

        this.lichSuPhien = this.bcService.getLichSuPhien(30, id);
        Debug.trace((Object) ("Lich su phien " + this.buildLichSuPhien()));
    }


    public void updateBauCuaInfoToUser(User user, byte countTime, byte remainTime, boolean bettingState, byte totalBetTime, boolean isBettingState) {
        BauCuaInfoMsg msg = new BauCuaInfoMsg();
        msg.referenceId = this.referenceId;
        msg.remainTime = remainTime;
        msg.potData = this.buildPotData();
        msg.betData = this.buildBetData(user.getName());
        msg.lichSuPhien = this.buildLichSuPhien();
        msg.bettingState = bettingState;
        msg.dice1 = this.resultBC.dices[0];
        msg.dice2 = this.resultBC.dices[1];
        msg.dice3 = this.resultBC.dices[2];
        msg.xPot = this.resultBC.xPot;
        msg.xValue = this.resultBC.xValue;
        msg.room = this.id;
        msg.userRoomInfoList = userRoomInfoList;
        msg.funds = this.jackPot;
        msg.isNohu = false;
        msg.allTransaction = allTransactionsMapRealtime;
        msg.totalTime = totalBetTime;
        if (isBettingState)
            msg.betTimeRemain = (byte) (20 - countTime);
        cacheService.setValue("BauCuareferenceId", (int) this.referenceId);
        this.sendMessageToUser((BaseMsg) msg, user);
    }

    public void startNewGame(long newReferenceId) {
        this.referenceId = newReferenceId;
        cacheService.setValue("BauCuareferenceId", (int) this.referenceId);
        for (int i = 0; i < 6; i++) {
            this.pots.get(i).renew();
            this.potsUser.get(i).renew();
            this.mapReportBet.put(i, 0L);
            this.mapBotReportBet.put(i, 0L);
        }
        cacheService.setObject("mapBotReportBet", mapBotReportBet);
        cacheService.setObject("mapReportBet", mapReportBet);
        transactionsMapRealtime.clear();
        allTransactionsMapRealtime.clear();
        this.transactionsMap.clear();
        this.resultBC = new ResultBauCua(this.referenceId, this.id, this.minBetValue);
        OldBotBC = this.botBC;
        this.botBC = BotBauCuaTo2.getBotBauCua(this.id);
        int i = 0;
        removeOldBot();
        for (BotBauCua botBauCua : botBC) {
            userRoomInfoList.put(botBauCua.getNickname(), new UserRoomInfo(botBauCua.getNickname(), botBauCua.getMoneyCurrent(), String.valueOf(i)));
            i++;
        }
        this.updateBauCuaInformation();
        this.NotifyUser();
        Debug.trace((Object) ("BOT BAU CUA ROOM " + this.id + ", size= " + this.botBC.size()));
    }

    private void removeOldBot() {
        for (BotBauCua botBauCua : OldBotBC) {
            userRoomInfoList.remove(botBauCua.getNickname());
        }
    }


    public void botBet(int time, boolean bettingState) {

        for (BotBauCua b : this.botBC) {
            if (b.getTimeBetting() != time) continue;

            this.bet(b.getNickname(), b.getBetStr(), bettingState);

            if (bettingState) {
                allTransactionsMapRealtime.add(new BauCuaRealtimeTransaction(b.getNickname(), b.getBetStr()));
                try {
                    lock.lock();
                    transactionsMapRealtime.add(new BauCuaRealtimeTransaction(b.getNickname(), b.getBetStr()));
                } finally {
                    lock.unlock();
                }

                // broadcast to clients
                updateBauCuaPerSecond((byte) time, bettingState, true);
            }
        }
    }

    public ResultBetBauCuaMsg bet(String username, String betStr, boolean bettingState) {
        long currentMoney;
        int result;
        ResultBetBauCuaMsg msg;
        block15:
        {
            long totalBetValue = 0L;
            result = 100;
            msg = new ResultBetBauCuaMsg();
            currentMoney = this.userService.getMoneyUserCache(username, this.moneyTypeStr);
            if (bettingState) {
                String[] arr = betStr.split(","); // lấy cửa bet
                if (arr.length == 6) {
                    long[] betValues = new long[6]; // lấy giá trị bet
                    try {
                        int i;
                        for (i = 0; i < 6; ++i) {
                            betValues[i] = Long.parseLong(arr[i]);
                            if (betValues[i] < 0L) {
                                throw new NumberFormatException();
                            }
                            totalBetValue += betValues[i];
                            if (betValues[i] > 0) {
                                msg.moneyBet = betValues[i];
                            }
                        }
                        if (totalBetValue <= 0L) break block15;
                        if (totalBetValue <= currentMoney) {
                            long fee = (long) ((float) totalBetValue * this.tax / 100.0f);
                            MoneyResponse response = new MoneyResponse(false, "1001");
                            if (!this.isBot(username)) {
                                response = this.userService.updateMoney(username, -totalBetValue, this.moneyTypeStr, GAME_NAME, "Bầu cua: Đặt cược", "Phiên " + this.referenceId, fee, this.referenceId, TransType.START_TRANS);

                            } else {
                                response.setSuccess(true);
                            }
                            if (!response.isSuccess()) break block15;

                            if (!this.transactionsMap.containsKey(username)) {
                                TransactionBauCua newTransaction = new TransactionBauCua();
                                newTransaction.username = username;
                                newTransaction.moneyType = this.moneyType;
                                newTransaction.referenceId = this.referenceId;
                                newTransaction.room = this.minBetValue;
                                newTransaction.betValues = betValues;
                                this.transactionsMap.put(username, newTransaction);
                            } else {
                                TransactionBauCua transaction = this.transactionsMap.get(username);
                                for (i = 0; i < 6; ++i) {
                                    long[] arrl = transaction.betValues;
                                    int n = i;
                                    arrl[n] = arrl[n] + betValues[i];
                                }
                                this.transactionsMap.put(username, transaction);
                            }

                            TransactionBauCuaDetail tranDetail = new TransactionBauCuaDetail();
                            tranDetail.username = username;
                            tranDetail.referenceId = this.referenceId;
                            tranDetail.moneyType = this.moneyType;
                            tranDetail.room = this.minBetValue;
                            tranDetail.betValues = betValues;
                            currentMoney = response.getCurrentMoney();
                            for (i = 0; i < 6; ++i) {
                                if (betValues[i] <= 0L) continue;
                                this.pots.get(i).bet(tranDetail.username, tranDetail.betValues[i]);
                                if (!isBot(tranDetail.username)) { // neu la nguoi thi day vao pot
                                    this.mapReportBet.put(i, (this.mapReportBet.get(i) + tranDetail.betValues[i]));
                                    this.potsUser.get(i).bet(tranDetail.username, tranDetail.betValues[i]);
                                } else {
                                    this.mapBotReportBet.put(i, (this.mapReportBet.get(i) + tranDetail.betValues[i]));
                                }

                                msg.potId = (byte) i;
                            }
                            cacheService.setObject("mapBotReportBet", mapBotReportBet);
                            cacheService.setObject("mapReportBet", mapReportBet);
                            try {

                                if (!isBot(username)) {
                                    this.bcService.saveTransactionBauCuaDetail(tranDetail);
                                }

                            } catch (IOException | InterruptedException | TimeoutException arrl) {
                                // empty catch block
                            }
                            result = 1;
                            break block15;
                        }
                        result = 102;
                    } catch (NumberFormatException e) {
                        Debug.trace((Object) ("Bet value: " + betStr + " incorrect: " + e.getMessage()));
                    }
                }

            } else {
                result = 101;
            }
        }
        try {
            if (!isBot(username)) {
                BauCuaUserInfomation bauCuaUserInfomation = listBauCuaInformation.get(username);
                long total = 0;
                for (int i = 0; i < transactionsMap.get(username).betValues.length; i++) {
                    total += transactionsMap.get(username).betValues[i];
                }
                bauCuaUserInfomation.setTotalBet(total);
                bauCuaUserInfomation.setTotalCurrentMoney(this.userService.getMoneyUserCache(username, this.moneyTypeStr));
                listBauCuaInformation.put(username, bauCuaUserInfomation);


            }
        } catch (Exception e) {

        }

        msg.result = (byte) result;
        msg.currentMoney = currentMoney;
        return msg;
    }

    public void bet(User user, String betStr, boolean bettingState) {
        ResultBetBauCuaMsg msg = this.bet(user.getName(), betStr, bettingState);
        try {
            lock.lock();
            transactionsMapRealtime.add(new BauCuaRealtimeTransaction(user.getName(), betStr));
        } finally {
            lock.unlock();
        }
        allTransactionsMapRealtime.add(new BauCuaRealtimeTransaction(user.getName(), betStr));
        this.sendMessageToUser((BaseMsg) msg, user);
        Collection<BauCuaUserInfomation> values = listBauCuaInformation.values();
        ArrayList<BauCuaUserInfomation> baucualist = new ArrayList<>(values);
        try {
            cacheService.setValue("baucualist", objectMapper.writeValueAsString(baucualist));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void finishGame() {
    }

    //todo : update thong tin ve be
    private void updateBauCuaInformation() {
        for (String name : listBauCuaInformation.keySet()) {
            BauCuaUserInfomation bauCuaUserInfomation = listBauCuaInformation.get(name);
            bauCuaUserInfomation.setTotalBet(0);
            bauCuaUserInfomation.setTotalCurrentMoney(this.userService.getMoneyUserCache(name, this.moneyTypeStr));
            listBauCuaInformation.put(name, bauCuaUserInfomation);
            Collection<BauCuaUserInfomation> values = listBauCuaInformation.values();
            ArrayList<BauCuaUserInfomation> baucualist = new ArrayList<>(values);

            try {
                cacheService.setValue("baucualist", objectMapper.writeValueAsString(baucualist));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    public void getMsgLichSuNoHu(User user) {
        HashMap<Integer, Integer> mapRate = new HashMap<>();
        mapRate.put(0, 0);
        mapRate.put(1, 0);
        mapRate.put(2, 0);
        mapRate.put(3, 0);
        mapRate.put(4, 0);
        mapRate.put(5, 0);
        for (HuBauCuaWinTransaction huBauCuaWinTransaction : list50WinHu) {
            mapRate.put(huBauCuaWinTransaction.getPotId(), mapRate.get(huBauCuaWinTransaction.getPotId()) + 1);
        }
        LichSuNoHuMsg msg = new LichSuNoHuMsg();
        msg.mapRate = mapRate;
        msg.list50WinHu = list50WinHu;
        this.sendMessageToUser(msg, user);
    }

    public void updateBauCuaPerSecond(byte remainTime, boolean bettingState, boolean realTimeBetProcess) { // todo : update mỗi giây
        UpdateBauCuaPerSecondMsg msg = new UpdateBauCuaPerSecondMsg();
        msg.potData = this.buildPotData();
        msg.remainTime = remainTime;
        msg.bettingState = bettingState;
        msg.listBet = new ArrayList<>();
        if (realTimeBetProcess) {
            msg.listBet.addAll(transactionsMapRealtime);
            try {
                lock.lock();
                transactionsMapRealtime.clear();
            } finally {
                lock.unlock();
            }
        }
        this.sendMessageToRoom(msg);
    }

    public void storeTimeToCache(byte remainTime, boolean bettingState) {
        cacheService.setValue("BauCuaRemainTime", String.valueOf(remainTime));
        cacheService.setObject("bettingStateBauCua", bettingState);
    }

    public long calculatePrizes() {
        boolean isNohu = false;
        transactionsMapRealtime.clear();
        allTransactionsMapRealtime.clear();
        List<WinUser> listWin = new ArrayList<>();
        List<UserWinHuBauCua> userWinHuBauCuaList = new ArrayList<>();
        int potIdNohu = -1;
        int[] tiLe = this.calculateTiLe(this.dices);

        long totalVinPay = 0L;
        long totalPrizesUser = 0L;
        long totalUserBetInRoom = 0L;
        long[] totalBetValuesInRoom = new long[6];
        long[] totalPrizesInRoom = new long[6];
        for (TransactionBauCua tran : this.transactionsMap.values()) {
            long totalPrize = 0L;
            long totalHuPrize = 0L;
            long totalBetValues = 0L;
            for (int i = 0; i < 6; ++i) { // lấy 6 cửa
                int n;
                long[] arrl;
                if (tiLe[i] > 0) {
                    if (tiLe[i] == 3 && pots.get(i).getTotalValue() > 0) { // noo hu
                        isNohu = true;
                        potIdNohu = i;
                        long bet = tran.betValues[i];
                        long win = bet * tiLe[i];
                        long fee = (long) (win * (tax / 100));
                        long prize = (long) (bet * ((double) (this.jackPot) / pots.get(i).getTotalValue()) + (win - fee));
                        totalPrize += prize;
                        tran.prizes[i] = prize;

                        totalHuPrize += (long) (bet * ((double) (this.jackPot) / pots.get(i).getTotalValue()));

                        arrl = totalPrizesInRoom;
                        n = i;
                        arrl[n] = arrl[n] + (tran.betValues[i] * ((this.jackPot) / pots.get(i).getTotalValue())) + (tran.betValues[i] * (long) tiLe[i] + tran.betValues[i]);
                    } else {
                        long bet = tran.betValues[i];
                        long win = bet * tiLe[i];
                        long fee = (long) (win * (tax / 100.0f));
                        long prize = (win + bet - fee);
                        totalPrize += prize;
                        tran.prizes[i] = prize;

                        totalHuPrize += (bet * tiLe[i]) + bet;

                        arrl = totalPrizesInRoom;
                        n = i;
                        arrl[n] = arrl[n] + (tran.betValues[i] * (long) tiLe[i] + tran.betValues[i]);
                    }
                }

                totalBetValues += tran.betValues[i];
                this.jackPot += totalBetValues / 100;
                arrl = totalBetValuesInRoom;
                n = i;
                arrl[n] = arrl[n] + tran.betValues[i];

            }
            if (totalPrize > 0L) {
                MoneyResponse response;
                if (this.moneyType == 1) {
                    totalVinPay += totalPrize;
                }
                if (!isBot(tran.username) && (response = this.userService.updateMoney(tran.username, totalPrize, this.moneyTypeStr, GAME_NAME, "Bầu cua: Trận thắng", "Phiên " + this.referenceId, 0L, this.referenceId, TransType.END_TRANS)) != null && response.isSuccess()) {
                    UpdateBauCuaPrizeMsg msg = new UpdateBauCuaPrizeMsg();
                    msg.prize = totalPrize;
                    msg.pizeNohu = totalHuPrize;
                    msg.currentMoney = response.getCurrentMoney();
                    msg.room = this.id;

                    this.sendMessageToUser((BaseMsg) msg, tran.username);

                    if (this.moneyType == 1 && totalPrize >= (long) BroadcastMessageServiceImpl.MIN_MONEY) {
                        this.broadcastMsgService.putMessage(Games.BAU_CUA.getId(), tran.username, totalPrize);
                    }
                }
                listWin.add(new WinUser(tran.username, totalPrize, this.userService.getCurrentMoneyUserCache(tran.username, "vin")));
            }
            try {
                tran.totalExchange = totalPrize - totalBetValues;
                tran.dices = CommonUtils.arrayByteToString(this.resultBC.dices);
                this.bcService.saveTransactionBauCua(tran);

            } catch (IOException | InterruptedException | TimeoutException response) {
                // empty catch block
            }
            if (isNohu && totalPrize > 0) {
                System.out.println(totalHuPrize);
                userWinHuBauCuaList.add(new UserWinHuBauCua(Base64.getEncoder().encodeToString((tran.username.getBytes())), totalHuPrize));
            }

            //if (!isBot(tran.username)) { // check user is bot or not to calculate profit of session
            totalPrizesUser += totalPrize; // calculate total prizes in current room
            totalUserBetInRoom += totalBetValues; // calculate total bet value
//            } else {
//                totalBotBetInRoom += totalBetValues;
//                totalPrizesBot += totalPrize;
//            }

            if (!isBot(tran.username)) {
                fund -= tran.totalExchange;
            }
        }

        // update fund BauCua

        if (isNohu) {
            list50WinHu.add(new HuBauCuaWinTransaction(this.referenceId, Base64.getEncoder().encodeToString(VinPlayUtils.getCurrentDateTime().getBytes()), (potIdNohu), this.jackPot, userWinHuBauCuaList));
            if (list50WinHu.size() >= 50) {
                list50WinHu.remove(0);
            }
            this.jackPot -= (totalPrizesUser); // tru di tong giai trong game
            if (this.jackPot < 500000) {
                this.jackPot = 500000;
            }

        }

//        else {
//            long profit = totalUserBetInRoom - totalPrizesUser;
//            if (profit > 0) { // nếu là bot thì không tính vào hũ
//                this.jackPot += profit * RATE_NO_HU;
//            }
//        }


        try {
            long updateFund = 0;
            try {
                updateFund = Long.parseLong(cacheService.getValueStr("update_fund_bau_cua_to"));
            } catch (Exception e) {
                updateFund = 0;
            }
            this.fund += updateFund;
            this.mgService.saveFund(this.name, this.fund);
            this.mgService.savePot(this.name, jackPot, false);
        } catch (IOException | InterruptedException | TimeoutException response) {
        }

        this.resultBC.totalBetValues = totalBetValuesInRoom;
        this.resultBC.totalPrizes = totalPrizesInRoom;
        try {
            this.bcService.saveResultBauCua(this.resultBC);
            if (this.moneyType == 1) {
                ArrayList<TransactionBauCua> list = new ArrayList<TransactionBauCua>(this.transactionsMap.values());
                Debug.trace((Object) ("TRANSACTION BAU CUA: " + list.size()));
                this.bcService.calculteToiChonCa(this.dices, list);
            }
        } catch (IOException | InterruptedException | TimeoutException list) {
            // empty catch block
        }
        cacheService.setValue("Hu_Bau_cua_to2" + this.id, (int) this.jackPot);
        UpdateBauCuaWinEffect msg = new UpdateBauCuaWinEffect();
        msg.listWinUser = listWin;
        msg.funds = this.jackPot;
        msg.isNohu = isNohu;
        this.sendMessageToRoom(msg);
        return totalVinPay;
    }

    private String getDateTime() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        //  now.plusHours(7);
        System.out.println("Before Formatting: " + now);
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formatDateTime = now.format(format);
        return formatDateTime;
    }


    public void NotifyUser() {
        BauCuaUpdateListUser msg = new BauCuaUpdateListUser();
        msg.userRoomInfoList = userRoomInfoList;
        this.sendMessageToRoom(msg);

    }

    public long tryCalculatePrizes(int[] tiLe) {
        long totalValues = 0L;
        for (TransactionBauCua tran : this.transactionsMap.values()) {
            for (int i = 0; i < 6; ++i) {
                if (!this.isBot(tran.username)) {
                    totalValues += tran.betValues[i] * (long) tiLe[i] + tran.betValues[i];
                }
            }
        }
        return totalValues;
    }

    public String buildPotData() {
        StringBuilder builder = new StringBuilder();
        for (PotBauCua pot : this.pots) {
            builder.append(pot.getTotalValue());
            builder.append(",");
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    public String buildBetData(String username) {
        StringBuilder builder = new StringBuilder();
        for (PotBauCua pot : this.pots) {
            builder.append(pot.getTotalBetByUsername(username));
            builder.append(",");
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    @Override
    public boolean joinRoom(User user) {
        boolean result = super.joinRoom(user);
        if (result) {
            user.setProperty((Object) "MGROOM_BAU_CUA_TO2_INFO", (Object) this);
        }
        PlayerInfo pInfo = (PlayerInfo) user.getProperty((Object) "PLAYER_INFO");
        if (pInfo == null) {
            pInfo = new PlayerInfo();
            pInfo.userId = user.getId();
            pInfo.nickName = user.getName();
            int avatar = new Random().nextInt(12);
            pInfo.avatarUrl = String.valueOf(avatar);
            user.setProperty((Object) "PLAYER_INFO", (Object) pInfo);
        }
        userRoomInfoList.put(user.getName(), new UserRoomInfo(user.getName(), this.userService.getCurrentMoneyUserCache(user.getName(), "vin"), pInfo.avatarUrl));
        int i = 0;
        for (BotBauCua botBauCua : botBC) {
            userRoomInfoList.put(botBauCua.getNickname(), new UserRoomInfo(botBauCua.getNickname(), botBauCua.getMoneyCurrent(), String.valueOf(i)));
            i++;
        }
        NotifyUser();
        listBauCuaInformation.put(user.getName(), new BauCuaUserInfomation(user.getName(), 0, this.userService.getMoneyUserCache(user.getName(), this.moneyTypeStr)));
        Collection<BauCuaUserInfomation> values = listBauCuaInformation.values();
        ArrayList<BauCuaUserInfomation> baucualist = new ArrayList<>(values);
        try {
            cacheService.setValue("baucualist", objectMapper.writeValueAsString(baucualist));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void removeUser(User user) {
        this.getUsers();
        PlayerInfo pInfo = (PlayerInfo) user.getProperty((Object) "PLAYER_INFO");
        if (pInfo == null) {
            pInfo = new PlayerInfo();
            pInfo.userId = user.getId();
            pInfo.nickName = user.getName();
            int avatar = new Random().nextInt(12);
            pInfo.avatarUrl = String.valueOf(avatar);
            user.setProperty((Object) "PLAYER_INFO", (Object) pInfo);
        }
        listBauCuaInformation.remove(user.getName());
        Collection<BauCuaUserInfomation> values = listBauCuaInformation.values();
        ArrayList<BauCuaUserInfomation> baucualist = new ArrayList<>(values);

        try {
            cacheService.setValue("baucualist", objectMapper.writeValueAsString(baucualist));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        userRoomInfoList.remove(user.getName());


    }

    public float getTax() {
        return this.tax;
    }

    public byte getMoneyType() {
        return this.moneyType;
    }

    //    private byte[] generateDices() {
//        byte[] dices;
//        block1:
//        {
//            int num = 0;
//            do {
//                Random rd = new Random();
//                dices = new byte[]{(byte) rd.nextInt(6), (byte) rd.nextInt(6), (byte) rd.nextInt(6)};
//                this.xPot = 1;
//                this.xValue = 1;
//                int[] tiLe = this.calculateTiLe(dices);
//                if (checkNohu(tiLe)) {
//                    return generateDices();
//                }
//                long totalPrizes = this.tryCalculatePrizes(tiLe);
//                if (this.jackPot - totalPrizes > 0L) break block1;
//            } while (++num <= 3);
//            return this.traGiaiBeNhat();
//        }
//        return dices;
//    }
    private byte[] generateDices() {
        byte[] dices;
        int num = 0;
        do {
            Random rd = new Random();
            dices = new byte[]{(byte) rd.nextInt(6), (byte) rd.nextInt(6), (byte) rd.nextInt(6)};
            this.xPot = 1;
            this.xValue = 1;
            int[] tiLe = this.calculateTiLe(dices);
            if (checkNohu(tiLe)) {
                return generateDices();
            }
            long totalPrizes = this.tryCalculatePrizes(tiLe);
            if (this.fund - totalPrizes > 0L) {
                return dices;
            }
        } while (++num <= 3);
        return this.traGiaiBeNhat();
    }

    boolean checkNohu(int[] tile) {
        for (int i = 0; i < tile.length; i++) {
            if (tile[i] >= 3) {
                return true;
            }
        }
        return false;
    }

    private byte[] traGiaiBeNhat() {
        byte[] dices = new byte[3];
        ArrayList<Pot> potsTmp = new ArrayList<Pot>();
        ArrayList<Integer> indexPot = new ArrayList<Integer>();
        for (int j = 0; j < this.potsUser.size(); ++j) {
            Pot p = this.potsUser.get(j);
            boolean added = false;
            for (int i = 0; i < potsTmp.size(); ++i) {
                Random rd;
                int n;
                Pot pt = (Pot) potsTmp.get(i);
                if (p.getTotalValue() < pt.getTotalValue()) {
                    potsTmp.add(i, p);
                    indexPot.add(i, j);
                    added = true;
                    break;
                }
                if (p.getTotalValue() != pt.getTotalValue() || (n = (rd = new Random()).nextInt(2)) != 0) continue;
                potsTmp.add(i, p);
                indexPot.add(i, j);
                added = true;
                break;
            }
            if (added) continue;
            potsTmp.add(p);
            indexPot.add(j);
        }
        for (int i = 0; i < 3; ++i) {
            dices[i] = ((Integer) indexPot.get(i)).byteValue();
        }
        this.xPot = 1;
        this.xValue = 1;
        return dices;
    }

    private int[] calculateTiLe(byte[] dices) {
        int[] tiLe = new int[6];
        for (int i = 0; i < 6; ++i) {
            tiLe[i] = 0;
            for (int j = 0; j < dices.length; ++j) {
                if (i != dices[j]) continue;
                int[] arrn = tiLe;
                int n = i;
                arrn[n] = arrn[n] + 1;
            }
            if (tiLe[i] <= 0 || i != this.xPot) continue;
            int[] arrn = tiLe;
            int n = i;
            arrn[n] = arrn[n] * this.xValue;
        }
        return tiLe;
    }

    public void generateResult() {
        SetBauCuaKetqua setBauCuaKetqua = null;
        this.dices = this.generateDices();
        try {
            setBauCuaKetqua = (SetBauCuaKetqua) cacheService.getObject("setBauCuaKetqua");
        } catch (KeyNotFoundException e) {

        }
        if (setBauCuaKetqua != null) {
            if (setBauCuaKetqua.getStatus().equals("be")) {
                this.dices = setBauCuaKetqua.getListDices();
                cacheService.setObject("setBauCuaKetqua", new SetBauCuaKetqua("auto", new byte[]{}));
            }
        }

        //this.dices = new byte[]{1, 1, 1};
        this.resultBC.referenceId = this.referenceId;
        this.resultBC.dices = this.dices;
        this.resultBC.xPot = this.xPot;
        this.resultBC.xValue = this.xValue;
        Debug.trace((Object) ("BAU CUA " + this.id + " DICES: " + this.dices[0] + "," + this.dices[1] + "," + this.dices[2] + ", xPot= " + this.xPot + ", xValue= " + this.xValue));
        UpdateBauCuaResultMsg msg = new UpdateBauCuaResultMsg();
        msg.dice1 = this.dices[0];
        msg.dice2 = this.dices[1];
        msg.dice3 = this.dices[2];
        msg.xPot = this.xPot;
        msg.xValue = this.xValue;
        this.sendMessageToRoom(msg);
        this.lichSuPhien.add(this.resultBC);
        if (this.lichSuPhien.size() > 30) {
            this.lichSuPhien.remove(0);
        }
    }

    private byte randomXPot() {
        Random rd = new Random();
        return (byte) rd.nextInt(6);
    }

    private byte randomXValue() {
        Random rd = new Random();
        int n = rd.nextInt(20);
        if (n == 0) {
            return 3;
        }
        if (1 <= n && n <= 7) {
            return 2;
        }
        return 1;
    }

    public boolean isBot(String username) {
        UserCacheModel model = this.userService.getUser(username);
        return model.isBot();
    }

    public void getLichSuPhien(User user) {
        BauCuaLichSuPhienMsg msg = new BauCuaLichSuPhienMsg();
        msg.data = this.buildLichSuPhien();
        this.sendMessageToUser((BaseMsg) msg, user);
    }

    private String buildLichSuPhien() {
        StringBuilder builder = new StringBuilder();
        for (ResultBauCua entry : this.lichSuPhien) {
            builder.append(entry.dices[0]);
            builder.append(",");
            builder.append(entry.dices[1]);
            builder.append(",");
            builder.append(entry.dices[2]);
            builder.append(",");
            builder.append(entry.xPot);
            builder.append(",");
            builder.append(entry.xValue);
            builder.append(",");
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }
}

