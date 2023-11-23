/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.vbee.common.messages.dvt.CashoutByBankMessage
 */
package com.vinplay.dichvuthe.dao;

import com.vinplay.dichvuthe.entities.*;
import com.vinplay.dichvuthe.response.CashoutTransResponse;
import com.vinplay.dichvuthe.response.CashoutUserDailyResponse;
import com.vinplay.payment.entities.UserWithDrawCard;
import com.vinplay.payment.entities.UserWithdraw;
import com.vinplay.payment.entities.UserWithdrawMomo;
import com.vinplay.vbee.common.messages.dvt.CashoutByBankMessage;
import com.vinplay.vbee.common.messages.dvt.CashoutByCardMessage;

import java.sql.SQLException;
import java.util.List;

public interface CashoutDao {
    public long getSystemCashout() throws SQLException;

    public boolean updateSystemCashout(long var1) throws SQLException;

    public void logCashoutByBank(CashoutByBankMessage var1) throws Exception;

    public BankAccountInfo getBankAccountInfo(String var1) throws SQLException;

    public CashoutUserDailyResponse getCashoutUserToday(String var1);

    public List<CashoutTransResponse> getListCashoutByCardPending() throws Exception;

    public void updateCashOutByCard(String var1, String var2, int var3, String var4, int var5, int var6) throws Exception;

    public void insertCardIntoDB(String var1, int var2, String var3, String var4, String var5) throws Exception;

    public List<CashoutTransResponse> getListCashoutByCardPending(String var1, String var2, String var3) throws Exception;

    public boolean InsertCashoutByBankManual(UserWithdraw userWithdraw);
    public boolean InsertCashoutByMomoManual(UserWithdrawMomo userWithdrawMomo);
    public boolean InsertCashoutByCardManual(UserWithDrawCard userWithDrawCard);
    public CashoutBankResponse GetListCashoutBank(UserWithdraw userWithdraw, int page, int maxItem, String fromTime, String endTime);
    public CashoutBankResponse GetListCashoutBankExportFile(UserWithdraw userWithdraw, String fromTime, String endTime);
    public CashoutCardResponse GetListCashoutCard(UserWithDrawCard userWithDrawCard, int page, int maxItem, String fromTime, String endTime);
    public CashoutMomoResponse GetListCashoutMomo(UserWithdrawMomo userWithdrawMomo, int page, int maxItem, String fromTime, String endTime);
    public boolean UpdateCashoutBank(String Id, String status, String userApprove);
    public boolean UpdateCashoutMomo(String Id, String status, String userApprove);
    public boolean UpdateCashoutCard(String Id, String status, String userApprove , String Seri , String Pin);
    public String getCashoutBankStatus(String Id);
    public String getCashoutMomoStatus(String Id);
    public UserWithDrawCard FindCashoutCardById(String Id);
    public UserWithdraw FindCashoutBankById(String Id);
    public UserWithdrawMomo FindCashoutMomoById(String Id);
    public CashoutByCardMessage FindLogByTimeAndNickname(String time, String nickName);


}

