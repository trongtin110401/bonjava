package game.modules.minigame.entities;

public class UserRoomInfo {
    private String username;
    private long currentMoney;
    private String avatar;
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getCurrentMoney() {
        return currentMoney;
    }

    public void setCurrentMoney(long currentMoney) {
        this.currentMoney = currentMoney;
    }


    public UserRoomInfo(String username, long currentMoney, String avatar) {
        this.username = username;
        this.currentMoney = currentMoney;
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
