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
 *  bitzero.server.entities.managers.IUserManager
 *  bitzero.server.exceptions.BZException
 *  bitzero.server.extensions.BZExtension
 *  bitzero.server.extensions.BaseClientRequestHandler
 *  bitzero.server.extensions.data.BaseMsg
 *  bitzero.server.extensions.data.DataCmd
 *  bitzero.server.util.TaskScheduler
 *  bitzero.util.ExtensionUtility
 *  bitzero.util.common.business.CommonHandle
 *  bitzero.util.common.business.Debug
 *  com.vinplay.dal.entities.taixiu.ResultTaiXiu
 *  com.vinplay.dal.service.MiniGameService
 *  com.vinplay.dal.service.TaiXiuService
 *  com.vinplay.dal.service.impl.MiniGameServiceImpl
 *  com.vinplay.dal.service.impl.TaiXiuServiceImpl
 *  com.vinplay.usercore.service.impl.UserServiceImpl
 *  com.vinplay.vbee.common.response.MoneyResponse
 *  com.vinplay.vbee.common.statics.TransType
 *  com.vinplay.vbee.common.utils.DateTimeUtils
 */
package game.modules.minigame;

import bitzero.server.BitZeroServer;
import bitzero.server.core.*;
import bitzero.server.entities.User;
import bitzero.server.exceptions.BZException;
import bitzero.server.extensions.BaseClientRequestHandler;
import bitzero.server.extensions.data.BaseMsg;
import bitzero.server.extensions.data.DataCmd;
import bitzero.util.ExtensionUtility;
import bitzero.util.common.business.CommonHandle;
import bitzero.util.common.business.Debug;
import com.vinplay.common.HttpCommon;
import com.vinplay.dal.entities.taixiu.ResultTaiXiu;
import com.vinplay.dal.entities.taixiu.ResultTaiXiuMd5;
import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.MiniGameService;
import com.vinplay.dal.service.TaiXiuService;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.dal.service.impl.MiniGameServiceImpl;
import com.vinplay.dal.service.impl.TaiXiuMd5ServiceImpl;
import com.vinplay.dal.service.impl.TaiXiuServiceImpl;
import com.vinplay.miniGame.TaiXiuAdminReportObj;
import com.vinplay.miniGame.TaiXiuSetAmountBotFake;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.config.VBeePath;
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.exceptions.KeyNotFoundException;
import com.vinplay.vbee.common.response.MoneyResponse;
import com.vinplay.vbee.common.response.minigame.TaiXiuAdmin;
import com.vinplay.vbee.common.response.minigame.TaiXiuChatMsg;
import com.vinplay.vbee.common.statics.TransType;
import com.vinplay.vbee.common.utils.DateTimeUtils;
import game.modules.chat.ChatModule;
import game.modules.chat.cmd.send.ChatMsg;
import game.modules.minigame.cmd.rev.*;
import game.modules.minigame.cmd.send.*;
import game.modules.minigame.entities.BotMinigame;
import game.modules.minigame.entities.BotTaiXiu;
import game.modules.minigame.room.MGRoom;
import game.modules.minigame.room.MGRoomTaiXiu;
import game.modules.minigame.utils.GenerationTaiXiu;
import game.modules.minigame.utils.MiniGameUtils;
import game.modules.minigame.utils.TaiXiuUtils;
import game.utils.GameUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.*;

