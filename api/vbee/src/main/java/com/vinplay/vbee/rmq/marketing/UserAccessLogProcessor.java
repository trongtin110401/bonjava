package com.vinplay.vbee.rmq.marketing;

import com.vinplay.marketing.service.MarketingService;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.messages.marketing.UserAccessLogMessage;
import com.vinplay.vbee.common.messages.minigame.NoHuTaiXiuMessage;
import com.vinplay.vbee.dao.impl.TaiXiuDaoImpl;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

public class UserAccessLogProcessor implements BaseProcessor<byte[], Boolean> {
    private static final Logger logger = Logger.getLogger((String) "vbee");

    @Override
    public Boolean execute(Param<byte[]> params) throws SQLException {
        byte[] body = (byte[]) params.get();
        try {
            UserAccessLogMessage message = (UserAccessLogMessage) NoHuTaiXiuMessage.fromBytes((byte[]) body);
            LocalDateTime accessTime =
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(message.getAccessTime()),
                            TimeZone.getDefault().toZoneId());

            MarketingService marketingService = new MarketingService();
            marketingService.logUserAccess(message.getUserId(), message.getUtmId(), message.getDevice(), accessTime);
            return true;
        } catch (Exception e) {
            logger.error((Object) "Handle save transaction error ", (Throwable) e);
        }
        return false;
    }


}
