/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.usercore.service.impl.OtpServiceImpl
 *  com.vinplay.usercore.service.impl.UserServiceImpl
 *  com.vinplay.usercore.utils.GameCommon
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.response.BaseResponseModel
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.backend.processors.money;

import bitzero.util.common.business.Debug;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.OtpServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.usercore.utils.PartnerConfig;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.response.BaseResponseModel;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.json.JSONObject;

public class UpdateMoneyUserBanCaProcessor
        implements BaseProcessor<HttpServletRequest, String> {

    private static final Logger logger = Logger.getLogger((String) "backend");

    public synchronized String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest) param.get();
        BaseResponseModel response = new BaseResponseModel(false, "1001");
        try {
            String nickname = request.getParameter("nn");
            long money = Long.valueOf(request.getParameter("mn"));        
            String hash = request.getParameter("h");
            String hs = nickname + money + "game000#98@88";
            hs = VinPlayUtils.getMD5Hash(hs).toLowerCase();
            logger.debug((Object) ("vcl: " + hs + ", money: " + hash));
            if (!hash.equals(hs))
            {
                return response.toJson();
            }
            logger.debug((Object) ("Request UpdateMoneyUser: nickname: " + nickname + ", money: " + money));              
            if (nickname != null && money != 0L) {
                UserServiceImpl service = new UserServiceImpl();
                response = service.updateMoneyFromAdmin(nickname, money, "vin", "Exchange", "HamCaMap", "Doi tien Ham Ca Map");
                if(!response.isSuccess())
                    return response.toJson();
                boolean resultUpdateFish = service.UpdateFishMoney(nickname, money);
                if(!resultUpdateFish){
                    response.setSuccess(false);
                    return response.toJson();
                }
                return response.toJson();
                // log
                // notify

                /*

                try {
                    HttpClient httpClient = HttpClientBuilder.create().build();
                    String url = "";
                    if ("XXENG".equals(PartnerConfig.Client)) {
                        url = PartnerConfig.HostBot;
                    } else if ("R68".equals(PartnerConfig.Client)) {
                        url = PartnerConfig.HostBot;
                    } else {
                        url = PartnerConfig.HostBot;
                    }
                    HttpPost httpPost = new HttpPost(url + "/rpadmin/ozawasecret1243/3");
                    httpPost.addHeader("Content-Type", "application/json");

                    JSONObject obj = new JSONObject();
                    obj.put("sender_nick_name", "admin");
                    String serverName = "xxeng";
                    UserService userService = new UserServiceImpl();
                    UserModel receiverModel = userService.getUserByNickName(nickname);
                    if (receiverModel != null && receiverModel.getClient() != null && !receiverModel.getClient().equals("")) {
                        if (receiverModel.getClient().equals("M")) {
                                serverName = "manVip";
                            }
                            else if (receiverModel.getClient().equals("R")) {
                                serverName = "r99";
                            }
                        else if (receiverModel.getClient().equals("V")) {
                            serverName = "Vip52";
                        }
                    }
                    obj.put("serverName", serverName);
                    obj.put("receiver_nick_name", nickname);
                    obj.put("money", money);
                    obj.put("description", "Doi tien Ham Ca Map");
                    obj.put("created_time", System.currentTimeMillis());
                    obj.put("action", "ADMIN_TRANSFER_MONEY");
                    StringEntity requestEntity = new StringEntity(obj.toString(), "UTF-8");
                    httpPost.setEntity(requestEntity);

                    // add request header
                    HttpResponse httpResponse = httpClient.execute(httpPost);

                    BufferedReader rd = new BufferedReader(
                            new InputStreamReader(httpResponse.getEntity().getContent()));

                    StringBuffer result = new StringBuffer();
                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                    logger.debug((Object) ("LobbyModule bot tele response: " + result));
                } catch (Exception ex) {
                    logger.debug((Object) ("LobbyModule error: " + ex));
                }
                // Kiem tra user nhan tien
                // notify cho dai ly
                UserServiceImpl userService = new UserServiceImpl();
                UserModel userReceive = userService.getUserByNickName(nickname);
                if (userReceive != null && (userReceive.getDaily() == 1 || userReceive.getDaily() == 2)) {
                    // notify 
                    try {
                        HttpClient httpClient = HttpClientBuilder.create().build();
                        String url = "";
                        if ("XXENG".equals(PartnerConfig.Client)) {
                            url = PartnerConfig.HostBot;
                        } else if ("R68".equals(PartnerConfig.Client)) {
                            url = PartnerConfig.HostBot;
                        } else {
                            url = PartnerConfig.HostBot;
                        }
                        HttpPost httpPost = new HttpPost(url + "/rpadmin/ozawasecret1243/10");
                        httpPost.addHeader("Content-Type", "application/json");

                        JSONObject objSend = new JSONObject();
                        objSend.put("sender_nick_name", "Admin");
                        String serverName = "xxeng";
                        UserModel receiverModel = userService.getUserByNickName(nickname);
                        if (receiverModel != null && receiverModel.getClient() != null && !receiverModel.getClient().equals("")) {
                            if (receiverModel.getClient().equals("M")) {
                                serverName = "manVip";
                            }
                            else if (receiverModel.getClient().equals("R")) {
                                serverName = "r99";
                            }
                            else if (receiverModel.getClient().equals("V")) {
                                serverName = "Vip52";
                            }
                        }
                        objSend.put("serverName", serverName);
                        objSend.put("receiver_nick_name", nickname);
                        objSend.put("receiver_mobile", userReceive.getMobile());
                        objSend.put("is_agent", true);
                        objSend.put("money", money);
                        objSend.put("description", "Doi tien Ham Ca Map");
                        // get current money receive                           
                        long currentMoneyReceive = userReceive.getCurrentMoney("vin");
                        objSend.put("previous_money", currentMoneyReceive);
                        objSend.put("current_money", currentMoneyReceive + money);
                        long date = System.currentTimeMillis();
                        int offset = TimeZone.getDefault().getOffset(date);
                        objSend.put("created_time", date + offset);

                        StringEntity requestEntity = new StringEntity(objSend.toString(), "UTF-8");
                        httpPost.setEntity(requestEntity);

                        // add request header
                        HttpResponse response2 = httpClient.execute(httpPost);

                        BufferedReader rd = new BufferedReader(
                                new InputStreamReader(response2.getEntity().getContent()));

                        StringBuffer result = new StringBuffer();
                        String line = "";
                        while ((line = rd.readLine()) != null) {
                            result.append(line);
                        }
                        logger.info((Object) ("LobbyModule bot tele response: " + result));
                    } catch (Exception ex) {
                        logger.info((Object) ("LobbyModule error: " + ex));
                    }
                }
                */
            }
        } catch (Exception e) {
            logger.debug((Object) e);
        }

        logger.debug((Object) ("Response UpdateMoneyUser: " + response.toJson()));
        return response.toJson();
    }
}
