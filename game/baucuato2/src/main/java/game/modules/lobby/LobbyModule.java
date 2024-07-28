/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  bitzero.server.BitZeroServer
 *  bitzero.server.core.BZEventParam
 *  bitzero.server.core.BZEventType
 *  bitzero.server.core.IBZEvent
 *  bitzero.server.core.IBZEventListener
 *  bitzero.server.core.IBZEventParam
 *  bitzero.server.core.IBZEventType
 *  bitzero.server.entities.User
 *  bitzero.server.exceptions.BZException
 *  bitzero.server.extensions.BZExtension
 *  bitzero.server.extensions.BaseClientRequestHandler
 *  bitzero.server.extensions.data.BaseMsg
 *  bitzero.server.extensions.data.DataCmd
 *  bitzero.server.util.TaskScheduler
 *  bitzero.util.common.business.Debug
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IMap
 *  com.vinplay.dal.service.impl.CacheServiceImpl
 *  com.vinplay.dichvuthe.response.CashoutResponse
 *  com.vinplay.dichvuthe.response.I2BResponse
 *  com.vinplay.dichvuthe.response.RechargeIAPResponse
 *  com.vinplay.dichvuthe.response.RechargeResponse
 *  com.vinplay.dichvuthe.response.SoftpinResponse
 *  com.vinplay.dichvuthe.service.CashOutService
 *  com.vinplay.dichvuthe.service.RechargeService
 *  com.vinplay.dichvuthe.service.impl.CashOutServiceImpl
 *  com.vinplay.dichvuthe.service.impl.RechargeServiceImpl
 *  com.vinplay.dichvuthe.utils.DvtUtils
 *  com.vinplay.usercore.entities.TransferMoneyResponse
 *  com.vinplay.usercore.entities.VippointResponse
 *  com.vinplay.usercore.service.GiftCodeService
 *  com.vinplay.usercore.service.OtpService
 *  com.vinplay.usercore.service.SecurityService
 *  com.vinplay.usercore.service.UserService
 *  com.vinplay.usercore.service.VippointService
 *  com.vinplay.usercore.service.impl.GiftCodeServiceImpl
 *  com.vinplay.usercore.service.impl.LuckyServiceImpl
 *  com.vinplay.usercore.service.impl.OtpServiceImpl
 *  com.vinplay.usercore.service.impl.SecurityServiceImpl
 *  com.vinplay.usercore.service.impl.UserServiceImpl
 *  com.vinplay.usercore.service.impl.VippointServiceImpl
 *  com.vinplay.usercore.utils.LuckyUtils
 *  com.vinplay.usercore.utils.VippointUtils
 *  com.vinplay.vbee.common.enums.Games
 *  com.vinplay.vbee.common.enums.PhoneCardType
 *  com.vinplay.vbee.common.enums.Platform
 *  com.vinplay.vbee.common.enums.ProviderType
 *  com.vinplay.vbee.common.hazelcast.HazelcastClientFactory
 *  com.vinplay.vbee.common.models.cache.UserCacheModel
 *  com.vinplay.vbee.common.response.GiftCodeUpdateResponse
 *  com.vinplay.vbee.common.response.LuckyResponse
 *  com.vinplay.vbee.common.response.LuckyVipResponse
 *  com.vinplay.vbee.common.response.MoneyResponse
 *  com.vinplay.vbee.common.response.NapXuResponse
 *  com.vinplay.vbee.common.utils.DateTimeUtils
 *  com.vinplay.vbee.common.utils.VinPlayUtils
 *  org.json.JSONException
 */
package game.modules.lobby;

import bitzero.server.BitZeroServer;
import bitzero.server.core.BZEventParam;
import bitzero.server.core.BZEventType;
import bitzero.server.core.IBZEvent;
import bitzero.server.core.IBZEventListener;
import bitzero.server.core.IBZEventParam;
import bitzero.server.core.IBZEventType;
import bitzero.server.entities.User;
import bitzero.server.exceptions.BZException;
import bitzero.server.extensions.BaseClientRequestHandler;
import bitzero.server.extensions.data.BaseMsg;
import bitzero.server.extensions.data.DataCmd;
import bitzero.util.ExtensionUtility;
import bitzero.util.common.business.Debug;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.dal.dao.LogMoneyUserDao;
import com.vinplay.dal.dao.impl.LogMoneyUserDaoImpl;
import com.vinplay.dal.service.impl.AgentServiceImpl;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.dichvuthe.dao.RechargeDao;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.response.CashoutResponse;
import com.vinplay.dichvuthe.response.I2BResponse;
import com.vinplay.dichvuthe.response.RechargeIAPResponse;
import com.vinplay.dichvuthe.response.RechargeResponse;
import com.vinplay.dichvuthe.response.SoftpinResponse;
import com.vinplay.dichvuthe.service.CashOutService;
import com.vinplay.dichvuthe.service.RechargeService;
import com.vinplay.dichvuthe.service.impl.AlertServiceImpl;
import com.vinplay.dichvuthe.service.impl.CashOutServiceImpl;
import com.vinplay.dichvuthe.service.impl.RechargeServiceImpl;
import com.vinplay.dichvuthe.utils.DvtUtils;
import com.vinplay.payment.entities.UserWithdraw;
import com.vinplay.payment.entities.UserWithdrawMomo;
import com.vinplay.usercore.dao.GiftCodeDAO;
import com.vinplay.usercore.dao.impl.GiftCodeDAOImpl;
import com.vinplay.usercore.dao.impl.OtpDaoImpl;
import com.vinplay.usercore.dao.impl.UserDaoImpl;
import com.vinplay.usercore.entities.TransferMoneyResponse;
import com.vinplay.usercore.entities.VippointResponse;
import com.vinplay.usercore.service.GiftCodeService;
import com.vinplay.usercore.service.OtpService;
import com.vinplay.usercore.service.SecurityService;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.VippointService;
import com.vinplay.usercore.service.impl.GiftCodeServiceImpl;
import com.vinplay.usercore.service.impl.LuckyServiceImpl;
import com.vinplay.usercore.service.impl.OtpServiceImpl;
import com.vinplay.usercore.service.impl.SecurityServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.service.impl.VippointServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.usercore.utils.LuckyUtils;
import com.vinplay.usercore.utils.PartnerConfig;
import com.vinplay.usercore.utils.VippointUtils;
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.enums.PhoneCardType;
import com.vinplay.vbee.common.enums.Platform;
import com.vinplay.vbee.common.enums.ProviderType;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.response.*;
import com.vinplay.vbee.common.utils.DateTimeUtils;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import game.entities.QuotaResponse;
import game.modules.lobby.cmd.rev.*;
import game.modules.lobby.cmd.send.*;
import game.modules.minigame.cmd.MiniGameCMD;
import game.modules.minigame.utils.MiniGameUtils;
import game.utils.ConfigGame;
import game.utils.HuVangConfig;
import game.utils.ServerUtil;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.json.JSONException;

