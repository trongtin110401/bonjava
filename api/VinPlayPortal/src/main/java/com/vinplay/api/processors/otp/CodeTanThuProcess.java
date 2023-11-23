package com.vinplay.api.processors.otp;

import com.vinplay.api.entities.CodeNewBie;
import com.vinplay.api.otp.CodeTanThu;
import com.vinplay.api.otp.OTPprocess;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

public class CodeTanThuProcess implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = (HttpServletRequest)param.get();
            String nickname = request.getParameter("nickname");
            String giftcode = request.getParameter("code");
            CodeTanThu codett = new CodeTanThu();
            OTPprocess otp_pro = new OTPprocess();

            String code_vidu = "abcd";
            boolean check_code_chinhxac = codett.checkCodeChinhxac(giftcode, code_vidu);

            boolean check_active = otp_pro.checkUserActiveOTP(nickname);
            boolean check_use = codett.checkUse(nickname);
            if(check_use == false && check_active == true && check_code_chinhxac == true){
                CodeNewBie codenew = new CodeNewBie();
                codett.InsertCode(codenew);
                return "1";
            }else{
                return "0";
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }
}
