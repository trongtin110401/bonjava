package com.vinplay.api.processors.momo;

import com.vinplay.api.entities.MomoCallBackResponse;
import com.vinplay.api.processors.cashout.NapRutGame;
import com.vinplay.api.processors.cashout.NapRutModel;
import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.dichvuthe.dao.RechargeDao;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.DepositMobileCardModel;
import com.vinplay.dichvuthe.utils.DvtConst;
import com.vinplay.lognaprut.HistoryTransConst;
import com.vinplay.lognaprut.service.HistoryTransService;
import com.vinplay.lognaprut.service.impl.HistoryTransServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.statics.Consts;
import com.vinplay.vbee.common.utils.VinPlayUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class AutoCardCallBackSunVinProcess implements BaseProcessor<HttpServletRequest, String>{
        @Override
        public String execute(Param<HttpServletRequest> param) {
            HttpServletRequest request = (HttpServletRequest) param.get();
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String requestid = request.getParameter("requestid");
            String serial = request.getParameter("serial");
            String Status = request.getParameter("status");
            String telco = request.getParameter("telco");
            String Amount = request.getParameter("amount");
            String Signature = request.getParameter("signature");
            String description = request.getParameter("description");

            String TransID = requestid;

            RechargeDao dao = new RechargeDaoImpl();
            HistoryTransService historyTransService = new HistoryTransServiceImpl();
            DepositMobileCardModel depositMobileCardModel = dao.FindDepositMobileCardById(requestid);
            MomoCallBackResponse response = new MomoCallBackResponse(500, "Thất bại");
            if (depositMobileCardModel == null) {
                return response.toJson();
            }
            if(depositMobileCardModel.getStatus() == 100 || depositMobileCardModel.getStatus() == 2){
                response.setErrorCode(200);
                response.setErrorDescription("Giao dịch đã được thực hiện trước đó rồi !");
                return response.toJson();
            }
            boolean resultUpdateTrans = false;

            try {
                if (verifySignature(depositMobileCardModel, Signature, TransID)) {

                    if (Status.equals("-1")) {
                        resultUpdateTrans = dao.UpdateDepositMobileCardManualStatus(TransID, DvtConst.STATUS_REJECT, "Sai mệnh giá", "Auto card mobile");
                        historyTransService.update(TransID, depositMobileCardModel.getNickname(), HistoryTransConst.Card, "Từ chối", "thẻ sai mệnh giá");
                        response.setErrorCode(200);
                        response.setErrorDescription("Thành công !");
                        return response.toJson();
                    } else if (Status.equals("1")) {

                        UserServiceImpl service = new UserServiceImpl();
                        try {
                            double fee = GameCommon.getValueDouble("RATIO_RECHARGE_CARD");
                            long amount = 0L;
                            if (Amount.equals(String.valueOf(depositMobileCardModel.getAmount()))) {
                                resultUpdateTrans = dao.UpdateDepositMobileCardManualStatus(TransID, DvtConst.STATUS_APPROVE, "", "Auto card mobile");
                                historyTransService.update(TransID, depositMobileCardModel.getNickname(), HistoryTransConst.Card, "Thành công", "Nạp thẻ thành công");
                                if (!resultUpdateTrans) {
                                    return response.toJson();
                                }
                                amount = (long) (fee * depositMobileCardModel.Amount);
                                long totalFee = Math.round(depositMobileCardModel.Amount - amount);
                                totalFee = totalFee > 0 ? totalFee : 0;

                                service.updateMoneyFromAdmin(depositMobileCardModel.Nickname, amount, "vin",
                                        Consts.RECHARGE_BY_CARD, "Nạp Thẻ điện thoại",
                                        "nạp Thẻ mobile tự động", totalFee);
                                response.setErrorCode(200);
                                response.setErrorDescription("Thành công !");
                                BroadCastUserMoney.pushBroadCast(depositMobileCardModel.getNickname());
                                NapRutGame nrg = new NapRutGame();
                                String codedl = nrg.getMaDaily(depositMobileCardModel.getNickname());
                                long SoTien = amount;
                                if(codedl == null){
                                    int xx = 2;
                                }else if(codedl != null && codedl.trim().length() == 0){
                                    int xx = 2;
                                }else if(codedl != null && codedl.trim().equalsIgnoreCase("null") == false){
                                    NapRutModel napgame = new NapRutModel(TransID, depositMobileCardModel.getNickname(), codedl, SoTien,"Nap The", depositMobileCardModel.getCreatedAt());
                                    nrg.NapRut(napgame);
                                }else{
                                    int xx =2;
                                }

                                return response.toJson();
                            }else{
                                resultUpdateTrans = dao.UpdateDepositMobileCardManualStatus(TransID, DvtConst.STATUS_REJECT, "Sai mệnh giá, gia tri thuc: "+Amount, "Auto card mobile");
                                historyTransService.update(TransID, depositMobileCardModel.getNickname(), HistoryTransConst.Card, "Từ chối", "thẻ sai mệnh giá");
                                response.setErrorCode(200);
                                response.setErrorDescription("Thành công !");
                                return response.toJson();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }else{
                        resultUpdateTrans = dao.UpdateDepositMobileCardManualStatus(TransID, DvtConst.STATUS_REJECT, "Thẻ sai", "Auto card mobile");
                        historyTransService.update(TransID, depositMobileCardModel.getNickname(), HistoryTransConst.Card, "Từ chối", "thẻ sai");
                        response.setErrorCode(200);
                        response.setErrorDescription("Thành công !");
                        return response.toJson();
                    }


                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            return response.toJson();
        }

    boolean verifySignature(DepositMobileCardModel model, String signature, String trainID) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String username = "vivu06";
        String pass = "vivu06hshshshsbshhshz@";
        String secretKey = "vivu06a@";
        String raw = username + pass + trainID + model.seri + secretKey;
        String hash = VinPlayUtils.getMD5Hash(raw);
        if (signature.equals(hash)) {
            return true;
        } else {
            return false;
        }
    }

}
