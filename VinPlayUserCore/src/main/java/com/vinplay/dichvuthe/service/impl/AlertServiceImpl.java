/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IMap
 *  com.vinplay.vbee.common.hazelcast.HazelcastClientFactory
 *  com.vinplay.vbee.common.messages.BaseMessage
 *  com.vinplay.vbee.common.messages.BrandnameMessage
 *  com.vinplay.vbee.common.models.cache.UserCacheModel
 *  com.vinplay.vbee.common.rmq.RMQApi
 *  org.apache.log4j.Logger
 *  org.json.JSONObject
 *  org.json.simple.parser.JSONParser
 */
package com.vinplay.dichvuthe.service.impl;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.brandname.enties.ListPhone;
import com.vinplay.brandname.service.APISMSLocator;
import com.vinplay.brandname.service.IAPISMS;
import com.vinplay.dichvuthe.client.VinplayClient;
import com.vinplay.dichvuthe.service.AlertService;
import com.vinplay.dichvuthe.service.LogSMSDAO;
import com.vinplay.dichvuthe.service.LogSMSDAOImpl;
import com.vinplay.usercore.service.impl.SmsBrandNameService;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.usercore.utils.PartnerConfig;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.messages.BaseMessage;
import com.vinplay.vbee.common.messages.BrandnameMessage;

import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.rmq.RMQApi;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.python.parser.ast.Str;

