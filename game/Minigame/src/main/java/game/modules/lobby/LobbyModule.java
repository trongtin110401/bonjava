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
import bitzero.server.core.*;
import bitzero.server.entities.User;
import bitzero.server.exceptions.BZException;
import bitzero.server.extensions.BaseClientRequestHandler;
import bitzero.server.extensions.data.BaseMsg;
import bitzero.server.extensions.data.DataCmd;
import bitzero.util.ExtensionUtility;
import bitzero.util.common.business.Debug;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vinplay.common.HttpCommon;
import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.dal.dao.LogMoneyUserDao;
import com.vinplay.dal.dao.impl.LogMoneyUserDaoImpl;
import com.vinplay.dal.service.BroadcastMessageService;
import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.AgentServiceImpl;
import com.vinplay.dal.service.impl.BroadcastMessageServiceImpl;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.dal.service.impl.CardBanMoneyService;
import com.vinplay.dichvuthe.dao.CashoutDao;
import com.vinplay.dichvuthe.dao.RechargeDao;
import com.vinplay.dichvuthe.dao.impl.CashoutDaoImpl;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.DepositBankModel;
import com.vinplay.dichvuthe.entities.DepositMomoModel;
import com.vinplay.dichvuthe.entities.DepositOnePayModel;
import com.vinplay.dichvuthe.response.*;
import com.vinplay.dichvuthe.service.CashOutService;
import com.vinplay.dichvuthe.service.RechargeMomoService;
import com.vinplay.dichvuthe.service.RechargeService;
import com.vinplay.dichvuthe.service.impl.AlertServiceImpl;
import com.vinplay.dichvuthe.service.impl.CashOutServiceImpl;
import com.vinplay.dichvuthe.service.impl.RechargeMomoServiceImpl;
import com.vinplay.dichvuthe.service.impl.RechargeServiceImpl;
import com.vinplay.dichvuthe.utils.DvtConst;
import com.vinplay.dichvuthe.utils.DvtUtils;
import com.vinplay.lognaprut.HistoryTransConst;
import com.vinplay.lognaprut.service.HistoryTransService;
import com.vinplay.lognaprut.service.impl.HistoryTransServiceImpl;
import com.vinplay.payment.entities.UserWithDrawCard;
import com.vinplay.payment.entities.UserWithdraw;
import com.vinplay.payment.entities.UserWithdrawMomo;
import com.vinplay.secretcode.entity.UserSecretEntity;
import com.vinplay.secretcode.service.impl.UserSecretServiceImpl;
import com.vinplay.usercore.dao.GiftCodeDAO;
import com.vinplay.usercore.dao.impl.GiftCodeDAOImpl;
import com.vinplay.usercore.dao.impl.OtpDaoImpl;
import com.vinplay.usercore.dao.impl.UserDaoImpl;
import com.vinplay.usercore.entities.TransferMoneyResponse;
import com.vinplay.usercore.entities.VippointResponse;
import com.vinplay.usercore.service.*;
import com.vinplay.usercore.service.impl.*;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.usercore.utils.LuckyUtils;
import com.vinplay.usercore.utils.PartnerConfig;
import com.vinplay.usercore.utils.VippointUtils;
import com.vinplay.utils.SlotNohuObject;
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.enums.PhoneCardType;
import com.vinplay.vbee.common.enums.Platform;
import com.vinplay.vbee.common.enums.ProviderType;
import com.vinplay.vbee.common.exceptions.KeyNotFoundException;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.response.*;
import com.vinplay.vbee.common.statics.TransType;
import com.vinplay.vbee.common.utils.DateTimeUtils;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import game.entities.QuotaResponse;
import game.modules.lobby.cmd.rev.*;
import game.modules.lobby.cmd.send.*;
import game.modules.minigame.cmd.MiniGameCMD;
import game.modules.minigame.room.MGRoom;
import game.modules.minigame.utils.MiniGameUtils;
import game.utils.ConfigGame;
import game.utils.GameUtils;
import game.utils.HuVangConfig;
import game.utils.ServerUtil;
import okhttp3.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class LobbyModule extends BaseClientRequestHandler {
    private static final String CURRENT_COMMAND = "cmd";
    private static final String CURRENT_OBJECT_COMMAND = "obj_cmd";
    private static final String FORCE_CHECK_OTP = "force_check_otp";
    private static final String UPDATE_NEW_MOBILE = "update_new_mobile";
    private GameLoopTask gameLoopTask = new GameLoopTask();
    private UpdateCardTransactionTask cardTransactionTask = new UpdateCardTransactionTask();
    private UserService userService = new UserServiceImpl();
    private RechargeService rechargeService = new RechargeServiceImpl();
    private RechargeMomoService rechargeMomoService = new RechargeMomoServiceImpl();
    private RechargeDao rechargeDAO = new RechargeDaoImpl();
    private GiftCodeService gfService = new GiftCodeServiceImpl();
    private CashOutService cashOutService = new CashOutServiceImpl();
    private OtpService otpService = new OtpServiceImpl();
    private SecurityService securityService = new SecurityServiceImpl();
    private VippointService vpService = new VippointServiceImpl();
    private Set<User> usersSubJackpot = new HashSet<User>();
    private HashMap<String, User> listuserCr = new HashMap<>();
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
    private UserSecretServiceImpl userSecretService = new UserSecretServiceImpl();
    Map<String, String> mapUserOnePay = new HashMap<>();

    private CacheService cacheService = new CacheServiceImpl();
    private BroadcastMessageService broadcastMessageService = new BroadcastMessageServiceImpl();

    private final CheckMoMoTransStatusTask checkOnePayStatusTask = new CheckMoMoTransStatusTask();
    private final CheckCodePayTransStatusTask checkCodePayStatusTask = new CheckCodePayTransStatusTask();
    private final CheckOutGameTask checkOutGameTask = new CheckOutGameTask();
    private final CheckCodePayHuyStatusTask checkCodePayHuyStatusTask = new CheckCodePayHuyStatusTask();
    private final BroadcastMessageTask broadcastMessageTask = new BroadcastMessageTask();
    private final CheckMoneyUser checkMoneyUser = new CheckMoneyUser();

    private static final org.apache.log4j.Logger logger = Logger.getLogger((String) "recharge");

    public void init() {
        super.init();
        this.getParentExtension().addEventListener(BZEventType.USER_DISCONNECT, this);
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.gameLoopTask, 10, 1, TimeUnit.SECONDS);
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.cardTransactionTask, 30, 30, TimeUnit.SECONDS);
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.checkMoneyUser, 10, 1, TimeUnit.SECONDS);
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.checkOnePayStatusTask, 1, 10, TimeUnit.MINUTES);
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.checkCodePayStatusTask, 1, 1, TimeUnit.SECONDS);
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.checkOutGameTask, 1, 500, TimeUnit.MILLISECONDS);
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.checkCodePayHuyStatusTask, 1, 1, TimeUnit.SECONDS);
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.broadcastMessageTask, 1, 70, TimeUnit.SECONDS);
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this::loginFromOtherDevice, 1, 100, TimeUnit.MILLISECONDS);
        try {
            this.initVP();
            MongoDBConnectionFactory.init();
        } catch (Exception e) {
            Debug.trace("init vippoint event error " + e);
        }
        try {
            LuckyUtils.initSlotMap();
            DvtUtils.initDVT(true);
        } catch (Exception e) {
            Debug.trace("init slot free error " + e);
        }
        try {
            PartnerConfig.ReadConfig();
        } catch (Exception e) {
            Debug.trace("init partnerconfig event error " + e);
        }
        long currentTime = System.currentTimeMillis() / 1000L;
        long endToday = DateTimeUtils.getEndTimeToDayAsLong() / 1000L;
        int n = (int) (endToday - currentTime);
        BitZeroServer.getInstance().getTaskScheduler().schedule(this.slotDailyTask, n + 5, TimeUnit.SECONDS);
        logger.debug("LobbyModule Init");
        CacheService cacheService = new CacheServiceImpl();
        cacheService.setValue("nap_bank", "false");
        cacheService.setValue("nap_one_pay", "false");
        cacheService.setValue("nap_momo", "false");
        cacheService.setValue("rut_tien_bank", "false");
        cacheService.setValue("rut_tien_card_phone", "false");
        cacheService.setValue("nap_card_phone", "false");
        cacheService.setValue("login_noti", "false");
        cacheService.setValue("number", 0);
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
                //todo : đổi mật khẩu
                this.doiPass(user, dataCmd);
                break;
            }
            case 20001: {
                //  this.doiVippoint(user, dataCmd);
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
            //todo : két sắt
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
                //todo : nạp thẻ điện thoại
                this.napTheDienThoai(user, dataCmd);
                break;
            }
            case 20045:
//            {
//             //   this.napVinCard(user, dataCmd);
//                break;
//            }
            case 20046:
                //   this.napMegaCard(user, dataCmd);
                break;
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
            case 20017: { // todo : giftcode
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
            case 20037:
//                {
//                // this.checkIAP(user, dataCmd);
//                break;
//            }
            case 20038:
//                {
                //    this.resultIAP(user, dataCmd);
                break;
//            }
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
                    .CMD_DEPOSIT_BANK_MANUAL: {
                this.depositBankManual(user, dataCmd);
                break;
            }
            case MiniGameCMD
                    .CMD_DEPOSIT_CODEPAY_MANUAL:
                _MANUAL:
                {
                    this.depositCodePayManual(user, dataCmd);
                    break;
                }
//            case MiniGameCMD
//                    .CMD_DEPOSIT_CODEPAY_TIME:_MANUAL: {
//                this.depositCodePayTime(user, dataCmd);
//                break;
//            }
            case MiniGameCMD
                    .CMD_DEPOSIT_MOMO_MANUAL: { // todo : nap bang momo
                this.depositMomoManual(user, dataCmd);
                break;
            }
            case 20203: { // todo : nap bang momo
                this.depositMomoComfirmManual(user, dataCmd);
                break;
            }
            case MiniGameCMD.CMD_WITHDRAW_CARD_MANUAL: {
                this.cashOutByCard(user, dataCmd);
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
            case 20205: {
                // todo : nap onepay
                this.depositOnePayBankManual(user, dataCmd);
                break;
            }
            case 20297: { // todo: nhận mã otp

                this.submitOTP(user, dataCmd);
                break;
            }
            case 20300: {
                this.lichSugiaoDichNguoiChoi(user, dataCmd);
                break;

            }
            case 20301: {
                this.transferMoneyToDaiLy(user, dataCmd);
                break;
            }
            case 20221: {
                this.getPhoneNumber(user);
                break;
            }
            case MiniGameCMD.CMD_TRANS_FER_MONEY_TO_USER: {
                this.transferMoneyToAnUser(user, dataCmd);
                break;
            }
            case 20302: {
                this.handleCreateSecretCode(user, dataCmd);
                break;
            }
        }
    }

    private void handleCreateSecretCode(User user, DataCmd dataCmd) {
        CreateSecretCodeCmd cmd = new CreateSecretCodeCmd(dataCmd);
        UserSecretEntity userSecret = new UserSecretEntity();
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap<String, UserModel> userMap = client.getMap("users");
        UserModel model = null;
        if (null == cmd.secretCode || null == cmd.secretCode.trim() || cmd.secretCode.length() < 4) {
            this.sendRequireCodeRes(user, 4);
            return;
        }
        if (userMap.containsKey(user.getName())) {
            model = userMap.get(user.getName());
        }
        // UserModel userModel2 = userService.getUserByUserName(user.get());
        if (model.getPassword().equals(cmd.password)) {
            if (userSecretService.findCode(user.getName()) == null) {
                userSecret.username = user.getName();
                userSecret.code = cmd.secretCode;
                userSecretService.insertUserSecret(userSecret);
                this.sendRequireCodeRes(user, 1);
            } else {
                this.sendRequireCodeRes(user, 4);
            }

        } else {
            this.sendRequireCodeRes(user, 3); // todo loi vi ko dung mat khau
        }

        // user.


    }


    private void sendRequireCodeRes(User user, int codeStep) { // todo : stepcode 0 - popup create 2factor code , 2 - popup wrong
        RequireCreateSecretCodeMsg msg = new RequireCreateSecretCodeMsg();
        msg.stepCode = codeStep;
        this.send(msg, user);
    }

    private int checkSecretCode(String code, String username) { // 0 can cap nhat secretcode

        UserSecretEntity currentCode = userSecretService.findCode(username);
        if (currentCode == null) return 0; // chua cap nhat code
        if (currentCode.code.equals(code)) return 1; // dung code

        return 2; // sai code
    }

    /**
     * Get phoneNumberUser
     *
     * @param user
     */
    private void getPhoneNumber(User user) {
        UserCacheModel userCacheModel = this.userService.getUser(user.getName());
        ResultGetPhoneNumber msg = new ResultGetPhoneNumber();
        if (userCacheModel != null && userCacheModel.getMobile() != null) {
            msg.Error = 1;
            msg.phoneNumber = Long.parseLong(userCacheModel.getMobile());
        }
        this.send(msg, user);
    }

    // todo : lich su giao dich nguoi choi
    private void lichSugiaoDichNguoiChoi(User user, DataCmd dataCmd) {
    }

    private void submitOTP(User user, DataCmd dataCmd) {
        OnePayOtpCmd cmd = new OnePayOtpCmd(dataCmd);
        this.rechargeService.UpdateDepositOnepayOTP(cmd.transId, cmd.otp, 103);
        RequestOnePayAction msg = new RequestOnePayAction();
        DepositOnePayModel depositOnePayModel = rechargeService.FindDepositOnePayById(cmd.transId);
        msg.stepCode = 1;
        msg.techcombankTrans = depositOnePayModel.Description;
        msg.currentMoney = this.userService.getCurrentMoneyUserCache(user.getName(), "vin");
        this.send(msg, user);

        // this.rechargeService.UpdateDepositStatusOnepay(mapUserOnePay.get(user),"103"); // todo 103 trạng thái đã submit otp
    }

    private synchronized void updateMomoTrans() {
        ArrayList<DepositMomoModel> listDepositPendingMomo = this.rechargeMomoService.GetListDepositPendingMomo();
        HistoryTransService historyTransService = new HistoryTransServiceImpl();
        for (DepositMomoModel model : listDepositPendingMomo) {
            this.rechargeMomoService.UpdateDepositMomoManualStatus(model.Id, DvtConst.STATUS_REJECT, "bị hủy vì không chyển khoản", "Tự động kiểm tra Momo");

            historyTransService.update(model.Id, model.Nickname, HistoryTransConst.MOMO, "Từ chối", "Chúng tôi chưa nhận được số tiền của quý khách vui lòng kiêm tra lại");

        }
    }

    private synchronized void updateCodePayTrans() {

    }


    private ArrayList<String> removeIteminList(ArrayList<String> listTrans, String transId) {
        for (int i = 0; i < listTrans.size(); i++) {
            if (listTrans.get(i).equals(transId)) {
                listTrans.remove(i);
            }
        }
        return listTrans;
    }


    //    public void handleServerEvent(IBZEvent ibzevent) throws BZException {
