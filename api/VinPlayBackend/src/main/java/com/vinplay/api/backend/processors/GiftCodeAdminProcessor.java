/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.usercore.service.impl.GiftCodeServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.messages.GiftCodeMessage
 *  com.vinplay.vbee.common.response.BaseResponseModel
 *  com.vinplay.vbee.common.utils.VinPlayUtils
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.backend.processors;

import bitzero.util.common.business.Debug;
import com.vinplay.dal.dao.AgentDAO;
import com.vinplay.dal.dao.impl.AgentDAOImpl;
import com.vinplay.usercore.service.impl.GiftCodeServiceImpl;
import com.vinplay.usercore.utils.PartnerConfig;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.messages.GiftCodeMessage;
import com.vinplay.vbee.common.response.AgentResponse;
import com.vinplay.vbee.common.response.BaseResponseModel;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.json.JSONObject;

public class GiftCodeAdminProcessor
implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"backend");

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String gia = request.getParameter("gp");
        String soluong = request.getParameter("gq");
        String dotphathanh = request.getParameter("gl");
        String source = request.getParameter("gs");
        String moneyType = request.getParameter("mt");
        String type = request.getParameter("type");
        logger.debug((Object)("LobbyModule error: " + gia + ":" + soluong + ":" + dotphathanh + ":" + source + ":" + moneyType + ":" + type));
        BaseResponseModel response = new BaseResponseModel(false, "1001");
        if (!(gia == null || gia.equals("") || soluong == null || soluong.equals("") || dotphathanh == null || dotphathanh.equals("") || source == null || source.equals("") || moneyType == null || moneyType.equals(""))) {
            try {
                GiftCodeServiceImpl service = new GiftCodeServiceImpl();
                VinPlayUtils.loadGiftcode((List)service.loadAllGiftcode());
                String str = String.valueOf(dotphathanh) + gia + source;
                int leng = 12 - str.length();
                String giftCode = "";
                GiftCodeMessage msg = new GiftCodeMessage();
                for (int i = 0; i < Integer.parseInt(soluong); ++i) {
                    giftCode = VinPlayUtils.genGiftCode((int)leng);
                    msg = new GiftCodeMessage(giftCode.toUpperCase(), gia, Integer.parseInt(soluong), source, 1, 1, Integer.parseInt(moneyType), dotphathanh, type, "");
                    service.genGiftCode(msg);                    
                }
                // đẩy report

                /*
                try {
                    HttpClient client = HttpClientBuilder.create().build();
                    String url = "";
                    if ("XXENG".equals(PartnerConfig.Client)) {
                        url = PartnerConfig.HostBot;
                    } else if ("R68".equals(PartnerConfig.Client)) {
                        url = PartnerConfig.HostBot;
                    } else {
                        url = PartnerConfig.HostBot;
                    }
                    HttpPost httpPost = new HttpPost(url + "/rpadmin/ozawasecret1243/1");
                    httpPost.addHeader("Content-Type", "application/json");
                    // get agent by key                       
                    JSONObject obj = new JSONObject();
                    obj.put("agent", "superadmin");
                    obj.put("nick_name", "superadmin");
                    obj.put("source", source);
                    obj.put("price", gia);
                    obj.put("quantity", soluong);
                    obj.put("action", "CREATE_CODE");

                    StringEntity requestEntity = new StringEntity(obj.toString(), "UTF-8");
                    httpPost.setEntity(requestEntity);

                    // add request header
                    HttpResponse httpResponse = client.execute(httpPost);

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
                */
                response.setErrorCode("0");
                response.setSuccess(true);
            }
            catch (Exception e) {
                logger.debug((Object)e.getMessage());
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                String sStackTrace = sw.toString(); // stack trace as a string
                Debug.trace((Object)sStackTrace);           
            }
            logger.debug(response.toJson());
            return response.toJson();
        }
        return "MISSING PARAMETTER";
    }
}

