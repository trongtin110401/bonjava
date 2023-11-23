/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.messages.BaseMessage
 *  com.vinplay.vbee.common.messages.minigame.UpdateLuotRutLocMessage
 */
package com.vinplay.vbee.rmq.overunder.processor;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.messages.BaseMessage;
import com.vinplay.vbee.common.messages.minigame.UpdateLuotRutLocMessage;
import com.vinplay.vbee.dao.impl.OverUnderDaoImpl;
import java.sql.SQLException;

public class UpdateLuotRutLocOuProcessor
implements BaseProcessor<byte[], Boolean> {
    public Boolean execute(Param<byte[]> param) {
        UpdateLuotRutLocMessage message = (UpdateLuotRutLocMessage)BaseMessage.fromBytes((byte[])((byte[])param.get()));
        OverUnderDaoImpl dao = new OverUnderDaoImpl();
        boolean success = false;
        try {
            success = dao.updateLuotRutLoc(message);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }
}

