package com.vinplay.utils;

import bitzero.server.entities.User;
import bitzero.server.extensions.data.DataCmd;
import com.vinplay.dichvuthe.entities.DepositBankModel;
import com.vinplay.dichvuthe.entities.DepositMomoModel;
import com.vinplay.payment.entities.UserWithdraw;
import com.vinplay.payment.entities.UserWithdrawMomo;
import com.vinplay.vbee.common.dto.UseGiftCodeDto;

import java.text.NumberFormat;
import java.util.Locale;

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

    public static boolean SendMessageTX(String message) {
        try {
            TelegramUtil telegramUtil = new TelegramUtil();
            telegramUtil.sendMessageBetTX(message);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean SendMessageTXMD5(String message) {
        try {
            TelegramUtil telegramUtil = new TelegramUtil();
            telegramUtil.sendMessageBetTXMD5(message);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean SendMessageCashout(UserWithdraw userWithdraw) {
        try {
            String message = "<b>YRút tiền bank từ User " + userWithdraw.Username + "</b>";
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
            String message = "<b>Tài Khoản  " + model.Nickname + "</b> Nạp Bank số tiền : " + model.Amount;
            return SendMessageNap(message);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean SendMessageCashoutMomo(UserWithdrawMomo userWithdraw) {
        try {
            String message = "<b>Rút tiền Momo từ User " + userWithdraw.Nickname + "</b>";
            message += "\n Số tiền <b>" + userWithdraw.Amount + "</b>";
            message += "\n Số điện thoại nhận : <b>" + userWithdraw.PhoneNumber + "</b>";
            return SendMessageRut(message);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean SendMessageDepositMomo(DepositMomoModel model) {
        try {
            String message = "<b>Tài Khoản  " + model.Nickname + "</b> Nạp Momo số tiền : " + model.Amount;
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

    public static boolean SendMessageBetTX(String nickname, long money, short betSize, long referenceId, int userId) {
        try {
            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));
            String formattedNumber = numberFormat.format(money);
            String message = "Phiên : <b> " + referenceId + "</b>";
            message += "\nTài Khoản : <b> " + nickname + "</b> / <b>" + userId + "</b>";
            if (betSize == 0) {
                message += "\nCửa Đặt : <b> Xỉu </b>";
            } else {
                message += "\nCửa Đặt : <b> Tài </b>";
            }
            message += "\nSố Tiền Đặt : <b>" + formattedNumber + "</b>";
            return SendMessageTX(message);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean SendMessageBetTXMD5(String nickname, long money, short betSize, long referenceId, int userId) {
        try {
            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));
            String formattedNumber = numberFormat.format(money);
            String message = "Phiên : <b> " + referenceId + "</b>";
            message += "\nTài Khoản : <b> " + nickname + "</b> / <b>" + userId + "</b>";
            if (betSize == 0) {
                message += "\nCửa Đặt : <b> Xỉu </b>";
            } else {
                message += "\nCửa Đặt : <b> Tài </b>";
            }
            message += "\nSố Tiền Đặt : <b>" + formattedNumber + "</b>";
            return SendMessageTXMD5(message);
        } catch (Exception e) {
            return false;
        }
    }
}
