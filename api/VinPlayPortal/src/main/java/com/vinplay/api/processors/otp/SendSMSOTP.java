package com.vinplay.api.processors.otp;

import com.vinplay.api.entities.UserOTP;
import com.vinplay.api.otp.GenOTP;
import com.vinplay.api.otp.OTPELK;
import com.vinplay.api.otp.OTPprocess;
import com.vinplay.api.otp.SendOTP;
import com.vinplay.usercore.service.UserExtraService;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserExtraServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.statics.TransType;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SendSMSOTP implements BaseProcessor<HttpServletRequest, String> {
    public synchronized String execute(Param<HttpServletRequest> param) {
//        try {
            HttpServletRequest request = (HttpServletRequest)param.get();
            String nicknamex = request.getParameter("nickname");
            String username = request.getParameter("username");
            String phone = request.getParameter("phone");
            String accessToken = request.getParameter("at");
            return "500";
//            String nickname = this.getUserNameByAccessToken(accessToken);
//            GenOTP gen = new GenOTP();
//            OTPprocess opt_pro = new OTPprocess();
//            OTPELK oelk = new OTPELK();
//            String otp = gen.GenOTP();
//            SendOTP sendsms = new SendOTP();
//
//            UserOTP uotpx = opt_pro.GetActive(nickname);
//            boolean checkPhone = opt_pro.CheckPhoneExit(phone);
//            Long create_time_2 = new Date().getTime();
//
//            UserService userService = new UserServiceImpl();
//            long vin = userService.getCurrentMoneyUserCache(nickname, "vin");
//            if(vin < 0){
//                return "444";
//            }else{
//                int active = 0;
//                Long active_time = 0l;
//                int turn = 0;
//                if(uotpx == null && checkPhone == false){
//                    Date d = new Date(create_time_2);
//                    SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//                    String strDate = sdfDate.format(d);
//                    UserOTP uotp = new UserOTP(nickname, username,phone, otp, active, create_time_2, active_time, turn, strDate);
//                    opt_pro.InsertActive(uotp);
//                    oelk.InsertActiveELK(uotp);
//                    sendsms.sendSMSOTP_SMS(phone, otp);
//                    userService.updateMoney(nickname, -1000, "vin", "OTP", "PhiKichHoatOTP", "M\u00e3: ", 0L, null, TransType.NO_VIPPOINT);
//                    return "1";
//                }else if(uotpx != null && phone.equalsIgnoreCase(uotpx.getPhone()) == true){
//                    boolean check_Getotp = opt_pro.CheckTimeGetOTP(uotpx.getCreat_time(), create_time_2);
//                    if(check_Getotp == true){
//                        Date d = new Date(create_time_2);
//                        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//                        String strDate = sdfDate.format(d);
//                        UserOTP uotp = new UserOTP(uotpx.getNickname(), uotpx.getUsername(),uotpx.getPhone(), otp, active, create_time_2, active_time, turn, strDate);
//                        opt_pro.UpdateActive(uotp);
//                        String id_elk = oelk.GetIDActivebyNickname(uotp.getNickname());
//                        oelk.UpdateActiveELK(uotp,id_elk);
//                        sendsms.sendSMSOTP_SMS(uotpx.getPhone(), otp);
//                        userService.updateMoney(nickname, -1000, "vin", "OTP", "PhiKichHoatOTP", "M\u00e3: ", 0L, null, TransType.NO_VIPPOINT);
//                        return "1";
//                    }else{
//                        return "500";
//                    }
//                }else{
//                    return "0";
//                }
//            }


//            if(checkPhone==false){
//                if(uotpx == null){
//                    UserOTP uotp = new UserOTP(nickname, username,phone, otp, active, create_time_2, active_time, turn);
//                    opt_pro.InsertActive(uotp);
//                    sendsms.sendSMSOTP(phone, otp);
//                    return "1";
//                }else{
//                    boolean check_Getotp = opt_pro.CheckTimeGetOTP(uotpx.getCreat_time(), create_time_2);
//                    if(check_Getotp == true){
//                        UserOTP uotp = new UserOTP(nickname, username,phone, otp, active, create_time_2, active_time, turn);
//                        opt_pro.UpdateActive(uotp);
//                        sendsms.sendSMSOTP(phone, otp);
//                        return "1";
//                    }else{
//                        return "500";
//                    }
//
//                }
//            }else{
//                return "0";
//            }



//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "0";
    }
    private String getUserNameByAccessToken(String accessToken){
        UserExtraService userExtraService = new UserExtraServiceImpl();
        return userExtraService.getModelFromToken(accessToken).getNickname();
    }
}
