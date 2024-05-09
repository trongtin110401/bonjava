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
    UserLoseByDayResponse getListUserTeleCashBack(int pageIndex, int pageSize, String timeStart, String timeEnd, String nickname, String code);

    MoneyShootFishResponse getMoneyShootFish(String startTime, String endTime);

    long getTotalShootFishByNickname(String startTime, String endTime, String nickname);

    void deleteExpenseById(String id);

    boolean checkActiveByNickname(String nickName);

    void activeUserTele(String nickname);

    void activeUserPhone(String nickname);

    void deactivateUserTele(String nickname);

    boolean activePhoneNumber(String nickname, String phoneNumber);

    String getPhoneByNickname(String nickname);

    UserPhone getUserPhoneInfoByPhoneNumber(String nickname);

    UserActivePhoneResponse getAllUserActivePhone(String nickname,String phone, int pageIndex, int pageSize);
}

