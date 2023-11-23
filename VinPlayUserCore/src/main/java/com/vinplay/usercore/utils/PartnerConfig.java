/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vinplay.usercore.utils;

//import bitzero.util.common.business.Debug;
import com.vinplay.usercore.dao.impl.GameConfigDaoImpl;
import org.apache.catalina.Host;
import org.apache.log4j.Logger;
//import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

/**
 * @author HA
 */
public class PartnerConfig {
    public static String CongGachThe = "";
    public static String GachTheSecretKey = "";
    public static String GachTheCallBackUrl = "";
    public static String NapTienGaSecretKey = "";
    public static String NapTienGaApiKey = "";
    public static String MuaCardSecretKey = "";
    public static String MuaCardCallback = "";
    public static String ESMSApiKey = "";
    public static String ESMSSecretKey = "";
    public static String MuaCard24hPartnerId = "";
    public static String MuaCard24hPartnerKey = "";
    public static String ECardPartnerId = "";
    public static String ECardUsername = "";
    public static String Client = "";
    public static String RutCuocApiKey = "";
    public static String HostBot = "http://bot-web-service.default:6868/botteleapi";
    public static String SMSPartner = "";
    public static String Airpay24hUsername = "";
    public static String Airpay24hPassword = "";
    public static String NapTienGaApiKeyVip52 = "";
    public static String NapTienGaSecretKeyVip52 = "";
    public static String KhoTheEndpoint = "";
    public static String KhoThePartnerId = "";
    public static String KhoTheUsername = "";
    public static String KhoThePassword = "";
    public static String KhoThePrivateKey = "";
    public static String KhoThePublicKey = "";
    public static String MomoZoanPartnerName = "";
    public static String MomoZoanPartnerKey = "";
    public static String MomoZoanEndpoint = "";
    public static String ZoanCardEndpoint = "";
    public static String ZoanCardUsername = "";
    public static String ZoanCardPassword = "";
    public static String ZoanCardCallbackUrl = "";
    private static final org.apache.log4j.Logger logger = Logger.getLogger((String)"recharge");

    public static String SMSBrandNameUserName = "";
    public static String SMSBrandNamePassword = "";
    public static String SMSBrandNameUrlApi = "";
    public static String SMSBrandNameName = "";

    public static void ReadConfig() {
//        FileInputStream input = null;
        try {
//            if (configFile == null || configFile.isEmpty()) {
//                configFile = "config/partner.properties";
//            }
//            Properties prop = new Properties();
//            input = new FileInputStream(configFile);
//            prop.load(input);
//            Debug.trace("PartnerConfig");
            GameConfigDaoImpl dao = new GameConfigDaoImpl();
            String partnerConfig = dao.getGameCommon("partner");
//            Debug.trace("PartnerConfig:"+partnerConfig);
            logger.debug("PartnerConfig:"+partnerConfig);

            JSONObject json = (JSONObject)new JSONParser().parse(partnerConfig);
//            Debug.trace("PartnerConfig 1:"+json.toString());

            CongGachThe = (String)json.get("cong_gach_the");
//            Debug.trace("CongGachThe:"+CongGachThe);

            GachTheSecretKey = (String)json.get("gachthe_api_key");
            GachTheCallBackUrl = (String)json.get("gachthe_callback_url");
            NapTienGaApiKey = (String)json.get("naptienga_api_key");
            NapTienGaSecretKey = (String)json.get("naptienga_secret_key");
            ESMSApiKey = (String)json.get("esm_api_key");
            ESMSSecretKey = (String)json.get("esm_secret_key");
            MuaCardSecretKey = (String)json.get("mua_card_secret_key");
            MuaCardCallback = (String)json.get("mua_card_callback");
            MuaCard24hPartnerId = (String)json.get("mua_card_24h_partner_id");
            MuaCard24hPartnerKey = (String)json.get("mua_card_24h_partner_key");
            ECardPartnerId = (String)json.get("ecard_partner_id");
            ECardUsername = (String)json.get("ecard_username");
            Client = (String)json.get("client");
            RutCuocApiKey = (String)json.get("rut_cuoc_api_key");
            HostBot = (String)json.get("host_bot");
            SMSPartner = (String)json.get("sms_partner");
            Airpay24hUsername = (String)json.get("airpay24h_username");
            Airpay24hPassword = (String)json.get("airpay24h_password");
            NapTienGaApiKeyVip52 = (String)json.get("naptienga_api_key_vip52");
            NapTienGaSecretKeyVip52 = (String)json.get("naptienga_secret_key_vip52");
            KhoTheEndpoint = (String)json.get("khothe_endpoint");
            KhoThePartnerId = (String)json.get("khothe_partner_id");
            KhoTheUsername = (String)json.get("khothe_username");
            KhoThePassword = (String)json.get("khothe_password");
            KhoThePrivateKey = (String)json.get("khothe_private_key");
            KhoThePublicKey = (String)json.get("khothe_public_key");
            MomoZoanPartnerKey = (String)json.get("momo_zoan_partner_key");
            MomoZoanPartnerName = (String)json.get("momo_zoan_partner_name");
            MomoZoanEndpoint = (String)json.get("momo_zoan_endpoint");
            ZoanCardEndpoint = (String)json.get("zoan_card_endpoint");
            ZoanCardUsername = (String)json.get("zoan_card_username");
            ZoanCardPassword = (String)json.get("zoan_card_password");
            ZoanCardCallbackUrl = (String)json.get("zoan_card_callback_url");

            //sms brand name

            SMSBrandNamePassword = (String)json.get("sms_brandname_password");
            SMSBrandNameUserName = (String)json.get("sms_brandname_username");
            SMSBrandNameUrlApi = (String)json.get("sms_brandname_url");
            SMSBrandNameName = (String)json.get("sms_brandname_name");
        } catch (Exception ex) {
//            Debug.trace(ex.getMessage());
        }
    }
}
