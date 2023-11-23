package com.vinplay.lognaprut.service.impl;

import com.vinplay.lognaprut.HistoryTransDao;
import com.vinplay.lognaprut.entities.HistoryTransModel;
import com.vinplay.lognaprut.impl.HistoryTransDaoImpl;
import com.vinplay.lognaprut.service.HistoryTransService;
import com.vinplay.utils.TelegramUtil;

public class HistoryTransServiceImpl implements HistoryTransService {

    @Override
    public boolean update(String transId, String nickName, String transType, String trangthai, String ghichu) {
        try {
            HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
            HistoryTransModel historyTransModel = historyTransDao.findTransaction(transId, nickName, transType);
            if(historyTransModel == null) {
                return false;
            }
            historyTransModel.setTrangthai(trangthai);
            historyTransModel.setGhiChu(ghichu);
//        new TelegramUtil().senMessToDaily(nickName, trangthai + transType, Long.parseLong(historyTransModel.sotien), ghichu);
            return historyTransDao.updateTransaction(historyTransModel);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
