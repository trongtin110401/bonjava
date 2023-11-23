package game.modules.lobby;

public class checknapEnity {
    private String nickname;
    private String hinhtruc;
    private long sotien;

    public checknapEnity() {
    }

    public checknapEnity(String nickname, String hinhtruc, long sotien) {
        this.nickname = nickname;
        this.hinhtruc = hinhtruc;
        this.sotien = sotien;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHinhtruc() {
        return hinhtruc;
    }

    public void setHinhtruc(String hinhtruc) {
        this.hinhtruc = hinhtruc;
    }

    public long getSotien() {
        return sotien;
    }

    public void setSotien(long sotien) {
        this.sotien = sotien;
    }

    @Override
    public String toString() {
        return "checknapEnity{" +
                "nickname='" + nickname + '\'' +
                ", hinhtruc='" + hinhtruc + '\'' +
                ", sotien=" + sotien +
                '}';
    }
}
