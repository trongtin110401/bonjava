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

import com.vinplay.usercore.service.impl.UserForAdminServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.ReportUserResponse;
import com.vinplay.vbee.common.response.ResultUserReponse;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class ReportUserProcessor
        implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger("backend");

    public String execute(Param<HttpServletRequest> param) {
        ReportUserResponse response = new ReportUserResponse(false, "1001");
        HttpServletRequest request = param.get();
        String timeStart = request.getParameter("ts");
        String timeEnd = request.getParameter("te");


        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate localDateStart = LocalDate.parse(timeStart, inputFormatter);
        LocalDate localDateEnd = LocalDate.parse(timeEnd, inputFormatter);

        String ts = localDateStart.format(outputFormatter);
        String te = localDateEnd.format(outputFormatter);

        UserForAdminServiceImpl service = new UserForAdminServiceImpl();
        try {
            int totalRecord = service.countUser(ts, te);
            int userPay = service.countUserPay(ts, te);
            int userSecurity = service.countUserSecurity(ts, te);
            int userPayAndSecurity = service.countUserPayAndSecurity(ts, te);
            response.setTotal(totalRecord);
            response.setUserPay(userPay);
            response.setUserSecurity(userSecurity);
            response.setUserPayAndSecurity(userPayAndSecurity);
            response.setSuccess(true);
            response.setErrorCode("0");
        } catch (SQLException e) {
            logger.debug(e);
        }
        return response.toJson();
    }
}

