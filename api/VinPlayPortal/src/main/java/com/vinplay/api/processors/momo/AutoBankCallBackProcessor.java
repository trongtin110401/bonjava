package com.vinplay.api.processors.momo;

import com.vinplay.api.entities.BankCallBack;
import com.vinplay.api.entities.BankCallBackResponse;
import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.dichvuthe.dao.RechargeDao;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.DepositBankModel;
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

public class AutoBankCallBackProcessor implements BaseProcessor<HttpServletRequest, String> {
    @Override
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest) param.get();
        BankCallBack bankcallback;
        BankCallBackResponse response = new BankCallBackResponse(200, "Thành công");
        String status = request.getParameter("status");
        String tranID = request.getParameter("tranID");
        String keyID = request.getParameter("keyID");
        String phoneAccount = request.getParameter("phoneAccount");
        String phoneCustomer = request.getParameter("phoneCustomer");
        String nameCustomer = request.getParameter("nameCustomer");
        String comment = request.getParameter("comment");
        String mamount = request.getParameter("amount");
        String type = request.getParameter("type");
        String money = request.getParameter("money");
        String signature = request.getParameter("signature");
        bankcallback = new BankCallBack(tranID,keyID,phoneAccount,phoneCustomer,nameCustomer,comment,mamount,type,money,signature);
        RechargeDao dao = new RechargeDaoImpl();
        // find transaction in db
        synchronized (this){
            DepositBankModel trans = dao.FindDepositBankById(bankcallback.getKeyID());
            HistoryTransService historyTransService = new HistoryTransServiceImpl();
            if (trans == null) {
                response.setErrorCode(500);
                response.setErrorDescription("Không tồn tại transaction Id");
                return response.toJson();
            }
            if(status == null){
                status = "0";
            }
            if(trans.getStatus() == 2 || trans.getStatus() == 100){
                response.setErrorCode(500);
                response.setErrorDescription("Từ chối callback. Giao dịch đã xử lý trước đó!!!");
                return response.toJson();
            }
            if(status.equalsIgnoreCase("2")){
                boolean resultUpdateTrans = dao.UpdateDepositBankManualStatus(bankcallback.getKeyID(), DvtConst.STATUS_REJECT, "", "Nap Bank Auto");
                historyTransService.update(trans.Id, trans.Nickname, HistoryTransConst.BANK, " Từ Chối", " giao dịch bị hủy");
                if (!resultUpdateTrans) {
                    response.setErrorCode(500);
                    response.setErrorDescription("Cập nhật thất bại");
                    return response.toJson();
                }else{
                    response.setErrorCode(500);
                    response.setErrorDescription("Cập nhật đơn nạp bị hủy");
                    return response.toJson();
                }
            }

            if (verifySignature(bankcallback)){
                // update trạng thái thành công
                if (bankcallback.getAmount().equals(String.valueOf(trans.Amount)) && trans.getStatus() !=100) { // đúng mới cộng tiền
                    boolean resultUpdateTrans = dao.UpdateDepositBankManualStatus(bankcallback.getKeyID(), DvtConst.STATUS_APPROVE, "", "Nap Bank Auto");
                    historyTransService.update(trans.Id, trans.Nickname, HistoryTransConst.BANK, "Thành công", " giao dịch thành công");
                    if (!resultUpdateTrans) {
                        response.setErrorCode(500);
                        response.setErrorDescription("Cập nhật thất bại");
                        return response.toJson();
                    }
                    // cộng tiền
                    UserServiceImpl service = new UserServiceImpl();
                    try {
                        double fee = GameCommon.getValueDouble("RATIO_RECHARGE_BANK");
                        double amount = fee * trans.Amount;
                        long totalFee = Math.round(trans.Amount - amount);
                        totalFee = totalFee > 0 ? totalFee : 0;
                        service.updateMoneyFromAdmin(trans.Nickname, (long) amount, "vin",
                                Consts.RECHARGE_BY_BANK, "Nạp Bank",
                                "nạp Bank tự động", totalFee);
                    }catch (Exception e) {
                        response.setErrorCode(500);
                        response.setErrorDescription(""+e);
                        return response.toJson();
                    }
                }else{
                    historyTransService.update(trans.Id, trans.Nickname, HistoryTransConst.BANK, "Từ chối", "Chuyển tiền không đủ");
                }


            }else{
                response.setErrorCode(500);
                response.setErrorDescription("Signature sai!");
                return response.toJson();
            }

            BroadCastUserMoney.pushBroadCast(trans.Nickname);
        }
        return response.toJson();
    }

    private boolean verifySignature(BankCallBack bankcallback) {

        String accessToken = "p3WPJZgSmY072I760nhRMjA0MS0wOC0xOCAyMDowMDo1Mg==";
        String key = "e097cfab500e529bd90904c972218bd";
        String builderContent = bankcallback.getTranID() + "|" + bankcallback.getAmount() + "|" + bankcallback.getComment() + "|" + key + "|" + accessToken;
        String myHash = "";
        try {
            myHash = VinPlayUtils.getMD5Hash(builderContent);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (myHash.equals(bankcallback.getSignature())) {
            return true;
        } else {
            return false;
        }


    }

}
