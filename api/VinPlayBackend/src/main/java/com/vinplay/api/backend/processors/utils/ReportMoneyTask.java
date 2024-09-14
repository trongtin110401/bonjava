/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IMap
 *  com.vinplay.dal.dao.impl.ReportDaoImpl
 *  com.vinplay.dal.entities.report.ReportTotalMoneyModel
 *  com.vinplay.dal.service.LogPortalService
 *  com.vinplay.dal.service.impl.LogPortalServiceImpl
 *  com.vinplay.usercore.utils.GameCommon
 *  com.vinplay.vbee.common.hazelcast.HazelcastClientFactory
 *  com.vinplay.vbee.common.utils.VinPlayUtils
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.backend.processors.utils;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.dal.dao.impl.ReportDaoImpl;
import com.vinplay.dal.entities.report.ReportTotalMoneyModel;
import com.vinplay.dal.service.LogPortalService;
import com.vinplay.dal.service.impl.LogPortalServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.utils.VinPlayUtils;

import java.util.Date;
import java.util.Map;
import java.util.TimerTask;

import org.apache.log4j.Logger;

public class ReportMoneyTask extends TimerTask {
    private static final Logger logger = Logger.getLogger((String) "report");
    private static LogPortalService service = new LogPortalServiceImpl();

    @Override
    public void run() {
        try {

            service.saveLog();

            boolean remove;
            Date timeRun = new Date();

            String date = "";
            String today = VinPlayUtils.getCurrentDate();
            String yesterday = VinPlayUtils.getYesterday();
            String tomorrow = VinPlayUtils.getTomorrowString();

            HazelcastInstance client = HazelcastClientFactory.getInstance();
            ReportDaoImpl dao = new ReportDaoImpl();

            // T?ng h?p b�o c�o lu?ng ti?n h�m qua
            ReportMoneyUtils.fixYesterdayData(today, yesterday, false);
            // T?ng h?p b�o c�o lu?ng ti?n h�m nay
            ReportMoneyUtils.fixYesterdayData(tomorrow, today, true);

            // T?ng h?p b�o c�o bi?u ?? lu?ng ti?n
            String superAgent = GameCommon.getValueStr((String) "SUPER_AGENT");
            ReportTotalMoneyModel model = dao.getTotalMoney(superAgent);
            dao.saveLogTotalMoney(model);
            remove = true;
            date = yesterday;
            logger.info("Save report " + date + " start at " + timeRun);
            IMap<String, Object> reportMap = client.getMap("cacheReports");
            for (Map.Entry entry : reportMap.entrySet()) {
                if (!((String) entry.getKey()).contains(date) || !remove) continue;
                reportMap.remove(entry.getKey());
            }
            logger.info((Object) ("Save report " + timeRun + " success at " + new Date()));
        } catch (Exception e) {
            logger.debug((Object) e);
            e.printStackTrace();
        }
    }
}

