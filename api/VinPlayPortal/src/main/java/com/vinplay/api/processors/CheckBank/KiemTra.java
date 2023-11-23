package com.vinplay.api.processors.CheckBank;

import com.vinplay.common.HttpCommon;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class KiemTra {

    public boolean Check(String stk, String bankname){
        try {
        String bin = "";
        if(bankname.equalsIgnoreCase("mbbank")){
            bin = "970422";
        }else if(bankname.equalsIgnoreCase("Techcombank")){
            bin = "970407";
        }else if(bankname.equalsIgnoreCase("vietcombank")){
            bin = "970436";
        }else if(bankname.equalsIgnoreCase("vietinbank")){
            bin = "970415";
        }else if(bankname.equalsIgnoreCase("abbank")){
            bin = "970425";
        }else if(bankname.equalsIgnoreCase("acb")){
            bin = "970416";
        }else if(bankname.equalsIgnoreCase("agribank")){
            bin = "970405";
        }else if(bankname.equalsIgnoreCase("bidv")){
            bin = "970418";
        }else if(bankname.equalsIgnoreCase("dongabank")){
            bin = "970406";
        }else if(bankname.equalsIgnoreCase("hdbank")){
            bin = "970437";
        }else if(bankname.equalsIgnoreCase("sacombank")){
            bin = "970403";
        }else if(bankname.equalsIgnoreCase("tpbank")){
            bin = "970423";
        }else if(bankname.equalsIgnoreCase("vibbank")){
            bin = "970441";
        }else if(bankname.equalsIgnoreCase("vietabank")){
            bin = "970427";
        }else if(bankname.equalsIgnoreCase("msbbank")){
            bin = "970426";
        }else if(bankname.equalsIgnoreCase("vpbank")){
            bin = "970432";
        }

        boolean check_connect = false;
        int count = 10;
        String datacontent = null;
        do{
            datacontent = null;
            count--;
            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("http://ac18.ls136.com/api/checkName?stk=111111&bankCode=970432")
                    .method("GET", null)
                    .build();
                Response response = client.newCall(request).execute();
                datacontent = response.body().string();
                if(datacontent.contains("isSuccess")){
                    check_connect = true;
                }else{
                    check_connect = false;
                }

        }while (check_connect == false && count > 0);

        if(datacontent.contains("\"isSuccess\": true")){
            return true;
        }else{
            return false;
        }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

//    public static void main(String[] args) {
//        KiemTra kt = new KiemTra();
//    }

}
