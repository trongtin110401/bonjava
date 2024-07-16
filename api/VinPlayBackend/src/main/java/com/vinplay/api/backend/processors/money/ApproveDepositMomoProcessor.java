package com.vinplay.api.backend.processors.money;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.common.notification.SendToWS;
import com.vinplay.common.report.EventactionAdminObj;
import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.dichvuthe.dao.RechargeDao;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.DepositBankModel;
import com.vinplay.dichvuthe.entities.DepositMomoModel;
import com.vinplay.dichvuthe.utils.DvtConst;
import com.vinplay.lognaprut.HistoryTransConst;
import com.vinplay.lognaprut.entities.HistoryTransModel;
import com.vinplay.lognaprut.impl.InsertELK;
import com.vinplay.lognaprut.service.HistoryTransService;
import com.vinplay.lognaprut.service.impl.HistoryTransServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.response.BaseResponseModel;
import com.vinplay.vbee.common.statics.Consts;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class ApproveDepositMomoProcessor implements BaseProcessor<HttpServletRequest, String> {

    @Override
    public String execute(Param<HttpServletRequest> param) {
        // type = 0 is approve
        // type = 1 is reject
        BaseResponseModel response = new BaseResponseModel(false, "1001");
        HistoryTransService historyTransService = new HistoryTransServiceImpl();
        try {
            HttpServletRequest request = (HttpServletRequest) param.get();
            //check transId
            String transId = request.getParameter("transId");
            String typeStr = request.getParameter("type");
            String userApprove = request.getParameter("uad");
            String tienx = request.getParameter("tien");
            long tien = 0;
            if (tienx != null) {
                tien = Long.parseLong(tienx);
            }
            if (transId.isEmpty() || typeStr.isEmpty()) {
                return response.toJson();
            }
            int type = Integer.parseInt(typeStr);
            RechargeDao dao = new RechargeDaoImpl();

            // find transaction in db
            DepositMomoModel trans = dao.FindDepositMomoById(transId);
            if (trans == null) {
                return response.toJson();
            }
//            if (trans.Status != DvtConst.STATUS_PENDING) {
//                return response.toJson();
//            }
            // update trans in db
            int status = type == 0 ? DvtConst.STATUS_APPROVE : DvtConst.STATUS_REJECT;
            boolean resultUpdateTrans = dao.UpdateDepositMomoManualStatus2(transId, status, "", userApprove, tien);
            historyTransService.update(transId, trans.Nickname, HistoryTransConst.MOMO, this.getTrangthai(status), this.getTrangthaiDes(status));
            if(resultUpdateTrans) {
                EventactionAdminObj model = new EventactionAdminObj();
                model.setId(transId);
                model.setStatus(status);
                model.setType("DEPOSIT_MOMO");
                try {
                    SendToWS.sendBEExcEventaction(model);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (!resultUpdateTrans) {
                return response.toJson();
            }
            if (type == 1) {
                response.setSuccess(true);
                return response.toJson();
            }
            //update user money
            if (type == 0) {
                UserServiceImpl service = new UserServiceImpl();
                try {

                    double fee = GameCommon.getValueDouble("RATIO_RECHARGE_MOMO");
                    double amount = fee * tien;
                    long totalFee = Math.round(tien - amount);
                    totalFee = totalFee > 0 ? totalFee : 0;
                    response = service.updateMoneyFromAdmin(trans.Nickname,tien, "vin", Consts.RECHARGE_BY_MOMO, "Deposit Momo", "Deposit Momo", totalFee);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            BroadCastUserMoney.pushBroadCast(trans.Nickname);
            return response.toJson();
            //send to user

            //

        } catch (Exception e) {
            return response.toJson();
        }

    }

    String getTrangthai(int status) {
        switch (status) {
            case 1:
                return "Đang xử lý";
            case 2:
                return "Từ chối";
            case 100:
                return "Thành công";
        }
        return "Đang xử lý";
    }

    String getTrangthaiDes(int status) {
        switch (status) {
            case 1:
                return "Hệ thống đang xử lý giao dịch của bạn";
            case 2:
                return "Giao dịch của bạn bị từ chối";
            case 100:
                return "Giao dịch thành công Thành công";
        }
        return "Đang xử lý";
    }



}
