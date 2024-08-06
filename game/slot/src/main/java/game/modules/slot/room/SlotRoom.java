/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  bitzero.server.entities.User
 *  bitzero.server.extensions.data.BaseMsg
 *  bitzero.util.ExtensionUtility
 *  bitzero.util.common.business.Debug
 *  com.vinplay.dal.service.BroadcastMessageService
 *  com.vinplay.dal.service.CacheService
 *  com.vinplay.dal.service.MiniGameService
 *  com.vinplay.dal.service.SlotMachineService
 *  com.vinplay.dal.service.impl.BroadcastMessageServiceImpl
 *  com.vinplay.dal.service.impl.CacheServiceImpl
 *  com.vinplay.dal.service.impl.MiniGameServiceImpl
 *  com.vinplay.dal.service.impl.SlotMachineServiceImpl
 *  com.vinplay.usercore.service.UserService
 *  com.vinplay.usercore.service.impl.UserServiceImpl
 *  com.vinplay.vbee.common.exceptions.KeyNotFoundException
 */
package game.modules.slot.room;

import bitzero.server.entities.User;
import bitzero.server.extensions.data.BaseMsg;
import bitzero.util.ExtensionUtility;
import com.vinplay.dal.common.BroadCastUserState;
import com.vinplay.dal.service.BroadcastMessageService;
import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.MiniGameService;
import com.vinplay.dal.service.SlotMachineService;
import com.vinplay.dal.service.impl.BroadcastMessageServiceImpl;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.dal.service.impl.MiniGameServiceImpl;
import com.vinplay.dal.service.impl.SlotMachineServiceImpl;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.utils.SlotNohuObject;
import com.vinplay.vbee.common.exceptions.KeyNotFoundException;
import com.vinplay.vbee.common.models.slot.SlotFreeSpin;
import game.modules.slot.SlotModule;
import game.modules.slot.entities.slot.AutoUser;
import game.util.ConfigGame;

import java.util.*;

public abstract class SlotRoom {
    protected SlotModule module;
    public static final String CACHE_NAME_USER_SPOT = "user_force_jackpot_";
    public static final String CACHE_BET_VALUE_SLOT = "bet_value_jackpot_";
    public static final String CACHE_JACK_POT_VALUE_SLOT = "pot_value_jackpot";

    protected byte id;
    protected String gameName;
    protected String cacheFreeSpinName;
    protected String name;
    protected List<User> users = new ArrayList<>();
    protected long pot;
    protected long initJackpotValues;
    protected int betValue;
    protected short moneyType;
    protected String moneyTypeStr;

    protected String cachePercentFeeName;
    protected int percentFee = 0;
    protected String cachePercentJackpot;
    protected float percentJackpot = 0;
    protected int countHu = -1;
    protected int countNoHuX2 = 0;
    protected boolean huX2 = false;
    protected UserService userService = new UserServiceImpl();
    protected SlotMachineService slotService = new SlotMachineServiceImpl();
    protected BroadcastMessageService broadcastMsgService = new BroadcastMessageServiceImpl();
    protected MiniGameService miniGameService = new MiniGameServiceImpl();
    protected CacheService cacheService = new CacheServiceImpl();
    protected final Map<String, AutoUser> usersAuto = new HashMap<>();

    protected Random random = new Random();

