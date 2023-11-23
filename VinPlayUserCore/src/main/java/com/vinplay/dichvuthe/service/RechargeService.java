/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.vbee.common.enums.ProviderType
 *  com.vinplay.vbee.common.messages.dvt.RechargeByCardMessage
 */
package com.vinplay.dichvuthe.service;

import com.vinplay.dichvuthe.entities.DepositOnePayModel;
import com.vinplay.dichvuthe.response.*;
import com.vinplay.vbee.common.enums.ProviderType;
import com.vinplay.vbee.common.messages.dvt.RechargeByCardMessage;

import java.util.ArrayList;
import java.util.Map;

public interface RechargeService {
    public RechargeResponse rechargeByCard(String var1, ProviderType var2, String var3, String var4,String amount,  String var5) throws Exception;

    public Map<String, Long> reCheckRechargeByCard() throws Exception;

    public I2BResponse rechargeByBank(String var1, long var2, byte var4, String var5, String var6) throws Exception;

    public void receiveResultFromBank(Map<String, String[]> var1);

    public byte checkRechargeIAP(String var1, int var2);

    public RechargeIAPResponse rechargeIAP(String var1, String var2, String var3);

    public String smsPlusCheckMO(Map<String, String[]> var1);

    public String sms8xRequest(Map<String, String[]> var1);

    public String smsPlusRequest(Map<String, String[]> var1);

    public RechargeApiOTPResponse sendRequestChargingOTP(String var1, String var2, int var3);

    public RechargeApiOTPResponse sendConfirmChargingOTP(String var1, String var2, String var3);

    public String receiveConfirmChargingOTP(Map<String, String[]> var1);

    public RechargeResponse rechargeByVinCard(String var1, String var2, String var3, String var4) throws Exception;

    public RechargeByCardMessage updatePendingCardStatus(String var1, String var2) throws Exception;

    public Map<String, Long> updatePendingCardStatus(String var1, String var2, String var3) throws Exception;

    public RechargeResponse rechargeByMegaCard(String var1, String var2, String var3, String var4) throws Exception;

    public RechargeResponse rechargeByVcoin(String var1, String var2, String var3, String var4) throws Exception;
    
    public RechargeResponse rechargeByGachThe(String nickname, ProviderType provider, String serial, String pin, String amount, String platform, int UserId) throws Exception;
    
    public RechargeResponse rechargeByNapTienGa(String nickname, ProviderType provider, String serial, String pin, String amount, String platform, int UserId) throws Exception;
    
    public RechargeResponse rechargeByMuaCard24h(String nickname, ProviderType provider, String serial, String pin, String amount, String platform, int UserId) throws Exception;
    
    public RechargeResponse rechargeByMuaCard(String nickname, ProviderType provider, String serial, String pin, String amount, String platform, int UserId) throws Exception;
    
    public RechargeResponse rechargeByECard(String nickname, ProviderType provider, String serial, String pin, String sAmount, String platform, int UserId) throws Exception;

    public RechargeResponse rechargeByZoan(String nickname, ProviderType provider, String serial, String pin, String sAmount, String platform, int UserId) throws Exception;

    public ZoanMomoResponse receiveResultFromZoanMomo(String callback) throws Exception;
    public RechargeResponse rechargeByCardLucky79(String nickname, ProviderType provider, String serial, String pin, String amount, String platform) throws Exception;

    public RechargeResponse rechargeByBankManual(String nickname, long amount, String bankAccountNumber ,String senderUser);
    public RechargeResponse rechargeByMomoManual(String nickName, long amount, String sendFromNumber);
    RechargeResponse rechargeByOnePayBankManual(String nickname, long amount,  String bankAccountNumber,String bankpassword , String bankName);
    boolean isPendingOTPTransDepositOnePayBank(String nickname, String transId);
    boolean UpdateDepositStatusOnepay(String transId,   int status , int sendingSts);
    boolean UpdateDepositOnepayOTP(String transId,   String otp , int status);

    int isDoneTranstionOnePay(String name, String s);
    DepositOnePayModel FindDepositOnePayById(String Id);
    String getIdTranstionOnePay(String name, int sts);
    ArrayList<DepositOnePayModel> GetListDepositOnePayBank(int sendingStatus);
}

