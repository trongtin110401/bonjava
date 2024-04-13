package com.vinplay.api.processors.AutoXuLyBank;

import com.vinplay.common.notification.NotificationAdminObj;
import com.vinplay.common.notification.SendToWS;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.DepositBankModel;
import com.vinplay.dichvuthe.utils.DvtConst;
import com.vinplay.utils.TelegramAlert;
import com.vinplay.utils.TelegramUtil;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
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

        String url = autoBank.getUrl() + ":" + autoBank.getPort() + autoBank.getApiRegCharge()
                + "?apiKey=" + autoBank.getApiKey() + "&chargeType=" + chargeType + "&amount=" + amount + "&subType=" + subType + "&requestId=" + UUID.randomUUID();
        depositBankModel = processResponse(APIProcess.responseGetAPI(url, null), nickName);
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
