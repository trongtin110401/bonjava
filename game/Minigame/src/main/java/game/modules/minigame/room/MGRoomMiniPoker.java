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
 *  com.vinplay.cardlib.models.GroupType
 *  com.vinplay.cardlib.utils.CardLibUtils
 *  com.vinplay.dal.service.BroadcastMessageService
 *  com.vinplay.dal.service.CacheService
 *  com.vinplay.dal.service.MiniGameService
 *  com.vinplay.dal.service.MiniPokerService
 *  com.vinplay.dal.service.impl.BroadcastMessageServiceImpl
 *  com.vinplay.dal.service.impl.CacheServiceImpl
 *  com.vinplay.dal.service.impl.MiniGameServiceImpl
 *  com.vinplay.dal.service.impl.MiniPokerServiceImpl
 *  com.vinplay.usercore.service.UserService
 *  com.vinplay.usercore.service.impl.UserServiceImpl
 *  com.vinplay.vbee.common.enums.Games
 *  com.vinplay.vbee.common.exceptions.KeyNotFoundException
 *  com.vinplay.vbee.common.models.UserModel
 *  com.vinplay.vbee.common.models.cache.UserCacheModel
 *  com.vinplay.vbee.common.response.MoneyResponse
 *  com.vinplay.vbee.common.statics.TransType
 */
package game.modules.minigame.room;

