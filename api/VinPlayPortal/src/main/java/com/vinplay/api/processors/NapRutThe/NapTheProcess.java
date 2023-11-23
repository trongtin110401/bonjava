package com.vinplay.api.processors.NapRutThe;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.common.notification.NotificationAdminObj;
import com.vinplay.common.notification.SendToWS;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.DepositMobileCardModel;
import com.vinplay.dichvuthe.service.impl.RechargeServiceImpl;
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
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

public class NapTheProcess implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = (HttpServletRequest) param.get();
            String cardTypex = request.getParameter("cardtype");
            String pin = request.getParameter("pin");
            String seri = request.getParameter("seri");
            String amountx = request.getParameter("amount");
            String accessToken = request.getParameter("at");
            String nickname = this.getUserNameByAccessToken(accessToken);
            String cardType = "";

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
            if (cardTypex.equalsIgnoreCase("0")) {
                cardType = "VT";
            } else if (cardTypex.equalsIgnoreCase("1")) {
                cardType = "Vina";
            } else if (cardTypex.equalsIgnoreCase("2")) {
                cardType = "Mobi";
            } else if (cardTypex.equalsIgnoreCase("3")) {
                cardType = "vnm";
            } else {
                cardType = "";
            }

            NapThe napthe = new NapThe();
            BlockNapThe block = new BlockNapThe();

            int type_the = 0;
            String loaithe = "";
            if (cardType.equalsIgnoreCase("VT")) {
                loaithe = "Viettel";
                type_the = 0;
            } else if (cardType.equalsIgnoreCase("Vina")) {
                loaithe = "Vinaphone";
                type_the = 1;
            } else if (cardType.equalsIgnoreCase("Mobi")) {
                loaithe = "MobiFone";
                type_the = 2;
            } else {
                loaithe = "";
                type_the = 3;
            }

            boolean check_block = block.checkFormat(seri,pin, type_the);
            if(check_block == false){
                return "{\"msg\":\"Thong tin xac thuc bi sai\",\"errorCode\":2}";
            }


            String id = "gacon_time1" + String.valueOf(VinPlayUtils.generateTransId());
            String configureConthe = getConfigureCongThe();
            JSONObject result = null;
            if(configureConthe.equals("1")) {
                id= id.replaceAll("gacon_time","vivu_tome");
                result = napthe.doCharge(cardType, pin, seri, id, amount);
            }else if(configureConthe.equals("2")) {
                result = napthe.doCharge_tokyo(cardType, pin, seri, id, amount);
            }else if(configureConthe.equals("3")) {
                int random = new Random().nextInt(10);
                if(random < 4) {
                    id= id.replaceAll("gacon_time","vivu_tome");
                    result = napthe.doCharge(cardType, pin, seri, id, amount);
                }else {
                    result = napthe.doCharge_tokyo(cardType, pin, seri, id, amount);
                }
            }else {
                result = napthe.doCharge_tokyo(cardType, pin, seri, id, amount);
            }
            RechargeDaoImpl dao = new RechargeDaoImpl();






            if ("0".equals(result.get("errorCode").toString())) { // 0 là thành công
                try {

                    //todo: insert record vào db
                    DepositMobileCardModel depositMobileCardModel = new DepositMobileCardModel(id, nickname, amount, seri, pin, cardType);
                    dao.InsertDepositMobileCardManual(depositMobileCardModel);
                    HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
                    historyTransDao.insertTransaction(new HistoryTransModel(loaithe, "Thẻ Điện thoại", "Nạp tiền", String.valueOf(amount), "Đang xử lý", "Đang chờ xử lý", nickname, HistoryTransConst.Card, id));
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
            } else {
                //MoneyLogger.log(nickname, "RechargeByCard", 0L, 0L, "vin", "Nap vin qua the", "1001", result.get("errorCode").toString());
                return result.toJSONString();
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
            }else {
                map.put(key,"1");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "1";
    }
}
