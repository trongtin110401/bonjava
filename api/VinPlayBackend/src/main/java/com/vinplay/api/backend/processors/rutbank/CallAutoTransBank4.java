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

public class CallAutoTransBank4 {
    public String CallAPI(UserWithdraw uwd, String accessToken, String urlCallBack) {
        String BankName = uwd.BankName;
        String bankAccountName = uwd.BankAccountName;
        String bankAccount = uwd.BankAccountNumber;
        String amount = uwd.Amount + "";
        String comment = "chuyen khoan";
        String TransId = uwd.Id;
        String bankCode = "";
        if (BankName.equalsIgnoreCase("Vietcombank")) {
            bankCode = "10010";
        } else if (BankName.equalsIgnoreCase("Techcombank")) {
            bankCode = "10020";
        } else if (BankName.equalsIgnoreCase("MBBank")) {
            bankCode = "10030";
        } else if (BankName.equalsIgnoreCase("TPBank")) {
            bankCode = "10040";
        } else if (BankName.equalsIgnoreCase("Agribank")) {
            bankCode = "10050";
        } else if (BankName.equalsIgnoreCase("KienLongBank")) {
            bankCode = "10060";
        } else if (BankName.equalsIgnoreCase("HDBank")) {
            bankCode = "10070";
        } else if (BankName.equalsIgnoreCase("Vietbank")) {
            bankCode = "Error"; //bank nay ko co trong he thong you88
        } else if (BankName.equalsIgnoreCase("Eximbank")) {
            bankCode = "10090";
        } else if (BankName.equalsIgnoreCase("Sacombank")) {
            bankCode = "10100";
        } else if (BankName.equalsIgnoreCase("VietCapitalBank")) {
            bankCode = "Error"; //bank nay ko co trong he thong you88
        } else if (BankName.equalsIgnoreCase("DongABank")) {
            bankCode = "10120";
        } else if (BankName.equalsIgnoreCase("NamABank")) {
            bankCode = "10130";
        } else if (BankName.equalsIgnoreCase("OricomBank")) {
            bankCode = "10140";
        } else if (BankName.equalsIgnoreCase("SCB")) {
            bankCode = "Error"; //bank nay ko co trong he thong you88
        } else if (BankName.equalsIgnoreCase("Saigonbank")) {
            bankCode = "Error"; //bank nay ko co trong he thong you88
        } else if (BankName.equalsIgnoreCase("BacABank")) {
            bankCode = "10170";
        } else if (BankName.equalsIgnoreCase("NCB")) {
            bankCode = "10180";
        } else if (BankName.equalsIgnoreCase("VPBank")) {
            bankCode = "10190";
        } else if (BankName.equalsIgnoreCase("Shinhanbank")) {
            bankCode = "Error"; //bank nay ko co trong he thong you88
        } else if (BankName.equalsIgnoreCase("LienVietPostBank")) {
            bankCode = "10210";
        } else if (BankName.equalsIgnoreCase("VietinBank")) {
            bankCode = "10220";
        } else if (BankName.equalsIgnoreCase("BIDV")) {
            bankCode = "10230";
        } else if (BankName.equalsIgnoreCase("SeABank")) {
            bankCode = "10240";
        } else if (BankName.equalsIgnoreCase("ABBANK")) {
            bankCode = "10250";
        } else if (BankName.equalsIgnoreCase("MaritimeBank")) {
            bankCode = "10260";
        } else if (BankName.equalsIgnoreCase("VietABank")) {
            bankCode = "10270";
        } else if (BankName.equalsIgnoreCase("PVcombank")) {
            bankCode = "10280";
        } else if (BankName.equalsIgnoreCase("BaoVietBank")) {
            bankCode = "10290";
        } else if (BankName.equalsIgnoreCase("PGBank")) {
            bankCode = "10300";
        } else if (BankName.equalsIgnoreCase("VIBBank")) {
            bankCode = "10310";
        } else if (BankName.equalsIgnoreCase("ACB")) {
            bankCode = "10320";
        } else if (BankName.equalsIgnoreCase("MSBbank")) {
            bankCode = "10260";
        } else if (BankName.equalsIgnoreCase("SHBbank")) {
            bankCode = "10330";
        } else {
            bankCode = "Error";
        }

        if (bankCode.equalsIgnoreCase("error")) {
            return "{\"696969\":\"" + TransId + "\"}";
        } else {
            AutoBankEntity autoBank = new AutoBankEntity();
            String signature = generateSignature(bankAccount, amount, TransId, autoBank.getApiSecret());
            String url = autoBank.getUrl() + ":" + autoBank.getPort() + autoBank.getApiRegCharge()
                    + "?apiKey=" + autoBank.getApiKey() + "&bank_code=" + bankCode + "&bank_account="
                    + bankAccount + "&bank_accountName=" + bankAccountName + "&amount=" + amount + "&signature=" + signature
                    + "&requestId=" + TransId + "&msg=" + comment;
            return APIProcess.responseGetAPI(url, null);
        }

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
