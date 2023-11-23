/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IMap
 *  com.vinplay.dichvuthe.service.impl.AlertServiceImpl
 *  com.vinplay.usercore.dao.impl.MoneyInGameDaoImpl
 *  com.vinplay.usercore.service.impl.OtpServiceImpl
 *  com.vinplay.usercore.service.impl.XocDiaServiceImpl
 *  com.vinplay.usercore.utils.GameCommon
 *  com.vinplay.vbee.common.exceptions.KeyNotFoundException
 *  com.vinplay.vbee.common.hazelcast.HazelcastClientFactory
 */
package com.vinplay.api.backend.report.utils;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.api.backend.server.VinPlayBackendMain;
import com.vinplay.dichvuthe.service.impl.AlertServiceImpl;
import com.vinplay.usercore.dao.impl.MoneyInGameDaoImpl;
import com.vinplay.usercore.service.impl.OtpServiceImpl;
import com.vinplay.usercore.service.impl.XocDiaServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.exceptions.KeyNotFoundException;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BackendUtils {
    private static final List<String> MOBILE_ALERT = Arrays.asList(new String[0]);

    public static void init() throws SQLException {
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap userMap = client.getMap("users");
        if (userMap.isEmpty()) {
            XocDiaServiceImpl xdSer = new XocDiaServiceImpl();
            List sessionBlockList = xdSer.getListSessionActive();
            MoneyInGameDaoImpl dao = new MoneyInGameDaoImpl();
            dao.restoreAllGame(sessionBlockList);
        }
    }

    public static int checkOTPSuperAdmin(String otp, String type) throws UnsupportedEncodingException, NoSuchAlgorithmException, SQLException, KeyNotFoundException {
        String admin;
        int code = 3;
        String[] arr = GameCommon.getValueStr((String)"SUPER_ADMIN").split(",");
        OtpServiceImpl otpService = new OtpServiceImpl();
        String[] array = arr;
        int length = array.length;
        for (int i = 0; i < length && (code = otpService.checkOtp(otp, admin = array[i], type, (String)null)) != 0; ++i) {
        }
        return code;
    }

    public static void aleftErrorDvt(HazelcastInstance client) {
        IMap<String, Integer> mapDvt;
        if (BackendUtils.apiRunTask() && (mapDvt = client.getMap("cacheDvt")) != null && mapDvt.size() > 0) {
            for (Map.Entry entry : mapDvt.entrySet()) {
                String action = (String)entry.getKey();
                int numError = (Integer)entry.getValue();
                if (numError != 5 && numError != 10 && numError != 15) continue;
                AlertServiceImpl alertSer = new AlertServiceImpl();
                String content = "Loi ket noi dich vu the: " + action;
                alertSer.sendSMS2List(MOBILE_ALERT, content, true);
            }
        }
    }

    public static boolean apiRunTask() {
        return VinPlayBackendMain.TYPE == 1;
    }
}

