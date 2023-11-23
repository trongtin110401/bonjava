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
import com.vinplay.lognaprut.HistoryTransConst;
import com.vinplay.lognaprut.HistoryTransDao;
import com.vinplay.lognaprut.entities.HistoryTransModel;
import com.vinplay.lognaprut.impl.HistoryTransDaoImpl;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.OtpServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.usercore.utils.PartnerConfig;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.response.BaseResponseModel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;

import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.json.JSONObject;

public class UpdateMoneyUserProcessor
        implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "backend");

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest) param.get();
        BaseResponseModel response = new BaseResponseModel(false, "1001");
        try {
            long maxmoney = 1000000000;
            String actionName = request.getParameter("ac");
            String nickname = request.getParameter("nn");
            long money = Long.valueOf(request.getParameter("mn"));
            String moneyType = request.getParameter("mt");
            String reason = request.getParameter("rs");
            String otp = request.getParameter("otp");
            String type = request.getParameter("type");
            String nicknameSend = request.getParameter("nns");
            logger.debug((Object) ("Request UpdateMoneyUser: nickname: " + nickname + ", money: " + money + ", moneyType: " + moneyType + ", reason: " + reason + ", otp: " + otp + ", otpType: " + type));
            if (nickname != null && reason != null && !reason.isEmpty() && money != 0L && moneyType != null
                    && (moneyType.equals("vin") || moneyType.equals("xu")) && otp != null && type != null && (type.equals("1") || type.equals("0"))) {
                if (money <= maxmoney) {
                    OtpServiceImpl otpService = new OtpServiceImpl();
                    int code = 3;
                    if (moneyType.equals("vin")) {
                        if (otp.equals("1") || otp.equals("")) {
                            code = 0;
                        } else {
                            String admin;
                            String[] arr = GameCommon.getValueStr((String) "SUPER_ADMIN").split(",");
                            int i = 0;
                            String[] array = arr;
                            int length = array.length;
//                    for (int j = 0; j < length && (code = otpService.checkOtp(otp, admin = array[j], type, (String)null)) != 0; ++j) {
                            for (int j = 0; j < length && (code = otpService.checkOdp(array[j], otp)) != 0; ++j) {
                                if (i > 0 && money > 2000000L) {
                                    return response.toJson();
                                }
                                ++i;
                            }
                        }

                    } else if (nicknameSend != null) {
//                    code = otpService.checkOtp(otp, nicknameSend, type, (String)null);
//                    16 Sep 2019 | 02:08:35,355 | DEBUG | qtp21802780-63 - /api_backend?c=100&nn=cuccu888&mn=1000000&mt=vin&rs=AAAAAA&otp=55555&type=0&ac=Admin | backend |     | Request UpdateMoneyUser: nickname: cuccu888, money: 1000000, moneyType: vin, reason: AAAAAA, otp: 55555, otpType: 0
                        String[] arr = GameCommon.getValueStr((String) "SUPER_ADMIN").split(",");
                        code = otpService.checkOdp(arr[0], otp);
                    }
                    if (code == 0) {
                        if (actionName == null) {
                            actionName = "Admin";
                        }
                        UserServiceImpl service = new UserServiceImpl();
                        response = service.updateMoneyFromAdmin(nickname, money, moneyType, actionName, "Admin", reason);

                        // Kiem tra user nhan tien
                        // notify cho dai ly
                        UserServiceImpl userService = new UserServiceImpl();
                        UserModel userReceive = userService.getUserByNickName(nickname);
                        String transactionId = String.valueOf(VinPlayUtils.generateTransId());
                        if (userReceive != null && (Objects.equals(userReceive.getDaily(), 1) || Objects.equals(userReceive.getDaily(), 12) || Objects.equals(userReceive.getDaily(), 100))) {
                            if (Objects.equals(userReceive.getDaily(), 1) || Objects.equals(userReceive.getDaily(), 2)) {
                                insertLogTransFerMoneyToDaiLy(money, reason, nickname, transactionId, HistoryTransConst.ADMIN_TRANSFER_TO_DAILY);
                            }
                            // notify
                       /* try {
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
                            objSend.put("description", reason);
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
                        }*/
                        } else {
                            insertLogTransFerMoneyToDaiLy(money, reason, nickname, transactionId, HistoryTransConst.ADMIN_TRANSFER_TO_USER);
                        }
                    } else if (code == 3) {
                        response.setErrorCode("1008");
                    } else if (code == 4) {
                        response.setErrorCode("1021");
                    }
                    logger.debug((Object) ("Code UpdateMoneyUser: " + code));
                }
            }
        } catch (Exception e) {
            logger.debug((Object) e);
        }
        logger.debug((Object) ("Response UpdateMoneyUser: " + response.toJson()));
        return response.toJson();
    }

    private void insertLogTransFerMoneyToDaiLy(long money, String description, String nickname, String transactionId, String typelog) {
        logger.info("updateMoneyFromAdmin insert log with containsKey");
        HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
        historyTransDao.insertTransaction(new HistoryTransModel("Chuyển Tiền Từ Admin", "Admin",
                "Nạp tiền", String.valueOf(money), "Thành công", description, nickname, typelog, transactionId));

    }
}

