/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.usercore.service.impl.OtpServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.log4j.Logger
 */
package com.vinplay.api.processors.otp;

import com.vinplay.usercore.service.impl.OtpServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.UserInfoModel;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

public class GetOtpByPhoneAgentProcessor
implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"api");

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String phone = request.getParameter("p");
        String hash = request.getParameter("h"); 
        String type = request.getParameter("t"); 
        if (phone != null && hash != null) {
            try {           
                String phoneHash = phone + "game000#98@88";
                phoneHash = VinPlayUtils.getMD5Hash(phoneHash).toLowerCase();
                if (hash.equals(phoneHash))
                {
                    UserServiceImpl userService = new UserServiceImpl();      
                    List<UserInfoModel> users = userService.checkPhoneByUser(phone);
                    if (users != null && users.size() == 1)
                    {
                        UserInfoModel user = users.get(0);
                        OtpServiceImpl service = new OtpServiceImpl();   
                        if (user.dai_ly == 1 || user.dai_ly == 2)
                        {
                            if ("1".equals(type))
                            {
                                String otp = service.GenerateOdp(user.nickName, phone);                        
                                return otp;
                            }
                            else if ("2".equals(type))
                            {
                                String otp = service.GenerateOTP(user.nickName, phone);                        
                                return otp;
                            }
                            else
                            {
                                return "";
                            }
                        }
                        else
                        {
                            return "";
                        }
                    }
                }
            }
            catch (Exception e) {
                logger.debug((Object)e);
            }
        }
        return "";
    }
}

