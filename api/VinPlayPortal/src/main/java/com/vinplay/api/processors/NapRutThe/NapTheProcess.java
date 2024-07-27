package com.vinplay.api.processors.NapRutThe;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.common.notification.NotificationAdminObj;
import com.vinplay.common.notification.SendToWS;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.DepositMobileCardModel;
import com.vinplay.dichvuthe.service.impl.RechargeServiceImpl;
import com.vinplay.dichvuthe.utils.DvtConst;
import com.vinplay.gachthe.NapTienGaClient;
import com.vinplay.lognaprut.HistoryTransConst;
import com.vinplay.lognaprut.HistoryTransDao;
import com.vinplay.lognaprut.entities.HistoryTransModel;
import com.vinplay.lognaprut.impl.HistoryTransDaoImpl;
import com.vinplay.usercore.logger.MoneyLogger;
import com.vinplay.usercore.service.UserExtraService;
import com.vinplay.usercore.service.impl.UserExtraServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

public class NapTheProcess implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = (HttpServletRequest) param.get();
            String cardType = request.getParameter("cardtype");
            String pin = request.getParameter("pin");
            String seri = request.getParameter("seri");
            String amountx = request.getParameter("amount");
            String accessToken = request.getParameter("at");
            String nickname = this.getUserNameByAccessToken(accessToken);

            long amount = 0;
            if (pin != null) {
                pin = pin.trim();
            }
            if (seri != null) {
                seri = seri.trim();
            }
            if (amountx != null) {
                amount = Long.parseLong(amountx);
            }

            NapThe napthe = new NapThe();
            BlockNapThe block = new BlockNapThe();

            int type_the = 0;
            String loaithe = "";
            if (cardType.equalsIgnoreCase("VT")) {
                loaithe = "Viettel";
                type_the = 0;
            } else if (cardType.equalsIgnoreCase("VN")) {
                loaithe = "Vinaphone";
                type_the = 1;
            } else if (cardType.equalsIgnoreCase("MB")) {
                loaithe = "MobiFone";
                type_the = 2;
            } else {
                loaithe = "";
                type_the = 3;
            }

            boolean check_block = block.checkFormat(seri, pin, type_the);
            if (check_block == false) {
                return "{\"msg\":\"Thong tin xac thuc bi sai\",\"errorCode\":2}";
            }
            long id = VinPlayUtils.generateTransId();
            JSONObject result = napthe.napTheAuto(cardType, pin, seri, String.valueOf(id), amount);
            if (result.get("stt").equals(0.0d) || result.get("stt").equals(-1.0d) || result.get("stt").equals(-2.0d)) {
                return result.toJSONString();
            }
            RechargeDaoImpl dao = new RechargeDaoImpl();

            try {
                //todo: insert record vào db
                DepositMobileCardModel depositMobileCardModel = new DepositMobileCardModel(String.valueOf(id), nickname, amount, seri, pin, cardType);
                depositMobileCardModel.Status = DvtConst.STATUS_PENDING;
                dao.InsertDepositMobileCardManual(depositMobileCardModel);
                HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
                historyTransDao.insertTransaction(new HistoryTransModel(loaithe, "Thẻ Cào", "recharge", String.valueOf(amount), "Đang xử lý", "Đang chờ xử lý", nickname, HistoryTransConst.Card, String.valueOf(id)));
                // insert vào history
//                                new TelegramUtil().senMessToDaily(nickname, "Tạo phiếu nạp Thẻ Điện thoại", 0);
                NotificationAdminObj obj = new NotificationAdminObj();
                try {
                    obj.setNapCardPhone(true);
                    SendToWS.sendBEExcNotification(obj);
                    SendToWS.sendBEExcRechargebyautocard(depositMobileCardModel);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return result.toJSONString();
            } catch (Exception e2) {
                RechargeServiceImpl.logger.debug((Object) e2);
                MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1001", e2.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private String getUserNameByAccessToken(String accessToken) {
        UserExtraService userExtraService = new UserExtraServiceImpl();
        return userExtraService.getModelFromToken(accessToken).getNickname();
    }

    public String getConfigureCongThe() {
        try {
            String key = "CONFIGURE_CONGTHE_BECANG";
            HazelcastInstance instance = HazelcastClientFactory.getInstance();
            IMap map = instance.getMap("cacheConfig");
            if (map.containsKey((Object) key)) {
                return (String) map.get((Object) key);
            } else {
                map.put(key, "1");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "1";
    }
}
