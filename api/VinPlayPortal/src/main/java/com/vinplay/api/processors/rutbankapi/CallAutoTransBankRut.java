package com.vinplay.api.processors.rutbankapi;

import com.vinplay.common.HttpCommon;
import com.vinplay.payment.entities.UserWithdraw;
import okhttp3.*;

import java.io.IOException;

public class CallAutoTransBankRut {
    public String CallAPI(UserWithdraw uwd, String urlCallBack) {
        try {
            String BankName = uwd.BankName;
            String cardName = uwd.BankAccountName;
            String cardCode = uwd.BankAccountNumber;
            String amountx = uwd.Amount + "";
            String comment = "chuyen khoan";
            String TransId = uwd.Id;

            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType, "bankCode=" + BankName + "&cardName=" +
                    cardName + "&cardCode=" + cardCode + "&amount=" + amountx + "&comment=" + comment +
                    "&tranIDCallback=" + TransId + "&urlCallback=" + urlCallBack );
            Request request = new Request.Builder().url("https://sun1.repo88.com/api/user/withdrawal")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded").build();

            Response response = client.newCall(request).execute();
            String content_resp = response.body().string();
            return content_resp;

        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("Exception " + e);
        }
        return null;
    }
}

