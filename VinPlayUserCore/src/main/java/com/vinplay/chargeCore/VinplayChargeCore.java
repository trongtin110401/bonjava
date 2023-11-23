/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  org.json.simple.JSONObject
 *  org.json.simple.parser.JSONParser
 */
package com.vinplay.chargeCore;

import com.vinplay.dichvuthe.client.HttpClient;
import com.vinplay.dichvuthe.encode.AES;
import com.vinplay.dichvuthe.entities.ChargeObj;
import com.vinplay.usercore.utils.GameCommon;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class VinplayChargeCore {
    public static ChargeObj rechargeByCard(String reqId, String provider, String serial, String pin) throws Exception {
        String res = HttpClient.get(GameCommon.getValueStr("CHARGE_CORE_URL") + "/api/charge?uId=" + GameCommon.getValueStr("CHARGE_CORE_UID") + "&reqId=" + reqId + "&provider=" + provider + "&serial=" + serial + "&pin=" + AES.encrypt(pin, GameCommon.getValueStr("CHARGE_CORE_KEY")));
        JSONObject json = (JSONObject)new JSONParser().parse(res);
        ChargeObj result = VinplayChargeCore.convertToChargeObj(json);
        return result;
    }

    public static ChargeObj checkTrans(String reqId) throws Exception {
        String res = HttpClient.get(GameCommon.getValueStr("CHARGE_CORE_URL") + "/api/check-trans?uId=" + GameCommon.getValueStr("CHARGE_CORE_UID") + "&reqId=" + reqId);
        JSONObject json = (JSONObject)new JSONParser().parse(res);
        ChargeObj result = VinplayChargeCore.convertToChargeObj(json);
        return result;
    }

    private static ChargeObj convertToChargeObj(JSONObject jData) throws Exception {
        ChargeObj response = new ChargeObj();
        if (jData.get((Object)"reqId") != null) {
            response.setId(String.valueOf(jData.get((Object)"reqId")));
        }
        if (jData.get((Object)"provider") != null) {
            response.setProvider(String.valueOf(jData.get((Object)"provider")));
        }
        if (jData.get((Object)"serial") != null) {
            response.setSerial(String.valueOf(jData.get((Object)"serial")));
        }
        if (jData.get((Object)"pin") != null) {
            response.setPin(String.valueOf(jData.get((Object)"pin")));
        }
        if (jData.get((Object)"amount") != null) {
            response.setAmount(Integer.parseInt(String.valueOf(jData.get((Object)"amount"))));
        }
        if (jData.get((Object)"status") != null) {
            response.setStatus(Integer.parseInt(String.valueOf(jData.get((Object)"status"))));
        }
        if (jData.get((Object)"message") != null) {
            response.setMessage(String.valueOf(jData.get((Object)"message")));
        }
        if (jData.get((Object)"channel") != null) {
            response.setChannel(VinplayChargeCore.mapPartner(String.valueOf(jData.get((Object)"channel"))));
        }
        if (jData.get((Object)"uId") != null) {
            response.setUserId(String.valueOf(jData.get((Object)"uId")));
        }
        return response;
    }

    private static String mapPartner(String channel) {
        if (channel.equalsIgnoreCase("store")) {
            return "dvt";
        }
        if (channel.equalsIgnoreCase("epay")) {
            return "epay";
        }
        if (channel.equalsIgnoreCase("vtc")) {
            return "vtc";
        }
        if (channel.equalsIgnoreCase("1pay")) {
            return "1pay";
        }
        if (channel.equalsIgnoreCase("maxpay")) {
            return "maxpay";
        }
        if (channel.equalsIgnoreCase("abtpay")) {
            return "abtpay";
        }
        return channel;
    }
}

