/*
 * Decompiled with CFR 0.144.
 */
package game.entity.entitiesxocdia;

public class RefundModel {
    public long moneyRefund;
    public long currentMoney;
    public String pots;
    public String moneyRfPots;

    public RefundModel(long moneyRefund, long currentMoney, String pots, String moneyRfPots) {
        this.moneyRefund = moneyRefund;
        this.currentMoney = currentMoney;
        this.pots = pots;
        this.moneyRfPots = moneyRfPots;
    }

    public long getMoneyRefund() {
        return moneyRefund;
    }

    public void setMoneyRefund(long moneyRefund) {
        this.moneyRefund = moneyRefund;
    }

    public long getCurrentMoney() {
        return currentMoney;
    }

    public void setCurrentMoney(long currentMoney) {
        this.currentMoney = currentMoney;
    }

    public String getPots() {
        return pots;
    }

    public void setPots(String pots) {
        this.pots = pots;
    }

    public String getMoneyRfPots() {
        return moneyRfPots;
    }

    public void setMoneyRfPots(String moneyRfPots) {
        this.moneyRfPots = moneyRfPots;
    }

    public RefundModel() {
    }
}

