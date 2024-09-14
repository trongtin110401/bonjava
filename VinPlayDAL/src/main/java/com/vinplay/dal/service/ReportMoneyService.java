package com.vinplay.dal.service;

import com.vinplay.dal.entities.report.ReportMoneyModelNew;

import java.util.List;

public interface ReportMoneyService {

    public List<ReportMoneyModelNew> search(String nickName, String actionName, String timeStart,
                                            String timeEnd, int page, int totalRecord);

}