public class LobbyModule
extends BaseClientRequestHandler {
    private static final String CURRENT_COMMAND = "cmd";
    private static final String CURRENT_OBJECT_COMMAND = "obj_cmd";
    private static final String FORCE_CHECK_OTP = "force_check_otp";
    private static final String UPDATE_NEW_MOBILE = "update_new_mobile";
    private GameLoopTask gameLoopTask = new GameLoopTask();
    private UpdateCardTransactionTask cardTransactionTask = new UpdateCardTransactionTask();
    private UserService userService = new UserServiceImpl();
    private RechargeService rechargeService = new RechargeServiceImpl();
    private RechargeDao rechargeDAO = new RechargeDaoImpl();
    private GiftCodeService gfService = new GiftCodeServiceImpl();
    private CashOutService cashOutService = new CashOutServiceImpl();
    private OtpService otpService = new OtpServiceImpl();
    private SecurityService securityService = new SecurityServiceImpl();
    private VippointService vpService = new VippointServiceImpl();
    private Set<User> usersSubJackpot = new HashSet<User>();
    private long countUpdateJackpot = 0L;
    private Date eventTimeStart = null;
    private Date eventTimeEnd = null;
    private EventStartTask eventStartTask = new EventStartTask();
    private EventEndTask eventEndTask = new EventEndTask();
    private EventLuckyStartTask eventLuckyStartTask = new EventLuckyStartTask();
    private EventLuckyEndTask eventLuckyEndTask = new EventLuckyEndTask();
    private long timeLucky;
    private EventUnluckyTask eventUnluckyTask = new EventUnluckyTask();
    private EventluckyTask eventLuckyTask = new EventluckyTask();
    private EventX2EndTask eventX2EndTask = new EventX2EndTask();
    private final Runnable slotDailyTask = new SlotDailyTask();
    private static final Logger logger = Logger.getLogger((String)"recharge");

    public void init() {
        super.init();
        this.getParentExtension().addEventListener((IBZEventType)BZEventType.USER_DISCONNECT, (IBZEventListener)this);
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate((Runnable)this.gameLoopTask, 10, 1, TimeUnit.SECONDS);
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate((Runnable)this.cardTransactionTask, 30, 30, TimeUnit.SECONDS);
        try {
            this.initVP();
            MongoDBConnectionFactory.init();
        }
        catch (Exception e) {
            Debug.trace((Object)("init vippoint event error " + e));
        }
        try {
            LuckyUtils.initSlotMap();
            DvtUtils.initDVT((boolean)true);
        }
        catch (Exception e) {
            Debug.trace((Object)("init slot free error " + e));
        }
        try {
            PartnerConfig.ReadConfig();
        }
        catch (Exception e) {
            Debug.trace((Object)("init partnerconfig event error " + e));
        }        
        long currentTime = System.currentTimeMillis() / 1000L;
        long endToday = DateTimeUtils.getEndTimeToDayAsLong() / 1000L;
        int n = (int)(endToday - currentTime);
        BitZeroServer.getInstance().getTaskScheduler().schedule(this.slotDailyTask, n + 5, TimeUnit.SECONDS);
        logger.debug((Object)("LobbyModule Init"));
    }

    public void handleClientRequest(User user, DataCmd dataCmd) {
        switch (dataCmd.getId()) {
            case 20050: {
                this.getInfo(user, dataCmd);
                break;
            }
            case 20051: {
                this.getMoneyUse(user, dataCmd);
                break;
            }
            case 20000: {
                this.doiPass(user, dataCmd);
                break;
            }
            case 20001: {
                this.doiVippoint(user, dataCmd);
                break;
            }
            case 20002: {
                this.updateUserInfo(user, dataCmd);
                break;
            }
            case 20003: {
                this.updateEmail(user, dataCmd);
                break;
            }
            case 20004: {
                this.updateMobile(user, dataCmd);
                Debug.trace(dataCmd.readString());
                break;
            }
            case 20005: {
                this.activeEmail(user, dataCmd);
                break;
            }
            case 20006: {
                this.activeMobile(user, dataCmd);
                break;
            }
            case 20007: {
                this.updateNewMobile(user, dataCmd);
                break;
            }
            case 20008: {
                this.loginOtp(user, dataCmd);
                break;
            }
            case 20009: {
                this.ketSat(user, dataCmd);
                break;
            }
            case 20010: {
                this.gameConfig(user, dataCmd);
                break;
            }
            case 20011: {
                this.napXu(user, dataCmd);
                break;
            }
            case 20012: {

                this.napTheDienThoai(user, dataCmd);
                break;
            }
            case 20045: {
                this.napVinCard(user, dataCmd);
                break;
            }
            case 20046: {
                this.napMegaCard(user, dataCmd);
                break;
            }
            case 20013: {
                this.napQuaNganHang(user, dataCmd);
                break;
            }
            case 20014: {
                this.chuyenKhoan(user, dataCmd);
                break;
            }
            case 20015: {
                this.muaMaThe(user, dataCmd);
                break;
            }
            case 20016: {
                this.napTienDienThoai(user, dataCmd);
                break;
            }
            case 20017: {
                this.giftCode(user, dataCmd);
                break;
            }
            case 20018: {
                this.checkUser(user, dataCmd);
                break;
            }
            case 20019: {
                this.checkOtp(user, dataCmd);
                break;
            }
            case 20220: {
                this.sendOtp(user, dataCmd);
                break;
            }
            case 20102: {
                this.subscribeJackPot(user);
                break;
            }
            case 20103: {
                this.unSubscribeJackPot(user);
                break;
            }
            case 20104: {
                this.updateHuVang(user);
                break;
            }
            case 20037: {
                this.checkIAP(user, dataCmd);
                break;
            }
            case 20038: {
                this.resultIAP(user, dataCmd);
                break;
            }
            case 20039: {
                this.getEventInfo(user, dataCmd);
                break;
            }
            case 20040: {
                this.requestApiOTP(user, dataCmd);
                break;
            }
            case 20041: {
                this.confirmApiOTP(user, dataCmd);
                break;
            }
            case 20042: {
                this.playVQMM(user);
                break;
            }
            case 20043: {
                this.getVQVip(user);
                break;
            }
            case 20044: {
                this.playVQVip(user);
            }
            case 20200: {
                this.napTheDienThoaiGachthe(user, dataCmd);
                break;
            }
            case MiniGameCMD
                    .CMD_DEPOSIT_BANK_MANUAL : {
                this.depositBankManual(user, dataCmd);
                break;
            }
            case MiniGameCMD
                    .CMD_DEPOSIT_MOMO_MANUAL : {
                this.depositMomoManual(user, dataCmd);
                break;
            }
            case MiniGameCMD.CMD_WITHDRAW_CARD_MANUAL: {
                this.cashoutCard(user, dataCmd);
                break;
            }
            case MiniGameCMD.CMD_WITHDRAW_BANK_MANUAL: {
                this.cashoutBank(user, dataCmd);
                break;
            }
            case MiniGameCMD.CMD_WITHDRAW_MOMO_MANUAL: {
                this.cashoutMomo(user, dataCmd);
                break;
            }

        }
    }

    private void cashoutMomo(User user, DataCmd dataCmd){
        try{
            CashoutMomoCmd cmd = new CashoutMomoCmd(dataCmd);
            String nickname = user.getName();
            UserWithdrawMomo userWithdraw = new UserWithdrawMomo(nickname, cmd.Amount, cmd.PhoneNumber);
            BaseResponseModel res = this.userService.UpdateMoneyWhenWithdrawMomo(userWithdraw);
            ResultCashoutMomo msg = new ResultCashoutMomo();
            int errorCode = Integer.parseInt(res.getErrorCode());
            msg.Error = (byte)errorCode;
            msg.currentMoney = errorCode == 0 ? this.userService.getCurrentMoneyUserCache(nickname, "vin") : 0 ;
            this.send((BaseMsg) msg, user);
        }catch (Exception e){
            logger.debug(e);
        }
    }

    private void cashoutBank(User user, DataCmd dataCmd){
        try{
            CashoutBankCmd cmd = new CashoutBankCmd(dataCmd);
            String nickname = user.getName();
            UserWithdraw userWithdraw = new UserWithdraw(nickname, cmd.Amount, cmd.BankNumber, cmd.BankAccountName, cmd.BankName);
            BaseResponseModel res = this.userService.UpdateMoneyWhenWithdrawBank(userWithdraw);
            ResultCashoutBank msg = new ResultCashoutBank();
            int errorCode = Integer.parseInt(res.getErrorCode());
            msg.Error = (byte)errorCode;
            msg.currentMoney = errorCode == 0 ? this.userService.getCurrentMoneyUserCache(nickname, "vin") : 0;
            this.send((BaseMsg) msg, user);
        }catch (Exception e){
            logger.debug(e);
        }
    }

    private void cashoutCard(User user, DataCmd dataCmd){
        try{
            CashoutCardCmd cmd = new CashoutCardCmd(dataCmd);
            String nickname = user.getName();
            CashOutService sv = new CashOutServiceImpl();
            SoftpinResponse res = sv.cashOutByCardHungHa(nickname, cmd.TelcoId, cmd.Amount, cmd.Quantity);
            CashoutCardMsg msg = new CashoutCardMsg();
            msg.Error = (byte)res.getCode();
            msg.CurrentMoney = res.getCurrentMoney();
            msg.ListCard = res.getSoftpin();
            this.send((BaseMsg)msg, user);


        }catch (Exception e){
            logger.error(e);
        }
    }

    private void depositMomoManual(User user, DataCmd dataCmd){
        try{
            DepositMomoManualCmd cmd = new DepositMomoManualCmd(dataCmd);
            String nickname = user.getName();
            RechargeResponse res = this.rechargeService.rechargeByMomoManual(nickname, cmd.Amount, cmd.SendFrom);
            DepositMomoManualMsg msg = new DepositMomoManualMsg();
            if(res != null){
                msg.Error = (byte)res.getCode();
            }else{
                msg.Error = (byte)1;
            }
            this.send((BaseMsg)msg, user);
        }catch (Exception e){
            logger.error(e);
        }
    }

    private void depositBankManual(User user, DataCmd dataCmd){
//        try{
//            DepositBankManualCmd cmd = new DepositBankManualCmd(dataCmd);
//            String nickname = user.getName();
//            RechargeResponse res = this.rechargeService.rechargeByBankManual(nickname, cmd.amount, cmd.bankNumber,cmd.);
//            DepositBankManualMsg msg = new DepositBankManualMsg();
//            if(res != null){
//                msg.Error = (byte)res.getCode();
//            }else{
//                msg.Error = (byte)1;
//            }
//            this.send((BaseMsg)msg, user);
//        }catch (Exception e){
//            logger.error(e);
//        }

    }

    private void playVQMM(User user) {
        LuckyServiceImpl service = new LuckyServiceImpl();
        LuckyResponse response = service.getResultLuckyRotation(user.getName(), user.getIpAddress());
        VQMMMsg msg = new VQMMMsg();
        switch (response.getErrorCode()) {
            case "0": {
                msg.Error = 0;
                break;
            }
            case "3001": {
                msg.Error = 2;
                break;
            }
            case "3002": {
                msg.Error = 3;
                break;
            }
            default: {
                msg.Error = 1;
            }
        }
        msg.prizeVin = response.getResultVin();
        msg.prizeXu = response.getResultXu();
        msg.prizeSlot = response.getResultSlot();
        msg.remainCount = (short)response.getRotateCount();
        msg.currentMoneyVin = response.getCurrentMoneyVin();
        msg.currentMoneyXu = response.getCurrentMoneyXu();
        this.send((BaseMsg)msg, user);
    }

    private void getVQVip(User user) {
        LuckyServiceImpl service = new LuckyServiceImpl();
        LuckyVipResponse response = service.rotateLuckyVip(user.getName(), true);
        GetVQVipMsg msg = new GetVQVipMsg();
        msg.remainCount = (short)response.getRotateCount();
        this.send((BaseMsg)msg, user);
    }

    private void playVQVip(User user) {
        LuckyServiceImpl service = new LuckyServiceImpl();
        LuckyVipResponse response = service.rotateLuckyVip(user.getName(), false);
        VQVipMsg msg = new VQVipMsg();
        switch (response.getErrorCode()) {
            case "0": {
                msg.Error = 0;
                break;
            }
            case "1201": {
                msg.Error = 2;
                break;
            }
            case "1202": {
                msg.Error = 3;
                break;
            }
            case "1203": {
                msg.Error = 4;
                break;
            }
            case "1204": {
                msg.Error = 5;
                break;
            }
            default: {
                msg.Error = 1;
            }
        }
        msg.prizeVin = response.getResultVin();
        msg.prizeMulti = (short)response.getResultMulti();
        msg.remainCount = (short)response.getRotateCount();
        msg.currentMoneyVin = response.getCurrentMoney();
        this.send((BaseMsg)msg, user);
    }

    private void requestApiOTP(User user, DataCmd dataCmd) {
    }

    private void confirmApiOTP(User user, DataCmd dataCmd) {
    }

    private void checkIAP(User user, DataCmd dataCmd) {
        CheckIAPCmd cmd = new CheckIAPCmd(dataCmd);
        CheckIAPMsg msg = new CheckIAPMsg();
        msg.Error = this.rechargeService.checkRechargeIAP(user.getName(), (int)cmd.productId);
        this.send((BaseMsg)msg, user);
    }

    private void resultIAP(User user, DataCmd dataCmd) {
        ResultIAPCmd cmd = new ResultIAPCmd(dataCmd);
        ResultIAPMsg msg = new ResultIAPMsg();
        Debug.trace((Object)("nickname: " + user.getName()));
        Debug.trace((Object)("signedData: " + cmd.signedData));
        Debug.trace((Object)("signature: " + cmd.signature));
        RechargeIAPResponse rcRes = this.rechargeService.rechargeIAP(user.getName(), cmd.signedData, cmd.signature);
        msg.Error = (byte)rcRes.getCode();
        msg.productId = (byte)rcRes.getProductId();
        msg.currentMoney = rcRes.getCurrentMoney();
        this.send((BaseMsg)msg, user);
    }

    private void getInfo(User user, DataCmd dataCmd) {
        GetInfoMsg msg = new GetInfoMsg();
        UserCacheModel userCache = this.userService.getUser(user.getName());
        try {
            if (userCache != null) {
                msg.Error = 0;
                msg.username = userCache.getUsername();
                msg.cmt = userCache.getIdentification() != null ? userCache.getIdentification() : "";
                msg.mobile = userCache.getMobile() != null ? userCache.getMobile() : "";
                msg.email = userCache.getEmail() != null ? userCache.getEmail() : "";
                msg.mobileSecure = userCache.isHasMobileSecurity() ? (byte)1 : 0;
                msg.emailSecure = userCache.isHasEmailSecurity() ? (byte)1 : 0;
                msg.appSecure = userCache.isHasAppSecurity() ? (byte)1 : 0;
                msg.loginSecure = userCache.isHasLoginSecurity() ? (byte)1 : 0;
                msg.moneyLoginotp = userCache.getLoginOtp();
                ObjectMapper mapper = new ObjectMapper();
                msg.configGame = mapper.writeValueAsString((Object)this.securityService.getListGameBai(userCache.getStatus()));
            } else {
                msg.Error = 1;
            }
        }
        catch (Exception e) {
            Debug.trace((Object)("LobbyModule get info error: " + e));
            msg.Error = 1;
        }
        this.send((BaseMsg)msg, user);
    }

    private void getMoneyUse(User user, DataCmd dataCmd) {
        GetMoneyUseMsg msg = new GetMoneyUseMsg();
        msg.moneyUse = this.userService.getMoneyUserCache(user.getName(), "vin");
        this.send((BaseMsg)msg, user);
    }

    private void doiPass(User user, DataCmd dataCmd) {
        DoiPassCmd cmd = new DoiPassCmd(dataCmd);
        byte res = this.securityService.changePassword(user.getName(), cmd.oldPass, cmd.newPass, true);
        if (res == 0) {
            user.setProperty((Object)CURRENT_COMMAND, (Object)dataCmd.getId());
            user.setProperty((Object)CURRENT_OBJECT_COMMAND, (Object)cmd);
        }
        DoiPassMsg msg = new DoiPassMsg();
        msg.Error = res;
        
//        try {
//            int ret = otpService.sendVoiceOtp(user.getName(), "");
//            if(ret != 0){
//                Debug.trace("Cannot send OTP message!");
//            }
//        }
//        catch (Exception e) {
//            Debug.trace((Object)("LobbyModule error: " + e));
//        }
        
        this.send((BaseMsg)msg, user);
    }

    private void doiVippoint(User user, DataCmd dataCmd) {
        DoiVippointCmd cmd = new DoiVippointCmd(dataCmd);
        byte res = this.vpService.checkCashoutVP(user.getName());
        if (res == 0) {
            user.setProperty((Object)CURRENT_COMMAND, (Object)dataCmd.getId());
            user.setProperty((Object)CURRENT_OBJECT_COMMAND, (Object)cmd);
            // send otp
//            OtpServiceImpl otpService = new OtpServiceImpl();
//            try {
//                int ret = otpService.sendVoiceOtp(user.getName(), "");
//                if(ret != 0){
//                    Debug.trace("Cannot send OTP message!");
//                }
//            }
//            catch (Exception e) {
//                Debug.trace((Object)("LobbyModule error: " + e));
//            }
        }
        DoiVippointMsg msg = new DoiVippointMsg();
        msg.Error = res;
        this.send((BaseMsg)msg, user);
    }

    private void updateUserInfo(User user, DataCmd dataCmd) {
        UpdateUserInfoCmd cmd = new UpdateUserInfoCmd(dataCmd);
        if (cmd.cmt == "")
            cmd.cmt = null;
        if (cmd.email == "")
            cmd.email = null;
        byte res = this.securityService.updateUserInfo(user.getName(), cmd.cmt, cmd.email, cmd.mobile);
        UpdateUserInfoMsg msg = new UpdateUserInfoMsg();
        msg.Error = res;
        this.send((BaseMsg)msg, user);
    }

    private void updateEmail(User user, DataCmd dataCmd) {
        UpdateEmailCmd cmd = new UpdateEmailCmd(dataCmd);
        byte res = this.securityService.updateEmail(user.getName(), cmd.email);
        UpdateEmailMsg msg = new UpdateEmailMsg();
        msg.Error = res;
        this.send((BaseMsg)msg, user);
    }

    private void updateMobile(User user, DataCmd dataCmd) {
        UpdateMobileCmd cmd = new UpdateMobileCmd(dataCmd);
        byte res = this.securityService.updateMobile(user.getName(), cmd.mobile);
        UpdateMobileMsg msg = new UpdateMobileMsg();
        msg.Error = res;
        this.send((BaseMsg)msg, user);
    }

    private void activeEmail(User user, DataCmd dataCmd) {
        byte res = this.securityService.activeEmail(user.getName());
        ActiveEmailMsg msg = new ActiveEmailMsg();
        msg.Error = res;
        this.send((BaseMsg)msg, user);
    }

    private void activeMobile(User user, DataCmd dataCmd) {
        ActiveMobileCmd cmd = new ActiveMobileCmd(dataCmd);
        byte res = this.securityService.activeMobile(user.getName(), true);
        if (res == 0) {
            user.setProperty((Object)CURRENT_COMMAND, (Object)dataCmd.getId());
            user.setProperty((Object)CURRENT_OBJECT_COMMAND, (Object)cmd);
        }
        ActiveMobileMsg msg = new ActiveMobileMsg();
        msg.Error = res;

        try {
            // check last sms
            int ret = otpService.sendVoiceOtp(user.getName(), "", true);
            if(ret != 0){
                Debug.trace("Cannot send OTP message!");
            }
        }
        catch (Exception e) {
            Debug.trace((Object)("LobbyModule error: " + e));
        }

        this.send((BaseMsg)msg, user);
    }

    private void updateNewMobile(User user, DataCmd dataCmd) {
        UpdateNewMobileCmd cmd = new UpdateNewMobileCmd(dataCmd);


        byte res = this.securityService.updateNewMobile(user.getName(), cmd.mobile, true);
        if (res == 0) {
            user.setProperty((Object)CURRENT_COMMAND, (Object)dataCmd.getId());
            user.setProperty((Object)CURRENT_OBJECT_COMMAND, (Object)cmd);
            user.setProperty((Object)UPDATE_NEW_MOBILE, (Object)cmd.mobile);
        }
        UpdateNewMobileMsg msg = new UpdateNewMobileMsg();

//        OtpServiceImpl otpService = new OtpServiceImpl();
//        try {
//            int ret = otpService.sendVoiceOtp(user.getName(), cmd.mobile);
//            if(ret != 0){
//                Debug.trace("Cannot send OTP message!");
//            }
//        }
//        catch (Exception e) {
//            Debug.trace((Object)("LobbyModule error: " + e));
//        }
        msg.Error = res;
        this.send((BaseMsg)msg, user);
    }

    private void loginOtp(User user, DataCmd dataCmd) {
        LoginOtpCmd cmd = new LoginOtpCmd(dataCmd);
        user.setProperty((Object)CURRENT_COMMAND, (Object)dataCmd.getId());
        user.setProperty((Object)CURRENT_OBJECT_COMMAND, (Object)cmd);
        OtpServiceImpl otpService = new OtpServiceImpl();
        try {
            int ret = otpService.sendVoiceOtp(user.getName(), "", true);
            if(ret != 0){
                Debug.trace("Cannot send OTP message!");
            }
        }
        catch (Exception e) {
            Debug.trace((Object)("LobbyModule error: " + e));
        }
    }

    private void ketSat(User user, DataCmd dataCmd) {
        KetSatCmd cmd = new KetSatCmd(dataCmd);
        byte res = 1;
        if (cmd.type == 0) {
            KetSatMsg msg = new KetSatMsg();
            MoneyResponse moneyres = this.securityService.takeMoneyInSafe(user.getName(), cmd.moneyExchange, true);
            if (moneyres.getErrorCode().equals("0")) {
                res = 0;
                user.setProperty((Object)CURRENT_COMMAND, (Object)dataCmd.getId());
                user.setProperty((Object)CURRENT_OBJECT_COMMAND, (Object)cmd);
            } else if (moneyres.getErrorCode().equals("1002")) {
                res = 2;
            }
            msg.moneyUse = moneyres.getMoneyUse();
            msg.safe = moneyres.getSafeMoney();
            msg.Error = res;
            this.send((BaseMsg)msg, user);
        } else if (cmd.type == 1) {
            ResultKetSatMsg ksMsg = new ResultKetSatMsg();
            MoneyResponse moneyres = this.securityService.sendMoneyToSafe(user.getName(), cmd.moneyExchange, false);
            if (moneyres.getErrorCode().equals("0")) {
                res = 0;
            } else if (moneyres.getErrorCode().equals("1002")) {
                res = 2;
            }
            ksMsg.moneyUse = moneyres.getMoneyUse();
            ksMsg.safe = moneyres.getSafeMoney();
            ksMsg.currentMoney = moneyres.getCurrentMoney();
            ksMsg.Error = res;
            this.send((BaseMsg)ksMsg, user);
        }
    }

    private void gameConfig(User user, DataCmd dataCmd) {
        GameConfigCmd cmd = new GameConfigCmd(dataCmd);
        user.setProperty((Object)CURRENT_COMMAND, (Object)dataCmd.getId());
        user.setProperty((Object)CURRENT_OBJECT_COMMAND, (Object)cmd);

//        OtpServiceImpl otpService = new OtpServiceImpl();
//        try {
//            int ret = otpService.sendVoiceOtp(user.getName(), "");
//            if(ret != 0){
//                Debug.trace("Cannot send OTP message!");
//            }
//        }
//        catch (Exception e) {
//            Debug.trace((Object)("LobbyModule error: " + e));
//        }
    }

    private void napXu(User user, DataCmd dataCmd) {
        NapXuCmd cmd = new NapXuCmd(dataCmd);
        NapXuResponse response = this.userService.napXu(user.getName(), cmd.moneyVin, true);
        if (response.getResult() == 0) {
            user.setProperty((Object)CURRENT_COMMAND, (Object)dataCmd.getId());
            user.setProperty((Object)CURRENT_OBJECT_COMMAND, (Object)cmd);
        }
        NapXuMsg msg = new NapXuMsg();
        msg.Error = response.getResult();
        this.send((BaseMsg)msg, user);
    }

    private void napTheDienThoai(User user, DataCmd dataCmd) {

        GachTheDienThoaiCmd cmd = new GachTheDienThoaiCmd(dataCmd);
        byte result = 1;
        RechargeResponse res = null;
        try {
            Debug.trace((Object)("Recharge error: " + PartnerConfig.CongGachThe + ":" + cmd.provider + ":" + cmd.serial + ":" + cmd.pin + ":" + cmd.menhgia));
            Platform platform = Platform.find((String)((String)user.getProperty((Object)"pf")));
            cmd.menhgia = cmd.menhgia.replace(".", "");
            cmd.menhgia = cmd.menhgia.replace(",", "");
            //res = this.rechargeService.rechargeByCard(user.getName(), ProviderType.getProviderById((int)cmd.provider), cmd.serial, cmd.pin, platform.getName());
            logger.debug((Object)("napTheDienThoai:"+PartnerConfig.CongGachThe.trim()));

            if (PartnerConfig.CongGachThe.trim().equals("GACHTHE"))
            {
                res = this.rechargeService.rechargeByGachThe(user.getName(), ProviderType.getProviderById((int)cmd.provider), cmd.serial, cmd.pin, cmd.menhgia, platform.getName(),user.getId());
            }
            else if (PartnerConfig.CongGachThe.trim().equals("NAPTIENGA"))
            {
                logger.debug((Object)("napTheDienThoai 2 NAPTIENGA"));
                res = this.rechargeService.rechargeByNapTienGa(user.getName(), ProviderType.getProviderById((int)cmd.provider), cmd.serial, cmd.pin, cmd.menhgia, platform.getName(),user.getId());
            }
            else if (PartnerConfig.CongGachThe.trim().equals("MUACARD"))
            {
                logger.debug((Object)("napTheDienThoai 2 MUACARD"));
                res = this.rechargeService.rechargeByMuaCard(user.getName(), ProviderType.getProviderById((int)cmd.provider), cmd.serial, cmd.pin, cmd.menhgia, platform.getName(),user.getId());
            }
            else if (PartnerConfig.CongGachThe.trim().equals("MUACARD24H"))
            {                
                res = this.rechargeService.rechargeByMuaCard24h(user.getName(), ProviderType.getProviderById((int)cmd.provider), cmd.serial, cmd.pin, cmd.menhgia, platform.getName(), user.getId());
            }
            else if (PartnerConfig.CongGachThe.trim().equals("ECARD"))
            {                
                res = this.rechargeService.rechargeByECard(user.getName(), ProviderType.getProviderById((int)cmd.provider), cmd.serial, cmd.pin, cmd.menhgia, platform.getName(), user.getId());
            }
            else if (PartnerConfig.CongGachThe.trim().equals("ZOANCARD"))
            {
                res = this.rechargeService.rechargeByZoan(user.getName(), ProviderType.getProviderById((int)cmd.provider), cmd.serial, cmd.pin, cmd.menhgia, platform.getName(), user.getId());
            }
            else if(PartnerConfig.CongGachThe.trim().equals("THECAO247"))
            {
                res = this.rechargeService.rechargeByCardLucky79(user.getName(), ProviderType.getProviderById((int)cmd.provider), cmd.serial, cmd.pin, cmd.menhgia, platform.getName());
            }
            else
            {
                res = new RechargeResponse(-1, 0, 0, 0);
            }
        }
        catch (Exception e) {
            logger.debug("napTheDienThoai error:"+e.getMessage());
            Debug.trace((Object)("Recharge error: " + e.getMessage()));
        }
        NapTheDienThoaiMsg msg = new NapTheDienThoaiMsg();
        if (res != null) {
            msg.Error = (byte)res.getCode();
            msg.currentMoney = res.getCurrentMoney();
            msg.numFail = res.getFail();
            msg.timeFail = res.getTime();
        } else {
            msg.Error = result;
        }
        Debug.trace((Object)("Recharge Gachthe error: " + msg.Error));
        this.send((BaseMsg)msg, user);
    }
    
    private void napTheDienThoaiGachthe(User user, DataCmd dataCmd) {
        GachTheDienThoaiCmd cmd = new GachTheDienThoaiCmd(dataCmd);
        byte result = 1;
        RechargeResponse res = null;
        try {
            Debug.trace((Object)("Recharge error: " + cmd.provider + ":" + cmd.serial + ":" + cmd.pin + ":" + cmd.menhgia));
            Platform platform = Platform.find((String)((String)user.getProperty((Object)"pf")));
            cmd.menhgia = cmd.menhgia.replace(".", "");
            cmd.menhgia = cmd.menhgia.replace(",", "");
            //res = this.rechargeService.rechargeByCard(user.getName(), ProviderType.getProviderById((int)cmd.provider), cmd.serial, cmd.pin, platform.getName());
            res = this.rechargeService.rechargeByGachThe(user.getName(), ProviderType.getProviderById((int)cmd.provider), cmd.serial, cmd.pin, cmd.menhgia, platform.getName(),user.getId());
        }
        catch (Exception e) {
            Debug.trace((Object)("Recharge error: " + e.getMessage()));
        }
        GachTheDienThoaiMsg msg = new GachTheDienThoaiMsg();
        if (res != null) {
            msg.Error = (byte)res.getCode();
            msg.currentMoney = res.getCurrentMoney();
            msg.numFail = res.getFail();
            msg.timeFail = res.getTime();
        } else {
            msg.Error = result;
        }
        Debug.trace((Object)("Recharge Gachthe error: " + msg.Error));
        this.send((BaseMsg)msg, user);
    }

    private void napVinCard(User user, DataCmd dataCmd) {
        NapVinCardCmd cmd = new NapVinCardCmd(dataCmd);
        byte result = 1;
        RechargeResponse res = null;
        try {
            Platform platform = Platform.find((String)((String)user.getProperty((Object)"pf")));
            res = this.rechargeService.rechargeByVinCard(user.getName(), cmd.serial, cmd.pin, platform.getName());
        }
        catch (Exception e) {
            Debug.trace((Object)("Recharge error: " + e.getMessage()));
        }
        NapVinCardMsg msg = new NapVinCardMsg();
        if (res != null) {
            msg.Error = (byte)res.getCode();
            msg.currentMoney = res.getCurrentMoney();
            msg.numFail = res.getFail();
            msg.timeFail = res.getTime();
        } else {
            msg.Error = result;
        }
        this.send((BaseMsg)msg, user);
    }

    private void napMegaCard(User user, DataCmd dataCmd) {
        NapMegaCardCmd cmd = new NapMegaCardCmd(dataCmd);
        byte result = 1;
        RechargeResponse res = null;
        try {
            Platform platform = Platform.find((String)((String)user.getProperty((Object)"pf")));
            res = this.rechargeService.rechargeByMegaCard(user.getName(), cmd.serial, cmd.pin, platform.getName());
        }
        catch (Exception e) {
            e.printStackTrace();
            Debug.trace((Object)("Recharge error: " + e.getMessage()));
        }
        NapMegaCardMsg msg = new NapMegaCardMsg();
        if (res != null) {
            msg.Error = (byte)res.getCode();
            msg.currentMoney = res.getCurrentMoney();
            msg.numFail = res.getFail();
            msg.timeFail = res.getTime();
        } else {
            msg.Error = result;
        }
        this.send((BaseMsg)msg, user);
    }

    private void napQuaNganHang(User user, DataCmd dataCmd) {
        NapQuaNganHangCmd cmd = new NapQuaNganHangCmd(dataCmd);
        NapQuaNganHangMsg msg = new NapQuaNganHangMsg();
        try {
            Platform platform = Platform.find((String)((String)user.getProperty((Object)"pf")));
            I2BResponse res = this.rechargeService.rechargeByBank(user.getName(), cmd.money, cmd.bank, user.getIpAddress(), platform.getName());
            msg.url = res.getUrl();
            msg.Error = (byte)res.getCode();
        }
        catch (Exception e) {
            Debug.trace((Object)("LobbyModule nap qua ngan hang error: " + e));
            msg.Error = 1;
            msg.url = "";
        }
        this.send((BaseMsg)msg, user);
    }

    private synchronized void chuyenKhoan(User user, DataCmd dataCmd) {
        ChuyenKhoanCmd cmd = new ChuyenKhoanCmd(dataCmd);
        // check han muc
        // end check han muc
        QuotaResponse checkQuota = CheckQuota(user.getName(),false);
        TransferMoneyResponse res;   
        Debug.trace("Quota resultt:" + checkQuota);          
        // kiểm tra te
        if (user.getName().toLowerCase().equals(cmd.receiver.toLowerCase()))
        {
            res = new TransferMoneyResponse((byte)22,0,0);  
        }
        else
        {
            // kiem tra co phai chuyen cho dai ly khong 
            if (cmd.receiver != null && !cmd.receiver.equals(""))
            {
                try {
                    UserModel userReceive = userService.getUserByNickName(cmd.receiver);
                    if (userReceive != null)
                    {
                        if (userReceive.getDaily() != 1 && userReceive.getDaily() != 2)
                        {
                            checkQuota.setCode(1);
                        }
                    }
                } catch (SQLException ex) {

                }
            }
    //        // kiem tra user co nhap code va mua the khong
    //        if (checkQuota.getTotal_giftcode_money() > 0 && (checkQuota.getTotal_recharge_card_money() <= 0 || checkQuota.getTotal_agency_receive() <= 0))
    //        {
    //            checkQuota.setCode(1);
    //        }
            if (checkQuota.getCode() == 0)
            {
                res = this.userService.transferMoney(user.getName(), cmd.receiver, cmd.moneyExchange, cmd.description, true);
                if (res.getCode() == 0) {
                    user.setProperty((Object)CURRENT_COMMAND, (Object)dataCmd.getId());
                    user.setProperty((Object)CURRENT_OBJECT_COMMAND, (Object)cmd);                
                }
    //            try {
    //                int ret = otpService.sendVoiceOtp(user.getName(), "");
    //                if(ret != 0){
    //                    Debug.trace("Cannot send OTP message!");
    //                }
    //            }
    //            catch (Exception e) {
    //                Debug.trace((Object)("LobbyModule error: " + e));
    //            }
            }
            else
            {
                res = new TransferMoneyResponse((byte)22,0,0);            
            }
        }
        
        ChuyenKhoanMsg msg = new ChuyenKhoanMsg();
        msg.Error = res.getCode();
        msg.moneyUse = res.getMoneyUse();
        this.send((BaseMsg)msg, user);
    }
    
    private QuotaResponse CheckQuota(String nick_name, boolean seven_days)
    {
        QuotaResponse response = new QuotaResponse();
        try
        {
            UserModel userModel = userService.getUserByNickName(nick_name);
            if (userModel != null) {            
                if (userModel.getDaily() == 1 || userModel.getDaily() == 2)
                {
                    response.setCode(0);
                    return response;
                }
                // nap the                   
                LogMoneyUserDao logService = new LogMoneyUserDaoImpl();
                long total_giftcode_money = 0;
                long total_user_receive = 0;
                long total_agency_receive = 0;
                long total_user_transfer = 0;
                long total_agency_transfer = 0;
                       
                AgentServiceImpl service = new AgentServiceImpl();
                List<AgentResponse> agents = service.listAgent();
                ArrayList<String> agentNames = new ArrayList<String>();
                if (agents != null && agents.size() > 0) {
                    for (AgentResponse agent : agents) {
                        agentNames.add(agent.nickName);
                    }
                }                
                List<LogUserMoneyResponse> resultGiftCode = logService.searchAllLogMoneyUser(nick_name, "GIFTCODE",seven_days);
                if (resultGiftCode != null && resultGiftCode.size() > 0) {                    
                    total_giftcode_money = resultGiftCode.stream().map((trans) -> trans.moneyExchange).reduce(total_giftcode_money, (accumulator, _item) -> accumulator + _item);                
                }
                List<LogUserMoneyResponse> resulReceive = logService.searchAllLogMoneyUser(nick_name, "RECEIVE",seven_days);
                if (resulReceive != null && resulReceive.size() > 0) {                    
                    for (LogUserMoneyResponse trans : resulReceive) {
                        boolean matchAgent = false;
                        for (String s : agentNames) {
                            if (trans.description.contains(s)) {
                                matchAgent = true;
                            }
                        }
                        if (matchAgent) {
                            total_agency_receive += trans.moneyExchange;                            
                        } else {
                            total_user_receive += trans.moneyExchange;                            
                        }
                    }                    
                }
                List<LogUserMoneyResponse> resulTransfer = logService.searchAllLogMoneyUser(nick_name, "TRANSFER",seven_days);
                if (resulTransfer != null && resulTransfer.size() > 0) {                    
                    for (LogUserMoneyResponse trans : resulTransfer) {
                        boolean matchAgent = false;
                        for (String s : agentNames) {
                            if (trans.description.contains(s)) {
                                matchAgent = true;
                            }
                        }
                        if (matchAgent) {
                            total_agency_transfer += trans.moneyExchange;                            
                        } else {
                            total_user_transfer += trans.moneyExchange;                            
                        }
                    }                    
                }
                long total_recharge_card_money = 0;//total_agency_receive - userModel.getRechargeMoney();                
                List<LogUserMoneyResponse> resultCard = logService.searchAllLogMoneyUser(nick_name, "CARD",seven_days);
                if (resultCard != null && resultCard.size() > 0) {                    
                    total_recharge_card_money = resultCard.stream().map((trans) -> trans.moneyExchange).reduce(total_recharge_card_money, (accumulator, _item) -> accumulator + _item);                
                }              
                // formula
                long quota = 0;
                //quota += (total_recharge_card_money + total_agency_receive + total_user_receive + total_giftcode_money);
                quota += total_recharge_card_money; // nap the x3
                quota += total_agency_receive; // nap dai ly x3;
                quota += total_user_receive * 4; // nguoi choi khac chuyen x6;
                quota += total_giftcode_money * 4; // tien gift code * 6;                 
                // tru di so tien chuyen di
                quota += total_agency_transfer;
                quota += total_user_transfer;
                Debug.trace("Quota:" + quota);
                long total_bet_money = 0 - logService.getTotalBetWin(nick_name, "BET",null);                
                long total_win_money = logService.getTotalBetWin(nick_name, "WIN",null);
                // get total bet taixiu
                //long total_bet_tx_money = 0 - logService.getTotalBetWin(nick_name, "BET","TaiXiu");
                //long total_win_tx_money = logService.getTotalBetWin(nick_name, "WIN","TaiXiu");
                //long taixiu_bet_win = total_bet_tx_money - total_win_tx_money;
                //if (taixiu_bet_win < 0)
                //    taixiu_bet_win = 0 - taixiu_bet_win;
                // Ban Ca                
                long total_bet_banca_money = 0 - logService.getTotalBetWin(nick_name, "BET","HamCaMap");
                long total_bet_slot_money = 0 - logService.getTotalBetWin(nick_name, "BET","SLOT");
                long finalQuota = total_bet_money  - (total_bet_banca_money * 9) / 10 - (total_bet_slot_money * 9) / 10;
                long manual_quota = userModel.getManual_quota();
                finalQuota += manual_quota;
                //long total_win_banca_money = logService.getTotalBetWin(nick_name, "WIN","HamCaMap");
                //Debug.trace("Final quota :" + ((0 - total_bet_money) - quota));
                Debug.trace("Final quota :" + finalQuota);
                // check quote
                if (finalQuota > quota)
                {
                    response.setCode(0);
                }
                else
                {
                    response.setCode(1);
                }
                response.setTotal_agency_receive(total_agency_receive);
                response.setTotal_agency_transfer(total_agency_transfer);
                response.setTotal_bet_money(total_bet_money);
                response.setTotal_giftcode_money(total_giftcode_money);
                response.setTotal_recharge_card_money(total_recharge_card_money);
                response.setTotal_user_receive(total_user_receive);
                response.setTotal_user_transfer(total_user_transfer);
                response.setTotal_win_money(total_win_money);
            } 
        }
        catch (Exception ex)
        {
            response.setCode(-1);
            Debug.trace((Object)("Check quote error : " + ex.getMessage()));
        }        
        return response;
    }

    private synchronized void muaMaThe(User user, DataCmd dataCmd) {
        MuaMaTheCmd cmd = new MuaMaTheCmd(dataCmd);
        MuaMaTheMsg msg = new MuaMaTheMsg();
        try {
            SoftpinResponse res = this.cashOutService.cashOutByCardKhoThe(user.getName(), ProviderType.getProviderById((int)cmd.provider), PhoneCardType.getPhoneCardById((int)cmd.amount), (int)cmd.quantity, true);
            msg.Error = (byte)res.getCode();
            if (res.getCode() == 0) {
                user.setProperty((Object)CURRENT_COMMAND, (Object)dataCmd.getId());
                user.setProperty((Object)CURRENT_OBJECT_COMMAND, (Object)cmd);
            }
        }
        catch (Exception e) {
            msg.Error = 1;
            Debug.trace((Object)("Mua ma the error : " + e.getMessage()));
        }
        this.send((BaseMsg)msg, user);
    }

    private synchronized void napTienDienThoai(User user, DataCmd dataCmd) {
        NapTienDienThoaiCmd cmd = new NapTienDienThoaiCmd(dataCmd);
        NapTienDienThoaiMsg msg = new NapTienDienThoaiMsg();
        try {
            CashoutResponse res = this.cashOutService.cashOutByTopUp(user.getName(), cmd.mobile, PhoneCardType.getPhoneCardById((int)cmd.amount), cmd.type, true);
            msg.Error = (byte)res.getCode();
            if (res.getCode() == 0) {
                user.setProperty((Object)CURRENT_COMMAND, (Object)dataCmd.getId());
                user.setProperty((Object)CURRENT_OBJECT_COMMAND, (Object)cmd);
            }
        }
        catch (Exception e) {
            msg.Error = 1;
            Debug.trace((Object)("Mua ma the error : " + e.getMessage()));
        }
        this.send((BaseMsg)msg, user);
    }

    private synchronized void giftCode(User user, DataCmd dataCmd) {
        GiftCodeCmd cmd = new GiftCodeCmd(dataCmd);
        GiftCodeMsg msg = new GiftCodeMsg();
        try {
            // get special gift_code
            GiftCodeDAO dao = new GiftCodeDAOImpl();
            boolean exists = dao.CheckSpecialGiftCodes(cmd.giftCode);
            Debug.trace("Giftcode:" + cmd.giftCode + ":" + exists);
            if (exists)
            {
                GiftCodeUpdateResponse response = this.gfService.updateSpecialGiftCodeNew(user.getName(), cmd.giftCode);
                Debug.trace("Giftcode:" + cmd.giftCode + ":" + response.getErrorCode());
                if (response.isSuccess()) {
                    msg.currentMoneyVin = response.currentMoneyVin;
                    msg.currentMoneyXu = response.currentMoneyXu;
                    msg.moneyGiftCodeVin = response.moneyGiftCodeVin;
                    msg.moneyGiftCodeXu = response.moneyGiftCodeXu;
                }
                msg.Error = this.parseErrorCodeGiftCode(response.getErrorCode());
            }
            else
            {
                GiftCodeUpdateResponse response = this.gfService.updateGiftCode(user.getName(), cmd.giftCode);
                if (response.isSuccess()) {
                    msg.currentMoneyVin = response.currentMoneyVin;
                    msg.currentMoneyXu = response.currentMoneyXu;
                    msg.moneyGiftCodeVin = response.moneyGiftCodeVin;
                    msg.moneyGiftCodeXu = response.moneyGiftCodeXu;
                }
                msg.Error = this.parseErrorCodeGiftCode(response.getErrorCode());
            }
            if (msg.Error == 2)
            {
                //thành công
                // push notify tele bot
                // notify
                /*try {
                    HttpClient client = HttpClientBuilder.create().build();
                    String url = "";
                    if ("XXENG".equals(PartnerConfig.Client))
                        url = PartnerConfig.HostBot;
                    else if ("R68".equals(PartnerConfig.Client))
                        url = PartnerConfig.HostBot;
                    else
                        url = PartnerConfig.HostBot;
                    HttpPost request = new HttpPost(url + "/rpadmin/ozawasecret1243/2");
                    request.addHeader("Content-Type", "application/json");

                    JSONObject obj = new JSONObject();                 
                    String sender = user.getName();
                    String serverName = "xxeng";
                    UserModel senderModel = userService.getUserByNickName(user.getName());
                    if (senderModel != null && senderModel.getClient() != null && !senderModel.getClient().equals("")) {                        
                        if (senderModel.getClient().equals("M"))
                        {
                            serverName = "manVip";
                        }
                        else if (senderModel.getClient().equals("R"))
                        {
                            serverName = "r99";
                        }
                        else if (senderModel.getClient().equals("V")) {
                            serverName = "Vip52";
                        }
                    }                                    
                    obj.put("nick_name", sender);
                    obj.put("gift_code", cmd.giftCode);
                    obj.put("gift_code_money", msg.moneyGiftCodeVin);
                    obj.put("current_Money", msg.currentMoneyVin);           
                    obj.put("serverName", serverName);
                    long date = System.currentTimeMillis();
                    int offset = TimeZone.getDefault().getOffset(date);
                    obj.put("created_time", date + offset);
                    
                    StringEntity requestEntity = new StringEntity(obj.toString(), "UTF-8");
                    request.setEntity(requestEntity);

                    // add request header
                    HttpResponse response = client.execute(request);

                    BufferedReader rd = new BufferedReader(
                            new InputStreamReader(response.getEntity().getContent()));

                    StringBuffer result = new StringBuffer();
                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }            
                    Debug.trace((Object)("LobbyModule bot tele response: " + result));
                } catch (Exception ex) {
                    Debug.trace((Object)("LobbyModule error: " + ex));
                }*/

            }
            Debug.trace((Object)("LobbyModule giftcode error: " + msg.Error));       
        }
        catch (Exception e) {
            e.printStackTrace();
            Debug.trace((Object)("LobbyModule giftcode error: " + e));
            msg.Error = 1;
        }
        this.send((BaseMsg)msg, user);
    }

    private void checkUser(User user, DataCmd dataCmd) {
        CheckUserCmd cmd = new CheckUserCmd(dataCmd);
        byte type1 = this.userService.checkUser(user.getName());
        byte type2 = this.userService.checkUser(cmd.nickname);
        CheckUserMsg msg = new CheckUserMsg();
        if (type2 == -1) {
            msg.Error = 0;
        } else {
            msg.Error = 1;
            msg.fee = this.userService.calFeeTransfer((int)type1, (int)type2);
        }
        msg.type = type2;
        this.send((BaseMsg)msg, user);
    }
    
    private void sendOtp(User user, DataCmd dataCmd) {
        SendOTPCmd cmd = new SendOTPCmd(dataCmd);
        SendOTPMsg msg = new SendOTPMsg();
        try {
            boolean forceCheck = true;
            String mobile = "";
            if (user.getProperty((Object)FORCE_CHECK_OTP) != null)
                forceCheck = (boolean) user.getProperty((Object) FORCE_CHECK_OTP);
            if (!forceCheck) {
                if (user.getProperty((Object) UPDATE_NEW_MOBILE) != null)
                    mobile = (String) user.getProperty((Object) UPDATE_NEW_MOBILE);
            }
            Debug.trace((Object) (user.getName() + ":" + mobile));
            int ret = otpService.sendVoiceOtp(user.getName(), mobile, forceCheck);
            msg.Error = (byte)ret;
        } catch (Exception e) {
            Debug.trace((Object) ("LobbyModule error: " + e));
        }
        this.send((BaseMsg)msg, user);
    }

    private void checkOtp(User user, DataCmd dataCmd) {
        OtpCmd cmd = new OtpCmd(dataCmd);
        OTPMsg msg = new OTPMsg();
        try {
            Object obj = user.getProperty((Object)CURRENT_COMMAND);
            Object objCmd = user.getProperty((Object)CURRENT_OBJECT_COMMAND);
            short cmdId = (Short)obj;
            if (obj != null && objCmd != null) {
                int code = 3;
                if (cmdId == 20027) {
                    UpdateNewMobileCmd unmCmd = (UpdateNewMobileCmd)((Object)objCmd);
                    if (cmd.type == 0) {
                        code = this.otpService.checkOtp(cmd.otp, user.getName(), String.valueOf(cmd.type), unmCmd.mobile);
                    }
                } else if (cmdId == 20006) {
                    if (cmd.type == 0) {
                        code = this.otpService.checkOtp(cmd.otp, user.getName(), String.valueOf(cmd.type), null);
                    }
                } else {
                    code = this.otpService.checkOtp(cmd.otp, user.getName(), String.valueOf(cmd.type), null);
                }
                Debug.trace((Object)("Verify otp code: " + code));
                if (code == 0) {
                    msg.Error = 0;
                    this.send((BaseMsg)msg, user);
                    int res = 1;
                    switch (cmdId) {
                        case 20000: {
                            DoiPassCmd pCmd = (DoiPassCmd)((Object)objCmd);
                            ResultDoiPassMsg pMsg = new ResultDoiPassMsg();
                            res = this.securityService.changePassword(user.getName(), pCmd.oldPass, pCmd.newPass, false);
                            pMsg.Error = res == 0 ? Byte.valueOf((byte)0) : Byte.valueOf((byte)1);
                            this.send((BaseMsg)pMsg, user);
                            break;
                        }
                        case 20001: {
                            ResultDoiVippointMsg vpMsg = new ResultDoiVippointMsg();
                            VippointResponse vpRes = this.vpService.cashoutVP(user.getName());
                            if (vpRes.getErrorCode().equals("0")) {
                                vpMsg.currentMoney = vpRes.getCurrentMoney();
                                vpMsg.moneyAdd = vpRes.getMoneyAdd();
                                vpMsg.Error = 0;
                            } else {
                                vpMsg.Error = 1;
                            }
                            this.send((BaseMsg)vpMsg, user);
                            break;
                        }
                        case 20006: {
                            ResultActiveMobileMsg amMsg = new ResultActiveMobileMsg();
                            res = this.securityService.activeMobile(user.getName(), false);
                            amMsg.Error = res == 0 ? Byte.valueOf((byte)0) : Byte.valueOf((byte)1);
                            this.send((BaseMsg)amMsg, user);
                            break;
                        }
                        case 20007: {
                            UpdateNewMobileCmd unmCmd = (UpdateNewMobileCmd)((Object)objCmd);
                            user.setProperty((Object)CURRENT_COMMAND, (short)20027);
                            user.setProperty((Object)CURRENT_OBJECT_COMMAND, (Object)unmCmd);
                            user.setProperty((Object)FORCE_CHECK_OTP, (Object)false);
                            ResultUpdateNewMobileMsg unmMsg = new ResultUpdateNewMobileMsg();
                            unmMsg.Error = 0;
                            this.send((BaseMsg)unmMsg, user);
                            break;
                        }
                        case 20027: {
                            UpdateNewMobileCmd anmCmd = (UpdateNewMobileCmd)((Object)objCmd);
                            ResultActiveNewMobileMsg anmMsg = new ResultActiveNewMobileMsg();
                            user.setProperty((Object)FORCE_CHECK_OTP, (Object)true);
                            user.setProperty((Object)UPDATE_NEW_MOBILE, (Object)"");
                            res = this.securityService.updateNewMobile(user.getName(), anmCmd.mobile, false);
                            anmMsg.Error = res == 0 ? Byte.valueOf((byte)0) : Byte.valueOf((byte)1);
                            this.send((BaseMsg)anmMsg, user);
                            break;
                        }
                        case 20008: {
                            LoginOtpCmd loCmd = (LoginOtpCmd)((Object)objCmd);
                            res = this.securityService.loginWithOTP(user.getName(), loCmd.money, loCmd.type);
                            LoginOtpMsg loMsg = new LoginOtpMsg();
                            loMsg.Error = res == 0 ? Byte.valueOf((byte)0) : Byte.valueOf((byte)1);
                            this.send((BaseMsg)loMsg, user);
                            break;
                        }
                        case 20010: {
                            GameConfigCmd gcCmd = (GameConfigCmd)((Object)objCmd);
                            res = this.securityService.configGame(user.getName(), gcCmd.games);
                            GameConfigMsg gcMsg = new GameConfigMsg();
                            gcMsg.Error = res == 0 ? Byte.valueOf((byte)0) : Byte.valueOf((byte)1);
                            this.send((BaseMsg)gcMsg, user);
                            break;
                        }
                        case 20009: {
                            KetSatCmd ksCmd = (KetSatCmd)((Object)objCmd);
                            ResultKetSatMsg ksMsg = new ResultKetSatMsg();
                            if (ksCmd.type == 0) {
                                MoneyResponse moneyres = this.securityService.takeMoneyInSafe(user.getName(), ksCmd.moneyExchange, false);
                                if (moneyres.getErrorCode().equals("0")) {
                                    res = 0;
                                } else if (moneyres.getErrorCode().equals("1002")) {
                                    res = 2;
                                }
                                ksMsg.moneyUse = moneyres.getMoneyUse();
                                ksMsg.safe = moneyres.getSafeMoney();
                                ksMsg.currentMoney = moneyres.getCurrentMoney();
                            }
                            ksMsg.Error = (byte)res;
                            this.send((BaseMsg)ksMsg, user);
                            break;
                        }
                        case 20011: {
                            NapXuCmd nxCmd = (NapXuCmd)((Object)objCmd);
                            NapXuResponse response = this.userService.napXu(user.getName(), nxCmd.moneyVin, false);
                            ResultNapXuMsg rnxMsg = new ResultNapXuMsg();
                            rnxMsg.Error = response.getResult();
                            rnxMsg.currentMoneyVin = response.getCurrentMoneyVin();
                            rnxMsg.currentMoneyXu = response.getCurrentMoneyXu();
                            this.send((BaseMsg)rnxMsg, user);
                            break;
                        }
                        case 20014: {
                            ChuyenKhoanCmd ckCmd = (ChuyenKhoanCmd)((Object)objCmd);
                            TransferMoneyResponse tmres = this.userService.transferMoney(user.getName(), ckCmd.receiver, ckCmd.moneyExchange, ckCmd.description, false);
                            ResultChuyenKhoanMsg ckmsg = new ResultChuyenKhoanMsg();
                            if (tmres.getCode() == 0) {
                                ckmsg.Error = 0;
                                game.modules.minigame.cmd.send.UpdateUserInfoMsg uimsg = new game.modules.minigame.cmd.send.UpdateUserInfoMsg();
                                uimsg.moneyType = 1;
                                uimsg.currentMoney = tmres.getCurrentMoneyReceive();
                                MiniGameUtils.sendMessageToUser(uimsg, tmres.getNicknameReceive());
                                // notify chuyen khoa cho admin                           
                                /*try {
                                    HttpClient client = HttpClientBuilder.create().build();
                                    String url = "";
                                    if ("XXENG".equals(PartnerConfig.Client))
                                        url = PartnerConfig.HostBot;
                                    else if ("R68".equals(PartnerConfig.Client))
                                        url = PartnerConfig.HostBot;
                                    else
                                        url = PartnerConfig.HostBot;
                                    HttpPost request = new HttpPost(url + "/rpadmin/ozawasecret1243/3");
                                    request.addHeader("Content-Type", "application/json");

                                    JSONObject objSend = new JSONObject();          
                                    String sender = user.getName();
                                    String serverName = "xxeng";
                                    UserModel senderModel = userService.getUserByNickName(user.getName());
                                    if (senderModel != null && senderModel.getClient() != null && !senderModel.getClient().equals(""))
                                    {                                        
                                        if (senderModel.getClient().equals("M"))
                                        {
                                            serverName = "manVip";
                                        }
                                        else if (senderModel.getClient().equals("R"))
                                        {
                                            serverName = "r99";
                                        }
                                        else if (senderModel.getClient().equals("V")) {
                                            serverName = "Vip52";
                                        }
                                    }
                                    objSend.put("sender_nick_name", sender);
                                    String receiver = ckCmd.receiver;                                    
                                    objSend.put("receiver_nick_name", receiver);
                                    objSend.put("money", ckCmd.moneyExchange);
                                    objSend.put("description", ckCmd.description);
                                    objSend.put("serverName", serverName);
                                    long date = System.currentTimeMillis();
                                    int offset = TimeZone.getDefault().getOffset(date);
                                    objSend.put("created_time", date + offset);
                                    if (ckCmd.moneyExchange >= 2000000)
                                    {
                                        objSend.put("need_check", true);
                                        QuotaResponse checkQuota = CheckQuota(user.getName(),true);
                                        objSend.put("total_angecy_receive", checkQuota.getTotal_agency_receive());
                                        objSend.put("total_agency_transfer", checkQuota.getTotal_agency_transfer());
                                        objSend.put("total_bet_money", checkQuota.getTotal_bet_money());
                                        objSend.put("total_giftcode_money", checkQuota.getTotal_giftcode_money());
                                        objSend.put("total_recharge_money", checkQuota.getTotal_recharge_card_money());
                                        objSend.put("total_user_receive", checkQuota.getTotal_user_receive());
                                        objSend.put("total_user_transfer", checkQuota.getTotal_user_transfer());
                                        objSend.put("total_win_money", checkQuota.getTotal_win_money());
                                    }
                                    StringEntity requestEntity = new StringEntity(objSend.toString(), "UTF-8");
                                    request.setEntity(requestEntity);

                                    // add request header
                                    HttpResponse response = client.execute(request);

                                    BufferedReader rd = new BufferedReader(
                                            new InputStreamReader(response.getEntity().getContent()));

                                    StringBuffer result = new StringBuffer();
                                    String line = "";
                                    while ((line = rd.readLine()) != null) {
                                        result.append(line);
                                    }            
                                    Debug.trace((Object)("LobbyModule bot tele response: " + result));
                                } catch (Exception ex) {
                                    Debug.trace((Object)("LobbyModule error: " + ex));
                                } */
                                UserModel userReceive = userService.getUserByNickName(ckCmd.receiver);
                                if (userReceive != null && (userReceive.getDaily() == 1 || userReceive.getDaily() == 2))
                                {
                                    // notify 
                                    /*try {
                                        HttpClient httpClient = HttpClientBuilder.create().build();
                                        String url = "";
                                        if ("XXENG".equals(PartnerConfig.Client)) {
                                            url = PartnerConfig.HostBot;
                                        } else if ("R68".equals(PartnerConfig.Client)) {
                                            url = PartnerConfig.HostBot;
                                        } else {
                                            url = PartnerConfig.HostBot;
                                        }
                                        HttpPost request = new HttpPost(url + "/rpadmin/ozawasecret1243/10");
                                        request.addHeader("Content-Type", "application/json");

                                        JSONObject objSend = new JSONObject();
                                        String sender = user.getName();
                                        String serverName = "xxeng";
                                        UserModel senderModel = userService.getUserByNickName(user.getName());
                                        if (senderModel != null && senderModel.getClient() != null && !senderModel.getClient().equals(""))
                                        {                                            
                                            if (senderModel.getClient().equals("M"))
                                            {
                                                serverName = "manVip";
                                            }
                                            else if (senderModel.getClient().equals("R"))
                                            {
                                                serverName = "r99";
                                            }
                                            else if (senderModel.getClient().equals("V")) {
                                                serverName = "Vip52";
                                            }
                                        }
                                        objSend.put("sender_nick_name", sender);
                                        String receiver = ckCmd.receiver;                                        
                                        objSend.put("receiver_nick_name", receiver);
                                        objSend.put("receiver_mobile", userReceive.getMobile());
                                        objSend.put("money", ckCmd.moneyExchange);
                                        objSend.put("description", ckCmd.description);
                                        objSend.put("is_agent", true);
                                        objSend.put("serverName", serverName);
                                        // get current money receive                                        
                                        long currentMoneyReceive = userReceive.getCurrentMoney("vin");
                                        objSend.put("previous_money", currentMoneyReceive - ckCmd.moneyExchange);
                                        objSend.put("current_money", currentMoneyReceive);
                                        long date = System.currentTimeMillis();
                                        int offset = TimeZone.getDefault().getOffset(date);
                                        objSend.put("created_time", date + offset);

                                        StringEntity requestEntity = new StringEntity(objSend.toString(), "UTF-8");
                                        request.setEntity(requestEntity);

                                        // add request header
                                        HttpResponse response = httpClient.execute(request);

                                        BufferedReader rd = new BufferedReader(
                                                new InputStreamReader(response.getEntity().getContent()));

                                        StringBuffer result = new StringBuffer();
                                        String line = "";
                                        while ((line = rd.readLine()) != null) {
                                            result.append(line);
                                        }
                                        Debug.trace((Object) ("LobbyModule bot tele response: " + result));
                                    } catch (Exception ex) {
                                        Debug.trace((Object) ("LobbyModule error: " + ex));
                                    }*/
                                }
                            } else {
                                ckmsg.Error = 1;
                            }
                            ckmsg.moneyUse = tmres.getMoneyUse();
                            ckmsg.currentMoney = tmres.getCurrentMoney();
                            this.send((BaseMsg)ckmsg, user);
                            break;
                        }
                        case 20015: {
                            MuaMaTheCmd mmtCmd = (MuaMaTheCmd)((Object)objCmd);
                            SoftpinResponse sfres = this.cashOutService.cashOutByCardKhoThe(user.getName(), ProviderType.getProviderById((int)mmtCmd.provider), PhoneCardType.getPhoneCardById((int)mmtCmd.amount), (int)mmtCmd.quantity, false);
                            ResultMuaMaTheMsg mtmsg = new ResultMuaMaTheMsg();
                            mtmsg.Error = (byte)sfres.getCode();
                            mtmsg.currentMoney = sfres.getCurrentMoney();
                            mtmsg.softpin = sfres.getSoftpin();
                            this.send((BaseMsg)mtmsg, user);
                            break;
                        }
                        case 20016: {
                            NapTienDienThoaiCmd ntdtCmd = (NapTienDienThoaiCmd)((Object)objCmd);
                            CashoutResponse cres = this.cashOutService.cashOutByTopUp(user.getName(), ntdtCmd.mobile, PhoneCardType.getPhoneCardById((int)ntdtCmd.amount), ntdtCmd.type, false);
                            ResultNapTienDienThoaiMsg ntdtmsg = new ResultNapTienDienThoaiMsg();
                            ntdtmsg.Error = (byte)cres.getCode();
                            ntdtmsg.currentMoney = cres.getCurrentMoney();
                            this.send((BaseMsg)ntdtmsg, user);
                        }
                    }
                } else {
                    msg.Error = (byte)code;
                    this.send((BaseMsg)msg, user);
                }
            } else {
                msg.Error = 2;
                this.send((BaseMsg)msg, user);
            }
        }
        catch (Exception e) {
            msg.Error = 2;
            this.send((BaseMsg)msg, user);
            Debug.trace((Object)("LobbyModule check otp error: " + e));
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStackTrace = sw.toString(); // stack trace as a string
            Debug.trace((Object)sStackTrace);
        }
    }

    private byte parseErrorCodeGiftCode(String errorCode) {
        if (errorCode.equals("0")) {
            return 2;
        }
        if (errorCode.equals("10002")) {
            return 1;
        }
        if (errorCode.equals("10003")) {
            return 3;
        }
        if (errorCode.equals("10004")) {
            return 4;
        }
        return 0;
    }

    public void handleServerEvent(IBZEvent ibzevent) throws BZException {
        if (ibzevent.getType() == BZEventType.USER_DISCONNECT) {
            User user = (User)ibzevent.getParameter((IBZEventParam)BZEventParam.USER);
            this.unSubscribeJackPot(user);
        }
    }

    private void subscribeJackPot(User user) {
        this.usersSubJackpot.add(user);
    }

    private void unSubscribeJackPot(User user) {
        this.usersSubJackpot.remove((Object)user);
    }

    public void updateJackpot() {
        try {
            CacheServiceImpl cacheService = new CacheServiceImpl();
            int miniPoker100 = cacheService.getValueInt(Games.MINI_POKER.getName() + "_vin_100");
            int miniPoker1000 = cacheService.getValueInt(Games.MINI_POKER.getName() + "_vin_1000");
            int miniPoker10000 = cacheService.getValueInt(Games.MINI_POKER.getName() + "_vin_10000");
            int pokeGo100 = cacheService.getValueInt(Games.POKE_GO.getName() + "_vin_100");
            int pokeGo1000 = cacheService.getValueInt(Games.POKE_GO.getName() + "_vin_1000");
            int pokeGo10000 = cacheService.getValueInt(Games.POKE_GO.getName() + "_vin_10000");
            int khoBau100 = cacheService.getValueInt(Games.KHO_BAU.getName() + "_vin_100");
            int khoBau1000 = cacheService.getValueInt(Games.KHO_BAU.getName() + "_vin_1000");
            int khoBau10000 = cacheService.getValueInt(Games.KHO_BAU.getName() + "_vin_10000");
            int ndv100 = cacheService.getValueInt(Games.NU_DIEP_VIEN.getName() + "_vin_100");
            int ndv1000 = cacheService.getValueInt(Games.NU_DIEP_VIEN.getName() + "_vin_1000");
            int ndv10000 = cacheService.getValueInt(Games.NU_DIEP_VIEN.getName() + "_vin_10000");
            int avengers100 = cacheService.getValueInt(Games.AVENGERS.getName() + "_vin_100");
            int avengers1000 = cacheService.getValueInt(Games.AVENGERS.getName() + "_vin_1000");
            int avengers10000 = cacheService.getValueInt(Games.AVENGERS.getName() + "_vin_10000");
            int vqv100 = cacheService.getValueInt(Games.VUONG_QUOC_VIN.getName() + "_vin_100");
            int vqv1000 = cacheService.getValueInt(Games.VUONG_QUOC_VIN.getName() + "_vin_1000");
            int vqv10000 = cacheService.getValueInt(Games.VUONG_QUOC_VIN.getName() + "_vin_10000");
            int fish100 = cacheService.getValueInt(Games.HAM_CA_MAP.getName() + "_vin_100");
            int fish1000 = cacheService.getValueInt(Games.HAM_CA_MAP.getName() + "_vin_1000");

            //spartan game

            int spartan100 = cacheService.getValueInt(Games.LADY_NIGHT.getName() + "_vin_100");
            int spartan1000 = cacheService.getValueInt(Games.LADY_NIGHT.getName() + "_vin_1000");
            int spartan5000 = cacheService.getValueInt(Games.LADY_NIGHT.getName() + "_vin_5000");
            int spartan10000 = cacheService.getValueInt(Games.LADY_NIGHT.getName() + "_vin_10000");

            UpdateJackpotMsg msg = new UpdateJackpotMsg();
            msg.potMiniPoker100 = miniPoker100;
            msg.potMiniPoker1000 = miniPoker1000;
            msg.potMiniPoker10000 = miniPoker10000;
            msg.potPokeGo100 = pokeGo100;
            msg.potPokeGo1000 = pokeGo1000;
            msg.potPokeGo10000 = pokeGo10000;
            msg.potKhoBau100 = khoBau100;
            msg.potKhoBau1000 = khoBau1000;
            msg.potKhoBau10000 = khoBau10000;
            msg.potNDV100 = ndv100;
            msg.potNDV1000 = ndv1000;
            msg.potNDV10000 = ndv10000;
            msg.potAvengers100 = avengers100;
            msg.potAvengers1000 = avengers1000;
            msg.potAvengers10000 = avengers10000;
            msg.vqv100 = vqv100;
            msg.vqv1000 = vqv1000;
            msg.vqv10000 = vqv10000;
            msg.fish100 = fish100;
            msg.fish1000 = fish1000;

            msg.sparta100 = spartan100;
            msg.sparta1000 = spartan1000;
            msg.sparta5000 = spartan1000;
            msg.sparta10000 = spartan10000;
            for (User user : this.usersSubJackpot) {
                if (user == null) continue;
                this.send((BaseMsg)msg, user);
            }
        }
        catch (Exception e) {
            Debug.trace((Object)("Update jackpot exception: " + e.getMessage()));
        }
    }

    public void updateHuVang(User user) {
        HuVangMsg msg = new HuVangMsg();
        msg.huBaCay = HuVangConfig.instance().getThoiGianHuVang(Games.BA_CAY.getName());
        msg.huBaiCao = HuVangConfig.instance().getThoiGianHuVang(Games.BAI_CAO.getName());
        msg.huBinh = HuVangConfig.instance().getThoiGianHuVang(Games.BINH.getName());
        msg.huSam = HuVangConfig.instance().getThoiGianHuVang(Games.SAM.getName());
        msg.huTLMN = HuVangConfig.instance().getThoiGianHuVang(Games.TLMN.getName());
        this.send((BaseMsg)msg, user);
    }

    private void gameLoop() {
        ++this.countUpdateJackpot;
        if (this.countUpdateJackpot >= (long)ConfigGame.getIntValue("update_jackpot_time")) {
            this.updateJackpot();
            this.countUpdateJackpot = 0L;
        }
    }

    private void getEventInfo(User user, DataCmd dataCmd) {
        GetEventVPInfoMsg msg = new GetEventVPInfoMsg();
        HazelcastInstance instance = HazelcastClientFactory.getInstance();
        IMap map = instance.getMap("cacheConfig");
        String sLucky = (String)map.get((Object)"VIPPOINT_EVENT_LUCKY");
        if (sLucky != null && sLucky.equals("1")) {
            Date now = new Date();
            try {
                String currentDate = VinPlayUtils.getDateTimeStr((Date)now).substring(0, 11);
                String etLuckyToday = currentDate + VippointUtils.END_LUCKY_TIME;
                Date eventLuckyTimeEnd = VinPlayUtils.getDateTime((String)etLuckyToday);
                if (now.getTime() < eventLuckyTimeEnd.getTime()) {
                    msg.status = 1;
                    msg.time = (eventLuckyTimeEnd.getTime() - now.getTime()) / 1000L;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            msg.status = 0;
            msg.time = 0L;
        }
        this.send((BaseMsg)msg, user);
    }

    private void initVP() throws JSONException, SQLException, ParseException {
        VippointUtils.init();
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        this.eventTimeStart = VinPlayUtils.getDateTime((String)VippointUtils.START);
        this.eventTimeEnd = VinPlayUtils.getDateTime((String)VippointUtils.END);
        Date eventX2End = VippointUtils.END_X2_TIME;
        Date eventLuckyTimeStart = VinPlayUtils.getDateTime((String)(VippointUtils.START.substring(0, 11) + VippointUtils.START_LUCKY_TIME));
        Date eventLuckyTimeEnd = VinPlayUtils.getDateTime((String)(VippointUtils.START.substring(0, 11) + VippointUtils.END_LUCKY_TIME));
        this.timeLucky = eventLuckyTimeEnd.getTime() - eventLuckyTimeStart.getTime();
        Debug.trace((Object)("time lucky: " + this.timeLucky));
        HazelcastInstance instance = HazelcastClientFactory.getInstance();
        IMap map = instance.getMap("cacheConfig");
        if (now.getTime() < this.eventTimeStart.getTime()) {
            Debug.trace((Object)("event chua dien ra: " + new Date()));
            map.put((Object)"VIPPOINT_EVENT_STATUS", (Object)"0");
            map.put((Object)"VIPPOINT_EVENT_X2_STATUS", (Object)"0");
            map.put((Object)"VIPPOINT_EVENT_LUCKY", (Object)"0");
            BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable)this.eventStartTask, this.calculateRemainTime(this.eventTimeStart), TimeUnit.SECONDS);
            BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable)this.eventEndTask, this.calculateRemainTime(this.eventTimeEnd), TimeUnit.SECONDS);
            BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable)this.eventX2EndTask, this.calculateRemainTime(eventX2End), TimeUnit.SECONDS);
            BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable)this.eventLuckyStartTask, this.calculateRemainTime(eventLuckyTimeStart), TimeUnit.SECONDS);
            BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable)this.eventLuckyEndTask, this.calculateRemainTime(eventLuckyTimeEnd), TimeUnit.SECONDS);
        } else if (now.getTime() < this.eventTimeEnd.getTime()) {
            Debug.trace((Object)"event dang dien ra");
            map.put((Object)"VIPPOINT_EVENT_STATUS", (Object)"1");
            if (now.getTime() < eventX2End.getTime()) {
                Debug.trace((Object)"event x2 dang dien ra");
                map.put((Object)"VIPPOINT_EVENT_X2_STATUS", (Object)"1");
                BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable)this.eventX2EndTask, this.calculateRemainTime(eventX2End), TimeUnit.SECONDS);
            } else {
                Debug.trace((Object)"event x2 da ket thuc");
                map.put((Object)"VIPPOINT_EVENT_X2_STATUS", (Object)"0");
            }
            BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable)this.eventEndTask, this.calculateRemainTime(this.eventTimeEnd), TimeUnit.SECONDS);
            String currentDate = VinPlayUtils.getDateTimeStr((Date)now).substring(0, 11);
            String stLuckyToday = currentDate + VippointUtils.START_LUCKY_TIME;
            String etLuckyToday = currentDate + VippointUtils.END_LUCKY_TIME;
            eventLuckyTimeStart = VinPlayUtils.getDateTime((String)stLuckyToday);
            eventLuckyTimeEnd = VinPlayUtils.getDateTime((String)etLuckyToday);
            if (now.getTime() < eventLuckyTimeStart.getTime()) {
                Debug.trace((Object)"event lucky chua dien ra");
                map.put((Object)"VIPPOINT_EVENT_LUCKY", (Object)"0");
                BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable)this.eventLuckyStartTask, this.calculateRemainTime(eventLuckyTimeStart), TimeUnit.SECONDS);
                BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable)this.eventLuckyEndTask, this.calculateRemainTime(eventLuckyTimeEnd), TimeUnit.SECONDS);
            } else if (now.getTime() < eventLuckyTimeEnd.getTime()) {
                Debug.trace((Object)"event lucky dang dien ra");
                List unluckyTime = VippointUtils.randomUnluckyTime((Date)this.eventTimeStart, (Date)this.eventTimeEnd);
                Debug.trace((Object)"unluckyTime: ");
                for (Object dt : unluckyTime) {
                    if (now.getTime() < ((Date)dt).getTime()) {
                        BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable)this.eventUnluckyTask, this.calculateRemainTime((Date)dt) / 60, TimeUnit.MINUTES);
                        Debug.trace((Object)("OK: " + VinPlayUtils.getDateTimeStr((Date)dt)));
                        continue;
                    }
                    Debug.trace((Object)("NOK: " + VinPlayUtils.getDateTimeStr((Date)dt)));
                }
                List<Date> luckyTime = VippointUtils.randomLuckyTime((Date)this.eventTimeStart, (Date)this.eventTimeEnd);
                Debug.trace((Object)"luckyTime: ");
                for (Date dt : luckyTime) {
                    if (now.getTime() < dt.getTime()) {
                        BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable)this.eventLuckyTask, this.calculateRemainTime(dt) / 60, TimeUnit.MINUTES);
                        Debug.trace((Object)("OK: " + VinPlayUtils.getDateTimeStr((Date)dt)));
                        continue;
                    }
                    Debug.trace((Object)("NOK: " + VinPlayUtils.getDateTimeStr((Date)dt)));
                }
                String sLucky = (String)map.get((Object)"VIPPOINT_EVENT_LUCKY");
                if (sLucky != null && sLucky.equals("0")) {
                    this.sendMsgToAllUser((byte)1, eventLuckyTimeEnd.getTime() - now.getTime());
                }
                map.put((Object)"VIPPOINT_EVENT_LUCKY", (Object)"1");
                cal.setTime(eventLuckyTimeStart);
                cal.add(5, 1);
                eventLuckyTimeStart = cal.getTime();
                BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable)this.eventLuckyStartTask, this.calculateRemainTime(eventLuckyTimeStart), TimeUnit.SECONDS);
                BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable)this.eventLuckyEndTask, this.calculateRemainTime(eventLuckyTimeEnd), TimeUnit.SECONDS);
            } else {
                Debug.trace((Object)"event lucky da het");
                String sLucky = (String)map.get((Object)"VIPPOINT_EVENT_LUCKY");
                if (sLucky != null && sLucky.equals("1")) {
                    this.sendMsgToAllUser((byte)0, 0L);
                }
                map.put((Object)"VIPPOINT_EVENT_LUCKY", (Object)"0");
                cal.setTime(eventLuckyTimeStart);
                cal.add(5, 1);
                eventLuckyTimeStart = cal.getTime();
                BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable)this.eventLuckyStartTask, this.calculateRemainTime(eventLuckyTimeStart), TimeUnit.SECONDS);
                cal.setTime(eventLuckyTimeEnd);
                cal.add(5, 1);
                eventLuckyTimeEnd = cal.getTime();
                BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable)this.eventLuckyEndTask, this.calculateRemainTime(eventLuckyTimeEnd), TimeUnit.SECONDS);
            }
        } else {
            Debug.trace((Object)"event da ket thuc");
            map.put((Object)"VIPPOINT_EVENT_STATUS", (Object)"0");
            map.put((Object)"VIPPOINT_EVENT_X2_STATUS", (Object)"0");
            String sLucky = (String)map.get((Object)"VIPPOINT_EVENT_LUCKY");
            if (sLucky != null && sLucky.equals("1")) {
                this.sendMsgToAllUser((byte)0, 0L);
            }
            map.put((Object)"VIPPOINT_EVENT_LUCKY", (Object)"0");
        }
    }

    private int calculateRemainTime(Date runTime) {
        int time = 0;
        Date now = new Date();
        if (runTime.getTime() > now.getTime()) {
            time = (int)((runTime.getTime() - now.getTime()) / 1000L);
        }
        return time;
    }

    private void sendMsgToAllUser(byte status, long time) {
        GetEventVPInfoMsg msg = new GetEventVPInfoMsg();
        msg.status = status;
        msg.time = time / 1000L;
        ServerUtil.sendMsgToAllUsers(msg);
    }

    private final class GameLoopTask
    implements Runnable {
        private GameLoopTask() {
        }

        @Override
        public void run() {
            LobbyModule.this.gameLoop();
        }
    }

    private final class EventStartTask
    implements Runnable {
        private EventStartTask() {
        }

        @Override
        public void run() {
            Debug.trace((Object)("vippoint event start: " + new Date()));
            HazelcastInstance instance = HazelcastClientFactory.getInstance();
            IMap map = instance.getMap("cacheConfig");
            map.put((Object)"VIPPOINT_EVENT_STATUS", (Object)"1");
            map.put((Object)"VIPPOINT_EVENT_X2_STATUS", (Object)"1");
        }
    }

    private final class EventEndTask
    implements Runnable {
        private EventEndTask() {
        }

        @Override
        public void run() {
            Debug.trace((Object)("vippoint event end: " + new Date()));
            HazelcastInstance instance = HazelcastClientFactory.getInstance();
            IMap map = instance.getMap("cacheConfig");
            map.put((Object)"VIPPOINT_EVENT_STATUS", (Object)"0");
            map.put((Object)"VIPPOINT_EVENT_X2_STATUS", (Object)"0");
            String sLucky = (String)map.get((Object)"VIPPOINT_EVENT_LUCKY");
            if (sLucky != null && sLucky.equals("1")) {
                LobbyModule.this.sendMsgToAllUser((byte)0, 0L);
            }
            map.put((Object)"VIPPOINT_EVENT_LUCKY", (Object)"0");
        }
    }

    private final class EventX2EndTask
    implements Runnable {
        private EventX2EndTask() {
        }

        @Override
        public void run() {
            Debug.trace((Object)("event x2 end: " + new Date()));
            HazelcastInstance instance = HazelcastClientFactory.getInstance();
            IMap map = instance.getMap("cacheConfig");
            map.put((Object)"VIPPOINT_EVENT_X2_STATUS", (Object)"0");
        }
    }

    private final class EventLuckyStartTask
    implements Runnable {
        private EventLuckyStartTask() {
        }

        @Override
        public void run() {
            Date now = new Date();
            if (now.getTime() < LobbyModule.this.eventTimeEnd.getTime()) {
                Debug.trace((Object)("event lucky start: " + now));
                try {
                    VippointUtils.init();
                    HazelcastInstance instance = HazelcastClientFactory.getInstance();
                    IMap map = instance.getMap("cacheConfig");
                    String sLucky = (String)map.get((Object)"VIPPOINT_EVENT_LUCKY");
                    if (sLucky != null && sLucky.equals("0")) {
                        Debug.trace((Object)("send message to all user: " + now));
                        LobbyModule.this.sendMsgToAllUser((byte)1, LobbyModule.this.timeLucky);
                    }
                    map.put((Object)"VIPPOINT_EVENT_LUCKY", (Object)"1");
                    List unluckyTime = VippointUtils.randomUnluckyTime((Date)LobbyModule.this.eventTimeStart, (Date)LobbyModule.this.eventTimeEnd);
                    Debug.trace((Object)"unluckyTime: ");
                    for (Object dt : unluckyTime) {
                        if (now.getTime() < ((Date)dt).getTime()) {
                            BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable)LobbyModule.this.eventUnluckyTask, LobbyModule.this.calculateRemainTime((Date)dt) / 60, TimeUnit.MINUTES);
                            Debug.trace((Object)("OK: " + VinPlayUtils.getDateTimeStr((Date)dt)));
                            continue;
                        }
                        Debug.trace((Object)("NOK: " + VinPlayUtils.getDateTimeStr((Date)dt)));
                    }
                    List<Date> luckyTime = VippointUtils.randomLuckyTime((Date)LobbyModule.this.eventTimeStart, (Date)LobbyModule.this.eventTimeEnd);
                    Debug.trace((Object)"luckyTime: ");
                    for (Date dt : luckyTime) {
                        if (now.getTime() < dt.getTime()) {
                            BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable)LobbyModule.this.eventLuckyTask, LobbyModule.this.calculateRemainTime(dt) / 60, TimeUnit.MINUTES);
                            Debug.trace((Object)("OK: " + VinPlayUtils.getDateTimeStr((Date)dt)));
                            continue;
                        }
                        Debug.trace((Object)("NOK: " + VinPlayUtils.getDateTimeStr((Date)dt)));
                    }
                    BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable)LobbyModule.this.eventLuckyStartTask, 24, TimeUnit.HOURS);
                }
                catch (Exception e) {
                    Debug.trace((Object)("event lucky start error: " + e));
                }
            }
        }
    }

    private final class EventLuckyEndTask
    implements Runnable {
        private EventLuckyEndTask() {
        }

        @Override
        public void run() {
            Date now = new Date();
            if (now.getTime() < LobbyModule.this.eventTimeEnd.getTime()) {
                Debug.trace((Object)("event lucky end: " + now));
                HazelcastInstance instance = HazelcastClientFactory.getInstance();
                IMap map = instance.getMap("cacheConfig");
                String sLucky = (String)map.get((Object)"VIPPOINT_EVENT_LUCKY");
                if (sLucky != null && sLucky.equals("1")) {
                    Debug.trace((Object)("send message to all user: " + now));
                    LobbyModule.this.sendMsgToAllUser((byte)0, 0L);
                }
                map.put((Object)"VIPPOINT_EVENT_LUCKY", (Object)"0");
                BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable)LobbyModule.this.eventLuckyEndTask, 24, TimeUnit.HOURS);
            }
        }
    }

    private final class EventUnluckyTask
    implements Runnable {
        private EventUnluckyTask() {
        }

        @Override
        public void run() {
            Debug.trace((Object)("sub vippoint: " + new Date()));
            EventVPUnluckyMsg msg = new EventVPUnluckyMsg();
            ServerUtil.sendMsgToAllUsers(msg);
            List<String> users = LobbyModule.this.vpService.subVippointEvent();
            for (String nickname : users) {
                HasNewMailMsg mailMsg = new HasNewMailMsg();
                MiniGameUtils.sendMessageToUser(mailMsg, nickname);
            }
        }
    }

    private final class EventluckyTask
    implements Runnable {
        private EventluckyTask() {
        }

        @Override
        public void run() {
            Debug.trace((Object)("add vippoint: " + new Date()));
            List<String> users = LobbyModule.this.vpService.addVippointEvent();
            for (String nickname : users) {
                HasNewMailMsg mailMsg = new HasNewMailMsg();
                MiniGameUtils.sendMessageToUser(mailMsg, nickname);
            }
        }
    }
    public int getEsmsOTP(String nickname, String mobile, String type) throws Exception {
        int code = 1;
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap<String, UserModel> userMap = client.getMap("users");
        UserModel model = null;
        if (userMap.containsKey((Object)nickname)) {
            model = (UserModel)userMap.get((Object)nickname);
            UserCacheModel userCacheModel = (UserCacheModel)model;
        } else {
            UserDaoImpl dao = new UserDaoImpl();
            model = dao.getUserByNickName(nickname);
        }
        if (model != null) {
            if (model.getMobile() != null && !model.getMobile().isEmpty() && model.isHasMobileSecurity()) {
                OtpDaoImpl otpDao = new OtpDaoImpl();
                String mobile2 = this.revertMobile(model.getMobile());
                String otp = null;
                try {
                    otp = VinPlayUtils.genOtpSMS((String)model.getMobile(), (String)"");
                    Debug.trace("Lobby OTP: " + model.getMobile());
                }
                catch (Exception e) {
                    Debug.trace("Mobile: " + model.getMobile() + "---");
//                    logger.debug((Object)e);
                }
                otpDao.updateOtpSMS(model.getMobile(), otp, "OZZ OTP");
                AlertServiceImpl service = new AlertServiceImpl();

                String content = String.format(GameCommon.MESSAGE_OTP_SUCCESS, otp, VinPlayUtils.getCurrentDate());
                if ("ESMS".equals(PartnerConfig.SMSPartner)) {
                        boolean rs = service.SendSMSEsms(model.getMobile(), otp);
                    } else if ("ESMS_VOICE".equals(PartnerConfig.SMSPartner)) {
                        boolean rs = service.SendVoiceOTPESMS(model.getMobile(), otp);
                    } else if ("RUTCUOC".equals(PartnerConfig.SMSPartner)) {
                        boolean rs = service.SendSMSRutCuoc(model.getMobile(), otp);
                    } else {
                        boolean rs = service.SendSMSAirpay(model.getMobile(), otp);
                    }
                code = 0;
            } else {
                code = 4;
            }
        } else {
            Debug.trace("Model is ");
            code = 2;
        }
        return code;
    }
    public String revertMobile(String mobile) {
        if (mobile.substring(0, 2).equals("84")) {
            return "0" + mobile.substring(2);
        }
        return mobile;
    }





    private final class SlotDailyTask
    implements Runnable {
        private SlotDailyTask() {
        }

        @Override
        public void run() {
            try {
                LuckyUtils.initSlotMap();
                DvtUtils.initDVT((boolean)false);
            }
            catch (Exception e) {
                e.printStackTrace();
                Debug.trace((Object)("init slot free errot: " + e));
            }
            BitZeroServer.getInstance().getTaskScheduler().schedule(LobbyModule.this.slotDailyTask, 24, TimeUnit.HOURS);
        }
    }
    
    
    private final class UpdateCardTransactionTask
    implements Runnable {
        private UpdateCardTransactionTask() {
            
        }

        @Override
        public void run() {
            // get pending card in minutes
            try
            {
                List<Document> pendingTrans = rechargeDAO.getRechargeByGachtheRecently();
                if (pendingTrans.size() > 0)
                {
                    pendingTrans.forEach((document) -> {
                        NapTheDienThoaiMsg msg = new NapTheDienThoaiMsg();
                        if (document.getInteger("code") == 0)
                        {                        
                            msg.Error = Byte.parseByte(document.getInteger("code").toString());
                            msg.currentMoney = document.getLong("current_money") + document.getLong("add_money");
                            msg.numFail = 0;
                            msg.timeFail = 0;
                        }   
                        else
                        {
                            msg.Error = (byte)35;
                        }

                        Debug.trace((Object)("Send Noti Recharge Gachthe error: " + msg.Error));
                        //User user = ExtensionUtility.globalUserManager.getUserById(document.getInteger("user_id"));
                        //User user = ExtensionUtility.getExtension().getApi().getUserById(document.getInteger("user_id"));
                        List<User> user = ExtensionUtility.getExtension().getApi().getUserByName(document.getString("nick_name"));
                        if (user != null)
                        {
                            // lấy theo user name 
                            rechargeDAO.UpdateGachtheTransctionsSent(document.getString("request_id"));
                            send((BaseMsg)msg, user);
                        }
                        else
                        {
                            user = ExtensionUtility.getExtension().getApi().getUserByName(document.getString("username"));
                            if (user != null)
                            {
                                rechargeDAO.UpdateGachtheTransctionsSent(document.getString("request_id"));
                                send((BaseMsg)msg, user);
                            }
                            else
                            {
                                user = ExtensionUtility.globalUserManager.getUserByName(document.getString("username"));
                                rechargeDAO.UpdateGachtheTransctionsSent(document.getString("request_id"));
                                send((BaseMsg)msg, user);
                            }
                        }
                    });
                }
            }
            catch (Exception e)
            {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                String sStackTrace = sw.toString(); // stack trace as a string
                Debug.trace((Object)sStackTrace);               
                Debug.trace((Object)("update trans error: " + e));
            }
        }
    }
}

