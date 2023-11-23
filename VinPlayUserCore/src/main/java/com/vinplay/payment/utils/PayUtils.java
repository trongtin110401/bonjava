/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.vbee.common.utils.DateTimeUtils
 *  org.json.JSONArray
 *  org.json.JSONObject
 */
package com.vinplay.payment.utils;

import com.vinplay.dichvuthe.service.AlertService;
import com.vinplay.dichvuthe.service.impl.AlertServiceImpl;
import com.vinplay.payment.entities.MerchantInfo;
import com.vinplay.payment.service.PaymentService;
import com.vinplay.payment.service.impl.PaymentServiceImpl;
import com.vinplay.usercore.dao.impl.GameConfigDaoImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.utils.DateTimeUtils;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class PayUtils {
    private static Map<String, MerchantInfo> merchantMap = new HashMap<String, MerchantInfo>();
    private static PaymentService pmService = new PaymentServiceImpl();
    private static AlertService alertSer = new AlertServiceImpl();
    private static List<String> receives = Arrays.asList("0986354389", "0984574749");
    public static final String TO_VIN = "0";
    public static final String FROM_VIN = "1";
    public static final int SUCCESS = 0;
    public static final int ERROR_SYSTEM = 1;
    public static final int MERCHANT_ID_INVALID = 2;
    public static final int CHECKSUM_INVALID = 3;
    public static final int PARAMS_INVALID = 4;
    public static final int NICKNAME_INVALID = 5;
    public static final int TOKEN_INVALID = 6;
    public static final int TOKEN_TIME_OUT = 7;
    public static final int NOT_ENOUGH_MONEY = 8;
    public static final int DUPLICATE_TRANS_ID = 9;
    public static final int LIMITED = 10;
    public static final int BLOCKED = 11;
    public static final int AMOUNT_INVALID = 12;

    //status payment
    public static final String STATUS_TRANS_PENDING = "pending";
    public static final String STATUS_TRANS_REJECT = "reject";
    public static final String STATUS_TRANS_ERROR = "error";
    public static final String STATUS_TRANS_SUCCESS = "success";

    public static void init() throws Exception {
        GameConfigDaoImpl dao = new GameConfigDaoImpl();
        JSONObject mcObj = new JSONObject(dao.getGameCommon("merchant"));
        JSONArray jArray = mcObj.getJSONArray("mc_info");
        if (jArray != null) {
            for (int i = 0; i < jArray.length(); ++i) {
                JSONObject jObj = jArray.getJSONObject(i);
                Iterator keys = jObj.keys();
                while (keys.hasNext()) {
                    String key = (String)keys.next();
                    JSONArray a = jObj.getJSONArray(key);
                    long moneyInDay = pmService.getTotalMoney(key, null, DateTimeUtils.getStartTimeToDay(), DateTimeUtils.getEndTimeToDay(), TO_VIN);
                    merchantMap.put(key, new MerchantInfo(key, a.getString(0), a.getString(1), a.getDouble(2), a.getDouble(3), a.getLong(4), a.getLong(5), a.getLong(6), a.getLong(7), moneyInDay, Calendar.getInstance().get(6), 0));
                }
            }
        }
    }

    public static boolean checkMerchantId(String merchantId) {
        return merchantMap.containsKey(merchantId);
    }

    public static boolean checkMerchantKey(String merchantId, String merchantKey) {
        return PayUtils.checkMerchantId(merchantId) && merchantMap.get(merchantId).getMerchantKey().equals(merchantKey);
    }

    public static MerchantInfo getMerchant(String merchantId) {
        if (PayUtils.checkMerchantId(merchantId)) {
            return merchantMap.get(merchantId);
        }
        return null;
    }

    public static String getMerchantKey(String merchantId) {
        if (PayUtils.checkMerchantId(merchantId)) {
            return merchantMap.get(merchantId).getMerchantKey();
        }
        return null;
    }

    public static boolean checkMoneyLimit(String nickname, MerchantInfo merchantInfo, long money, String type) throws Exception {
        if (type.equals(FROM_VIN)) {
            return false;
        }
        long moneySystemLimit = GameCommon.getValueLong(merchantInfo.getMerchantId() + "CASHOUT_LIMIT_SYSTEM");
        boolean isToday = Calendar.getInstance().get(6) == merchantInfo.getUpdateDay();
        long moneySystemInDay = isToday ? merchantInfo.getMoneyInDay() : 0L;
        try {
            if (isToday) {
                if (moneySystemInDay > Math.round((double)moneySystemLimit * 0.8) && merchantInfo.getNumAlertInDay() < 3) {
                    String content = "Canh bao merchant: " + merchantInfo.getMerchantId() + " sap vuot qua han muc doi Vin trong ngay. Use: " + moneySystemInDay + ". Limit: " + moneySystemLimit;
                    alertSer.alert2List(receives, content, false);
                    merchantInfo.setNumAlertInDay(merchantInfo.getNumAlertInDay() + 1);
                    merchantMap.put(merchantInfo.getMerchantId(), merchantInfo);
                }
            } else {
                merchantInfo.setNumAlertInDay(0);
                merchantMap.put(merchantInfo.getMerchantId(), merchantInfo);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (money + moneySystemInDay <= moneySystemLimit) {
            long moneyUserLimit = GameCommon.getValueLong(merchantInfo.getMerchantId() + "CASHOUT_LIMIT_USER");
            long moneyUserInDay = pmService.getTotalMoney(merchantInfo.getMerchantId(), nickname, DateTimeUtils.getStartTimeToDay(), DateTimeUtils.getEndTimeToDay(), type);
            if (money + moneyUserInDay <= moneyUserLimit) {
                return false;
            }
        }
        return true;
    }

    public static boolean updateMoneyMerchant(MerchantInfo merchantInfo, long money, String type) {
        if (type.equals(TO_VIN)) {
            int today = Calendar.getInstance().get(6);
            if (today == merchantInfo.getUpdateDay()) {
                merchantInfo.setMoneyInDay(merchantInfo.getMoneyInDay() + money);
            } else {
                merchantInfo.setMoneyInDay(money);
                merchantInfo.setUpdateDay(today);
            }
            merchantMap.put(merchantInfo.getMerchantId(), merchantInfo);
        }
        return true;
    }
}

