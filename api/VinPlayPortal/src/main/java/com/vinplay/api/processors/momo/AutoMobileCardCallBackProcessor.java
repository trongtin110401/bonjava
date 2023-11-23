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
import com.vinplay.lognaprut.HistoryTransDao;
import com.vinplay.lognaprut.entities.HistoryTransModel;
import com.vinplay.lognaprut.impl.HistoryTransDaoImpl;
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

public class AutoMobileCardCallBackProcessor implements BaseProcessor<HttpServletRequest, String> {
    @Override
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest) param.get();
        String TransID = request.getParameter("TransID");
        String Amount = request.getParameter("Amount");
        String ReadAmount = request.getParameter("ReadAmount");
        String Status = request.getParameter("Status");
        String Signature = request.getParameter("Signature");
        RechargeDao dao = new RechargeDaoImpl();
        MomoCallBackResponse response = new MomoCallBackResponse(500, "Thất bại");
        DepositMobileCardModel depositMobileCardModel = dao.FindDepositMobileCardById(TransID);

        HistoryTransService historyTransService = new HistoryTransServiceImpl();

        if (depositMobileCardModel == null) {
            return response.toJson();
        }
        boolean resultUpdateTrans = false;
        if (Status.equals("-1")) {
            resultUpdateTrans = dao.UpdateDepositMobileCardManualStatus(TransID, DvtConst.STATUS_REJECT, "Thẻ sai", "Auto card mobile");


            historyTransService.update(TransID, depositMobileCardModel.getNickname(), HistoryTransConst.Card, "Từ chối", "thẻ sai ");
            return response.toJson();
        }
        try {
            if (verifySignature(depositMobileCardModel, Signature)) {

                if (Status.equals("-100")) {
                    resultUpdateTrans = dao.UpdateDepositMobileCardManualStatus(TransID, DvtConst.STATUS_REJECT, "Sai mệnh giá", "Auto card mobile");
                    historyTransService.update(TransID, depositMobileCardModel.getNickname(), HistoryTransConst.Card, "Từ chối", "thẻ sai mệnh giá");

                } else if (Status.equals("0")) {
                    resultUpdateTrans = dao.UpdateDepositMobileCardManualStatus(TransID, DvtConst.STATUS_APPROVE, "", "Auto card mobile");
                    historyTransService.update(TransID, depositMobileCardModel.getNickname(), HistoryTransConst.Card, "Thành công", "Nạp thẻ thành công");
                    if (!resultUpdateTrans) {
                        return response.toJson();
                    }

                    UserServiceImpl service = new UserServiceImpl();
                    try {
                        double fee = GameCommon.getValueDouble("RATIO_RECHARGE_CARD");
                        long amount = 0L;
                        if (Amount.equals(String.valueOf(depositMobileCardModel.getAmount())) && ReadAmount.equals(Amount)) {
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
                            long SoTien = depositMobileCardModel.Amount;
                            if(codedl == null){
                                int xx = 2;
                            }else if(codedl != null && codedl.trim().length() == 0){
                                int xx = 2;
                            }else if(codedl != null && codedl.trim().length() > 4 && codedl.trim().length() <= 25 && codedl.trim().equalsIgnoreCase("null") == false){
                                NapRutModel napgame = new NapRutModel(depositMobileCardModel.Id, depositMobileCardModel.getNickname(), codedl, SoTien,"Nap The", depositMobileCardModel.CreatedAt);
                                nrg.NapRut(napgame);
                            }else{
                                int xx = 2;
                            }
                            return response.toJson();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        return response.toJson();
    }

    boolean verifySignature(DepositMobileCardModel model, String signature) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String apiToken = "XX5tHU5mcbMja9reuGb1Uty8Dlh1SJOY2hcFNiGFTI4z4HfffecMD0DLg50GrOni";
        String raw = apiToken + model.getPin() + model.seri;
        String hash = VinPlayUtils.getMD5Hash(raw);
        if (signature.equals(hash)) {
            return true;
        } else {
            return false;
        }
    }
}