    public SlotRoom(byte id, String gameName, String room, int betValue, short moneyType, long pot, long fun, long initPotValue) {
        this.id = id;
        this.gameName = gameName;
        this.name = room;
        this.betValue = betValue;
        this.moneyType = moneyType;
        this.pot = pot;
        this.initJackpotValues = initPotValue;
        this.moneyTypeStr = this.moneyType == 1 ? "vin" : "xu";

        cacheService.setValue(CACHE_JACK_POT_VALUE_SLOT + name, pot);
        this.initJackpotValues = initPotValue;
        this.moneyTypeStr = this.moneyType == 1 ? "vin" : "xu";
        try {
            this.countHu = this.cacheService.getValueInt(name + "_count_hu");
            this.countNoHuX2 = this.cacheService.getValueInt(name + "_count_no_hu_x2");
            this.calculatHuX2();
        } catch (KeyNotFoundException ignored) {
        }

        setFunValue(fun);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean joinRoom(User user) {
        List<User> list = this.users;
        synchronized (list) {
            if (!this.users.contains(user)) {
                this.users.add(user);
                return true;
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean quitRoom(User user) {
        List<User> list = this.users;
        synchronized (list) {
            if (this.users.contains(user)) {
                this.users.remove(user);
                return true;
            }
        }
        return false;
    }

    public void sendMessageToRoom(BaseMsg msg) {
        ArrayList<User> usersCopy = new ArrayList<>(this.users);
        for (User user : usersCopy) {
            ExtensionUtility.getExtension().send(msg, user);
        }
    }

    public void sendNotifyNoHu(String username, byte type, long totalPrizes, String gn) {
        SlotNohuObject msg = new SlotNohuObject(username, type, totalPrizes, gn);
        CacheService cacheService = new CacheServiceImpl();
        cacheService.setObject("notifyNohu", msg);
    }

    public void noHuX2() {

    }

    private void calculatHuX2() {
    }

    public int getBetValue() {
        return this.betValue;
    }

    public void forceStopAutoPlay(User user) {
        user.removeProperty("auto_" + this.gameName);
        BroadCastUserState.popBroadCast(user.getName());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void autoPlay(User user, String lines, short resultFirstPlay) {
        synchronized (this.usersAuto) {
            if (this.usersAuto.containsKey(user.getName())) {
                AutoUser entry = this.usersAuto.get(user.getName());
                this.forceStopAutoPlay(entry.getUser());
            }
            AutoUser autoUser = new AutoUser(user, lines);
            if (resultFirstPlay == 0) {
                autoUser.setMaxCount(5);
            } else if (resultFirstPlay == 5) {
                autoUser.setMaxCount(20);
            } else {
                autoUser.setMaxCount(8);
            }
            this.usersAuto.put(user.getName(), autoUser);
            user.setProperty("auto_" + this.gameName, true);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stopAutoPlay(User user) {
        synchronized (this.usersAuto) {
            if (this.usersAuto.containsKey(user.getName())
                    && this.usersAuto.get(user.getName()).getUser().getUniqueId() == user.getUniqueId()) {
                this.usersAuto.remove(user.getName());
                user.removeProperty("auto_" + this.gameName);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void userMinimize(User user) {
        synchronized (this.usersAuto) {
            AutoUser entry;
            if (this.usersAuto.containsKey(user.getName())
                    && (entry = this.usersAuto.get(user.getName())).getUser().getUniqueId() == user.getUniqueId()) {
                entry.setMinimize(true);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void userMaximize(User user) {
        synchronized (this.usersAuto) {
            AutoUser entry;
            if (this.usersAuto.containsKey(user.getName())
                    && (entry = this.usersAuto.get(user.getName())).getUser().getUniqueId() == user.getUniqueId()) {
                entry.setMinimize(false);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isUserMinimize(User user) {
        synchronized (this.usersAuto) {
            AutoUser entry;
            if (this.usersAuto.containsKey(user.getName())
                    && (entry = this.usersAuto.get(user.getName())).getUser().getUniqueId() == user.getUniqueId()) {
                return entry.isMinimize();
            }
        }
        return false;
    }

    void setPercentFee() {
        try {
            this.cachePercentFeeName = gameName + "_PERCENT_FEE";
            this.percentFee = cacheService.getValueInt(cachePercentFeeName);
        } catch (Exception ex) {
            this.percentFee = 1;
            cacheService.setValue(cachePercentFeeName, percentFee);
        }
    }

    void setPercentJackpot() {
        this.cachePercentJackpot = gameName + "_PERCENT_JACKPOT" + "_" + betValue;

        float configValue = ConfigGame.getFloatValue(cachePercentJackpot, 0.5f);
        this.percentJackpot = cacheService.getValueFloat(cachePercentJackpot, 0.5f);

        if (configValue != this.percentJackpot) {
            this.percentJackpot = configValue;
            cacheService.setValue(cachePercentJackpot, configValue);
        }
    }

    /**
     * Kiểm tra xem một người chơi có trúng jackpot ngẫu nhiên hay không
     *
     * @param percentage Phần trăm cơ hội trúng jackpot
     * @return true nếu người chơi trúng jackpot, ngược lại trả về false
     */
    public boolean randomJackpot(double percentage) {
        if (percentage < 0.0) {
            throw new IllegalArgumentException("Percentage must be greater than 0.0");
        }

        if (percentage == 0) {
            return false;
        }

        int totalNumbers = (int) Math.ceil(100 / percentage);
        int jackpotNumber = random.nextInt(totalNumbers);

        // Generate a random number and check if it matches the jackpot number
        int randomNumber = random.nextInt(totalNumbers);
        return randomNumber == jackpotNumber;
    }

    protected abstract void gameLoop();

    protected abstract void checkResetPot();


    protected abstract void playListAuto(List<AutoUser> var1);

    protected String buildDescription(long totalBet, long totalPrizes, short result) {
        if (totalBet == 0L) {
            return this.resultToString(result) + ": " + totalPrizes;
        }
        return "Quay: " + totalBet + ", " + this.resultToString(result) + ": " + totalPrizes;
    }

    protected String resultToString(short result) {
        switch (result) {
            case 3: {
                return "Nổ hũ";
            }
            case 4: {
                return "Nổ hũ X2";
            }
            case 1: {
                return "Thắng";
            }
            case 5: {
                return "Bonus";
            }
            case 2: {
                return "Thắng lớn";
            }
            default:
                return "Trượt";
        }
    }

    public byte getId() {
        return this.id;
    }

    public static class ResultSlot {
        public static final short SYSTEM_ERROR = 100;
        public static final short INVALID_BET_VALUE = 101;
        public static final short NOT_ENOUGH_MONEY = 102;
        public static final short MISSED = 0;
        public static final short WIN = 1;
        public static final short BIG_WIN = 2;
        public static final short JACKPOT = 3;
        public static final short JACKPOT_X2 = 4;
        public static final short BONUS_GAME = 5;
        public static final short FREE_SPIN = 6;
        public static final short LOG_FREE_SPIN = 7;
    }

    protected final class GameLoopTask implements Runnable {

        GameLoopTask() {
        }

        @Override
        public void run() {
            try {
                SlotRoom.this.gameLoop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected final class CheckResetPot implements Runnable {

        CheckResetPot() {

        }

        @Override
        public void run() {
            try {
                SlotRoom.this.checkResetPot();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected final class PlayListAutoUserTask extends Thread {
        private final List<AutoUser> users;

        PlayListAutoUserTask(List<AutoUser> users) {
            this.users = users;
            this.setName(SlotRoom.this.gameName + "_" + SlotRoom.this.betValue + "_AutoPlayTask");
        }

        @Override
        public void run() {
            SlotRoom.this.playListAuto(this.users);
        }
    }

    protected int getNumOfFreeSpin(String username) {
        return getFreeSpinInfo(username).getNum();
    }

    protected SlotFreeSpin getFreeSpinInfo(String username) {
        try {
            return slotService.getLuotQuayFreeSlot(cacheFreeSpinName, username);
        } catch (Exception ex) {
            return new SlotFreeSpin();
        }
    }

    protected long getFunValue() {
        String key = gameName + "_" + moneyTypeStr + "_" + betValue;
        return cacheService.getValueLong(key, 0);
    }

    protected void setFunValue(long value) {
        String key = gameName + "_" + moneyTypeStr + "_" + betValue;
        cacheService.setValue(key, value);
    }

    protected synchronized void updateFunValue(long value) {
        setFunValue(getFunValue() + value);
    }
}

