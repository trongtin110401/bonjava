package com.vinplay.api.backend.processors.rutbank;

import com.vinplay.api.backend.processors.entity.APIProcess;
import com.vinplay.api.backend.processors.entity.AutoBankEntity;
import com.vinplay.common.HttpCommon;
import com.vinplay.payment.entities.UserWithdraw;
import okhttp3.*;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CallAutoTransBank {
    public String CallAPI(UserWithdraw uwd) {
        try {
            String BankName = uwd.BankName;
            String bankAccountName = uwd.BankAccountName;
            String bankAccount = uwd.BankAccountNumber;
            String amount = uwd.Amount + "";
            String TransId = uwd.Id;

            AutoBankEntity autoBank = new AutoBankEntity();
            String signature = generateSignature(bankAccount, amount, TransId, autoBank.getApiSecret());
            String url = autoBank.getUrl() + ":" + autoBank.getPort() + autoBank.getApiChargeOut()
                    + "?apiKey=" + autoBank.getApiKey() + "&bank_code=" + BankName + "&bank_account="
                    + bankAccount + "&bank_accountName=" + bankAccountName + "&amount=" + amount + "&signature=" + signature
                    + "&requestId=" + TransId + "&msg=" + TransId;
            ;
            return APIProcess.responseGetAPI(url, null);

        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Exception " + e);
        }
        return null;
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
