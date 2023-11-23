package game.modules.lobby;

public class CodeTT {
    private String code;
    private int money;
    private String timelog;
    private int stop;

    public CodeTT() {
    }


    public CodeTT(String code, int money, String timelog, int stop) {
        this.code = code;
        this.money = money;
        this.timelog = timelog;
        this.stop = stop;
    }

    public String getTimelog() {
        return timelog;
    }

    public void setTimelog(String timelog) {
        this.timelog = timelog;
    }

    public int getStop() {
        return stop;
    }

    public void setStop(int stop) {
        this.stop = stop;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    @Override
    public String toString() {
        return "CodeTT{" +
                "code='" + code + '\'' +
                ", money=" + money +
                ", timelog='" + timelog + '\'' +
                ", stop=" + stop +
                '}';
    }
}

