/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IMap
 *  com.vinplay.vbee.common.enums.Games
 *  com.vinplay.vbee.common.hazelcast.HazelcastClientFactory
 *  com.vinplay.vbee.common.messages.BaseMessage
 *  com.vinplay.vbee.common.messages.minigame.LogRutLocMessge
 *  com.vinplay.vbee.common.messages.minigame.LogTanLocMessage
 *  com.vinplay.vbee.common.messages.minigame.ResultTaiXiuMessage
 *  com.vinplay.vbee.common.messages.minigame.ThanhDuMessage
 *  com.vinplay.vbee.common.messages.minigame.TransactionTaiXiuDetailMessage
 *  com.vinplay.vbee.common.messages.minigame.TransactionTaiXiuMessage
 *  com.vinplay.vbee.common.messages.minigame.UpdateLuotRutLocMessage
 *  com.vinplay.vbee.common.messages.minigame.UpdatePotMessage
 *  com.vinplay.vbee.common.models.cache.RutLocCacheModel
 *  com.vinplay.vbee.common.models.cache.ThanhDuTXModel
 *  com.vinplay.vbee.common.models.cache.TopRLTLModel
 *  com.vinplay.vbee.common.models.cache.TopWinCache
 *  com.vinplay.vbee.common.models.minigame.TopWin
 *  com.vinplay.vbee.common.models.minigame.taixiu.XepHangRLTLModel
 *  com.vinplay.vbee.common.rmq.RMQApi
 *  org.apache.log4j.Logger
 */
package com.vinplay.dal.service.impl;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.dal.dao.OverUnderDAO;
import com.vinplay.dal.dao.impl.MiniGameDAOImpl;
import com.vinplay.dal.dao.impl.OverUnderDAOImpl;
import com.vinplay.dal.entities.report.ReportMoneySystemModel;
import com.vinplay.dal.entities.taixiu.ResultTaiXiu;
import com.vinplay.dal.entities.taixiu.TransactionTaiXiu;
import com.vinplay.dal.entities.taixiu.TransactionTaiXiuDetail;
import com.vinplay.dal.entities.taixiu.VinhDanhRLTLModel;
import com.vinplay.dal.service.OverUnderService;
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.messages.BaseMessage;
import com.vinplay.vbee.common.messages.minigame.LogRutLocMessge;
import com.vinplay.vbee.common.messages.minigame.LogTanLocMessage;
import com.vinplay.vbee.common.messages.minigame.ResultTaiXiuMessage;
import com.vinplay.vbee.common.messages.minigame.ThanhDuMessage;
import com.vinplay.vbee.common.messages.minigame.TransactionTaiXiuDetailMessage;
import com.vinplay.vbee.common.messages.minigame.TransactionTaiXiuMessage;
import com.vinplay.vbee.common.messages.minigame.UpdateLuotRutLocMessage;
import com.vinplay.vbee.common.messages.minigame.UpdatePotMessage;
import com.vinplay.vbee.common.models.cache.RutLocCacheModel;
import com.vinplay.vbee.common.models.cache.ThanhDuTXModel;
import com.vinplay.vbee.common.models.cache.TopRLTLModel;
import com.vinplay.vbee.common.models.cache.TopWinCache;
import com.vinplay.vbee.common.models.minigame.TopWin;
import com.vinplay.vbee.common.models.minigame.taixiu.XepHangRLTLModel;
import com.vinplay.vbee.common.rmq.RMQApi;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;
import org.apache.log4j.Logger;

