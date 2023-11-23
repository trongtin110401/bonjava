package com.vinplay.api.backend.processors.money;

import com.vinplay.dichvuthe.dao.RechargeDao;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.DepositOnePayModel;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.BaseResponseModel;

import javax.servlet.http.HttpServletRequest;

public class UpdateMaGiaoDichTechcombankOnePayProcessor implements BaseProcessor<HttpServletRequest, String> {
    @Override
    public String execute(Param<HttpServletRequest> param) {
        // type = 0 is approve
        // type = 1 is reject
        BaseResponseModel response = new BaseResponseModel(false, "1001");
        try{
            HttpServletRequest request = (HttpServletRequest)param.get();
            //check transId
            String transId = request.getParameter("transId");
            String typeStr = request.getParameter("type");
            String userApprove = request.getParameter("uad");
            if(transId.isEmpty() || typeStr.isEmpty()){
                return response.toJson();
            }

            RechargeDao dao = new RechargeDaoImpl();

            // find transaction in db
            DepositOnePayModel trans = dao.FindDepositOnePayById(transId);
            if(trans == null){
                return response.toJson();
            }
            if(trans.Status == 105 ){
                return response.toJson();
            }
            // update trans in db
            boolean resultUpdateTrans = dao.UpdateMaGiaoDichTechcomBankOnepay(transId, typeStr, userApprove);
            if(!resultUpdateTrans){
                return response.toJson();
            } else {
                response.setSuccess(true);
                return response.toJson();
            }

            //update user money
            //send to user

            //

        }catch (Exception e){
            return response.toJson();
        }

    }
}
