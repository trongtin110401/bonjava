/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.modules.minigame;


public class LineWin {
    private int line;
    private double prizeAmount;
    private boolean isJackpot;

    /**
     * @return the line
     */
    public int getLine() {
        return line;
    }

    /**
     * @param line the line to set
     */
    public void setLine(int line) {
        this.line = line;
    }

    /**
     * @return the isJackpot
     */
    public boolean isJackpot() {
        return isJackpot;
    }

    /**
     * @param isJackpot the isJackpot to set
     */
    public void setJackpot(boolean isJackpot) {
        this.isJackpot = isJackpot;
    }

    /**
     * @return the prizeAmount
     */
    public double getPrizeAmount() {
        return prizeAmount;
    }

    /**
     * @param prizeAmount the prizeAmount to set
     */
    public void setPrizeAmount(double prizeAmount) {
        this.prizeAmount = prizeAmount;
    }

    @Override
    public String toString() {
        return "LineWin{" +
                "line=" + line +
                ",amount=" + prizeAmount +
                ",jp=" + isJackpot +
                '}';
    }
}
