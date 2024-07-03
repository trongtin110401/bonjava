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

import bitzero.server.api.IBZApi;
import bitzero.server.entities.User;
import bitzero.server.extensions.data.BaseMsg;
import bitzero.util.ExtensionUtility;
import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.CacheServiceImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class MGRoom {
    public static final String MGROOM_TAI_XIU_INFO = "MGROOM_TAI_XIU_INFO";
    public static final String MGROOM_MINI_POKER_INFO = "MGROOM_MINI_POKER_INFO";
    public static final String MGROOM_BAU_CUA_INFO = "MGROOM_BAU_CUA_TO2_INFO";
    public static final String MGROOM_CAO_THAP_INFO = "MGROOM_CAO_THAP_INFO";
    public static final String MGROOM_POKEGO_INFO = "MGROOM_POKEGO_INFO";
    protected String name;
    protected List<User> users = new ArrayList<User>();

    protected int betValue;

    public MGRoom(String name, int betValue, long fund, short moneyType) {
        this.name = name;
        this.betValue = betValue;
        this.moneyType = moneyType;
        this.moneyTypeStr = moneyType == 1 ? "vin" : "xu";

        setFunValue(fund);
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

    public List<User> getUsers() {
        return users;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
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
            ExtensionUtility.getExtension().send(msg, user);
        }
    }

    /**
     * L?y giá tr? qu? hi?n t?i
     *
     * @return Giá tr? jackpot hi?n t?i
     */
    protected long getFunValue() {
        return cacheService.getValueLong(name, 0);
    }

    /**
     * C?p nh?t giá tr? qu? hi?n t?i
     *
     * @param value Giá tr? c?n c?p nh?t
     */
    protected void setFunValue(long value) {
        cacheService.setValue(name, value);
    }

    /**
     * C?p nh?t giá tr? qu? hi?n t?i
     *
     * @param value Giá tr? c?n c?p nh?t
     */
    protected void updateFunValue(long value) {
        setFunValue(getFunValue() + value);
    }

    protected CacheService cacheService = new CacheServiceImpl();

    protected short moneyType = 1;
    protected String moneyTypeStr;
}

