package com.vinplay.api.entities;

import bitzero.server.util.MD5;

public class DepositLodeModel {
    public static final String DOMAIN = "https://api.bandoluuniem.net/partner/";
    public static final String GAMENAME = "sun";
    public static final String PrivateKey = "e807f1sf282d1sfsxx010cae0738a19f";
    public String userName;
    public Long amount;
    public String signature;

    public DepositLodeModel(String userName, Long amount) {
        this.userName = userName;
        this.amount = amount;
    }

    public String getMd5Deposit() {
        String rawData = userName + GAMENAME + amount + PrivateKey;
        System.out.println(rawData);
        return MD5.getInstance().getHash(rawData);
    }

//    public static void main(String[] args) {
//        DepositSabaModel model = new DepositSabaModel("govn",6542963l,"dfasdfasdf","dfasdfasdf2",1000l);
//        String urlRequest = DepositSabaModel.DOMAIN+"/service/quickplay?gameName="+DepositSabaModel.GAMENAME+"&accountID=go"+model.accountID+"&userName="+model.userName+"&nickName="+model.nickName+"&signature="+model.getMd5Deposit()+"&isMobile="+1;
//        System.out.println(urlRequest);
//
//    }
}
