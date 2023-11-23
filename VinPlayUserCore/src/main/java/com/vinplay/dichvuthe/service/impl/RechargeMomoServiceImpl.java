package com.vinplay.dichvuthe.service.impl;

import bitzero.server.entities.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinplay.common.HttpCommon;
import com.vinplay.common.notification.NotificationAdminObj;
import com.vinplay.common.notification.SendToWS;
import com.vinplay.dichvuthe.dao.RechargeDao;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.DepositMomoModel;
import com.vinplay.dichvuthe.response.MomoResponse;
import com.vinplay.dichvuthe.response.RechargeResponse;
import com.vinplay.dichvuthe.service.RechargeMomoService;
import com.vinplay.dichvuthe.utils.DvtConst;
import com.vinplay.dichvuthe.utils.DvtUtils;
import com.vinplay.lognaprut.HistoryTransConst;
import com.vinplay.lognaprut.HistoryTransDao;
import com.vinplay.lognaprut.entities.HistoryTransModel;
import com.vinplay.lognaprut.impl.HistoryTransDaoImpl;
import com.vinplay.momo.MomoClient;
import com.vinplay.usercore.service.CacheService;
import com.vinplay.usercore.service.impl.CacheServiceImpl;
import com.vinplay.utils.TelegramAlert;
import com.vinplay.utils.TelegramUtil;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

public class RechargeMomoServiceImpl implements RechargeMomoService {
    @Override
    public synchronized MomoResponse rechargeByMomoManual(User nickName, long amount, String sendFromNumber) {
        try {
            int code = 1;
            MomoResponse res = new MomoResponse("0", "0", "0", "0", code);
            //HazelcastInstance client;
            //IMap<String, UserModel> userMap;
            if (nickName == null || amount <= 0 || sendFromNumber.isEmpty()) {
                res.setCode(DvtConst.RECHARGE_STATUS_DATA_ERROR);
                return res;
            }
            //get pending transaction
            RechargeDao rechargeDao = new RechargeDaoImpl();
            if (rechargeDao.isPendingTransDepositMomo(nickName.getName())) {
                res.setCode(DvtConst.RECHARGE_STATUS_PENDING_TRANS);
                return res;
            }


            String transId = String.valueOf(VinPlayUtils.generateTransId());
            MomoClient momoClient = new MomoClient();
            String respone = momoClient.doCharge("dt74", "0f14df38e7c8db7ee0bf6ccfd84cff7f", transId, amount, "50", "IF9mwRw95NvT9vDyjcwoMjAyMS0wNS0wMiAwMjozMzo0NA==");
            com.vinplay.momo.MomoResponse jsonObject = new ObjectMapper().readValue(respone, com.vinplay.momo.MomoResponse.class);
            String comment = "";
            if (200L ==   jsonObject.getErrorCode()) {
                comment = String.valueOf((int) jsonObject.getComment()); // comment
                String userinfor = new String(Base64.getDecoder().decode((String) jsonObject.getInfomationAccount()));
                JSONObject json = (JSONObject) new JSONParser().parse(userinfor);
                String phone = (String) json.get("phone"); // phone
                String name = (String) json.get("name"); // name
                DepositMomoModel model = new DepositMomoModel(nickName.getName(), amount, phone, name, sendFromNumber);
                res.setCode(0);
                res.setReceiverName(name);
                res.setComment(comment);
                res.setReceiverPhone(phone);
                res.setTid(transId);
                if (!rechargeDao.InsertDepositCustomMomoManual(model, transId, comment)) {
                    return res;
                }
                HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
                historyTransDao.insertTransaction(new HistoryTransModel("MoMo", "Momo", "Nạp tiền", String.valueOf(amount), "Đang xử lý", "Đang chờ xử lý", nickName.getName(), HistoryTransConst.MOMO, transId));
//                new TelegramUtil().senMessToDaily(nickName.getName(), "Tạo phiếu nạp Momo", 0);
                NotificationAdminObj obj = new NotificationAdminObj();
                try {
                    model.setId(transId);
                    model.setStatus(1);
                    model.setDescription("NEW");
                    model.setCreatedAt(VinPlayUtils.getCurrentDateTime());
                    model.setUpdatedAt(VinPlayUtils.getCurrentDateTime());
                    SendToWS.sendBEExcRechargebymomo(model);
                    obj.setNapMomo(true);
                    SendToWS.sendBEExcNotification(obj);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
            // insert to db
            return res;
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public ArrayList<DepositMomoModel> GetListDepositPendingMomo() {
        RechargeDao rechargeDao = new RechargeDaoImpl();
        return rechargeDao.GetListDepositPendingMomo();
    }

    @Override
    public boolean UpdateDepositMomoManualStatus(String transId, int status, String des, String userApprove) {
        RechargeDao dao = new RechargeDaoImpl();
        return dao.UpdateDepositMomoManualStatus(transId, status, des, userApprove);

    }

    @Override
    public DepositMomoModel FindDepositMomoById(String transId) {
        RechargeDao dao = new RechargeDaoImpl();
        return dao.FindDepositMomoById(transId);
    }
}
