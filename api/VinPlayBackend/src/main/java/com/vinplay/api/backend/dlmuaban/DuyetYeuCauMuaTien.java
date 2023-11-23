package com.vinplay.api.backend.dlmuaban;

import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.statics.Consts;

import javax.servlet.http.HttpServletRequest;

public class DuyetYeuCauMuaTien implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String trainid = request.getParameter("trainid");
        long tien_final = 0;
        MuaBanTien muaban = new MuaBanTien();
        muaban.DuyetMua(trainid);
        // cộng tiền
        UserServiceImpl service = new UserServiceImpl();
        yeucaumuaentity trans = muaban.FindDonMuaTien(trainid);
        long tien = trans.getTien();
        try {
            double flus = GameCommon.getValueDouble("RATIO_RECHARGE_BANK_TL");
            double fee = GameCommon.getValueDouble("RATIO_RECHARGE_MOMO");
            double amount = fee * tien;
            long totalFee = Math.round(tien - amount);
            double tien_tmp = tien * flus;
            tien_final = (long) tien_tmp;
            totalFee = totalFee > 0 ? totalFee : 0;
            service.updateMoneyFromAdmin(trans.getNickname(), tien_final, "vin",
                    Consts.RECHARGE_BY_BANK, "Mua Tiền",
                    "Duyệt đơn mua tiền", totalFee);
        }catch (Exception e) {
            return "{\"errorcode\":\"500\",\"des\":\"That Bai, Exception "+e+"\"}";
        }
        return "{\"errorcode\":\"200\",\"des\":\"Thanh Cong\"}";
    }
}
