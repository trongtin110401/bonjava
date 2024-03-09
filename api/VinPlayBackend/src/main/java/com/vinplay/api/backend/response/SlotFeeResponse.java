/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.api.backend.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    // Constructors, getters, and setters...

    @JsonProperty("Cowboy")
    public int getCowboy() {
        return Cowboy;
    }

    @JsonProperty("FastAndFurious")
    public int getFastAndFurious() {
        return FastAndFurious;
    }

    @JsonProperty("LadyNight")
    public int getLadyNight() {
        return LadyNight;
    }

    @JsonProperty("Caribe")
    public int getCaribe() {
        return Caribe;
    }

    @JsonProperty("BongLaiCac")
    public int getBongLaiCac() {
        return BongLaiCac;
    }

    @JsonProperty("Halloween")
    public int getHalloween() {
        return Halloween;
    }

    @JsonProperty("LasVegas")
    public int getLasVegas() {
        return LasVegas;
    }

    @JsonProperty("SexyDance")
    public int getSexyDance() {
        return SexyDance;
    }

    @JsonProperty("LienMinh")
    public int getLienMinh() {
        return LienMinh;
    }


    public void setCowboy(int cowboy) {
        Cowboy = cowboy;
    }

    public void setFastAndFurious(int fastAndFurious) {
        FastAndFurious = fastAndFurious;
    }

    public void setLadyNight(int ladyNight) {
        LadyNight = ladyNight;
    }

    public void setCaribe(int caribe) {
        Caribe = caribe;
    }

    public void setBongLaiCac(int bongLaiCac) {
        BongLaiCac = bongLaiCac;
    }

    public void setHalloween(int halloween) {
        Halloween = halloween;
    }

    public void setLasVegas(int lasVegas) {
        LasVegas = lasVegas;
    }

    public void setSexyDance(int sexyDance) {
        SexyDance = sexyDance;
    }

    public void setLienMinh(int lienMinh) {
        LienMinh = lienMinh;
    }
}

