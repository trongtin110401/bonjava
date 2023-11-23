package com.vinplay.api.dlmuaban;

import com.vinplay.usercore.service.UserExtraService;
import com.vinplay.usercore.service.impl.UserExtraServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.statics.Consts;
import com.vinplay.vbee.common.utils.VinPlayUtils;

import javax.servlet.http.HttpServletRequest;

public class BanTienChoDLProcess implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String bank_nhan = request.getParameter("bank_nhan");
        String stk_nhan = request.getParameter("stk_nhan");
        String name_nhan = request.getParameter("name_nhan");
        String money = request.getParameter("tien");
        int tien_real = Integer.parseInt(money);
        String trangthai = "Chưa Thanh Toán";
        String sttcode = "1";
        String accessToken = request.getParameter("at");
        String nickname = this.getUserNameByAccessToken(accessToken);
        String nickname_dl = request.getParameter("nickname_dl");
        String dlid_tmp = request.getParameter("dlid");
        long dlid = Long.parseLong(dlid_tmp);
        String note = request.getParameter("note");
        String timelog = VinPlayUtils.getCurrentDateTime();
        String TransID = String.valueOf(VinPlayUtils.generateTransId());
        XuLyBanTien xuly = new XuLyBanTien();
        BanTienEnity ban = new BanTienEnity(TransID, bank_nhan,stk_nhan, name_nhan, tien_real, trangthai, sttcode, nickname, nickname_dl, dlid, timelog, note);
        xuly.InsertBan(ban);

        // cộng tiền
        UserServiceImpl service = new UserServiceImpl();
        long tien_final = 0;
        long tien1 = tien_real;
        long tien = tien1 * (-1);
        try {
            double flus = 1;
            double fee = 1;
            double amount = fee * tien;
            long totalFee = Math.round(tien - amount);
            double tien_tmp = tien * flus;
            tien_final = (long) tien_tmp;
            totalFee = totalFee > 0 ? totalFee : 0;
            service.updateMoneyFromAdmin(nickname, tien_final, "vin",
                    Consts.RECHARGE_BY_BANK, "Bán Tiền",
                    "Bán Tiền Cho Đại Lý "+nickname_dl, totalFee);
        }catch (Exception e) {
            return "{\"errorcode\":\"500\",\"des\":\"That Bai, Exception "+e+"\"}";
        }

        return "{\"errorcode\":\"200\",\"des\":\"Thanh Cong\"}";
    }
    private String getUserNameByAccessToken(String accessToken){
        UserExtraService userExtraService = new UserExtraServiceImpl();
        return userExtraService.getModelFromToken(accessToken).getNickname();
    }
}
