package game.modules.minigame.entities;

import org.python.parser.ast.Str;

import java.io.Serializable;
import java.util.List;

public class HuBauCuaWinTransaction implements Serializable {
    long session;
    String time;
    int potId;
    long totalMoney;
    List<UserWinHuBauCua> userWinHuBauCuaList;

    public HuBauCuaWinTransaction() {
    }

    @Override
    public String toString() {
        return "HuBauCuaWinTransaction{" +
                "session='" + session + '\'' +
                ", time='" + time + '\'' +
                ", potId='" + potId + '\'' +
                ", totalMoney='" + totalMoney + '\'' +
                ", userWinHuBauCuaList=" + userWinHuBauCuaList +
                '}';
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public HuBauCuaWinTransaction(long session, String time, int potId, long totalMoney, List<UserWinHuBauCua> userWinHuBauCuaList) {
        this.session = session;
        this.time = time;
        this.potId = potId;
        this.totalMoney = totalMoney;
        this.userWinHuBauCuaList = userWinHuBauCuaList;
    }

    public int getPotId() {
        return potId;
    }

    public void setPotId(int potId) {
        this.potId = potId;
    }

    public long getSession() {
        return session;
    }

    public void setSession(long session) {
        this.session = session;
    }

    public long getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(long totalMoney) {
        this.totalMoney = totalMoney;
    }

    public List<UserWinHuBauCua> getUserWinHuBauCuaList() {
        return userWinHuBauCuaList;
    }

    public void setUserWinHuBauCuaList(List<UserWinHuBauCua> userWinHuBauCuaList) {
        this.userWinHuBauCuaList = userWinHuBauCuaList;
    }


}
