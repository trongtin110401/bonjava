package game.modules.lobby;

public class naptmp {
    private String nickname;
    private long tongnap;
    private long napthe;
    private long napadmin;
    private long napbank;
    private long napmomo;

    public naptmp() {
    }


    public naptmp(String nickname, long tongnap, long napthe, long napadmin) {
        this.nickname = nickname;
        this.tongnap = tongnap;
        this.napthe = napthe;
        this.napadmin = napadmin;
    }

    public long getNapbank() {
        return napbank;
    }

    public void setNapbank(long napbank) {
        this.napbank = napbank;
    }

    public long getNapmomo() {
        return napmomo;
    }

    public void setNapmomo(long napmomo) {
        this.napmomo = napmomo;
    }

    public long getNapadmin() {
        return napadmin;
    }

    public void setNapadmin(long napadmin) {
        this.napadmin = napadmin;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getTongnap() {
        return tongnap;
    }

    public void setTongnap(long tongnap) {
        this.tongnap = tongnap;
    }

    public long getNapthe() {
        return napthe;
    }

    public void setNapthe(long napthe) {
        this.napthe = napthe;
    }

    @Override
    public String toString() {
        return "naptmp{" +
                "nickname='" + nickname + '\'' +
                ", tongnap=" + tongnap +
                ", napthe=" + napthe +
                ", napadmin=" + napadmin +
                ", napbank=" + napbank +
                ", napmomo=" + napmomo +
                '}';
    }
}