//        if (ibzevent.getType() == BZEventType.USER_DISCONNECT) {
//            User user = (User) ibzevent.getParameter((IBZEventParam) BZEventParam.USER);
//           // this.userDis(user);
//        }
//    }
    private void cashoutMomo(User user, DataCmd dataCmd) {
        try {
            CashoutMomoCmd cmd = new CashoutMomoCmd(dataCmd);
            String nickname = user.getName();
            UserWithdrawMomo userWithdraw = new UserWithdrawMomo(nickname, cmd.Amount, cmd.PhoneNumber);
            BaseResponseModel res = this.userService.UpdateMoneyWhenWithdrawMomo(userWithdraw);
            ResultCashoutMomo msg = new ResultCashoutMomo();
            int errorCode = Integer.parseInt(res.getErrorCode());
            msg.Error = (byte) errorCode;
            msg.currentMoney = errorCode == 0 ? this.userService.getCurrentMoneyUserCache(nickname, "vin") : 0;
            this.send((BaseMsg) msg, user);
        } catch (Exception e) {
            logger.debug(e);
        }
    }

    // todo : rút qua bank
    private synchronized void cashoutBank(User user, DataCmd dataCmd) {  // todo : thêm số tài khoản người gửi
        try {
            CashoutBankCmd cmd = new CashoutBankCmd(dataCmd);

//            int checkCodeStatus = this.checkSecretCode(cmd.secretCode,user.getName());
//            if(checkCodeStatus !=1){
//                sendRequireCodeRes(user,checkCodeStatus);
//                return;
//            }

            CheckNap checknap = new CheckNap();
            String nickname = user.getName();
            naptmp ntmp = checknap.tongnapThe(nickname);
            long tiennap = ntmp.getTongnap();
            long sodu = 0;
            long yeu_cau_rut = cmd.Amount;
            long tienrut = checknap.tongrut(nickname);
            long tongx = ntmp.getNapbank() + ntmp.getNapmomo();
            long rutx = tienrut * (-1);
            long ruty = rutx + yeu_cau_rut;
            CheckTienCuoc taixiu = new CheckTienCuoc();
            NapRutGame nrg = new NapRutGame();
            boolean check_onoff = nrg.OnOffAutoRut();
//            int yeu_cau_rut = 1;
//            int tongx = 1;
//            int ruty = 1;
//            int tiennap = 20000;

            if (tiennap >= 20000) {
                if (check_onoff == true && yeu_cau_rut < 10000000 && tongx > ruty) {

                    //Auto rut tien bank
                    String ACCESS_TOKEN_bank2 = "";
                    String ACCESS_TOKEN_bank3 = "";
                    String userAprrove = "";
                    String ACCESS_TOKEN = "";
                    double randomDouble = Math.random();
                    randomDouble = randomDouble * 100 + 1;
                    int randomInt = (int) randomDouble;
                    boolean checkBank = true;
                    int dudu = randomInt % 2;
                    if (dudu == 0) {
                        checkBank = true;
                    } else {
                        checkBank = false;
                    }
                    if (checkBank == true) {
                        ACCESS_TOKEN = ACCESS_TOKEN_bank2;
                        userAprrove = "Auto Rút Tiền Bank";
                    } else {
                        ACCESS_TOKEN = ACCESS_TOKEN_bank3;
                        userAprrove = "Auto Rút Tiền Bank";
                    }

                    String URL_CALL_BACK = "https://lunglinhlalenluons.store/api?c=4009";

                    UserWithdraw userWithdraw = new UserWithdraw(nickname, cmd.Amount, cmd.BankNumber, cmd.BankAccountName, cmd.BankName);
                    BaseResponseModel res = this.userService.UpdateMoneyWhenWithdrawBank(userWithdraw);
                    String transId = userWithdraw.Id;
                    //find trans
                    CashoutDao cashoutDao = new CashoutDaoImpl();
                    UserWithdraw userWithdrawx = cashoutDao.FindCashoutBankById(transId);
                    if (userWithdrawx == null) {
                        ResultCashoutBank msg = new ResultCashoutBank();
                        int errorCode = 1;
                        msg.Error = (byte) errorCode;
                        this.send((BaseMsg) msg, user);
                    } else {
                        AutoRutTien auRut = new AutoRutTien();
                        auRut.sendMesToAdmin(transId, 102);
                        CallAutoTransBankRut callBank = new CallAutoTransBankRut();
                        String output = callBank.CallAPI(userWithdraw, ACCESS_TOKEN, URL_CALL_BACK); //Product
                        String check_money_now = "Số dư tài khoản không đủ để thực hiện";
                        if (output.contains(check_money_now)) {
                            auRut.sendMesToAdmin(transId, 3); // Số dư tài khoản không đủ để thực hiện
                        }
                        // update trans
                        boolean updateTrans = cashoutDao.UpdateCashoutBank(transId, "sending", userAprrove);
                        if (!updateTrans) {
                            ResultCashoutBank msg = new ResultCashoutBank();
                            int errorCode = 1;
                            msg.Error = (byte) errorCode;
                            this.send((BaseMsg) msg, user);
                        } else {
                            HistoryTransService historyTransService = new HistoryTransServiceImpl();
                            historyTransService.update(transId, userWithdraw.Username, HistoryTransConst.RUT_BANK, "Đã duyệt", "Giao dịch thành công!");
                            BroadCastUserMoney.pushBroadCast(userWithdraw.Username);
                            ResultCashoutBank msg = new ResultCashoutBank();
                            int errorCode = 0;
                            msg.Error = (byte) errorCode;
                            this.send((BaseMsg) msg, user);
                            String codedl = nrg.getMaDaily(nickname);
                            long SoTien = yeu_cau_rut * (-1);
                            if (codedl == null) {
                                int xx = 2;
                            } else if (codedl != null && codedl.trim().length() == 0) {
                                int xx = 2;
                            } else if (codedl != null && codedl.trim().equalsIgnoreCase("null") == false) {
                                NapRutModel napgame = new NapRutModel(transId, nickname, codedl, SoTien, "Rut Bank", userWithdraw.CreatedAt);
                                if (nrg.getTransID(transId) == false) {
                                    nrg.NapRut(napgame);
                                }
                            } else {
                                int xx = 2;
                            }
                        }

                    }


                } else {
                    long taixi = 0;
                    UserWithdraw userWithdraw = new UserWithdraw(nickname, cmd.Amount, cmd.BankNumber, cmd.BankAccountName, cmd.BankName);
                    BaseResponseModel res = this.userService.UpdateMoneyWhenWithdrawBank(userWithdraw);
                    ResultCashoutBank msg = new ResultCashoutBank();
                    int errorCode = Integer.parseInt(res.getErrorCode());
                    msg.Error = (byte) errorCode;
                    msg.currentMoney = errorCode == 0 ? this.userService.getCurrentMoneyUserCache(nickname, "vin") : 0;
//                    sodu = this.userService.getCurrentMoneyUserCache(nickname, "vin");
                    BroadCastUserMoney.pushBroadCast(user.getName());
                    this.send((BaseMsg) msg, user);

                    long tienthe = ntmp.getNapthe();
                    long xinloc = checknap.xinloc(nickname);
                    long tongadmin = ntmp.getNapadmin();
                    checknap.Notify(nickname, tiennap, cmd.Amount, tienrut, tienthe, xinloc, tongadmin, sodu, tongx, taixi);
                }

            } else {
                ResultCashoutBank msg = new ResultCashoutBank();
                int errorCode = 1;
                msg.Error = (byte) errorCode;
                this.send((BaseMsg) msg, user);
            }


        } catch (Exception e) {
            logger.debug(e);
        }
    }

    // todo :rut tien qua the bang tay
    private synchronized void cashOutByCard(User user, DataCmd dataCmd) {
        try {

            CashoutCardCmd cmd = new CashoutCardCmd(dataCmd);
//            UserDAO udao = new UserDAO();
//            long tien = udao.GetVin(user.getName());

            long tien = this.userService.getCurrentMoneyUserCache(user.getName(), "vin");
            long sodu = tien - cmd.Amount;
            if (sodu < 50000) {
                return;
            }
            //todo check code status
//            int checkCodeStatus = this.checkSecretCode(cmd.secretCode,user.getName());
//            if(checkCodeStatus !=1){
//                sendRequireCodeRes(user,checkCodeStatus);
//                return;
//            }

            String nickname = user.getName();
            UserWithDrawCard withDrawCard = new UserWithDrawCard(nickname, cmd.TelcoId, cmd.Amount, cmd.Quantity);
            // đoạn này phải update

            BaseResponseModel res = this.userService.UpdateMoneyWhenWithdrawCard(withDrawCard);
            CashoutCardMsg msg = new CashoutCardMsg();
            int errorCode = Integer.parseInt(res.getErrorCode());
            msg.Error = (byte) errorCode;
            msg.CurrentMoney = errorCode == 0 ? this.userService.getCurrentMoneyUserCache(nickname, "vin") : 0;
            BroadCastUserMoney.pushBroadCast(user.getName());
            this.send((BaseMsg) msg, user);


        } catch (Exception e) {
            logger.debug(e);
        }

    }

    // todo : rut tien qua the auto mac dinh
    private void cashoutCard(User user, DataCmd dataCmd) {
        try {
            CashoutCardCmd cmd = new CashoutCardCmd(dataCmd);
            String nickname = user.getName();
            CashOutService sv = new CashOutServiceImpl();
            SoftpinResponse res = sv.cashOutByCardHungHa(nickname, cmd.TelcoId, cmd.Amount, cmd.Quantity);
            CashoutCardMsg msg = new CashoutCardMsg();
            msg.Error = (byte) res.getCode();
            msg.CurrentMoney = res.getCurrentMoney();
            msg.ListCard = res.getSoftpin();
            this.send((BaseMsg) msg, user);

        } catch (Exception e) {
            logger.error(e);
        }
    }

    private synchronized void depositMomoComfirmManual(User user, DataCmd dataCmd) { // tạo phiếu
        Debug.info("Call toi momo tao phieu");
        try {
            DepositSubmitMomoCmd cmd = new DepositSubmitMomoCmd(dataCmd);
            String nickname = user.getName();
            DepositMomoModel res = this.rechargeMomoService.FindDepositMomoById(cmd.transId);
            DepositMomoManualMsg msg = new DepositMomoManualMsg();
            if (res != null) {
                msg.Error = 4;
                msg.code = 4;
                msg.name = res.ReceivedName;
                msg.receiverPhone = res.ReceivedPhoneNumber;
                msg.comment = "nothing";
                msg.currentMoney = userService.getCurrentMoneyUserCache(nickname, "vin");
                msg.transId = cmd.transId;
            } else {
                msg.Error = (byte) 1;
            }
            this.send((BaseMsg) msg, user);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private synchronized void depositMomoManual(User user, DataCmd dataCmd) { // tạo phiếu
        // d
        Debug.info("Nap momo auto");
        try {
            DepositMomoManualCmd cmd = new DepositMomoManualCmd(dataCmd);
            String nickname = user.getName();
            MomoResponse res = this.rechargeMomoService.rechargeByMomoManual(user, cmd.Amount, cmd.SendFrom);
            DepositMomoManualMsg msg = new DepositMomoManualMsg();
            if (res != null) {
                msg.Error = (byte) res.getCode();
                msg.code = res.getCode();
                msg.name = res.getReceiverName();
                msg.receiverPhone = res.getReceiverPhone();
                msg.comment = res.getComment();
                msg.currentMoney = userService.getCurrentMoneyUserCache(nickname, "vin");
                msg.transId = res.getTid();

            } else {
                msg.Error = (byte) 1;
            }
            this.send((BaseMsg) msg, user);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private synchronized void depositBankManual(User user, DataCmd dataCmd) {
        try {
            DepositBankManualCmd cmd = new DepositBankManualCmd(dataCmd);
            String nickname = user.getName();
            RechargeResponse res = this.rechargeService.rechargeByBankManual(nickname, cmd.amount, cmd.bankNumber, cmd.sender);
            DepositBankManualMsg msg = new DepositBankManualMsg();
            if (res != null) {
                msg.Error = (byte) res.getCode();
            } else {
                msg.Error = (byte) 1;
            }
            this.send((BaseMsg) msg, user);
        } catch (Exception e) {
            logger.error(e);
        }

    }

    private synchronized void depositCodePayManual(User user, DataCmd dataCmd) {
        try {

            CodePayCmd cmd = new CodePayCmd(dataCmd);
            String nickname = user.getName();
            String bankcode = "";
            String bank = cmd.bank;
            String cardName = cmd.cardName;
            String cardCode = cmd.cardCode;
            String ver = cmd.ver;
            HistoryTransService historyTransService = new HistoryTransServiceImpl();
            RechargeDao dao = new RechargeDaoImpl();

            if (ver.equalsIgnoreCase("new") == false) {
                String TranID = String.valueOf(VinPlayUtils.generateTransId());
                String commentcode = "LX" + GenContent(nickname);
                String nick = GetNicknameByCode(commentcode);
                if (nick == null) {
                    InsertCodeUserBankELK(nickname, commentcode);
                }


                String dataall = "CodePay" + "|" + bank + "|" + cardName + "|" + TranID + "|" + commentcode;
                RechargeServiceImpl reg = new RechargeServiceImpl();
                reg.rechargeByBankManual(nickname, 1, cardCode, dataall);

                CodePayMsg msg = new CodePayMsg();
                msg.Error = (byte) 200;
                msg.comment = commentcode;
                this.send((BaseMsg) msg, user);
            } else {
                CodePayMsg msg = new CodePayMsg();
                GencommentCodepay gen = new GencommentCodepay();
                Long time_check = new Date().getTime();
                SimpleDateFormat sim = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Codepayok codepay3 = gen.findNickname(nickname);
                if (bank.equalsIgnoreCase("you88") || cardName.equalsIgnoreCase("you88") || cardCode.equalsIgnoreCase("you88")) {
                    if (codepay3 == null) {
                        msg.Error = (byte) 300;
                        msg.comment = "you88";
                        this.send((BaseMsg) msg, user);
                    } else {
                        Date out = sim.parse(codepay3.getTimelog());
                        Long timelog = out.getTime();
                        Long time_end = Math.abs(time_check - timelog);
                        Long time_con = 7200 - time_end / 1000;
                        if (time_end <= 7200000 && codepay3.getUse() == 0) {
                            msg.Error = (byte) 200;
                            msg.comment = codepay3.getCodepay() + "|" + time_con + "|" + codepay3.getBankname();
                            this.send((BaseMsg) msg, user);
                        } else {

                            msg.Error = (byte) 300;
                            msg.comment = "you88";
                            this.send((BaseMsg) msg, user);
                        }
                    }
                } else {
                    String TranID = String.valueOf(VinPlayUtils.generateTransId());
                    String commentcode = "";
                    if (codepay3 == null) {
                        boolean check = false;
                        do {
                            String commentcodeyyy = "LX" + gen.randomMaChuyenTien().toUpperCase();
                            Codepayok codepay1 = gen.findCodepay(commentcodeyyy);
                            if (codepay1 == null) {
                                gen.insertCodepay(nickname, commentcodeyyy, bank);
                                check = true;
                                commentcode = commentcodeyyy;
                            } else {
                                check = false;
                            }
                        } while (check == false);

                        String dataall = "CodePay" + "|" + bank + "|" + cardName + "|" + TranID + "|" + commentcode;
                        RechargeServiceImpl reg = new RechargeServiceImpl();
                        reg.rechargeByBankManual(nickname, 1, cardCode, dataall);

                        Long time_con = 7200L;
                        msg.Error = (byte) 200;
                        msg.comment = commentcode + "|" + time_con + "|" + bank;
                        this.send((BaseMsg) msg, user);

                    } else {
                        Date out = sim.parse(codepay3.getTimelog());
                        Long timelog = out.getTime();
                        boolean checktaodon = false;

                        boolean check = false;

                        do {
                            if (codepay3.getUse() == 1) {

                                String commentcodeyyy = "LX" + gen.randomMaChuyenTien().toUpperCase();
                                Codepayok codepay12 = gen.findCodepay(commentcodeyyy);
                                if (codepay12 == null) {
                                    gen.updateCodepay(nickname, false, commentcodeyyy, bank);
                                    check = true;
                                    checktaodon = true;
                                    commentcode = commentcodeyyy;
                                } else {
                                    check = false;
                                    checktaodon = false;
                                }
                            } else {
                                Long time_end = time_check - timelog;
                                if (time_end <= 7200000) {
                                    commentcode = codepay3.getCodepay();
                                    check = true;
                                } else {

                                    String commentcodeyyy = "LX" + gen.randomMaChuyenTien().toUpperCase();
                                    Codepayok codepay12 = gen.findCodepay(commentcodeyyy);
                                    if (codepay12 == null) {
                                        gen.updateCodepay(nickname, false, commentcodeyyy, bank);
                                        commentcode = commentcodeyyy;
                                        check = true;
                                        checktaodon = true;
                                    } else {
                                        check = false;
                                        checktaodon = false;
                                    }
                                }
                            }

                        } while (check == false);

                        //commentcode = gen.TaoMaCodePayNickNotNull(nickname, bank, codepay3, time_check, timelog);
                        Codepayok codepayx = gen.findNickname(nickname);
                        Long time_end = time_check - timelog;
                        Long time_con = 7200 - time_end / 1000;

                        if (checktaodon == true) {
                            String dataall = "CodePay" + "|" + codepayx.getBankname() + "|" + cardName + "|" + TranID + "|" + commentcode;
                            RechargeServiceImpl reg = new RechargeServiceImpl();
                            reg.rechargeByBankManual(nickname, 1, cardCode, dataall);
                        }

                        msg.Error = (byte) 200;
                        msg.comment = commentcode + "|" + time_con + "|" + codepayx.getBankname();
                        this.send((BaseMsg) msg, user);
                    }
                }
            }


        } catch (Exception e) {
            logger.error(e);
        }

    }

    private synchronized void depositCodePayTime(User user, DataCmd dataCmd) {
        try {
            CodePayCmd cmd = new CodePayCmd(dataCmd);
            String nickname = user.getName();
            String bankcode = "";
            String bank = cmd.bank;
            String cardName = cmd.cardName;
            String cardCode = cmd.cardCode;
            String commentcode = "LX" + GenContent(nickname);
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            final ArrayList<DepositBankModel> records = new ArrayList<DepositBankModel>();
            MongoDatabase db = MongoDBConnectionFactory.getDB();
            MongoCollection col = db.getCollection(DvtConst.DEPOSIT_BANK_COLLECTION);
            conditions.put("UserSender", "CodePay");
            conditions.put("Status", 1);
            conditions.put("Nickname", nickname);
            FindIterable iterable = col.find((Bson) new Document(conditions));
            iterable.forEach((Block) new Block<Document>() {

                public void apply(Document document) {

                    DepositBankModel modelz = new DepositBankModel(
                            document.getString((Object) "Id"),
                            document.getString((Object) "Nickname"),
                            document.getString((Object) "CreatedAt"),
                            document.getString((Object) "UpdatedAt"),
                            document.getLong((Object) "Amount"),
                            document.getInteger((Object) "Status"),
                            document.getString((Object) "BankBrandName"),
                            document.getString((Object) "BankAccountNumber"),
                            document.getString((Object) "BankAccountName"),
                            document.getString((Object) "Description"),
                            document.getString((Object) "UserApprove")
                    );
                    modelz.setUserSender(document.getString((Object) "UserSender"));
                    records.add(modelz);
                }
            });
            if (records.size() == 0) {
                CodePayMsg msg = new CodePayMsg();
                msg.Error = (byte) 300;
                msg.comment = commentcode;
                this.send((BaseMsg) msg, user);
            } else {
                CodePayMsg msg = new CodePayMsg();
                msg.Error = (byte) 200;
                msg.comment = commentcode;
                this.send((BaseMsg) msg, user);
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void InsertCodeUserBankELK(String nickname, String code) {
        try {
            boolean check = false;
            String sig = "\"successful\":1";
            int retry = 3;
            do {
                retry--;
                if (retry < 0) {
                    break;
                }
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"nickname\":\"" + nickname + "\", \"code\":\"" + code + "\"}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/codeuserbank/_doc")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();
                String data = response.body().string();
                if (data.contains(sig) == true) {
                    check = true;
                }
            } while (check == false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String GetNicknameByCode(String code) {
        try {
            String nick = "";
            String code1 = code.toUpperCase();
            boolean check = false;
            String sig = "\"successful\":1";
            int retry = 3;
            do {
                retry--;
                if (retry < 0) {
                    break;
                }
                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"code.keyword\":\"" + code1 + "\"}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10,\"sort\":[],\"aggs\":{}}");
                Request request = new Request.Builder()
                        .url(System.getenv("ELASTICSEARCH_URL") + "/codeuserbank/_search")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();
                String data = response.body().string();
                if (data.contains(sig) == true) {
                    check = true;
                }
                JSONObject obj = new JSONObject(data);
                JSONArray jsonArray = obj.getJSONObject("hits").getJSONArray("hits");
                final int n = jsonArray.length();
                if (n == 0) {
                    nick = null;
                } else {
                    for (int i = 0; i < n; ++i) {
                        final JSONObject person = jsonArray.getJSONObject(i);
                        JSONObject test = person.getJSONObject("_source");
                        String nickname = test.getString("nickname");
                        nick = nickname;
                    }
                }

            } while (check == false);
            return nick;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String GenContent(String nickname) {
        //txt to decimal
        char[] nick_char = nickname.toCharArray();
        ArrayList<Integer> list1 = new ArrayList<>();
        for (char c : nick_char) {
            int y = (int) c;
            list1.add(y);
        }

        //decimal to hex
        String hex = "";
        for (int i : list1) {
            String h = Integer.toHexString(i);
            hex = hex + h + ",";
        }

        // hex to key
        hex = hex.toUpperCase();
        int sum = 0;
        int size = 3;
        int range = 65536;//16 bit
        ArrayList<String> list3 = new ArrayList<>();
        String[] list2 = hex.split(",");
        for (String s : list2) {
            int u = Integer.parseInt(s, 16);
            sum = sum + u;
        }
        sum %= range;
        String sum1 = Integer.toHexString(sum).toUpperCase();
        String sum2 = "000000000" + sum1;
        String sum3 = sum2.substring(sum2.length() - size);
        sum3 = nickname.substring(0, 2).toUpperCase() + sum3 + nickname.length();
        return sum3;

    }


    private synchronized void depositOnePayBankManual(User user, DataCmd dataCmd) {
        try {
            DepositOnePayCmd cmd = new DepositOnePayCmd(dataCmd);
            String nickname = user.getName();
            RechargeResponse res = this.rechargeService.rechargeByOnePayBankManual(nickname, cmd.Amount, cmd.bankAccountNumber, cmd.bankPassword, cmd.bankName);
            DepositOnePayManualMsg msg = new DepositOnePayManualMsg();

            if (res.getCode() != 3) {
                mapUserOnePay.put(user.getName(), res.getTid());
            }

            if (res != null) {
                msg.Error = (byte) res.getCode();

            } else {
                msg.Error = (byte) 5;
            }
            if (null != res.getTid()) {
                msg.transId = res.getTid();
            } else {
                msg.transId = "Không có";
            }
            this.send((BaseMsg) msg, listuserCr.get(user.getName()));

        } catch (Exception e) {
            logger.error(e);
        }

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
        msg.remainCount = (short) response.getRotateCount();
        msg.currentMoneyVin = response.getCurrentMoneyVin();
        msg.currentMoneyXu = response.getCurrentMoneyXu();
        this.send((BaseMsg) msg, user);
    }

    private void getVQVip(User user) {
        LuckyServiceImpl service = new LuckyServiceImpl();
        LuckyVipResponse response = service.rotateLuckyVip(user.getName(), true);
        GetVQVipMsg msg = new GetVQVipMsg();
        msg.remainCount = (short) response.getRotateCount();
        this.send((BaseMsg) msg, user);
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
        msg.prizeMulti = (short) response.getResultMulti();
        msg.remainCount = (short) response.getRotateCount();
        msg.currentMoneyVin = response.getCurrentMoney();
        this.send((BaseMsg) msg, user);
    }

    private void requestApiOTP(User user, DataCmd dataCmd) {
    }

    private void confirmApiOTP(User user, DataCmd dataCmd) {
    }

    private void checkIAP(User user, DataCmd dataCmd) {
        CheckIAPCmd cmd = new CheckIAPCmd(dataCmd);
        CheckIAPMsg msg = new CheckIAPMsg();
        msg.Error = this.rechargeService.checkRechargeIAP(user.getName(), cmd.productId);
        this.send(msg, user);
    }

    private void resultIAP(User user, DataCmd dataCmd) {
        ResultIAPCmd cmd = new ResultIAPCmd(dataCmd);
        ResultIAPMsg msg = new ResultIAPMsg();
        Debug.trace("nickname: " + user.getName());
        Debug.trace("signedData: " + cmd.signedData);
        Debug.trace("signature: " + cmd.signature);
        RechargeIAPResponse rcRes = this.rechargeService.rechargeIAP(user.getName(), cmd.signedData, cmd.signature);
        msg.Error = (byte) rcRes.getCode();
        msg.productId = (byte) rcRes.getProductId();
        msg.currentMoney = rcRes.getCurrentMoney();
        this.send(msg, user);
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
                msg.mobileSecure = userCache.isHasMobileSecurity() ? (byte) 1 : 0;
                msg.emailSecure = userCache.isHasEmailSecurity() ? (byte) 1 : 0;
                msg.appSecure = userCache.isHasAppSecurity() ? (byte) 1 : 0;
                msg.loginSecure = userCache.isHasLoginSecurity() ? (byte) 1 : 0;
                msg.moneyLoginotp = userCache.getLoginOtp();
                ObjectMapper mapper = new ObjectMapper();
                msg.configGame = mapper.writeValueAsString(this.securityService.getListGameBai(userCache.getStatus()));
                OtherService otherService = new OtherServiceImpl();
                if (otherService.checkActiveByNickname(userCache.getNickname())) {
                    msg.appSecure = 1;
                } else {
                    msg.appSecure = 0;
                }
                String phone = otherService.getPhoneActiveByNickname(userCache.getNickname());
                msg.mobile = phone;
                if (phone.isEmpty()) {
                    msg.mobileSecure = 0;
                } else {
                    msg.mobileSecure = 1;
                }
            } else {
                msg.Error = 1;
            }
        } catch (Exception e) {
            Debug.trace("LobbyModule get info error: " + e);
            msg.Error = 1;
        }
        this.send(msg, user);
    }

    private void getMoneyUse(User user, DataCmd dataCmd) {
        GetMoneyUseMsg msg = new GetMoneyUseMsg();
        msg.moneyUse = this.userService.getMoneyUserCache(user.getName(), "vin");
        this.send(msg, user);
    }

    private void doiPass(User user, DataCmd dataCmd) {
        DoiPassCmd cmd = new DoiPassCmd(dataCmd);
        byte res = this.securityService.changePassword(user.getName(), cmd.oldPass, cmd.newPass, true);
        if (res == 0) {
            user.setProperty(CURRENT_COMMAND, dataCmd.getId());
            user.setProperty(CURRENT_OBJECT_COMMAND, cmd);
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

        this.send(msg, user);
    }

    private void doiVippoint(User user, DataCmd dataCmd) {
        DoiVippointCmd cmd = new DoiVippointCmd(dataCmd);
        byte res = this.vpService.checkCashoutVP(user.getName());
        if (res == 0) {
            user.setProperty(CURRENT_COMMAND, dataCmd.getId());
            user.setProperty(CURRENT_OBJECT_COMMAND, cmd);
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
        this.send(msg, user);
    }

    private void updateUserInfo(User user, DataCmd dataCmd) {
        UpdateUserInfoCmd cmd = new UpdateUserInfoCmd(dataCmd);
        if (Objects.equals(cmd.cmt, ""))
            cmd.cmt = null;
        if (Objects.equals(cmd.email, ""))
            cmd.email = null;
        byte res = this.securityService.updateUserInfo(user.getName(), cmd.cmt, cmd.email, cmd.mobile);
        UpdateUserInfoMsg msg = new UpdateUserInfoMsg();
        msg.Error = res;
        this.send(msg, user);
    }

    private void updateEmail(User user, DataCmd dataCmd) {
        UpdateEmailCmd cmd = new UpdateEmailCmd(dataCmd);
        byte res = this.securityService.updateEmail(user.getName(), cmd.email);
        UpdateEmailMsg msg = new UpdateEmailMsg();
        msg.Error = res;
        this.send(msg, user);
    }

    private void updateMobile(User user, DataCmd dataCmd) {
        UpdateMobileCmd cmd = new UpdateMobileCmd(dataCmd);
        byte res = this.securityService.updateMobile(user.getName(), cmd.mobile);
        UpdateMobileMsg msg = new UpdateMobileMsg();
        msg.Error = res;
        this.send(msg, user);
    }

    private void activeEmail(User user, DataCmd dataCmd) {
        byte res = this.securityService.activeEmail(user.getName());
        ActiveEmailMsg msg = new ActiveEmailMsg();
        msg.Error = res;
        this.send(msg, user);
    }

    private void activeMobile(User user, DataCmd dataCmd) {
        ActiveMobileCmd cmd = new ActiveMobileCmd(dataCmd);
        byte res = this.securityService.activeMobile(user.getName(), true);
        if (res == 0) {
            user.setProperty(CURRENT_COMMAND, dataCmd.getId());
            user.setProperty(CURRENT_OBJECT_COMMAND, cmd);
        }
        ActiveMobileMsg msg = new ActiveMobileMsg();
        msg.Error = res;

        try {
            // check last sms
//            int ret = otpService.sendVoiceOtp(user.getName(), "", true);
//            if (ret != 0) {
//                Debug.trace("Cannot send OTP message!");
//            }
        } catch (Exception e) {
            Debug.trace("LobbyModule error: " + e);
        }

        this.send(msg, user);
    }

    private void updateNewMobile(User user, DataCmd dataCmd) {
        UpdateNewMobileCmd cmd = new UpdateNewMobileCmd(dataCmd);
        byte res = this.securityService.updateNewMobile(user.getName(), cmd.mobile, true);
        if (res == 0) {
            user.setProperty(CURRENT_COMMAND, dataCmd.getId());
            user.setProperty(CURRENT_OBJECT_COMMAND, cmd);
            user.setProperty(UPDATE_NEW_MOBILE, cmd.mobile);
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
        this.send(msg, user);
    }

    private void loginOtp(User user, DataCmd dataCmd) {
        LoginOtpCmd cmd = new LoginOtpCmd(dataCmd);
        user.setProperty(CURRENT_COMMAND, dataCmd.getId());
        user.setProperty(CURRENT_OBJECT_COMMAND, cmd);
        OtpServiceImpl otpService = new OtpServiceImpl();
        try {
            int ret = otpService.sendVoiceOtp(user.getName(), "", true);
            if (ret != 0) {
                Debug.trace("Cannot send OTP message!");
            }
        } catch (Exception e) {
            Debug.trace("LobbyModule error: " + e);
        }
    }

    // todo : nhét tiền vào két sắt
    private void ketSat(User user, DataCmd dataCmd) {
        KetSatCmd cmd = new KetSatCmd(dataCmd);
        byte res = 1;
        // todo: 0 là rút tiền khỏi két
        ResultKetSatMsg ksMsg = new ResultKetSatMsg();
        MoneyResponse moneyres = new MoneyResponse(false, "1001");
        if (cmd.type == 0) {
            moneyres = this.securityService.takeMoneyInSafe(user.getName(), cmd.moneyExchange, false);
            if (moneyres.getErrorCode().equals("0")) {
                res = 0;
                user.setProperty(CURRENT_COMMAND, dataCmd.getId());
                user.setProperty(CURRENT_OBJECT_COMMAND, cmd);
            } else if (moneyres.getErrorCode().equals("1002")) {
                res = 2;
            }
        }
        //todo : 1 là nạp tiền vào két
        else if (cmd.type == 1) {
            moneyres = this.securityService.sendMoneyToSafe(user.getName(), cmd.moneyExchange, false);
            if (moneyres.getErrorCode().equals("0")) {
                res = 0;
            } else if (moneyres.getErrorCode().equals("1002")) {
                res = 2;
            }
        }
        ksMsg.moneyUse = moneyres.getMoneyUse();
        ksMsg.safe = moneyres.getSafeMoney();
        ksMsg.currentMoney = moneyres.getCurrentMoney();
        ksMsg.Error = cmd.moneyExchange == 0 ? 3 : res;
        this.send(ksMsg, user);
    }

    private void gameConfig(User user, DataCmd dataCmd) {
        GameConfigCmd cmd = new GameConfigCmd(dataCmd);
        user.setProperty(CURRENT_COMMAND, dataCmd.getId());
        user.setProperty(CURRENT_OBJECT_COMMAND, cmd);

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
            user.setProperty(CURRENT_COMMAND, dataCmd.getId());
            user.setProperty(CURRENT_OBJECT_COMMAND, cmd);
        }
        NapXuMsg msg = new NapXuMsg();
        msg.Error = response.getResult();
        this.send(msg, user);
    }

    private void napTheDienThoai(User user, DataCmd dataCmd) {
        ;
        GachTheDienThoaiCmd cmd = new GachTheDienThoaiCmd(dataCmd);
        byte result = 1;
        RechargeResponse res = null;
        NapTheDienThoaiMsg msg = new NapTheDienThoaiMsg();
        if (GameUtils.allowDepositCard(user.getName())) { // todo check time ban allow deposit
            try {
                Debug.trace("Recharge error: " + PartnerConfig.CongGachThe + ":" + cmd.provider + ":" + cmd.serial + ":" + cmd.pin + ":" + cmd.menhgia);
                Platform platform = Platform.find((String) user.getProperty("pf"));
                cmd.menhgia = cmd.menhgia.replace(".", "");
                cmd.menhgia = cmd.menhgia.replace(",", "");

                res = this.rechargeService.rechargeByGachThe(user.getName(), ProviderType.getProviderById(cmd.provider), cmd.serial, cmd.pin, cmd.menhgia, platform.getName(), user.getId());

            } catch (Exception e) {
                logger.debug("napTheDienThoai error:" + e.getMessage());
                Debug.trace("Recharge error: " + e.getMessage());
            }

            if (res != null) {
                msg.Error = (byte) res.getCode();
                msg.currentMoney = res.getCurrentMoney();
                msg.numFail = res.getFail();
                msg.timeFail = res.getTime();
            } else {
                msg.Error = result;
            }
        } else {
            msg.Error = 8; // khoas do nap the qua nhieu lan
            msg.timeFail = 2; // khoa trong 2s
        }

        BroadCastUserMoney.pushBroadCast(user.getName());
        Debug.trace("Recharge Gachthe error: " + msg.Error);
        this.send(msg, user);
        CardBanMoneyService banMoneyService = new CardBanMoneyService();
        banMoneyService.banWidrawUser(user.getName(), 2000);
    }

    private void napTheDienThoaiGachthe(User user, DataCmd dataCmd) {
        GachTheDienThoaiCmd cmd = new GachTheDienThoaiCmd(dataCmd);
        byte result = 1;
        RechargeResponse res = null;
        try {
            Debug.trace("Recharge error: " + cmd.provider + ":" + cmd.serial + ":" + cmd.pin + ":" + cmd.menhgia);
            Platform platform = Platform.find((String) user.getProperty((Object) "pf"));
            cmd.menhgia = cmd.menhgia.replace(".", "");
            cmd.menhgia = cmd.menhgia.replace(",", "");
            //res = this.rechargeService.rechargeByCard(user.getName(), ProviderType.getProviderById((int)cmd.provider), cmd.serial, cmd.pin, platform.getName());
            res = this.rechargeService.rechargeByGachThe(user.getName(), ProviderType.getProviderById((int) cmd.provider), cmd.serial, cmd.pin, cmd.menhgia, platform.getName(), user.getId());
        } catch (Exception e) {
            Debug.trace("Recharge error: " + e.getMessage());
        }
        GachTheDienThoaiMsg msg = new GachTheDienThoaiMsg();
        if (res != null) {
            msg.Error = (byte) res.getCode();
            msg.currentMoney = res.getCurrentMoney();
            msg.numFail = res.getFail();
            msg.timeFail = res.getTime();
        } else {
            msg.Error = result;
        }
        Debug.trace("Recharge Gachthe error: " + msg.Error);
        this.send(msg, user);
    }

    private void napVinCard(User user, DataCmd dataCmd) {
        NapVinCardCmd cmd = new NapVinCardCmd(dataCmd);
        byte result = 1;
        RechargeResponse res = null;
        try {
            Platform platform = Platform.find((String) ((String) user.getProperty((Object) "pf")));
            res = this.rechargeService.rechargeByVinCard(user.getName(), cmd.serial, cmd.pin, platform.getName());
        } catch (Exception e) {
            Debug.trace((Object) ("Recharge error: " + e.getMessage()));
        }
        NapVinCardMsg msg = new NapVinCardMsg();
        if (res != null) {
            msg.Error = (byte) res.getCode();
            msg.currentMoney = res.getCurrentMoney();
            msg.numFail = res.getFail();
            msg.timeFail = res.getTime();
        } else {
            msg.Error = result;
        }
        this.send((BaseMsg) msg, user);
    }

    private void napMegaCard(User user, DataCmd dataCmd) {
        NapMegaCardCmd cmd = new NapMegaCardCmd(dataCmd);
        byte result = 1;
        RechargeResponse res = null;
        try {
            Platform platform = Platform.find((String) ((String) user.getProperty((Object) "pf")));
            res = this.rechargeService.rechargeByMegaCard(user.getName(), cmd.serial, cmd.pin, platform.getName());
        } catch (Exception e) {
            e.printStackTrace();
            Debug.trace((Object) ("Recharge error: " + e.getMessage()));
        }
        NapMegaCardMsg msg = new NapMegaCardMsg();
        if (res != null) {
            msg.Error = (byte) res.getCode();
            msg.currentMoney = res.getCurrentMoney();
            msg.numFail = res.getFail();
            msg.timeFail = res.getTime();
        } else {
            msg.Error = result;
        }
        this.send((BaseMsg) msg, user);
    }

    private void napQuaNganHang(User user, DataCmd dataCmd) {
        NapQuaNganHangCmd cmd = new NapQuaNganHangCmd(dataCmd);
        NapQuaNganHangMsg msg = new NapQuaNganHangMsg();
        try {
            Platform platform = Platform.find((String) ((String) user.getProperty((Object) "pf")));
            I2BResponse res = this.rechargeService.rechargeByBank(user.getName(), cmd.money, cmd.bank, user.getIpAddress(), platform.getName());
            msg.url = res.getUrl();
            msg.Error = (byte) res.getCode();
        } catch (Exception e) {
            Debug.trace((Object) ("LobbyModule nap qua ngan hang error: " + e));
            msg.Error = 1;
            msg.url = "";
        }
        this.send((BaseMsg) msg, user);
    }

    private synchronized void chuyenKhoan(User user, DataCmd dataCmd) {
        ChuyenKhoanCmd cmd = new ChuyenKhoanCmd(dataCmd);
        // check han muc
        // end check han muc
        QuotaResponse checkQuota = CheckQuota(user.getName(), false);
        TransferMoneyResponse res;
        Debug.trace("Quota resultt:" + checkQuota);
        // kiểm tra te
        if (user.getName().toLowerCase().equals(cmd.receiver.toLowerCase())) {
            res = new TransferMoneyResponse((byte) 22, 0, 0);
        } else {
            // kiem tra co phai chuyen cho dai ly khong 
            if (!cmd.receiver.equals("")) {
                try {
                    UserModel userReceive = userService.getUserByNickName(cmd.receiver);
                    if (userReceive != null) {
                        if (userReceive.getDaily() != 1 && userReceive.getDaily() != 2) {
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
            if (checkQuota.getCode() == 0) {
                res = this.userService.transferMoney(user.getName(), cmd.receiver, cmd.moneyExchange, cmd.description, true);
                if (res.getCode() == 0) {
                    user.setProperty((Object) CURRENT_COMMAND, (Object) dataCmd.getId());
                    user.setProperty((Object) CURRENT_OBJECT_COMMAND, (Object) cmd);
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
            } else {
                res = new TransferMoneyResponse((byte) 22, 0, 0);
            }
        }

        ChuyenKhoanMsg msg = new ChuyenKhoanMsg();
        msg.Error = res.getCode();
        msg.moneyUse = res.getMoneyUse();
        msg.currentMoney = res.getCurrentMoney();
        this.send((BaseMsg) msg, user);
    }

    private synchronized void transferMoneyToAnUser(User user, DataCmd dataCmd) {
        ChuyenKhoanCmd cmd = new ChuyenKhoanCmd(dataCmd);
        // todo check code client
//        int checkCodeStatus = this.checkSecretCode(cmd.secretCode,user.getName());
//        if(checkCodeStatus !=1){
//            sendRequireCodeRes(user,checkCodeStatus);
//            return;
//        }


        TransferMoneyResponse res = new TransferMoneyResponse((byte) 0, 0, 0);
        // kiểm tra te
        if (user.getName().equals(cmd.receiver.toLowerCase())) {
            res = new TransferMoneyResponse((byte) 22, 0, 0);
        } else {
            try {
                UserModel userReceive = userService.getUserByNickName(cmd.receiver);
                if (userReceive != null) {
                    if (!cmd.receiver.equals("")) {
                        res = this.userService.transferMoneyToAnUser(user.getName(), userReceive.getNickname(), cmd.moneyExchange, cmd.description, false);
                        if (res.getCode() == 0) {
                            user.setProperty(CURRENT_COMMAND, dataCmd.getId());
                            user.setProperty(CURRENT_OBJECT_COMMAND, cmd);
                        }
                    } else {
                        res = new TransferMoneyResponse((byte) 22, 0, 0);
                    }
                } else {
                    res = new TransferMoneyResponse((byte) 23, 0, 0);
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
        ChuyenKhoanMsg msg = new ChuyenKhoanMsg();
        msg.Error = res.getCode();
        msg.moneyUse = res.getMoneyUse();
        msg.currentMoney = res.getCurrentMoney();
        this.send(msg, user);
    }

    private QuotaResponse CheckQuota(String nick_name, boolean seven_days) {
        QuotaResponse response = new QuotaResponse();
        try {
            UserModel userModel = userService.getUserByNickName(nick_name);
            if (userModel != null) {
                if (userModel.getDaily() == 1 || userModel.getDaily() == 2) {
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
                List<LogUserMoneyResponse> resultGiftCode = logService.searchAllLogMoneyUser(nick_name, "GIFTCODE", seven_days);
                if (resultGiftCode != null && resultGiftCode.size() > 0) {
                    total_giftcode_money = resultGiftCode.stream().map((trans) -> trans.moneyExchange).reduce(total_giftcode_money, (accumulator, _item) -> accumulator + _item);
                }
                List<LogUserMoneyResponse> resulReceive = logService.searchAllLogMoneyUser(nick_name, "RECEIVE", seven_days);
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
                List<LogUserMoneyResponse> resulTransfer = logService.searchAllLogMoneyUser(nick_name, "TRANSFER", seven_days);
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
                List<LogUserMoneyResponse> resultCard = logService.searchAllLogMoneyUser(nick_name, "CARD", seven_days);
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
                long total_bet_money = 0 - logService.getTotalBetWin(nick_name, "BET", null);
                long total_win_money = logService.getTotalBetWin(nick_name, "WIN", null);
                // get total bet taixiu
                //long total_bet_tx_money = 0 - logService.getTotalBetWin(nick_name, "BET","TaiXiu");
                //long total_win_tx_money = logService.getTotalBetWin(nick_name, "WIN","TaiXiu");
                //long taixiu_bet_win = total_bet_tx_money - total_win_tx_money;
                //if (taixiu_bet_win < 0)
                //    taixiu_bet_win = 0 - taixiu_bet_win;
                // Ban Ca                
                long total_bet_banca_money = 0 - logService.getTotalBetWin(nick_name, "BET", "HamCaMap");
                long total_bet_slot_money = 0 - logService.getTotalBetWin(nick_name, "BET", "SLOT");
                long finalQuota = total_bet_money - (total_bet_banca_money * 9) / 10 - (total_bet_slot_money * 9) / 10;
                long manual_quota = userModel.getManual_quota();
                finalQuota += manual_quota;
                //long total_win_banca_money = logService.getTotalBetWin(nick_name, "WIN","HamCaMap");
                //Debug.trace("Final quota :" + ((0 - total_bet_money) - quota));
                Debug.trace("Final quota :" + finalQuota);
                // check quote
                if (finalQuota > quota) {
                    response.setCode(0);
                } else {
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
        } catch (Exception ex) {
            response.setCode(-1);
            Debug.trace((Object) ("Check quote error : " + ex.getMessage()));
        }
        return response;
    }

    private synchronized void muaMaThe(User user, DataCmd dataCmd) {
        MuaMaTheCmd cmd = new MuaMaTheCmd(dataCmd);
        MuaMaTheMsg msg = new MuaMaTheMsg();
        try {
            SoftpinResponse res = this.cashOutService.cashOutByCardKhoThe(user.getName(), ProviderType.getProviderById((int) cmd.provider), PhoneCardType.getPhoneCardById((int) cmd.amount), (int) cmd.quantity, true);
            msg.Error = (byte) res.getCode();
            if (res.getCode() == 0) {
                user.setProperty((Object) CURRENT_COMMAND, (Object) dataCmd.getId());
                user.setProperty((Object) CURRENT_OBJECT_COMMAND, (Object) cmd);
            }

        } catch (Exception e) {
            msg.Error = 1;
            Debug.trace((Object) ("Mua ma the error : " + e.getMessage()));
        }
        this.send((BaseMsg) msg, user);
    }

    private synchronized void napTienDienThoai(User user, DataCmd dataCmd) {
        NapTienDienThoaiCmd cmd = new NapTienDienThoaiCmd(dataCmd);
        NapTienDienThoaiMsg msg = new NapTienDienThoaiMsg();
        try {
            CashoutResponse res = this.cashOutService.cashOutByTopUp(user.getName(), cmd.mobile, PhoneCardType.getPhoneCardById((int) cmd.amount), cmd.type, true);
            msg.Error = (byte) res.getCode();
            if (res.getCode() == 0) {
                user.setProperty((Object) CURRENT_COMMAND, (Object) dataCmd.getId());
                user.setProperty((Object) CURRENT_OBJECT_COMMAND, (Object) cmd);
            }
        } catch (Exception e) {
            msg.Error = 1;
            Debug.trace((Object) ("Mua ma the error : " + e.getMessage()));
        }
        this.send((BaseMsg) msg, user);
    }

    private synchronized void giftCode(User user, DataCmd dataCmd) {
        GiftCodeCmd cmd = new GiftCodeCmd(dataCmd);
        GiftCodeMsg msg = new GiftCodeMsg();
        try {
            // get special gift_code
            GiftCodeDAO dao = new GiftCodeDAOImpl();
            boolean exists = dao.CheckSpecialGiftCodes(cmd.giftCode);
            Debug.trace("Giftcode:" + cmd.giftCode + ":" + exists);
            TanThuDAO ttdao = new TanThuDAO();
            OTPCheck otpcheck = new OTPCheck();
            ArrayList<CodeTT> listcode = ttdao.GetCode();
            CodeTT codex = null;
            UserOTP usotp = ttdao.GetActive(user.getName());
            boolean check_code_tt = false;
            boolean check_use_code_tanthu = ttdao.CheckUseCode(user.getName());
//            boolean check_user_active_otp = otpcheck.checkUserActiveOTP(user.getName());
            boolean check_user_active_otp = false;
            if (usotp.getActive() == 1) {
                check_user_active_otp = true;
            } else {
                check_user_active_otp = false;
            }


            for (CodeTT cc : listcode) {
                if (cc.getCode().trim().equals(cmd.giftCode.trim()) && cc.getStop() == 0) {
                    check_code_tt = true;
                    codex = new CodeTT(cc.getCode(), cc.getMoney(), cc.getTimelog(), cc.getStop());
                    break;
                }
            }

            if (check_user_active_otp) {
                if (!check_use_code_tanthu && check_code_tt) {
                    String timelog = VinPlayUtils.getCurrentDateTime();
                    UseCode usercode = new UseCode(user.getName(), codex.getCode(), 1, timelog, usotp.getUsername(), usotp.getPhone(), usotp.getActive());
                    ttdao.InsertCode(usercode);
                    MoneyResponse mnres = userService.updateMoney(user.getName(), codex.getMoney(), "vin", "GiftCodeTanThu", "GiftCodeTanThu", "M\u00e3: " + cmd.giftCode, 0L, null, TransType.NO_VIPPOINT);
                    msg.Error = this.parseErrorCodeGiftCode("0");
                } else {
                    if (exists) {
                        GiftCodeUpdateResponse response = this.gfService.updateSpecialGiftCodeNew(user.getName(), cmd.giftCode);
                        Debug.trace("Giftcode:" + cmd.giftCode + ":" + response.getErrorCode());
                        if (response.isSuccess()) {
                            msg.currentMoneyVin = response.currentMoneyVin;
                            msg.currentMoneyXu = response.currentMoneyXu;
                            msg.moneyGiftCodeVin = response.moneyGiftCodeVin;
                            msg.moneyGiftCodeXu = response.moneyGiftCodeXu;
                        }
                        msg.Error = this.parseErrorCodeGiftCode(response.getErrorCode());
                    } else {
                        GiftCodeUpdateResponse response = this.gfService.updateGiftCode(user.getName(), cmd.giftCode);
                        if (response.isSuccess()) {
                            msg.currentMoneyVin = response.currentMoneyVin;
                            msg.currentMoneyXu = response.currentMoneyXu;
                            msg.moneyGiftCodeVin = response.moneyGiftCodeVin;
                            msg.moneyGiftCodeXu = response.moneyGiftCodeXu;
                        }
                        msg.Error = this.parseErrorCodeGiftCode(response.getErrorCode());
                    }
                }
            } else {
                msg.Error = this.parseErrorCodeGiftCode("10003");
            }


            if (msg.Error == 2) {
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
            Debug.trace((Object) ("LobbyModule giftcode error: " + msg.Error));
        } catch (Exception e) {
            e.printStackTrace();
            Debug.trace((Object) ("LobbyModule giftcode error: " + e));
            msg.Error = 1;
        }
        this.send((BaseMsg) msg, user);
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
            msg.fee = this.userService.calFeeTransfer((int) type1, (int) type2);
        }
        msg.type = type2;
        this.send((BaseMsg) msg, user);
    }

    private void sendOtp(User user, DataCmd dataCmd) {
        SendOTPCmd cmd = new SendOTPCmd(dataCmd);
        SendOTPMsg msg = new SendOTPMsg();
        try {
            boolean forceCheck = true;
            String mobile = "";
            if (user.getProperty(FORCE_CHECK_OTP) != null)
                forceCheck = (boolean) user.getProperty(FORCE_CHECK_OTP);
            if (!forceCheck) {
                if (user.getProperty(UPDATE_NEW_MOBILE) != null)
                    mobile = (String) user.getProperty(UPDATE_NEW_MOBILE);
            }
            Debug.trace(user.getName() + ":" + mobile);
            int ret = otpService.sendVoiceOtp(user.getName(), mobile, forceCheck);
            msg.Error = (byte) ret;
        } catch (Exception e) {
            Debug.trace("LobbyModule error: " + e);
        }
        this.send(msg, user);
    }

    private void checkOtp(User user, DataCmd dataCmd) {
        OtpCmd cmd = new OtpCmd(dataCmd);
        OTPMsg msg = new OTPMsg();
        try {
            Object obj = user.getProperty((Object) CURRENT_COMMAND);
            Object objCmd = user.getProperty((Object) CURRENT_OBJECT_COMMAND);
            short cmdId = (Short) obj;
            if (obj != null && objCmd != null) {
                int code = 3;
                if (cmdId == 20027) {
                    UpdateNewMobileCmd unmCmd = (UpdateNewMobileCmd) ((Object) objCmd);
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
                Debug.trace((Object) ("Verify otp code: " + code));
                if (code == 0) {
                    msg.Error = 0;
                    this.send((BaseMsg) msg, user);
                    int res = 1;
                    switch (cmdId) {
                        case 20000: {
                            DoiPassCmd pCmd = (DoiPassCmd) ((Object) objCmd);
                            ResultDoiPassMsg pMsg = new ResultDoiPassMsg();
                            res = this.securityService.changePassword(user.getName(), pCmd.oldPass, pCmd.newPass, false);
                            pMsg.Error = res == 0 ? Byte.valueOf((byte) 0) : Byte.valueOf((byte) 1);
                            this.send((BaseMsg) pMsg, user);
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
                            this.send((BaseMsg) vpMsg, user);
                            break;
                        }
                        case 20006: {
                            ResultActiveMobileMsg amMsg = new ResultActiveMobileMsg();
                            res = this.securityService.activeMobile(user.getName(), false);
                            amMsg.Error = res == 0 ? Byte.valueOf((byte) 0) : Byte.valueOf((byte) 1);
                            this.send((BaseMsg) amMsg, user);
                            break;
                        }
                        case 20007: {
                            UpdateNewMobileCmd unmCmd = (UpdateNewMobileCmd) ((Object) objCmd);
                            user.setProperty((Object) CURRENT_COMMAND, (short) 20027);
                            user.setProperty((Object) CURRENT_OBJECT_COMMAND, (Object) unmCmd);
                            user.setProperty((Object) FORCE_CHECK_OTP, (Object) false);
                            ResultUpdateNewMobileMsg unmMsg = new ResultUpdateNewMobileMsg();
                            unmMsg.Error = 0;
                            this.send((BaseMsg) unmMsg, user);
                            break;
                        }
                        case 20027: {
                            UpdateNewMobileCmd anmCmd = (UpdateNewMobileCmd) ((Object) objCmd);
                            ResultActiveNewMobileMsg anmMsg = new ResultActiveNewMobileMsg();
                            user.setProperty((Object) FORCE_CHECK_OTP, (Object) true);
                            user.setProperty((Object) UPDATE_NEW_MOBILE, (Object) "");
                            res = this.securityService.updateNewMobile(user.getName(), anmCmd.mobile, false);
                            anmMsg.Error = res == 0 ? Byte.valueOf((byte) 0) : Byte.valueOf((byte) 1);
                            this.send((BaseMsg) anmMsg, user);
                            break;
                        }
                        case 20008: {
                            LoginOtpCmd loCmd = (LoginOtpCmd) ((Object) objCmd);
                            res = this.securityService.loginWithOTP(user.getName(), loCmd.money, loCmd.type);
                            LoginOtpMsg loMsg = new LoginOtpMsg();
                            loMsg.Error = res == 0 ? Byte.valueOf((byte) 0) : Byte.valueOf((byte) 1);
                            this.send((BaseMsg) loMsg, user);
                            break;
                        }
                        case 20010: {
                            GameConfigCmd gcCmd = (GameConfigCmd) ((Object) objCmd);
                            res = this.securityService.configGame(user.getName(), gcCmd.games);
                            GameConfigMsg gcMsg = new GameConfigMsg();
                            gcMsg.Error = res == 0 ? Byte.valueOf((byte) 0) : Byte.valueOf((byte) 1);
                            this.send((BaseMsg) gcMsg, user);
                            break;
                        }
                        case 20009: {
                            KetSatCmd ksCmd = (KetSatCmd) ((Object) objCmd);
                            ResultKetSatMsg ksMsg = new ResultKetSatMsg();
                            //0 là rút tiền khỏi két- 1 là nạp tiền vào két
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
                            ksMsg.Error = (byte) res;
                            this.send((BaseMsg) ksMsg, user);
                            break;
                        }
                        case 20011: {
                            NapXuCmd nxCmd = (NapXuCmd) ((Object) objCmd);
                            NapXuResponse response = this.userService.napXu(user.getName(), nxCmd.moneyVin, false);
                            ResultNapXuMsg rnxMsg = new ResultNapXuMsg();
                            rnxMsg.Error = response.getResult();
                            rnxMsg.currentMoneyVin = response.getCurrentMoneyVin();
                            rnxMsg.currentMoneyXu = response.getCurrentMoneyXu();
                            this.send((BaseMsg) rnxMsg, user);
                            break;
                        }
                        case 20014: {
                            ChuyenKhoanCmd ckCmd = (ChuyenKhoanCmd) ((Object) objCmd);
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
                                if (userReceive != null && (userReceive.getDaily() == 1 || userReceive.getDaily() == 2)) {
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
                            this.send((BaseMsg) ckmsg, user);
                            break;
                        }
                        case 20015: {
                            MuaMaTheCmd mmtCmd = (MuaMaTheCmd) ((Object) objCmd);
                            SoftpinResponse sfres = this.cashOutService.cashOutByCardKhoThe(user.getName(), ProviderType.getProviderById((int) mmtCmd.provider), PhoneCardType.getPhoneCardById((int) mmtCmd.amount), (int) mmtCmd.quantity, false);
                            ResultMuaMaTheMsg mtmsg = new ResultMuaMaTheMsg();
                            mtmsg.Error = (byte) sfres.getCode();
                            mtmsg.currentMoney = sfres.getCurrentMoney();
                            mtmsg.softpin = sfres.getSoftpin();
                            this.send((BaseMsg) mtmsg, user);
                            break;

                        }
                        case 20016: {
                            NapTienDienThoaiCmd ntdtCmd = (NapTienDienThoaiCmd) ((Object) objCmd);
                            CashoutResponse cres = this.cashOutService.cashOutByTopUp(user.getName(), ntdtCmd.mobile, PhoneCardType.getPhoneCardById((int) ntdtCmd.amount), ntdtCmd.type, false);
                            ResultNapTienDienThoaiMsg ntdtmsg = new ResultNapTienDienThoaiMsg();
                            ntdtmsg.Error = (byte) cres.getCode();
                            ntdtmsg.currentMoney = cres.getCurrentMoney();
                            this.send((BaseMsg) ntdtmsg, user);
                        }
                    }
                } else {
                    msg.Error = (byte) code;
                    this.send((BaseMsg) msg, user);
                }
            } else {
                msg.Error = 2;
                this.send((BaseMsg) msg, user);
            }
        } catch (Exception e) {
            msg.Error = 2;
            this.send((BaseMsg) msg, user);
            Debug.trace((Object) ("LobbyModule check otp error: " + e));
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStackTrace = sw.toString(); // stack trace as a string
            Debug.trace((Object) sStackTrace);
        }
    }

    /**
     * @param user
     * @param dataCmd
     */
    private synchronized void transferMoneyToDaiLy(User user, DataCmd dataCmd) {
        //OtpCmd cmd = new OtpCmd(dataCmd);
        TransferToDaiLyMsg msg = new TransferToDaiLyMsg();
        try {
            TransferToDaiLyCmd cmd = new TransferToDaiLyCmd(dataCmd);
//            int checkCodeStatus = this.checkSecretCode(cmd.secretCode,user.getName());
//            if(checkCodeStatus !=1){
//                sendRequireCodeRes(user,checkCodeStatus);
//                return;
//            }

            if (cmd.moneyExchange != null) {
                int code = 0;
                Debug.trace((Object) ("Verify otp code: " + code));
                if (code == 0) {
                    TransferMoneyResponse tmres = this.userService.transferMoneyDaiLyUnOtp(user.getName(), cmd.userReceiver, cmd.moneyExchange, cmd.description, false);
                    ResultChuyenKhoanMsg ckmsg = new ResultChuyenKhoanMsg();
                    if (tmres.getCode() == 0) {
                        ckmsg.Error = 0;
                        game.modules.minigame.cmd.send.UpdateUserInfoMsg uimsg = new game.modules.minigame.cmd.send.UpdateUserInfoMsg();
                        uimsg.moneyType = 1;
                        uimsg.currentMoney = tmres.getCurrentMoneyReceive();
                        MiniGameUtils.sendMessageToUser(uimsg, tmres.getNicknameReceive());
                    } else {
                        ckmsg.Error = 1;
                    }
                    ckmsg.moneyUse = tmres.getMoneyUse();
                    ckmsg.currentMoney = tmres.getCurrentMoney();
                    this.send((BaseMsg) ckmsg, user);
                } else {
                    msg.Error = (byte) code;
                    this.send((BaseMsg) msg, user);
                }
            } else {
                msg.Error = 2;
                this.send((BaseMsg) msg, user);
            }
        } catch (Exception e) {
            msg.Error = 2;
            this.send((BaseMsg) msg, user);
            Debug.trace((Object) ("LobbyModule transferMoneyToDaiLy error: " + e));
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStackTrace = sw.toString(); // stack trace as a string
            Debug.trace((Object) sStackTrace);
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

    // todo : user disconect
    public void handleServerEvent(IBZEvent ibzevent) throws BZException {
        if (ibzevent.getType() == BZEventType.USER_DISCONNECT) {
            User user = (User) ibzevent.getParameter((IBZEventParam) BZEventParam.USER);
            // todo : update trạng thái disconect
            this.unSubscribeJackPot(user);

            // update trang thai logout
        }
    }

    private void subscribeJackPot(User user) {
        this.usersSubJackpot.add(user);
        this.listuserCr.put(user.getName(), user);
    }

    private void unSubscribeJackPot(User user) {
        if (mapUserOnePay.containsKey(user)) {
            rechargeService.UpdateDepositStatusOnepay(mapUserOnePay.get(user.getName()), 101, 0);
            mapUserOnePay.remove(user.getName());
        }
        this.usersSubJackpot.remove((Object) user);
        this.listuserCr.remove(user.getName());
    }


    public void bauCuaJackpot() {
        try {
            int baucauto = 500000;
            try {
                baucauto = (int) cacheService.getValueInt("Hu_Bau_cua_to20");
            } catch (Exception e) {

            }
            UpdateBauCuaJackpotMsg msg = new UpdateBauCuaJackpotMsg();
            msg.baucuatofund = baucauto;
            for (User user : this.usersSubJackpot) {
                if (user == null) continue;
                this.send((BaseMsg) msg, user);
            }
        } catch (Exception e) {
            Debug.info("Hu bau cua loi");
        }
    }


    public void txJackpot() {
        try {
            //CacheService cacheService = new CacheServiceImpl();
            long txHu;
            long txTai;
            long txXiu;
            txTai = Long.parseLong(cacheService.getValueStr("Lobby_tx_tai_1"));
            txXiu = Long.parseLong(cacheService.getValueStr("Lobby_tx_xiu_1"));
            txHu = Long.parseLong(cacheService.getValueStr("Hu_TX_1"));
            UpdateTXJackpotMsg msg = new UpdateTXJackpotMsg();
            msg.moneyHu = txHu;
            msg.moneyTai = txTai;
            msg.moneyXiu = txXiu;
            for (User user : this.usersSubJackpot) {
                if (user == null) continue;
                this.send(msg, user);
            }
        } catch (Exception e) {
            cacheService.setValue("Lobby_tx_tai_1", 0);
            cacheService.setValue("Lobby_tx_xiu_1", 0);
            cacheService.setValue("Hu_TX_1", 0);
            Debug.info("Hu TX lỗi " + e.getMessage());
        }
    }

    public void txMd5Jackpot() {
        try {
            //CacheService cacheService = new CacheServiceImpl();
            long txHu;
            long txTai;
            long txXiu;
            txTai = Long.parseLong(cacheService.getValueStr("Md5_Lobby_tx_tai_1"));
            txXiu = Long.parseLong(cacheService.getValueStr("Md5_Lobby_tx_xiu_1"));
            try {
                txHu = Long.parseLong(cacheService.getValueStr("Hu_TX_1"));
            } catch (Exception ex) {
                txHu = 0;
            }
            UpdateTX5JackpotMsg msg = new UpdateTX5JackpotMsg();
            msg.moneyHu = txHu;
            msg.moneyTai = txTai;
            msg.moneyXiu = txXiu;
            for (User user : this.usersSubJackpot) {
                if (user == null) continue;
                this.send(msg, user);
            }
        } catch (Exception e) {
            cacheService.setValue("Md5_Lobby_tx_tai_1", 0);
            cacheService.setValue("Md5_Lobby_tx_xiu_1", 0);
            cacheService.setValue("Hu_TX_1", 0);
            Debug.info("Hu TX lỗi " + e.getMessage());
        }
    }

    public void updateJackpot() {
        try {
            CacheService cacheService = new CacheServiceImpl();

            long caoThap1000 = cacheService.getValueLong(MGRoom.CACHE_JACK_POT_VALUE_SLOT + "_" + 1000 + "_" + Games.CAO_THAP.getName(), 0);
            long caoThap10000 = cacheService.getValueLong(MGRoom.CACHE_JACK_POT_VALUE_SLOT + "_" + 10000 + "_" + Games.CAO_THAP.getName(), 0);
            long caoThap50000 = cacheService.getValueLong(MGRoom.CACHE_JACK_POT_VALUE_SLOT + "_" + 50000 + "_" + Games.CAO_THAP.getName(), 0);
            long caoThap100000 = cacheService.getValueLong(MGRoom.CACHE_JACK_POT_VALUE_SLOT + "_" + 100000 + "_" + Games.CAO_THAP.getName(), 0);
            long caoThap500000 = cacheService.getValueLong(MGRoom.CACHE_JACK_POT_VALUE_SLOT + "_" + 500000 + "_" + Games.CAO_THAP.getName(), 0);


            long miniPoker100 = cacheService.getValueLong(MGRoom.CACHE_JACK_POT_VALUE_SLOT + "_" + 1000 + "_" + Games.MINI_POKER.getName(), 0);
            long miniPoker1000 = cacheService.getValueLong(MGRoom.CACHE_JACK_POT_VALUE_SLOT + "_" + 10000 + "_" + Games.MINI_POKER.getName(), 0);
            long miniPoker10000 = cacheService.getValueLong(MGRoom.CACHE_JACK_POT_VALUE_SLOT + "_" + 100000 + "_" + Games.MINI_POKER.getName(), 0);

            long pokeGo100 = cacheService.getValueLong(MGRoom.CACHE_JACK_POT_VALUE_SLOT + "_" + 1000 + "_" + Games.CANDY.getName(), 0);
            long pokeGo1000 = cacheService.getValueLong(MGRoom.CACHE_JACK_POT_VALUE_SLOT + "_" + 1000 + "_" + Games.CANDY.getName(), 0);
            long pokeGo10000 = cacheService.getValueLong(MGRoom.CACHE_JACK_POT_VALUE_SLOT + "_" + 1000 + "_" + Games.CANDY.getName(), 0);

            long khoBau100 = cacheService.getValueLong(Games.KHO_BAU.getName() + "_vin_100", 0);
            long khoBau1000 = cacheService.getValueLong(Games.KHO_BAU.getName() + "_vin_1000", 0);
            long khoBau10000 = cacheService.getValueLong(Games.KHO_BAU.getName() + "_vin_10000", 0);

            long ndv100 = cacheService.getValueLong(Games.NU_DIEP_VIEN.getName() + "_vin_100", 0);
            long ndv1000 = cacheService.getValueLong(Games.NU_DIEP_VIEN.getName() + "_vin_1000", 0);
            long ndv10000 = cacheService.getValueLong(Games.NU_DIEP_VIEN.getName() + "_vin_10000", 0);

            long avengers100 = cacheService.getValueLong(Games.AVENGERS.getName() + "_vin_100", 0);
            long avengers1000 = cacheService.getValueLong(Games.AVENGERS.getName() + "_vin_1000", 0);
            long avengers10000 = cacheService.getValueLong(Games.AVENGERS.getName() + "_vin_10000", 0);

            long vqv100 = cacheService.getValueLong(Games.VUONG_QUOC_VIN.getName() + "_vin_100", 0);
            long vqv1000 = cacheService.getValueLong(Games.VUONG_QUOC_VIN.getName() + "_vin_1000", 0);
            long vqv10000 = cacheService.getValueLong(Games.VUONG_QUOC_VIN.getName() + "_vin_10000", 0);

            long fish100 = cacheService.getValueLong(Games.HAM_CA_MAP.getName() + "_vin_100", 0);
            long fish1000 = cacheService.getValueLong(Games.HAM_CA_MAP.getName() + "_vin_1000", 0);

            //spartan game

            long spartan100 = cacheService.getValueLong(Games.LADY_NIGHT.getName() + "_vin_100", 0);
            long spartan1000 = cacheService.getValueLong(Games.LADY_NIGHT.getName() + "_vin_1000", 0);
            long spartan5000 = cacheService.getValueLong(Games.LADY_NIGHT.getName() + "_vin_5000", 0);
            long spartan10000 = cacheService.getValueLong(Games.LADY_NIGHT.getName() + "_vin_10000", 0);
            long baucauto = cacheService.getValueLong("Hu_Bau_cua_to20", 0);

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

            msg.baucuatofund = baucauto;

            msg.potCaoThap1000 = caoThap1000;
            msg.potCaoThap10000 = caoThap10000;
            msg.potCaoThap50000 = caoThap100000;
            msg.potCaoThap100000 = caoThap50000;
            msg.potCaoThap500000 = caoThap500000;
            for (User user : this.usersSubJackpot) {
                if (user == null) continue;
                this.send(msg, user);
            }
        } catch (Exception e) {
            Debug.trace((Object) ("Update jackpot exception: " + e.getMessage()));
        }
    }

    public void updateHuVang(User user) {
        HuVangMsg msg = new HuVangMsg();
        msg.huBaCay = HuVangConfig.instance().getThoiGianHuVang(Games.BA_CAY.getName());
        msg.huBaiCao = HuVangConfig.instance().getThoiGianHuVang(Games.BAI_CAO.getName());
        msg.huBinh = HuVangConfig.instance().getThoiGianHuVang(Games.BINH.getName());
        msg.huSam = HuVangConfig.instance().getThoiGianHuVang(Games.SAM.getName());
        msg.huTLMN = HuVangConfig.instance().getThoiGianHuVang(Games.TLMN.getName());
        this.send((BaseMsg) msg, user);
    }

    private void gameLoop() {

        ++this.countUpdateJackpot;
        if (this.countUpdateJackpot >= (long) ConfigGame.getIntValue("update_jackpot_time")) {
            this.updateJackpot();
//            this.bauCuaJackpot();
            this.txJackpot();
            this.txMd5Jackpot();
            this.countUpdateJackpot = 0L;
        }

        try {
            this.checkOnepayTranstion();
        } catch (Exception r) {
            logger.debug(r.getMessage());
        }
        try {
            this.sendNotifyNoHu();
        } catch (Exception e) {
            // this.removeCacheHu();
        }


    }

    public synchronized void sendNotifyNoHu() {

        try {
            CacheService cacheService = new CacheServiceImpl();
            SlotNohuObject slotNohuObject = (SlotNohuObject) cacheService.getObject("notifyNohu");
            if (null != slotNohuObject.username) {
                SlotNoHuMsg msg = new SlotNoHuMsg();
                msg.username = slotNohuObject.username;
                msg.type = slotNohuObject.type;
                msg.totalPrizes = slotNohuObject.totalPrizes;
                msg.gameName = slotNohuObject.gameName;
                for (User user : this.usersSubJackpot) {
                    if (user == null) continue;
                    this.send((BaseMsg) msg, user);
                }

            }
            this.removeCacheHu();
        } catch (KeyNotFoundException e) {
            //  this.removeCacheHu();
        }


    }

    private synchronized void removeCacheHu() {
        CacheService cacheService = new CacheServiceImpl();
        cacheService.setObject("notifyNohu", new SlotNohuObject());
    }

    private synchronized void broadCastMoney() {
        CacheService cacheService = new CacheServiceImpl();
        ArrayList<String> listUser = null;
        try {
            listUser = (ArrayList<String>) cacheService.getObject("List_Money_Change");
            if (null != listUser) {
                for (String username : listUser) {
                    BroadcastMoneyChangeMsg msg = new BroadcastMoneyChangeMsg();
                    msg.currentMoney = userService.getCurrentMoneyUserCache(username, "vin");
                    if (listuserCr.get(username) == null) {
                        listUser.remove(username);
                        continue;
                    }
                    this.send(msg, listuserCr.get(username));
                    listUser.remove(username);
                }
                listUser = new ArrayList<>();
                cacheService.setObject("List_Money_Change", listUser);
            }
        } catch (KeyNotFoundException e) {
            listUser = new ArrayList<>();
            cacheService.setObject("List_Money_Change", listUser);
            Debug.info("Call exception user  after send iss" + listUser.size());
        }
    }

    private synchronized void broadCastTime() {
        CacheService cacheService = new CacheServiceImpl();
        ArrayList<String> listUser = null;
        try {
            listUser = (ArrayList<String>) cacheService.getObject("List_Time_Change");
//            Debug.info("list user iss" + listUser.size());
            if (null != listUser) {
                for (String username : listUser) {
                    BroadcastTimeChangeMsg msg = new BroadcastTimeChangeMsg();
                    msg.time = 0;
                    msg.Error = (byte) 200;
                    if (listuserCr.get(username) == null) {
                        listUser.remove(username);
                        continue;
                    }
                    this.send(msg, listuserCr.get(username));
                    listUser.remove(username);
                }

            }
            cacheService.setObject("List_Time_Change", listUser);
        } catch (Exception e) {
            listUser = new ArrayList<>();
            cacheService.setObject("List_Time_Change", listUser);
            Debug.info("Call exception user  after send iss" + listUser.size());
            // e.printStackTrace();
        }
    }

    private synchronized void broadCastTime2() {
        CacheService cacheService = new CacheServiceImpl();
        ArrayList<String> listUser = null;
        try {
            listUser = (ArrayList<String>) cacheService.getObject("List_Time_Change");
//            Debug.info("list user iss" + listUser.size());
            if (null != listUser) {
                for (String username : listUser) {
                    BroadcastTimeChangeMsg msg = new BroadcastTimeChangeMsg();
                    msg.time = 300;
                    msg.Error = (byte) 200;
                    if (listuserCr.get(username) == null) {
                        listUser.remove(username);
                        continue;
                    }
                    this.send(msg, listuserCr.get(username));
                    listUser.remove(username);
                }

            }
            cacheService.setObject("List_Time_Change", listUser);
        } catch (Exception e) {
            listUser = new ArrayList<>();
            cacheService.setObject("List_Time_Change", listUser);
            Debug.info("Call exception user  after send iss" + listUser.size());
            // e.printStackTrace();
        }
    }

    private final class CheckOutGameTask
            implements Runnable {

        @Override
        public void run() {
            try {

                LobbyModule.this.broadCastOutGame();
            } catch (Exception e) {
                CacheService cacheService = new CacheServiceImpl();
                cacheService.setObject("List_Time_OutGame", new ArrayList<String>());
                Debug.info(e.getMessage());
            }

        }
    }

    private synchronized void broadCastOutGame() {
        CacheService cacheService = new CacheServiceImpl();
        ArrayList<String> listUser = null;
        try {
            listUser = (ArrayList<String>) cacheService.getObject("List_Time_OutGame");
//            Debug.info("list user iss" + listUser.size());
            if (null != listUser) {
                for (String username : listUser) {
                    String[] datas = username.split("\\|");
                    BroadcastOutGameChangeMsg msg = new BroadcastOutGameChangeMsg();
                    String username2 = datas[0];
                    msg.idGame = datas[1];
                    msg.Error = (byte) 200;
                    if (listuserCr.get(username2) == null) {
                        listUser.remove(username2);
                        continue;
                    }
                    this.send(msg, listuserCr.get(username2));
                    listUser.remove(username);
                }

            }
            cacheService.setObject("List_Time_OutGame", listUser);
        } catch (Exception e) {
            listUser = new ArrayList<>();
            cacheService.setObject("List_Time_OutGame", listUser);
            Debug.info("Call exception user  after send iss" + listUser.size());
        }
    }

    private void getEventInfo(User user, DataCmd dataCmd) {
        GetEventVPInfoMsg msg = new GetEventVPInfoMsg();
        HazelcastInstance instance = HazelcastClientFactory.getInstance();
        IMap map = instance.getMap("cacheConfig");
        String sLucky = (String) map.get((Object) "VIPPOINT_EVENT_LUCKY");
        if (sLucky != null && sLucky.equals("1")) {
            Date now = new Date();
            try {
                String currentDate = VinPlayUtils.getDateTimeStr((Date) now).substring(0, 11);
                String etLuckyToday = currentDate + VippointUtils.END_LUCKY_TIME;
                Date eventLuckyTimeEnd = VinPlayUtils.getDateTime((String) etLuckyToday);
                if (now.getTime() < eventLuckyTimeEnd.getTime()) {
                    msg.status = 1;
                    msg.time = (eventLuckyTimeEnd.getTime() - now.getTime()) / 1000L;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            msg.status = 0;
            msg.time = 0L;
        }
        this.send((BaseMsg) msg, user);
    }

    private void initVP() throws JSONException, SQLException, ParseException {
        VippointUtils.init();
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        this.eventTimeStart = VinPlayUtils.getDateTime((String) VippointUtils.START);
        this.eventTimeEnd = VinPlayUtils.getDateTime((String) VippointUtils.END);
        Date eventX2End = VippointUtils.END_X2_TIME;
        Date eventLuckyTimeStart = VinPlayUtils.getDateTime((String) (VippointUtils.START.substring(0, 11) + VippointUtils.START_LUCKY_TIME));
        Date eventLuckyTimeEnd = VinPlayUtils.getDateTime((String) (VippointUtils.START.substring(0, 11) + VippointUtils.END_LUCKY_TIME));
        this.timeLucky = eventLuckyTimeEnd.getTime() - eventLuckyTimeStart.getTime();
        Debug.trace((Object) ("time lucky: " + this.timeLucky));
        HazelcastInstance instance = HazelcastClientFactory.getInstance();
        IMap map = instance.getMap("cacheConfig");
        if (now.getTime() < this.eventTimeStart.getTime()) {
            Debug.trace((Object) ("event chua dien ra: " + new Date()));
            map.put((Object) "VIPPOINT_EVENT_STATUS", (Object) "0");
            map.put((Object) "VIPPOINT_EVENT_X2_STATUS", (Object) "0");
            map.put((Object) "VIPPOINT_EVENT_LUCKY", (Object) "0");
            BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable) this.eventStartTask, this.calculateRemainTime(this.eventTimeStart), TimeUnit.SECONDS);
            BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable) this.eventEndTask, this.calculateRemainTime(this.eventTimeEnd), TimeUnit.SECONDS);
            BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable) this.eventX2EndTask, this.calculateRemainTime(eventX2End), TimeUnit.SECONDS);
            BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable) this.eventLuckyStartTask, this.calculateRemainTime(eventLuckyTimeStart), TimeUnit.SECONDS);
            BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable) this.eventLuckyEndTask, this.calculateRemainTime(eventLuckyTimeEnd), TimeUnit.SECONDS);
        } else if (now.getTime() < this.eventTimeEnd.getTime()) {
            Debug.trace((Object) "event dang dien ra");
            map.put((Object) "VIPPOINT_EVENT_STATUS", (Object) "1");
            if (now.getTime() < eventX2End.getTime()) {
                Debug.trace((Object) "event x2 dang dien ra");
                map.put((Object) "VIPPOINT_EVENT_X2_STATUS", (Object) "1");
                BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable) this.eventX2EndTask, this.calculateRemainTime(eventX2End), TimeUnit.SECONDS);
            } else {
                Debug.trace((Object) "event x2 da ket thuc");
                map.put((Object) "VIPPOINT_EVENT_X2_STATUS", (Object) "0");
            }
            BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable) this.eventEndTask, this.calculateRemainTime(this.eventTimeEnd), TimeUnit.SECONDS);
            String currentDate = VinPlayUtils.getDateTimeStr((Date) now).substring(0, 11);
            String stLuckyToday = currentDate + VippointUtils.START_LUCKY_TIME;
            String etLuckyToday = currentDate + VippointUtils.END_LUCKY_TIME;
            eventLuckyTimeStart = VinPlayUtils.getDateTime((String) stLuckyToday);
            eventLuckyTimeEnd = VinPlayUtils.getDateTime((String) etLuckyToday);
            if (now.getTime() < eventLuckyTimeStart.getTime()) {
                Debug.trace((Object) "event lucky chua dien ra");
                map.put((Object) "VIPPOINT_EVENT_LUCKY", (Object) "0");
                BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable) this.eventLuckyStartTask, this.calculateRemainTime(eventLuckyTimeStart), TimeUnit.SECONDS);
                BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable) this.eventLuckyEndTask, this.calculateRemainTime(eventLuckyTimeEnd), TimeUnit.SECONDS);
            } else if (now.getTime() < eventLuckyTimeEnd.getTime()) {
                Debug.trace((Object) "event lucky dang dien ra");
                List unluckyTime = VippointUtils.randomUnluckyTime((Date) this.eventTimeStart, (Date) this.eventTimeEnd);
                Debug.trace((Object) "unluckyTime: ");
                for (Object dt : unluckyTime) {
                    if (now.getTime() < ((Date) dt).getTime()) {
                        BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable) this.eventUnluckyTask, this.calculateRemainTime((Date) dt) / 60, TimeUnit.MINUTES);
                        Debug.trace((Object) ("OK: " + VinPlayUtils.getDateTimeStr((Date) dt)));
                        continue;
                    }
                    Debug.trace((Object) ("NOK: " + VinPlayUtils.getDateTimeStr((Date) dt)));
                }
                List<Date> luckyTime = VippointUtils.randomLuckyTime((Date) this.eventTimeStart, (Date) this.eventTimeEnd);
                Debug.trace((Object) "luckyTime: ");
                for (Date dt : luckyTime) {
                    if (now.getTime() < dt.getTime()) {
                        BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable) this.eventLuckyTask, this.calculateRemainTime(dt) / 60, TimeUnit.MINUTES);
                        Debug.trace((Object) ("OK: " + VinPlayUtils.getDateTimeStr((Date) dt)));
                        continue;
                    }
                    Debug.trace((Object) ("NOK: " + VinPlayUtils.getDateTimeStr((Date) dt)));
                }
                String sLucky = (String) map.get((Object) "VIPPOINT_EVENT_LUCKY");
                if (sLucky != null && sLucky.equals("0")) {
                    this.sendMsgToAllUser((byte) 1, eventLuckyTimeEnd.getTime() - now.getTime());
                }
                map.put((Object) "VIPPOINT_EVENT_LUCKY", (Object) "1");
                cal.setTime(eventLuckyTimeStart);
                cal.add(5, 1);
                eventLuckyTimeStart = cal.getTime();
                BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable) this.eventLuckyStartTask, this.calculateRemainTime(eventLuckyTimeStart), TimeUnit.SECONDS);
                BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable) this.eventLuckyEndTask, this.calculateRemainTime(eventLuckyTimeEnd), TimeUnit.SECONDS);
            } else {
                Debug.trace((Object) "event lucky da het");
                String sLucky = (String) map.get((Object) "VIPPOINT_EVENT_LUCKY");
                if (sLucky != null && sLucky.equals("1")) {
                    this.sendMsgToAllUser((byte) 0, 0L);
                }
                map.put((Object) "VIPPOINT_EVENT_LUCKY", (Object) "0");
                cal.setTime(eventLuckyTimeStart);
                cal.add(5, 1);
                eventLuckyTimeStart = cal.getTime();
                BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable) this.eventLuckyStartTask, this.calculateRemainTime(eventLuckyTimeStart), TimeUnit.SECONDS);
                cal.setTime(eventLuckyTimeEnd);
                cal.add(5, 1);
                eventLuckyTimeEnd = cal.getTime();
                BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable) this.eventLuckyEndTask, this.calculateRemainTime(eventLuckyTimeEnd), TimeUnit.SECONDS);
            }
        } else {
            Debug.trace((Object) "event da ket thuc");
            map.put((Object) "VIPPOINT_EVENT_STATUS", (Object) "0");
            map.put((Object) "VIPPOINT_EVENT_X2_STATUS", (Object) "0");
            String sLucky = (String) map.get((Object) "VIPPOINT_EVENT_LUCKY");
            if (sLucky != null && sLucky.equals("1")) {
                this.sendMsgToAllUser((byte) 0, 0L);
            }
            map.put((Object) "VIPPOINT_EVENT_LUCKY", (Object) "0");
        }
    }

    private int calculateRemainTime(Date runTime) {
        int time = 0;
        Date now = new Date();
        if (runTime.getTime() > now.getTime()) {
            time = (int) ((runTime.getTime() - now.getTime()) / 1000L);
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
            Debug.trace((Object) ("vippoint event start: " + new Date()));
            HazelcastInstance instance = HazelcastClientFactory.getInstance();
            IMap map = instance.getMap("cacheConfig");
            map.put((Object) "VIPPOINT_EVENT_STATUS", (Object) "1");
            map.put((Object) "VIPPOINT_EVENT_X2_STATUS", (Object) "1");

        }
    }

    private final class EventEndTask
            implements Runnable {
        private EventEndTask() {
        }

        @Override
        public void run() {
            Debug.trace((Object) ("vippoint event end: " + new Date()));
            HazelcastInstance instance = HazelcastClientFactory.getInstance();
            IMap map = instance.getMap("cacheConfig");
            map.put((Object) "VIPPOINT_EVENT_STATUS", (Object) "0");
            map.put((Object) "VIPPOINT_EVENT_X2_STATUS", (Object) "0");
            String sLucky = (String) map.get((Object) "VIPPOINT_EVENT_LUCKY");
            if (sLucky != null && sLucky.equals("1")) {
                LobbyModule.this.sendMsgToAllUser((byte) 0, 0L);
            }
            map.put((Object) "VIPPOINT_EVENT_LUCKY", (Object) "0");
        }
    }

    private final class EventX2EndTask
            implements Runnable {
        private EventX2EndTask() {
        }

        @Override
        public void run() {
            Debug.trace((Object) ("event x2 end: " + new Date()));
            HazelcastInstance instance = HazelcastClientFactory.getInstance();
            IMap map = instance.getMap("cacheConfig");
            map.put((Object) "VIPPOINT_EVENT_X2_STATUS", (Object) "0");
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
                Debug.trace((Object) ("event lucky start: " + now));
                try {
                    VippointUtils.init();
                    HazelcastInstance instance = HazelcastClientFactory.getInstance();
                    IMap map = instance.getMap("cacheConfig");
                    String sLucky = (String) map.get((Object) "VIPPOINT_EVENT_LUCKY");
                    if (sLucky != null && sLucky.equals("0")) {
                        Debug.trace((Object) ("send message to all user: " + now));
                        LobbyModule.this.sendMsgToAllUser((byte) 1, LobbyModule.this.timeLucky);
                    }
                    map.put((Object) "VIPPOINT_EVENT_LUCKY", (Object) "1");
                    List unluckyTime = VippointUtils.randomUnluckyTime((Date) LobbyModule.this.eventTimeStart, (Date) LobbyModule.this.eventTimeEnd);
                    Debug.trace((Object) "unluckyTime: ");
                    for (Object dt : unluckyTime) {
                        if (now.getTime() < ((Date) dt).getTime()) {
                            BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable) LobbyModule.this.eventUnluckyTask, LobbyModule.this.calculateRemainTime((Date) dt) / 60, TimeUnit.MINUTES);
                            Debug.trace((Object) ("OK: " + VinPlayUtils.getDateTimeStr((Date) dt)));
                            continue;
                        }
                        Debug.trace((Object) ("NOK: " + VinPlayUtils.getDateTimeStr((Date) dt)));
                    }
                    List<Date> luckyTime = VippointUtils.randomLuckyTime((Date) LobbyModule.this.eventTimeStart, (Date) LobbyModule.this.eventTimeEnd);
                    Debug.trace((Object) "luckyTime: ");
                    for (Date dt : luckyTime) {
                        if (now.getTime() < dt.getTime()) {
                            BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable) LobbyModule.this.eventLuckyTask, LobbyModule.this.calculateRemainTime(dt) / 60, TimeUnit.MINUTES);
                            Debug.trace((Object) ("OK: " + VinPlayUtils.getDateTimeStr((Date) dt)));
                            continue;
                        }
                        Debug.trace((Object) ("NOK: " + VinPlayUtils.getDateTimeStr((Date) dt)));
                    }
                    BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable) LobbyModule.this.eventLuckyStartTask, 24, TimeUnit.HOURS);
                } catch (Exception e) {
                    Debug.trace((Object) ("event lucky start error: " + e));
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
                Debug.trace((Object) ("event lucky end: " + now));
                HazelcastInstance instance = HazelcastClientFactory.getInstance();
                IMap map = instance.getMap("cacheConfig");
                String sLucky = (String) map.get((Object) "VIPPOINT_EVENT_LUCKY");
                if (sLucky != null && sLucky.equals("1")) {
                    Debug.trace((Object) ("send message to all user: " + now));
                    LobbyModule.this.sendMsgToAllUser((byte) 0, 0L);
                }
                map.put((Object) "VIPPOINT_EVENT_LUCKY", (Object) "0");
                BitZeroServer.getInstance().getTaskScheduler().schedule((Runnable) LobbyModule.this.eventLuckyEndTask, 24, TimeUnit.HOURS);
            }
        }
    }

    private final class EventUnluckyTask
            implements Runnable {
        private EventUnluckyTask() {
        }

        @Override
        public void run() {
            Debug.trace((Object) ("sub vippoint: " + new Date()));
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
            Debug.trace((Object) ("add vippoint: " + new Date()));
            List<String> users = LobbyModule.this.vpService.addVippointEvent();
            for (String nickname : users) {
                HasNewMailMsg mailMsg = new HasNewMailMsg();
                MiniGameUtils.sendMessageToUser(mailMsg, nickname);
            }
        }
    }

    private final class CheckMoneyUser implements Runnable {

        @Override
        public void run() {
            try {

                LobbyModule.this.broadCastMoney();
            } catch (Exception e) { // remove all
                CacheService cacheService = new CacheServiceImpl();
                cacheService.setObject("List_Money_Change", new ArrayList<String>());
                Debug.info(e.getMessage());
            }
        }
    }

    private final class CheckMoMoTransStatusTask
            implements Runnable {
        private CheckMoMoTransStatusTask() {
        }

        @Override
        public void run() {
            Debug.trace((Object) ("update status CheckMoMoTransStatusTask: " + new Date()));
            LobbyModule.this.updateMomoTrans();


        }
    }

    private final class CheckCodePayTransStatusTask
            implements Runnable {

        @Override
        public void run() {
            try {

                LobbyModule.this.broadCastTime();
            } catch (Exception e) {
                CacheService cacheService = new CacheServiceImpl();
                cacheService.setObject("List_Time_Change", new ArrayList<String>());
                Debug.info(e.getMessage());
            }

        }
    }

    private final class BroadcastMessageTask implements Runnable {
        @Override
        public void run() {
            try {
                LobbyModule.this.broadcastMessageService();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void broadcastMessageService() {

        String message = broadcastMessageService.toJson();
        if (StringUtils.isEmpty(message)) {
            return;
        }
        BroadcastMessageMsg msg = new BroadcastMessageMsg();
        msg.message = message;
        List users = ExtensionUtility.globalUserManager.getAllUsers();
        if (users != null) {
            this.send(msg, users);
        }
        broadcastMessageService.clearMessage();

    }

    private synchronized void loginFromOtherDevice() {
        String nickname;
        while ((nickname = cacheService.getQueueElement("LOGIN_OTHER_DEVICE_QUEUE")) != null) {
            try {
                if (StringUtils.isNotEmpty(nickname)) {
                    logger.debug("Kick user " + nickname + " due to login other device");
                    List<User> users = ExtensionUtility.globalUserManager.getUserByName(nickname);
                    if (CollectionUtils.isNotEmpty(users)) {
                        LoginOtherDeviceMsg msg = new LoginOtherDeviceMsg();
                        this.send(msg, users);
                    }
                }
            } finally {
                cacheService.getMap("LOGIN_OTHER_DEVICE_MAP").remove(nickname);
            }
        }
    }

    private final class CheckCodePayHuyStatusTask
            implements Runnable {

        @Override
        public void run() {
            try {
                LobbyModule.this.broadCastTime2();
            } catch (Exception e) {
                CacheService cacheService = new CacheServiceImpl();
                cacheService.setObject("List_Time_Change", new ArrayList<String>());
                Debug.info(e.getMessage());
            }

        }
    }

    // todo : check onpay action
    private synchronized void checkOnepayTranstion() {

        ArrayList<DepositOnePayModel> listOnePayModel = this.rechargeService.GetListDepositOnePayBank(1); // lấy list user và gửi thông báo về cho từng user
        for (DepositOnePayModel model : listOnePayModel) {

            boolean isSended = false;

            int stepCode = 0;
            int code = 0;
            switch (model.Status) {
                case 99: {
                    ReqOnePayOTP msg = new ReqOnePayOTP();
                    msg.transId = model.Id;
                    if (model.Description != null || !model.Description.isEmpty()) {
                        msg.techcombankTranS = model.Description;

                    } else {
                        msg.techcombankTranS = "11";
                    }
                    if (listuserCr.containsKey(model.getNickname())) {
                        this.send(msg, listuserCr.get(model.getNickname()));
                        rechargeService.UpdateDepositStatusOnepay(model.Id, 102, 0); // cập nhật trạng thái đã gửi

                    } else {
                        rechargeService.UpdateDepositStatusOnepay(model.Id, 101, 0); // đã bỏ dở giao dịch
                    }
                    isSended = true;
                    break;
                }
                case 105: {
                    stepCode = 0;
                    code = 105;
                    break;
                }
                case 0: {
                    stepCode = 2;
                    code = 0;
                    break;
                }
                case 2: {
                    stepCode = 3;
                    code = 2;
                    break;
                }
                case 3: {
                    stepCode = 4;
                    code = 3;
                    break;
                }
                case 4: {
                    stepCode = 5;
                    code = 4;
                    break;
                }

            }
            if (!isSended) {
                RequestOnePayAction action = new RequestOnePayAction();
                action.currentMoney = userService.getCurrentMoneyUserCache(model.getNickname(), "vin");
                action.techcombankTrans = model.Description;
                action.stepCode = stepCode;
                if (listuserCr.containsKey(model.getNickname())) {
                    this.send(action, listuserCr.get(model.getNickname()));
                    this.rechargeService.UpdateDepositStatusOnepay(model.Id, code, 0);
                } else {
                    this.rechargeService.UpdateDepositStatusOnepay(model.Id, 101, 0); // người dùng đã thoát
                }
            }

        }


//        Debug.trace((Object) ("update status onepay: Size" + lstActionAdmin.keySet()));
//        Debug.trace((Object) ("update status onepay: Size" + listuserCr.keySet()));
//        for( String userName : lstActionAdmin.keySet()){
//            int status = this.rechargeService.isDoneTranstionOnePay(userName,lstActionAdmin.get(userName));
//            Debug.trace((Object) ("update status onepay: of " + userName+" =====>" + status));
//            if( status==99){ // status = 99 trạng thái cần otp
//                ReqOnePayOTP msg = new ReqOnePayOTP();
//                msg.transId = lstActionAdmin.get(userName);
//                DepositOnePayModel depositOnePayModel = this.rechargeService.FindDepositOnePayById(lstActionAdmin.get(userName));
//                if(depositOnePayModel.Description!=null||!depositOnePayModel.Description.isEmpty()){
//                    msg.techcombankTranS = depositOnePayModel.Description;
//
//                } else {
//                    msg.techcombankTranS="11";
//                }
//                this.send(msg,listuserCr.get(userName));
//
//                rechargeService.UpdateDepositStatusOnepay(lstActionAdmin.get(userName),102,0); // todo :update trạng thái đã gửi
//            }else if(status==105){ // done
//
//                RequestOnePayAction msg = new RequestOnePayAction();
//                DepositOnePayModel depositOnePayModel = this.rechargeService.FindDepositOnePayById(lstActionAdmin.get(userName));
//                if(depositOnePayModel.Description!=null||!depositOnePayModel.Description.isEmpty()){
//                    msg.techcombankTrans = depositOnePayModel.Description;
//
//                } else {
//                    msg.techcombankTrans="11";
//                }
//                msg.stepCode =0;
//                msg.currentMoney =userService.getCurrentMoneyUserCache(userName, "vin");
//                this.send(msg,listuserCr.get(userName));
//                mapUserOnePay.get(userName).setListTrans(removeIteminList( mapUserOnePay.get(userName).getListTrans(),lstActionAdmin.get(userName))); // đã xử lý
//            }else  if ( status ==0){
//
//                RequestOnePayAction msg = new RequestOnePayAction();
//                DepositOnePayModel depositOnePayModel = this.rechargeService.FindDepositOnePayById(lstActionAdmin.get(userName));
//                if(depositOnePayModel.Description!=null||!depositOnePayModel.Description.isEmpty()){
//                    msg.techcombankTrans = depositOnePayModel.Description;
//
//                } else {
//                    msg.techcombankTrans="11";
//                }
//                msg.stepCode =2;
//                msg.currentMoney =userService.getCurrentMoneyUserCache(userName, "vin");
//                this.send(msg,listuserCr.get(userName));
//               // lstActionAdmin.remove(userName);
//                mapUserOnePay.get(userName).setListTrans(removeIteminList( mapUserOnePay.get(userName).getListTrans(),lstActionAdmin.get(userName)));
//            }else  if ( status ==2){
//
//                RequestOnePayAction msg = new RequestOnePayAction();
//                DepositOnePayModel depositOnePayModel = this.rechargeService.FindDepositOnePayById(lstActionAdmin.get(userName));
//                if(depositOnePayModel.Description!=null||!depositOnePayModel.Description.isEmpty()){
//                    msg.techcombankTrans = depositOnePayModel.Description;
//
//                } else {
//                    msg.techcombankTrans="11";
//                }
//                msg.stepCode =3;
//                msg.currentMoney =userService.getCurrentMoneyUserCache(userName, "vin");
//                this.send(msg,listuserCr.get(userName));
//                mapUserOnePay.get(userName).setListTrans(removeIteminList( mapUserOnePay.get(userName).getListTrans(),lstActionAdmin.get(userName)));
//            }else  if ( status ==3){
//
//                RequestOnePayAction msg = new RequestOnePayAction();
//                DepositOnePayModel depositOnePayModel = this.rechargeService.FindDepositOnePayById(lstActionAdmin.get(userName));
//                if(depositOnePayModel.Description!=null||!depositOnePayModel.Description.isEmpty()){
//                    msg.techcombankTrans = depositOnePayModel.Description;
//
//                } else {
//                    msg.techcombankTrans="11";
//                }
//                msg.stepCode =4;
//                msg.currentMoney =userService.getCurrentMoneyUserCache(userName, "vin");
//                this.send(msg,listuserCr.get(userName));
//                mapUserOnePay.get(userName).setListTrans(removeIteminList( mapUserOnePay.get(userName).getListTrans(),lstActionAdmin.get(userName)));
//            }
//            lstActionAdmin.remove(userName);
//        }

    }


    public int getEsmsOTP(String nickname, String mobile, String type) throws Exception {
        int code = 1;
        HazelcastInstance client = HazelcastClientFactory.getInstance();
        IMap<String, UserModel> userMap = client.getMap("users");
        UserModel model = null;
        if (userMap.containsKey((Object) nickname)) {
            model = (UserModel) userMap.get((Object) nickname);
            UserCacheModel userCacheModel = (UserCacheModel) model;
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
                    otp = VinPlayUtils.genOtpSMS((String) model.getMobile(), (String) "");
                    Debug.trace("Lobby OTP: " + model.getMobile());
                } catch (Exception e) {
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
                DvtUtils.initDVT((boolean) false);
            } catch (Exception e) {
                e.printStackTrace();
                Debug.trace((Object) ("init slot free errot: " + e));
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
            try {
                List<Document> pendingTrans = rechargeDAO.getRechargeByGachtheRecently();
                Debug.trace((Object) ("update trans error: " + pendingTrans.size()));
                if (pendingTrans.size() > 0) {
                    pendingTrans.forEach((document) -> {
                        NapTheDienThoaiMsg msg = new NapTheDienThoaiMsg();
                        if (document.getInteger("code") == 0) {
                            msg.Error = Byte.parseByte(document.getInteger("code").toString());
                            msg.currentMoney = document.getLong("current_money") + document.getLong("add_money");
                            msg.numFail = 0;
                            msg.timeFail = 0;
                        } else {
                            msg.Error = (byte) 35;
                        }

                        Debug.trace((Object) ("Send Noti Recharge Gachthe error: " + msg.Error));
                        //User user = ExtensionUtility.globalUserManager.getUserById(document.getInteger("user_id"));
                        //User user = ExtensionUtility.getExtension().getApi().getUserById(document.getInteger("user_id"));
                        List<User> user = ExtensionUtility.getExtension().getApi().getUserByName(document.getString("nick_name"));
                        if (user != null) {
                            // lấy theo user name 
                            rechargeDAO.UpdateGachtheTransctionsSent(document.getString("request_id"));
                            send((BaseMsg) msg, user);
                        } else {
                            user = ExtensionUtility.getExtension().getApi().getUserByName(document.getString("username"));
                            if (user != null) {
                                rechargeDAO.UpdateGachtheTransctionsSent(document.getString("request_id"));
                                send((BaseMsg) msg, user);
                            } else {
                                user = ExtensionUtility.globalUserManager.getUserByName(document.getString("username"));
                                rechargeDAO.UpdateGachtheTransctionsSent(document.getString("request_id"));
                                send((BaseMsg) msg, user);
                            }
                        }
                    });
                }
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                String sStackTrace = sw.toString(); // stack trace as a string
                Debug.trace((Object) sStackTrace);
                Debug.trace((Object) ("update trans error: " + e));
            }
        }
    }
}

