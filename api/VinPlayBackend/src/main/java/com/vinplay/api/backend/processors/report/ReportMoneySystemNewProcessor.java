/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IMap
 *  com.vinplay.dal.dao.impl.ReportDaoImpl
 *  com.vinplay.dal.entities.report.ReportMoneySystemModel
 *  com.vinplay.dal.entities.report.ReportTXModel
 *  com.vinplay.dal.entities.report.ReportTotalMoneyModel
 *  com.vinplay.usercore.utils.GameCommon
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.hazelcast.HazelcastClientFactory
 *  com.vinplay.vbee.common.models.cache.ReportModel
 *  com.vinplay.vbee.common.statics.Consts
 *  com.vinplay.vbee.common.utils.VinPlayUtils
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.backend.processors.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinplay.api.backend.processors.UpdateFundProcessor;
import com.vinplay.api.backend.response.ReportMoneySystemResponse;
import com.vinplay.dal.dao.impl.ReportDaoImpl;
import com.vinplay.dal.entities.report.*;
import com.vinplay.dal.service.impl.AgentServiceImpl;
import com.vinplay.dal.service.impl.LogMoneyUserServiceImpl;
import com.vinplay.usercore.service.OtherService;
import com.vinplay.usercore.service.impl.OtherServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.AgentResponse;
import com.vinplay.vbee.common.response.LogUserMoneyResponse;
import com.vinplay.vbee.common.response.TransactionFundResponse;
import com.vinplay.vbee.common.statics.Consts;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.bson.Document;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class ReportMoneySystemNewProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "report");
    private final ArrayList<String> listAgency = getListAgent();

    public String execute(Param<HttpServletRequest> param) {
        ReportMoneySystemResponse res;
        HttpServletRequest request = (HttpServletRequest) param.get();
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        String nickName = request.getParameter("nickname");
        res = new ReportMoneySystemResponse(false, "1001");
        Map<String, ReportMoneySystemModel> map = new HashMap();
        List<ReportMoneySystemModelNew> listReport = new ArrayList<ReportMoneySystemModelNew>();
        List<MoneyInOut> listUserIn = new ArrayList<MoneyInOut>();
        List<MoneyInOut> listUserInEvent = new ArrayList<MoneyInOut>();
        List<MoneyInOut> listUserOut = new ArrayList<MoneyInOut>();
        List<MoneyInOut> listOther = new ArrayList<MoneyInOut>();
        MoneyInOut moneyAgentIn = new MoneyInOut();
        MoneyInOut moneyAgentOut = new MoneyInOut();
        boolean endToday = true;


        Map<String, Long> MoneyIn = new HashedMap();
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date st = format.parse(startTime);
            Date et = format.parse(endTime);
            Date currentDate = VinPlayUtils.getCurrentDates();
            if (et.getTime() < currentDate.getTime())
                endToday = false;

            // search report money user
            HashMap<String, Long> user = new HashMap<String, Long>();
            ReportDaoImpl dao = new ReportDaoImpl();
            HashMap<String, Long> vinOutAgent = new HashMap<String, Long>();
            ReportTotalMoneyModel totalModelStart = dao.getReportTotalMoneyAtTime(startTime, true);
            ReportTotalMoneyModel totalModelEnd = new ReportTotalMoneyModel();
            totalModelEnd = endToday ? dao.getTotalMoney(GameCommon.getValueStr((String) "SUPER_AGENT")) : dao.getReportTotalMoneyAtTime(endTime, false);
            vinOutAgent.put("agentStart", totalModelStart.moneyAgent1 + totalModelStart.moneyAgent2 + totalModelStart.moneySuperAgent);
            vinOutAgent.put("agentEnd", totalModelEnd.moneyAgent1 + totalModelEnd.moneyAgent2 + totalModelEnd.moneySuperAgent);

            user.put("userStart", totalModelStart.moneyUser);
            user.put("userEnd", totalModelEnd.moneyUser);

            // search fund
            OtherService otherService = new OtherServiceImpl();
            TransactionFundResponse transactionFundResponse = otherService.getTransactionFund(1, 0, null, startTime + " 00:00:00", endTime + " 23:59:59", null);
            List<ReportMoneySystemModelNew> listGameFunds = transactionFundResponse.getTransactions().stream()
                    .collect(Collectors.groupingBy(doc -> doc.getString("gameName")))
                    .entrySet()
                    .stream()
                    .map(entry -> {
                        String fundName = entry.getKey();

                        ReportMoneySystemModelNew report = new ReportMoneySystemModelNew();
                        report.actionName = UpdateFundProcessor.fund2GameName.get(fundName);
                        if(report.actionName == null) {
                            System.out.println("===================> fundName: " + fundName);
                        }

                        List<Document> documents = entry.getValue();
                        report.fund = documents.stream().mapToLong(doc -> {
                            if (doc.get("type", String.class).equals("deposit")) {
                                return -doc.getLong("amount");
                            } else {
                                return doc.getLong("amount");
                            }
                        }).sum();
                        return report;
                    }).collect(Collectors.toList());


            LogMoneyUserServiceImpl service = new LogMoneyUserServiceImpl();
            //search all log with time
            List<LogUserMoneyResponse> logs = service.searchLogMoneyUser(nickName, "", "", startTime, endTime, -1, -1);
            if (logs == null || logs.isEmpty())
                return res.toJson();

            List<ReportMoneySystemModelNew> listGameReport = new ArrayList<>();
            for (LogUserMoneyResponse log : logs) {
                if (Consts.GAMES.contains(log.actionName)) {
                    listGameReport = processListGame(listReport, log);
                } else if (Consts.VIN_IN_USER.contains(log.actionName)) {
                    listUserIn = processListMoney(listUserIn, log);
                } else if (Consts.VIN_IN_EVENT.contains(log.actionName)) {
                    listUserInEvent = processListMoney(listUserInEvent, log);
                } else if (Consts.VIN_OUT_USER.contains(log.actionName)) {
                    listUserOut = processListMoney(listUserOut, log);
                } else if (Consts.VIN_OTHER.contains(log.actionName)) {
                    listOther = processListMoney(listOther, log);
                } else if (Consts.TRANSFER_MONEY.equals(log.actionName)) {
                    if (log.moneyExchange < 0) {
                        moneyAgentIn = processMoneyInToAgency(moneyAgentIn, log);
                    } else {
                        moneyAgentOut = processMoneyOutAgency(moneyAgentOut, log);
                    }
                }
            }
            try {
                // combine list
                listGameReport.addAll(listGameFunds);
                Map<String, List<ReportMoneySystemModelNew>> gameName2ReportModel = listGameReport.stream()
                        .collect(Collectors.groupingBy(reportMoneySystemModelNew -> reportMoneySystemModelNew.actionName));
                gameName2ReportModel.forEach((gameName, reportMoneySystemModelNews) -> {
                    if (reportMoneySystemModelNews.size() > 1) {
                        ReportMoneySystemModelNew report = new ReportMoneySystemModelNew();
                        report.actionName = gameName;
                        report.moneyWin = reportMoneySystemModelNews.stream().mapToLong(ReportMoneySystemModelNew::getMoneyWin).sum();
                        report.moneyLost = reportMoneySystemModelNews.stream().mapToLong(ReportMoneySystemModelNew::getMoneyLost).sum();
                        report.moneyOther = reportMoneySystemModelNews.stream().mapToLong(ReportMoneySystemModelNew::getMoneyOther).sum();
                        report.fee = reportMoneySystemModelNews.stream().mapToLong(ReportMoneySystemModelNew::getFee).sum();
                        report.revenuePlayGame = reportMoneySystemModelNews.stream().mapToLong(ReportMoneySystemModelNew::getRevenuePlayGame).sum();
                        report.revenue = reportMoneySystemModelNews.stream().mapToLong(ReportMoneySystemModelNew::getRevenue).sum();
                        report.fund = reportMoneySystemModelNews.stream().mapToLong(ReportMoneySystemModelNew::getFee).sum();
                        listReport.add(report);
                    } else {
                        listReport.addAll(reportMoneySystemModelNews);
                    }
                });

                ReportMoneyModel reportMoneyModel = new ReportMoneyModel(listReport, listUserIn, listUserInEvent, listUserOut, listOther);
                reportMoneyModel.UserMoney = user;
                // reportMoneyModel.AgentMoney = vinOutAgent;

                reportMoneyModel.AgentMoneyIn = moneyAgentIn;
                reportMoneyModel.AgentMoneyOut = moneyAgentOut;
                ObjectMapper mapper = new ObjectMapper();

                LocalDate dateStart = LocalDate.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String sqlDateStart = dateStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                LocalDate dateEnd = LocalDate.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String sqlDateEnd = dateEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                reportMoneyModel.totalShootFishProfit = otherService.getTotalShootFishByNickname(sqlDateStart, sqlDateEnd, nickName);

                return mapper.writeValueAsString((Object) reportMoneyModel);
            } catch (Exception e) {
                e.printStackTrace();
                return "{\"success\":false,\"errorCode\":\"1001\"}";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res.toJson();
    }

    private List<MoneyInOut> processListMoney(List<MoneyInOut> listReport, LogUserMoneyResponse log) {
        try {
            MoneyInOut model = new MoneyInOut();
            if (isExistMoney(listReport, log.actionName)) {
                model = getElementByActionMoney(listReport, log.actionName);
            } else {
                model.actionName = log.actionName;
            }
            model.total += log.moneyExchange;
            model.fee += log.fee;
            int index = getElementIndexMoney(listReport, model.actionName);
            if (index == -1)
                listReport.add(model);
            else
                listReport.set(index, model);
            return listReport;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<ReportMoneySystemModelNew> processListGame(List<ReportMoneySystemModelNew> listReport, LogUserMoneyResponse log) {
        try {
            return processDataGameReport(listReport, log);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<ReportMoneySystemModelNew> processDataGameReport(List<ReportMoneySystemModelNew> listReport, LogUserMoneyResponse log) {
        ReportMoneySystemModelNew model = new ReportMoneySystemModelNew();
        if (isExist(listReport, log.actionName)) {
            model = getElementByAction(listReport, log.actionName);
        } else {
            model.actionName = log.actionName;
        }
        if (log.actionName.equals(Consts.TAI_XIU)) {
            if (log.moneyExchange < 0) {
                model.moneyLost += log.moneyExchange;
            } else if (log.serviceName.contains("Hoàn trả")) {
                model.moneyOther += log.moneyExchange;
            } else {
                model.moneyWin += log.moneyExchange;
            }

        } else {
            if (log.moneyExchange < 0) {
                model.moneyLost += log.moneyExchange;
            } else {
                model.moneyWin += log.moneyExchange;
            }
        }
        model.fee += log.fee;
        model.revenuePlayGame += log.moneyExchange;
        model.revenue += (log.moneyExchange - log.fee);
        int index = getElementIndex(listReport, model.actionName);
        if (index == -1)
            listReport.add(model);
        else
            listReport.set(index, model);
        return listReport;
    }

    // ReportMoneySystemModelNew
    private boolean isExist(List<ReportMoneySystemModelNew> listReport, String actionName) {
        if (actionName == null)
            return false;
        for (ReportMoneySystemModelNew e : listReport) {
            if (e.actionName.equals(actionName)) {
                return true;
            }
        }
        return false;
    }

    private ReportMoneySystemModelNew getElementByAction(List<ReportMoneySystemModelNew> listReport, String actionName) {
        for (ReportMoneySystemModelNew e : listReport) {
            if (e.actionName.equals(actionName)) {
                return e;
            }
        }
        return null;
    }

    private int getElementIndex(List<ReportMoneySystemModelNew> listReport, String actionName) {
        int i = 0;
        for (ReportMoneySystemModelNew e : listReport) {
            if (e.actionName.equals(actionName)) {
                return i;
            } else {
                i++;
            }
        }
        return -1;
    }

    // end ReportMoneySystemModelNew
    // Money In out
    private boolean isExistMoney(List<MoneyInOut> listReport, String actionName) {
        if (actionName == null)
            return false;
        for (MoneyInOut e : listReport) {
            if (e.actionName.equals(actionName)) {
                return true;
            }
        }
        return false;
    }

    private MoneyInOut getElementByActionMoney(List<MoneyInOut> listReport, String actionName) {
        for (MoneyInOut e : listReport) {
            if (e.actionName.equals(actionName)) {
                return e;
            }
        }
        return null;
    }

    private int getElementIndexMoney(List<MoneyInOut> listReport, String actionName) {
        int i = 0;
        for (MoneyInOut e : listReport) {
            if (e.actionName.equals(actionName)) {
                return i;
            } else {
                i++;
            }
        }
        return -1;
    }
    // end ReportMoneySystemModelNew

    // process money agency ( from user to agency)
    private MoneyInOut processMoneyInToAgency(MoneyInOut agencyMoneyIn, LogUserMoneyResponse log) {
        try {
            if (log.moneyExchange > 0)
                return agencyMoneyIn;

            if (!isInListAgency(log.description))
                return agencyMoneyIn;
            agencyMoneyIn.total += log.moneyExchange;
            agencyMoneyIn.fee += log.fee;
            return agencyMoneyIn;
        } catch (Exception e) {
            return null;
        }
    }

    // from agency to user
    private MoneyInOut processMoneyOutAgency(MoneyInOut agencyMoneyOut, LogUserMoneyResponse log) {
        try {
            if (log.moneyExchange < 0)
                return agencyMoneyOut;

            if (!isInListAgency(log.description))
                return agencyMoneyOut;
            agencyMoneyOut.total += log.moneyExchange;
            agencyMoneyOut.fee += log.fee;
            return agencyMoneyOut;
        } catch (Exception e) {
            return null;
        }
    }

    // getListAgent

    private ArrayList<String> getListAgent() {
        try {
            AgentServiceImpl service = new AgentServiceImpl();
            List<AgentResponse> agents = service.listAgent();
            ArrayList<String> agentNames = new ArrayList<String>();
            if (agents != null && agents.size() > 0) {
                for (AgentResponse agent : agents) {
                    agentNames.add(agent.nickName);
                }
            }
            return agentNames;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private boolean isInListAgency(String content) {
        if (listAgency == null)
            return false;

        for (String agency : listAgency) {
            if (content.contains(agency))
                return true;
        }
        return false;
    }

}

