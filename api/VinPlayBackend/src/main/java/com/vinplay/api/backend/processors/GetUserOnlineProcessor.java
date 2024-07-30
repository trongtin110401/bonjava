package com.vinplay.api.backend.processors;

import bitzero.server.entities.User;
import bitzero.util.ExtensionUtility;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.dal.dao.impl.LogMoneyUserDaoImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
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
        IMap<String, Object> userOnline = instance.getMap("USER_ONLINE");

        int pageIndex = getParameter(request, "pageIndex", 1);
        int pageSize = getParameter(request, "pageSize", 50);

        List<User> users = ExtensionUtility.globalUserManager.getAllUsers();

        List<String> usernames = getPage(new ArrayList<>(userOnline.keySet()), pageIndex, pageSize);
        LogMoneyUserDaoImpl dao = new LogMoneyUserDaoImpl();

        List<UserCCUResponse> userOnlineResponse = usernames.stream()
                .map(username -> createUserCCUResponse(username, dao))
                .collect(Collectors.toList());

        response.setUsers(userOnlineResponse);
        return response.toJson();
    }

    private UserCCUResponse createUserCCUResponse(String username, LogMoneyUserDaoImpl dao) {
        List<LogUserMoneyResponse> responses = dao.getMoneyCashInAndCashOutByNickname(username, ACTION_NAME);

        long totalDeposit = 0;
        long totalCashOut = 0;

        for (LogUserMoneyResponse r : responses) {
            long money = Math.abs(r.getMoneyExchange());

            if (isDepositAction(r.getActionName())) {
                totalDeposit += money;
            } else if (isCashOutAction(r.getActionName())) {
                totalCashOut += money;
            }
        }

        UserCCUResponse userCCUResponse = new UserCCUResponse();
        userCCUResponse.setNickName(username);
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
