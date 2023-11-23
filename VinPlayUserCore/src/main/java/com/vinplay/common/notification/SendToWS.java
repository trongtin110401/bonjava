package com.vinplay.common.notification;

import com.vinplay.common.report.EventactionAdminObj;
import com.vinplay.common.report.RechargebyonepayotpAdminObj;
import com.vinplay.dichvuthe.entities.DepositBankModel;
import com.vinplay.dichvuthe.entities.DepositMobileCardModel;
import com.vinplay.dichvuthe.entities.DepositMomoModel;
import com.vinplay.dichvuthe.entities.DepositOnePayModel;
import com.vinplay.payment.entities.UserWithDrawCard;
import com.vinplay.payment.entities.UserWithdraw;
import com.vinplay.usercore.service.CacheService;
import com.vinplay.usercore.service.impl.CacheServiceImpl;

import java.io.IOException;

public class SendToWS {
    public static void sendBEExcNotification(NotificationAdminObj obj) throws IOException {
        CacheService cacheService = new CacheServiceImpl();
        cacheService.setValue("notify_admin", obj.toJson());
    }

    public static void sendBEExcCashoutbybank(UserWithdraw obj) throws IOException {
        CacheService cacheService = new CacheServiceImpl();
        cacheService.setValue("cashoutbybank_admin", obj.toJson());
    }

    public static void sendBEExcCashoutbycardmanual(UserWithDrawCard obj) throws IOException {
        CacheService cacheService = new CacheServiceImpl();
        cacheService.setValue("cashoutbycardmanual_admin", obj.toJson());
    }

    public static void sendBEExcRechargebybank(DepositBankModel obj) throws IOException {
        CacheService cacheService = new CacheServiceImpl();
        cacheService.setValue("rechargebybank_admin", obj.toJson());
    }

    public static void sendBEExcRechargebyMomosunvin(DepositBankModel obj) throws IOException {
        CacheService cacheService = new CacheServiceImpl();
        cacheService.setValue("rechargebymomosunvin_admin", obj.toJson());
    }

    public static void sendBEExcRechargebyonepay(DepositOnePayModel obj) throws IOException {
        CacheService cacheService = new CacheServiceImpl();
        cacheService.setValue("rechargebyonepay_admin", obj.toJson());
    }

    public static void sendBEExcRechargebymomo(DepositMomoModel obj) throws IOException {
        CacheService cacheService = new CacheServiceImpl();
        cacheService.setValue("rechargebymomo_admin", obj.toJson());
    }

    public static void sendBEExcRechargebyonepayotp(RechargebyonepayotpAdminObj obj) throws IOException {
        CacheService cacheService = new CacheServiceImpl();
        cacheService.setValue("rechargebyonepayotp_admin", obj.toJson());
    }

    public static void sendBEExcRechargebyautocard(DepositMobileCardModel obj) throws IOException {
        CacheService cacheService = new CacheServiceImpl();
        cacheService.setValue("rechargebyautocard_admin", obj.toJson());
    }

    public static void sendBEExcEventaction(EventactionAdminObj obj) throws IOException {
        CacheService cacheService = new CacheServiceImpl();
        cacheService.setValue("eventaction_admin", obj.toJson());
    }
}
