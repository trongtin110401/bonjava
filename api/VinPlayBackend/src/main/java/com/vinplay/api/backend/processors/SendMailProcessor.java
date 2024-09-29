/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.usercore.service.impl.MailBoxServiceImpl
 *  com.vinplay.usercore.service.impl.UserServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.response.SendMailResponse
 *  javax.servlet.http.HttpServletRequest
 */
package com.vinplay.api.backend.processors;

import com.vinplay.usercore.dao.UserDao;
import com.vinplay.usercore.dao.impl.UserDaoImpl;
import com.vinplay.usercore.service.impl.MailBoxServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.messages.SendMailMessage;
import com.vinplay.vbee.common.response.SendMailResponse;
import com.vinplay.vbee.common.rmq.RMQApi;

import java.sql.SQLException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public class SendMailProcessor
        implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) throws SQLException {
        HttpServletRequest request = (HttpServletRequest) param.get();
        SendMailResponse response = new SendMailResponse(true, "0", "");
        String nickName = request.getParameter("nn");
        String title = request.getParameter("tm");
        String content = request.getParameter("cm");
        if (!title.isEmpty() && !content.isEmpty()) {
            MailBoxServiceImpl service = new MailBoxServiceImpl();
            UserServiceImpl user = new UserServiceImpl();
            boolean check = false;
            if (nickName.equals("*")) {
                SendMailMessage sendMailMessage = new SendMailMessage();
                sendMailMessage.setContent(content);
                sendMailMessage.setTitle(title);
                sendMailMessage.setId("mailId");
                sendMailMessage.setNickName("*");
                try {
                    RMQApi.publishMessage("queue_send_mail", sendMailMessage, 1503);
                    response.setErrorCode("0");
                    response.setSuccess(true);
                } catch (Exception e) {
                    response.setErrorCode("10001");
                    e.printStackTrace();
                }
            } else {
                String[] parts;
                for (String name : parts = nickName.split(",")) {
                    try {
                        if (user.checkNickname(name)) continue;
                        response.setErrorCode("10002");
                        response.setNickName(name);
                        response.setSuccess(false);
                        return response.toJson();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                for (String name : parts) {
                    check = service.sendMailBoxFromByNickNameAdmin(name, title, content);
                }
                if (check) {
                    response.setErrorCode("0");
                    response.setSuccess(true);
                } else {
                    response.setErrorCode("10001");
                }
            }
            return response.toJson();
        }
        return "MISSING PARAMETTER";
    }
}

