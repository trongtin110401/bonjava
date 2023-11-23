package com.vinplay.dal.entities.report;

import java.util.HashMap;
import java.util.List;

public class ReportMoneyModel {
    public List<ReportMoneySystemModelNew> ListReportGame;
    public List<MoneyInOut> ListUserIn;
    public List<MoneyInOut> ListUserInEvent;
    public List<MoneyInOut> ListUserOut;
    public List<MoneyInOut> ListOther;
    public HashMap<String, Long> UserMoney;
    public HashMap<String, Long> AgentMoney;
    public MoneyInOut AgentMoneyIn;
    public MoneyInOut AgentMoneyOut;
    public ReportMoneyModel(List<ReportMoneySystemModelNew> listReportGame, List<MoneyInOut> listUserIn, List<MoneyInOut> listUserInEvent, List<MoneyInOut> listUserOut, List<MoneyInOut> listOther) {
        ListReportGame = listReportGame;
        ListUserIn = listUserIn;
        ListUserInEvent = listUserInEvent;
        ListUserOut = listUserOut;
        ListOther = listOther;
    }
}
