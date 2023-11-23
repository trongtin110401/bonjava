package com.vinplay.api.backend.dlmuaban;

import com.vinplay.usercore.service.UserExtraService;
import com.vinplay.usercore.service.impl.UserExtraServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.utils.VinPlayUtils;

import javax.servlet.http.HttpServletRequest;

public class YeuCauMuaTienDL implements BaseProcessor<HttpServletRequest, String> {
    public synchronized String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = (HttpServletRequest) param.get();
            String dlid = request.getParameter("dlid");
            String tien = request.getParameter("tien");
            String accessToken = request.getParameter("at");
            String nickname = this.getUserNameByAccessToken(accessToken);
            String timelog = VinPlayUtils.getCurrentDateTime();
            String TransID = String.valueOf(VinPlayUtils.generateTransId());
            String trangthai = "Chờ Duyệt";
            int sttcode = 1;
            Long dl_id = Long.parseLong(dlid);
            Long tienmua = Long.parseLong(tien);
            String des = "";
            String note = "";
            MuaBanTien muaban = new MuaBanTien();
            yeucaumuaentity yeucau = new yeucaumuaentity(TransID,dl_id,nickname,tienmua,trangthai, sttcode,des, timelog,note);
            muaban.Mua(yeucau);
            return "{\"errorcode\":\"200\",\"des\":\"Thanh Cong\"}";
        }catch (Exception e){
            return "{\"errorcode\":\"500\",\"des\":\"Exception "+e+"\"}";
        }

    }

    private String getUserNameByAccessToken(String accessToken){
        UserExtraService userExtraService = new UserExtraServiceImpl();
        return userExtraService.getModelFromToken(accessToken).getNickname();
    }

}
