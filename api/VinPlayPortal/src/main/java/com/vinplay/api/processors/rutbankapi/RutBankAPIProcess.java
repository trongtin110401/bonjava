package com.vinplay.api.processors.rutbankapi;

import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.payment.entities.UserWithdraw;
import com.vinplay.usercore.service.UserExtraService;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserExtraServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.utils.TelegramUtil;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;

public class RutBankAPIProcess implements BaseProcessor<HttpServletRequest, String> {
    private UserService userService = new UserServiceImpl();

    public synchronized String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = param.get();
            String accessToken = request.getParameter("at");
            String nickname = this.getUserNameByAccessToken(accessToken);
            String amount = request.getParameter("amount");
            String bankname = request.getParameter("bankname");
            String banknum = request.getParameter("banknum");
            String bankacc = request.getParameter("bankacc");
            bankacc = bankacc.replaceAll("_", " ");
            CheckNap checknap = new CheckNap();
            naptmp ntmp = checknap.tongnapThe(nickname);
            long tiennap = ntmp.getTongnap();
            long sodu = 0;
            int yeu_cau_rut_1 = Integer.parseInt(amount);
            long yeu_cau_rut = Long.parseLong(amount);
            long tienrut = checknap.tongrut(nickname);
            long tongx = ntmp.getNapbank() + ntmp.getNapmomo();
            if (tiennap >= 0) {
                long taixi = 0;
                UserWithdraw userWithdraw = new UserWithdraw(nickname, yeu_cau_rut_1, banknum, bankacc, bankname);
                this.userService.UpdateMoneyWhenWithdrawBank(userWithdraw);
                sodu = this.userService.getCurrentMoneyUserCache(nickname, "vin");
                BroadCastUserMoney.pushBroadCast(nickname);
                long tienthe = ntmp.getNapthe();
                long xinloc = checknap.xinloc(nickname);
                long tongadmin = ntmp.getNapadmin();
                checknap.Notify(nickname, tiennap, yeu_cau_rut, tienrut, tienthe, xinloc, tongadmin, sodu, tongx, taixi);
                sendMessage("yeu cau rut: \nbankname" + bankname + "\n banknum:: " + banknum + "\nsotien: " + amount);
                return "{\"error\":200,\"currentMoney\":" + sodu + "}";
            } else {
                return "{\"error\":300}";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    private String getUserNameByAccessToken(String accessToken) {
        UserExtraService userExtraService = new UserExtraServiceImpl();
        return userExtraService.getModelFromToken(accessToken).getNickname();
    }

    public void sendMessage(String message) {
        try {
            String messageEncode = URLEncoder.encode(message);
            TelegramUtil telegramUtil = new TelegramUtil();
            telegramUtil.sendMessageNapRut(messageEncode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
