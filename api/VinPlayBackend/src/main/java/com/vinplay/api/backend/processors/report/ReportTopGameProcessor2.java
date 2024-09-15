/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IMap
 *  com.vinplay.dal.dao.impl.ReportDaoImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.hazelcast.HazelcastClientFactory
 *  com.vinplay.vbee.common.models.TopCaoThu
 *  com.vinplay.vbee.common.models.cache.ReportModel
 *  com.vinplay.vbee.common.statics.Consts
 *  com.vinplay.vbee.common.utils.MapUtils
 *  com.vinplay.vbee.common.utils.VinPlayUtils
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.backend.processors.report;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.api.backend.response.ReportTopGameResponse;
import com.vinplay.dal.dao.impl.ReportDao2Impl;
import com.vinplay.dal.dao.impl.ReportDaoImpl;
import com.vinplay.usercore.service.OtherService;
import com.vinplay.usercore.service.impl.OtherServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.models.TopCaoThu;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.models.cache.ReportModel;
import com.vinplay.vbee.common.statics.Consts;
import com.vinplay.vbee.common.utils.MapUtils;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ReportTopGameProcessor2 implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "report");

    public String execute(Param<HttpServletRequest> param) {
        ReportTopGameResponse res = new ReportTopGameResponse(false, "1001");
        try {

            HttpServletRequest request = (HttpServletRequest) param.get();
            String action = request.getParameter("ac");
            int displayNumber = Integer.parseInt(request.getParameter("n"));
            String startTime = request.getParameter("ts");
            String endTime = request.getParameter("te");


            List<String> actions;
            if (StringUtils.isNotEmpty(action)) {
                actions = Collections.singletonList(action);
            } else {
                // ko tính bắn cá
                actions = Consts.GAMES.stream().filter(s -> !s.equals("HamCaMap")).collect(Collectors.toList());
            }

            ReportDao2Impl dao = new ReportDao2Impl();
            List<TopCaoThu> topWin = dao.topPlayer(startTime, endTime, actions, 1, 1, displayNumber);
            List<TopCaoThu> topLose = dao.topPlayer(startTime, endTime, actions, 0, 1, displayNumber);

            res.topUserWin = topWin;
            res.topUserLost = topLose;
            res.topBotWin = new ArrayList<>();
            res.topBotLost = new ArrayList<>();
            res.setSuccess(true);
            res.setErrorCode("success");
            return res.toJson();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}

