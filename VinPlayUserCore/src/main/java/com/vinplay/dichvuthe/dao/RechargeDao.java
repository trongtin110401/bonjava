/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.vbee.common.exceptions.KeyNotFoundException
 *  com.vinplay.vbee.common.messages.dvt.RechargeByBankMessage
 *  com.vinplay.vbee.common.messages.dvt.RechargeByCardMessage
 */
package com.vinplay.dichvuthe.dao;

import com.vinplay.dichvuthe.entities.*;
import com.vinplay.dichvuthe.response.*;
import com.vinplay.iap.lib.Purchase;
import com.vinplay.usercore.response.LogRechargeBankNLResponse;
import com.vinplay.usercore.response.LogRechargeBankNapasResponse;
import com.vinplay.vbee.common.exceptions.KeyNotFoundException;
import com.vinplay.vbee.common.messages.dvt.RechargeByBankMessage;
import com.vinplay.vbee.common.messages.dvt.RechargeByCardMessage;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.vinplay.vbee.common.response.RechargeByCardReponse;
import org.bson.Document;

public interface RechargeDao {
    public List<RechargeByCardMessage> getListCardPending(String var1, String var2) throws NumberFormatException, KeyNotFoundException;

    public List<RechargeByCardMessage> getListCardPending() throws NumberFormatException, KeyNotFoundException;

    public boolean updateCard(String var1, int var2, int var3, String var4, int var5);

    public RechargeByBankMessage getRechargeByBank(String var1);

    public boolean logRechargeByBank(RechargeByBankMessage var1) throws Exception;

    public boolean updateRechargeByBank(String var1, String var2, String var3, String var4, String var5, String var6) throws Exception;

    public boolean insertLogRechargeByBankError(String var1, String var2, String var3, String var4, String var5, String var6, String var7, String var8, String var9, String var10, String var11, String var12, String var13, String var14) throws Exception;

    public boolean logRechargeByNL(String var1, String var2, String var3, String var4, String var5, int var6, int var7, int var8, int var9, String var10, String var11, String var12, String var13, String var14, String var15, String var16, String var17);

    public boolean updateRechargeByNL(String var1, String var2, String var3);

    public boolean logRechargeByNLError(String var1, String var2, String var3);

    public NganLuongModel getNLTrans(String var1);

    public LogRechargeBankNapasResponse getLogNapas(String var1, String var2, String var3, String var4, String var5, String var6, String var7, String var8, int var9);

    public LogRechargeBankNLResponse getLogNL(String var1, String var2, String var3, String var4, String var5, String var6, String var7, String var8, int var9);

    public boolean saveLogIAP(Purchase var1, String var2, int var3, int var4, String var5);

    public boolean checkOrderId(String var1);

    public long getTotalRechargeIapInday(String var1, Calendar var2) throws ParseException;

    public boolean checkRequestIdSMS(String var1);

    public boolean checkRequestIdSMSPlus(String var1);

    public boolean saveLogRechargeBySMS(String var1, String var2, String var3, int var4, String var5, String var6, String var7, int var8, String var9, int var10);

    public boolean saveLogRechargeBySMSPlus(String var1, String var2, String var3, int var4, String var5, String var6, String var7, String var8, String var9, int var10, String var11, int var12);

    public boolean saveLogRechargeBySMSPlusCheckMO(String var1, String var2, int var3, String var4, int var5, String var6);

    public LogSMS8x98Response getLogSMS8x98(String var1, String var2, int var3, String var4, String var5, int var6, String var7, String var8, int var9);

    public LogSMSPlusResponse getLogSMSPlus(String var1, String var2, int var3, String var4, int var5, String var6, String var7, int var8);

    public boolean saveLogRequestApiOTP(String var1, String var2, int var3, String var4, String var5, String var6, String var7, int var8, String var9);

    public boolean saveLogConfirmApiOTP(String var1, String var2, int var3, String var4, String var5, String var6, String var7, String var8, int var9, String var10, int var11);

    public LogApiOtpConfirmResponse getApiOtpConfirm(String var1, String var2, int var3, String var4, int var5, String var6, String var7, int var8);

    public LogApiOtpRequestResponse getApiOtpRequest(String var1, String var2, int var3, String var4, int var5, String var6, String var7, int var8);

    public RechargeByCardMessage getPendingCardByReferenceId(String var1);

    public boolean insertLogUpdateCardPending(String var1, String var2, String var3, String var4, String var5, String var6, String var7, String var8, String var9, String var10, String var11, String var12) throws Exception;

    public boolean isAgent(String var1) throws SQLException;

    public List<String> getListSmsIdNearly();

    public List<String> getListSmsPlusIdNearly();

    public LogSMSPlusCheckMoResponse getLogSMSPlusCheckMO(String var1, int var2, int var3, String var4, String var5, int var6);

    public boolean updateSMS(String var1, int var2, String var3, int var4);

    public Document getRechargeByGachthe(String transId);

    public Document getRechargeByGachthe(String nickname, String serial, String pin);

    public List<Document> getRechargeByGachtheRecently();

