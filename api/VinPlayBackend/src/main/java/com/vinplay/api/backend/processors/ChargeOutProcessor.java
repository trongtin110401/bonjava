package com.vinplay.api.backend.processors;

import com.vinplay.api.backend.processors.entity.APIProcess;
import com.vinplay.api.backend.processors.entity.AutoBankEntity;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class ChargeOutProcessor implements BaseProcessor<HttpServletRequest, String> {
    @Override
    public String execute(Param<HttpServletRequest> param) {

        HttpServletRequest request = param.get();

        String bankCode = request.getParameter("bankCode");
        String bankAccount = request.getParameter("bankAccount");
        String bankAccountName = request.getParameter("bankAccountName");
        String msg = request.getParameter("msg");
        String amount = request.getParameter("amount");

        if (bankCode.isEmpty() || bankAccountName.isEmpty() || bankAccount.isEmpty() || amount.isEmpty()) {
            return "{\"error\":400,\"data\":" + "data cannot be null " + "}";
        }

        try {
            if (Double.parseDouble(amount) < 200000) {
                return "{\"error\":400,\"data\":" + "Số tiền rút phải từ 200.000 vnd " + "}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":400,\"data\":" + "amount invalid type" + "}";
        }

        String requestId = String.valueOf(UUID.randomUUID());

        AutoBankEntity autoBank = new AutoBankEntity();
        String signature = generateSignature(bankAccount, amount, requestId, autoBank.getApiSecret());

        String url = autoBank.getUrl() + ":" + autoBank.getPort() + autoBank.getApiRegCharge()
                + "?apiKey=" + autoBank.getApiKey() + "&bank_code=" + bankCode + "&bank_account="
                + bankAccount + "&bank_accountName=" + bankAccountName + "&amount=" + amount + "&signature=" + signature
                + "&requestId=" + requestId + "&msg=" + msg;
        return APIProcess.responseGetAPI(url, null);
    }

    private String generateSignature(String bankAccount, String amount, String requestId, String apiSecret) {
        String input = bankAccount + amount + requestId + apiSecret;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
