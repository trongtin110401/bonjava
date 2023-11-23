/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.vbee.common.utils.VinPlayUtils
 */
package com.vinplay.api.utils;

import com.vinplay.api.entities.CaoThapPrizes;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import java.util.ArrayList;
import java.util.List;

public class CaoThapUtils {
    private static final String PRIZE_DAY_TOP_1 = "2.000.000 Vin";
    private static final String PRIZE_DAY_TOP_2 = "1.000.000 Vin";
    private static final String PRIZE_DAY_TOP_3 = "500.000 Vin";
    private static final String PRIZE_DAY_TOP_4 = "";
    private static final String PRIZE_DAY_TOP_5 = "";
    private static final String PRIZE_DAY_TOP_6 = "";
    private static final String PRIZE_DAY_TOP_7 = "";
    private static final String PRIZE_DAY_TOP_8 = "";
    private static final String PRIZE_DAY_TOP_9 = "";
    private static final String PRIZE_DAY_TOP_10 = "";
    private static final String PRIZE_MONTH_TOP_1 = "Iphone 7 128Gb";
    private static final String PRIZE_MONTH_TOP_1_VIN = "18,000,000 Vin";
    private static final String PRIZE_MONTH_TOP_2 = "";
    private static final String PRIZE_MONTH_TOP_3 = "";
    private static final String PRIZE_MONTH_TOP_4 = "";
    private static final String PRIZE_MONTH_TOP_5 = "";
    private static final String PRIZE_MONTH_TOP_6 = "";
    private static final String PRIZE_MONTH_TOP_7 = "";
    private static final String PRIZE_MONTH_TOP_8 = "";
    private static final String PRIZE_MONTH_TOP_9 = "";
    private static final String PRIZE_MONTH_TOP_10 = "";
    private static final String MONEY_MONTH_TOP_1 = "18.000.000";
    private static final String MONEY_DAY_TOP_1 = "2.000.000";
    private static final String MONEY_DAY_TOP_2 = "1.000.000";
    private static final String MONEY_DAY_TOP_3 = "500.000";
    private static final String NUM_MONTH_TOP_1 = "1";
    private static final String NUM_DAY_TOP_1 = "30";
    private static final String NUM_DAY_TOP_2 = "30";
    private static final String NUM_DAY_TOP_3 = "30";

    public static List<CaoThapPrizes> getPrizes(String platform) {
        ArrayList<CaoThapPrizes> prizes = new ArrayList<CaoThapPrizes>();
        if (VinPlayUtils.isMobile((String)platform)) {
            prizes.add(new CaoThapPrizes(PRIZE_MONTH_TOP_1_VIN, MONEY_MONTH_TOP_1, NUM_MONTH_TOP_1));
        } else {
            prizes.add(new CaoThapPrizes(PRIZE_MONTH_TOP_1, MONEY_MONTH_TOP_1, NUM_MONTH_TOP_1));
        }
        prizes.add(new CaoThapPrizes(PRIZE_DAY_TOP_3, MONEY_DAY_TOP_3, "30"));
        prizes.add(new CaoThapPrizes("200.000 Vin", "200.000", "30"));
        prizes.add(new CaoThapPrizes("100.000 Vin", "100.000", "30"));
        return prizes;
    }

    public static String getPrize(int type, int stt, String platform) {
        String prize = "";
        if (type == 0) {
            switch (stt) {
                case 1: {
                    prize = PRIZE_DAY_TOP_3;
                    break;
                }
                case 2: {
                    prize = "200.000 Vin";
                    break;
                }
                case 3: {
                    prize = "100.000 Vin";
                    break;
                }
                case 4: {
                    prize = "";
                    break;
                }
                case 5: {
                    prize = "";
                    break;
                }
                case 6: {
                    prize = "";
                    break;
                }
                case 7: {
                    prize = "";
                    break;
                }
                case 8: {
                    prize = "";
                    break;
                }
                case 9: {
                    prize = "";
                    break;
                }
                case 10: {
                    prize = "";
                }
            }
        } else {
            switch (stt) {
                case 1: {
                    if (VinPlayUtils.isMobile((String)platform)) {
                        prize = PRIZE_MONTH_TOP_1_VIN;
                        break;
                    }
                    prize = PRIZE_MONTH_TOP_1;
                    break;
                }
                case 2: {
                    prize = "";
                    break;
                }
                case 3: {
                    prize = "";
                    break;
                }
                case 4: {
                    prize = "";
                    break;
                }
                case 5: {
                    prize = "";
                    break;
                }
                case 6: {
                    prize = "";
                    break;
                }
                case 7: {
                    prize = "";
                    break;
                }
                case 8: {
                    prize = "";
                    break;
                }
                case 9: {
                    prize = "";
                    break;
                }
                case 10: {
                    prize = "";
                }
            }
        }
        return prize;
    }
}

