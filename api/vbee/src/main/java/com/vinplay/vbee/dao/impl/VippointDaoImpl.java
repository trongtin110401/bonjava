/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.vbee.common.messages.vippoint.VippointEventMessage
 *  com.vinplay.vbee.common.pools.ConnectionPool
 */
package com.vinplay.vbee.dao.impl;

import com.vinplay.vbee.common.messages.vippoint.VippointEventMessage;
import com.vinplay.vbee.common.pools.ConnectionPool;
import com.vinplay.vbee.dao.VippointDao;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class VippointDaoImpl
implements VippointDao {
    @Override
    public boolean updateVippointEvent(VippointEventMessage message, int isBot) throws SQLException {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        CallableStatement call = null;
        call = conn.prepareCall("CALL update_vippoint_event(?,?,?,?,?,?,?,?,?)");
        int param = 1;
        call.setInt(param++, message.getUserId());
        call.setString(param++, message.getNickname());
        if (message.getType() == 0) {
            call.setInt(param++, message.getVpReal());
            call.setInt(param++, message.getVpEvent());
            call.setInt(param++, 0);
        } else if (message.getType() == 2) {
            call.setInt(param++, message.getNumSub());
            call.setInt(param++, message.getVpSub());
            call.setInt(param++, message.getVpEvent());
        } else if (message.getType() == 1) {
            call.setInt(param++, message.getNumAdd());
            call.setInt(param++, message.getVpAdd());
            call.setInt(param++, message.getVpEvent());
        } else if (message.getType() == 3) {
            call.setInt(param++, message.getNumAdd());
            call.setInt(param++, message.getVp());
            call.setInt(param++, 0);
        }
        call.setInt(param++, message.getPlace());
        call.setInt(param++, message.getPlaceMax());
        call.setInt(param++, message.getType());
        call.setInt(param++, isBot);
        call.executeUpdate();
        if (call != null) {
            call.close();
        }
        if (conn != null) {
            conn.close();
        }
        return true;
    }
}

