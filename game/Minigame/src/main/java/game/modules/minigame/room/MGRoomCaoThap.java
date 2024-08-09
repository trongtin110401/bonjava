/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  bitzero.server.BitZeroServer
 *  bitzero.server.entities.User
 *  bitzero.server.extensions.data.BaseMsg
 *  bitzero.server.util.TaskScheduler
 *  bitzero.util.common.business.Debug
 *  com.vinplay.cardlib.models.Card
 *  com.vinplay.cardlib.models.Deck
 *  com.vinplay.cardlib.models.Rank
 *  com.vinplay.dal.service.BroadcastMessageService
 *  com.vinplay.dal.service.CaoThapService
 *  com.vinplay.dal.service.impl.BroadcastMessageServiceImpl
 *  com.vinplay.dal.service.impl.CaoThapServiceImpl
 *  com.vinplay.usercore.service.UserService
 *  com.vinplay.usercore.service.impl.UserServiceImpl
 *  com.vinplay.vbee.common.enums.Games
 *  com.vinplay.vbee.common.response.MoneyResponse
 *  com.vinplay.vbee.common.statics.TransType
 */
package game.modules.minigame.room;

import bitzero.server.BitZeroServer;
import bitzero.server.entities.User;
import bitzero.util.common.business.Debug;
import com.vinplay.cardlib.models.Card;
import com.vinplay.cardlib.models.Deck;
import com.vinplay.cardlib.models.Rank;
import com.vinplay.dal.service.BroadcastMessageService;
import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.CaoThapService;
import com.vinplay.dal.service.impl.BroadcastMessageServiceImpl;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.dal.service.impl.CaoThapServiceImpl;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.response.MoneyResponse;
import com.vinplay.vbee.common.statics.TransType;
import game.modules.minigame.cmd.send.caothap.*;
import game.modules.minigame.entities.CaoThapInfo;
import game.modules.minigame.entities.MinigameConstant;
import game.modules.minigame.utils.CaoThapUtils;
import game.utils.GameUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MGRoomCaoThap extends MGRoom {
    private long pot;
    private float tax = MinigameConstant.MINIGAME_TAX_VIN;
    private byte moneyType;
    private String moneyTypeStr;
    private int baseBetValue = 0;
    private UserService userService = new UserServiceImpl();
    private final Runnable gameLoopTask = new GameLoopTask();
    private final Runnable timeLoopTask = new TimeLoopTask();
    private final Map<String, CaoThapInfo> usersCaoThap;
    private CaoThapService ctService = new CaoThapServiceImpl();
    private BroadcastMessageService broadcastMsgService = new BroadcastMessageServiceImpl();

    protected CacheService sv = new CacheServiceImpl();

    public MGRoomCaoThap(String roomName, byte moneyType, long pot, long fund, int baseBetValue) {
        super(Games.CAO_THAP.getName(), roomName, baseBetValue, fund, moneyType);
        this.moneyType = moneyType;
        if (moneyType == 1) {
            this.moneyTypeStr = "vin";
            this.tax = MinigameConstant.MINIGAME_TAX_VIN;
        } else if (moneyType == 0) {
            this.moneyTypeStr = "xu";
            this.tax = MinigameConstant.MINIGAME_TAX_XU;
        }
        this.pot = pot;
        cacheService.setValue(CACHE_JACK_POT_VALUE_SLOT + "_" + this.betValue + "_" + gameName, pot);

        this.baseBetValue = baseBetValue;
        this.usersCaoThap = new HashMap<>();


        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.gameLoopTask, 10, 1, TimeUnit.SECONDS);
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.timeLoopTask, 10, 2, TimeUnit.SECONDS);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void startPlay(User user, int betValue, long referenceId) {
        StartPlayCaoThapMsg msg = new StartPlayCaoThapMsg();
        synchronized (this.usersCaoThap) {
            if (!this.usersCaoThap.containsKey(user.getName())) {
                long moneyUse = this.userService.getMoneyUserCache(user.getName(), this.moneyTypeStr);
                if (moneyUse >= (long) betValue) {
                    Deck deck = new Deck();
                    deck.shuffle();
                    Card card;

                    do {
                        card = CaoThapUtils.randomWithoutA(deck);
                    } while (card.getRank() != Rank.Ace && card.getRank() != Rank.King);

                    byte numA = 0;
                    if (card.getRank() == Rank.Ace) {
                        numA = 1;
                    }
                    long fee = Math.round((float) betValue * this.tax / 100.0f);
                    long moneyToFund = (long) betValue - fee;
                    MoneyResponse moneyResponse = new MoneyResponse(false, "1001");
                    if (!isBot(user.getName())) {
                        moneyResponse = this.userService.updateMoney(user.getName(), -betValue, this.moneyTypeStr, "CaoThap", "Cao thấp: Đặt cược", "Phiên: " + referenceId + ", Bước: 1", fee, referenceId, TransType.START_TRANS);
                    } else {
                        moneyResponse.setSuccess(true);
                    }
                    if (moneyResponse != null && moneyResponse.isSuccess()) {
                        updateFunValue(moneyToFund);
                        this.saveFund();
                        List<Double> ratioLst = CaoThapUtils.getRatio(deck, card);
                        msg.money1 = Math.round((double) betValue * ratioLst.get(1));
                        msg.money2 = betValue;
                        msg.money3 = Math.round((double) betValue * ratioLst.get(0));
                        msg.card = (byte) card.getCode();
                        msg.currentMoney = moneyResponse.getCurrentMoney();
                        msg.referenceId = referenceId;
                        ArrayList<Card> carryCards = new ArrayList<>();
                        carryCards.add(card);
                        CaoThapInfo info = new CaoThapInfo(user, referenceId, deck, card, (short) 1, (short) 10, betValue, numA, carryCards, msg.money1, msg.money3, user.getId());
                        this.usersCaoThap.put(user.getName(), info);
                        try {
                            if (!this.isBot(user.getName())) {
                                this.ctService.logCaoThap(referenceId, user.getName(), betValue, (short) 0, -betValue, card.toString(), this.pot, getFunValue(), this.moneyType, (short) 0, 1);
                            }
                        } catch (Exception e) {
                            Debug.trace("CAO THAP: log cao thap error ", e.getMessage());
                        }
                    } else {
                        msg.Error = ResultCaoThap.LOI_HE_THONG;
                    }
                } else {
                    msg.Error = ResultCaoThap.NOT_ENOUGH_MONEY;
                }
            } else {
                msg.Error = ResultCaoThap.USER_PLAYING;
            }
        }
        this.sendMessageToUser(msg, user);
    }

    public static final int TREN = 1;
    public static final int DUOI = 0;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void play(User user, byte choose) {
        ResultCaoThapMsg msg = new ResultCaoThapMsg();
        if (this.usersCaoThap.containsKey(user.getName())) {
            long currentMoney = this.userService.getCurrentMoneyUserCache(user.getName(), this.moneyTypeStr);
            CaoThapInfo info = this.usersCaoThap.get(user.getName());
            info.setTime((short) 10);
            info.setStep((short) (info.getStep() + 1));
            if (!((info.getMoneyUp() == 0L && choose == TREN) || (info.getMoneyDown() == 0L && choose == DUOI))) { // Không phải chọn TRÊN khi ra A và chọn DƯỚI khi ra 2
                long moneyWin;
//                long fund;
                byte numA;
                Card card;
                Deck deck;
                short result = 0;
//                int i = 0;
                boolean noHu = false;
                if (this.moneyType == 1 && info.getNumA() == 2) {
                    // // lock no hũ 11/01 kane
                    noHu = (this.baseBetValue < 100000 || this.userService.getTotalRechargeMoney(user.getName()) >= Math.round((double) this.pot * 0.1)) && CaoThapUtils.isDoWithRatio(1000.0);
                }

                while (true) {
                    numA = info.getNumA();
                    if (noHu) {
                        deck = info.getDeck();
                        card = deck.deal();
                    } else {
                        card = CaoThapUtils.randomWithoutA(info.getDeck());
                        deck = info.getDeck();
                        deck.popCard(card);
                    }

                    moneyWin = 0L;
                    if (card.getRank().getRank() == info.getCard().getRank().getRank()) {
                        result = ResultCaoThap.HOA;
                        moneyWin = Math.round((float) (info.getMoney() * 9L) / 10L);
                    } else if (card.getRank().getRank() > info.getCard().getRank().getRank()) {
                        if (choose == TREN) {
                            result = ResultCaoThap.THANG;
                            moneyWin = info.getMoneyUp();
                        } else {
                            result = ResultCaoThap.THUA;
                        }
                    } else if (card.getRank().getRank() < info.getCard().getRank().getRank()) {
                        if (choose == TREN) {
                            result = ResultCaoThap.THUA;
                        } else if (choose == DUOI) {
                            result = ResultCaoThap.THANG;
                            moneyWin = info.getMoneyDown();
                        }
                    }

                    if (result == ResultCaoThap.THANG) {
                        if (info.getStep() == ResultCaoThap.STEP_ONE) {
                            if (getFunValue() - moneyWin < 0) {
                                continue;
                            }
                        } else {
                            if (getFunValue() - moneyWin - info.getMoney() < 0) {
                                continue;
                            }
                        }

                        if (card.getRank() == Rank.Ace && (numA = (byte) (numA + 1)) == 3)
                            result = ResultCaoThap.NO_HU;
                    }
                    break;
                }

                boolean askUserNext = false;
                long moneyToUser = 0L;
                if (result == ResultCaoThap.THANG) {
                    moneyToUser = moneyWin;
                    askUserNext = true;
                    if (!isBot(user.getName())) {
                        // calculate fund
                        long moneyToFund = info.getStep() == ResultCaoThap.STEP_ONE
                                ? (getFunValue() - moneyWin)
                                : (getFunValue() - moneyWin - info.getMoney());
                        updateFunValue(-moneyToFund);
                        this.saveFund();
                    }
                } else if (result == ResultCaoThap.HOA) {
                    moneyToUser = moneyWin;
                    this.pot += info.getMoney() - moneyWin;
                    this.savePot();
                    askUserNext = true;
//                    if (info.getStep() == ResultCaoThap.STEP_ONE) {
//                        updateFunValue(-(info.getMoney() + moneyWin));
//                        this.saveFund();
//                    }
                } else if (result == ResultCaoThap.THUA) {
                    if (!isBot(user.getName()) && info.getStep() > ResultCaoThap.STEP_ONE) {
                        // rollback money to fund
                        updateFunValue(info.getMoney());
                        this.saveFund();
                    }
                    currentMoney = this.userService.getCurrentMoneyUserCache(user.getName(), this.moneyTypeStr);
                    if (!isBot(user.getName())) {
                        this.userService.updateMoney(user.getName(), moneyToUser, this.moneyTypeStr, "CaoThap", "", "", 0L, info.getReferenceId(), TransType.END_TRANS);

                    }
                } else if (result == ResultCaoThap.NO_HU) {
                    if (info.getMoney() > moneyWin) {
                        this.pot += info.getMoney() - moneyWin;
                    } else {
                        updateFunValue(-(moneyWin - info.getMoney()));
                        this.saveFund();
                    }
                    moneyToUser = Math.round(this.pot / 2L);
                    this.pot -= moneyToUser;
                    this.savePot();
                    MoneyResponse moneyResponse = this.userService.updateMoney(user.getName(), moneyToUser += moneyWin, this.moneyTypeStr, "CaoThap", "Cao thấp: Nổ hũ", "Phiên: " + info.getReferenceId() + ", Bước: " + info.getStep(), 0L, info.getReferenceId(), TransType.END_TRANS);
                    if (moneyResponse != null && moneyResponse.isSuccess()) {
                        if (this.moneyType == 1) {
                            GameUtils.sendSMSToUser(user.getName(), "Chuc mung " + user.getName() + " da no hu game Cao Thap phong " + this.baseBetValue + ". So tien no hu: " + moneyToUser + " Vin");
                            if (moneyToUser >= (long) BroadcastMessageServiceImpl.MIN_MONEY) {
                                this.broadcastMsgService.putMessage(Games.CAO_THAP.getId(), user.getName(), moneyToUser);
                            }
                        }
                        currentMoney = moneyResponse.getCurrentMoney();
                        try {
                            List<Card> carryCardsNoHu = info.getCarryCards();
                            carryCardsNoHu.add(card);
                            if (!isBot(user.getName())) {
                                this.ctService.logCaoThapWin(info.getReferenceId(), user.getName(), this.baseBetValue, (short) 7, moneyToUser, CaoThapUtils.getCardStr(carryCardsNoHu), this.moneyType);

                            }
                        } catch (Exception e) {
                            Debug.trace("CAO THAP: log cao thap error ", e.getMessage());
                        }
                    }
                }


                try {
                    if (!isBot(user.getName())) {
                        this.ctService.logCaoThap(info.getReferenceId(), user.getName(), info.getMoney(), result, moneyToUser, card.toString(), this.pot, getFunValue(), this.moneyType, choose, info.getStep());
                    }
                } catch (Exception e) {
                    Debug.trace("CAO THAP: log cao thap error ", e.getMessage());
                }
                List<Double> ratioLst = CaoThapUtils.getRatio(deck, card);
                msg.money1 = Math.round((double) moneyToUser * ratioLst.get(1));
                msg.money2 = moneyToUser;
                msg.money3 = Math.round((double) moneyToUser * ratioLst.get(0));
                msg.card = (byte) card.getCode();
                this.sendMessageToUser(msg, user);
                if (askUserNext) {
                    info.setDeck(deck);
                    info.setCard(card);
                    info.setNumA(numA);
                    info.setMoney(moneyToUser);
                    List<Card> carryCards = info.getCarryCards();
                    carryCards.add(card);
                    info.setCarryCards(carryCards);
                    info.setMoneyUp(msg.money1);
                    info.setMoneyDown(msg.money3);
                    this.usersCaoThap.put(user.getName(), info);
                } else {
                    StopPlayCaoThapMsg msgStop = new StopPlayCaoThapMsg();
                    msgStop.result = (byte) result;
                    msgStop.currentMoney = currentMoney;
                    msgStop.moneyExchange = moneyToUser;
                    this.usersCaoThap.remove(user.getName());
                    this.sendMessageToUser(msgStop, user);
                }
            }
        }
//        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stopPlay(User user) {
        StopPlayCaoThapMsg msg = new StopPlayCaoThapMsg();
        Map<String, CaoThapInfo> map2 = this.usersCaoThap;
        synchronized (map2) {
            if (this.usersCaoThap.containsKey(user.getName())) {
                CaoThapInfo info = this.usersCaoThap.get(user.getName());
                if (info.getStep() != ResultCaoThap.USER_PLAYING) {
                    MoneyResponse moneyResponse = this.userService.updateMoney(user.getName(), info.getMoney(), this.moneyTypeStr, "CaoThap", "Cao thấp: Trận thắng", "Phiên: " + info.getReferenceId() + ", Bước: " + info.getStep(), 0L, info.getReferenceId(), TransType.END_TRANS);
                    if (moneyResponse != null && moneyResponse.isSuccess()) {
                        try {
                            this.ctService.logCaoThapWin(info.getReferenceId(), user.getName(), this.baseBetValue, ResultCaoThap.THANG, info.getMoney(), CaoThapUtils.getCardStr(info.getCarryCards()), (int) this.moneyType);
                        } catch (Exception e) {
                            Debug.trace("CAO THAP: log cao thap error ", e.getMessage());
                        }
                        msg.result = ResultCaoThap.THANG;
                        msg.currentMoney = moneyResponse.getCurrentMoney();
                        msg.moneyExchange = info.getMoney();
                        this.usersCaoThap.remove(user.getName());
                        if (this.moneyType == 1 && info.getMoney() >= (long) BroadcastMessageServiceImpl.MIN_MONEY) {
                            this.broadcastMsgService.putMessage(Games.CAO_THAP.getId(), user.getName(), info.getMoney());
                        }
                    } else {
                        msg.Error = ResultCaoThap.LOI_HE_THONG;
                    }
                } else {
                    msg.Error = ResultCaoThap.STEP_ONE;
                }
                this.sendMessageToUser(msg, user);
            }
        }
    }

    public void updatePotToUser(User user) {
        UpdatePotCaoThapMsg msg = new UpdatePotCaoThapMsg();
        msg.value = this.pot;
        this.sendMessageToUser(msg, user);
    }

    private void saveFund() {
        try {
            this.ctService.updateFundCaoThap(this.name, getFunValue());
        } catch (IOException | InterruptedException | TimeoutException e) {
            Debug.trace((Object[]) new Object[]{"CAO THAP: update fund cao thap error ", e.getMessage()});
        }
    }

    private void savePot() {
        UpdatePotCaoThapMsg msg = new UpdatePotCaoThapMsg();

        msg.value = this.pot;
        this.sendMessageToRoom(msg);
        try {
            sv.setValue(CACHE_JACK_POT_VALUE_SLOT + "_" + this.betValue + "_" + gameName, pot);
            this.ctService.updatePotCaoThap(this.name, this.pot);
        } catch (IOException | InterruptedException | TimeoutException e) {
            Debug.trace("CAO THAP: update pot cao thap error ", e.getMessage());
        }
    }

    public Map<String, CaoThapInfo> getUsers() {
        return this.usersCaoThap;
    }

    public void gameLoop() {
        try {
            for (Map.Entry<String, CaoThapInfo> entry : this.usersCaoThap.entrySet()) {
                CaoThapInfo info = entry.getValue();
                short i = Short.parseShort(String.valueOf(info.getTime() - 1));
                info.setTime(i);
                if (i == 0) {
                    if (info.getStep() == 1) {
                        byte choose;
                        if (info.getMoneyUp() == 0L) {
                            choose = 0;
                        } else if (info.getMoneyDown() == 0L) {
                            choose = 1;
                        } else {
                            Random rd = new Random();
                            choose = (byte) rd.nextInt(2);
                        }
                        this.play(info.getUser(), choose);
                        continue;
                    }
                    this.stopPlay(info.getUser());
                    continue;
                }
                this.usersCaoThap.put(entry.getKey(), info);
            }
        } catch (Exception e) {
            Debug.trace("Exception: " + e.getMessage(), e);
        }
    }

    public void timeLoop() {
        try {
            for (Map.Entry<String, CaoThapInfo> entry : this.usersCaoThap.entrySet()) {
                UpdateTimeCaoThapMsg msg = new UpdateTimeCaoThapMsg();
                msg.time = entry.getValue().getTime();
                this.sendMessageToUser(msg, entry.getValue().getUser());
            }
        } catch (Exception e) {
            Debug.trace("Exception: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean joinRoom(User user) {
        boolean result = super.joinRoom(user);
        if (result) {
            user.setProperty("MGROOM_CAO_THAP_INFO", this);
        }
        return result;
    }

    @Override
    public boolean quitRoom(User user) {
        return super.quitRoom(user);
    }

    @Override
    protected void checkResetPot() {
        //todo : check reset pot
    }

    public boolean isBot(String username) {
        UserCacheModel model = this.userService.getUser(username);
        return model.isBot();
    }

    public class ResultCaoThap {
        public static final short LOI_HE_THONG = 100;
        public static final short USER_PLAYING = 1;
        public static final short STEP_ONE = 2;
        public static final short NOT_ENOUGH_MONEY = 3;
        public static final short THANG = 4;
        public static final short THUA = 5;
        public static final short HOA = 6;
        public static final short NO_HU = 7;
    }

    private final class GameLoopTask
            implements Runnable {
        private GameLoopTask() {
        }

        @Override
        public void run() {
            try {
                MGRoomCaoThap.this.gameLoop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private final class TimeLoopTask
            implements Runnable {
        private TimeLoopTask() {
        }

        @Override
        public void run() {
            try {
                MGRoomCaoThap.this.timeLoop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

