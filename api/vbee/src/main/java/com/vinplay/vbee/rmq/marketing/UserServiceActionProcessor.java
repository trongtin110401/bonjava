package com.vinplay.vbee.rmq.marketing;

import com.vinplay.marketing.service.MarketingService;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.messages.marketing.UserAccessLogMessage;
import com.vinplay.vbee.common.messages.marketing.UserServiceActionMessage;
import com.vinplay.vbee.common.messages.minigame.NoHuTaiXiuMessage;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class UserServiceActionProcessor implements BaseProcessor<byte[], Boolean> {
    private static final Logger logger = Logger.getLogger((String) "vbee");

    @Override
    public Boolean execute(Param<byte[]> params) throws SQLException {
        byte[] body = params.get();
        try {
            UserServiceActionMessage message = (UserServiceActionMessage) NoHuTaiXiuMessage.fromBytes(body);
            MarketingService marketingService = new MarketingService();
            marketingService.addUserServiceAndServiceLog(message.getNickname(), message.getAction(), message.getActionValue(), LocalDateTime.parse(message.getCreateTime(), DateTimeFormatter.ofPattern("\"yyyy-MM-dd HH:mm:ss")));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
