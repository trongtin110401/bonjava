/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.vbee.common.models.cache.ThanhDuTXModel
 *  com.vinplay.vbee.common.response.BaseResponseModel
 *  com.vinplay.vbee.common.utils.VinPlayUtils
 */
package com.vinplay.api.processors.minigame.response;

import com.vinplay.vbee.common.models.cache.ThanhDuTXModel;
import com.vinplay.vbee.common.response.BaseResponseModel;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import java.util.ArrayList;
import java.util.List;

public class TopThanhDuResponse
extends BaseResponseModel {
    public static String[] winPrizesDaily = new String[]{"500.000", "200.000", "100.000"};
    public static String[] lossPrizesDaily = new String[]{"500.000", "200.000", "100.000"};
    private static String[] winPrizesMonthly = new String[]{"iPhone 11 Pro Max"};
    private static String[] lossPrizesMonthly = new String[]{"iPhone 11 Pro Max"};
    private static String[] winPrizesMonthlyVin = new String[]{"18,000,000 Vin"};
    private static String[] lossPrizesMonthlyVin = new String[]{"22,000,000 Vin"};
    private List<TopThanhDuModel> results = new ArrayList<TopThanhDuModel>();

    public TopThanhDuResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    public void setTopThanhDu(List<ThanhDuTXModel> input, String[] prizes) {
        for (int i = 0; i < input.size(); ++i) {
            ThanhDuTXModel entry = input.get(i);
            TopThanhDuModel model = new TopThanhDuModel();
            model.username = entry.username;
            model.number = entry.number;
            model.totalMoney = entry.totalValue;
            model.referenceId = entry.currentReferenceId;
            if (i < prizes.length) {
                model.prize = prizes[i];
            }
            this.results.add(model);
        }
    }

    public static String[] getWinPrizesMonthly(String platform) {
        if (VinPlayUtils.isMobile((String)platform)) {
            return winPrizesMonthlyVin;
        }
        return winPrizesMonthly;
    }

    public static String[] getLossPrizesMonthlyVin(String platform) {
        if (VinPlayUtils.isMobile((String)platform)) {
            return lossPrizesMonthlyVin;
        }
        return lossPrizesMonthly;
    }

    public List<TopThanhDuModel> getResults() {
        return this.results;
    }

    public class TopThanhDuModel {
        public String username;
        public int number;
        public long totalMoney;
        public long referenceId;
        public String prize = "";
    }

}

