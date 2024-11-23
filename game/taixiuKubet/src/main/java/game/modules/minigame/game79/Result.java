package game.modules.minigame.game79;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Result {
    @JsonProperty("Dice1")
    private int dice1;

    @JsonProperty("Dice2")
    private int dice2;

    @JsonProperty("Dice3")
    private int dice3;

    // Getters and setters

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
}

