package game.scheduler;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import game.bean.MapperUtils;
import game.entity.entitiesxocdia.GamePotReportModel;
import game.entity.entitynotification.NotificationAdminObj;
import game.entity.entitytaixiu.TaiXiuAdminReportObj;
import game.entity.entitytaixiu.TaiXiuAdminReportResponse;
import game.entity.report.*;
import game.entity.response.*;
import game.exceptions.KeyNotFoundException;
import game.models.baucuato2.BauCuaListUserResponse;
import game.models.baucuato2.BauCuaToReportResponse;
import game.models.baucuato2.BauCuaUserInfomation;
import game.service.CacheService;
import game.ws.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.yeauty.pojo.Session;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class ScheduledTasks { // chay schedule lien tuc // cach nay chi dung cho nhung cai thay doi lien tuc nhu thoi gian cua game thoi ko nen dung cho nhung thang co event
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final String USER_TAI_XIU = "user_tai_xiu";
    private static final String USER_TAI_XIU_MD5 = "user_tai_xiu_md5";
    private static final String CASHOUTBYBANK_ADMIN = "cashoutbybank_admin";
    private static final String CASHOUTBYCARDMANUAL_ADMIN = "cashoutbycardmanual_admin";
    private static final String RECHARGEBYBANK_ADMIN = "rechargebybank_admin";
    private static final String RECHARGEBYONEPAY_ADMIN = "rechargebyonepay_admin";
    private static final String RECHARGEBYONEPAYOTP_ADMIN = "rechargebyonepayotp_admin";
    private static final String RECHARGEBYMOMO_ADMIN = "rechargebymomo_admin";
    private static final String RECHARGEBYMOMOSUNVIN_ADMIN = "rechargebymomosunvin_admin";
    private static final String RECHARGEBYAUTOCARD_ADMIN = "rechargebyautocard_admin";
    private static final String NOTIFY_ADMIN = "notify_admin";
    private static final String EVENTACTION_ADMIN = "eventaction_admin";


    @Autowired
    CacheService cacheService;

    @Scheduled(fixedRate = 800)
    public void getCacheXocDia() {
        try {

            String timmer = (String) cacheService.getValueStr("XocDia_Flag_Time");
            int gameState = cacheService.getValueInt("XocDia_Flag_GameState");
            String isBetting = (String) cacheService.getValueStr("XocDia_Flag_betting");
            String session = (String) cacheService.getValueStr("XocDia_Flag_session");

            XocDiaGameStatus xocDiaGameStatus = new XocDiaGameStatus("1", timmer, String.valueOf(gameState), isBetting, session);
            String json = MapperUtils.mapper.writeValueAsString(xocDiaGameStatus);
            this.sendMessToAdmin(json);

            cacheService.removeKey("XocDia_Flag_Time");
            cacheService.removeKey("XocDia_Flag_GameState");
            cacheService.removeKey("XocDia_Flag_betting");
            cacheService.removeKey("XocDia_Flag_session");
        } catch (KeyNotFoundException | JsonProcessingException e) {
            e.printStackTrace();
            System.out.println("loi");

        }
    }

    //   @Scheduled(fixedRate = 1000)
    public void getCacheXocReportDia() {
        try {
            String flag = (String) cacheService.getValueStr("Xocdia_Change");


            ArrayList<GamePotReportModel> gamePotReportModels = new ArrayList<GamePotReportModel>();
            for (int i = 0; i < 6; i++) {
                GamePotReportModel gamePotReportModel = MapperUtils.mapper.readValue(cacheService.getValueStr("XocDia_Pot" + i), GamePotReportModel.class);
                gamePotReportModels.add(gamePotReportModel);
            }
            XocDiaReportResponse xocDiaReportResponse = new XocDiaReportResponse("2", gamePotReportModels);

            String json = MapperUtils.mapper.writeValueAsString(xocDiaReportResponse);
            this.sendMessToAdmin(json);

            cacheService.removeKey("Xocdia_Change");

        } catch (KeyNotFoundException | JsonProcessingException e) {
            e.printStackTrace();
            System.out.println("lloi duoi");

        }
    }
    @Scheduled(fixedRate = 800)
    public void sendStateUser() {
        try {

            HashMap<String,String> mapState = (HashMap<String, String>) cacheService.getObject("List_UserState_Slot");
            ArrayList<String> listState = new ArrayList<>(mapState.values());
            StateGameResponse response = new StateGameResponse("2",listState);
            String json = MapperUtils.mapper.writeValueAsString(response);
            this.sendStateToAdmin(json);

        } catch (KeyNotFoundException | JsonProcessingException e) {
            e.printStackTrace();
            System.out.println("loi");

        }
    }

    private void  sendStateToAdmin(String mess){
        for (Session session : ServerStateGame.sessions) {
            session.sendText(mess);
        }
    }
    @Scheduled(fixedRate = 900)
    public void executeBauCuaInfo() {
        try {

            BauCuaToReportResponse response = new BauCuaToReportResponse(true, "0");
            Map<Integer, Long> mapBotReportBet = (Map<Integer, Long>) cacheService.getObject("mapBotReportBet");
            Map<Integer, Long> mapReportBet = (Map<Integer, Long>) cacheService.getObject("mapReportBet");
            long referentId = cacheService.getValueInt("BauCuareferenceId");
            String remainTime = cacheService.getValueStr("BauCuaRemainTime");
            boolean bettingState = (boolean) cacheService.getObject("bettingStateBauCua");
            response.setMapReportBet(mapReportBet);
            response.setMapBotReportBet(mapBotReportBet);
            response.setBetting(bettingState);
            response.setReferenceId(referentId);
            response.setRemainingTime((remainTime));
            this.sendMessToAdminBauCua(response.toJson());


            cacheService.removeKey("bettingStateBauCua");
            cacheService.removeKey("BauCuareferenceId");
            cacheService.removeKey("BauCuaRemainTime");
        } catch (KeyNotFoundException e) {
            e.printStackTrace();
        }


    }

    @Scheduled(fixedRate = 1000)
    public void executeListUser() {

        BauCuaListUserResponse response = new BauCuaListUserResponse(true, "1");
        try {
            ArrayList<BauCuaUserInfomation> list = (ArrayList<BauCuaUserInfomation>) MapperUtils.mapper.readValue(cacheService.getValueStr("baucualist"), new TypeReference<ArrayList<BauCuaUserInfomation>>() {
            });

            response.setListBauCuaInformation(list);
            this.sendMessToAdminBauCua(response.toJson());

        } catch (KeyNotFoundException e) {

        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    /**
     * Send thông tin tài xỉu sang admin php
     *
     * @return
     */
    @Scheduled(fixedRate = 1000)
    public void sendTXAdmin() {
        try {
            ArrayList<TaiXiuAdminReportResponse> lstTaiXiuAdminReportObjs = new ArrayList<>();
            TaiXiuAdminReportObj obj = MapperUtils.mapper.readValue(cacheService.getValueStr(USER_TAI_XIU), TaiXiuAdminReportObj.class);
            TaiXiuAdminReportResponse response = new TaiXiuAdminReportResponse();
            response.setMoneyTai(obj.getMoneyTai());
            response.setMoneyXiu(obj.getMoneyXiu());
            response.setNguoiChoiBetTai(obj.getNumberUserRealTai());
            response.setNguoiChoiBetXiu(obj.getNumberUserRealXiu());
            response.setMoneyTaiFull(obj.getMoneyTaiFull());
            response.setMoneyXiuFull(obj.getMoneyXiuFull());
            response.setPhienId(obj.getPhienId());
            response.setContributors(obj.getContributors());
            response.setNumberUserAndBotBetTai(obj.getNumberUserAndBotBetTai());
            response.setNumberUserAndBotBetXiu(obj.getNumberUserAndBotBetXiu());
            response.setRealTime(obj.getRealTime());
            response.setBettingRound(obj.isBettingRound());
            if (obj.getLstMsg().size() > 10)
                obj.getLstMsg().subList(0, obj.getLstMsg().size() - 10).clear();
            response.setLstMsg(obj.getLstMsg());
            lstTaiXiuAdminReportObjs.add(response);
            TaiXiuReportResponse oResponse = new TaiXiuReportResponse("2", lstTaiXiuAdminReportObjs);
            String json = MapperUtils.mapper.writeValueAsString(oResponse);
            this.sendMessTXToAdmin(json);
            cacheService.removeKey(USER_TAI_XIU);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Loi senTXAdmin");

        }
    }

    /**
     * Send thông tin tài xỉu sang admin php
     *
     * @return
     */
    @Scheduled(fixedRate = 1000)
    public void sendTXMD5Admin() {
        try {
            ArrayList<TaiXiuAdminReportResponse> lstTaiXiuAdminReportObjs = new ArrayList<>();
            TaiXiuAdminReportObj obj = MapperUtils.mapper.readValue(cacheService.getValueStr(USER_TAI_XIU_MD5), TaiXiuAdminReportObj.class);
            TaiXiuAdminReportResponse response = new TaiXiuAdminReportResponse();
            response.setMoneyTai(obj.getMoneyTai());
            response.setMoneyXiu(obj.getMoneyXiu());
            response.setNguoiChoiBetTai(obj.getNumberUserRealTai());
            response.setNguoiChoiBetXiu(obj.getNumberUserRealXiu());
            response.setMoneyTaiFull(obj.getMoneyTaiFull());
            response.setMoneyXiuFull(obj.getMoneyXiuFull());
            response.setPhienId(obj.getPhienId());
            response.setContributors(obj.getContributors());
            response.setNumberUserAndBotBetTai(obj.getNumberUserAndBotBetTai());
            response.setNumberUserAndBotBetXiu(obj.getNumberUserAndBotBetXiu());
            response.setRealTime(obj.getRealTime());
            response.setBettingRound(obj.isBettingRound());
            response.setTaiXiuMd5Hash(obj.getTaiXiuMd5Hash());
            response.setTaiXiuPlainResult(obj.getTaiXiuPlainResult());
            if (obj.getLstMsg().size() > 10)
                obj.getLstMsg().subList(0, obj.getLstMsg().size() - 10).clear();
            response.setLstMsg(obj.getLstMsg());
            lstTaiXiuAdminReportObjs.add(response);
            TaiXiuReportResponse oResponse = new TaiXiuReportResponse("2", lstTaiXiuAdminReportObjs);
            String json = MapperUtils.mapper.writeValueAsString(oResponse);
            this.sendMessTXMd5ToAdmin(json);
            cacheService.removeKey(USER_TAI_XIU_MD5);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Loi senTXMD5Admin");

        }
    }


    private void sendMessToAdmin(String mess) {
        for (Session session : ServerGame.sessions) {
            session.sendText(mess);
        }
    }

    private void sendMessToAdminBauCua(String mess) {
        for (Session session : ServerBauCua.sessions) {
            session.sendText(mess);
        }
    }

    private void sendMessTXToAdmin(String mess) {
        for (Session session : ServerTXGame.sessions) {
            session.sendText(mess);
        }
    }

    private void sendMessTXMd5ToAdmin(String mess) {
        for (Session session : ServerTXMD5Game.sessions) {
            session.sendText(mess);
        }
    }

    /**
     * Send thông tin Cashoutbybank sang admin php
     *
     * @return
     */
    @Scheduled(fixedRate = 1000)
    public void sendCashoutbybankAdmin() {

        try {
            CashoutbybankAdminObj obj = MapperUtils.mapper.readValue(cacheService.getValueStr(CASHOUTBYBANK_ADMIN), CashoutbybankAdminObj.class);
            CashoutbybankReportResponse oResponse = new CashoutbybankReportResponse("2", obj);
            String json = MapperUtils.mapper.writeValueAsString(oResponse);
            this.sendMessCashoutbybankToAdmin(json);
            cacheService.removeKey(CASHOUTBYBANK_ADMIN);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Loi sendCashoutbybankAdmin");

        }

    }

    private void sendMessCashoutbybankToAdmin(String mess) {
        for (Session session : ServerCashoutbybankGame.sessions) {
            session.sendText(mess);
        }
        //todo : update lại cache
    }

    /**
     * Send thông tin sendCashoutbycardmanualAdmin sang admin php
     *
     * @return
     */
    @Scheduled(fixedRate = 1000)
    public void sendCashoutbycardmanualAdmin() {

        try {
            CashoutbycardmanualAdminObj obj = MapperUtils.mapper.readValue(cacheService.getValueStr(CASHOUTBYCARDMANUAL_ADMIN), CashoutbycardmanualAdminObj.class);
            CashoutbycardmanualReportResponse oResponse = new CashoutbycardmanualReportResponse("2", obj);
            String json = MapperUtils.mapper.writeValueAsString(oResponse);
            this.sendMessCashoutbycardmanualToAdmin(json);
            cacheService.removeKey(CASHOUTBYCARDMANUAL_ADMIN);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Loi sendCashoutbycardmanualAdmin");

        }

    }

    private void sendMessCashoutbycardmanualToAdmin(String mess) {
        for (Session session : ServerCashoutbycardmanualGame.sessions) {
            session.sendText(mess);
        }
        //todo : update lại cache
    }

    /**
     * Send thông tin sendRechargebybankAdmin sang admin php
     *
     * @return
     */
    @Scheduled(fixedRate = 1000)
    public void sendRechargebybankAdmin() {

        try {
            RechargebybankAdminObj obj = MapperUtils.mapper.readValue(cacheService.getValueStr(RECHARGEBYBANK_ADMIN), RechargebybankAdminObj.class);
            RechargebybankReportResponse oResponse = new RechargebybankReportResponse("2", obj);
            String json = MapperUtils.mapper.writeValueAsString(oResponse);
            this.sendMessRechargebybankToAdmin(json);
            cacheService.removeKey(RECHARGEBYBANK_ADMIN);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Loi sendRechargebybankAdmin");

        }

    }

    private void sendMessRechargebybankToAdmin(String mess) {
        for (Session session : ServerRechargebybankGame.sessions) {
            session.sendText(mess);
        }
        //todo : update lại cache
    }

    /**
     * Send thông tin sendRechargebycodepayAdmin sang admin php
     *
     * @return
     */
    @Scheduled(fixedRate = 1000)
    public void sendRechargebymomosunvinAdmin() {

        try {
            RechargebybankAdminObj obj = MapperUtils.mapper.readValue(cacheService.getValueStr(RECHARGEBYMOMOSUNVIN_ADMIN), RechargebybankAdminObj.class);
            RechargebybankReportResponse oResponse = new RechargebybankReportResponse("2", obj);
            String json = MapperUtils.mapper.writeValueAsString(oResponse);
            this.sendMessRechargebymomosunvinToAdmin(json);
            cacheService.removeKey(RECHARGEBYMOMOSUNVIN_ADMIN);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Loi RECHARGEBYMOMOSUNVIN_ADMIN");

        }

    }

    private void sendMessRechargebymomosunvinToAdmin(String mess) {
        for (Session session : ServerRechargebymomosunvinGame.sessions) {
            session.sendText(mess);
        }
        //todo : update lại cache
    }

    /**
     * Send thông tin sendRechargebyonepayAdmin sang admin php
     *
     * @return
     */
    @Scheduled(fixedRate = 1000)
    public void sendRechargebyonepayAdmin() {

        try {
            RechargebyonepayAdminObj obj = MapperUtils.mapper.readValue(cacheService.getValueStr(RECHARGEBYONEPAY_ADMIN), RechargebyonepayAdminObj.class);
            RechargebyonepayReportResponse oResponse = new RechargebyonepayReportResponse("2", obj);
            String json = MapperUtils.mapper.writeValueAsString(oResponse);
            this.sendMessRechargebyonepayToAdmin(json);
            cacheService.removeKey(RECHARGEBYONEPAY_ADMIN);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Loi sendRechargebyonepayAdmin");

        }

    }

    private void sendMessRechargebyonepayToAdmin(String mess) {
        for (Session session : ServerRechargebyonepayGame.sessions) {
            session.sendText(mess);
        }
        //todo : update lại cache
    }

    /**
     * Send thông tin sendRechargebymomoAdmin sang admin php
     *
     * @return
     */
    @Scheduled(fixedRate = 1000)
    public void sendRechargebymomoAdmin() {

        try {
            RechargebymomoAdminObj obj = MapperUtils.mapper.readValue(cacheService.getValueStr(RECHARGEBYMOMO_ADMIN), RechargebymomoAdminObj.class);
            RechargebymomoReportResponse oResponse = new RechargebymomoReportResponse("2", obj);
            String json = MapperUtils.mapper.writeValueAsString(oResponse);
            this.sendMessRechargebymomoToAdmin(json);
            cacheService.removeKey(RECHARGEBYMOMO_ADMIN);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Loi sendRechargebymomoAdmin");

        }

    }

    private void sendMessRechargebymomoToAdmin(String mess) {
        for (Session session : ServerRechargebymomoGame.sessions) {
            session.sendText(mess);
        }
        //todo : update lại cache
    }


    /**
     * Send thông tin sendRechargebyonepayotpAdmin sang admin php
     *
     * @return
     */
    @Scheduled(fixedRate = 500)
    public void sendRechargebyonepayotpAdmin() {

        try {
            RechargebyonepayotpAdminObj obj = MapperUtils.mapper.readValue(cacheService.getValueStr(RECHARGEBYONEPAYOTP_ADMIN), RechargebyonepayotpAdminObj.class);
            RechargebyonepayotpReportResponse oResponse = new RechargebyonepayotpReportResponse("2", obj);
            String json = MapperUtils.mapper.writeValueAsString(oResponse);
            this.sendMessRechargebyonepayotpToAdmin(json);
            cacheService.removeKey(RECHARGEBYONEPAYOTP_ADMIN);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Loi sendRechargebyonepayAdmin");

        }

    }

    private void sendMessRechargebyonepayotpToAdmin(String mess) {
        for (Session session : ServerRechargebyonepayotpGame.sessions) {
            session.sendText(mess);
        }
        //todo : update lại cache
    }

    /**
     * Send thông tin sendRechargebyautocardAdmin sang admin php
     *
     * @return
     */
    @Scheduled(fixedRate = 1000)
    public void sendRechargebyautocardAdmin() {

        try {
            RechargebyautocardAdminObj obj = MapperUtils.mapper.readValue(cacheService.getValueStr(RECHARGEBYAUTOCARD_ADMIN), RechargebyautocardAdminObj.class);
            RechargebyautocardReportResponse oResponse = new RechargebyautocardReportResponse("2", obj);
            String json = MapperUtils.mapper.writeValueAsString(oResponse);
            this.sendMessRechargebyautocardToAdmin(json);
            cacheService.removeKey(RECHARGEBYAUTOCARD_ADMIN);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Loi sendRechargebyautocardAdmin");

        }

    }

    private void sendMessRechargebyautocardToAdmin(String mess) {
        for (Session session : ServerRechargebyautocardGame.sessions) {
            session.sendText(mess);
        }
        //todo : update lại cache
    }

    /**
     * Send thông tin Notification sang admin php
     * @return
     */
    @Scheduled(fixedRate = 500)
    public void sendNotificationAdmin() {
        try {
            NotificationAdminObj obj =  MapperUtils.mapper.readValue(cacheService.getValueStr(NOTIFY_ADMIN), NotificationAdminObj.class);
            NotifyReportResponse oResponse = new NotifyReportResponse("2", obj);
            String json = MapperUtils.mapper.writeValueAsString(oResponse);
            this.sendMessNotificationToAdmin(json);
            cacheService.removeKey(NOTIFY_ADMIN);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Loi sendNotificationAdmin");

        }
    }

    private void sendMessNotificationToAdmin(String mess) {
        for (Session session : ServerNotifyGame.sessions) {
            session.sendText(mess);
        }
        //todo : update lại cache
    }

    /**
     * Send thông tin Eventaction sang admin php
     * @return
     */
    @Scheduled(fixedRate = 1000)
    public void sendEventactionAdmin() {
        try {
            EventactionAdminObj obj =  MapperUtils.mapper.readValue(cacheService.getValueStr(EVENTACTION_ADMIN), EventactionAdminObj.class);
            EventactionResponse oResponse = new EventactionResponse("2", obj);
            String json = MapperUtils.mapper.writeValueAsString(oResponse);
            this.sendMessEventactionToAdmin(json);
            cacheService.removeKey(EVENTACTION_ADMIN);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Loi sendNotificationAdmin");
        }
    }

    private void sendMessEventactionToAdmin(String mess) {
        for (Session session : ServerEventactionGame.sessions) {
            session.sendText(mess);
        }
        //todo : update lại cache
    }


}
