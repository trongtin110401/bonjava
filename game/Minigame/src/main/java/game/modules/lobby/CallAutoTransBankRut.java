package game.modules.lobby;

import com.vinplay.common.HttpCommon;
import com.vinplay.payment.entities.UserWithdraw;
import okhttp3.*;

import java.io.IOException;

public class CallAutoTransBankRut {
    public String CallAPI(UserWithdraw uwd, String accessToken, String urlCallBack){
        try {
            String BankName = uwd.BankName;
            String cardName = uwd.BankAccountName;
            String cardCode = uwd.BankAccountNumber;
            String amountx = uwd.Amount + "";
            String comment = "chuyen khoan";
            String TransId = uwd.Id;
            String bankCode = "";
            if(BankName.equalsIgnoreCase("Vietcombank")){
                bankCode = "10010";
            }else if(BankName.equalsIgnoreCase("Techcombank")){
                bankCode = "10020";
            }else if(BankName.equalsIgnoreCase("MBBank")){
                bankCode = "10030";
            }else if(BankName.equalsIgnoreCase("TPBank")){
                bankCode = "10040";
            }else if(BankName.equalsIgnoreCase("Agribank")){
                bankCode = "10050";
            }else if(BankName.equalsIgnoreCase("KienLongBank")){
                bankCode = "10060";
            }else if(BankName.equalsIgnoreCase("HDBank")){
                bankCode = "10070";
            }else if(BankName.equalsIgnoreCase("Vietbank")){
                bankCode = "Error"; //bank nay ko co trong he thong you88
            }else if(BankName.equalsIgnoreCase("Eximbank")){
                bankCode = "10090";
            }else if(BankName.equalsIgnoreCase("Sacombank")){
                bankCode = "10100";
            }else if(BankName.equalsIgnoreCase("VietCapitalBank")){
                bankCode = "Error"; //bank nay ko co trong he thong you88
            }else if(BankName.equalsIgnoreCase("DongABank")){
                bankCode = "10120";
            }else if(BankName.equalsIgnoreCase("NamABank")){
                bankCode = "10130";
            }else if(BankName.equalsIgnoreCase("OricomBank")){
                bankCode = "10140";
            }else if(BankName.equalsIgnoreCase("SCB")){
                bankCode = "Error"; //bank nay ko co trong he thong you88
            }else if(BankName.equalsIgnoreCase("Saigonbank")){
                bankCode = "Error"; //bank nay ko co trong he thong you88
            }else if(BankName.equalsIgnoreCase("BacABank")){
                bankCode = "10170";
            }else if(BankName.equalsIgnoreCase("NCB")){
                bankCode = "10180";
            }else if(BankName.equalsIgnoreCase("VPBank")){
                bankCode = "10190";
            }else if(BankName.equalsIgnoreCase("Shinhanbank")){
                bankCode = "Error"; //bank nay ko co trong he thong you88
            }else if(BankName.equalsIgnoreCase("LienVietPostBank")){
                bankCode = "10210";
            }else if(BankName.equalsIgnoreCase("VietinBank")){
                bankCode = "10220";
            }else if(BankName.equalsIgnoreCase("BIDV")){
                bankCode = "10230";
            }else if(BankName.equalsIgnoreCase("SeABank")){
                bankCode = "10240";
            }else if(BankName.equalsIgnoreCase("ABBANK")){
                bankCode = "10250";
            }else if(BankName.equalsIgnoreCase("MaritimeBank")){
                bankCode = "10260";
            }else if(BankName.equalsIgnoreCase("VietABank")){
                bankCode = "10270";
            }else if(BankName.equalsIgnoreCase("PVcombank")){
                bankCode = "10280";
            }else if(BankName.equalsIgnoreCase("BaoVietBank")){
                bankCode = "10290";
            }else if(BankName.equalsIgnoreCase("PGBank")){
                bankCode = "10300";
            }else if(BankName.equalsIgnoreCase("VIBBank")){
                bankCode = "10310";
            }else if(BankName.equalsIgnoreCase("ACB")){
                bankCode = "10320";
            }else if(BankName.equalsIgnoreCase("MSBbank")) {
                bankCode = "10260";
            }else if(BankName.equalsIgnoreCase("SHBbank")){
                bankCode = "10330";
            }else {
                bankCode = "Error";
            }

            if(bankCode.equalsIgnoreCase("error")){

                return "{\"696969\":\""+TransId+"\"}";
            }else{
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                RequestBody body = RequestBody.create(mediaType, "bankCode="+bankCode+"&cardName="+cardName+"&cardCode="+cardCode+"&amount="+amountx+"&comment="+comment+"&tranIDCallback="+TransId+"&urlCallback="+urlCallBack+"&accessToken="+accessToken);
                Request request = new Request.Builder().url("https://sun1.repo88.com/api/user/withdrawal")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded").build();

                Response response = client.newCall(request).execute();
                String content_resp = response.body().string();
                return content_resp;
            }

        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("Exception "+e);
        }
        return null;
    }
}
