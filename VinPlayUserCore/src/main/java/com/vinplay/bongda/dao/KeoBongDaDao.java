package com.vinplay.bongda.dao;

import com.vinplay.bongda.entities.KeoBongDa;
import com.vinplay.bongda.entities.KeoBongDaResponse;
import com.vinplay.bongda.entities.UserBetBongDaResponse;
import com.vinplay.payment.entities.UserWithdraw;

public interface KeoBongDaDao {

    KeoBongDa findKeoBongDaBySessionAndId(String session, String id);

    KeoBongDa findKeoBongDaBySessionAndId(String id);

    boolean insertKeoBongDa(KeoBongDa keoBongDa);

    boolean updateKeoBongDa(KeoBongDa keoBongDa);

    KeoBongDaResponse getListKeoBongDa(String session, int page, int maxItem, String fromTime, String endTime);

    KeoBongDaResponse getListKeoBongDaClient(String session);
    boolean deletekeoBongDa(String id);
}
