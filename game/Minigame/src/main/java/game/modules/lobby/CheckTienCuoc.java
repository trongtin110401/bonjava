package game.modules.lobby;

import com.vinplay.dal.dao.impl.ReportDaoImpl;
import com.vinplay.dal.entities.report.MoneyInOut;
import com.vinplay.dal.entities.report.ReportMoneySystemModel;
import com.vinplay.dal.entities.report.ReportMoneySystemModelNew;
import com.vinplay.dal.entities.report.ReportTotalMoneyModel;
import com.vinplay.dal.service.impl.AgentServiceImpl;
import com.vinplay.dal.service.impl.LogMoneyUserServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.response.AgentResponse;
import com.vinplay.vbee.common.response.LogUserMoneyResponse;
import com.vinplay.vbee.common.statics.Consts;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.apache.commons.collections.map.HashedMap;

import java.text.SimpleDateFormat;
import java.util.*;

public class CheckTienCuoc {
    private final ArrayList<String> listAgency = getListAgent();

    public long checkTien(String nickName){
        long tiencuoc = 0;
        String endTime = "";
        ReportMoneySystemResponse res = new ReportMoneySystemResponse(false, "1001");
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

            Date et = VinPlayUtils.getCurrentDates();
            long time1 = et.getTime();
            long tim2 = time1 - 86400000;
            Date da = new Date(tim2);
            String startTime = format.format(da);
            Date st = format.parse(startTime);
            Date currentDate = VinPlayUtils.getCurrentDates();
            if(et.getTime() < currentDate.getTime())
                endToday = false;

            // search report money user
            HashMap<String, Long> user = new HashMap<String, Long>();
            ReportDaoImpl dao = new ReportDaoImpl();
            HashMap<String, Long> vinOutAgent = new HashMap<String, Long>();
            ReportTotalMoneyModel totalModelStart = dao.getReportTotalMoneyAtTime(startTime, true);
            ReportTotalMoneyModel totalModelEnd = new ReportTotalMoneyModel();
            totalModelEnd = endToday ? dao.getTotalMoney(GameCommon.getValueStr((String)"SUPER_AGENT")) : dao.getReportTotalMoneyAtTime(endTime, false);
            vinOutAgent.put("agentStart", totalModelStart.moneyAgent1 + totalModelStart.moneyAgent2 + totalModelStart.moneySuperAgent);
            vinOutAgent.put("agentEnd", totalModelEnd.moneyAgent1 + totalModelEnd.moneyAgent2 + totalModelEnd.moneySuperAgent);

            user.put("userStart", totalModelStart.moneyUser);
            user.put("userEnd", totalModelEnd.moneyUser);


            LogMoneyUserServiceImpl service = new LogMoneyUserServiceImpl();
            //search all log with time
            List<LogUserMoneyResponse> list = service.searchLogMoneyUser(nickName, "","", startTime, endTime, -1 ,-1);
            if(list == null || list.size() == 0)
                return 0;
            //
            for(LogUserMoneyResponse log : list){
                if(Consts.GAMES_SLOT.contains(log.actionName)){
                    listReport = processListGame(listReport, log);
                }
            }
            for(ReportMoneySystemModelNew li : listReport){
                tiencuoc = tiencuoc + li.moneyLost;
            }
            return tiencuoc;
        }catch (Exception e){
            e.printStackTrace();
        }

