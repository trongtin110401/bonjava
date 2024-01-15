package com.vinplay.dal.entities.taixiu;

import java.io.Serializable;

public class BetResult implements Serializable {
    private static final long serialVersionUID = 1L;
    private long zeroWhite;
    private long fourWhite;
    private long threeWhite;
    private long oneWhite;
    private long even;
    private long odd;


    public long getZeroWhite() {
        return zeroWhite;
    }

    public void setZeroWhite(long zeroWhite) {
        this.zeroWhite = zeroWhite;
    }

    public long getFourWhite() {
        return fourWhite;
    }

    public void setFourWhite(long fourWhite) {
        this.fourWhite = fourWhite;
    }

    public long getThreeWhite() {
        return threeWhite;
    }

    public void setThreeWhite(long threeWhite) {
        this.threeWhite = threeWhite;
    }

    public long getOneWhite() {
        return oneWhite;
    }

    public void setOneWhite(long oneWhite) {
        this.oneWhite = oneWhite;
    }

    public long getEven() {
        return even;
    }

    public void setEven(long even) {
        this.even = even;
    }

    public long getOdd() {
        return odd;
    }

    public void setOdd(long odd) {
        this.odd = odd;
    }
}
