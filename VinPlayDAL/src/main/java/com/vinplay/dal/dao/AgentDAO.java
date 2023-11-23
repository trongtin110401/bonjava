/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.vbee.common.messages.dvt.RefundFeeAgentMessage
 *  com.vinplay.vbee.common.models.AgentModel
 *  com.vinplay.vbee.common.models.cache.AgentDSModel
 *  com.vinplay.vbee.common.response.AgentResponse
 *  com.vinplay.vbee.common.response.LogAgentTranferMoneyResponse
 *  com.vinplay.vbee.common.response.TranferAgentResponse
 */
package com.vinplay.dal.dao;

import com.vinplay.dal.entities.agent.BonusTopDSModel;
import com.vinplay.dal.entities.agent.TranferMoneyAgent;
import com.vinplay.vbee.common.messages.dvt.RefundFeeAgentMessage;
import com.vinplay.vbee.common.models.AgentModel;
import com.vinplay.vbee.common.models.cache.AgentDSModel;
import com.vinplay.vbee.common.response.AgentResponse;
import com.vinplay.vbee.common.response.LogAgentTranferMoneyResponse;
import com.vinplay.vbee.common.response.TranferAgentResponse;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface AgentDAO {
    public List<AgentResponse> listAgent() throws SQLException;

    public List<AgentResponse> listAgentByClient(String client, String agentType) throws SQLException;

    public AgentResponse listAgentByKey(String key) throws SQLException;

    public List<LogAgentTranferMoneyResponse> searchAgentTranferMoney(String var1, String var2, String var3, String var4, String var5, String var6, int var7);

    public List<AgentResponse> listUserAgent(String var1) throws SQLException;

    public List<AgentResponse> listUserAgentByParentID(int var1) throws SQLException;

    public TranferAgentResponse searchAgentTranfer(String var1, String var2, String var3, String var4) throws SQLException;

    public LogAgentTranferMoneyResponse searchAgentTranferMoney(String tid);

    public AgentDSModel getDS(String var1, String var2, String var3, boolean var4) throws SQLException;

    public Map<String, String> getAllNameAgent() throws SQLException;

    public Map<String, ArrayList<String>> getAllAgent() throws SQLException;

    public boolean checkRefundFeeAgent(String var1, String var2);

    public List<RefundFeeAgentMessage> getLogRefundFeeAgent(String var1, String var2);

    public long countsearchAgentTranferMoney(String var1, String var2, String var3, String var4, String var5, String var6);

    public long totalMoneyVinReceiveFromAgent(String var1, String var2, String var3, String var4, String var5, String var6);

    public long totalMoneyVinSendFromAgent(String var1, String var2, String var3, String var4, String var5, String var6);

    public long totalMoneyVinFeeFromAgent(String var1, String var2, String var3, String var4, String var5, String var6);

    public List<LogAgentTranferMoneyResponse> searchAgentTranferMoneyVinSale(String var1, String var2, String var3, String var4, Boolean typeThongKe, int var6, int var7);

    public long countSearchAgentTranferMoneyVinSale(String var1, String var2, String var3, String var4);

    public long totalMoneyVinReceiveFromAgentByStatus(String var1, String var2, String var3, String var4);

    public long totalMoneyVinSendFromAgentByStatus(String var1, String var2, String var3, String var4);

    public boolean updateTopDsFromAgent(String var1, String var2, String var3, String var4);

    public boolean updateTopDsFromAgentMySQL(String var1, String var2, String var3, String var4) throws SQLException;

    public boolean logBonusTopDS(BonusTopDSModel var1);

    public boolean checkBonusTopDS(String var1, String var2);

    public List<BonusTopDSModel> getLogBonusTopDS(String var1, String var2);

    public TranferMoneyAgent getTransferMoneyAgent(String var1, String var2, String var3);

    public List<AgentResponse> listUserAgentAdmin(String var1) throws SQLException;

    public List<LogAgentTranferMoneyResponse> searchAgentTranferMoney(String var1, String var2, String var3, String var4, String var5, int var6);

    public long countsearchAgentTranferMoney(String var1, String var2, String var3, String var4, String var5);

    public long totalMoneyVinReceiveFromAgent(String var1, String var2, String var3, String var4, String var5);

    public long totalMoneyVinSendFromAgent(String var1, String var2, String var3, String var4, String var5);

    public long totalMoneyVinFeeFromAgent(String var1, String var2, String var3, String var4, String var5);

    public List<AgentResponse> listUserAgentLevel2ByParentID(int var1) throws SQLException;

    public List<AgentResponse> listUserAgentLevel2ByID(int var1) throws SQLException;

    public List<AgentResponse> listUserAgentActive(String var1) throws SQLException;

    public List<AgentResponse> listUserAgentLevel1Active() throws SQLException;

    public List<LogAgentTranferMoneyResponse> searchAgentTongTranferMoney(String var1, String var2, String var3, String var4, String var5, String var6, int var7);

    public long countsearchAgentTongTranferMoney(String var1, String var2, String var3, String var4, String var5, String var6);

    public long totalMoneyVinReceiveFromAgentTong(String var1, String var2, String var3, String var4, String var5, String var6);

    public long totalMoneyVinSendFromAgentTong(String var1, String var2, String var3, String var4, String var5, String var6);

    public long totalMoneyVinFeeFromAgentTong(String var1, String var2, String var3, String var4, String var5, String var6);

    public List<AgentModel> getListPercentBonusVincard(String var1) throws SQLException;

    public int registerPercentBonusVincard(String var1, int var2) throws SQLException;
}