import bitzero.server.BitZeroServer;
import bitzero.server.entities.User;
import bitzero.util.common.business.Debug;
import com.vinplay.cardlib.models.Card;
import com.vinplay.cardlib.models.GroupType;
import com.vinplay.cardlib.utils.CardLibUtils;
import com.vinplay.dal.service.BroadcastMessageService;
import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.MiniGameService;
import com.vinplay.dal.service.MiniPokerService;
import com.vinplay.dal.service.impl.BroadcastMessageServiceImpl;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.dal.service.impl.MiniGameServiceImpl;
import com.vinplay.dal.service.impl.MiniPokerServiceImpl;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.response.MoneyResponse;
import com.vinplay.vbee.common.statics.TransType;
import game.modules.minigame.cmd.send.minipoker.ForceStopAuatoPlayMiniPokerMsg;
import game.modules.minigame.cmd.send.minipoker.ResultMiniPokerMsg;
import game.modules.minigame.cmd.send.minipoker.UpdatePotMiniPokerMsg;
import game.modules.minigame.entities.AutoUserMiniPoker;
import game.modules.minigame.utils.GenerationMiniPoker;
import game.utils.ConfigGame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MGRoomMiniPoker extends MGRoom {
    private long pot;
    private final short moneyType;
    private long betValue = 0L;
    private final long initPotValue;
    private UserService userService = new UserServiceImpl();
    private MiniPokerService mpService = new MiniPokerServiceImpl();
    private MiniGameService mgService = new MiniGameServiceImpl();
    private final BroadcastMessageService broadcastMsgService = new BroadcastMessageServiceImpl();
    private GenerationMiniPoker gen = new GenerationMiniPoker();
    private final Map<String, AutoUserMiniPoker> usersAuto = new HashMap<>();
    private final Runnable gameLoopTask = new GameLoopTask();
    private long lastTimeUpdatePotToRoom = 0L;
    private long lastTimeUpdateFundToRoom = 0L;
    private final ThreadPoolExecutor executor;
    private boolean huX2 = false;
    protected CacheService sv = new CacheServiceImpl();
    private final Runnable checkResetPotTask = new CheckResetPot();

    public static final String CACHE_NAME_USER_SPOT = "user_force_jackpot_";
    public static final String CACHE_BET_VALUE_SLOT = "bet_value_jackpot_";
    CacheService cacheService = new CacheServiceImpl();

    public MGRoomMiniPoker(String roomName, short moneyType, long pot, long fund, long baseBetValue, long initPotValue) {
        super(Games.MINI_POKER.getName(), roomName, (int) baseBetValue, fund, moneyType);
        this.gameName = Games.MINI_POKER.getName();
        this.moneyType = moneyType;

        setPercentFee();

        this.executor = moneyType == 1 ? (ThreadPoolExecutor) Executors.newFixedThreadPool(ConfigGame.getIntValue("mini_poker_thread_pool_per_room_vin")) : (ThreadPoolExecutor) Executors.newFixedThreadPool(ConfigGame.getIntValue("mini_poker_thread_pool_per_room_xu"));
        if (pot < 0) {
            pot = initPotValue;
        }
        this.pot = pot;
        sv.setValue(CACHE_JACK_POT_VALUE_SLOT + "_" + this.betValue + "_" + gameName, pot);
        this.betValue = baseBetValue;
        this.initPotValue = initPotValue;

        setPercentFee();
        setPercentJackpot();

        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.gameLoopTask, 10, 1, TimeUnit.SECONDS);
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.checkResetPotTask, 10, 10, TimeUnit.SECONDS);
        try {
            this.mgService.savePot(this.name, CACHE_JACK_POT_VALUE_SLOT + "_" + this.betValue + "_" + gameName, pot, this.huX2);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public short play(User user) {
        return this.play(user, this.betValue);
    }

    public short play(User user, long betValue) {
        ResultMiniPokerMsg msg = this.play(user.getName(), betValue);
        this.sendMessageToUser(msg, user);
        return msg.result;
    }

    public ResultMiniPokerMsg play(String username, long betValue) {
        long lastPot = this.pot;
        long lastFund = getFunValue();
        ResultMiniPokerMsg resultMiniPokerMsg = new ResultMiniPokerMsg();
        StringBuilder builder = new StringBuilder();
        short result = ResultPoker.TRUOT;
        long prize = 0L;
        UserCacheModel u = this.userService.getUser(username);
        long referenceId = System.currentTimeMillis();

        // từ PHP admin, cài đặt cho một người chơi trúng JACKPOT
        String usernameForce;
        // phòng được set nổ hũ
        String roomForce;
        boolean forceJackpotByUser = false;
        try {
            usernameForce = sv.getValueStr(CACHE_NAME_USER_SPOT + this.gameName);
            roomForce = sv.getValueStr(CACHE_BET_VALUE_SLOT + this.gameName);
        } catch (Exception e) {
            usernameForce = "";
            roomForce = "";
        }
        long currentMoney = this.userService.getMoneyUserCache(username, this.moneyTypeStr);
        if (betValue > 0L) {
            if (currentMoney >= betValue) {
                MoneyResponse moneyRes = new MoneyResponse(false, "1001");
                long fee = (long) (betValue * percentFee / 100.0f);
                if (!u.isBot()) {
                    moneyRes = this.userService.updateMoney(username, -betValue, this.moneyTypeStr, Games.MINI_POKER.getName(), "Quay MiniPoker", "Đặt cược MiniPoker", fee, referenceId, TransType.START_TRANS);
                } else {
                    moneyRes.setSuccess(true);
                }
                if (moneyRes != null && moneyRes.isSuccess()) {
                    boolean enoughToPair = false;
                    long moneyToPot = betValue / 100L;

                    long moneyToFund = betValue - moneyToPot - fee;
                    long tienThuongX2;
                    this.pot += moneyToPot;
                    if (!u.isBot()) {
                        updateFunValue(moneyToFund);
                    }
                    synchronized (this) {
                        while (!enoughToPair) {
                            GroupType groupType;
                            prize = 0L;
                            tienThuongX2 = 0L;
                            long moneyExchange;
                            boolean isForceJackpot = false;
                            if (betValue == 100) {
                                if (usernameForce.equals(username) && roomForce.equals(String.valueOf(100))) {
                                    isForceJackpot = true;
                                    forceJackpotByUser = true;
                                    result = ResultPoker.NO_HU;
                                }
                            } else if (betValue == 1000) {
                                if (usernameForce.equals(username) && roomForce.equals(String.valueOf(1000))) {
                                    isForceJackpot = true;
                                    forceJackpotByUser = true;
                                    result = ResultPoker.NO_HU;
                                }
                            } else {
                                if (usernameForce.equals(username) && roomForce.equals(String.valueOf(10000))) {
                                    isForceJackpot = true;
                                    forceJackpotByUser = true;
                                    result = ResultPoker.NO_HU;
                                }
                            }

                            List<Card> cards = this.gen.randomCards2(forceJackpotByUser);
                            if (cards.size() != 5) {
                                cards = this.gen.randomCards();
                            }

                            if ((groupType = CardLibUtils.calculateTypePoker(cards)) == null) continue;

                            switch (groupType) {
                                case HighCard: {
                                    result = ResultPoker.BAI_CAO;
                                    break;
                                }
                                case OnePair: {
                                    if (CardLibUtils.pairEqualOrGreatJack(cards)) {
                                        result = ResultPoker.MOT_DOI_TO;
                                        prize = (int) ((float) betValue * 2.5f);
                                        break;
                                    }
                                    result = ResultPoker.MOT_DOI_NHO;
                                    break;
                                }
                                case TwoPair: {
                                    result = ResultPoker.HAI_DOI;
                                    prize = betValue * 5L;
                                    break;
                                }
                                case ThreeOfKind: {
                                    result = ResultPoker.SAM_CO;
                                    prize = betValue * 8L;
                                    break;
                                }
                                case Straight: {
                                    result = ResultPoker.SANH;
                                    if (this.moneyType == 1) {
                                        prize = betValue * 13L;
                                        break;
                                    }
                                    prize = betValue * 12L;
                                    break;
                                }
                                case Flush: {
                                    result = ResultPoker.THUNG;
                                    if (this.moneyType == 1) {
                                        prize = betValue * 20L;
                                        break;
                                    }
                                    prize = betValue * 18L;
                                    break;
                                }
                                case FullHouse: {
                                    result = ResultPoker.CU_LU;
                                    if (this.moneyType == 1) {
                                        prize = betValue * 50L;
                                        break;
                                    }
                                    prize = betValue * 40L;
                                    break;
                                }
                                case FourOfKind: {
                                    result = ResultPoker.TU_QUY;
                                    if (this.moneyType == 1) {
                                        prize = betValue * 150L;
                                        break;
                                    }
                                    prize = betValue * 120L;
                                    break;
                                }
                                case StraightFlush: {
                                    if (forceJackpotByUser) {
                                        if (CardLibUtils.isStraightFlushJack(cards)) {
                                            result = ResultPoker.NO_HU;
                                            if (this.huX2) {
                                                tienThuongX2 = this.pot;
                                                prize = this.pot * 2L;
                                                break;
                                            }
                                            prize = this.pot;
                                            break;
                                        }
                                        result = ResultPoker.THUNG_PHA_SANH_NHO;
                                        prize = betValue * 1000L;
                                    }
                                }
                            }

                            // Phần thưởng quá lớn, random lại
                            if (!isForceJackpot && prize > 0 && getFunValue() < prize) {
                                continue;
                            }

                            long fundExchange = Math.max(prize, 0L);
                            enoughToPair = true;
                            if (cards.size() == 5) {
                                resultMiniPokerMsg.card1 = (byte) cards.get(0).getCode();
                                resultMiniPokerMsg.card2 = (byte) cards.get(1).getCode();
                                resultMiniPokerMsg.card3 = (byte) cards.get(2).getCode();
                                resultMiniPokerMsg.card4 = (byte) cards.get(3).getCode();
                                resultMiniPokerMsg.card5 = (byte) cards.get(4).getCode();
                            }
                            if (prize > 0L) {
                                if (result == ResultPoker.NO_HU) {
                                    if (this.huX2) {
                                        result = ResultPoker.NO_HU_X2;
                                    }
                                    if (isForceJackpot) {
                                        try {
                                            this.pot = this.initPotValue;
                                            if (!u.isBot()) updateFunValue(-initPotValue);
                                            sv.removeKey(CACHE_NAME_USER_SPOT + this.gameName);
                                            sv.removeKey(CACHE_BET_VALUE_SLOT + this.gameName);
                                        } catch (Exception exception) {
                                            exception.printStackTrace();
                                        }
                                    }
                                } else {
                                    if (!u.isBot()) updateFunValue(-fundExchange);
                                }
                            }

                            long moneyAdded = prize;
                            String des = "Quay MiniPoker";
                            if (result == ResultPoker.NO_HU_X2 && !u.isBot()) {
                                moneyAdded -= tienThuongX2;
                                this.userService.updateMoney(username, tienThuongX2, this.moneyTypeStr, this.gameName, des, "Thắng X2", 0L, null, TransType.NO_VIPPOINT);
                            }
                            if (!u.isBot()) {
                                moneyRes = this.userService.updateMoney(username, moneyAdded, this.moneyTypeStr, Games.MINI_POKER.getName(), des, this.buildDescription(betValue, moneyAdded, result), 0, referenceId, TransType.END_TRANS);
                            }
                            moneyExchange = prize - betValue;
                            if (moneyRes != null && moneyRes.isSuccess()) {
                                currentMoney = moneyRes.getCurrentMoney();
                                if (this.moneyType == 1 && moneyExchange >= (long) BroadcastMessageServiceImpl.MIN_MONEY) {
                                    this.broadcastMsgService.putMessage(Games.MINI_POKER.getId(), username, moneyExchange);
                                }
                            }
                            builder.append(cards.get(0).toString());
                            for (int i = 1; i < cards.size(); ++i) {
                                builder.append(",");
                                builder.append(cards.get(i).toString());
                            }
                            try {
                                if (!isBot(username)) {
                                    this.mpService.logMiniPoker(username, betValue, result, prize, builder.toString(), lastPot, lastFund, this.moneyType);
                                }
                            } catch (IOException | InterruptedException | TimeoutException e) {
                                Debug.trace("Log mini poker error ", e.getMessage());
                            }
                        }
                    }
                    this.saveFund();
                    this.savePot();
                }
            } else {
                result = ResultPoker.KHONG_DU_TIEN;
            }
        } else {
            result = ResultPoker.DAT_CUOC_KHONG_HOP_LE;
        }

        sv.setValue(CACHE_JACK_POT_VALUE_SLOT + "_" + this.betValue + "_" + gameName, String.valueOf(this.pot));

        if (forceJackpotByUser) {
            this.sendNotifyNoHu(username, (byte) 1, resultMiniPokerMsg.prize, this.gameName);
        }

        resultMiniPokerMsg.result = result;
        resultMiniPokerMsg.prize = prize;
        resultMiniPokerMsg.currentMoney = currentMoney;
        return resultMiniPokerMsg;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void autoPlay(User user) {
        synchronized (this.usersAuto) {
            if (this.usersAuto.containsKey(user.getName())) {
                AutoUserMiniPoker entry = this.usersAuto.get(user.getName());
                this.forceStopAutoPlay(entry.getUser());
            }
            this.usersAuto.put(user.getName(), new AutoUserMiniPoker(user, 0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stopAutoPlay(User user) {
        synchronized (this.usersAuto) {
            if (this.usersAuto.containsKey(user.getName()) && this.usersAuto.get(user.getName()).getUser().getId() == user.getId()) {
                this.usersAuto.remove(user.getName());
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void forceStopAutoPlay(User user) {
        synchronized (this.usersAuto) {
            this.usersAuto.remove(user.getName());
            ForceStopAuatoPlayMiniPokerMsg msg = new ForceStopAuatoPlayMiniPokerMsg();
            this.sendMessageToUser(msg, user);
        }
    }

    public boolean isBot(String username) {
        UserCacheModel model = this.userService.getUser(username);
        return model.isBot();
    }

    private void saveFund() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - this.lastTimeUpdateFundToRoom >= 3000L) {
            try {
                this.mgService.saveFund(this.name, getFunValue());
            } catch (IOException | InterruptedException | TimeoutException e) {
                Debug.trace("MINI POKER: update fund poker error ", e.getMessage());
            }
            this.lastTimeUpdateFundToRoom = currentTime;
        }
    }

    private void savePot() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - this.lastTimeUpdatePotToRoom >= 3000L) {
            UpdatePotMiniPokerMsg msg = new UpdatePotMiniPokerMsg();
            msg.value = this.pot;
            msg.x2 = this.huX2 ? (byte) 1 : 0;
            this.sendMessageToRoom(msg);
            this.lastTimeUpdatePotToRoom = currentTime;
            try {
                this.mgService.savePot(this.name, CACHE_JACK_POT_VALUE_SLOT + "_" + this.betValue + "_" + gameName, this.pot, false);
            } catch (IOException | InterruptedException | TimeoutException e) {
                Debug.trace("MINI POKER: update pot poker error ", e.getMessage());
            }
        }
    }

    public void updatePotToUser(User user) {
        UpdatePotMiniPokerMsg msg = new UpdatePotMiniPokerMsg();
        msg.value = this.pot;
        msg.x2 = this.huX2 ? (byte) 1 : 0;
        this.sendMessageToUser(msg, user);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void gameLoop() {

        setPercentFee();
        setPercentJackpot();

        ArrayList<User> usersPlay = new ArrayList<>();
        synchronized (this.usersAuto) {
            for (AutoUserMiniPoker user : this.usersAuto.values()) {
                boolean play = user.incCount();
                if (!play) continue;
                usersPlay.add(user.getUser());
            }
        }
        int numThreads = usersPlay.size() / 100 + 1;
        for (int i = 1; i <= numThreads; ++i) {
            int fromIndex = (i - 1) * 100;
            int toIndex = i * 100;
            if (toIndex > usersPlay.size()) {
                toIndex = usersPlay.size();
            }
            ArrayList tmp = new ArrayList(usersPlay.subList(fromIndex, toIndex));
            PlayListMiniPokerTask task = new PlayListMiniPokerTask(tmp);
            this.executor.execute(task);
        }
        usersPlay.clear();
    }

    public void playListMiniPoker(List<User> users) {
        for (User user : users) {
            short result = this.play(user, this.betValue);
            if (result != 1 && result != 2 && result != 12 && result != 102 && result != 100) continue;
            this.forceStopAutoPlay(user);
        }
        users.clear();
    }

    private String buildDescription(long totalBet, long totalPrizes, short result) {
        if (totalBet == 0L) {
            return this.resultToString(result) + ": " + totalPrizes;
        }
        return "Quay: " + totalBet + ", " + this.resultToString(result) + ": " + totalPrizes;
    }

    private String resultToString(short result) {
        switch (result) {
            case 1: {
                return "Nổ hũ";
            }
            case 12: {
                return "Nổ hũ X2";
            }
            case 2: {
                return "Thùng phá sảnh";
            }
            case 3: {
                return "Tứ quý";
            }
            case 4: {
                return "Cù lũ";
            }
            case 5: {
                return "Thắng";
            }
            case 6: {
                return "Sảnh";
            }
            case 7: {
                return "Sảnh chúa";
            }
            case 8: {
                return "Hai đôi";
            }
            case 9: {
                return "Lá bài cao";
            }
        }
        return "Trượt";
    }

    @Override
    public boolean joinRoom(User user) {
        boolean result = super.joinRoom(user);
        if (result) {
            user.setProperty("MGROOM_MINI_POKER_INFO", this);
        }
        return result;
    }

    @Override
    public boolean quitRoom(User user) {
        return super.quitRoom(user);
    }

    @Override
    protected void checkResetPot() {
        try {
            int isReset = sv.getValueInt("reset_pot_" + this.gameName + "_" + this.betValue, 0);
            if (isReset == 1) {
                this.pot = this.initPotValue;
//                updateFunValue(-getFunValue());
                this.savePot();
//                this.saveFund();
                this.sv.removeKey("reset_pot_" + this.gameName + "_" + this.betValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startHuX2() {
    }


    private void calculatHuX2() {
    }

    public static class ResultPoker {
        public static final short LOI_HE_THONG = 100;
        public static final short DAT_CUOC_KHONG_HOP_LE = 101;
        public static final short KHONG_DU_TIEN = 102;
        public static final short TRUOT = 0;
        public static final short NO_HU = 1;
        public static final short THUNG_PHA_SANH_NHO = 2;
        public static final short TU_QUY = 3;
        public static final short CU_LU = 4;
        public static final short THUNG = 5;
        public static final short SANH = 6;
        public static final short SAM_CO = 7;
        public static final short HAI_DOI = 8;
        public static final short MOT_DOI_TO = 9;
        public static final short MOT_DOI_NHO = 10;
        public static final short BAI_CAO = 11;
        public static final short NO_HU_X2 = 12;
    }

    private final class GameLoopTask
            implements Runnable {
        private GameLoopTask() {
        }

        @Override
        public void run() {
            try {
                MGRoomMiniPoker.this.gameLoop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private final class PlayListMiniPokerTask
            extends Thread {
        private final List<User> users;

        private PlayListMiniPokerTask(List<User> users) {
            this.users = users;
            this.setName("AutoPlayMiniPoker");
        }

        @Override
        public void run() {
            MGRoomMiniPoker.this.playListMiniPoker(this.users);
        }
    }

}