public class AlertServiceImpl
implements AlertService {
    private static final Logger logger = Logger.getLogger((String)"api");
    //private static final String apiKey = "83A4C760ABE6C4B8620F1147456499";
    //private static final String secretKey = "F9CF275108AC1ED762C80EA222202A";

    @Override
    public boolean sendSMS2List(List<String> receives, String content, boolean call) {
        return this.sendSMS(receives, content, call);
    }

    @Override
    public boolean SendSMSEsms(String phone, String messge) {
        try {
            String messageText = URLEncoder.encode(messge, "UTF-8");
            //URL url = new URL("http://rest.esms.vn/MainService.svc/json/SendMultipleMessage_V4_get?Phone=" + phone + "&Content=" + messageText + "&ApiKey="+ apiKey +"&SecretKey="+ secretKey +"&IsUnicode=0&SmsType=2&brandname=Verify");
            //trangxu2018@gmail.com
            //Apikey=71BE071F1632792CEB9018BC521CE9 Secretkey=DE64CF710AA20DEB64A120415B249E
            String surl = "http://rest.esms.vn/MainService.svc/json/SendMultipleMessage_V4_get?Phone=" + phone + "&Content=" + messageText + "&ApiKey="+ PartnerConfig.ESMSApiKey +"&SecretKey="+ PartnerConfig.ESMSSecretKey +"&IsUnicode=0&brandname=Verify&SmsType=2";
            //logger.debug("url:" + surl);
            URL url = new URL(surl);
            //...teamthanhviet9999@gmail.com
            //Apikey=83A4C760ABE6C4B8620F1147456499 Secretkey=F9CF275108AC1ED762C80EA222202A

            //URL url = new URL("http://rest.esms.vn/MainService.svc/json/SendMultipleMessage_V4_get?Phone=" + phone + "&Content=" + messageText + "&ApiKey=83A4C760ABE6C4B8620F1147456499&SecretKey=F9CF275108AC1ED762C80EA222202A&IsUnicode=0&brandname=Verify&SmsType=2");

            //logger.debug("key:" + PartnerConfig.ESMSApiKey);
            //logger.debug("secret:" + PartnerConfig.ESMSSecretKey);
            HttpURLConnection request = (HttpURLConnection)url.openConnection();
            request.setConnectTimeout(90000);
            request.setUseCaches(false);
            request.setDoOutput(true);
            request.setDoInput(true);
            HttpURLConnection.setFollowRedirects(true);
            request.setInstanceFollowRedirects(true);
            request.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            request.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String result = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                result = result.concat(line);
            }
            logger.debug("json send sms:" + result);                 
            // just save to log
            if (result.contains("<CodeResult>"))
            {
                LogSMSDAO log = new LogSMSDAOImpl();
                if (result.contains("<CodeResult>100</CodeResult>"))
                {
                    log.saveLog(VinPlayUtils.genTransactionId(6688), phone, messge, "1", VinPlayUtils.getCurrentDateTime(), "OTP");                    
                    return true;
                }
                else
                {
                    log.saveLog(VinPlayUtils.genTransactionId(6688), phone, messge, "0", VinPlayUtils.getCurrentDateTime(), "OTP");
                    return false;
                }                
            }
            else
            {
                JSONObject json = (JSONObject)new JSONParser().parse(result);       
                boolean rs = json != null && json.get("CodeResult") != null && String.valueOf(json.get("CodeResult")).equals("100");           
                LogSMSDAO log = new LogSMSDAOImpl();
                if (rs)
                {
                    log.saveLog(VinPlayUtils.genTransactionId(6688), phone, messge, "1", VinPlayUtils.getCurrentDateTime(), "OTP");
                }
                else
                {
                    log.saveLog(VinPlayUtils.genTransactionId(6688), phone, messge, "0", VinPlayUtils.getCurrentDateTime(), "OTP");
                }
                return rs;
            }
        }
        catch (Exception ex) {            
            logger.debug("ex:" + ex.getMessage());
            StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                String sStackTrace = sw.toString(); // stack trace as a string
            logger.debug("trace:" + sStackTrace);    
            return false;
        }
    }
    
    @Override
    public boolean SendVoiceOTPESMS(String phone, String otp) {
        try {                        
//            String surl = "http://rest.esms.vn/MainService.svc/json/SendMultipleMessage_V4_get?Phone=" + phone + "&Content=" 
//                    + messageText + "&ApiKey="+ PartnerConfig.ESMSApiKey +"&SecretKey="+ PartnerConfig.ESMSSecretKey +"&IsUnicode=0&brandname=Verify&SmsType=2";
            //logger.debug("url:" + surl);
            String surl = "http://voiceapi.esms.vn/MainService.svc/json/VoiceOTP?ApiKey="+ PartnerConfig.ESMSApiKey + 
                    "&SecretKey="+ PartnerConfig.ESMSSecretKey +"&Phone="+ phone +"&Code="+ otp +"&Speed=-3&Voice=hatieumai";
            URL url = new URL(surl);
            
            HttpURLConnection request = (HttpURLConnection)url.openConnection();
            request.setConnectTimeout(90000);
            request.setUseCaches(false);
            request.setDoOutput(true);
            request.setDoInput(true);
            HttpURLConnection.setFollowRedirects(true);
            request.setInstanceFollowRedirects(true);
            request.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            request.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String result = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                result = result.concat(line);
            }
            logger.debug("json send sms:" + result);                 
            // just save to log
            if (result.contains("<CodeResult>"))
            {
                LogSMSDAO log = new LogSMSDAOImpl();
                if (result.contains("<CodeResult>100</CodeResult>"))
                {
                    log.saveLog(VinPlayUtils.genTransactionId(6688), phone, otp, "1", VinPlayUtils.getCurrentDateTime(), "OTP");                    
                    return true;
                }
                else
                {
                    log.saveLog(VinPlayUtils.genTransactionId(6688), phone, otp, "0", VinPlayUtils.getCurrentDateTime(), "OTP");
                    return false;
                }                
            }
            else
            {
                JSONObject json = (JSONObject)new JSONParser().parse(result);       
                boolean rs = json != null && json.get("CodeResult") != null && String.valueOf(json.get("CodeResult")).equals("100");           
                LogSMSDAO log = new LogSMSDAOImpl();
                if (rs)
                {
                    log.saveLog(VinPlayUtils.genTransactionId(6688), phone, otp, "1", VinPlayUtils.getCurrentDateTime(), "OTP");
                }
                else
                {
                    log.saveLog(VinPlayUtils.genTransactionId(6688), phone, otp, "0", VinPlayUtils.getCurrentDateTime(), "OTP");
                }
                return rs;
            }
        }
        catch (Exception ex) {            
            logger.debug("ex:" + ex.getMessage());
            StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                String sStackTrace = sw.toString(); // stack trace as a string
            logger.debug("trace:" + sStackTrace);    
            return false;
        }
    }
    
    @Override
    public boolean SendSMSRutCuoc(String phone, String messge) {
        try {
            String messageText = URLEncoder.encode(messge, "UTF-8");
            //URL url = new URL("http://rest.esms.vn/MainService.svc/json/SendMultipleMessage_V4_get?Phone=" + phone + "&Content=" + messageText + "&ApiKey="+ apiKey +"&SecretKey="+ secretKey +"&IsUnicode=0&SmsType=2&brandname=Verify");
            //trangxu2018@gmail.com
            //Apikey=71BE071F1632792CEB9018BC521CE9 Secretkey=DE64CF710AA20DEB64A120415B249E
            String surl = "http://rutcuoc.com:10009/api/NTH/RegCharge?apiKey=" + PartnerConfig.RutCuocApiKey + "&type=sms&phoneNum=" + phone + "&content=" + messageText;
            //logger.debug("url:" + surl);
            URL url = new URL(surl);
            //...teamthanhviet9999@gmail.com
            //Apikey=83A4C760ABE6C4B8620F1147456499 Secretkey=F9CF275108AC1ED762C80EA222202A

            //URL url = new URL("http://rest.esms.vn/MainService.svc/json/SendMultipleMessage_V4_get?Phone=" + phone + "&Content=" + messageText + "&ApiKey=83A4C760ABE6C4B8620F1147456499&SecretKey=F9CF275108AC1ED762C80EA222202A&IsUnicode=0&brandname=Verify&SmsType=2");

            //logger.debug("key:" + PartnerConfig.ESMSApiKey);
            //logger.debug("secret:" + PartnerConfig.ESMSSecretKey);
            HttpURLConnection request = (HttpURLConnection)url.openConnection();
            request.setConnectTimeout(90000);
            request.setUseCaches(false);
            request.setDoOutput(true);
            request.setDoInput(true);
            HttpURLConnection.setFollowRedirects(true);
            request.setInstanceFollowRedirects(true);
            request.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            request.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String result = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                result = result.concat(line);
            }
            logger.debug("json send sms:" + result);      
            LogSMSDAO log = new LogSMSDAOImpl();
            // just save to log            
            log.saveLog(VinPlayUtils.genTransactionId(6688), phone, messge, "1", VinPlayUtils.getCurrentDateTime(), "OTP");
            return true;    
        }
        catch (Exception ex) {            
            logger.debug("ex:" + ex.getMessage());
            StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                String sStackTrace = sw.toString(); // stack trace as a string
            logger.debug("trace:" + sStackTrace);    
            return false;
        }
    }
    
    @Override
    public boolean SendSMSAirpay(String phone, String messge) {
        try {
            LogSMSDAO log = new LogSMSDAOImpl();
            String id = LoginAirpay();     
            if (id != null && !"".equals(id))
            {
                JSONObject obj = new JSONObject();
                obj.put("UserId", id);
                obj.put("SDT", phone);
                obj.put("Content", messge);

                String surl = "http://www.airpay24h.com/api/APIData/InsertSim";            
                URL url = new URL(surl);            
                HttpURLConnection request = (HttpURLConnection)url.openConnection();
                request.setConnectTimeout(90000);
                request.setUseCaches(false);
                request.setDoOutput(true);
                request.setDoInput(true);
                HttpURLConnection.setFollowRedirects(true);
                request.setInstanceFollowRedirects(true);
                request.setRequestProperty("Content-Type", "application/json");
                request.setRequestMethod("POST");

                OutputStream os = request.getOutputStream();
                os.write(obj.toString().getBytes("UTF-8"));
                os.close();            

                BufferedReader rd = new BufferedReader(new InputStreamReader(request.getInputStream()));
                String result = "";
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result = result.concat(line);
                }
                logger.debug("json send sms:" + result);
                JSONObject json = (JSONObject)new JSONParser().parse(result);    
                if (json != null)
                {
                    if (json.getBoolean("Status"))
                    {
                        log.saveLog(VinPlayUtils.genTransactionId(6688), phone, messge, "1", VinPlayUtils.getCurrentDateTime(), "OTP");
                        return true;
                    }
                    else
                    {
                        log.saveLog(VinPlayUtils.genTransactionId(6688), phone, messge, "0", VinPlayUtils.getCurrentDateTime(), "OTP");
                    }
                }
                else
                {
                    log.saveLog(VinPlayUtils.genTransactionId(6688), phone, messge, "0", VinPlayUtils.getCurrentDateTime(), "OTP");
                }                
            }
            else
            {
                log.saveLog(VinPlayUtils.genTransactionId(6688), phone, messge, "0", VinPlayUtils.getCurrentDateTime(), "OTP");
            }    
            return false;
        }
        catch (Exception ex) {            
            logger.debug("ex:" + ex.getMessage());
            StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                String sStackTrace = sw.toString(); // stack trace as a string
            logger.debug("trace:" + sStackTrace);    
            return false;
        }
    }

    @Override
    public boolean SendSmsBrandName(String phone, String message) {
        try{
            SmsBrandNameService sv = new SmsBrandNameService(PartnerConfig.SMSBrandNameUrlApi, PartnerConfig.SMSBrandNameUserName,PartnerConfig.SMSBrandNamePassword,PartnerConfig.SMSBrandNameName);
            String result = sv.SendGet(message, phone, VinPlayUtils.genTransactionId(6688));
            if(result.equals(""))
                return false;
            return true;
        }catch (Exception ex){
            logger.debug("ex:" + ex.getMessage());
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String sStackTrace = sw.toString(); // stack trace as a string
            logger.debug("trace:" + sStackTrace);
            return false;
        }
    }

    String LoginAirpay()
    {
        try {            
            JSONObject obj = new JSONObject();
            obj.put("UserName", PartnerConfig.Airpay24hUsername);
            obj.put("Password", PartnerConfig.Airpay24hPassword);
            String surl = "http://www.airpay24h.com/api/APIData/Login";
            URL url = new URL(surl);            
            HttpURLConnection request = (HttpURLConnection)url.openConnection();
            request.setConnectTimeout(90000);
            request.setUseCaches(false);
            request.setDoOutput(true);
            request.setDoInput(true);
            HttpURLConnection.setFollowRedirects(true);
            request.setInstanceFollowRedirects(true);
            request.setRequestProperty("Content-Type", "application/json");
            request.setRequestMethod("POST");
            
            OutputStream os = request.getOutputStream();
            os.write(obj.toString().getBytes("UTF-8"));
            os.close();
            
            BufferedReader rd = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String result = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                result = result.concat(line);
            }
            JSONObject json = (JSONObject)new JSONParser().parse(result);    
            if (json != null)
            {
                return json.getString("ID");
            }
            return "";
        }
        catch (Exception ex) {            
            logger.debug("ex:" + ex.getMessage());
            StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                String sStackTrace = sw.toString(); // stack trace as a string
            logger.debug("trace:" + sStackTrace);    
            return "";
        }
    }

    @Override
    public boolean sendSMS2One(String mobile, String content, boolean call) {
        try {
            return true;
//            String messageText = URLEncoder.encode(content, "UTF-8");
//            //URL url = new URL("http://rest.esms.vn/MainService.svc/json/SendMultipleMessage_V4_get?Phone=" + phone + "&Content=" + messageText + "&ApiKey="+ apiKey +"&SecretKey="+ secretKey +"&IsUnicode=0&SmsType=2&brandname=Verify");
//            //trangxu2018@gmail.com
//            //Apikey=71BE071F1632792CEB9018BC521CE9 Secretkey=DE64CF710AA20DEB64A120415B249E
//            String surl = "http://rest.esms.vn/MainService.svc/json/SendMultipleMessage_V4_get?Phone=" + mobile + "&Content=" + messageText + "&ApiKey="+ PartnerConfig.ESMSApiKey +"&SecretKey="+ PartnerConfig.ESMSSecretKey +"&IsUnicode=0&brandname=Verify&SmsType=2";
//            //logger.debug("url:" + surl);
//            URL url = new URL(surl);
//            //...teamthanhviet9999@gmail.com
//            //Apikey=83A4C760ABE6C4B8620F1147456499 Secretkey=F9CF275108AC1ED762C80EA222202A
//
//            //URL url = new URL("http://rest.esms.vn/MainService.svc/json/SendMultipleMessage_V4_get?Phone=" + phone + "&Content=" + messageText + "&ApiKey=83A4C760ABE6C4B8620F1147456499&SecretKey=F9CF275108AC1ED762C80EA222202A&IsUnicode=0&brandname=Verify&SmsType=2");
//
//            //logger.debug("key:" + PartnerConfig.ESMSApiKey);
//            //logger.debug("secret:" + PartnerConfig.ESMSSecretKey);
//            HttpURLConnection request = (HttpURLConnection)url.openConnection();
//            request.setConnectTimeout(90000);
//            request.setUseCaches(false);
//            request.setDoOutput(true);
//            request.setDoInput(true);
//            HttpURLConnection.setFollowRedirects(true);
//            request.setInstanceFollowRedirects(true);
//            request.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            request.setRequestMethod("GET");
//            BufferedReader rd = new BufferedReader(new InputStreamReader(request.getInputStream()));
//            String result = "";
//            String line = "";
//            while ((line = rd.readLine()) != null) {
//                result = result.concat(line);
//            }
//            logger.debug("json send sms:" + result);
//            logger.debug("json send sms:" + result);
//            // just save to log
//            if (result.contains("<CodeResult>"))
//            {
//                LogSMSDAO log = new LogSMSDAOImpl();
//                if (result.contains("<CodeResult>100</CodeResult>"))
//                {
//                    log.saveLog(VinPlayUtils.genTransactionId(6688), mobile, content, "1", VinPlayUtils.getCurrentDateTime(), "OTP");
//                    return true;
//                }
//                else
//                {
//                    log.saveLog(VinPlayUtils.genTransactionId(6688), mobile, content, "0", VinPlayUtils.getCurrentDateTime(), "OTP");
//                    return false;
//                }
//            }
//            else
//            {
//                JSONObject json = (JSONObject)new JSONParser().parse(result);
//                boolean rs = json != null && json.get("CodeResult") != null && String.valueOf(json.get("CodeResult")).equals("100");
//                LogSMSDAO log = new LogSMSDAOImpl();
//                if (rs)
//                {
//                    log.saveLog(VinPlayUtils.genTransactionId(6688), mobile, content, "1", VinPlayUtils.getCurrentDateTime(), "OTP");
//                }
//                else
//                {
//                    log.saveLog(VinPlayUtils.genTransactionId(6688), mobile, content, "0", VinPlayUtils.getCurrentDateTime(), "OTP");
//                }
//                return rs;
//            }
        }
        catch (Exception ex) {
            logger.debug("ex:" + ex.getMessage());
            StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                String sStackTrace = sw.toString(); // stack trace as a string
            logger.debug("trace:" + sStackTrace);
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private boolean sendSMS(List<String> receives, String content, boolean call) {
        try {
            HazelcastInstance instance = HazelcastClientFactory.getInstance();
            IMap map = instance.getMap("cacheConfig");
            if (!map.containsKey((Object)"BRAND_NAME_ID")) return false;
            try {
                map.lock((Object)"BRAND_NAME_ID");
                long brandNameID = Long.parseLong((String)map.get((Object)"BRAND_NAME_ID"));
                APISMSLocator localtor = new APISMSLocator();
                IAPISMS service = localtor.getBasicHttpBinding_IAPISMS();
                if (content == null) return false;
                if (receives == null) return false;
                if (receives.size() < 1) return false;
                if (GameCommon.getValueInt("BRANDNAME_OPEN") == 1) {
                    String code = "-1";
                    ArrayList<ListPhone> listPhone = new ArrayList<ListPhone>();
                    try {
                        if (receives.size() == 1) {
                            map.put("BRAND_NAME_ID", (Object)String.valueOf(++brandNameID));
                            code = service.pushMsg2PhoneDLVR(GameCommon.BRANDNAME_SENDER, content, receives.get(0), String.valueOf(brandNameID), GameCommon.BRANDNAME_CLIENT_ID, GameCommon.BRANDNAME_USER, GameCommon.BRANDNAME_PASS);
                        } else {
                            for (String mobile : receives) {
                                ListPhone lp = new ListPhone(this.revertMobile(mobile), String.valueOf(++brandNameID));
                                listPhone.add(lp);
                            }
                            map.put("BRAND_NAME_ID", (Object)String.valueOf(brandNameID));
                            ListPhone[] arrPhone = listPhone.toArray(new ListPhone[listPhone.size()]);
                            code = service.pushMsg2ListPhoneDLVR(GameCommon.BRANDNAME_SENDER, content, arrPhone, GameCommon.BRANDNAME_CLIENT_ID, GameCommon.BRANDNAME_USER, GameCommon.BRANDNAME_PASS);
                        }
                    }
                    catch (Exception e) {
                        logger.debug((Object)e);
                    }
                    if (listPhone.size() >= 1) {
                        for (ListPhone lp2 : listPhone) {
                            BrandnameMessage message = new BrandnameMessage(lp2.getRequestId(), GameCommon.BRANDNAME_SENDER, content, lp2.getPhone(), code);
                            RMQApi.publishMessage((String)"queue_otp", (BaseMessage)message, (int)202);
                        }
                    } else {
                        BrandnameMessage message2 = new BrandnameMessage(String.valueOf(brandNameID), GameCommon.BRANDNAME_SENDER, content, receives.get(0), code);
                        RMQApi.publishMessage((String)"queue_otp", (BaseMessage)message2, (int)202);
                    }
                    if (!code.equals("1")) return false;
                    boolean message2 = true;
                    return message2;
                }
                if (GameCommon.getValueInt("DVT_SMS_OPEN") != 1) return false;
                VinplayClient.sendAleftSMS(receives, content, call);
                boolean code = true;
                return code;
            }
            catch (Exception e2) {
                logger.debug((Object)e2);
                return false;
            }
            finally {
                map.unlock((Object)"BRAND_NAME_ID");
            }
        }
        catch (Exception e3) {
            logger.debug((Object)e3);
        }
        return false;
    }

    @Override
    public boolean alert2List(List<String> receives, String content, boolean call) {
        return this.alert(receives, content, call);
    }

    @Override
    public boolean alert2One(String mobile, String content, boolean call) {
        ArrayList<String> receives = new ArrayList<String>();
        receives.add(mobile);
        return this.alert(receives, content, call);
    }

    private boolean alert(List<String> receives, String content, boolean call) {
        try {
            VinplayClient.aleft(receives, content, call);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.debug((Object)e);
            return false;
        }
    }

    private String revertMobile(String mobile) {
        if (mobile.substring(0, 1).equals("0")) {
            return "84" + mobile.substring(1);
        }
        return mobile;
    }

    private String revertMobileBegin84(String mobile) {
        if (mobile.substring(0, 1).equals("0")) {
            return "84" + mobile.substring(1);
        }
        return mobile;
    }

    private String revertMobileBegin0(String mobile) {
        if (mobile.substring(0, 2).equals("84")) {
            return "0" + mobile.substring(2);
        }
        return mobile;
    }

    @Override
    public boolean sendEmail(String subject, String content, List<String> receives) {
        try {
            VinplayClient.sendEmail(subject, content, receives);
        }
        catch (Exception e) {
            logger.debug((Object)e);
        }
        return true;
    }

    @Override
    public boolean sendSMS2User(String username, String content) {
        String mobile;
        UserCacheModel model;
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap<String, UserModel> userMap = client.getMap("users");
        if (userMap.containsKey((Object)username) && (model = (UserCacheModel)userMap.get((Object)username)) != null && model.isHasMobileSecurity() && (mobile = model.getMobile()) != null && !mobile.isEmpty()) {
            try {
                return this.sendSMS2One(mobile, content, false);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return false;
    }
}

