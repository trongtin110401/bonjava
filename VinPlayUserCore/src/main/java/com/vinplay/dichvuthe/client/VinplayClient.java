/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.vinplay.vbee.common.models.SoftpinJson
 *  org.json.simple.JSONArray
 *  org.json.simple.JSONObject
 *  org.json.simple.parser.JSONParser
 */
package com.vinplay.dichvuthe.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinplay.dichvuthe.client.HttpClient;
import com.vinplay.dichvuthe.entities.AlertObj;
import com.vinplay.dichvuthe.entities.BankcoObj;
import com.vinplay.dichvuthe.entities.BankrcObj;
import com.vinplay.dichvuthe.entities.ChargeObj;
import com.vinplay.dichvuthe.entities.ChargeVCObj;
import com.vinplay.dichvuthe.entities.EmailObj;
import com.vinplay.dichvuthe.entities.SendBankObj;
import com.vinplay.dichvuthe.entities.SendSoftpinObj;
import com.vinplay.dichvuthe.entities.SendTopupObj;
import com.vinplay.dichvuthe.entities.SoftpinObj;
import com.vinplay.dichvuthe.entities.TopupObj;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.models.SoftpinJson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class VinplayClient {
    public static ChargeObj rechargeByCard(String id, String provider, String serial, String pin) throws Exception {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("id", id);
        param.put("provider", provider);
        param.put("serial", serial);
        param.put("pin", pin);
        ObjectMapper mapper = new ObjectMapper();
        String res = HttpClient.post(GameCommon.getValueStr("DVT_URL") + "/api/charge", mapper.writeValueAsString(param));
        JSONObject json = (JSONObject)new JSONParser().parse(res);
        ChargeObj result = VinplayClient.convertToChargeObj(json);
        return result;
    }

    public static ChargeVCObj rechargeByVinCard(String id, String partner, String serial, String pin, String target) throws Exception {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("id", id);
        param.put("partner", partner);
        if (serial != null && !serial.isEmpty()) {
            param.put("serial", serial);
        }
        param.put("pin", pin);
        if (target != null && !target.isEmpty()) {
            param.put("target", target);
        }
        ObjectMapper mapper = new ObjectMapper();
        String res = HttpClient.post(GameCommon.getValueStr("VIN_CARD_URL") + "/api/charge", mapper.writeValueAsString(param));
        ChargeVCObj result = (ChargeVCObj)mapper.readValue(res, ChargeVCObj.class);
        return result;
    }

    public static ChargeObj reCheckRechargeByCard(String id) throws Exception {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("id", id);
        ObjectMapper mapper = new ObjectMapper();
        String res = HttpClient.post(GameCommon.getValueStr("DVT_URL") + "/api/charge-check", mapper.writeValueAsString(param));
        JSONObject json = (JSONObject)new JSONParser().parse(res);
        ChargeObj result = VinplayClient.convertToChargeObj(json);
        return result;
    }

    public static BankrcObj rechargeByBank() throws IOException {
        return null;
    }

    public static SoftpinObj cashOutByCard(String id, String provider, int amount, int quantity, String sign) throws Exception {
        SendSoftpinObj param = new SendSoftpinObj(id, provider, amount, quantity, sign);
        ObjectMapper mapper = new ObjectMapper();
        String res = HttpClient.post(GameCommon.getValueStr("DVT_URL") + "/api/buy-card", mapper.writeValueAsString((Object)param));
        JSONObject json = (JSONObject)new JSONParser().parse(res);
        SoftpinObj result = VinplayClient.convertToSoftpinObj(json);
        return result;
    }

    public static SoftpinObj reCheckCashOutByCard(String id, String sign) throws Exception {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("id", String.valueOf(id));
        param.put("sign", sign);
        ObjectMapper mapper = new ObjectMapper();
        String res = HttpClient.post(GameCommon.getValueStr("DVT_URL") + "/api/re-buy-card", mapper.writeValueAsString(param));
        JSONObject json = (JSONObject)new JSONParser().parse(res);
        SoftpinObj result = VinplayClient.convertToSoftpinObj(json);
        return result;
    }

    public static TopupObj cashOutByTopUp(String id, String target, int type, int amount, String sign) throws Exception {
        SendTopupObj param = new SendTopupObj(id, target, type, amount, sign);
        ObjectMapper mapper = new ObjectMapper();
        String res = HttpClient.post(GameCommon.getValueStr("DVT_URL") + "/api/topup", mapper.writeValueAsString((Object)param));
        JSONObject json = (JSONObject)new JSONParser().parse(res);
        TopupObj result = VinplayClient.convertToTopupObj(json);
        return result;
    }

    public static BankcoObj cashOutByBank(String id, String bank, String account, String name, int amount, String sign) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        SendBankObj param = new SendBankObj(id, bank, account, name, amount, sign);
        String res = HttpClient.post(GameCommon.getValueStr("DVT_URL") + "/api/payment", mapper.writeValueAsString((Object)param));
        BankcoObj result = (BankcoObj)mapper.readValue(res, BankcoObj.class);
        return result;
    }

    public static BankcoObj reCheckCashOutByBank(String id, String sign) throws Exception {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("id", String.valueOf(id));
        param.put("sign", sign);
        ObjectMapper mapper = new ObjectMapper();
        String res = HttpClient.post(GameCommon.getValueStr("DVT_URL") + "/api/check-payment", mapper.writeValueAsString(param));
        BankcoObj result = (BankcoObj)mapper.readValue(res, BankcoObj.class);
        return result;
    }

    public static String sendAleftSMS(List<String> receives, String content, boolean call) throws Exception {
        AlertObj obj = new AlertObj(receives, content, call);
        ObjectMapper mapper = new ObjectMapper();
        String res = HttpClient.post(GameCommon.getValueStr("DVT_URL") + "/api/alert", mapper.writeValueAsString((Object)obj));
        return res;
    }

    public static String aleft(List<String> receives, String content, boolean call) throws Exception {
        AlertObj obj = new AlertObj(receives, content, call);
        ObjectMapper mapper = new ObjectMapper();
        String res = HttpClient.post(GameCommon.getValueStr("ALERT_URL"), mapper.writeValueAsString((Object)obj));
        return res;
    }

    public static String sendEmail(String subject, String content, List<String> receives) throws Exception {
        EmailObj obj = new EmailObj(subject, content, receives);
        ObjectMapper mapper = new ObjectMapper();
        String res = HttpClient.post(GameCommon.getValueStr("DVT_URL") + "/api/email", mapper.writeValueAsString((Object)obj));
        return res;
    }

    private static TopupObj convertToTopupObj(JSONObject jData) throws Exception {
        TopupObj response = new TopupObj();
        if (jData.get((Object)"id") != null) {
            response.setId(String.valueOf(jData.get((Object)"id")));
        }
        if (jData.get((Object)"target") != null) {
            response.setTarget(String.valueOf(jData.get((Object)"target")));
        }
        if (jData.get((Object)"type") != null) {
            response.setType(Integer.parseInt(String.valueOf(jData.get((Object)"type"))));
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
            response.setPartner(VinplayClient.mapPartner(String.valueOf(jData.get((Object)"channel"))));
        }
        if (jData.get((Object)"sign") != null) {
            response.setSign(String.valueOf(jData.get((Object)"sign")));
        }
        return response;
    }

    private static ChargeObj convertToChargeObj(JSONObject jData) throws Exception {
        ChargeObj response = new ChargeObj();
        if (jData.get((Object)"id") != null) {
            response.setId(String.valueOf(jData.get((Object)"id")));
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
            response.setChannel(VinplayClient.mapPartner(String.valueOf(jData.get((Object)"channel"))));
        }
        return response;
    }

    private static SoftpinObj convertToSoftpinObj(JSONObject jData) throws Exception {
        SoftpinObj response = new SoftpinObj();
        if (jData.get((Object)"id") != null) {
            response.setId(String.valueOf(jData.get((Object)"id")));
            response.setPartnerTransId(String.valueOf(jData.get((Object)"id")));
        }
        if (jData.get((Object)"provider") != null) {
            response.setProvider(String.valueOf(jData.get((Object)"provider")));
        }
        if (jData.get((Object)"amount") != null) {
            response.setAmount(Integer.parseInt(String.valueOf(jData.get((Object)"amount"))));
        }
        if (jData.get((Object)"quantity") != null) {
            response.setQuantity(Integer.parseInt(String.valueOf(jData.get((Object)"quantity"))));
        }
        if (jData.get((Object)"status") != null) {
            response.setStatus(Integer.parseInt(String.valueOf(jData.get((Object)"status"))));
        }
        if (jData.get((Object)"message") != null) {
            response.setMessage(String.valueOf(jData.get((Object)"message")));
        }
        if (jData.get((Object)"softpinList") != null) {
            JSONArray arr = (JSONArray)jData.get((Object)"softpinList");
            ArrayList<SoftpinJson> softpinList = new ArrayList<SoftpinJson>();
            for (int i = 0; i < arr.size(); ++i) {
                JSONObject obj = (JSONObject)arr.get(i);
                SoftpinJson softpin = new SoftpinJson();
                softpin.setProvider(String.valueOf(obj.get((Object)"provider")));
                softpin.setAmount(Integer.parseInt(String.valueOf(obj.get((Object)"amount"))));
                softpin.setSerial(String.valueOf(obj.get((Object)"serial")));
                softpin.setPin(String.valueOf(obj.get((Object)"pin")));
                softpin.setExpire(String.valueOf(obj.get((Object)"expire")));
                softpinList.add(softpin);
            }
            response.setSoftpinList(softpinList);
        }
        if (jData.get((Object)"channel") != null) {
            response.setPartner(VinplayClient.mapPartner(String.valueOf(jData.get((Object)"channel"))));
        }
        if (jData.get((Object)"sign") != null) {
            response.setSign(String.valueOf(jData.get((Object)"sign")));
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
        return channel;
    }
}

