package com.vinplay.api.backend.processors.money;

import com.vinplay.api.backend.processors.cashout.NapRutGame;
import com.vinplay.api.backend.processors.cashout.NapRutModel;
import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.dichvuthe.dao.RechargeDao;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.DepositMobileCardModel;
import com.vinplay.dichvuthe.entities.DepositMomoModel;
import com.vinplay.dichvuthe.utils.DvtConst;
import com.vinplay.lognaprut.HistoryTransConst;
import com.vinplay.lognaprut.entities.HistoryTransModel;
import com.vinplay.lognaprut.service.HistoryTransService;
import com.vinplay.lognaprut.service.impl.HistoryTransServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.BaseResponseModel;
import com.vinplay.vbee.common.statics.Consts;

import javax.servlet.http.HttpServletRequest;

public class ApproveDepositMobileCardProcessor implements BaseProcessor<HttpServletRequest, String> {

    @Override
    public String execute(Param<HttpServletRequest> param) {
        // type = 0 is approve
        // type = 1 is reject
        synchronized (this) {
            BaseResponseModel response = new BaseResponseModel(false, "1001");
            try {
                HttpServletRequest request = (HttpServletRequest) param.get();
                //check transId
                String transId = request.getParameter("transId");
                String typeStr = request.getParameter("type");
                String userApprove = request.getParameter("uad");
                if (transId.isEmpty() || typeStr.isEmpty()) {
                    return response.toJson();
                }
                int type = Integer.parseInt(typeStr);
                RechargeDao dao = new RechargeDaoImpl();

                // find transaction in db
                DepositMobileCardModel trans = dao.FindDepositMobileCardById(transId);

                HistoryTransService historyTransService = new HistoryTransServiceImpl();
                if (trans == null) {
                    return response.toJson();
                }
                if (trans.Status != DvtConst.STATUS_PENDING) {
                    return response.toJson();
                }
                // update trans in db
                int status = type == 0 ? DvtConst.STATUS_APPROVE : DvtConst.STATUS_REJECT;
                boolean resultUpdateTrans = dao.UpdateDepositMobileCardManualStatus(transId, status, "", userApprove);
                if (!resultUpdateTrans) {
                    return response.toJson();
                }
                if (type == 1) {
                    historyTransService.update(transId, trans.getNickname(), HistoryTransConst.Card, "Từ chối", "Giao dịch bị từ chối ");
                    response.setSuccess(true);
                    return response.toJson();
                }
                //update user money
                if (type == 0) {
                    UserServiceImpl service = new UserServiceImpl();
                    try {
                        double fee = GameCommon.getValueDouble("RATIO_RECHARGE_MOMO");
                        double amount = fee * trans.Amount;
                        long totalFee = Math.round(trans.Amount - amount);
                        totalFee = totalFee > 0 ? totalFee : 0;
                        response = service.updateMoneyFromAdmin(trans.Nickname, (long) amount, "vin", Consts.RECHARGE_BY_CARD, "Nap tien bang the", "nap tien the dien thoai tu dong", totalFee);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    historyTransService.update(transId, trans.getNickname(), HistoryTransConst.Card, "Thành công", "Giao dịch Thành Công ");
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
                    NapRutModel napgame = new NapRutModel(trans.Id, trans.Nickname, codedl, SoTien,"Nap The", trans.CreatedAt);
                    nrg.NapRut(napgame);
                }else{
                    int xx = 2;
                }

                return response.toJson();
                //send to user

                //

            } catch (Exception e) {
                return response.toJson();
            }

        }
    }
}
