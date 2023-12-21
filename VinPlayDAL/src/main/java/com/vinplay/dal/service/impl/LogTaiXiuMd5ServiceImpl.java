/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.vbee.common.response.TaiXiuDetailReponse
 *  com.vinplay.vbee.common.response.TaiXiuResponse
 *  com.vinplay.vbee.common.response.TaiXiuResultResponse
 */
package com.vinplay.dal.service.impl;

import com.vinplay.dal.service.LogTaiXiuService;
import com.vinplay.vbee.common.response.TaiXiuDetailReponse;
import com.vinplay.vbee.common.response.TaiXiuResponse;
import com.vinplay.vbee.common.response.TaiXiuResultResponse;

import java.util.List;

public class LogTaiXiuMd5ServiceImpl
implements LogTaiXiuService {

    LogTaiXiuMd5ServiceImpl dao = new LogTaiXiuMd5ServiceImpl();

    @Override
    public List<TaiXiuResponse> listLogTaiXiu(String referentId, String userName, String betSide, String moneyType, String timeStart, String timeEnd, int page) {
        return dao.listLogTaiXiu(referentId, userName, betSide, moneyType, timeStart, timeEnd, page);
    }

    @Override
    public int countLogTaiXiu(String referentId, String userName, String betSide, String moneyType, String timeStart, String timeEnd) {
        return dao.countLogTaiXiu(referentId, userName, betSide, moneyType, timeStart, timeEnd);
    }

    @Override
    public List<TaiXiuDetailReponse> getLogTaiXiuDetail(String referent_id, String betSide, String moneyType, String nickName, int page) {
        return dao.getLogTaiXiuDetail(referent_id, betSide, moneyType, nickName, page);
    }

    @Override
    public int countLogTaiXiuDetail(String referent_id, String betSide, String moneyType, String nickName) {
        return dao.countLogTaiXiuDetail(referent_id, betSide, moneyType, nickName);
    }

    @Override
    public List<TaiXiuResultResponse> listLogTaiXiuResult(String referentId, String moneyType, String timeStart, String timeEnd, int page) {
        return dao.listLogTaiXiuResult(referentId, moneyType, timeStart, timeEnd, page);
    }

    @Override
    public int countLogTaiXiuResult(String referentId, String moneyType, String timeStart, String timeEnd) {
        return dao.countLogTaiXiuResult(referentId, moneyType, timeStart, timeEnd);
    }
}

