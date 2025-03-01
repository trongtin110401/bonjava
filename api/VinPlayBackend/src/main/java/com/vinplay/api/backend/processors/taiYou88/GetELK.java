package com.vinplay.api.backend.processors.taiYou88;

import com.vinplay.common.HttpCommon;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class GetELK {
    public ArrayList<String> GetDaily(){
//        try {
//            boolean check = false;
//            String sig = "\"successful\":1";
//            ArrayList<String> listDL;
//
//            int retry = 3;
//            do{
//                retry--;
//                if(retry < 0) {
//                    return null;
//                }
//                listDL = new ArrayList<>();
//                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
//                        .build();
//                MediaType mediaType = MediaType.parse("application/json");
//                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match_all\":{}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10000,\"sort\":[],\"aggs\":{}}");
//                Request request = new Request.Builder()
//                        .url(System.getenv("ELASTICSEARCH_URL") + "/analytics_daily/_search")
//                        .method("POST", body)
//                        .addHeader("Content-Type", "application/json")
//                        .build();
//                Response response = client.newCall(request).execute();
//                String data = response.body().string();
//                if(data.contains(sig) == true){
//                    check = true;
//                }
//                JSONObject obj = new JSONObject(data);
//                JSONArray jsonArray = obj.getJSONObject("hits").getJSONArray("hits");
//                final int n = jsonArray.length();
//                for (int i = 0; i < n; ++i) {
//                    final JSONObject person = jsonArray.getJSONObject(i);
//                    JSONObject test = person.getJSONObject("_source");
//                    String utm_dl = test.getString("utm_dl");
//                    listDL.add(utm_dl);
//                }
//            }while (check == false);
//            return listDL;
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        return null;
    }

    public long Total(String dl, String time_start, String time_end){
//        try {
//            boolean check = false;
//            String sig = "\"successful\":1";
//            long total = 0;
//
//            int retry = 3;
//            do{
//                retry--;
//                if(retry < 0) {
//                    return 0;
//                }
//                total = 0;
//                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
//                        .build();
//                MediaType mediaType = MediaType.parse("application/json");
//                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"utm_dl.keyword\":\""+dl+"\"}},{\"range\":{\"create_time.keyword\":{\"gt\":\""+time_start+"\",\"lt\":\""+time_end+"\"}}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10000,\"sort\":[],\"aggs\":{}}");
//                Request request = new Request.Builder()
//                        .url(System.getenv("ELASTICSEARCH_URL") + "/analytics_tai_you88/_search")
//                        .method("POST", body)
//                        .addHeader("Content-Type", "application/json")
//                        .build();
//                Response response = client.newCall(request).execute();
//                String data = response.body().string();
//                if(data.contains(sig) == true){
//                    check = true;
//                }
//                JSONObject obj = new JSONObject(data);
//                JSONArray jsonArray = obj.getJSONObject("hits").getJSONArray("hits");
//                final int n = jsonArray.length();
//                total = n;
//            }while (check == false);
//            return total;
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        return 0;
    }

    public long Tong(String dl, String time_start, String time_end, String dieukien){
//        try {
//            boolean check = false;
//            String sig = "\"successful\":1";
//            long total = 0;
//
//            int retry = 3;
//            do{
//                retry--;
//                if(retry < 0) {
//                    return 0;
//                }
//                total = 0;
//                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
//                        .build();
//                MediaType mediaType = MediaType.parse("application/json");
//                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"utm_dl.keyword\":\""+dl+"\"}},{\"range\":{\"create_time.keyword\":{\"gt\":\""+time_start+"\",\"lt\":\""+time_end+"\"}}},{\"match\":{\"utm_medium.keyword\":\""+dieukien+"\"}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10000,\"sort\":[],\"aggs\":{}}");
//                Request request = new Request.Builder()
//                        .url(System.getenv("ELASTICSEARCH_URL") + "/analytics_tai_you88/_search")
//                        .method("POST", body)
//                        .addHeader("Content-Type", "application/json")
//                        .build();
//                Response response = client.newCall(request).execute();
//                String data = response.body().string();
//                if(data.contains(sig) == true){
//                    check = true;
//                }
//                JSONObject obj = new JSONObject(data);
//                JSONArray jsonArray = obj.getJSONObject("hits").getJSONArray("hits");
//                final int n = jsonArray.length();
//                total = n;
//            }while (check == false);
//            return total;
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        return 0;
    }

    public ArrayList<DataDoimainResponse> XulyCound(String time_start, String time_end){

//        try {
//            ArrayList<DataDoimainResponse> listRecord = new ArrayList<>();
//            boolean check = false;
//            String sig = "\"successful\":1";
//            ArrayList<String> listDL = GetDaily();
//            for(String dl : listDL){
//                int retry = 3;
//                do{
//                    retry--;
//                    if(retry < 0) {
//                        break;
//                    }
//                    long errror = 0;
//                    long web = 0;
//                    long ios = 0;
//                    long android = 0;
//                    long tai = 0;
//                    long totalCount = 0;
//                    long click_login = 0;
//                    long click_register = 0;
//                    long click_download_app = 0;
//                    OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
//                            .build();
//                    MediaType mediaType = MediaType.parse("application/json");
//                    RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"utm_dl.keyword\":\""+dl+"\"}},{\"range\":{\"create_time.keyword\":{\"gt\":\""+time_start+"\",\"lt\":\""+time_end+"\"}}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10000,\"sort\":[],\"aggs\":{}}");
//                    Request request = new Request.Builder()
//                            .url(System.getenv("ELASTICSEARCH_URL") + "/analytics_tai_you88/_search")
//                            .method("POST", body)
//                            .addHeader("Content-Type", "application/json")
//                            .build();
//                    Response response = client.newCall(request).execute();
//                    String data = response.body().string();
//                    if(data.contains(sig) == true){
//                        check = true;
//                    }
//                    JSONObject obj = new JSONObject(data);
//                    JSONArray jsonArray = obj.getJSONObject("hits").getJSONArray("hits");
//                    final int n = jsonArray.length();
//                    totalCount = n;
//                    for (int i = 0; i < n; ++i) {
//                        final JSONObject person = jsonArray.getJSONObject(i);
//                        JSONObject test = person.getJSONObject("_source");
//                        String utm_medium = test.getString("utm_medium");
//                        if(utm_medium.equalsIgnoreCase("btn_choinhanhweb")){
//                            web = web+1;
//                        }else if(utm_medium.equalsIgnoreCase("download_ios")){
//                            ios = ios +1;
//                        }else if(utm_medium.equalsIgnoreCase("download_android")){
//                            android = android +1;
//                        }else if(utm_medium.equalsIgnoreCase("register_form_tai")){
//                            tai = tai +1;
//                        }else if(utm_medium.equalsIgnoreCase("click_download_app")){
//                            click_download_app = click_download_app +1;
//                        }else if(utm_medium.equalsIgnoreCase("click_login")){
//                            click_login = click_login+1;
//                        }else if(utm_medium.equalsIgnoreCase("click_register")){
//                            click_register = click_register +1;
//                        }
//                        else{
//                            errror = errror+1;
//                        }
//                    }
//                    DataDoimainResponse model = new DataDoimainResponse(dl, totalCount, web, ios, android, tai, click_login, click_register, click_download_app);
//                    listRecord.add(model);
//                }while (check == false);
//            }
//
//            return listRecord;
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        return null;
    }





}
