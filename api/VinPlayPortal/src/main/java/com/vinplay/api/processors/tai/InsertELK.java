package com.vinplay.api.processors.tai;

import com.vinplay.common.HttpCommon;
import okhttp3.*;

public class InsertELK {

    public void InsertDaily(String utm_dl){
        try {
            boolean check = false;
            String sig = "\"successful\":1";
            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return;
                }
                OkHttpClient client =  HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\r\n\"_class\": \"vn.com.syncmonges.entity.AnalyticsDaily\",\r\n\"id\": \"666999\",\r\n\"utm_dl\": \""+utm_dl+"\"\r\n}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/analytics_daily/_doc")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();
                String data = response.body().string();
                if(data.contains(sig) == true){
                    check = true;
                }
            }while (check == false);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void InsertDailyTai88(String utm_dl, String utm_source, String utm_medium, String utm_campaign, String create_time){
        try {
            boolean check = false;
            String sig = "\"successful\":1";

            int retry = 3;
            do{
                retry--;
                if(retry < 0) {
                    return;
                }
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\r\n\"_class\": \"vn.com.syncmonges.entity.AnalyticsTaiYou88\",\r\n\"id\": \"666999\",\r\n\"utm_dl\": \""+utm_dl+"\",\r\n\"utm_source\": \""+utm_source+"\",\r\n\"utm_medium\": \""+utm_medium+"\",\r\n\"utm_campaign\": \""+utm_campaign+"\",\r\n\"create_time\": \""+create_time+"\"\r\n}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/analytics_tai_you88/_doc")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();
                String data = response.body().string();
                if(data.contains(sig) == true){
                    check = true;
                }
            }while (check == false);

        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