        return 0;
    }

    private List<MoneyInOut> processListMoney(List<MoneyInOut> listReport, LogUserMoneyResponse log ){
        try{
            MoneyInOut model = new MoneyInOut();
            if(isExistMoney(listReport, log.actionName)){
                model = getElementByActionMoney(listReport, log.actionName);
            }else{
                model.actionName = log.actionName;
            }
            model.total += log.moneyExchange;
            model.fee += log.fee;
            int index = getElementIndexMoney(listReport, model.actionName);
            if(index == -1)
                listReport.add(model);
            else
                listReport.set(index, model);
            return listReport;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    private List<ReportMoneySystemModelNew> processListGame(List<ReportMoneySystemModelNew> listReport, LogUserMoneyResponse log){
        try{
            ReportMoneySystemModelNew model = new ReportMoneySystemModelNew();
            if(isExist(listReport, log.actionName)){
                model = getElementByAction(listReport, log.actionName);
            }else{
                model.actionName = log.actionName;
            }
            if(log.actionName.equals(Consts.TAI_XIU)){
                if(log.moneyExchange < 0){
                    model.moneyLost += log.moneyExchange;
                }
                else if(log.serviceName.contains("Hoàn trả")){
                    model.moneyOther += log.moneyExchange;
                } else{
                    model.moneyWin += log.moneyExchange;
                }

            }else{
                if(log.moneyExchange < 0){
                    model.moneyLost += log.moneyExchange;
                }
                else{
                    model.moneyWin += log.moneyExchange;
                }
            }
            model.fee += log.fee;
            model.revenuePlayGame += log.moneyExchange;
            model.revenue += (log.moneyExchange - log.fee);
            int index = getElementIndex(listReport, model.actionName);
            if(index == -1)
                listReport.add(model);
            else
                listReport.set(index, model);
            return listReport;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    // ReportMoneySystemModelNew
    private boolean isExist(List<ReportMoneySystemModelNew> listReport, String actionName){
        if(actionName == null)
            return false;
        for (ReportMoneySystemModelNew e : listReport){
            if(e.actionName.equals(actionName)){
                return true;
            }
        }
        return false;
    }
    private ReportMoneySystemModelNew getElementByAction(List<ReportMoneySystemModelNew> listReport, String actionName){
        for (ReportMoneySystemModelNew e : listReport){
            if(e.actionName.equals(actionName)){
                return e;
            }
        }
        return null;
    }
    private int getElementIndex(List<ReportMoneySystemModelNew> listReport, String actionName){
        int i = 0;
        for (ReportMoneySystemModelNew e : listReport){
            if(e.actionName.equals(actionName)){
                return i;
            }else{
                i ++;
            }
        }
        return -1;
    }
    // end ReportMoneySystemModelNew
    // Money In out
    private boolean isExistMoney(List<MoneyInOut> listReport, String actionName){
        if(actionName == null)
            return false;
        for (MoneyInOut e : listReport){
            if(e.actionName.equals(actionName)){
                return true;
            }
        }
        return false;
    }
    private MoneyInOut getElementByActionMoney(List<MoneyInOut> listReport, String actionName){
        for (MoneyInOut e : listReport){
            if(e.actionName.equals(actionName)){
                return e;
            }
        }
        return null;
    }
    private int getElementIndexMoney(List<MoneyInOut> listReport, String actionName){
        int i = 0;
        for (MoneyInOut e : listReport){
            if(e.actionName.equals(actionName)){
                return i;
            }else{
                i ++;
            }
        }
        return -1;
    }
    // end ReportMoneySystemModelNew

    // process money agency ( from user to agency)
    private MoneyInOut processMoneyInToAgency(MoneyInOut agencyMoneyIn, LogUserMoneyResponse log){
        try{
            if(log.moneyExchange > 0)
                return agencyMoneyIn;

            if(!isInListAgency(log.description))
                return agencyMoneyIn;
            agencyMoneyIn.total += log.moneyExchange;
            agencyMoneyIn.fee += log.fee;
            return agencyMoneyIn;
        }catch (Exception e){
            return null;
        }
    }

    // from agency to user
    private MoneyInOut processMoneyOutAgency(MoneyInOut agencyMoneyOut, LogUserMoneyResponse log){
        try{
            if(log.moneyExchange < 0)
                return agencyMoneyOut;

            if(!isInListAgency(log.description))
                return agencyMoneyOut;
            agencyMoneyOut.total += log.moneyExchange;
            agencyMoneyOut.fee += log.fee;
            return agencyMoneyOut;
        }catch (Exception e){
            return null;
        }
    }

    // getListAgent

    private ArrayList<String> getListAgent(){
        try{
            AgentServiceImpl service = new AgentServiceImpl();
            List<AgentResponse> agents = service.listAgent();
            ArrayList<String> agentNames = new ArrayList<String>();
            if (agents != null && agents.size() > 0) {
                for (AgentResponse agent : agents) {
                    agentNames.add(agent.nickName);
                }
            }
            return agentNames;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
    private boolean isInListAgency(String content){
        if(listAgency == null)
            return false;

        for (String agency: listAgency){
            if(content.contains(agency))
                return true;
        }
        return false;
    }
}
