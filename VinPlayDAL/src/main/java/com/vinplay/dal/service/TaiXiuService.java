/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.vbee.common.models.cache.ThanhDuTXModel
 *  com.vinplay.vbee.common.models.minigame.TopWin
 *  com.vinplay.vbee.common.models.minigame.taixiu.XepHangRLTLModel
 */
package com.vinplay.dal.service;

import com.vinplay.dal.entities.report.ReportMoneySystemModel;
import com.vinplay.dal.entities.taixiu.*;
import com.vinplay.vbee.common.models.cache.ThanhDuTXModel;
import com.vinplay.vbee.common.models.minigame.TopWin;
import com.vinplay.vbee.common.models.minigame.taixiu.XepHangRLTLModel;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;

public interface TaiXiuService {
    public boolean saveTransactionTaiXiu(long var1, int var3, String var4, int var5, long var6, short var8, long var9, long var11, long totalExchange) throws IOException, TimeoutException, InterruptedException;

    public boolean saveNoHuTaiXiu(long phien, String result, long money, String userName, String userMoneyHu, long totalUser) throws IOException, TimeoutException, InterruptedException;

    public boolean saveTransactionTaiXiu(List<TransactionTaiXiu> var1) throws IOException, TimeoutException, InterruptedException;

    public boolean saveNoHuTaiXiu(List<NohuTXDetail> var1) throws IOException, TimeoutException, InterruptedException;

    public boolean saveTransactionTaiXiuDetails(List<TransactionTaiXiuDetail> var1) throws IOException, TimeoutException, InterruptedException;

    public boolean saveTransactionTaiXiuDetail(TransactionTaiXiuDetail var1) throws IOException, TimeoutException, InterruptedException;

    public boolean updateTransactionTaiXiuDetail(TransactionTaiXiuDetail var1) throws IOException, TimeoutException, InterruptedException;

    public boolean saveResultTaiXiu(long var1, int var3, int var4, int var5, int var6, long var7, long var9, int var11, int var12, long var13, long var15, long var17, long var19, int var21, long moneyHu, int statusHu) throws Exception;

    public boolean saveResultTaiXiu(ResultTaiXiu var1) throws Exception;

    public String getLichSuPhien(int var1, int var2) throws SQLException;

    public List<ResultTaiXiu> getListLichSuPhien(int var1, int var2) throws SQLException;

    public ResultTaiXiu getKetQuaPhien(long var1, int var3) throws SQLException;

    public List<TopWin> getTopWin(int var1, String userCurrent) throws SQLException;

    public List<TopWin> getTopWinVinhDanhByDay(int var1, String userCurrent) throws SQLException;

    public List<TopWin> getTopWinVinhDanhByMonth(int moneyType, String userCurrent) throws SQLException;

    public List<TopWin> getTopTxVinhDanhForAdmin(int moneyType, Instant beginTime, Instant endTime, int limitNumber) throws SQLException;

    public List<TransactionTaiXiu> getLichSuGiaoDich(String var1, int var2, int var3) throws SQLException;

    public int countLichSuGiaoDich(String var1, int var2) throws SQLException;

    public List<TransactionTaiXiuDetail> getChiTietPhienTX(long var1, int var3) throws SQLException;

    public List<NohuTXDetail> getHistoryNoHuTX(int page) throws SQLException;

    public void calculateThanhDu(long var1, List<TransactionTaiXiu> var3, int var4) throws IOException, TimeoutException, InterruptedException;

    public List<ThanhDuTXModel> getTopThanhDuDaily(String var1, int var2) throws SQLException;

    public List<ThanhDuTXModel> getTopThanhDuMonthly(String var1, int var2) throws SQLException, ParseException;

    public long getPotTanLoc() throws SQLException;

    public void logTanLoc(String var1, long var2) throws IOException, TimeoutException, InterruptedException;

    public void logRutLoc(String var1, long var2, int var4, long var5) throws IOException, TimeoutException, InterruptedException;

    public void updatePotTanLoc(long var1) throws IOException, TimeoutException, InterruptedException;

    public int updateLuotRutLoc(String var1, int var2) throws IOException, TimeoutException, InterruptedException;

    public int getLuotRutLoc(String var1) throws SQLException;

    public List<XepHangRLTLModel> getXepHangTanLoc();

    public List<VinhDanhRLTLModel> getVinhDanhTanLoc();

    public long getSoTienTanLoc(String var1);

    public List<XepHangRLTLModel> getXepHangRutLoc();

    public List<VinhDanhRLTLModel> getVinhDanhRutLoc();

    public long getSoTienRutLoc(String var1);

    public boolean updatePot(long var1, String var3);

    public boolean updateFund(long var1, String var3);

    public void updateAllTop();

    void updateAllTopDay();

    void updateAllTopMonth();

    public ReportMoneySystemModel getReportTXToday();

    public ReportMoneySystemModel getReportTX(int var1);
}

