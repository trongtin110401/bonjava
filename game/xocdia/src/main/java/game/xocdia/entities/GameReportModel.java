package game.xocdia.entities;

import java.util.Objects;

public class GameReportModel {
    String name;
    long moneyBet;
    int potId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameReportModel that = (GameReportModel) o;
        return potId == that.potId && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, potId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMoneyBet() {
        return moneyBet;
    }

    public void setMoneyBet(long moneyBet) {
        this.moneyBet = moneyBet;
    }

    public int getPotId() {
        return potId;
    }

    public void setPotId(int potId) {
        this.potId = potId;
    }
}
