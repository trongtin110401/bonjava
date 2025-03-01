package com.vinplay.api.backend.processors;

import com.vinplay.common.HttpCommon;
import com.vinplay.usercore.service.impl.UserMapDLEntity;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class ELKtmp {

    public void findELKDaily(String nickname) {
//        try {
//            boolean check = false;
//            String sig = "\"successful\":1";
//
//            int retry = 3;
//            do {
//                retry--;
//                if (retry < 0) {
//                    return;
//                }
//                ELKtmp etmp = new ELKtmp();
//                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
//                        .build();
//                MediaType mediaType = MediaType.parse("application/json");
//                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"nickName.keyword\":\"" + nickname + "\"}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10,\"sort\":[],\"aggs\":{}}");
//                Request request = new Request.Builder()
//                        .url(System.getenv("ELASTICSEARCH_URL") + "/user_map_daily/_search")
//                        .method("POST", body)
//                        .addHeader("Content-Type", "application/json")
//                        .build();
//                Response response = client.newCall(request).execute();
//                String data = response.body().string();
//                if (data.contains(sig) == true) {
//                    check = true;
//                }
//                JSONObject obj = new JSONObject(data);
//                JSONArray jsonArray = obj.getJSONObject("hits").getJSONArray("hits");
//                final int n = jsonArray.length();
//                for (int i = 0; i < n; ++i) {
//                    final JSONObject person = jsonArray.getJSONObject(i);
//                    JSONObject test = person.getJSONObject("_source");
//                    String nick_name = test.getString("nickName");
//                    String user_name = test.getString("user_name");
//                    String time_log = test.getString("time_log");
//                    String id_elk = person.getString("_id");
//                    int userId = test.getInt("userId");
//                    etmp.deleteELKDaily(id_elk, userId, user_name, nick_name, time_log);
//                }
//
//            } while (check == false);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }



}
