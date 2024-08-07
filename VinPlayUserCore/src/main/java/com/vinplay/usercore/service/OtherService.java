/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.usercore.service;

import com.vinplay.vbee.common.response.*;
import org.bson.Document;
import org.python.parser.ast.Str;

import java.util.List;

public interface OtherService {

    LinkSocialResponse getLinkSocial();

    void updateLinkSocial(LinkSocialResponse response);

    void saveTransactionUpdateFund(Document document);

    void saveExpenseTransaction(Document document);

    TransactionFundResponse getTransactionFund(int pageIndex, int pageSize, String type, String startTime, String endTime, String fundName);

    TransactionExpenseResponse getTransactionExpense(int pageIndex, int pageSize, String type, String startTime, String endTime, String expense);

    UserTele getUserTeleInfoByNickname(String nickname);

    void saveUserTeleCashBack(Document document);

    void updateCodeCallBack(String code);

    UserLoseByDayResponse getListUserTeleCashBack(int pageIndex, int pageSize, String timeStart, String timeEnd, String nickname, String code, String type);


    MoneyShootFishResponse getMoneyShootFish(String startTime, String endTime);

    List<MoneyShootFishResponse> getTotalShootFish(String startTime, String endTime, String nickname) throws Exception;


    long getTotalShootFishByNickname(String startTime, String endTime, String nickname);

    void deleteExpenseById(String id);

    boolean checkActiveByNickname(String nickName);

    void activeUserTele(String nickname);

    void activeUserPhone(String nickname);

    void deactivateUserTele(String nickname);

    void deactivateUserPhone(String nickname);

    boolean activePhoneNumber(String nickname, String phoneNumber);

    String getPhoneByNickname(String nickname);

    UserPhone getUserPhoneInfoByPhoneNumber(String nickname);

    UserActivePhoneResponse getAllUserActivePhone(String nickname, String phone, int pageIndex, int pageSize);

    UserActiveTeleResponse getAllUserActiveTele(String nickname, int pageIndex, int pageSize);

    String getPhoneActiveByNickname(String nickname);

    void updateStatusSendBackCodeByDay(String type, String nickname, String startTime, String endTime);

    boolean checkIsSendBackCodeByDay(String type, String nickname, String startTime, String endTime);

    boolean createEvent(EventResponse eventResponse);

    long getMoneyShootFishByNickname(String startTime, String endTime, String nickname);

    boolean checkIfHaveAnyEventActive();

    EventResponse getCurrentEvent();

    boolean checkUserNapTienEvent(String eventId, String nickname);

    void saveUserNapTienEvent(UserEvent userEvent);

    ListEventResponse getAllEvent(String timeStart, String timeEnd, String eventName, String rate, boolean status);
}

