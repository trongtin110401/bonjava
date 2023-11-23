package com.vinplay.bongda.dao;


import com.vinplay.bongda.entities.UserBetBongDa;
import com.vinplay.bongda.entities.UserBetBongDaModel;
import com.vinplay.bongda.entities.UserBetBongDaResponse;
import com.vinplay.bongda.entities.UserRequestClientResponse;

public interface UserBetBongDaDao {

    UserBetBongDa findUserBetBongDaBySessionAndId(String session, String id);


    boolean deleteUserBetBongDa(String id);

    boolean insertUserBetBongDa(UserBetBongDa keoBongDa);

    boolean updateUserBetBongDa(String id, int result, int moneyWin);

    UserBetBongDaResponse getListUserBetBongDa(String session, int page, int maxItem, String fromTime, String endTime);


    UserRequestClientResponse getListRequestBongDa(String session, String id);
}
