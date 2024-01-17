/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line20basic;

public class Line20AwardsOnLine {
    private Line20Award award;
    private long money;
    private String lineName;

    public Line20AwardsOnLine(Line20Award award, long money, String lineName) {
        this.award = award;
        this.money = money;
        this.lineName = lineName;
    }

    public Line20Award getAward() {
        return this.award;
    }

    public void setAward(Line20Award award) {
        this.award = award;
    }

    public long getMoney() {
        return this.money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public String getLineName() {
        return this.lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getLineId() {
        return this.lineName.substring(4, this.lineName.length());
    }
}

