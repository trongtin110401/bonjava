package com.vinplay.api.backend.processors;

import com.google.gson.Gson;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.dal.common.UserInfo;
import com.vinplay.dal.dao.ReportDAO;
import com.vinplay.dal.dao.impl.LogMoneyUserDaoImpl;
import com.vinplay.dal.dao.impl.ReportDaoImpl;
import com.vinplay.dichvuthe.dao.CashoutDao;
import com.vinplay.dichvuthe.dao.impl.CashoutDaoImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.hazelcast.HazelcastUtils;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.response.LogUserMoneyResponse;
import com.vinplay.vbee.common.response.UserCCUResponse;
import com.vinplay.vbee.common.response.UserOnlineResponse;
import com.vinplay.vbee.common.statics.Consts;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

public class GetUserOnlineProcessor implements BaseProcessor<HttpServletRequest, String> {

    private static final List<String> ACTION_NAME = Arrays.asList(
            Consts.CASH_OUT_BY_BANK, Consts.CASH_OUT_BY_MOMO,
            Consts.RECHARGE_BY_BANK, Consts.RECHARGE_BY_MOMO, Consts.RECHARGE_BY_CARD
    );

    @Override
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        UserOnlineResponse response = new UserOnlineResponse(true, "200");
        HazelcastInstance instance = HazelcastClientFactory.getInstance();
        IMap<String, String> userOnlines = instance.getMap("USER_ONLINE");
        IMap cacheUser = HazelcastClientFactory.getInstance().getMap("cache_user");


        int pageIndex = getParameter(request, "pageIndex", 1);
        int pageSize = getParameter(request, "pageSize", 50);

        Gson gson = new Gson();
        List<String> usernames = getPage(new ArrayList<>(userOnlines.keySet()), pageIndex, pageSize);
        List<UserCCUResponse> userOnlineResponse = usernames.stream().map(nickname -> {
            String userInfoJsonData = userOnlines.get(nickname);
            UserInfo userInfo = gson.fromJson(userInfoJsonData, UserInfo.class);

            UserModel userModel = (UserModel) cacheUser.get(nickname);

            UserCCUResponse userCCUResponse = new UserCCUResponse();
            userCCUResponse.nickName = nickname;
            userCCUResponse.totalCashOut = userInfo.getTotalCashoutBank() + userInfo.getTotalCashoutMoMo();
            userCCUResponse.totalDeposit = userInfo.getTotalDepositMoMo() + userInfo.getTotalDepositBank() + userInfo.getTotalDepositCard();
            userCCUResponse.totalMoney = userModel.getVinTotal();
            return userCCUResponse;
        }).collect(Collectors.toList());

//        LogMoneyUserDaoImpl dao = new LogMoneyUserDaoImpl();
//
//        List<UserCCUResponse> userOnlineResponse = usernames.stream()
//                .map(username -> createUserCCUResponse(username, dao))
//                .sorted((u1, u2) -> Double.compare(u2.getTotalMoney(), u1.getTotalMoney()))
//                .collect(Collectors.toList());


        int totalRecord = userOnlines.size();
        int totalPage = (totalRecord + pageSize - 1) / pageSize;
        response.setTotalRecord(userOnlines.size());
        response.setTotalPage(totalPage);
        response.setPageIndex(pageIndex);
        response.setPageSize(pageSize);
        response.setUsers(userOnlineResponse);
        return response.toJson();
    }

    private UserCCUResponse createUserCCUResponse(String username, LogMoneyUserDaoImpl dao) {
        List<LogUserMoneyResponse> responses = dao.getMoneyCashInAndCashOutByNickname(username, ACTION_NAME);
        CashoutDao cashoutDao = new CashoutDaoImpl();
        int totalCashOut = 0;
        totalCashOut += cashoutDao.getTotalCashOutBankByNickname(username);
        totalCashOut += cashoutDao.getTotalCashOutMomoByNickname(username);
        ReportDAO reportDAO = new ReportDaoImpl();
        long currentMoeny = 0;
        try {
            currentMoeny = reportDAO.getCurrentMoney(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long totalDeposit = 0;

        for (LogUserMoneyResponse r : responses) {
            long money = Math.abs(r.getMoneyExchange());
            if (isDepositAction(r.getActionName())) {
                totalDeposit += money;
            }
        }
        UserCCUResponse userCCUResponse = new UserCCUResponse();
        userCCUResponse.setNickName(username);
        userCCUResponse.setTotalMoney(currentMoeny);
        userCCUResponse.setTotalCashOut(totalCashOut);
        userCCUResponse.setTotalDeposit(totalDeposit);

        return userCCUResponse;
    }

    private boolean isDepositAction(String actionName) {
        return Consts.RECHARGE_BY_BANK.equals(actionName) ||
                Consts.RECHARGE_BY_MOMO.equals(actionName) ||
                Consts.RECHARGE_BY_CARD.equals(actionName);
    }

    private boolean isCashOutAction(String actionName) {
        return Consts.CASH_OUT_BY_BANK.equals(actionName) ||
                Consts.CASH_OUT_BY_MOMO.equals(actionName);
    }

    private int getParameter(HttpServletRequest request, String name, int defaultValue) {
        try {
            return Integer.parseInt(request.getParameter(name));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static <T> List<T> getPage(List<T> list, int pageNumber, int pageSize) {
        if (pageSize <= 0 || pageNumber <= 0) {
            throw new IllegalArgumentException("Page size and page number must be greater than 0.");
        }

        int fromIndex = (pageNumber - 1) * pageSize;
        if (fromIndex >= list.size()) {
            return Collections.emptyList();
        }

        int toIndex = Math.min(fromIndex + pageSize, list.size());
        return list.subList(fromIndex, toIndex);
    }
}
