package game.scheduler;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import game.bean.MapperUtils;
import game.config.HttpCommon;
import game.entity.entitiesxocdia.UserBetModel;
import game.entity.entitytaixiu.TaiXiuAdmin;
import game.entity.entitytaixiu.TaiXiuAdminReportObj;
import game.entity.entitytaixiu.TaiXiuAdminReportResponse;
import game.entity.report.*;
import game.entity.response.*;
import game.exceptions.KeyNotFoundException;
import game.hazelcast.HazelcastClientFactory;
import game.models.baucuato2.BauCuaListUserResponse;
import game.models.baucuato2.BauCuaToReportResponse;
import game.models.baucuato2.BauCuaUserInfomation;
import game.models.minigame.TopWin;
import game.service.CacheService;
import game.service.impl.CacheServiceImpl;
import game.ws.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.yeauty.pojo.Session;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ScheduledTasks { // chay schedule lien tuc // cach nay chi dung cho nhung cai thay doi lien tuc nhu thoi gian cua game thoi ko nen dung cho nhung thang co event
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final String USER_TAI_XIU = "user_tai_xiu";
    private static final String SC_TAI_XIU = "SC_TAI_XIU";
    private static final String SC_TAI_XIU_MD5 = "SC_TAI_XIU_MD5";
    private static final String SC_TAI_XIU_KUBET = "SC_TAI_XIU_KUBET";
    private static final String USER_TAI_XIU_MD5 = "user_tai_xiu_md5";
    private static final String USER_TAI_XIU_KUBET= "user_tai_xiu_kubet";
    private static final String CASHOUTBYBANK_ADMIN = "cashoutbybank_admin";
    private static final String CASHOUTBYCARDMANUAL_ADMIN = "cashoutbycardmanual_admin";
    private static final String RECHARGEBYBANK_ADMIN = "rechargebybank_admin";
    private static final String RECHARGEBYONEPAY_ADMIN = "rechargebyonepay_admin";
    private static final String RECHARGEBYONEPAYOTP_ADMIN = "rechargebyonepayotp_admin";
    private static final String RECHARGEBYMOMO_ADMIN = "rechargebymomo_admin";
    private static final String RECHARGEBYMOMOSUNVIN_ADMIN = "rechargebymomosunvin_admin";
    private static final String CASHOUTBYMOMOSUNVIN_ADMIN = "cashoutbymomo_admin";
    private static final String RECHARGEBYAUTOCARD_ADMIN = "rechargebyautocard_admin";
    private static final String NOTIFY_ADMIN = "notify_admin";
    private static final String EVENTACTION_ADMIN = "eventaction_admin";
    HazelcastInstance cache;
    IMap<String, UserCacheModel> userMap;

    @Autowired
    CacheService cacheService;


    @Scheduled(fixedRate = 1000)
    public void getCacheXocDia() {
        try {

            String timmer = cacheService.getValueStr("XocDia_Flag_Time");
            int iTimer = Integer.parseInt(timmer);
            int gameState = cacheService.getValueInt("XocDia_Flag_GameState");
            String isBetting = cacheService.getValueStr("XocDia_Flag_betting");
            String session = cacheService.getValueStr("XocDia_Flag_session");

            XocDiaGameStatus xocDiaGameStatus = new XocDiaGameStatus("1", String.valueOf(iTimer >= 11 ? iTimer - 11 : iTimer), String.valueOf(gameState), isBetting, session);
            String json = MapperUtils.mapper.writeValueAsString(xocDiaGameStatus);
            this.sendMessToAdmin(json);

//            cacheService.removeKey("XocDia_Flag_Time");
//            cacheService.removeKey("XocDia_Flag_GameState");
//            cacheService.removeKey("XocDia_Flag_betting");
//            cacheService.removeKey("XocDia_Flag_session");
        } catch (KeyNotFoundException | JsonProcessingException ignored) {
        }
    }

    //   @Scheduled(fixedRate = 1000)

    @Scheduled(fixedRate = 800)
    public void sendStateUser() {
        try {

            HashMap<String, String> mapState = (HashMap<String, String>) cacheService.getObject("List_UserState_Slot");
            ArrayList<String> listState = new ArrayList<>(mapState.values());
            StateGameResponse response = new StateGameResponse("2", listState);
            String json = MapperUtils.mapper.writeValueAsString(response);
            this.sendStateToAdmin(json);

        } catch (KeyNotFoundException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void sendStateToAdmin(String mess) {
        for (Session session : ServerStateGame.sessions) {
            session.sendText(mess);
        }
    }

    @Scheduled(fixedRate = 1000)
    public void executeBauCuaInfo() {
        try {

            BauCuaToReportResponse response = new BauCuaToReportResponse(true, "0");
            Map<Integer, Long> mapBotReportBet = (Map<Integer, Long>) cacheService.getObject("mapBotReportBet");
            Map<Integer, Long> mapReportBet = (Map<Integer, Long>) cacheService.getObject("mapReportBet");
            long referentId = cacheService.getValueInt("BauCuareferenceId");
            String remainTime = cacheService.getValueStr("BauCuaRemainTime");
            int iRemainTime = Integer.parseInt(remainTime);
            boolean bettingState = (boolean) cacheService.getObject("bettingStateBauCua");
            response.setMapReportBet(mapReportBet);
            response.setMapBotReportBet(mapBotReportBet);
            response.setBetting(bettingState);
            response.setReferenceId(referentId);
            response.setRemainingTime(String.valueOf(iRemainTime >= 13 ? iRemainTime - 13 : iRemainTime));
            this.sendMessToAdminBauCua(response.toJson());
        } catch (KeyNotFoundException e) {
            e.printStackTrace();
        }


    }

    @Scheduled(fixedRate = 1000)
    public void executeListUser() {

        BauCuaListUserResponse response = new BauCuaListUserResponse(true, "1");
        try {
            ArrayList<BauCuaUserInfomation> list = MapperUtils.mapper.readValue(cacheService.getValueStr("baucualist"), new TypeReference<ArrayList<BauCuaUserInfomation>>() {
            });

            response.setListBauCuaInformation(getUserBauCua(list, "BauCua"));
            this.sendMessToAdminBauCua(response.toJson());

        } catch (Exception e) {
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
            // Thông tin phiên
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
            response.setContributors(getUserTX(obj.getContributors(), "TaiXiu"));
            response.setNumberUserAndBotBetTai(obj.getNumberUserAndBotBetTai());
            response.setNumberUserAndBotBetXiu(obj.getNumberUserAndBotBetXiu());
            response.setRealTime(obj.getRealTime());
            response.setBettingRound(obj.isBettingRound());
            response.setDice1(obj.getDice1());
            response.setDice2(obj.getDice2());
            response.setDice3(obj.getDice3());
            response.setSessionResult(obj.getSessionResult());
            if (obj.getLstMsg().size() > 10)
                obj.getLstMsg().subList(0, obj.getLstMsg().size() - 10).clear();
            response.setLstMsg(obj.getLstMsg());
            lstTaiXiuAdminReportObjs.add(response);

            // thông tin soi cầu
            String sc = null;
            try {
                sc = cacheService.getValueStr(SC_TAI_XIU);
            } catch (Exception ex) {
            }

            TaiXiuReportResponse oResponse = new TaiXiuReportResponse("2", lstTaiXiuAdminReportObjs, sc);

            String json = MapperUtils.mapper.writeValueAsString(oResponse);
            this.sendMessTXToAdmin(json);

            cacheService.removeKey(USER_TAI_XIU);
        } catch (Exception e) {
            e.printStackTrace();
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
            // thông tin phiên
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
            response.setContributors(getUserTX(obj.getContributors(), "TaiXiuMd5"));
            response.setNumberUserAndBotBetTai(obj.getNumberUserAndBotBetTai());
            response.setNumberUserAndBotBetXiu(obj.getNumberUserAndBotBetXiu());
            response.setRealTime(obj.getRealTime());
            response.setBettingRound(obj.isBettingRound());
            response.setTaiXiuMd5Hash(obj.getTaiXiuMd5Hash());
            response.setTaiXiuPlainResult(obj.getTaiXiuPlainResult());
            response.setDice1(obj.getDice1());
            response.setDice2(obj.getDice2());
            response.setDice3(obj.getDice3());
            response.setSessionResult(obj.getSessionResult());
            if (obj.getLstMsg().size() > 10)
                obj.getLstMsg().subList(0, obj.getLstMsg().size() - 10).clear();
            response.setLstMsg(obj.getLstMsg());
            lstTaiXiuAdminReportObjs.add(response);

            // thông tin soi cầu
            String sc = null;
            try {
                sc = cacheService.getValueStr(SC_TAI_XIU_MD5);
            } catch (Exception ex) {
            }
            TaiXiuReportResponse oResponse = new TaiXiuReportResponse("2", lstTaiXiuAdminReportObjs, sc);

            String json = MapperUtils.mapper.writeValueAsString(oResponse);
            this.sendMessTXMd5ToAdmin(json);
            cacheService.removeKey(USER_TAI_XIU_MD5);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * Send thông tin tài xỉu sang admin php
     *
     * @return
     */
    @Scheduled(fixedRate = 1000)
    public void sendTXKUBETAdmin() {
        try {
            // thông tin phiên
            ArrayList<TaiXiuAdminReportResponse> lstTaiXiuAdminReportObjs = new ArrayList<>();
            TaiXiuAdminReportObj obj = MapperUtils.mapper.readValue(cacheService.getValueStr(USER_TAI_XIU_KUBET), TaiXiuAdminReportObj.class);
            TaiXiuAdminReportResponse response = new TaiXiuAdminReportResponse();
            response.setMoneyTai(obj.getMoneyTai());
            response.setMoneyXiu(obj.getMoneyXiu());
            response.setMoneyChan(obj.getMoneyChan());
            response.setMoneyLe(obj.getMoneyLe());
            response.setNguoiChoiBetTai(obj.getNumberUserRealTai());
            response.setNguoiChoiBetXiu(obj.getNumberUserRealXiu());
            response.setNguoiChoiBetChan(obj.getNumberUserRealChan());
            response.setNguoiChoiBetLe(obj.getNumberUserRealLe());
            response.setMoneyTaiFull(obj.getMoneyTaiFull());
            response.setMoneyXiuFull(obj.getMoneyXiuFull());
            response.setMoneyChanFull(obj.getMoneyChanFull());
            response.setMoneyLeFull(obj.getMoneyLeFull());
            response.setPhienId(obj.getPhienId());
            response.setContributors(getUserTX(obj.getContributors(), "TaiXiuKubet"));
            response.setNumberUserAndBotBetTai(obj.getNumberUserAndBotBetTai());
            response.setNumberUserAndBotBetXiu(obj.getNumberUserAndBotBetXiu());
            response.setNumberUserAndBotBetChan(obj.getNumberUserAndBotBetChan());
            response.setNumberUserAndBotBetLe(obj.getNumberUserAndBotBetLe());
            response.setRealTime(obj.getRealTime());
            response.setBettingRound(obj.isBettingRound());
            response.setTaiXiuMd5Hash(obj.getTaiXiuMd5Hash());
            response.setTaiXiuPlainResult(obj.getTaiXiuPlainResult());
            response.setDice1(obj.getDice1());
            response.setDice2(obj.getDice2());
            response.setDice3(obj.getDice3());
            response.setSessionResult(obj.getSessionResult());
            if (obj.getLstMsg().size() > 10)
                obj.getLstMsg().subList(0, obj.getLstMsg().size() - 10).clear();
            response.setLstMsg(obj.getLstMsg());
            lstTaiXiuAdminReportObjs.add(response);

            // thông tin soi cầu
            String sc = null;
            try {
                sc = cacheService.getValueStr(SC_TAI_XIU_KUBET);
            } catch (Exception ex) {
            }
            TaiXiuReportResponse oResponse = new TaiXiuReportResponse("2", lstTaiXiuAdminReportObjs, sc);

            String json = MapperUtils.mapper.writeValueAsString(oResponse);
            this.sendMessTXKubetToAdmin(json);
            cacheService.removeKey(SC_TAI_XIU_KUBET);
        } catch (Exception e) {
            e.printStackTrace();

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

    private void sendMessTXKubetToAdmin(String mess) {
        for (Session session : ServerTXKUBETGame.sessions) {
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
        } catch (KeyNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
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
        } catch (KeyNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
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
        } catch (KeyNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
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
        } catch (KeyNotFoundException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Scheduled(fixedRate = 1000)
    public void sendCashOutByMomoAdmin() {

        try {
            UserWithdrawMomo obj = MapperUtils.mapper.readValue(cacheService.getValueStr(CASHOUTBYMOMOSUNVIN_ADMIN), UserWithdrawMomo.class);
            RechargebymomoReportResponseExt oResponse = new RechargebymomoReportResponseExt("2", obj);
            String json = MapperUtils.mapper.writeValueAsString(oResponse);
            this.sendMessCashoutbymomosunvinToAdmin(json);
            cacheService.removeKey(CASHOUTBYMOMOSUNVIN_ADMIN);
        } catch (KeyNotFoundException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendMessRechargebymomosunvinToAdmin(String mess) {
        for (Session session : ServerRechargebymomosunvinGame.sessions) {
            session.sendText(mess);
        }
        //todo : update lại cache
    }

    private void sendMessCashoutbymomosunvinToAdmin(String mess) {
        for (Session session : ServerCashoutbymomosunvinGame.sessions) {
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
        } catch (KeyNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
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
        } catch (KeyNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
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
        } catch (KeyNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
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
        } catch (KeyNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
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
     *
     * @return
     */
//    @Scheduled(fixedRate = 500)
//    public void sendNotificationAdmin() {
//        try {
//            NotificationAdminObj obj = MapperUtils.mapper.readValue(cacheService.getValueStr(NOTIFY_ADMIN), NotificationAdminObj.class);
//            NotifyReportResponse oResponse = new NotifyReportResponse("2", obj);
//            String json = MapperUtils.mapper.writeValueAsString(oResponse);
//            this.sendMessNotificationToAdmin(json);
//            cacheService.removeKey(NOTIFY_ADMIN);
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Loi sendNotificationAdmin");
//
//        }
//    }

//    private void sendMessNotificationToAdmin(String mess) {
//        for (Session session : ServerNotifyGame.sessions) {
//            session.sendText(mess);
//        }
//        //todo : update lại cache
//    }

    /**
     * Send thông tin Eventaction sang admin php
     *
     * @return
     */
//    @Scheduled(fixedRate = 1000)
//    public void sendEventactionAdmin() {
//        try {
//            EventactionAdminObj obj = MapperUtils.mapper.readValue(cacheService.getValueStr(EVENTACTION_ADMIN), EventactionAdminObj.class);
//            EventactionResponse oResponse = new EventactionResponse("2", obj);
//            String json = MapperUtils.mapper.writeValueAsString(oResponse);
//            this.sendMessEventactionToAdmin(json);
//            cacheService.removeKey(EVENTACTION_ADMIN);
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Loi sendNotificationAdmin");
//        }
//    }
    private void sendMessEventactionToAdmin(String mess) {
        for (Session session : ServerEventactionGame.sessions) {
            session.sendText(mess);
        }
        //todo : update lại cache
    }

    public List<TaiXiuAdmin> getUserTX(List<TaiXiuAdmin> userList, String boardName) {
        if (userList.isEmpty()) {
            return userList;
        }
        try {
            CacheService service = new CacheServiceImpl();
            String host = service.getValueStr("url_leader_board");
            final String BASE_URL = "http://" + host + ":8087/leaderboard/get_by_name";
            String users = convertListToString(userList.stream().map(TaiXiuAdmin::getUsername).collect(Collectors.toList()));
            String typeDate = "DAY_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String url = String.format("%s?boardName=%s_%s&users=%s", BASE_URL, boardName, typeDate, users);

            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder().build();
            Request request = new Request.Builder()
                    .url(url)
                    .method("GET", null)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.body() != null) {
                List<TopWin> topWins = getTopWin(response.body().string());
                cache = HazelcastClientFactory.getInstance();
                for (TopWin topWin : topWins) {
                    userList.stream()
                            .filter(taiXiuAdmin -> taiXiuAdmin.getUsername().equals(topWin.getUsername()))
                            .findFirst()
                            .ifPresent(taiXiuAdmin -> {
                                taiXiuAdmin.setReportMoneyToday(topWin.getMoney());
                                if (cache != null) {
                                    userMap = cache.getMap("users");
                                    UserCacheModel user = userMap.get(topWin.getUsername());
                                    if (user != null) {
                                        taiXiuAdmin.setTotalMoney(user.getVinTotal());
                                    }
                                }
                            });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userList;
    }

    public List<BauCuaUserInfomation> getUserBauCua(List<BauCuaUserInfomation> userList, String boardName) {
        if (userList.isEmpty()) {
            return userList;
        }
        try {
            CacheService service = new CacheServiceImpl();
            String host = service.getValueStr("url_leader_board");
            final String BASE_URL = "http://" + host + ":8087/leaderboard/get_by_name";
            String users = convertListToString(userList.stream().map(BauCuaUserInfomation::getUsername).collect(Collectors.toList()));
            String typeDate = "DAY_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String url = String.format("%s?boardName=%s_%s&users=%s", BASE_URL, boardName, typeDate, users);

            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder().build();
            Request request = new Request.Builder()
                    .url(url)
                    .method("GET", null)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.body() != null) {
                List<TopWin> topWins = getTopWin(response.body().string());

                for (TopWin topWin : topWins) {
                    userList.stream()
                            .filter(taiXiuAdmin -> taiXiuAdmin.getUsername().equals(topWin.getUsername()))
                            .findFirst()
                            .ifPresent(taiXiuAdmin -> taiXiuAdmin.setReportMoneyToday(topWin.getMoney()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userList.stream().filter(bauCuaUserInfomation -> bauCuaUserInfomation.getTotalBet() > 0)
                .collect(Collectors.toList());
    }

    public List<UserBetModel> getUserXD(List<UserBetModel> userList, String boardName) {
//        System.out.println("start get user info xd");
        if (userList.isEmpty()) {
            return userList;
        }
        try {
            CacheService service = new CacheServiceImpl();
            String host = "";
            try {
                host = service.getValueStr("url_leader_board");
            } catch (Exception e) {
                host = "localhost";
            }
            final String BASE_URL = "http://" + host + ":8087/leaderboard/get_by_name";
            String users = convertListToString(userList.stream().map(UserBetModel::getNickname).collect(Collectors.toList()));
            String typeDate = "DAY_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String url = String.format("%s?boardName=%s_%s&users=%s", BASE_URL, boardName, typeDate, users);

            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder().build();
            Request request = new Request.Builder()
                    .url(url)
                    .method("GET", null)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.body() != null) {
                List<TopWin> topWins = getTopWin(response.body().string());
                cache = HazelcastClientFactory.getInstance();
                for (TopWin topWin : topWins) {
                    userList.stream()
                            .filter(taiXiuAdmin -> taiXiuAdmin.getNickname().equals(topWin.getUsername()))
                            .findFirst()
                            .ifPresent(taiXiuAdmin -> {
                                taiXiuAdmin.setTotalProfit(topWin.getMoney());
                                if (cache != null) {
                                    userMap = cache.getMap("users");
                                    UserCacheModel user = userMap.get(topWin.getUsername());
                                    if (user != null) {
                                        taiXiuAdmin.setTotalMoney(user.getVinTotal());
                                    }
                                }
                            });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userList;
    }

    private static String convertListToString(List<String> userList) {
        return String.join(",", userList);
    }

    public List<TopWin> getTopWin(String response) {
        List<TopWin> topWins = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray dataArray = jsonObject.getJSONArray("data");

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject dataObject = dataArray.getJSONObject(i);
                TopWin topWin = new TopWin();
                topWin.setUsername(dataObject.getString("username"));
                topWin.setTotalMoneyOnGame(dataObject.getInt("score"));
                topWin.setMoney(dataObject.getInt("score"));
                topWins.add(topWin);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return topWins;
    }
}
