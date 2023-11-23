package com.vinplay.api.processors.autobankOK88;

import com.vinplay.common.HttpCommon;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;

public class CallAPIOK99 {
    public void Call(String nickname, String transid, String amount){
        try {
            String hash1 = nickname+"|"+transid+"|"+amount;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(hash1.getBytes());
            byte[] digest = md.digest();
            String myHash = DatatypeConverter.printHexBinary(digest);

            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");

            Request request = new Request.Builder()
                    .url("https://gateway.zesnegen.space/api/CallbackBankSun?nickname="+nickname+"&transid="+transid+"&amount="+amount+"&sign="+myHash)
                    .method("GET", null)
                    .build();
            Response response = client.newCall(request).execute();
            String data = response.body().string();
            System.out.println(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

