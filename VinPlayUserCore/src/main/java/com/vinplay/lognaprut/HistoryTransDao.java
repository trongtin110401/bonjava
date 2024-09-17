package com.vinplay.lognaprut;

import com.vinplay.lognaprut.entities.HistoryTransModel;
import com.vinplay.lognaprut.entities.HistoryTransResponse;
import com.vinplay.lognaprut.entities.ReportAdminTransferMoneyResponse;

import java.util.List;

public interface HistoryTransDao {
    void insertTransaction(HistoryTransModel historyTransModel);

    HistoryTransModel findTransaction(String transId, String nickName, String hinhthucTrans);

    HistoryTransModel findTransactionByTransId(String transId);
    List<HistoryTransModel> findCodepayLike(String codepay);
    boolean updateTransaction(HistoryTransModel historyTransModel);

    HistoryTransResponse getListTransByName(String nickName, int page, int maxItem);
    HistoryTransResponse getListTransByDay(String nickName, String fromTime, String endTime);
    HistoryTransResponse getListTransNapByName(String nickName, int page, int maxItem);
    HistoryTransResponse getListTransRutTheByName(String nickName, int page, int maxItem);
    HistoryTransResponse getListTransRutBankByName(String nickName, int page, int maxItem);

    ReportAdminTransferMoneyResponse getTotalAdminTransferByDay(String startTime, String endTime);
    HistoryTransModel getFirstTrans(String nickname, String startTime);
}
