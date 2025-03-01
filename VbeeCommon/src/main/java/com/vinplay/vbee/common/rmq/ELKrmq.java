package com.vinplay.vbee.common.rmq;

import com.vinplay.vbee.common.messages.FreezeMoneyMessage;
import com.vinplay.vbee.common.messages.LogMoneyUserMessage;
import com.vinplay.vbee.common.messages.MoneyMessageInGame;
import com.vinplay.vbee.common.messages.MoneyMessageInMinigame;
import com.vinplay.vbee.common.statics.Consts;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class ELKrmq {

    public long getTotalStakesTx(String username, long start, long end) {
//        String sig = "\"successful\":1";
//        boolean check = false;
//        int timeretry = 10;
//        do {
//            try {
//                long totalStakes = 0;
//                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
//                        .build();
//                MediaType mediaType = MediaType.parse("application/json");
//                RequestBody body = RequestBody.create(mediaType, "{\r\n    \"query\": {\r\n        \"bool\": {\r\n            \"must\": [\r\n                {\r\n                    \"match\": {\r\n                        \"user_name\": \""+username+"\"\r\n                    }\r\n                },\r\n                {\r\n                    \"range\": {\r\n                        \"timestamp\": {\r\n                            \"gt\": \""+start+"\",\r\n                            \"lt\": \""+end+"\"\r\n                        }\r\n                    }\r\n                }\r\n            ],\r\n            \"must_not\": [],\r\n            \"should\": []\r\n        }\r\n    },\r\n    \"from\": 0,\r\n    \"size\": 1000,\r\n    \"sort\": [],\r\n    \"aggs\": {}\r\n}");
//                Request request = new Request.Builder()
//                        .url(System.getenv("ELASTICSEARCH_URL") + "/transaction_tai_xiu/_search")
//                        .method("POST", body)
//                        .addHeader("Connection", "keep-alive")
//                        .build();
//                Response response = client.newCall(request).execute();
//                String data = response.body().string();
//                timeretry--;
//                if (data.contains(sig) == true) {
//                    check = true;
//                }
//                JSONObject obj = new JSONObject(data);
//                JSONArray jsonArray = obj.getJSONObject("hits").getJSONArray("hits");
//                final int n = jsonArray.length();
//                for (int i = 0; i < n; ++i) {
//                    final JSONObject person = jsonArray.getJSONObject(i);
//                    JSONObject test = person.getJSONObject("_source");
//                    long total_exchange = test.getLong("total_exchange");
//                    totalStakes+=Math.abs(total_exchange);
//                }
//                return totalStakes;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } while (check == false && timeretry > 0);

        return 0;
    }

}
