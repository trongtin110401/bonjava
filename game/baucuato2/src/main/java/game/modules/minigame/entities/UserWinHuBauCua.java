package game.modules.minigame.entities;

import java.util.Base64;

public class UserWinHuBauCua {
    String userName;
    long moneyWin;

    public String getUserName() {
        return (userName);
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getMoneyWin() {
        return moneyWin;
    }

    public void setMoneyWin(long moneyWin) {
        this.moneyWin = moneyWin;
    }

    public UserWinHuBauCua(String userName, long moneyWin) {
        this.userName = userName;
        this.moneyWin = moneyWin;
    }

    @Override
    public String toString() {
        return "UserWinHuBauCua{" +
                "userName='" + userName + '\'' +
                ", moneyWin=" + moneyWin +
                '}';
    }
}