    public boolean saveLogRechargeByGachThe(String nickname, String serial, String pin, long amount, String requestId, String requestTime, int code, String des, long money, String provider, String platform, long currentMoney, long addMoney, int userId, String username, String partner, String client);

    public boolean UpdateGachtheTransctions(String transId, int code, String message);

    public boolean UpdateGachtheTransctionsSent(String transId);

    public Document getRechargeByNapTienGa(String transId);

    public Document getRechargeByNapTienGa(String nickname, String serial, String pin);

    public List<Document> getRechargeByNapTienGaRecently();

    public boolean saveLogRechargeByNapTienGa(String nickname, String serial, String pin, long amount, String requestId, String requestTime, int code, String des, long money, String provider, String platform, long currentMoney, long addMoney, int userId, String username, int napTienGaId);

    public boolean UpdateNapTienGaTransctions(String transId, int code, String message);

    public boolean UpdateNapTienGaTransctionsSent(String transId);

    public boolean saveZoanMomoTran(ZoanMomoReq req, int code, String nickname, long currentMoney, String username, String partner, String client);

    //Deposit bank region

    public boolean InsertDepositBankManual(DepositBankModel depositBankModel);

    public boolean UpdateDepositBankManualStatus(String transId, int status, String desc, String userApprove);

    boolean UpdateDepositBankManualStatusCallBack(String transId, int status, String desc, String userApprove, String amount);

    boolean UpdateDepositCard(String transId, int status, String desc, String userApprove, long amount);

    public DepositBankReponse GetListDepositBank(DepositBankModel depositBankModel, int page, int maxItem, String fromTime, String endTime);

    public boolean isPendingTransDepositBank(String nickname);

    public DepositBankModel FindDepositBankById(String Id);

    // deposit Momo region

    public boolean InsertDepositMomoManual(DepositMomoModel depositBankModel);

    public boolean UpdateDepositMomoManualStatus(String transId, int status, String desc, String userApprove);

    public boolean UpdateDepositMomoManualStatus2(String transId, int status, String desc, String userApprove, long amount);

    boolean UpdateDepositMomoManualStatusCallBack(String transId, int status, String desc, String userApprove, String amount);

    public boolean UpdateDepositMomoManualStatus2(long amount, String transId, int status, String desc, String userApprove);

    public DepositMomoReponse GetListDepositMomo(DepositMomoModel depositMomoModel, int page, int maxItem, String fromTime, String endTime);

    public boolean isPendingTransDepositMomo(String nickname);

    public DepositMomoModel FindDepositMomoById(String Id);

    public boolean logRechargeByCodePay(RechargeByBankMessage message);

    public String InsertDepositOnePayBankManual(DepositOnePayModel depositBankModel);

    DepositOnePayResponse GetListDepositOnePayBank(DepositOnePayModel depositBankModel, int page, int maxItem, String fromTime, String endTime);

    boolean UpdateDepositOnepayOTP(String transId, String otp, int status);

    boolean UpdateDepositOnepayManualStatus(String transId, int status, String desc, String userApprove);

    boolean isPendingTransDepositOnePayBank(String nickname);

    boolean isPendingWaitOPTDepositOnePayBank(String nickname);

    boolean isPendingGetAnOPTDepositOnePayBank(String nickname);

    boolean isPendingSentResOTPDepositOnePayBank(String nickname);

    boolean isPendingOTPTransDepositOnePayBank(String nickname, String transId);

    boolean UpdateDepositStatusOnepay(String transId, int status, int sendingSts);

    boolean UpdateDepositStatusOnepay(String transId, int status, String admin, int sendingSts);

    int isDoneTranstionOnePay(String name, String transId);

    DepositOnePayModel FindDepositOnePayById(String Id);

    boolean UpdateMaGiaoDichTechcomBankOnepay(String transId, String maGiaoDich, String admin);

    String getTransDoneTranstionOnePay(String name, int status);

    ArrayList<DepositOnePayModel> GetListDepositOnePayBank(int sendingStatus);

    boolean InsertDepositCustomMomoManual(DepositMomoModel depositBankModel, String id, String comment);

    ArrayList<DepositMomoModel> GetListDepositPendingMomo();

    // card
    boolean InsertDepositMobileCardManual(DepositMobileCardModel depositBankModel);

    boolean UpdateDepositMobileCardManualStatus(String transId, int status, String desc, String userApprove);

    DepositCardResponse GetListMobileCardManual(DepositMobileCardModel depositMomoModel, int page, int maxItem, String fromTime, String endTime);

    ArrayList<DepositMobileCardModel> GetListDepositPendingMobileCard();

    boolean isPendingTransDepositMoBileCard(String nickname);

    DepositMobileCardModel FindDepositMobileCardById(String Id);

    public RechargeByCardReponse searchRechargeByCard(String transId);

    DepositCardResponse getListDepositCardSuccess(String fromTime, String endTime);


    DepositMomoReponse getListDepositMomoSuccess(String fromTime, String endTime);

    DepositBankReponse getListDepositBankSuccess(String fromTime, String endTime);
}

