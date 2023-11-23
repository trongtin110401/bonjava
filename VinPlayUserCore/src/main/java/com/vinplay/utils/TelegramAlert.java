package com.vinplay.utils;

import com.vinplay.dichvuthe.entities.DepositBankModel;
import com.vinplay.dichvuthe.entities.DepositMomoModel;
import com.vinplay.payment.entities.UserWithdraw;
import com.vinplay.payment.entities.UserWithdrawMomo;
import com.vinplay.usercore.utils.GameCommon;

public class TelegramAlert {
    public static boolean SendMessage(String message){
        try{
            String chatId = GameCommon.getValueStr("Telegram_chat_id");
            String bootToken = GameCommon.getValueStr("Telegram_boot_token");
            return TelegramUtil.sendMessage(message, chatId, bootToken);
        }catch (Exception e){
            return false;
        }

    }
    public static boolean SendMessageCashout(UserWithdraw userWithdraw){
        try{
            String message = "<b>Yêu cầu rút tiền từ User " + userWithdraw.Username + "</b>";
            message += "\n Số tiền <b>"+ userWithdraw.Amount+ "</b>";
            message += "\n Ngân hàng: <b>"+ userWithdraw.BankName+ "</b>";
            message += "\nTên tài khoản <b>"+ userWithdraw.BankAccountName+ "</b>";
            message += "\n Số tài khoản: <b>"+ userWithdraw.BankAccountNumber+ "</b>";
            return SendMessage(message);
        }catch (Exception e){
            return false;
        }
    }
    public static boolean SendMessageDepositBank(DepositBankModel model){
        try{
            String message = "<b>Yêu cầu nạp tiền qua bank  từ User " + model.Nickname+ "</b>";
            message += "\n Số tiền <b>"+ model.Amount+ "</b>";
            message += "\n Ngân hàng: <b>"+ model.BankBrandName+ "</b>";
            message += "\n Tên tài khoản <b>"+ model.BankAccountName+ "</b>";
            message += "\n Số tài khoản: <b>"+ model.BankAccountNumber+ "</b>";
            return SendMessage(message);
        }catch (Exception e){
            return false;
        }
    }
    public static boolean SendMessageCashoutMomo(UserWithdrawMomo userWithdraw){
        try{
            String message = "<b>Yêu cầu rút tiền Momo từ User " + userWithdraw.Nickname + "</b>";
            message += "\n Số tiền <b>"+ userWithdraw.Amount+ "</b>";
            message += "\n Số điện thoại nhận : <b>"+ userWithdraw.PhoneNumber+ "</b>";
            return SendMessage(message);
        }catch (Exception e){
            return false;
        }
    }

    public static boolean  SendMessageDepositMomo(DepositMomoModel model){
        try{
            String message = "Yêu cầu nạp tiền qua momo  từ User <b>" + model.Nickname+ "</b>";
            message += "\n Số tiền <b>"+ model.Amount+ "</b>";
            message += "\n Số điện thoại: <b>"+ model.ReceivedPhoneNumber+ "</b>";

            return SendMessage(message);
        }catch (Exception e){
            return false;
        }
    }
}
