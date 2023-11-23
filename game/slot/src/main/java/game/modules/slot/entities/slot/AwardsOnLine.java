/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot;

public class AwardsOnLine<Award> {
    private Award award;
    private long money;
    private String lineName;

    public AwardsOnLine(Award award, long money, String lineName) {
        this.award = award;
        this.money = money;
        this.lineName = lineName;
    }

    public Award getAward() {
        return this.award;
    }

    public void setAward(Award award) {
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

