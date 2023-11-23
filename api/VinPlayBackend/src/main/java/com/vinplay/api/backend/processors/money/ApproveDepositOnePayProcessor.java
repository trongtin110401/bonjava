package com.vinplay.api.backend.processors.money;


import com.vinplay.api.backend.processors.cashout.NapRutGame;
import com.vinplay.api.backend.processors.cashout.NapRutModel;
import com.vinplay.common.notification.SendToWS;
import com.vinplay.common.report.EventactionAdminObj;
import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.dichvuthe.dao.RechargeDao;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.DepositOnePayModel;
import com.vinplay.lognaprut.HistoryTransConst;
import com.vinplay.lognaprut.service.HistoryTransService;
import com.vinplay.lognaprut.service.impl.HistoryTransServiceImpl;
import com.vinplay.usercore.entities.UserOnePayTransaction;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.BaseResponseModel;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ApproveDepositOnePayProcessor implements BaseProcessor<HttpServletRequest, String> {
    @Override
    public String execute(Param<HttpServletRequest> param) {
        // type = 0 is approve
        // type = 1 is reject
        synchronized (this) {
            CacheService cacheService = new CacheServiceImpl();
            BaseResponseModel response = new BaseResponseModel(false, "1001");
            HistoryTransService historyTransService = new HistoryTransServiceImpl();
            try {
                HttpServletRequest request = (HttpServletRequest) param.get();
                //check transId
                String transId = request.getParameter("transId");
                String typeStr = request.getParameter("type");
                String username = request.getParameter("username");
                String userApprove = request.getParameter("uad");
                if (transId.isEmpty() || typeStr.isEmpty()) {
                    return response.toJson();
                }
                int type = Integer.parseInt(typeStr);
                RechargeDao dao = new RechargeDaoImpl();

                // find transaction in db
                DepositOnePayModel trans = dao.FindDepositOnePayById(transId);
                if (trans == null) {
                    return response.toJson();
                }
                if (trans.Status == 105 || trans.Status == 101 ) {
                    return response.toJson();
                }

                // update trans in db
                int status = type;
                boolean resultUpdateTrans = dao.UpdateDepositStatusOnepay(transId, status, userApprove, 1); // 1 là trạng thái cần gửi
                historyTransService.update(transId, trans.Nickname, HistoryTransConst.ONE_PAY, this.getTrangthai(status), this.getTrangthaiDes(status));

                if(resultUpdateTrans) {
                    EventactionAdminObj model = new EventactionAdminObj();
                    model.setId(transId);
                    model.setStatus(status);
                    model.setUsername(username);
                    model.setType("DEPOSIT_ONE_PAY");
                    try {
                        SendToWS.sendBEExcEventaction(model);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (!resultUpdateTrans) {
                    return response.toJson();
                }
                if (type != 105) {
                    response.setSuccess(true);
                    return response.toJson();
                }

                //update user money
                if (type == 105) {
                    UserServiceImpl service = new UserServiceImpl();
                    try {
                        double fee = GameCommon.getValueDouble("RATIO_RECHARGE_BANK");
                        double amount = fee * trans.Amount;
                        long totalFee = Math.round(trans.Amount - amount);
                        totalFee = totalFee > 0 ? totalFee : 0;
                        response = service.updateMoneyFromAdmin(trans.Nickname, trans.Amount, "vin", "RECHARGE_ONEPAY", "Deposit Onepay bank", "Deposit Onepay bank transID: "+ transId, totalFee);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                BroadCastUserMoney.pushBroadCast(trans.Nickname);

                NapRutGame nrg = new NapRutGame();
                String codedl = nrg.getMaDaily(trans.Nickname);
                long SoTien = trans.Amount;
                if(codedl == null){
                    int xx = 2;
                }else if(codedl != null && codedl.trim().length() == 0){
                    int xx = 2;
                }else if(codedl != null && codedl.trim().equalsIgnoreCase("null") == false){
                    NapRutModel napgame = new NapRutModel(trans.Id, trans.Nickname, codedl, SoTien,"SmartLink", trans.CreatedAt);
                    nrg.NapRut(napgame);
                }else {
                    int xx =2;
                }

                return response.toJson();

            } catch (Exception e) {
                return response.toJson();
            }
        }

    }

    String getTrangthaiDes(int status) {
        switch (status) {
            case 0:
                return "sai tài khoản , Quý khách vui lòng nhập đúng tài khoản !";
            case 3:
                return "Hệ thống hiện tại không hỗ trợ ngân hàng của bạn";
            case 101:
                return "Bạn đã bỏ dở giao dịch";
            case 2:
                return "Tài khoản của quý khách không đủ tiền";
            case 105:
                return "Giao dịch thành công";
        }
        return "Giao dịch đang được hệ thống xử lý";
    }

    String getTrangthai(int status) {
        switch (status) {
            case 0:
            case 3:
            case 101:
            case 2:
                return "Từ chối";
            case 105:
                return "Thành công";
        }
        return "Đang xử lý";
    }

}
