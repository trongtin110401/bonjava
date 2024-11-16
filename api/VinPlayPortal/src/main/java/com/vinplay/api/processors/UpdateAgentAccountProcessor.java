package com.vinplay.api.processors;

import com.vinplay.usercore.dao.impl.UserDaoImpl;
import com.vinplay.usercore.service.OtherService;
import com.vinplay.usercore.service.impl.OtherServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

public class UpdateAgentAccountProcessor
        implements BaseProcessor<HttpServletRequest, String> {

    public String execute(Param<HttpServletRequest> param) {
        try {
            OtherService service = new OtherServiceImpl();
            HttpServletRequest request = param.get();
            String nickname = request.getParameter("nickname");
            String mode = request.getParameter("mode");


            UserDaoImpl userDao = new UserDaoImpl();
            boolean b = userDao.updateAgentAccount(nickname, mode);
            if (b) {
                return "OK";
            } else {
                return "LỖI RỒI";
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}

