package com.vinplay.api.processors.autobankOK88;

import com.vinplay.common.HttpCommon;
import okhttp3.*;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CallAPIOK88 {
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
                    .url("https://bank.csno.cam/api/CallbackBankSun?nickname="+nickname+"&transid="+transid+"&amount="+amount+"&sign="+myHash)
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
