package com.vinplay.api.processors.NapRutThe;

import bitzero.util.common.business.Debug;
import com.vinplay.api.processors.AutoXuLyBank.APIProcess;
import com.vinplay.api.processors.AutoXuLyBank.AutoBankEntity;
import com.vinplay.common.HttpCommon;
import com.vinplay.dichvuthe.service.impl.RechargeServiceImpl;
import com.vinplay.gachthe.GachTheException;
import com.vinplay.gachthe.NapTienGaClient;
import com.vinplay.usercore.utils.PartnerConfig;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import okhttp3.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class NapThe {

    public static OkHttpClient client = new OkHttpClient().newBuilder().callTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build();

    public JSONObject doCharge(String cardType, String pin, String seri, String transId, long amount) throws Exception {

        try {

            this.validateCharge(cardType, pin, seri);
            String telo = "";
            if(cardType.equals("VT")){
                telo = "VTT";
            }else if(cardType.equals("Vina")){
                telo = "VNP";
            }else if(cardType.equals("Mobi")){
                telo = "VMS";
            }else{
                telo = "Error";
            }

            String username = "vivu06";
            String pass = "vivu06hshshshsbshhshz@";

            long t0 = System.nanoTime();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\"username\":\""+username+"\",\"password\":\""+pass+"\",\"amount\":\""+amount+"\",\"serial\":\""+seri+"\",\"telco\":\""+telo+"\",\"pincode\":\""+pin+"\",\"requestid\":\""+transId+"\"}");
            Request request = new Request.Builder()
//                    .url("http://45.77.183.1:8899/checkcard")
                    .url("http://45.32.106.125:8899/checkcard")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            Response response = client.newCall(request).execute();

            long t1 = System.nanoTime();
            System.out.println("Elapsed time =" + (t1 - t0)
                    + " nanoseconds");
            long t3 = t1 - t0;
            if (t3 > 9003827700l) {
                NotifyProxy("[Sun] Chet Nap The !");
            }

            String resul_resp = response.body().string();
            if(resul_resp == null){
                String response_end = "{\"msg\":"+"null respose"+",\"errorCode\":"+"100"+"}";
                JSONObject json = (JSONObject) new JSONParser().parse(response_end);
                return json;
            }else{
                if(resul_resp.contains("errorcode")){
                    String[] data1 = resul_resp.split(",");
                    String data2 = "";
                    String data2_plus = "";
                    String data2_plusplus = "";
                    for(String xx : data1){
                        if(xx.contains("errorcode")){
                            data2 = xx;
                        }
                        if(xx.contains("description")){
                            data2_plus = xx;
                        }
                        if(xx.contains("requestid")){
                            data2_plusplus = xx;
                        }
                    }
                    String[] data3 = data2.split(":");
                    String[] data4 = data2_plus.split(":");
                    String[] data5 = data2_plusplus.split(":");
                    String codex = data3[1];
                    codex = codex.replaceAll("}","");
                    codex = codex.replaceAll("\\{","");
                    codex = codex.replaceAll("\"","");
                    int code1 = Integer.parseInt(codex);
                    String code2 = data4[1];
                    code2 = code2.replaceAll("}","");
                    code2 = code2.replaceAll("\\{","");
                    String code3 = data5[1];
                    code3 = code3.replaceAll("}","");
                    code3 = code3.replaceAll("\\{","");

                    int code0 = 0;
                    if(code1 == 1){
                        code0 = 0;
                    }else {
                        code0 = code1;
                    }

                    String response_end = "{\"msg\":"+code2+",\"errorCode\":"+code0+"}";
                    JSONObject json = (JSONObject) new JSONParser().parse(response_end);
                    return json;
                }else{
                    String response_end = "{\"msg\":"+"null respose"+",\"errorCode\":"+"100"+"}";
                    JSONObject json = (JSONObject) new JSONParser().parse(response_end);
                    return json;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        String response_end = "{\"msg\":"+"null respose"+",\"errorCode\":"+"100"+"}";
        JSONObject json = (JSONObject) new JSONParser().parse(response_end);
        return json;


    }
    private boolean validateCharge(String cardType, String pin, String seri) throws GachTheException {
        if (cardType == null || cardType.isEmpty()) {
            throw new GachTheException("Ch\u01b0a c\u00f3 lo\u1ea1i th\u1ebb", 1);
        }

        if (pin == null || pin.isEmpty()) {
            throw new GachTheException("Ch\u01b0a c\u00f3 m\u00e3 pin", 3);
        }
        if (seri == null || seri.isEmpty()) {
            throw new GachTheException("Ch\u01b0a c\u00f3 serial", 4);
        }
        return true;
    }

    public void NotifyProxy(String noidung) {
        try {

            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("https://api.telegram.org/bot5629083296:AAHSvVRVnvSTj8VNusqBn1-NRVTICB36S30/sendMessage?chat_id=-779731749&text=" + noidung)
                    .method("GET", null)
                    .build();
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JSONObject doCharge_tokyo(String cardType, String pin, String seri, String transId, long amount) throws Exception {

        this.validateCharge(cardType, pin, seri);

        String apiToken="XX5tHU5mcbMja9reuGb1Uty8Dlh1SJOY2hcFNiGFTI4z4HfffecMD0DLg50GrOni";
        String signature = VinPlayUtils.getMD5Hash(apiToken+amount+transId+seri);

        String urlCallBack="https://apisieunhangao.net/api?c=4001&";
        OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "CardSeri="+seri+"&CardCode="+pin+"&Amount="+amount+"&Signature="+signature+"&TransID="+transId+"&CardType="+cardType+"&ApiToken="+apiToken+"&UrlCallBack="+urlCallBack+"");
        Request request = new Request.Builder()
                .url("http://66.42.62.119:8082/partner/RequestPayment")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = client.newCall(request).execute();

        JSONObject json = (JSONObject) new JSONParser().parse(response.body().string());

        return json;


    }


    public JSONObject napTheAuto(String cardType, String pin, String seri, String transId,long amount) throws Exception {
        NapTienGaClient napTienGaClient = new NapTienGaClient();
        napTienGaClient.installAllTrustManager();

        AutoBankEntity autoBank = new AutoBankEntity();

        String url = autoBank.getUrl() + ":" + autoBank.getPort() + autoBank.getApiCard()
                + "?apiKey=" + autoBank.getApiKey() + "&code="+ pin +"&serial="+ seri +"&type="+ cardType +"&menhGia="+ amount +"&requestId=" + transId;
        JSONObject json = (JSONObject)new JSONParser().parse(APIProcess.responseGetAPI(url, null));
        return json;
    }

}
