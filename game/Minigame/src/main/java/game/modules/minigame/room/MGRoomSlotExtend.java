package game.modules.minigame.room;

import bitzero.server.BitZeroServer;
import bitzero.server.entities.User;
import bitzero.util.common.business.Debug;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.dal.service.BroadcastMessageService;
import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.MiniGameService;
import com.vinplay.dal.service.impl.BroadcastMessageServiceImpl;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.dal.service.impl.MiniGameServiceImpl;
import com.vinplay.usercore.dao.impl.UserDaoImpl;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.exceptions.KeyNotFoundException;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.response.MoneyResponse;
import com.vinplay.vbee.common.statics.TransType;
import com.vinplay.vbee.common.utils.CommonUtils;
import com.vinplay.vbee.common.utils.DateTimeUtils;
import game.SlotExtendService;
import game.SlotExtendServiceImplement;
import game.modules.minigame.GroupItem;
import game.modules.minigame.ItemKind;
import game.modules.minigame.LineWin;
import game.modules.minigame.Slot3x3ExtendModule;

import game.modules.minigame.cmd.send.slot3x3.ForceStopAutoPlaySlotExtendMsg;
import game.modules.minigame.cmd.send.slot3x3.ResultSlotExtendMsg;
import game.modules.minigame.cmd.send.slot3x3.UpdatePotSlotExtend;
import game.modules.minigame.entities.AutoUserSlotExtend;
import game.modules.minigame.utils.RandomUtil;
import game.modules.minigame.utils.SlotExtendUtils;
import game.utils.ConfigGame;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MGRoomSlotExtend extends MGRoom {

    private long pot;
    private long fund;
    private long initPotValue;
    private int betValue;
    private short moneyType;
    private String moneyTypeStr;
    private UserService userService = new UserServiceImpl();
    private MiniGameService mgService = new MiniGameServiceImpl();
    private SlotExtendService slexService = new SlotExtendServiceImplement();

    private BroadcastMessageService broadcastMsgService = new BroadcastMessageServiceImpl();
    private final Runnable gameLoopTask;
    private Map<String, AutoUserSlotExtend> usersAuto;
    private long lastTimeUpdatePotToRoom;
    private long lastTimeUpdateFundToRoom;
    private ThreadPoolExecutor executor;
    private int countHu;
    private int countNoHuX2;
    private boolean huX2;
    protected CacheService sv;
    public static final int MINI_GAME_WIN_TYPE_FAIL = 0;
    public static final int MINI_GAME_WIN_TYPE_NORMAL = 1;
    public static final int MINI_GAME_WIN_TYPE_BIG_WIN = 2;
    public static final int MINI_GAME_WIN_TYPE_JACKPOT_BROKEN = 3;
    public static final int MAX_NUMBER_GET_FAIL = 15;
    // Tổng số lần random để lấy ra bộ bài hợp lệ
    public static final int DIAMOND_MAX_NUMBER_RANDOM = 5;
    private final int[] arrItemValue = {ItemKind.RED, ItemKind.GREEN, ItemKind.WHITE, ItemKind.BLUE, ItemKind.VIOLET, ItemKind.YELLOW};
    private final int[] arrItemValue1 = {ItemKind.RED, ItemKind.YELLOW, ItemKind.VIOLET, ItemKind.WHITE, ItemKind.BLUE,
            ItemKind.VIOLET, ItemKind.GREEN, ItemKind.VIOLET, ItemKind.WHITE, ItemKind.BLUE,
            ItemKind.WHITE, ItemKind.VIOLET, ItemKind.GREEN, ItemKind.VIOLET, ItemKind.BLUE,
            ItemKind.YELLOW, ItemKind.BLUE, ItemKind.VIOLET, ItemKind.YELLOW, ItemKind.GREEN};
    private final int[] arrItemValue2 = {ItemKind.YELLOW, ItemKind.VIOLET, ItemKind.GREEN, ItemKind.VIOLET, ItemKind.WHITE,
            ItemKind.YELLOW, ItemKind.VIOLET, ItemKind.VIOLET, ItemKind.WHITE, ItemKind.BLUE,
            ItemKind.WHITE, ItemKind.GREEN, ItemKind.YELLOW, ItemKind.YELLOW, ItemKind.YELLOW,
            ItemKind.RED, ItemKind.WHITE, ItemKind.VIOLET, ItemKind.GREEN, ItemKind.BLUE};
    private final int[] arrItemValue3 = {ItemKind.WHITE, ItemKind.VIOLET, ItemKind.GREEN, ItemKind.VIOLET, ItemKind.WHITE,
            ItemKind.YELLOW, ItemKind.VIOLET, ItemKind.GREEN, ItemKind.YELLOW, ItemKind.BLUE,
            ItemKind.BLUE, ItemKind.VIOLET, ItemKind.VIOLET, ItemKind.YELLOW, ItemKind.YELLOW,
            ItemKind.WHITE, ItemKind.YELLOW, ItemKind.GREEN, ItemKind.RED, ItemKind.VIOLET};

    private int[][] retVal = {
            {3, 1, 5, 3, 5, 3, 4, 5, 2},
            {3, 1, 5, 3, 5, 5, 4, 5, 2},
            {3, 1, 5, 4, 5, 6, 4, 5, 5},
            {3, 1, 5, 3, 5, 1, 4, 5, 5},
            {3, 1, 5, 3, 6, 5, 4, 5, 5},
            {3, 4, 5, 3, 5, 3, 4, 4, 2},
            {3, 4, 5, 3, 2, 3, 4, 4, 2}
    };

    int[][] arrLines = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 7, 2}, {6, 1, 8},
            {0, 4, 2}, {0, 4, 8}, {6, 4, 2}, {3, 7, 5}, {3, 1, 5},
            {6, 4, 8}, {0, 1, 5}, {3, 4, 8}, {3, 4, 2}, {6, 7, 5},
            {3, 1, 2}, {6, 4, 5}, {0, 4, 5}, {3, 7, 8}, {0, 7, 5},
            {0, 1, 8}, {0, 7, 8}, {6, 1, 2}, {3, 1, 8}, {3, 7, 2}, {6, 1, 5}, {6, 7, 2},
    };
    int[] arrMutil = {1, 3, 5, 10};

    public MGRoomSlotExtend(String name, short moneyType, long pot, long fund, int betValue, long initPotValue) {
        super(name);
        this.gameLoopTask = new GameLoopTask();
        this.usersAuto = new HashMap();
        this.lastTimeUpdatePotToRoom = 0L;
        this.lastTimeUpdateFundToRoom = 0L;
        this.countHu = -1;
        this.countNoHuX2 = 0;
        this.huX2 = false;
        this.sv = new CacheServiceImpl();
        this.moneyType = moneyType;
        this.moneyTypeStr = (this.moneyType == 1 ? "vin" : "xu");
        this.executor = (moneyType == 1 ? (ThreadPoolExecutor) Executors.newFixedThreadPool(ConfigGame.getIntValue("slot_extend_thread_pool_per_room_vin")) : (ThreadPoolExecutor) Executors.newFixedThreadPool(ConfigGame.getIntValue("slot_extend_thread_pool_per_room_xu")));
        this.pot = pot;
        CacheServiceImpl cacheService = new CacheServiceImpl();
        cacheService.setValue(name, (int) pot);
        this.fund = fund;
        this.betValue = betValue;
        this.initPotValue = initPotValue;
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.gameLoopTask, 10, 1, TimeUnit.SECONDS);
        try {
            this.countHu = this.sv.getValueInt(name + "_count_hu");
            this.countNoHuX2 = this.sv.getValueInt(name + "_count_no_hu_x2");
        } catch (KeyNotFoundException localKeyNotFoundException) {
        }

        try {
            this.mgService.savePot(name, pot, this.huX2);
        } catch (IOException | InterruptedException | TimeoutException localIOException) {
        }
    }


    public void autoPlay(User user, long gold) {
        Map<String, AutoUserSlotExtend> map = this.usersAuto;
        synchronized (map) {
            if (this.usersAuto.containsKey(user.getName())) {
                AutoUserSlotExtend entry = this.usersAuto.get(user.getName());
                forceStopAutoPlay(entry.getUser());
            }
            this.usersAuto.put(user.getName(), new AutoUserSlotExtend(user, gold));
        }
    }


    public void stopAutoPlay(User user) {
        Map<String, AutoUserSlotExtend> map = this.usersAuto;
        synchronized (map) {
            if ((this.usersAuto.containsKey(user.getName())) && (this.usersAuto.get(user.getName()).getUser().getId() == user.getId())) {
                this.usersAuto.remove(user.getName());
            }
        }
    }


    public void forceStopAutoPlay(User user) {
        Map<String, AutoUserSlotExtend> map = this.usersAuto;
        synchronized (map) {
            this.usersAuto.remove(user.getName());
            ForceStopAutoPlaySlotExtendMsg msg = new ForceStopAutoPlaySlotExtendMsg();
            sendMessageToUser(msg, user);
        }
    }

    public synchronized ResultSlotExtendMsg play(String username, long gold) {
        long startTime = System.currentTimeMillis();
        long refernceId = Slot3x3ExtendModule.getNewRefenceId();
           String currentTimeStr = DateTimeUtils.getCurrentTime();

        short result = 0;
        long currentMoney = this.userService.getMoneyUserCache(username, this.moneyTypeStr);
        boolean isValid = false;
        long prizeAmount = 0;
        int i = 0;
        int[] validShowItem = new int[9];
        boolean isJackpotBroken = false;
        List<LineWin> listLineWin = new ArrayList<>();
        StringBuilder strLineWin = new StringBuilder();

        int rd1 = 1;
        int rd2 = 1;
        int mutil = 1;
        int winType = 0;

        if (gold == this.betValue) {
            if (gold > 0L) {
                if (gold <= currentMoney) {
                    MoneyResponse moneyRes = this.userService.updateMoney(username, -gold, this.moneyTypeStr, "SlotExtend", "Quay SlotExtend", "Đặt cược SlotExtend", 0L, refernceId, TransType.START_TRANS);
                    if ((moneyRes != null) && (moneyRes.isSuccess())) {
                        long fee = gold * 2L / 100L;
                        long moneyToPot = gold / 100L;
                        long moneyToFund = gold - fee - moneyToPot;
                        this.fund += moneyToFund;
                        this.pot += moneyToPot;

                        outerloop:
                        while (!isValid && i < DIAMOND_MAX_NUMBER_RANDOM) {
                            int[] showItem = initShuffleCollectItem();
                            long checkPrizeAmount = 0;
                            List<LineWin> checkLineWin = new ArrayList<>();
                            boolean isJackpot = false;

                            for (int j = 0; j < arrLines.length; j++) {
                                //random Mutil
                                rd1 = arrMutil[RandomUtil.randInt(4)];
                                rd2 = arrMutil[RandomUtil.randInt(4)];
                                mutil = 1;
                                if (rd1 != 1) {
                                    mutil = rd1 * rd2;
                                }

                                int line = j;
                                GroupItem groupItem = new GroupItem(getItemValueByLineIndex(showItem, line), mutil);

                                if (groupItem.isJackpot()) {
                                    isJackpot = true;
                                    if (isJackpotBroken()) {
                                        isJackpotBroken = true;
                                        prizeAmount = this.initPotValue;
                                        LineWin lineWin = new LineWin();
                                        lineWin.setLine(line);
                                        lineWin.setJackpot(true);
                                        lineWin.setPrizeAmount(prizeAmount);
                                        listLineWin.add(lineWin);
                                        strLineWin.append(line + ",");

                                        validShowItem = showItem;
                                        break outerloop;
                                    }
                                    break;
                                } else if (groupItem.getPrizeAmount() > 0L) {
                                    long itemPrizeAmount = groupItem.getPrizeAmount();
                                    long prizeAmount1 = groupItem.getPrizeAmount() * this.betValue;
                                    LineWin lineWin = new LineWin();
                                    lineWin.setLine(line);
                                    checkPrizeAmount += itemPrizeAmount;
                                    lineWin.setPrizeAmount(prizeAmount1);
                                    checkLineWin.add(lineWin);
                                }
                            }


                            if (!isJackpot) {
                                prizeAmount = checkPrizeAmount * this.betValue * mutil;

                                if (prizeAmount >= this.betValue * 100) {
                                    if (prizeAmount <= this.fund && prizeAmount <= this.fund * 50 / 100L) {
                                        validShowItem = showItem;
                                        isValid = true;
                                        listLineWin = checkLineWin;
                                    } else {
                                        prizeAmount = 0;
                                    }

                                } else {
                                    if (prizeAmount > 0L && prizeAmount <= this.fund * 80 / 100L) {
                                        validShowItem = showItem;
                                        isValid = true;
                                        listLineWin = checkLineWin;
                                    } else {
                                        prizeAmount = 0;
                                    }
                                }
                            }

                            i++;
                        }


                        boolean bigWin = false;
                        if (prizeAmount >= this.betValue * 100L) {
                            bigWin = true;
                        }
                        refernceId = Slot3x3ExtendModule.getNewRefenceId();
                        if (isJackpotBroken) {
                            winType = MINI_GAME_WIN_TYPE_JACKPOT_BROKEN;

                        } else if (isValid && bigWin) {
                            winType = MINI_GAME_WIN_TYPE_BIG_WIN;
                        } else if (isValid) {
                            winType = MINI_GAME_WIN_TYPE_NORMAL;
                        } else {
                            prizeAmount = 0;
                            winType = MINI_GAME_WIN_TYPE_FAIL;
                            validShowItem = getCollectItemFail();
                        }
                        moneyRes = this.userService.updateMoney(username, prizeAmount, this.moneyTypeStr, "SlotExtend", "Quay SlotExtend", buildDescription(betValue, prizeAmount, result), fee, refernceId, TransType.END_TRANS);
                        double moneyExchange = prizeAmount - this.betValue;
                        if ((moneyRes != null) && (moneyRes.isSuccess())) {
                            currentMoney = moneyRes.getCurrentMoney();
                            if ((this.moneyType == 1) && (moneyExchange >= BroadcastMessageServiceImpl.MIN_MONEY)) {
                                this.broadcastMsgService.putMessage(31, username, (long) moneyExchange);
                            }
                        }
                        try {
                            // log                            
                            this.slexService.logSlotExtend(refernceId, username, this.betValue, "20", Arrays.toString(validShowItem), "prizeAmount", result, prizeAmount, this.moneyType, currentTimeStr);
                            if ((result == 3) || (result == 4)) {
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
                                this.slexService.addTop(displayName, this.betValue, prizeAmount, this.moneyType, currentTimeStr, result);
                            }
                        } catch (InterruptedException localInterruptedException) {
                        } catch (TimeoutException localTimeoutException) {
                        } catch (IOException localIOException) {
                        }

                        saveFund();
                        savePot();
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
        long endTime = System.currentTimeMillis();
        long handleTime = endTime - startTime;
        String ratioTime = CommonUtils.getRatioTime(handleTime);
        SlotExtendUtils.log(refernceId, username, this.betValue, Arrays.toString(validShowItem), result, this.moneyType, handleTime, ratioTime, currentTimeStr);
        if (isJackpotBroken) {
            this.pot = this.initPotValue;
            this.fund -= this.initPotValue;
        } else
            this.fund -= prizeAmount;
        ResultSlotExtendMsg msg = this.sendDiamondNewSpinSuccess(result, (int) refernceId, mutil, rd1, rd2, listLineWin, validShowItem, prizeAmount, winType, currentMoney);

        return msg;
    }

    private ResultSlotExtendMsg sendDiamondNewSpinSuccess(int result, int spinId, int mutil, int mutil1, int mutil2, List<LineWin> listLineWin, int[] showItem, long moneyPrize, int winType, long currMoney) {

        ResultSlotExtendMsg res = new ResultSlotExtendMsg();
        res.result = result;
        res.prize = moneyPrize;
        res.mutil = mutil;
        res.mutil1 = mutil1;
        res.mutil2 = mutil2;
        res.winType = winType;
        res.spinId = spinId;
        res.listLineWin = listLineWin;
        res.showItem = showItem;
        res.currMoney = currMoney;

        return res;
    }

    public short play(User user, long betvalue) {
        String username = user.getName();
        ResultSlotExtendMsg msg = play(username, betvalue);

        sendMessageToUser(msg, user);
        return (short) msg.result;
    }

    private void saveFund() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - this.lastTimeUpdateFundToRoom >= 60000L) {
            try {
                this.mgService.saveFund(this.name, this.fund);
            } catch (IOException | InterruptedException | TimeoutException e) {
                Debug.trace((Object[]) new Object[]{"Slot extend: update fund Slot extend error ", e.getMessage()});
            }
            this.lastTimeUpdateFundToRoom = currentTime;
        }
    }

    private void savePot() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - this.lastTimeUpdatePotToRoom >= 3000L) {
            UpdatePotSlotExtend msg = new UpdatePotSlotExtend();
            msg.value1 = this.pot;
            msg.value2 = this.pot;
            msg.value3 = this.pot;
            sendMessageToRoom(msg);
            this.lastTimeUpdatePotToRoom = currentTime;
            try {
                this.mgService.savePot(this.name, this.pot, this.huX2);
            } catch (IOException | InterruptedException | TimeoutException e) {
                Debug.trace((Object[]) new Object[]{"Slot extend: update pot Slot extend error ", e.getMessage()});
            }
        }
    }

    public void updatePotToUser(User user) {
        UpdatePotSlotExtend msg = new UpdatePotSlotExtend();
        msg.value1 = this.pot;
        msg.value2 = this.pot;
        msg.value3 = this.pot;
        sendMessageToUser(msg, user);
    }


    private void gameLoop() {
        ArrayList<AutoUserSlotExtend> usersPlay = new ArrayList();
        Map<String, AutoUserSlotExtend> map = this.usersAuto;
        synchronized (map) {
            for (AutoUserSlotExtend user : this.usersAuto.values()) {
                boolean play = user.incCount();
                if (play)
                    usersPlay.add(user);
            }
        }
        int numThreads = usersPlay.size() / 100 + 1;
        for (int i = 1; i <= numThreads; i++) {
            int fromIndex = (i - 1) * 100;
            int toIndex = i * 100;
            if (toIndex > usersPlay.size()) {
                toIndex = usersPlay.size();
            }
            ArrayList tmp = new ArrayList(usersPlay.subList(fromIndex, toIndex));
            PlayListSlotExtendTask task = new PlayListSlotExtendTask(tmp);
            this.executor.execute(task);
        }
        usersPlay.clear();
    }

    public void playListSlotExtend(List<AutoUserSlotExtend> users) {
        for (AutoUserSlotExtend user : users) {
            short result = play(user.getUser(), user.getBet());
            if ((result == 3) || (result == 4) || (result == 101) || (result == 102) || (result == 100)) {
                forceStopAutoPlay(user.getUser());

            } else if (result == 0) {
                user.setMaxCount(4);
            } else
                user.setMaxCount(8);
        }
        users.clear();
    }

    public boolean joinRoom(User user) {
        boolean result = super.joinRoom(user);
        if (result) {
            user.setProperty("MGROOM_SLOT_EXTEND_INFO", this);
        }
        return result;
    }

    @Override
    protected void checkResetPot() {
        //todo : check reset pot
    }


    private String buildDescription(long totalBet, long totalPrizes, short result) {
        if (totalBet == 0L) {
            return resultToString(result) + ": " + totalPrizes;
        }
        return "Quay: " + totalBet + ", " + resultToString(result) + ": " + totalPrizes;
    }

    private String resultToString(short result) {
        switch (result) {
            case 3:
                return "Nổ hũ";

            case 4:
                return "Nổ hũ X2";

            case 1:
                return "Thắng";

            case 2:
                return "Thắng lớn";
        }

        return "Trượt";
    }

    private final class PlayListSlotExtendTask extends Thread {
        private List<AutoUserSlotExtend> users;

        private PlayListSlotExtendTask(List<AutoUserSlotExtend> users) {
            this.users = users;
            setName("AutoPlaySlotExtend");
        }

        public void run() {
            MGRoomSlotExtend.this.playListSlotExtend(this.users);
        }
    }


    private final class GameLoopTask implements Runnable {
        private GameLoopTask() {
        }

        public void run() {
            try {
                MGRoomSlotExtend.this.gameLoop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int[] getCollectItemJackpot() {
        int[] collection = new int[9];
        for (int j = 0; j < MAX_NUMBER_GET_FAIL; j++) {
            for (int i = 0; i < 9; i++) {
                collection[i] = arrItemValue[RandomUtil.randInt(0, arrItemValue.length - 1)];
            }
            for (int[] line : arrLines) {
                int[] arrItem = {collection[line[0]], collection[line[1]], collection[line[2]]};
                GroupItem groupItem = new GroupItem(arrItem, 100);
                if (groupItem.isJackpot()) {
                    return collection;
                }
            }
        }
        int[] retVal = {1, 1, 1, 1, 5, 3, 4, 5, 4};
        return retVal;
    }

    private boolean isJackpotBroken() {
        return this.fund > this.initPotValue * 2L;
    }

    private int[] initShuffleCollectItem() {
        int[] collection = new int[9];
        try {
            int id1 = RandomUtil.randInt(0, arrItemValue1.length);
            int id2 = RandomUtil.randInt(0, arrItemValue2.length);
            int id3 = RandomUtil.randInt(0, arrItemValue3.length);
            collection[0] = arrItemValue1[id1];
            collection[1] = arrItemValue1[(id1 + 1) % arrItemValue1.length];
            collection[2] = arrItemValue1[(id1 + 2) % arrItemValue1.length];

            collection[3] = arrItemValue2[id2];
            collection[4] = arrItemValue2[(id2 + 1) % arrItemValue2.length];
            collection[5] = arrItemValue2[(id2 + 2) % arrItemValue2.length];

            collection[6] = arrItemValue3[id3];
            collection[7] = arrItemValue3[(id3 + 1) % arrItemValue3.length];
            collection[8] = arrItemValue3[(id3 + 2) % arrItemValue3.length];
        } catch (Exception e) {
            int[] v = {3, 4, 5, 3, 2, 3, 4, 4, 2};
            return v;
        }
        return collection;
    }

    private int[] getCollectItemFail() {
        int[] collection = new int[9];
        for (int j = 0; j < MAX_NUMBER_GET_FAIL; j++) {
            for (int i = 0; i < 9; i++) {
                collection[i] = arrItemValue[RandomUtil.randInt(0, arrItemValue.length - 1)];
            }
            double prizeAmount = 0;
            for (int[] line : arrLines) {
                int[] arrItem = {collection[line[0]], collection[line[1]], collection[line[2]]};
                GroupItem groupItem = new GroupItem(arrItem);
                if (groupItem.isJackpot()) {
                    prizeAmount += 1;
                    break;
                } else {
                    prizeAmount += groupItem.getPrizeAmount();
                }

            }
            if (prizeAmount <= 0) {
                return collection;
            }
        }
        int rd = RandomUtil.randInt(0, 6);
        int[] rv = retVal[rd];
        return rv;

    }

    private int[] getItemValueByLineIndex(int[] showItem, int index) {
        int[] line = arrLines[index];
        int[] retVal = {showItem[line[0]], showItem[line[1]], showItem[line[2]]};
        return retVal;
    }

}

