package com.vinplay.api.processors.momo;

import com.vinplay.api.entities.MomoCallBack;
import com.vinplay.api.entities.MomoCallBackResponse;
import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.dichvuthe.dao.RechargeDao;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.DepositMomoModel;
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

public class MomoCallBackProcessor implements BaseProcessor<HttpServletRequest, String> {
    @Override
    public String   execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest) param.get();
        MomoCallBack momoCallBack;
        MomoCallBackResponse response = new MomoCallBackResponse(200, "Thành công");

        String tranID = request.getParameter("tranID");
        String keyID = request.getParameter("keyID");
        String phoneAccount = request.getParameter("phoneAccount");
        String phoneCustomer = request.getParameter("phoneCustomer");
        String nameCustomer = request.getParameter("nameCustomer");
        String comment = request.getParameter("comment");
        String mamount = request.getParameter("amount");
        String money = request.getParameter("money");
        String signature = request.getParameter("signature");
        //  JsonReader jsonReader = new JsonReader(request.getReader());
        momoCallBack = new MomoCallBack(tranID, keyID, phoneAccount, phoneCustomer, nameCustomer, comment, mamount, money, signature);
        RechargeDao dao = new RechargeDaoImpl();

        // find transaction in db
        synchronized (this){
        DepositMomoModel trans = dao.FindDepositMomoById(momoCallBack.getKeyID());

        HistoryTransService historyTransService = new HistoryTransServiceImpl();
        if (trans == null) {
            response.setErrorCode(500);
            response.setErrorDescription("Không tồn tại transaction Id");
            return response.toJson();
        }



        if (verifySignature(momoCallBack)){
            // update trạng thái thành công
            if (momoCallBack.getAmount().equals(String.valueOf(trans.Amount)) && trans.getStatus() !=100) { // đúng mới cộng tiền
                boolean resultUpdateTrans = dao.UpdateDepositMomoManualStatus(momoCallBack.getKeyID(), DvtConst.STATUS_APPROVE, "", "Momo Auto");

                historyTransService.update(trans.Id, trans.Nickname, HistoryTransConst.MOMO, "Thành công", " giao dịch thành công");

                if (!resultUpdateTrans) {
                    response.setErrorCode(500);
                    response.setErrorDescription("Cập nhật thất bại");
                    return response.toJson();
                }

                // cộng tiền
                UserServiceImpl service = new UserServiceImpl();
                try {
                    double fee = GameCommon.getValueDouble("RATIO_RECHARGE_MOMO");
                    double amount = fee * trans.Amount;
                    long totalFee = Math.round(trans.Amount - amount);
                    totalFee = totalFee > 0 ? totalFee : 0;
                    service.updateMoneyFromAdmin(trans.Nickname, (long) amount, "vin",
                            Consts.RECHARGE_BY_MOMO, "Nạp Momo",
                            "nạp Momo tự động", totalFee);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                historyTransService.update(trans.Id, trans.Nickname, HistoryTransConst.MOMO, "Từ chối", "Chuyển tiền không đủ");

            }

        }
        BroadCastUserMoney.pushBroadCast(trans.Nickname);
        }
        return response.toJson();
    }


    private boolean verifySignature(MomoCallBack momoCallBack) {

        String accessToken = "IF9mwRw95NvT9vDyjcwo0jAyMS0wNS0wM0AwMjozMzo0NA==";
        String key = "0f14df38e7c8db7ee0bf6ccfd80cff70";
        String builderContent = momoCallBack.getTranID() + "|" + momoCallBack.getAmount() + "|" + momoCallBack.getComment() + "|" + key + "|" + accessToken;
        String myHash = "";
        try {
            myHash = VinPlayUtils.getMD5Hash(builderContent);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (myHash.equals(momoCallBack.getSignature())) {
            return true;
        } else {
            return false;
        }


    }

}
