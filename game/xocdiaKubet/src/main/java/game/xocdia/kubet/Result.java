package game.xocdia.kubet;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Result {

    @JsonProperty("ChipsData")
    private String chipsData;

    @JsonProperty("Dice1")
    private int dice1;

    @JsonProperty("Dice2")
    private int dice2;

    @JsonProperty("Dice3")
    private int dice3;

    @JsonProperty("Dice4")
    private int dice4;

    @JsonProperty("BigGate")
    private int bigGate;

    @JsonProperty("SmallGate")
    private int smallGate;

    // Getters and Setters
    public String getChipsData() {
        return chipsData;
    }

    public void setChipsData(String chipsData) {
        this.chipsData = chipsData;
    }

    public int getDice1() {
        return dice1;
    }

    public void setDice1(int dice1) {
        this.dice1 = dice1;
    }

    public int getDice2() {
        return dice2;
    }

    public void setDice2(int dice2) {
        this.dice2 = dice2;
    }

    public int getDice3() {
        return dice3;
    }

    public void setDice3(int dice3) {
        this.dice3 = dice3;
    }

    public int getDice4() {
        return dice4;
    }

    public void setDice4(int dice4) {
        this.dice4 = dice4;
    }

    public int getBigGate() {
        return bigGate;
    }

    public void setBigGate(int bigGate) {
        this.bigGate = bigGate;
    }

    public int getSmallGate() {
        return smallGate;
    }

    public void setSmallGate(int smallGate) {
        this.smallGate = smallGate;
    }

}
