package com.vinplay.api.processors.AutoXuLyBank;

import com.vinplay.api.processors.cashout.NapSunVinBankMomo;
import com.vinplay.common.notification.NotificationAdminObj;
import com.vinplay.common.notification.SendToWS;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.DepositBankModel;
import com.vinplay.dichvuthe.utils.DvtConst;
import com.vinplay.utils.TelegramAlert;
import com.vinplay.utils.TelegramUtil;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.models.BankPartnerModel;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class GetBankInfoProcessor implements BaseProcessor<HttpServletRequest, String> {


    @Override
    public String execute(Param<HttpServletRequest> param) {

        HttpServletRequest request = param.get();
        String chargeType = request.getParameter("chargeType");
        String amount = request.getParameter("amount");
        String subType = request.getParameter("subType");
        String nickName = request.getParameter("nn");

        if (chargeType.isEmpty()) {
            return "{\"error\":400,\"data\":" + "chargeType invalid " + "}";
        }
        RechargeDaoImpl rechargeDao = new RechargeDaoImpl();


        DepositBankModel depositBankModel;
        depositBankModel = rechargeDao.isPendingTransDepositBankByNicknameAndBankName(nickName, subType);
        if (depositBankModel != null) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            boolean isExpired;
            try {
                Date createdAt = dateFormat.parse(depositBankModel.getCreatedAt());
                Date now = new Date();
                isExpired = now.getTime() - createdAt.getTime() > 900000;
                if (isExpired) {
                    rechargeDao.UpdateDepositBankManualStatus(depositBankModel.getId(), DvtConst.STATUS_REJECT, "H?t H?n", "ADMIN");
                } else {
                    return depositBankModel.toJson();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        AutoBankEntity autoBank = new AutoBankEntity();
        NapSunVinBankMomo napsun = new NapSunVinBankMomo();
        String TranID = String.valueOf(VinPlayUtils.generateTransId());
        BankPartnerModel bankPartnerModel = napsun.sendBenThuBaTaoCodePay(TranID, subType, amount.isEmpty() ? (int) Long.parseLong(amount) : 1000);
        depositBankModel = new DepositBankModel();
        depositBankModel.setId(bankPartnerModel.id);
        depositBankModel.setSubType(bankPartnerModel.chargeType);
        depositBankModel.setAmount(Long.parseLong(amount));
        depositBankModel.setBankAccountName(bankPartnerModel.phoneName);
        depositBankModel.setBankAccountNumber(bankPartnerModel.phoneNum);
        depositBankModel.setQRCode(bankPartnerModel.qr_url);
        depositBankModel.setPaymentURL(bankPartnerModel.payment_url);
        depositBankModel.setDescription(bankPartnerModel.code);
        depositBankModel.setUserSender(bankPartnerModel.chargeType);
        depositBankModel.setNickname(nickName);
        depositBankModel.setTransactionID(TranID);
        depositBankModel.setTimeToExpired(bankPartnerModel.timeToExpired);
        depositBankModel.setCreatedAt(VinPlayUtils.getCurrentDateTime());
        depositBankModel.setStatus(DvtConst.STATUS_PENDING);
        depositBankModel.setDescription(bankPartnerModel.code);
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_YEAR, 10);
        Date expiredDate = calendar.getTime();
        depositBankModel.setExpiredDate(expiredDate);
        rechargeDao.InsertDepositBankManual(depositBankModel);

        NotificationAdminObj obj = new NotificationAdminObj();
        obj.setNapBank(true);
        try {
            SendToWS.sendBEExcRechargebybank(depositBankModel);
            SendToWS.sendBEExcNotification(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return depositBankModel.toJson();
    }


    DepositBankModel processResponse(String json, String nickName) {
        DepositBankModel depositBankModel = new DepositBankModel();
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(json);
            JSONObject dataObject = (JSONObject) jsonObject.get("data");
            depositBankModel.setId(String.valueOf(dataObject.get("id")));
            depositBankModel.setQRCode((String) dataObject.get("qr_url"));
            depositBankModel.setPaymentURL((String) dataObject.get("payment_url"));
            depositBankModel.setDescription((String) dataObject.get("code"));
            depositBankModel.setBankAccountNumber((String) dataObject.get("phoneNum"));
            depositBankModel.setBankAccountName((String) dataObject.get("phoneName"));
            depositBankModel.setUserSender((String) dataObject.get("chargeType"));
            depositBankModel.setSubType((String) dataObject.get("bank_provider"));
            String stringValue = String.valueOf(dataObject.get("timeToExpired"));
            double doubleValue = Double.parseDouble(stringValue);
            int timeToExpired = (int) doubleValue;
            depositBankModel.setTimeToExpired(timeToExpired);
            depositBankModel.setNickname(nickName);
            depositBankModel.setTransactionID(String.valueOf(VinPlayUtils.generateTransId()));
            Date currentDate = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.add(Calendar.DAY_OF_YEAR, 10);
            Date expiredDate = calendar.getTime();
            depositBankModel.setExpiredDate(expiredDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return depositBankModel;

    }

    public static void main(String[] args) {
        String createdAtString = "2024-04-03 23:32:13";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date createdAt = dateFormat.parse(createdAtString);

            Date now = new Date();

            boolean isExpired = now.getTime() - createdAt.getTime() > 900000; // 900 giây = 900000 mili giây

            if (isExpired) {
                System.out.println("?ã quá 900 giây.");
            } else {
                System.out.println("Ch?a quá 900 giây.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
