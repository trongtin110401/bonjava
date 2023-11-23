package com.vinplay.api.processors;

import com.vinplay.api.entities.RechargeBankResponse;
import com.vinplay.api.entities.RechargeMomoResponse;
import com.vinplay.usercore.dao.GiftCodeDAO;
import com.vinplay.usercore.dao.impl.GiftCodeDAOImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.response.UserInfoModel;
import org.apache.log4j.Logger;
import scala.Int;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class GetSpecialGiftCodeProcessor
        implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String)"api");

    public String execute(Param<HttpServletRequest> param) {
        try
        {
            HttpServletRequest request = (HttpServletRequest)param.get();
            String type = request.getParameter("t");
            String mobile = request.getParameter("m");
            UserServiceImpl userService = new UserServiceImpl();
            List<UserInfoModel> users = userService.checkPhoneByUser(mobile);
            if (users != null && users.size() == 1)
            {
                UserModel userModel = userService.getUserByNickName(users.get(0).nickName);
                if (userModel != null && userModel.getMobile().equals(mobile))
                {
                    GiftCodeDAO dao = new GiftCodeDAOImpl();
                    return dao.GetGiftCodeByTypeNN(Integer.parseInt(type),users.get(0).nickName);
                }
                else
                {
                    return "invalid mobile";
                }
            }
            else
            {
                return "invalid mobile";
            }
        }
        catch (Exception ex)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String sStackTrace = sw.toString(); // stack trace as a string
            return ex.getMessage() + "\n" + sStackTrace;
        }
    }
}
