/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.usercore.service.impl.UserForAdminServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.response.ResultUserReponse
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.backend.processors;

import com.vinplay.usercore.dao.impl.UserDaoImpl;
import com.vinplay.usercore.service.OtherService;
import com.vinplay.usercore.service.impl.OtherServiceImpl;
import com.vinplay.usercore.service.impl.UserForAdminServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.*;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReportUserProcessor
        implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger("backend");

    public String execute(Param<HttpServletRequest> param) {
        ReportUserResponse response = new ReportUserResponse(false, "1001");
        HttpServletRequest request = param.get();
        String timeStart = request.getParameter("ts");
        String timeEnd = request.getParameter("te");


        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate localDateStart = LocalDate.parse(timeStart, inputFormatter);
        LocalDate localDateEnd = LocalDate.parse(timeEnd, inputFormatter);

        String ts = localDateStart.format(outputFormatter);
        String te = localDateEnd.format(outputFormatter);

        UserForAdminServiceImpl service = new UserForAdminServiceImpl();
        try {
            int totalRecord = service.countUser(ts, te);
            UserDaoImpl userDao = new UserDaoImpl();
            List<String> usersPay = userDao.getListUserPay(ts, te);
            OtherService otherService = new OtherServiceImpl();
            UserActivePhoneResponse userActivePhoneResponse = otherService.getAllUserActivePhoneByDay(timeStart, timeEnd);
            UserActiveTeleResponse userActiveTeleResponse = otherService.getAllUserActiveTeleByDay(timeStart, timeEnd);
            Set<String> userSecurityPhone = new HashSet<>();
            for (UserPhone userPhone : userActivePhoneResponse.getUsers()) {
                userSecurityPhone.add(userPhone.getNickname());
            }
            Set<String> userSecurityTele = new HashSet<>();
            for (UserTele userTele : userActiveTeleResponse.getUsers()) {
                userSecurityTele.add(userTele.getNickname());
            }
            userSecurityPhone.retainAll(userSecurityTele);
            response.setTotal(totalRecord);
            response.setUserPay(usersPay.size());
            long count = usersPay.stream()
                    .filter(userSecurityPhone::contains)
                    .count();
            response.setUserSecurity(userSecurityPhone.size());
            response.setUserPayAndSecurity(count);
            response.setSuccess(true);
            response.setErrorCode("0");
        } catch (SQLException e) {
            logger.debug(e);
        }
        return response.toJson();
    }
}

