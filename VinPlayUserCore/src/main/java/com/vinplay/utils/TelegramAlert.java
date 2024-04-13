package com.vinplay.utils;

import com.vinplay.dichvuthe.entities.DepositBankModel;
import com.vinplay.dichvuthe.entities.DepositMomoModel;
import com.vinplay.payment.entities.UserWithdraw;
import com.vinplay.payment.entities.UserWithdrawMomo;
import com.vinplay.vbee.common.dto.UseGiftCodeDto;

public class TelegramAlert {
    public static boolean SendMessageNap(String message) {
        try {
            TelegramUtil telegramUtil = new TelegramUtil();
            telegramUtil.sendMessageNap(message);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean SendMessageRut(String message) {
        try {
            TelegramUtil telegramUtil = new TelegramUtil();
            telegramUtil.sendMessageRut(message);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean SendMessageCashout(UserWithdraw userWithdraw) {
        try {
            String message = "<b>Yêu cầu rút tiền từ User " + userWithdraw.Username + "</b>";
            message += "\n Số tiền <b>" + userWithdraw.Amount + "</b>";
            message += "\n Ngân hàng: <b>" + userWithdraw.BankName + "</b>";
            message += "\nTên tài khoản <b>" + userWithdraw.BankAccountName + "</b>";
            message += "\n Số tài khoản: <b>" + userWithdraw.BankAccountNumber + "</b>";
            return SendMessageRut(message);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean SendMessageDepositBank(DepositBankModel model) {
        try {
            String message = "<b>Yêu cầu nạp tiền qua bank  từ User " + model.Nickname + "</b>";
            message += "\n Số tiền <b>" + model.Amount + "</b>";
            message += "\n Ngân hàng: <b>" + model.getSubType() + "</b>";
            message += "\n Tên tài khoản <b>" + model.BankAccountName + "</b>";
            message += "\n Số tài khoản: <b>" + model.BankAccountNumber + "</b>";
            return SendMessageNap(message);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean SendMessageCashoutMomo(UserWithdrawMomo userWithdraw) {
        try {
            String message = "<b>Yêu cầu rút tiền Momo từ User " + userWithdraw.Nickname + "</b>";
            message += "\n Số tiền <b>" + userWithdraw.Amount + "</b>";
            message += "\n Số điện thoại nhận : <b>" + userWithdraw.PhoneNumber + "</b>";
            return SendMessageRut(message);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean SendMessageDepositMomo(DepositMomoModel model) {
        try {
            String message = "Yêu cầu nạp tiền qua momo  từ User <b>" + model.Nickname + "</b>";
            message += "\n Số tiền <b>" + model.Amount + "</b>";
            message += "\n Số điện thoại: <b>" + model.ReceivedPhoneNumber + "</b>";

            return SendMessageNap(message);
        } catch (Exception e) {
            return false;
        }
    }


    public static boolean SendMessageDepositGiftCode(UseGiftCodeDto userGiftCode) {
        try {
            String message = "Thực hiện nạp tiền qua giftcode  từ User <b>" + userGiftCode.getNickname() + "</b>";
            message += "\n Số tiền <b>" + userGiftCode.getPrice() + "</b>";
            message += "\n mã code: <b>" + userGiftCode.getCode() + "</b>";

            return SendMessageNap(message);
        } catch (Exception e) {
            return false;
        }
    }
    public static boolean SendMessageDepositMomo(DepositBankModel model) {
        try {
            String message = "Yêu cầu nạp tiền qua momo  từ User <b>" + model.Nickname + "</b>";
            message += "\n Số tiền <b>" + model.Amount + "</b>";
            message += "\n Số điện thoại: <b>" + model.getBankAccountNumber() + "</b>";

            return SendMessageNap(message);
        } catch (Exception e) {
            return false;
        }
    }
}
