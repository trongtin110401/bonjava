/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  bitzero.server.api.IBZApi
 *  bitzero.server.entities.User
 *  bitzero.server.extensions.data.BaseMsg
 *  bitzero.util.ExtensionUtility
 */
package game.modules.minigame.room;

import bitzero.server.entities.User;
import bitzero.server.extensions.data.BaseMsg;
import bitzero.util.ExtensionUtility;
import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.utils.SlotNohuObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class MGRoom {
    public static final String MGROOM_TAI_XIU_INFO = "MGROOM_TAI_XIU_INFO";
    public static final String MGROOM_MINI_POKER_INFO = "MGROOM_MINI_POKER_INFO";
    public static final String MGROOM_BAU_CUA_INFO = "MGROOM_BAU_CUA_INFO";
    public static final String MGROOM_CAO_THAP_INFO = "MGROOM_CAO_THAP_INFO";
    public static final String MGROOM_POKEGO_INFO = "MGROOM_POKEGO_INFO";

    public static final String CACHE_NAME_USER_SPOT = "user_force_jackpot_";
    public static final String CACHE_BET_VALUE_SLOT = "bet_value_jackpot_";
    public static final String CACHE_JACK_POT_VALUE_SLOT = "pot_value_jackpot";

    protected String name;
    protected String gameName;
    protected List<User> users = new ArrayList<User>();

    protected CacheService cacheService = new CacheServiceImpl();

    protected Random random = new Random();

    protected int percentFee = 0;
    protected String cachePercentFeeName;
    protected String cachePercentJackpot;
    protected int percentJackpot = 0;

    public MGRoom(String name) {
        this.name = name;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean joinRoom(User user) {
        List<User> list;

        List<User> list2 = list = this.users;
        synchronized (list2) {
            if (!this.users.contains((Object)user)) {
                this.users.add(user);
                return true;
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */

    // todo : xóa user khỏi
    //  list user ở trong room
    public boolean quitRoom(User user) {
        List<User> list;
        List<User> list2 = list = this.users;
        synchronized (list2) {
            if (this.users.contains((Object)user)) {
                this.users.remove((Object)user);
                return true;
            }
        }
        return false;
    }

    public void sendMessageToRoom(BaseMsg msg) {
        ArrayList<User> usersCopy = new ArrayList<User>(this.users);
        for (User user : usersCopy) {
            if (user == null) continue;
            ExtensionUtility.getExtension().send(msg, user);
        }
    }

    public void sendNotifyNoHu(String username, byte type, long totalPrizes, String gn) {
        SlotNohuObject msg = new SlotNohuObject(username, type, totalPrizes, gn);
        CacheService cacheService = new CacheServiceImpl();
        cacheService.setObject("notifyNohu", msg);

    }

//    public void sendMessageToUser(BaseMsg msg, String username) {
//        List<User> users = new ArrayList<>();
//        users.add(ExtensionUtility.getExtension().getApi().getUserByName(username));
//        if (users != null) {
//            ExtensionUtility.getExtension().sendUsers(msg, users);
//        }
//    }

    public void sendMessageToUser(BaseMsg msg, String username) {
//        List<User> users = new ArrayList<User>();
        List<User> users = ExtensionUtility.getExtension().getApi().getUserByName(username);
        if (users != null) {
            ExtensionUtility.getExtension().sendUsers(msg, users);
        }
    }

    public void sendMessageToUser(BaseMsg msg, User user) {
        if (user != null) {
            ExtensionUtility.getExtension().send(msg, user); // todo : gọi đến extension và gửi về client
        }
    }

    protected abstract void checkResetPot();

    /**
     * Thread reset lai so tien hu
     */
    protected final class CheckResetPot implements Runnable {

        protected CheckResetPot() {

        }

        @Override
        public void run() {
            try {
                MGRoom.this.checkResetPot();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void setPercentFee() {
        try {
            this.cachePercentFeeName = gameName + "_PERCENT_FEE";
            this.percentFee = cacheService.getValueInt(cachePercentFeeName);
        } catch (Exception ex) {
            this.percentFee = 2;
            cacheService.setValue(cachePercentFeeName, percentFee);
        }
    }

    void setPercentJackpot() {
        try {
            this.cachePercentJackpot = gameName + "_PERCENT_JACKPOT";
            this.percentJackpot = cacheService.getValueInt(cachePercentJackpot);
        } catch (Exception ex) {
            this.percentJackpot = 1;
            cacheService.setValue(cachePercentJackpot, percentJackpot);
        }
    }

    /**
     * Kiểm tra xem một người chơi có trúng jackpot ngẫu nhiên hay không
     *
     * @param percentage Phần trăm cơ hội trúng jackpot
     * @return true nếu người chơi trúng jackpot, ngược lại trả về false
     */
    public boolean randomJackpot(double percentage) {
        if (percentage <= 0.0) {
            throw new IllegalArgumentException("Percentage must be greater than 0.0");
        }

        int totalNumbers = (int) Math.ceil(100 / percentage);
        int jackpotNumber = random.nextInt(totalNumbers);

        // Generate a random number and check if it matches the jackpot number
        int randomNumber = random.nextInt(totalNumbers);
        return randomNumber == jackpotNumber;
    }

}

