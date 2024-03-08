/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.api.backend.response;

import com.vinplay.vbee.common.response.BaseResponseModel;

public class SlotFeeResponse extends BaseResponseModel {
    private int Cowboy = 0;
    private int FastAndFurious = 0;
    private int LadyNight = 0;
    private int Caribe = 0;
    private int BongLaiCac = 0;
    private int Halloween = 0;
    private int LasVegas = 0;
    private int SexyDance = 0;
    private int LienMinh = 0;

    public SlotFeeResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    public int getCowboy() {
        return Cowboy;
    }

    public void setCowboy(int cowboy) {
        Cowboy = cowboy;
    }

    public int getFastAndFurious() {
        return FastAndFurious;
    }

    public void setFastAndFurious(int fastAndFurious) {
        FastAndFurious = fastAndFurious;
    }

    public int getLadyNight() {
        return LadyNight;
    }

    public void setLadyNight(int ladyNight) {
        LadyNight = ladyNight;
    }

    public int getCaribe() {
        return Caribe;
    }

    public void setCaribe(int caribe) {
        Caribe = caribe;
    }

    public int getBongLaiCac() {
        return BongLaiCac;
    }

    public void setBongLaiCac(int bongLaiCac) {
        BongLaiCac = bongLaiCac;
    }

    public int getHalloween() {
        return Halloween;
    }

    public void setHalloween(int halloween) {
        Halloween = halloween;
    }

    public int getLasVegas() {
        return LasVegas;
    }

    public void setLasVegas(int lasVegas) {
        LasVegas = lasVegas;
    }

    public int getSexyDance() {
        return SexyDance;
    }

    public void setSexyDance(int sexyDance) {
        SexyDance = sexyDance;
    }

    public int getLienMinh() {
        return LienMinh;
    }

    public void setLienMinh(int lienMinh) {
        LienMinh = lienMinh;
    }
}

