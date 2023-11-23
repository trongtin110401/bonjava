package com.vinplay.api.processors.otp;

import com.vinplay.api.entities.UserOTP;
import com.vinplay.api.otp.OTPprocess;
import com.vinplay.lognaprut.HistoryTransConst;
import com.vinplay.lognaprut.HistoryTransDao;
import com.vinplay.lognaprut.entities.HistoryTransModel;
import com.vinplay.lognaprut.impl.HistoryTransDaoImpl;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.utils.VinPlayUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActiveAuthernTele implements BaseProcessor<HttpServletRequest, String> {
    public synchronized String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = (HttpServletRequest) param.get();
            String token = request.getParameter("t");
            String phone = request.getParameter("p");
            String pass = request.getParameter("z");
            phone = phone.replace(" ", "");
            phone = phone.replace("+", "");
//            https://bibabibo.xyz/api?c=4096&t=49e40d0bacad908934b5404898888ae0-sdfsdf32&p=639286987439&z=KhoaiToVcl&u=5114128124

            OTPprocess otp_pro = new OTPprocess();
            //chat id
            String userId = request.getParameter("u");
            Date d = new Date();
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strDate = sdfDate.format(d);
            String accessToken = token.split("-")[0];
            String nickname = token.split("-")[1];

            if (!pass.equalsIgnoreCase("KhoaiToVcl")) {
                //sai pass ne
                return "2";
            }

            //kiem tra phone
            if (otp_pro.CheckPhoneExit(phone)) {
                //ton tai roi
                return "1";
            }
            if (otp_pro.getUserOtpFind(nickname) != null) {
                //user da duoc kich hoat
                return "3";
            }
            UserService userService = new UserServiceImpl();

            if (!userService.checkAccesstoken(nickname, accessToken)) {
                return "2";
            }
            UserModel userModel = userService.getUserByNickName(nickname);
            String username = userModel.getUsername();

            //gio la luc active user

            UserOTP uotp = new UserOTP(nickname, username, phone, "123456", 1, d.getTime(), d.getTime(), 1, strDate);
            otp_pro.InsertActive(uotp);
            //active vao bang user
            otp_pro.UpdateSDTOTP(uotp.getNickname(), uotp.getPhone());
//            userService.updateMoneyFromAdmin(nickname, (long) 20000, "vin",
//                    "OTP", "Thưởng active otp",
//                    "Thưởng active otp", 0);
//            try {
//                HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
//                historyTransDao.insertTransaction(new HistoryTransModel("TELEGRAM" + "|" + "ACTIVE", HistoryTransConst.ONE_PAY, "Nạp tiền", "20000", "Thành công", "Active telegram", userModel.getNickname(), HistoryTransConst.ONE_PAY, VinPlayUtils.generateTransId()+""));
//            }catch (Exception e) {
//            }
            //done active
            return "0";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
