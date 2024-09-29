/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.messages.minigame.TransactionTaiXiuMessage
 *  org.apache.log4j.Logger
 */
package com.vinplay.vbee.rmq.minigame.processor;

import com.vinplay.usercore.dao.UserDao;
import com.vinplay.usercore.dao.impl.UserDaoImpl;
import com.vinplay.usercore.service.impl.MailBoxServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.messages.SendMailMessage;
import com.vinplay.vbee.common.rmq.RMQApi;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

public class SendMailQueueProcessor implements BaseProcessor<byte[], Boolean> {

    private static final Logger logger = Logger.getLogger((String) "vbee");
    private static final int PAGE_SIZE = 1000;

    public Boolean execute(Param<byte[]> param) {
        byte[] body = param.get();
        try {
            SendMailMessage message = (SendMailMessage) SendMailMessage.fromBytes(body);
            if (message.getNickName().equals("*")) {
                sendEmailToAllUsers(message);
            } else {
                sendEmailToUser(message);
            }


        } catch (Exception e) {
            logger.error("Handle save transaction error ", (Throwable) e);
        }
        return false;

    }

    private void sendEmailToUser(SendMailMessage message) {
        MailBoxServiceImpl service = new MailBoxServiceImpl();
        service.sendMailBoxBySystem(message.getNickName(), message.getTitle(), message.getContent(), message.getId());
    }

    private void sendEmailToAllUsers(SendMailMessage message) throws SQLException {
        UserDao userDao = new UserDaoImpl();
        int currentPage = 1;
        while (true) {
            List<String> users = userDao.getUsers(currentPage, PAGE_SIZE);
            if (users.isEmpty()) {
                break;
            }
            for (String nickname : users) {
                String mailId = String.valueOf(System.currentTimeMillis());
                SendMailMessage mail = new SendMailMessage();
                mail.setContent(message.getContent());
                mail.setId(mailId);
                mail.setTitle(message.getTitle());
                mail.setNickName(nickname);
                sendEmailToUser(mail);
            }
            currentPage++;
        }
    }

}

