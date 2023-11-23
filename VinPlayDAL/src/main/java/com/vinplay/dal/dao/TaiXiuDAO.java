/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.vbee.common.models.cache.ThanhDuTXModel
 *  com.vinplay.vbee.common.models.minigame.TopWin
 *  com.vinplay.vbee.common.models.minigame.taixiu.XepHangRLTLModel
 */
package com.vinplay.dal.dao;

import com.vinplay.dal.entities.report.ReportMoneySystemModel;
import com.vinplay.dal.entities.taixiu.*;
import com.vinplay.vbee.common.models.cache.ThanhDuTXModel;
import com.vinplay.vbee.common.models.minigame.TopWin;
import com.vinplay.vbee.common.models.minigame.taixiu.XepHangRLTLModel;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public interface TaiXiuDAO {
    List<ResultTaiXiu> getLichSuPhien(int var1, int var2) throws SQLException;

    ResultTaiXiu getKetQuaPhien(long var1, int var3) throws SQLException;

    List<TransactionTaiXiu> getLichSuGiaoDich(String var1, int var2, int var3) throws SQLException;


    Long getMoneyStakesTX(String nickname) throws SQLException;

    int countLichSuGiaoDichTX(String var1, int var2) throws SQLException;

    List<TopWin> getTopTaiXiu(int var1) throws SQLException;

    List<TopWin> getTopTaiXiuVinhDanh(int moneyType, Timestamp beginTime, Timestamp endTime, int limitNumber) throws SQLException;

    Long getMoneyStakesTXByMonth(String nickname, Timestamp beginTime, Timestamp endTime) throws SQLException;

    List<TransactionTaiXiuDetail> getChiTietPhien(long var1, int var3) throws SQLException;

    List<NohuTXDetail> getHistoryNoHuTX(int page) throws SQLException;

    List<ThanhDuTXModel> getTopThanhDuDaily(String var1, String var2, short var3) throws SQLException;

    int getMaxThanhDu(String var1, short var2) throws SQLException;

    int getSoLanRutLoc(String var1) throws SQLException;

    List<XepHangRLTLModel> getXepHangTanLoc();

    List<VinhDanhRLTLModel> getVinhDanhTanLoc();

    long getTongTienTanLoc(String var1);

    List<XepHangRLTLModel> getXepHangRutLoc();

    List<VinhDanhRLTLModel> getVinhDanhRutLoc();

    long getTongTienRutLoc(String var1);

    ReportMoneySystemModel getReportTXToDay();

    ReportMoneySystemModel getReportTX(String var1, String var2);
}

