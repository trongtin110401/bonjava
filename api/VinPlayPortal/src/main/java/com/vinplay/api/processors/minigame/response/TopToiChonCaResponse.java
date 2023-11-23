/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.vbee.common.models.minigame.baucua.ToiChonCa
 *  com.vinplay.vbee.common.response.BaseResponseModel
 */
package com.vinplay.api.processors.minigame.response;

import com.vinplay.vbee.common.models.minigame.baucua.ToiChonCa;
import com.vinplay.vbee.common.response.BaseResponseModel;
import java.util.ArrayList;
import java.util.List;

public class TopToiChonCaResponse
extends BaseResponseModel {
    private static String[] prizes = new String[]{"100.000 Vin", "50.000 Vin", "20.000 Vin", "10.000 Vin", "10.000 Vin"};
    private List<ToiChonCa> results = new ArrayList<ToiChonCa>();

    public TopToiChonCaResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    public List<ToiChonCa> getResults() {
        return this.results;
    }

    public void setResults(List<ToiChonCa> results) {
        this.results = results;
        for (int i = 0; i < prizes.length && i < results.size(); ++i) {
            ToiChonCa entry = results.get(i);
            entry.prize = prizes[i];
        }
    }
}

