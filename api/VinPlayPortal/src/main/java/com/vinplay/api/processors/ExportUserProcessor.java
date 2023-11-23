/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.dal.service.impl.AgentServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.response.ResultAgentRespone
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.processors;

import com.vinplay.api.entities.CSVUtils;
import com.vinplay.dal.service.impl.AgentServiceImpl;
import com.vinplay.usercore.dao.UserInfoDao;
import com.vinplay.usercore.dao.impl.UserDaoImpl;
import com.vinplay.usercore.dao.impl.UserInfoDaoImpl;
import com.vinplay.usercore.entities.ExportUser;
import com.vinplay.usercore.service.impl.MoneyInGameServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.response.AgentResponse;
import com.vinplay.vbee.common.response.ResultAgentRespone;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

public class ExportUserProcessor
implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"api");

    public String execute(Param<HttpServletRequest> param) {        
        AgentServiceImpl service = new AgentServiceImpl();
        HttpServletRequest request = (HttpServletRequest)param.get();
        String startDate = request.getParameter("sd");
        String endDate = request.getParameter("ed");
        try {
            Writer writer = new StringWriter();
            CSVUtils.writeLine(writer, Arrays.asList("nick_name", "mobile", "recharge_money"));
            UserInfoDao dao = new UserInfoDaoImpl();
            List<ExportUser> users = dao.GetExportUser(startDate + " 00:00:00", endDate + " 23:59:59");
            for (ExportUser d : users) {

                List<String> list = new ArrayList<>();
                list.add(d.getNick_name());
                list.add(d.getMobile());
                list.add(d.getRecharge_money() + "");
                CSVUtils.writeLine(writer, list);
            }
            return writer.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.debug((Object)e);
        }
        return "";
    }
}

