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

import com.vinplay.api.backend.response.FeeResponse;
import com.vinplay.dal.dao.impl.ReportDaoImpl;
import com.vinplay.dal.entities.report.ReportMoneySystemModel;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class GetFeeProcessor
        implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        FeeResponse feeResponse = new FeeResponse(false, "1001");
        HttpServletRequest request = param.get();
        String startTime = request.getParameter("ts");
        String endTime = request.getParameter("te");
        long totalFee = 0;
        try {
            ReportDaoImpl dao = new ReportDaoImpl();
            Map<String, ReportMoneySystemModel> map = dao.getReportMoneySystemMySQL(startTime, endTime, false);
            for (Map.Entry<String, ReportMoneySystemModel> entry : map.entrySet()) {
                ReportMoneySystemModel value = entry.getValue();
                totalFee += value.fee;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return feeResponse.toJson();
        }
        feeResponse.setTotalFee(totalFee);
        feeResponse.setSuccess(true);
        feeResponse.setErrorCode("Ok");
        return feeResponse.toJson();
    }
}

