package com.vinplay.api.processors.momo;

import com.google.gson.Gson;
import com.vinplay.cashout.CarDuPhongResponse;
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

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AutoMobileCardCallBackDuPhong implements BaseProcessor<HttpServletRequest, String> {
    @Override
    public String execute(Param<HttpServletRequest> param) {
        CarDuPhongResponse response = new CarDuPhongResponse(200, "Thành công");
        try {
            HttpServletRequest request = (HttpServletRequest) param.get();
            RechargeDao dao = new RechargeDaoImpl();
            HistoryTransService historyTransService = new HistoryTransServiceImpl();

            String body = null;

            body = request.getReader().lines()
                    .reduce("", (accumulator, actual) -> accumulator + actual);
            String body_real = java.net.URLDecoder.decode(body, StandardCharsets.UTF_8.name());

            CardDuPhong carddp = new Gson().fromJson(body_real, CardDuPhong.class);
            String TransID = carddp.getRequest_id();
            DepositMobileCardModel depositMobileCardModel = dao.FindDepositMobileCardById(TransID);

//            int check = 1;
//            if(check == 1){
//                response.setErrorCode(666);
//                response.setErrorDescription("request: "+body_real+" | card: "+carddp.toString());
//                return response.toJson();
//            }



            if (depositMobileCardModel == null) {
                response.setErrorCode(500);
                response.setErrorDescription("Thất Bại");
                return response.toJson();
            }
            boolean resultUpdateTrans = false;
            if(carddp.getStatus() == 2){
                if(verifySignature(carddp, carddp.callback_sign) == true){
                    resultUpdateTrans = dao.UpdateDepositMobileCardManualStatus(TransID, DvtConst.STATUS_APPROVE, "", "Auto card mobile");
                    historyTransService.update(TransID, depositMobileCardModel.getNickname(), HistoryTransConst.CARD, "Thành công", "Nạp thẻ thành công");
                    if (!resultUpdateTrans) {
                        response.setErrorCode(500);
                        response.setErrorDescription("Thất Bại");
                        return response.toJson();
                    }
                    UserServiceImpl service = new UserServiceImpl();
                    try {
                        double fee = GameCommon.getValueDouble("RATIO_RECHARGE_CARD");
                        long amount = 0L;


                            amount = (long) (fee * carddp.getValue());
                            long totalFee = Math.round(carddp.getValue() - amount);
                            totalFee = totalFee > 0 ? totalFee : 0;

                            service.updateMoneyFromAdmin(depositMobileCardModel.Nickname, amount, "vin",
                                    Consts.RECHARGE_BY_CARD, "Nạp Thẻ điện thoại",
                                    "nạp Thẻ mobile tự động", totalFee);

                            response.setErrorCode(200);
                            response.setErrorDescription("Thành công !");
                            BroadCastUserMoney.pushBroadCast(depositMobileCardModel.getNickname());
                            return response.toJson();


                    } catch (Exception e) {
                        response.setErrorCode(500);
                        response.setErrorDescription(e+"");
                        return response.toJson();
                    }


                }else{
                    resultUpdateTrans = dao.UpdateDepositMobileCardManualStatus(TransID, DvtConst.STATUS_REJECT, "Thẻ không dùng được", "Auto card mobile");
                    historyTransService.update(TransID, depositMobileCardModel.getNickname(), HistoryTransConst.CARD, "Từ chối", "Thẻ không dùng được");
                    response.setErrorCode(500);
                    response.setErrorDescription("Signature Sai");
                    return response.toJson();
                }
            }else if(carddp.getStatus() == 3){
                resultUpdateTrans = dao.UpdateDepositMobileCardManualStatus(TransID, DvtConst.STATUS_REJECT, "Thẻ không dùng được", "Auto card mobile");
                historyTransService.update(TransID, depositMobileCardModel.getNickname(), HistoryTransConst.CARD, "Từ chối", "Thẻ không dùng được");
                response.setErrorCode(500);
                response.setErrorDescription("Thất Bại");
                return response.toJson();
            }else if(carddp.getStatus() == 99){
                resultUpdateTrans = dao.UpdateDepositMobileCardManualStatus(TransID, DvtConst.STATUS_REJECT, "Thẻ chờ xử lý", "Auto card mobile");
                historyTransService.update(TransID, depositMobileCardModel.getNickname(), HistoryTransConst.CARD, "Từ chối", "Thẻ chờ xử lý");
                response.setErrorCode(500);
                response.setErrorDescription("Thất Bại");
                return response.toJson();
            }else if(carddp.getStatus() == 1){
                if(verifySignature(carddp, carddp.callback_sign) == true){
                    resultUpdateTrans = dao.UpdateDepositMobileCardManualStatus(TransID, DvtConst.STATUS_APPROVE, "", "Auto card mobile");
                    historyTransService.update(TransID, depositMobileCardModel.getNickname(), HistoryTransConst.CARD, "Thành công", "Nạp thẻ thành công");
                    if (!resultUpdateTrans) {
                        response.setErrorCode(500);
                        response.setErrorDescription("Thất Bại");
                        return response.toJson();
                    }
                    UserServiceImpl service = new UserServiceImpl();
                    try {
                        double fee = GameCommon.getValueDouble("RATIO_RECHARGE_CARD");
                        long amount = 0L;

                        if (carddp.getDeclared_value() == carddp.getValue()) {
                            amount = (long) (fee * depositMobileCardModel.Amount);
                            long totalFee = Math.round(depositMobileCardModel.Amount - amount);
                            totalFee = totalFee > 0 ? totalFee : 0;

                            service.updateMoneyFromAdmin(depositMobileCardModel.Nickname, amount, "vin",
                                    Consts.RECHARGE_BY_CARD, "Nạp Thẻ điện thoại",
                                    "nạp Thẻ mobile tự động", totalFee);

                            response.setErrorCode(200);
                            response.setErrorDescription("Thành công !");
                            BroadCastUserMoney.pushBroadCast(depositMobileCardModel.getNickname());
                            return response.toJson();
                        }

                    } catch (Exception e) {
                        response.setErrorCode(500);
                        response.setErrorDescription(e+"");
                        return response.toJson();
                    }
                }else{
                    resultUpdateTrans = dao.UpdateDepositMobileCardManualStatus(TransID, DvtConst.STATUS_REJECT, "Thẻ không dùng được", "Auto card mobile");
                    historyTransService.update(TransID, depositMobileCardModel.getNickname(), HistoryTransConst.CARD, "Từ chối", "Thẻ không dùng được");
                    response.setErrorCode(500);
                    response.setErrorDescription("Signature Sai");
                    return response.toJson();
                }

            }

        } catch (Exception e) {
            response.setErrorCode(500);
            response.setErrorDescription(e+"");
            return response.toJson();
        }
        response.setErrorCode(500);
        response.setErrorDescription("Error");
        return response.toJson();

    }

    boolean verifySignature(CardDuPhong carddp, String signature){
        String parner_key = "bdc34ba7cf08bd2fe9ee571536a0abc5";
        String parner_id = "9630150361";

        String sign = GenSignMd5_callback(parner_key,carddp.getCode(), carddp.getSerial());
        if(sign.equals(signature)){
            return true;
        }else{
            return false;
        }

    }

    public String GenSignMd5_callback(String partner_key, String code,String seri){
        try {
            String input = partner_key+code+seri;
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }


}
