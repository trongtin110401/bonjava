/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.messages.BaseMessage
 *  com.vinplay.vbee.common.messages.minigame.ResultTaiXiuMessage
 */
package com.vinplay.vbee.rmq.overunder.processor;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.messages.BaseMessage;
import com.vinplay.vbee.common.messages.minigame.ResultTaiXiuMessage;
import com.vinplay.vbee.dao.impl.OverUnderDaoImpl;
import java.sql.SQLException;

public class SaveResultOverUnderProcessor
implements BaseProcessor<byte[], Boolean> {
    public Boolean execute(Param<byte[]> param) {
        byte[] body = (byte[])param.get();
        ResultTaiXiuMessage message = (ResultTaiXiuMessage)BaseMessage.fromBytes((byte[])body);
        OverUnderDaoImpl dao = new OverUnderDaoImpl();
        boolean success = false;
        try {
            success = dao.saveResultTaiXiu(message);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }
}

