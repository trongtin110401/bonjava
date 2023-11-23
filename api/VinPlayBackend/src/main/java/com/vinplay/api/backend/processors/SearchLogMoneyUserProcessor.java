/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.dal.service.impl.LogMoneyUserServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.response.LogUserMoneyResponse
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.backend.processors;

import com.vinplay.api.backend.processors.daily.APIelkUserVin;
import com.vinplay.api.backend.processors.daily.UserVinEntity;
import com.vinplay.api.backend.response.LogMoneyResponse;
import com.vinplay.dal.service.impl.LogMoneyUserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.LogUserMoneyResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

public class SearchLogMoneyUserProcessor
implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"backend");

    public String execute(Param<HttpServletRequest> param) {
        LogMoneyResponse response = new LogMoneyResponse(false, "1001");
        HttpServletRequest request = (HttpServletRequest)param.get();
        String nickName = request.getParameter("nn");
        String userName = request.getParameter("un");
        String timestart = request.getParameter("ts");
        String timeend = request.getParameter("te");
        String moneyType = request.getParameter("mt");
        String actionName = request.getParameter("ag");
        String serviceName = request.getParameter("sn");
        String typeSearch = request.getParameter("type");
        int page = 50;
        try {
            if (request.getParameter("p") != null) {
                page = Integer.parseInt(request.getParameter("p"));
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        int like = Integer.parseInt(request.getParameter("lk"));
        int totalrecord = 50;
        try
        {
            totalrecord = Integer.parseInt(request.getParameter("tr"));
        }
        catch (Exception ex)
        {
            
        }
        if (page < 0) {
            return response.toJson();
        }
        LogMoneyUserServiceImpl service = new LogMoneyUserServiceImpl();
        try {
//            CÁCH TÌM KIẾM CŨ

            if(typeSearch.equals("mongo")) {
                List trans = service.searchLogMoneyUser(nickName, userName, moneyType, serviceName, actionName, timestart, timeend, page, like, totalrecord);
                int totalPages = service.countsearchLogMoneyUser(nickName, moneyType, serviceName, actionName, timestart, timeend, like);
                response.setTotalPages(totalPages);
                response.setTransactions(trans);
                response.setSuccess(true);
                response.setErrorCode("0");
            }

//           CÁCH TÌM KIẾM ÁP DỤNG ELK
            if(typeSearch.equals("elk")) {
                APIelkUserVin apielkUserVin = new APIelkUserVin();
                ArrayList<UserVinEntity> transByELK;
                int numStart = (page - 1) * totalrecord;
                int totalPages = 0;
                if (serviceName != null && !serviceName.equals("") && nickName != null && !nickName.equals("")) {
                    totalPages = apielkUserVin.TongUserVinByServiceNameNickname(nickName, serviceName, timestart, timeend);
                    transByELK = apielkUserVin.GetUserVinByServiceNameNickname(nickName, serviceName, timestart, timeend,  numStart, totalrecord);
                } else if (serviceName != null && !serviceName.equals("")) {
                    totalPages = apielkUserVin.TongUserVinByServiceName(serviceName, timestart, timeend);
                    transByELK = apielkUserVin.GetUserVinByServiceName(serviceName, timestart, timeend,  numStart, totalrecord);

                } else if (nickName != null && !nickName.equals("")) {
                    totalPages = apielkUserVin.TongUserVinByNickName(nickName, timestart, timeend);
                    transByELK = apielkUserVin.GetUserVinByNickName(nickName, timestart, timeend,  numStart, totalrecord);
                } else {
                    totalPages = apielkUserVin.GetTongUserVin(timestart, timeend);
                    transByELK = apielkUserVin.GetUserVin(timestart, timeend, numStart, totalrecord);
                }
                Collections.sort(transByELK,(o1, o2)
                        -> o2.getTransactionTime().compareTo(
                        o1.getTransactionTime()));
                response.setTotalPages(totalPages);
                response.setTransactionsbyelk(transByELK);
                response.setSuccess(true);
                response.setErrorCode("0");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.debug((Object)e);
        }
        return response.toJson();
    }
}