public class TaiXiuModule extends BaseClientRequestHandler {
    private static final Logger logger = Logger.getLogger(TaiXiuModule.class);
    private Map<String, MGRoom> rooms = new HashMap<String, MGRoom>();
    private final Runnable gameLoopTask = new GameLoopTask();  // thread game loop
    private final Runnable serverReadyTask = new ServerReadyTask(); // thread
    //private final Runnable botChatTask = new ScheduleBotChatTask();  // thread bot chat
    private final Runnable calculatingTXVinTask = new CalculatingTaiXiuPrize((short) 1);  // thread tính tài xỉu vin
    private final Runnable updateCacheTopDay = new UpdateCacheTopDay();  // thread tính tài xỉu top theo ngay
    private final Runnable updateCacheTopMonth = new UpdateCacheTopMonth();  // thread tính tài xỉu theo thang
    private final Runnable calculatingTXXuTask = new CalculatingTaiXiuPrize((short) 0);  // thread tính tài xỉu xu
    //private final Runnable rewardThanhDuDailyTask = new RewardThanhDuDaily();
    private final CacheService cacheService = new CacheServiceImpl(); // caching hazelcast service
    private int count = 0;
    private boolean serverReady = false;
    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);  // thread pool 10 cái thread
    private long referenceTaiXiuId; // được lấy từ trong database
    private TaiXiuService txService = new TaiXiuMd5ServiceImpl();
    private MiniGameService mgService = new MiniGameServiceImpl();
    private List<ResultTaiXiu> lichSuPhienTX = new ArrayList<ResultTaiXiu>();
    private GenerationTaiXiu generationTX = new GenerationTaiXiu();
    private short result = (short) -1;
    private long fundRutLoc = 0L;
    private int countRutLoc = 0;
    private int countReqRutLoc = 0;
    private int[] rutLocPrizes;
    private int[] phanBoGiaiThuong;
    private boolean enableRutLoc = false;
    private int tongSoNguoiRutLocLanTruoc = 30;
    private List<BotTaiXiu> botsVin = new ArrayList<BotTaiXiu>();
    private List<BotTaiXiu> botsXu = new ArrayList<BotTaiXiu>();
    private short forceBetSide = (short) -1;
    private long MinCtrl = 500000L;
    private long MaxCtrl = 1000000L;
    private List<String> listChat = new ArrayList<String>();
    private List<String> listChatUsers = new ArrayList<String>();
    public static String CacheCurrentReference = "Tai_xiu_current_reference_md5";
    public static long moneyHu = 50000000; // được lấy từ trong database
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(8);

    private int amountBotTaiFake = 0;
    private int amountBotXiuFake = 0;

    public void init() {
        TaiXiuChatMsg taiXiuChatMsg = new TaiXiuChatMsg();
        cacheService.setObject("admin_lst_msg_md5", taiXiuChatMsg);
        cacheService.setObject("admin_msg_md5", taiXiuChatMsg);
        Debug.info("referentTaiXiuId là " + this.referenceTaiXiuId);
        this.rooms.put(MGRoomTaiXiu.getKeyRoom((short) 1), new MGRoomTaiXiu("TaiXiu_1", this.referenceTaiXiuId, (byte) 1));
        this.rooms.put(MGRoomTaiXiu.getKeyRoom((short) 0), new MGRoomTaiXiu("TaiXiu_0", this.referenceTaiXiuId, (byte) 0));
        //Debug.info("referentTaiXiuId là " + this.referenceTaiXiuId);
        this.loadData();
        Debug.info("referentTaiXiuId sau khi load data là " + this.referenceTaiXiuId);
        //  this.loadChatData();
        // this.loadChatUsers();
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.gameLoopTask, 10, 1, TimeUnit.SECONDS);
        //BitZeroServer.getInstance().getTaskScheduler().schedule(this.botChatTask, 10, TimeUnit.SECONDS);
        BitZeroServer.getInstance().getTaskScheduler().schedule(this.serverReadyTask, 10, TimeUnit.SECONDS);
        Debug.trace("SERVER READY TASK RUNNING...");
        this.getParentExtension().addEventListener((IBZEventType) BZEventType.USER_DISCONNECT, (IBZEventListener) this);

        scheduler.scheduleAtFixedRate(updateCacheTopDay, 2000, 3600, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(updateCacheTopMonth, 2000, 86400, TimeUnit.SECONDS);
        try {
            int remainTimeTraThuongThanhDu = MiniGameUtils.calculateTimeRewardOnNextDay("");
            //  BitZeroServer.getInstance().getTaskScheduler().schedule(this.rewardThanhDuDailyTask, remainTimeTraThuongThanhDu, TimeUnit.SECONDS);
        } catch (ParseException e) {
            sendLogToTele(e.getMessage());
            Debug.trace((Object[]) new Object[]{"Calculate time reward Thanh du error ", e.getMessage()});
        }
    }

    // TODO: lấy list chat từ trong config
    public void loadChatData() {
        try {
            String entry;
            BufferedReader br2 = new BufferedReader(new InputStreamReader((InputStream) new FileInputStream(VBeePath.basePath.concat("config/list_chat.txt")), "UTF8"));
            while ((entry = br2.readLine()) != null) {
                this.listChat.add(entry);
            }
            br2.close();
            Debug.trace("BOT CHAT :" + listChat.size());
        } catch (IOException entry) {
            sendLogToTele(entry.getMessage());
            entry.printStackTrace();
        }
    }

    // TODO: load chat user
    public void loadChatUsers() {  // load chat user
        try {
            String entry;
            BufferedReader br2 = new BufferedReader(new InputStreamReader((InputStream) new FileInputStream(VBeePath.basePath.concat("config/bots.txt")), "UTF8")); // đọc từ file bots.txt
            while ((entry = br2.readLine()) != null) {
                this.listChatUsers.add(entry);
            }
            br2.close();
            Debug.trace("BOT CHAT USERS :" + listChatUsers.size());
        } catch (FileNotFoundException entry) {
            sendLogToTele(entry.getMessage());
        } catch (IOException entry) {
            // empty catch block
            sendLogToTele(entry.getMessage());
        }
    }

    public void handleServerEvent(IBZEvent ibzevent) throws BZException {
        if (ibzevent.getType() == BZEventType.USER_DISCONNECT) {
            User user = (User) ibzevent.getParameter((IBZEventParam) BZEventParam.USER);
            this.userDis(user);
        }
    }

    // user out room todo: user rời khỏi phòng
    private void userDis(User user) {
        MGRoom room = (MGRoom) user.getProperty((Object) "MGROOM_TAI_XIU_INFO");  // lấy ra object room tài xỉu info trong map thuộc tính
        if (room != null) {
            room.quitRoom(user); // management room quit user xóa user khỏi list user trong phòng
        }
    }

    //todo : load referenceId và tài xỉu phiên từ trong database
    private void loadData() {
        this.referenceTaiXiuId = 1L;
        try {
            this.referenceTaiXiuId = this.mgService.getReferenceId(Games.TAI_XIU_MD5.getId());
            this.lichSuPhienTX = this.txService.getListLichSuPhien(120, 1);
        } catch (SQLException e) {
            sendLogToTele(e.getMessage());
            Debug.trace((Object[]) new Object[]{"Load reference error ", e.getMessage()});
        }
        try {
            this.generationTX.readConfig();
        } catch (IOException e) {
            sendLogToTele(e.getMessage());
            Debug.trace((Object[]) new Object[]{"Load cau tai xiu error ", e.getMessage()});
        }
        try {
            this.fundRutLoc = this.txService.getPotTanLoc();
            if (this.fundRutLoc < 100000L) {
                this.countRutLoc = -1;
            }
        } catch (SQLException e) {
            sendLogToTele(e.getMessage());
            Debug.trace((Object[]) new Object[]{"Load fund tan loc error ", e.getMessage()});
        }
    }

    // TUTL OK đã sửa cho MD5
    private void saveReferences() {
        try {
            this.mgService.saveReferenceId(this.referenceTaiXiuId, Games.TAI_XIU_MD5.getId());
        } catch (SQLException e) {
            sendLogToTele(e.getMessage());
            Debug.trace((Object) ("Save reference error " + e.getMessage()));
        }
    }

    // todo : handle dựa trên data cmd
    public void handleClientRequest(User user, DataCmd dataCmd) {
        switch (dataCmd.getId()) {
            case 2000: {
                // mở game lên vào đây
                System.out.println("=====> Subscribe: " + dataCmd.getId() + " | " + user.getName());
                this.subcribeMiniGame(user, dataCmd);
                break;
            }
            case 2001: {
                System.out.println("=====> Unsubscribe: " + dataCmd.getId() + " | " + user.getName());
                this.unsubscribeMiniGame(user, dataCmd);
                break;
            }
            case 2002: {
                this.changeRoom(user, dataCmd);
                break;
            }
            case 2110: {
                System.out.println("=====> Bet: " + dataCmd.getId() + " | " + user.getName());
                if (GameUtils.disablePlayMiniGame(user)) {
                    return;
                }
                this.betTaiXiu(user, dataCmd);  // nếu id là 2110 thì vào đánh bài
                break;
            }
            case 2116: {
                System.out.println("=====> History: " + dataCmd.getId() + " | " + user.getName());
                this.getLichSuPhienTX(user);
                break;
            }
            case 2118: {
//                if (GameUtils.disablePlayMiniGame(user)) {
//                    return;
//                }
//                this.tanLoc(user, dataCmd);
                break;
            }
            case 2119: {
//                if (GameUtils.disablePlayMiniGame(user)) {
//                    return;
//                }
//                this.rutLoc(user, dataCmd);
            }
        }
    }

    private void subcribeMiniGame(User user, DataCmd dataCmd) {
        SubcribeMinigameCmd cmd = new SubcribeMinigameCmd(dataCmd);
        this.doSubcribeMiniGame(user, cmd.gameId, cmd.roomId);
        LichSuPhienMsg msgLSGD = new LichSuPhienMsg();
        msgLSGD.data = TaiXiuUtils.buildLichSuPhien(this.lichSuPhienTX, 22);
        this.send((BaseMsg) msgLSGD, user);
        UpdateRutLocMsg rutLocMsg = new UpdateRutLocMsg();
        try {
            rutLocMsg.soLuotRut = this.txService.getLuotRutLoc(user.getName());
        } catch (Exception e) {
            sendLogToTele(e.getMessage());
            Debug.trace((Object[]) new Object[]{"Get so luot rut loc " + user.getName() + " error ", e.getMessage()});
        }
        this.send((BaseMsg) rutLocMsg, user);
        UpdateFundTanLocMsg fundRLMsg = new UpdateFundTanLocMsg();
        fundRLMsg.value = this.fundRutLoc;
        this.send((BaseMsg) fundRLMsg, user);
    }

    // vào room
    private void doSubcribeMiniGame(User user, short gameId, short roomId) {
        switch (gameId) {
            case 2: {
                short moneyType = MGRoomTaiXiu.getMoneyType(roomId);
                String keyRoom = MGRoomTaiXiu.getKeyRoom(moneyType);
                MGRoomTaiXiu roomTX = (MGRoomTaiXiu) this.getGame(keyRoom);
                if (roomTX != null) {
                    // room tài xỉu add user vào room
                    roomTX.joinRoom(user);
                    roomTX.updateTaiXiuInfo(user, this.getRemainTimeRutLoc());
                    break;
                }
                CommonHandle.writeErrLog((String) "Game TAI XIU not found");
                break;
            }
            default: {
                Debug.trace((Object) "Game id not found");
            }
        }
    }

    private void unsubscribeMiniGame(User user, DataCmd dataCmd) {
        UnsubscribeMiniGameCmd cmd = new UnsubscribeMiniGameCmd(dataCmd);
        this.doUnsubscribeMiniGame(user, cmd.gameId, cmd.roomId);
    }

    // todo : rời khỏi room
    private void doUnsubscribeMiniGame(User user, short gameId, short roomId) {
        switch (gameId) {
            case 2: {
                short moneyType = MGRoomTaiXiu.getMoneyType(roomId);
                String keyRoom = MGRoomTaiXiu.getKeyRoom(moneyType);
                MGRoom room = this.getGame(keyRoom);
                if (room == null) break;
                room.quitRoom(user);
            }
        }
    }

    private void changeRoom(User user, DataCmd dataCmd) {
        ChangeRoomMinigameCmd cmd = new ChangeRoomMinigameCmd(dataCmd);
        this.doUnsubscribeMiniGame(user, cmd.gameId, cmd.lastRoomId);
        this.doSubcribeMiniGame(user, cmd.gameId, cmd.newRoomId);
    }

    //todo : bắt đầu một round tài xỉu mới
    private void startNewRoundTX() {
        MGRoomTaiXiu roomTXVin = this.getRoomTX((short) 1);
        MGRoomTaiXiu roomTXXu = this.getRoomTX((short) 0);
        ++this.referenceTaiXiuId;
        //update referenceId

        roomTXVin.startNewGame(this.referenceTaiXiuId);
        roomTXXu.startNewGame(this.referenceTaiXiuId);
        StartNewGameTaiXiuMsg msg = new StartNewGameTaiXiuMsg();
        msg.referenceId = this.referenceTaiXiuId;
        msg.moneyHu = moneyHu;
//        msg.remainTimeRutLoc = this.getRemainTimeRutLoc();
        this.sendMessageToTaiXiuNewThread(msg);
        this.saveReferences();
        this.cacheService.setValue("md5_allow_betting_" + this.referenceTaiXiuId, 1);
        this.cacheService.setValue(CacheCurrentReference, Long.toString(this.referenceTaiXiuId));
    }

    //todo : Schedule Bot tai xiu
    private void scheduleBot() {
        try {
            this.botsXu.clear();
            this.botsVin.clear();
            this.botsVin = BotMinigame.getBotTaiXiu("vin");
            Debug.trace((Object) ("BOTS VIN: " + this.botsVin.size()));
//      List<BotTaiXiu> botsVip = BotMinigame.getVipBotTaiXiu();
//      this.botsVin.addAll(botsVip);
//      Debug.trace((Object) ("TX BOTS VIP: " + botsVip.size()));
            //this.botsXu = BotMinigame.getBotTaiXiu("xu");
//            Debug.trace((Object) ("BOTS XU: " + this.botsXu.size()));
        } catch (Exception e) {
            sendLogToTele(e.getMessage());
            GameUtils.sendAlert("Bot tai xiu start error: " + e.getMessage() + ", time= " + DateTimeUtils.getCurrentTime());
        }
    }

    private void botBet(int count) {
        MGRoomTaiXiu roomVin = this.getRoomTX((short) 1);
        MGRoomTaiXiu roomXu = this.getRoomTX((short) 0);
        String[] strs = {"Jocelyn", "Kelsey", "Fallon", "Maynard", "Mildred", "Aubrey"};
        for (BotTaiXiu b : this.botsVin) {
            if (b.getTimeBetting() != 60 - count) continue;
            if (!ArrayUtils.contains(strs, b.getNickname())) {
                roomVin.betTaiXiu(b.getNickname(), 0, b.getBetValue(), b.getTimeBetting(), (short) 1, b.getBetSide(), true);
            }
        }

//    for (BotTaiXiu b : this.botsXu) {
//      if (b.getTimeBetting() != 60 - count) continue;
//      roomXu.betTaiXiu(b.getNickname(), 0, b.getBetValue(), b.getTimeBetting(), (short) 0, b.getBetSide(), true);
//    }
    }

    // todo : user đặt cược tiền
    public void betTaiXiu(User user, DataCmd dataCmd) {
        // convert từ dataCmd sang BetTaixiu cmd
        BetTaiXiuCmd cmd = new BetTaiXiuCmd(dataCmd);
        MGRoomTaiXiu roomTX = this.getRoomTX(cmd.moneyType);
        if (roomTX != null) {
            roomTX.betTaiXiu(user, cmd); // room tài xỉu khác null thì bet tài xỉu với user và cmd
        }
    }

    // todo : lấy số tiền thực tế người dùng dặt
    public void getUserPotTaiXiu() {
        short typeBet = 1;
        // this.getRoomTX((short) 1).getPotTai(); // tổng số tiền bên tài
        // lấy người chơi đặt tài
        TaiXiuAdminReportObj taiXiuAdminReportObj = new TaiXiuAdminReportObj(this.getRoomTX(typeBet).getUserBetTai(),
                this.getRoomTX(typeBet).getUserBetXiu(), this.getRoomTX(typeBet).getNumberUserRealTai(), this.getRoomTX(typeBet).getNumberUserRealXiu(),
                this.getRoomTX(typeBet).getTotalMoneyTai(), this.getRoomTX(typeBet).getTotalMoneyXiu(), this.referenceTaiXiuId);
        taiXiuAdminReportObj.setNumberUserAndBotBetTai(this.getRoomTX(typeBet).getNumberUerAndBotTai());
        taiXiuAdminReportObj.setNumberUserAndBotBetXiu(this.getRoomTX(typeBet).getNumberUerAndBotXiu());
        taiXiuAdminReportObj.setContributors(this.getRoomTX(typeBet).getListTransaction());
        taiXiuAdminReportObj.setRealTime(this.getRoomTX(typeBet).getRemainTime());
        taiXiuAdminReportObj.setBettingRound(this.getRoomTX(typeBet).bettingRound);
        List<TaiXiuChatMsg> listChat = new ArrayList<>();
        try {
            listChat = (List<TaiXiuChatMsg>) cacheService.getObject("lstTaiXiuAdminMsg");
        } catch (KeyNotFoundException e) {
//            e.printStackTrace();
            listChat = new ArrayList<>();
        }
        taiXiuAdminReportObj.setLstMsg(listChat);
        taiXiuAdminReportObj.setGetListChatUsers(this.getListChatUsers());
        taiXiuAdminReportObj.setTaiXiuMd5Hash(this.getRoomTX(typeBet).resultTX.getMd5TextResult());
        taiXiuAdminReportObj.setTaiXiuPlainResult(this.getRoomTX(typeBet).resultTX.getPlantTextResult());
        cacheService.setValue("user_tai_xiu_md5", taiXiuAdminReportObj.toJson());
        cacheService.setObject("lstTaiXiuAdminMsg", new ArrayList<>());
    }

    public List<String> getListChatUsers() {
        return listChatUsers;
    }

    private synchronized void gameLoop() { // game loop ----> lifecycle game
        try {
            MGRoomTaiXiu roomTXVin = this.getRoomTX((short) 1);
            MGRoomTaiXiu roomTXXu = this.getRoomTX((short) 0);
            if (count == 0) {
                this.generateTaiXiuDices(roomTXVin, roomTXXu);
            }
            ++this.count;
            this.botBet(this.count);
            try {
                TaiXiuSetAmountBotFake taiXiuSetAmountBotFake = (TaiXiuSetAmountBotFake) cacheService.getObject("taixiu_bot_fake_amount_md5");
                if (taiXiuSetAmountBotFake != null) {
                    amountBotTaiFake += (taiXiuSetAmountBotFake.getNumberBotTaiFake()) / 40;
                    amountBotXiuFake += (taiXiuSetAmountBotFake.getNumberBotXiuFake()) / 40;
                }
            } catch (KeyNotFoundException ex) {
//                sendLogToTele(ex.getMessage());
                amountBotXiuFake = 0;
                amountBotTaiFake = 0;
            }

            roomTXXu.updateTaiXiuPerSecond(amountBotTaiFake, amountBotXiuFake, count);
            roomTXVin.updateTaiXiuPerSecond(amountBotTaiFake, amountBotXiuFake, count);


            // lưu toàn bộ trạng thái của game vào cache service
            // trạng thái này phục vụ cho APIs và module wsreport
            this.getUserPotTaiXiu();
            this.sendTXTime(roomTXVin.getRemainTime(), roomTXVin.isBetting()); // todo tinh thoi gian con lai
            switch (this.count) {
                case 55: {
                    roomTXVin.disableBetting();
                    roomTXXu.disableBetting();
                    break;
                }
                case 60: {
                    roomTXVin.finish();
                    roomTXXu.finish();
                    break;
                }
//
//                case 58: {
//                    this.forceBetSide = roomTXVin.suggestResult();
//                    break;
//                }
                case 61: {
                    this.generateTaiXiuDicesMD5(roomTXVin, roomTXXu);
                    break;
                }
                case 63: {
                    BitZeroServer.getInstance().getTaskScheduler().schedule(this.calculatingTXVinTask, 1, TimeUnit.SECONDS);
                    BitZeroServer.getInstance().getTaskScheduler().schedule(this.calculatingTXXuTask, 1, TimeUnit.SECONDS);
                    amountBotTaiFake = 0;
                    amountBotXiuFake = 0;
                    break;
                }
                case 70: {
                    ScheduleBotTask t = new ScheduleBotTask();
                    this.executor.execute(t);
                    break;
                }
                case 75: {
                    try {
                        this.startNewRoundTX();
                        amountBotTaiFake = 0;
                        amountBotXiuFake = 0;
                        this.count = 0;

                    } catch (Exception e) {
                        sendLogToTele(e.getMessage());
                        Debug.trace("got bug", e.getCause());
                        ExceptionUtils.printRootCauseStackTrace(e);
                    }

                }
            }

        } catch (Exception e) {
            sendLogToTele(e.getMessage());
            Debug.trace((Object[]) new Object[]{"Exception: " + e.getMessage(), e});
            ExceptionUtils.printRootCauseStackTrace(e);
        }
    }

    private void forceBalanceLateGame(MGRoomTaiXiu roomTXVin) {
        int randomNum;
        Random rnd = new Random();
        int randomMin = ThreadLocalRandom.current().nextInt(0, (int) this.MaxCtrl);
        this.MinCtrl += randomMin;

        this.forceBetSide = roomTXVin.soTienNguoiChoiDatTai > roomTXVin.soTienNguoiChoiDatXiu
                && roomTXVin.soTienNguoiChoiDatTai - roomTXVin.soTienNguoiChoiDatXiu > this.MinCtrl ? (short) 0 :
                (roomTXVin.soTienNguoiChoiDatTai < roomTXVin.soTienNguoiChoiDatXiu && roomTXVin.soTienNguoiChoiDatXiu - roomTXVin.soTienNguoiChoiDatTai > this.MinCtrl ? (short) 1 :
                        ((randomNum = ThreadLocalRandom.current().nextInt(0, 1000560000)) % 2 == 0 ? (short) 0 : 1));
        //this.forceBetSide = (randomNum = ThreadLocalRandom.current().nextInt(0, 1000560000)) % 2 == 0 ? (short)0 : 1;
        try {
            try (PrintStream out = new PrintStream(new FileOutputStream("logTaiXiu.txt", true));) {
                out.println(roomTXVin.referenceId + "> Tai:" + roomTXVin.soTienNguoiChoiDatTai + "(" + roomTXVin.getUserBetTai() + ") Xiu:" + roomTXVin.soTienNguoiChoiDatXiu + "(" + roomTXVin.getUserBetXiu() + ") >" + this.forceBetSide);
                out.close();
            }
        } catch (Exception out) {
            sendLogToTele(out.getMessage());
            // empty catch block
        }
    }

    private void resetForceBalance() {
        this.forceBetSide = (short) -1;
    }

    private void generateTaiXiuDices(MGRoomTaiXiu roomTXVin, MGRoomTaiXiu roomTXXu) {
        short[] dices;

        dices = this.generationTX.generateDices();
        String result = generationTX.buildResultText(dices);
        String md5 = GenerationTaiXiu.hashMD5(result);

        ResultTaiXiuMd5 resultTaiXiuMd5 = new ResultTaiXiuMd5();
        resultTaiXiuMd5.dice1 = dices[0];
        resultTaiXiuMd5.dice2 = dices[1];
        resultTaiXiuMd5.dice3 = dices[2];
        resultTaiXiuMd5.setMd5TextResult(md5);
        resultTaiXiuMd5.setPlantTextResult(result);
        roomTXVin.resultTX = resultTaiXiuMd5;
        roomTXXu.resultTX = resultTaiXiuMd5;
    }


    private void generateTaiXiuDicesMD5(MGRoomTaiXiu roomTXVin, MGRoomTaiXiu roomTXXu) {
        String keyBeCang = "auto";
        try {
            keyBeCang = cacheService.getValueStr("tai_xiu_be_cang_md5");

        } catch (Exception r) {
            sendLogToTele(r.getMessage());
            Debug.info("Loi get key becang");
        }

        if ("tai".equals(keyBeCang)) {
            this.forceBetSide = 1;
        } else if ("xiu".equals(keyBeCang)) {
            this.forceBetSide = 0;
        } else {
            this.forceBetSide = -1;
        }
        cacheService.setValue("tai_xiu_be_cang_md5", "auto");

        short[] dices = new short[3];

        // can thiep be cau
        if (forceBetSide != -1) {
            dices = this.generationTX.generateResult(this.forceBetSide);
            String result = generationTX.buildResultText(dices);
            roomTXVin.resultTX.setPlantTextResult(result);
            roomTXXu.resultTX.setPlantTextResult(result);
        } else {
            dices[0] = (short) roomTXVin.resultTX.dice1;
            dices[1] = (short) roomTXVin.resultTX.dice2;
            dices[2] = (short) roomTXVin.resultTX.dice3;
        }

        this.resetForceBalance();
        short total = (short) (dices[0] + dices[1] + dices[2]);
        this.result = total > 10 ? (short) 1 : 0;

        /**
         * Show ket qua ra man
         */

        roomTXVin.updateResultDices(dices, this.result);
        roomTXXu.updateResultDices(dices, this.result);
        ResultTaiXiuMd5 resultTX = roomTXVin.resultTX;
        resultTX.referenceId = this.referenceTaiXiuId;
        resultTX.result = this.result;
        resultTX.dice1 = dices[0];
        resultTX.dice2 = dices[1];
        resultTX.dice3 = dices[2];
        Debug.trace("GENERATE RESULT DICES: " + dices[0] + " - " + dices[1] + " - " + dices[2] + "   " + this.result);
        this.lichSuPhienTX.add(resultTX);
        if (this.lichSuPhienTX.size() > 120) {
            this.lichSuPhienTX.remove(0);
        }
    }


    private void getLichSuPhienTX(User user) {
        LichSuPhienMsg msg = new LichSuPhienMsg();
        msg.data = TaiXiuUtils.buildLichSuPhien(this.lichSuPhienTX, 22);
        Debug.trace((Object) ("LSDG: " + TaiXiuUtils.logLichSuPhien(this.lichSuPhienTX, 120)));
        this.send((BaseMsg) msg, user);
    }

    private void tanLoc(User user, DataCmd dataCmd) {
        TanLocCMD cmd = new TanLocCMD(dataCmd);
        TanLocMsg msg = new TanLocMsg();
        msg.result = 1;
        UserServiceImpl userService = new UserServiceImpl();
        long curretnMoney = userService.getMoneyUserCache(user.getName(), "vin");
        if (cmd.money <= curretnMoney) {
            if (cmd.money >= 1000L) {
                boolean success;
                MoneyResponse response = userService.updateMoney(user.getName(), -cmd.money, "vin", "TaiXiu", "Tài Xỉu - Tán lộc", "Tán lộc tài xỉu", 0L, null, TransType.NO_VIPPOINT);
                if (response != null && response.isSuccess() && (success = this.updateFundRutLoc(cmd.money))) {
                    curretnMoney = response.getCurrentMoney();
                    msg.result = 0;
                    try {
                        this.txService.logTanLoc(user.getName(), cmd.money);
                    } catch (IOException | InterruptedException | TimeoutException exception) {
                        // empty catch block
                        sendLogToTele(exception.getMessage());
                    }
                    if (this.countRutLoc == -1 && this.fundRutLoc >= 100000L) {
                        this.countRutLoc = 0;
                        StartNewRoundRutLocMsg newRoundMsg = new StartNewRoundRutLocMsg();
                        newRoundMsg.remainTime = this.getRemainTimeRutLoc();
                        this.sendMessageToTaiXiu(newRoundMsg);
                    }
                }
            } else {
                msg.result = (short) 3;
            }
        } else {
            msg.result = (short) 2;
        }
        msg.currentMoney = curretnMoney;
        this.send((BaseMsg) msg, user);
    }

    private synchronized void rutLoc(User user, DataCmd dataCmd) {
        ResultRutLocMsg msg = new ResultRutLocMsg();
        UserServiceImpl userService = new UserServiceImpl();
        long currentMoney = userService.getMoneyUserCache(user.getName(), "vin");
        if (this.countRutLoc > -1) {
            if (this.enableRutLoc) {
                int soLuotRut = 0;
                try {
                    soLuotRut = this.txService.getLuotRutLoc(user.getName());
                } catch (SQLException e) {
                    sendLogToTele(e.getMessage());
                    Debug.trace((Object[]) new Object[]{"Get so luot rut loc error ", e.getMessage()});
                }
                if (soLuotRut > 0) {
                    MoneyResponse response;
                    int indexPirze = -1;
                    int prize = 0;
                    for (int i = 0; i < this.phanBoGiaiThuong.length; ++i) {
                        if (this.countReqRutLoc != this.phanBoGiaiThuong[i]) continue;
                        indexPirze = i;
                        break;
                    }
                    if (0 <= indexPirze && indexPirze < this.rutLocPrizes.length) {
                        prize = this.rutLocPrizes[indexPirze];
                    }
                    if ((long) prize > this.fundRutLoc) {
                        prize = 0;
                    }
                    if (prize > 0 && (response = userService.updateMoney(user.getName(), (long) prize, "vin", "TaiXiu", "Tài xỉu - Rút lộc", "C\u00e1\u00bb\u2122ng ti\u00e1\u00bb\ufffdn r\u00c3\u00bat l\u00e1\u00bb\u2122c", 0L, null, TransType.NO_VIPPOINT)) != null && response.isSuccess()) {
                        currentMoney = response.getCurrentMoney();
                    }
                    ++this.countReqRutLoc;
                    --soLuotRut;
                    msg.prize = prize;
                    try {
                        this.txService.updateLuotRutLoc(user.getName(), -1);
                        UpdateRutLocMsg soLuotRutMsg = new UpdateRutLocMsg();
                        soLuotRutMsg.soLuotRut = soLuotRut;
                        this.send((BaseMsg) soLuotRutMsg, user);
                    } catch (IOException | InterruptedException | TimeoutException e) {
                        sendLogToTele(e.getMessage());
                        Debug.trace((Object[]) new Object[]{"Update luot rut loc error ", e.getMessage()});
                    }
                } else {
                    msg.prize = -2;
                }
            } else {
                msg.prize = -1;
            }
        } else {
            msg.prize = -3;
        }
        msg.currentMoney = currentMoney;
        this.send((BaseMsg) msg, user);
        if (msg.prize > 0) {
            this.updateFundRutLoc(-msg.prize);
            try {
                this.txService.logRutLoc(user.getName(), (long) msg.prize, this.countReqRutLoc, this.fundRutLoc);
            } catch (IOException | InterruptedException | TimeoutException e) {
                sendLogToTele(e.getMessage());
                Debug.trace((Object[]) new Object[]{"Log rut loc error ", e.getMessage()});
            }
        }
    }

    private boolean updateFundRutLoc(long moneyExchagne) {
        boolean success = false;
        this.fundRutLoc += moneyExchagne;
        if (this.fundRutLoc < 0L) {
            Debug.trace((Object) ("Quy rut loc " + this.fundRutLoc + " < 0"));
        }
        try {
            this.txService.updatePotTanLoc(this.fundRutLoc);
            UpdateFundTanLocMsg msg = new UpdateFundTanLocMsg();
            msg.value = this.fundRutLoc;
            this.sendMessageToTaiXiu(msg);
            success = true;
        } catch (Exception e) {
            sendLogToTele(e.getMessage());
            Debug.trace((Object[]) new Object[]{"Update fund tan loc error ", e.getMessage()});
        }
        return success;
    }

    private short getRemainTimeRutLoc() {
        if (this.countRutLoc == -1) {
            return 0;
        }
        int remainTime = 900 - this.countRutLoc;
        if (remainTime < 0) {
            remainTime = 0;
        }
        return (short) remainTime;
    }

    private void sendMessageToTaiXiuNewThread(BaseMsg msg) {
        SendMessageToTXThread t = new SendMessageToTXThread(false, msg);
        this.executor.execute(t);
    }

    // todo : gửi thời gian đặt tài xỉu con lại , và được phép đặt cược hay không
    private void sendTXTime(short remainTime, boolean betting) {
        BroadcastTXTimeMsg msg = new BroadcastTXTimeMsg();
        msg.remainTime = (byte) remainTime;
        msg.betting = betting;
        SendMessageToTXThread t = new SendMessageToTXThread(true, msg);
        this.executor.execute(t);
    }

    // todo : gửi đến tất cả mọi user trong game
    private void sendMessageToAllUsers(BaseMsg msg) {
        List users = ExtensionUtility.globalUserManager.getAllUsers();
        if (users != null) {
            this.send(msg, users);
        }
    }

    private void sendMessageToTaiXiu(BaseMsg msg) {
        MGRoomTaiXiu roomTXVin = this.getRoomTX((short) 1);
        // todo : gửi message đến room
        roomTXVin.sendMessageToRoom(msg);
        MGRoomTaiXiu roomTXXu = this.getRoomTX((short) 0);
        // todo : gửi message đến room xu ( CÁI NÀY KO DÙNG TRONG GAME HIỆN TẠI)
        roomTXXu.sendMessageToRoom(msg);
    }

    public MGRoom getGame(String key) {
        return this.rooms.get(key);
    }

    public MGRoomTaiXiu getRoomTX(short moneyType) {
        String keyRoom = MGRoomTaiXiu.getKeyRoom(moneyType);
        return (MGRoomTaiXiu) this.getGame(keyRoom);
    }

    // todo : thread game loop
    private final class GameLoopTask
            implements Runnable {
        private GameLoopTask() {
        }

        @Override
        public void run() {
            try {
                TaiXiuModule.this.gameLoop();
            } catch (Exception e) {
                sendLogToTele(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private final class ServerReadyTask
            implements Runnable {
        private ServerReadyTask() {
        }

        @Override
        public void run() {
            if (!TaiXiuModule.this.serverReady) {
                Debug.trace((Object) "START MINI GAME");
                TaiXiuModule.this.serverReady = true;
                ScheduleBotTask t = new ScheduleBotTask();
                TaiXiuModule.this.executor.execute(t);
                TaiXiuModule.this.startNewRoundTX();
            }
        }
    }

    private final class CalculatingTaiXiuPrize implements Runnable {
        private short roomId;

        public CalculatingTaiXiuPrize(short roomId) {
            this.roomId = roomId;
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            try {
                MGRoomTaiXiu room = TaiXiuModule.this.getRoomTX(this.roomId);
                room.calculatePrize(TaiXiuModule.this.referenceTaiXiuId);
            } catch (Exception e) {
                sendLogToTele(e.getMessage() + "Calculate TX " + this.roomId + ", phien= " + TaiXiuModule.this.referenceTaiXiuId + " error: ");
                Debug.trace((Object) ("Calculate TX " + this.roomId + ", phien= " + TaiXiuModule.this.referenceTaiXiuId + " error: " + e.getMessage()));
            }
            long endTime = System.currentTimeMillis();
            Debug.trace((Object) ("CALCUALTE PRIZE, time handle= " + (endTime - startTime) + " (ms)") + " Room " + (roomId == 1 ? "vin" : "xu"));
            TaiXiuModule.this.txService.updateAllTop();
        }
    }

    private final class UpdateCacheTopDay
            implements Runnable {
        public UpdateCacheTopDay() {
        }

        @Override
        public void run() {
            TaiXiuModule.this.txService.updateAllTopDay();
        }
    }

    private final class UpdateCacheTopMonth
            implements Runnable {
        public UpdateCacheTopMonth() {
        }

        @Override
        public void run() {
            TaiXiuModule.this.txService.updateAllTopMonth();
        }
    }

    private final class RewardThanhDuDaily
            implements Runnable {
        private RewardThanhDuDaily() {
        }

        @Override
        public void run() {
            //       TaiXiuUtils.rewardThanhDu();
            //BitZeroServer.getInstance().getTaskScheduler().schedule(TaiXiuModule.this.rewardThanhDuDailyTask, 24, TimeUnit.HOURS);
            Debug.trace((Object) "Tra thuong Thanh Du");
        }
    }

    // todo : thread gửi message tài xìu tời client
    private final class SendMessageToTXThread
            extends Thread {
        private BaseMsg msg;
        private boolean all;

        private SendMessageToTXThread(boolean all, BaseMsg msg) {
            this.msg = msg;
            this.all = all;
        }

        @Override
        public void run() {
            Debug.trace("Call SendMessageToTXThread " + this.all);
            if (this.all) {
                TaiXiuModule.this.sendMessageToAllUsers(this.msg);
            } else {
                TaiXiuModule.this.sendMessageToTaiXiu(this.msg);
            }
        }
    }

    private final class ScheduleBotTask
            extends Thread {
        private ScheduleBotTask() {
        }

        @Override
        public void run() {
            try {
                Debug.trace("Schedule bot running ...");
                TaiXiuModule.this.scheduleBot();
                Debug.trace("Schedule bot finished ...");
            } catch (Exception ex) {
                sendLogToTele(ex.getMessage());
                Debug.trace(ex.getMessage());
            }
        }
    }

    private final class ScheduleBotChatTask
            extends Thread {
        private ScheduleBotChatTask() {
        }

        @Override
        public void run() {
            try {
                Debug.trace("Schedule bot chat running ...");
                TaiXiuModule.this.scheduleBotChat();
                Debug.trace("Schedule bot chat finished ...");
            } catch (Exception ex) {
                sendLogToTele(ex.getMessage());
                Debug.trace(ex.getMessage());
            }
        }
    }

    private void scheduleBotChat() { // todo:fake chat user
        try {
            Random rand = new Random();
            while (true) if (listChatUsers.size() > 0) {
                int sleep = rand.nextInt(2);
                Thread.sleep(14000 + sleep * 1000);
                MGRoomTaiXiu roomTXVin = this.getRoomTX((short) 1);
                ChatMsg msg = new ChatMsg();
                String user = listChatUsers.get(rand.nextInt(listChatUsers.size()));
                msg.nickname = user;
                String randMessage = listChat.get(rand.nextInt(listChat.size()));
                msg.mesasge = randMessage;
                roomTXVin.sendMessageToRoom(msg);
            } else {
                int sleep = rand.nextInt(5000);
                Thread.sleep(sleep * 1000);
            }
        } catch (Exception e) {
            System.out.println("exception scheduleBotChat " + e);
            sendLogToTele(e.getMessage());
            Debug.trace(e.getMessage());
            // GameUtils.sendAlert("Bot tai xiu start error: " + e.getMessage() + ", time= " + DateTimeUtils.getCurrentTime());
        }
    }


    /**
     * Schedule lấy message admin send
     */

    public void sendLogToTele(String log) {
//        logger.error(log+" vnxx");
//        new Thread(() -> {
//            try {
//                String messageEncode = URLEncoder.encode(log);
//                OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
//                        .build();
//                Request request = new Request.Builder()
//                        .url("https://api.telegram.org/bot5158664131:AAFSQZ_VCMGdpDYl34gqaXXwWDecwel-1xM/sendMessage?chat_id=-610762842&text=xxxx"+messageEncode)
//                        .method("GET", null)
//                        .build();
//                Response response = client.newCall(request).execute();
//                String data = response.body().string();
//            }catch (Exception exception) {
//                logger.error(log+" vnxx");
//                exception.printStackTrace();
//            }
//        }).start();

    }
}



