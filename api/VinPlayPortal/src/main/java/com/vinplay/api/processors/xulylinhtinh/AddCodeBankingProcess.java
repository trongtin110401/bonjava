package com.vinplay.api.processors.xulylinhtinh;

import com.vinplay.api.processors.cashout.GenCommentBank;
import com.vinplay.api.processors.momo.ELKAutoBankNew;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class AddCodeBankingProcess implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = (HttpServletRequest) param.get();
            String numx = request.getParameter("num");
            int numy = Integer.parseInt(numx);
            int numz = (numy-1)*300;
            GetUsers getu = new GetUsers();
            ELKAutoBankNew elk = new ELKAutoBankNew();
            GenCommentBank gen = new GenCommentBank();
            ArrayList<String> listNick = getu.getNicknameOK(numz);
            for(String nn : listNick){
                gen.GenContent2(nn);
            }
            return listNick.toString();
        }catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }
}
