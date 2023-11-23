/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.messages.BaseMessage
 *  com.vinplay.vbee.common.messages.statistic.LoginPortalInfoMsg
 */
package com.vinplay.vbee.rmq.statistic.processor;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.messages.BaseMessage;
import com.vinplay.vbee.common.messages.statistic.LoginPortalInfoMsg;
import com.vinplay.vbee.dao.impl.StatisticDaoImpl;

public class LoginPortalInfoProcessor
implements BaseProcessor<byte[], Boolean> {
    public Boolean execute(Param<byte[]> param) {
        LoginPortalInfoMsg msg = (LoginPortalInfoMsg)BaseMessage.fromBytes((byte[])((byte[])param.get()));
        StatisticDaoImpl dao = new StatisticDaoImpl();
        return dao.saveLoginPortalInfo(msg);
    }
}

