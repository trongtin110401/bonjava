package com.vinplay.zoan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.vinplay.dichvuthe.client.HttpClient;
import com.vinplay.dichvuthe.encode.RSA;
import com.vinplay.dichvuthe.entities.SoftpinObj;
import com.vinplay.khothe.BuyCardRequestObj;
import com.vinplay.khothe.BuyCardResponseObj;
import com.vinplay.usercore.utils.PartnerConfig;
import com.vinplay.vbee.common.models.SoftpinJson;
import com.vinplay.vbee.common.utils.VinPlayUtils;
//import game.utils.GameUtils;

import java.util.ArrayList;

public class ZoanClient {
    public static ZoanMomoResponse GetShippers() throws Exception {
        String id = System.currentTimeMillis() + "";
        long request_time = System.currentTimeMillis();
        String username = PartnerConfig.MomoZoanPartnerKey;
        String partnerKey = PartnerConfig.MomoZoanPartnerKey;
        String signature =  id + "|" + request_time + "|" + username + "|" + partnerKey;
        String url = PartnerConfig.MomoZoanEndpoint + "?username=" + PartnerConfig.MomoZoanPartnerName + "&request_id=" + id
                + "&request_time=" + request_time + "&signature=" + VinPlayUtils.getMD5Hash(signature);
        String res = HttpClient.get(url);
        Gson gson = new Gson();
        ZoanMomoResponse result = gson.fromJson(res, ZoanMomoResponse.class);
        return result;
    }
}
