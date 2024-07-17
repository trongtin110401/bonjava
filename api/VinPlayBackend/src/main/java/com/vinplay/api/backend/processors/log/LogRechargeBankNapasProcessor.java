/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl
 *  com.vinplay.usercore.response.LogRechargeBankNapasResponse
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.backend.processors.log;

import com.vinplay.common.HttpCommon;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.DepositBankModel;
import com.vinplay.dichvuthe.entities.DepositBankReponse;
import com.vinplay.dichvuthe.entities.DepositMomoModel;
import com.vinplay.usercore.response.LogRechargeBankNapasResponse;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import javax.servlet.http.HttpServletRequest;

import okhttp3.*;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class LogRechargeBankNapasProcessor
implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"backend");
    private static final int MAX_ITEM = 15;
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        DepositBankReponse res = new DepositBankReponse(false, "1001");
        try {
            String nickname = request.getParameter("nn");
            String bank = request.getParameter("b");
            String transId = request.getParameter("tid");
           // String ip = request.getParameter("ip");
            //String transNo = request.getParameter("tno");
            String status = request.getParameter("st");
            String startTime = request.getParameter("ts");
            String endTime = request.getParameter("te");
            String pages = request.getParameter("p");
            String numberMax = request.getParameter("max_item");

            String codepay = request.getParameter("codepay");
            String nicknamex = "";
            if(codepay != null){
                if(codepay.trim().length() != 0){
                    ArrayList<String> listnick = GetNicknameByCode(codepay.trim().toUpperCase());
                    if(listnick.size() == 0){
                        nicknamex = "KhongThay";
                    }else if(listnick.size() == 1){
                        nicknamex = listnick.get(0);
                    }else{
                        nicknamex = listnick.get(listnick.size()-1);
                    }
                    nickname = nicknamex;
                }

            }

            int page = Integer.parseInt(pages);
            int maxItem = numberMax != null ? Integer.parseInt(numberMax) : MAX_ITEM;
            RechargeDaoImpl dao = new RechargeDaoImpl();
            DepositBankModel modelSearch = new DepositBankModel(transId, nickname, status, bank);
            // let print all params to console
            System.out.println("nickname: " + nickname + " bank: " + bank + " transId: " + transId + " status: " + status + " startTime: " + startTime + " endTime: " + endTime + " pages: " + pages + " numberMax: " + numberMax + " codepay: " + codepay + " nicknamex: " + nicknamex);
            res = dao.GetListDepositBank(modelSearch, page, maxItem, startTime, endTime);

        }
        catch (Exception e) {
            logger.debug((Object)e);
        }
        return res.toJson();
    }
    public ArrayList<String> GetNicknameByCode(String code){
        try {
            ArrayList<String> listnick;
            String nick = "";
            String code1 = code.toUpperCase();
            boolean check = false;
            String sig = "\"successful\":1";
            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return null;
                }
                listnick = new ArrayList<>();
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"code.keyword\":\""+code1+"\"}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10,\"sort\":[],\"aggs\":{}}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/codeuserbank/_search")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();
                String data = response.body().string();
                if(data.contains(sig) == true){
                    check = true;
                }
                JSONObject obj = new JSONObject(data);
                JSONArray jsonArray = obj.getJSONObject("hits").getJSONArray("hits");
                final int n = jsonArray.length();
                if(n == 0){
                    nick = null;
                }else{
                    for (int i = 0; i < n; ++i) {
                        final JSONObject person = jsonArray.getJSONObject(i);
                        JSONObject test = person.getJSONObject("_source");
                        String nickname = test.getString("nickname");
                        nick = nickname;
                        listnick.add(nickname);
                    }
                }

            }while (check == false);
            return listnick;

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