public class OverUnderServiceImpl
implements OverUnderService {
    private Logger logger = Logger.getLogger((String)"rmq");
    private OverUnderDAO dao = new OverUnderDAOImpl();

    @Override
    public boolean saveTransactionTaiXiu(long referenceId, int userId, String username, int moneyType, long betValue, short betSide, long prize, long refund) throws IOException, TimeoutException, InterruptedException {
        TransactionTaiXiuMessage msg = new TransactionTaiXiuMessage();
        msg.referenceId = referenceId;
        msg.userId = userId;
        msg.username = username;
        msg.moneyType = moneyType;
        msg.betValue = betValue;
        msg.betSide = betSide;
        msg.prize = prize;
        msg.refund = refund;
        RMQApi.publishMessage((String)"queue_overunder", (BaseMessage)msg, (int)10100);
        return true;
    }

    @Override
    public boolean saveResultTaiXiu(long referenceId, int result, int dice1, int dice2, int dice3, long totalTai, long totalXiu, int numBetTai, int numBetXiu, long totalPrize, long totalRefundTai, long totalRefundXiu, long totalRevenue, int moneyType) throws Exception {
        ResultTaiXiuMessage msg = new ResultTaiXiuMessage();
        msg.referenceId = referenceId;
        msg.result = result;
        msg.dice1 = dice1;
        msg.dice2 = dice2;
        msg.dice3 = dice3;
        msg.totalTai = totalTai;
        msg.totalXiu = totalXiu;
        msg.numBetTai = numBetTai;
        msg.numBetXiu = numBetXiu;
        msg.totalPrize = totalPrize;
        msg.totalRefundTai = totalRefundTai;
        msg.totalRefundXiu = totalRefundXiu;
        msg.totalRevenue = totalRevenue;
        msg.moneyType = moneyType;
        RMQApi.publishMessage((String)"queue_overunder", (BaseMessage)msg, (int)10101);
        return true;
    }

    @Override
    public String getLichSuPhien(int soPhien, int moneyType) throws SQLException {
        List<ResultTaiXiu> results = this.dao.getLichSuPhien(soPhien, moneyType);
        return this.buildLichSuPhien(results, soPhien);
    }

    @Override
    public List<TopWin> getTopWin(int moneyType) throws SQLException {
        TopWinCache topTXCache;
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap topMap = client.getMap("cacheTop");
        if (topMap.containsKey((Object)(Games.OVER_UNDER.getName() + "_" + moneyType)) && (topTXCache = (TopWinCache)topMap.get((Object)(Games.OVER_UNDER.getName() + "_" + moneyType))) != null) {
            return topTXCache.getResult();
        }
        List<TopWin> topWins = this.dao.getTopTaiXiu(moneyType);
        return  topWins;
//        return new ArrayList<TopWin>();
    }

    @Override
    public void updateAllTop() {
        try {
            logger.debug("Run updateAllTop");

            List<TopWin> topWinVin = this.dao.getTopTaiXiu(1);
            this.logger.debug((Object)("TOP WIN VIN: " + topWinVin.size()));
            List<TopWin> topWinXu = this.dao.getTopTaiXiu(0);
            this.logger.debug((Object)("TOP WIN XU: " + topWinXu.size()));
            HazelcastInstance client = HazelcastClientFactory.getInstance();
            IMap topMap = client.getMap("cacheTop");
            TopWinCache cacheVin = (TopWinCache)topMap.get((Object)(Games.OVER_UNDER.getName() + "_1"));
            if (cacheVin == null) {
                cacheVin = new TopWinCache();
            }
            cacheVin.setResult(topWinVin);
            topMap.put((Object)(Games.OVER_UNDER.getName() + "_1"), (Object)cacheVin);
            TopWinCache cacheXu = (TopWinCache)topMap.get((Object)(Games.OVER_UNDER.getName() + "_0"));
            if (cacheXu == null) {
                cacheXu = new TopWinCache();
            }
            cacheXu.setResult(topWinXu);
            topMap.put((Object)(Games.OVER_UNDER.getName() + "_0"), (Object)cacheXu);
        }
        catch (SQLException e) {
            this.logger.error((Object)"UPDATE ALL TOP exception: ", (Throwable)e);
            e.printStackTrace();
        }
    }

    @Override
    public List<TransactionTaiXiu> getLichSuGiaoDich(String username, int page, int moneyType) throws SQLException {
        return this.dao.getLichSuGiaoDich(username, page, moneyType);
    }

    @Override
    public List<ResultTaiXiu> getListLichSuPhien(int soPhien, int moneyType) throws SQLException {
        List<ResultTaiXiu> results = this.dao.getLichSuPhien(soPhien, moneyType);
        return results;
    }

    @Override
    public boolean saveTransactionTaiXiuDetails(List<TransactionTaiXiuDetail> trans) throws IOException, TimeoutException, InterruptedException {
        boolean success = true;
        for (TransactionTaiXiuDetail tran : trans) {
            success = success && this.saveTransactionTaiXiuDetail(tran);
        }
        return success;
    }

    @Override
    public boolean saveResultTaiXiu(ResultTaiXiu rs) throws Exception {
        this.logger.debug((Object)"Save result tx");
        return this.saveResultTaiXiu(rs.referenceId, rs.result, rs.dice1, rs.dice2, rs.dice3, rs.totalTai, rs.totalXiu, rs.numBetTai, rs.numBetXiu, rs.totalPrize, rs.totalRefundTai, rs.totalRefundXiu, rs.totalRevenue, rs.moneyType);
    }

    @Override
    public boolean saveTransactionTaiXiu(List<TransactionTaiXiu> trans) throws IOException, TimeoutException, InterruptedException {
        for (TransactionTaiXiu tran : trans) {
            this.saveTransactionTaiXiu(tran.referenceId, tran.userId, tran.username, tran.moneyType, tran.betValue, (short)tran.betSide, tran.totalPrize, tran.totalRefund);
        }
        return false;
    }

    @Override
    public boolean saveTransactionTaiXiuDetail(TransactionTaiXiuDetail tran) throws IOException, TimeoutException, InterruptedException {
        TransactionTaiXiuDetailMessage msg = new TransactionTaiXiuDetailMessage();
        msg.referenceId = tran.referenceId;
        msg.transactionCode = tran.transactionCode;
        msg.userId = tran.userId;
        msg.username = tran.username;
        msg.betValue = tran.betValue;
        msg.betSide = tran.betSide;
        msg.prize = tran.prize;
        msg.refund = tran.refund;
        msg.inputTime = tran.inputTime;
        msg.moneyType = tran.moneyType;
        RMQApi.publishMessage((String)"queue_overunder", (BaseMessage)msg, (int)10102);
        return true;
    }

    @Override
    public int countLichSuGiaoDich(String nickname, int moneyType) throws SQLException {
        int totalRecords = this.dao.countLichSuGiaoDichTX(nickname, moneyType);
        return totalRecords / 10 + 1;
    }

    @Override
    public List<TransactionTaiXiuDetail> getChiTietPhienTX(long referenceId, int moneyType) throws SQLException {
        List<TransactionTaiXiuDetail> results = this.dao.getChiTietPhien(referenceId, moneyType);
        return results;
    }

    @Override
    public ResultTaiXiu getKetQuaPhien(long referenceId, int moneyType) throws SQLException {
        ResultTaiXiu resultTX = this.dao.getKetQuaPhien(referenceId, moneyType);
        return resultTX;
    }

    @Override
    public boolean updateTransactionTaiXiuDetail(TransactionTaiXiuDetail tran) throws IOException, TimeoutException, InterruptedException {
        TransactionTaiXiuDetailMessage msg = new TransactionTaiXiuDetailMessage();
        msg.referenceId = tran.referenceId;
        msg.transactionCode = tran.transactionCode;
        msg.userId = tran.userId;
        msg.username = tran.username;
        msg.betValue = tran.betValue;
        msg.betSide = tran.betSide;
        msg.prize = tran.prize;
        msg.refund = tran.refund;
        msg.inputTime = tran.inputTime;
        msg.moneyType = tran.moneyType;
        RMQApi.publishMessage((String)"queue_overunder", (BaseMessage)msg, (int)10103);
        return true;
    }

    @Override
    public void calculateThanhDu(long referenceId, List<TransactionTaiXiu> transacntions, int result) throws IOException, TimeoutException, InterruptedException {
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap winMap = client.getMap("cacheWinThanhDuOU");
        IMap lossMap = client.getMap("cacheLossThanhDuOU");
        for (TransactionTaiXiu tran : transacntions) {
            long moneyExchange;
            if (tran.betSide == result) {
                moneyExchange = tran.betValue - tran.totalRefund;
                if (moneyExchange >= 2000L) {
                    this.incrementThanhDu(referenceId, (IMap<String, ThanhDuTXModel>)winMap, tran.username, moneyExchange, 1);
                }
                if (moneyExchange <= 0L) continue;
                this.clearThanhDu((IMap<String, ThanhDuTXModel>)lossMap, tran.username, 0);
                continue;
            }
            moneyExchange = tran.betValue - tran.totalRefund;
            if (moneyExchange >= 2000L) {
                this.incrementThanhDu(referenceId, (IMap<String, ThanhDuTXModel>)lossMap, tran.username, moneyExchange, 0);
            }
            if (moneyExchange <= 0L) continue;
            this.clearThanhDu((IMap<String, ThanhDuTXModel>)winMap, tran.username, 1);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    private void incrementThanhDu(long referenceId, IMap<String, ThanhDuTXModel> map, String username, long moneyExchange, int type) throws IOException, TimeoutException, InterruptedException {
        if (moneyExchange < 2000L) return;
        ThanhDuTXModel model = null;
        ThanhDuTXModel thanhDuTXModel2;
        ThanhDuTXModel thanhDuTXModel;
        lbl42: // 7 sources:
        if (map.containsKey((Object)username)) {
            try {
                map.lock(username);
                model = (ThanhDuTXModel)map.get((Object)username);
                if (model == null)
                    break lbl42;
                if (!model.playOnToday()) {
                    model = new ThanhDuTXModel(username);
                } else {
                    thanhDuTXModel = model;
                    ++thanhDuTXModel.number;
                }
                model.addReference(referenceId);
                thanhDuTXModel2 = model;
                thanhDuTXModel2.totalValue += moneyExchange;
                if (!model.valid && moneyExchange >= 10000L) {
                    model.valid = true;
                }
                if (model.number <= model.maxNumber || !model.valid) break lbl42;
                model.maxNumber = model.number;
                ThanhDuMessage message = new ThanhDuMessage(model.username, model.number, model.totalValue, model.currentReferenceId, model.getReferences(), (short)type);
                RMQApi.publishMessage((String)"queue_overunder", (BaseMessage)message, (int)10104);
            }
            catch (Exception e) {
            }
            finally {


                map.unlock(username);
            }
        } else {
            model = new ThanhDuTXModel(username);
            model.totalValue = moneyExchange;
            model.addReference(referenceId);


            int max = 0;
            try {
                max = this.dao.getMaxThanhDu(username, (short)type);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            model.maxNumber = max;
            if (moneyExchange >= 10000L) {
                model.valid = true;
                ThanhDuMessage message2 = new ThanhDuMessage(model.username, model.number, model.totalValue, model.currentReferenceId, model.getReferences(), (short)type);
                RMQApi.publishMessage((String)"queue_overunder", (BaseMessage)message2, (int)10104);
            }
        }

        if (model == null) return;
        map.put(username, model);
    }

    private void clearThanhDu(IMap<String, ThanhDuTXModel> map, String username, int type) {
        ThanhDuTXModel model;
        if (map.containsKey((Object)username) && (model = (ThanhDuTXModel)map.get((Object)username)) != null) {
            model.clear();
            map.put(username, model);
        }
    }

    @Override
    public List<ThanhDuTXModel> getTopThanhDuDaily(String dateStr, int type) throws SQLException {
        String startTime = dateStr + " 00:00:00";
        String endTime = dateStr + " 23:59:59";
        return this.dao.getTopThanhDuDaily(startTime, endTime, (short)type);
    }

    @Override
    public List<ThanhDuTXModel> getTopThanhDuMonthly(String dateStr, int type) throws SQLException, ParseException {
        String string = dateStr + "-01";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dt = sdf.parse(string);
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        String startDate = sdf.format(c.getTime());
        String startTime = startDate + " 00:00:00";
        c.add(2, 1);
        c.add(5, -1);
        String endDate = sdf.format(c.getTime());
        String endTime = endDate + " 23:59:59";
        return this.dao.getTopThanhDuDaily(startTime, endTime, (short)type);
    }

    @Override
    public long getPotTanLoc() throws SQLException {
        MiniGameDAOImpl miniGameDAO = new MiniGameDAOImpl();
        return miniGameDAO.getPot("tan_loc_ou");
    }

    @Override
    public void logTanLoc(String username, long money) throws IOException, TimeoutException, InterruptedException {
        LogTanLocMessage message = new LogTanLocMessage();
        message.username = username;
        message.value = money;
        RMQApi.publishMessage((String)"queue_overunder", (BaseMessage)message, (int)10107);
    }

    @Override
    public void updatePotTanLoc(long newValue) throws IOException, TimeoutException, InterruptedException {
        UpdatePotMessage message = new UpdatePotMessage();
        message.newValue = newValue;
        message.potName = "tan_loc_ou";
        RMQApi.publishMessage((String)"queue_pot", (BaseMessage)message, (int)106);
    }

    @Override
    public void logRutLoc(String username, long prize, int timeRequest, long currentFund) throws IOException, TimeoutException, InterruptedException {
        LogRutLocMessge message = new LogRutLocMessge();
        message.username = username;
        message.prize = prize;
        message.timeRequest = timeRequest;
        message.currentFund = currentFund;
        RMQApi.publishMessage((String)"queue_overunder", (BaseMessage)message, (int)10108);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int updateLuotRutLoc(String username, int soLuotThem) throws IOException, TimeoutException, InterruptedException {
        UpdateLuotRutLocMessage message = new UpdateLuotRutLocMessage();
        message.username = username;
        message.soLuotThem = soLuotThem;
        RMQApi.publishMessage((String)"queue_overunder", (BaseMessage)message, (int)10109);
        int soLuotRut = soLuotThem;
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap userMap = client.getMap("cacheRutLocOU");
        if (userMap.containsKey((Object)username)) {
            try {
                 userMap.lock(username);
                RutLocCacheModel model = (RutLocCacheModel)userMap.get((Object)username);
                soLuotRut = model.addSoLuotRut(soLuotThem);
                 userMap.put(username, model);
            }
            catch (Exception e) {
                this.logger.error((Object)e);
            }
            finally {
                 userMap.unlock(username);
            }
        } else {
            RutLocCacheModel model = new RutLocCacheModel(soLuotThem);
             userMap.put(username, model);
        }
        return soLuotRut;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getLuotRutLoc(String username) throws SQLException {
        int soLuot = 0;
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap userMap = client.getMap("cacheRutLocOU");
        if (userMap.containsKey((Object)username)) {
            try {
                 userMap.lock(username);
                RutLocCacheModel model = (RutLocCacheModel)userMap.get((Object)username);
                soLuot = model.getSoLuotRut();
            }
            catch (Exception e) {
                this.logger.error((Object)e);
            }
            finally {
                 userMap.unlock(username);
            }
        } else {
            soLuot = this.dao.getSoLanRutLoc(username);
            RutLocCacheModel model = new RutLocCacheModel(soLuot);
             userMap.put(username, model);
        }
        return soLuot;
    }

    @Override
    public List<XepHangRLTLModel> getXepHangTanLoc() {
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap topMap = client.getMap("cacheTop");
        if (topMap != null) {
            TopRLTLModel topTanLoc = (TopRLTLModel)topMap.get((Object)"TopTanLocOU");
            if (topTanLoc == null) {
                topTanLoc = new TopRLTLModel();
            }
            if (topTanLoc.getResults().size() == 0) {
                List<XepHangRLTLModel> results = this.dao.getXepHangTanLoc();
                topTanLoc.setResults(results);
                topMap.put("TopTanLocOU", (Object)topTanLoc);
            }
            return topTanLoc.getResults();
        }
        return this.dao.getXepHangTanLoc();
    }

    @Override
    public List<VinhDanhRLTLModel> getVinhDanhTanLoc() {
        return this.dao.getVinhDanhTanLoc();
    }

    @Override
    public long getSoTienTanLoc(String username) {
        return this.dao.getTongTienTanLoc(username);
    }

    @Override
    public List<XepHangRLTLModel> getXepHangRutLoc() {
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap topMap = client.getMap("cacheTop");
        if (topMap != null) {
            TopRLTLModel topRutLoc = (TopRLTLModel)topMap.get((Object)"TopRutLocOU");
            if (topRutLoc == null) {
                topRutLoc = new TopRLTLModel();
            }
            if (topRutLoc.getResults().size() == 0) {
                List<XepHangRLTLModel> results = this.dao.getXepHangRutLoc();
                topRutLoc.setResults(results);
                topMap.put("TopRutLocOU", (Object)topRutLoc);
            }
            return topRutLoc.getResults();
        }
        return this.dao.getXepHangRutLoc();
    }

    @Override
    public List<VinhDanhRLTLModel> getVinhDanhRutLoc() {
        return this.dao.getVinhDanhRutLoc();
    }

    @Override
    public long getSoTienRutLoc(String username) {
        return this.dao.getTongTienRutLoc(username);
    }

    @Override
    public boolean updatePot(long pot, String potName) {
        return false;
    }

    @Override
    public boolean updateFund(long fund, String potName) {
        return false;
    }

    private String buildLichSuPhien(List<ResultTaiXiu> input, int number) {
        int end = input.size();
        int start = end - number > 0 ? end - number : 0;
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < end; ++i) {
            ResultTaiXiu entry = input.get(i);
            builder.append(entry.dice1);
            builder.append(",");
            builder.append(entry.dice2);
            builder.append(",");
            builder.append(entry.dice3);
            builder.append(",");
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    @Override
    public ReportMoneySystemModel getReportTXToday() {
        return this.dao.getReportTXToDay();
    }

    @Override
    public ReportMoneySystemModel getReportTX(int range) {
        ReportMoneySystemModel reportTX;
        ReportMoneySystemModel todayModel = this.getReportTXToday();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();
        String endDate = sdf.format(cal.getTime());
        int date = cal.get(5);
        int month = cal.get(2);
        int dateBefore = date % range - 1;
        cal.add(5, -dateBefore);
        String startDate = sdf.format(cal.getTime());
        int dateAfter = range - dateBefore;
        Calendar calTmp = Calendar.getInstance();
        calTmp.add(5, dateAfter);
        String dateReset = sdf.format(calTmp.getTime());
        int monthTmp = calTmp.get(2);
        if (monthTmp != month) {
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/01");
            dateReset = sdf2.format(calTmp.getTime());
        }
        ReportMoneySystemModel result = reportTX = this.dao.getReportTX(startDate, endDate);
        reportTX.moneyWin += todayModel.moneyWin;
        ReportMoneySystemModel reportMoneySystemModel = result;
        reportMoneySystemModel.moneyLost += todayModel.moneyLost;
        ReportMoneySystemModel reportMoneySystemModel2 = result;
        reportMoneySystemModel2.fee += todayModel.fee;
        ReportMoneySystemModel reportMoneySystemModel3 = result;
        reportMoneySystemModel3.moneyOther += todayModel.moneyOther;
        ReportMoneySystemModel reportMoneySystemModel4 = result;
        reportMoneySystemModel4.revenuePlayGame += todayModel.revenuePlayGame;
        ReportMoneySystemModel reportMoneySystemModel5 = result;
        reportMoneySystemModel5.revenue += todayModel.revenue;
        result.dateReset = dateReset;
        return result;
    }
}

