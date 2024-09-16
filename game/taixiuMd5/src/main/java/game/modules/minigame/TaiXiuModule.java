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
import com.vinplay.dal.entities.taixiu.ResultTaiXiu;
import com.vinplay.dal.entities.taixiu.ResultTaiXiuMd5;
import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.MiniGameService;
import com.vinplay.dal.service.TaiXiuService;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.dal.service.impl.MiniGameServiceImpl;
import com.vinplay.dal.service.impl.TaiXiuMd5ServiceImpl;
import com.vinplay.miniGame.TaiXiuAdminReportObj;
import com.vinplay.miniGame.TaiXiuSetAmountBotFake;
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.exceptions.KeyNotFoundException;
import com.vinplay.vbee.common.response.minigame.TaiXiuAdmin;
import com.vinplay.vbee.common.response.minigame.TaiXiuChatMsg;
import com.vinplay.vbee.common.utils.DateTimeUtils;
import game.modules.minigame.cmd.rev.BetTaiXiuCmd;
import game.modules.minigame.cmd.rev.ChangeRoomMinigameCmd;
import game.modules.minigame.cmd.rev.SubcribeMinigameCmd;
import game.modules.minigame.cmd.rev.UnsubscribeMiniGameCmd;
import game.modules.minigame.cmd.send.*;
import game.modules.minigame.entities.BotMinigame;
import game.modules.minigame.entities.BotTaiXiu;
import game.modules.minigame.room.MGRoom;
import game.modules.minigame.room.MGRoomTaiXiu;
import game.modules.minigame.utils.GenerationTaiXiu;
import game.modules.minigame.utils.TaiXiuUtils;
import game.utils.GameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TaiXiuModule extends BaseClientRequestHandler {
    private Map<String, MGRoom> rooms = new HashMap<String, MGRoom>();
    private final Runnable gameLoopTask = new GameLoopTask();  // thread game loop
    private final Runnable serverReadyTask = new ServerReadyTask(); // thread
    private final Runnable calculatingTXVinTask = new CalculatingTaiXiuPrize((short) 1);  // thread tính tài xỉu vin
    private final Runnable updateCacheTopDay = new UpdateCacheTopDay();  // thread tính tài xỉu top theo ngay
    private final Runnable updateCacheTopMonth = new UpdateCacheTopMonth();  // thread tính tài xỉu theo thang
    private final Runnable calculatingTXXuTask = new CalculatingTaiXiuPrize((short) 0);  // thread tính tài xỉu xu
    private final CacheService cacheService = new CacheServiceImpl(); // caching hazelcast service
    public int count = 0;
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
    private List<BotTaiXiu> botsVin = new ArrayList<BotTaiXiu>();
    private List<BotTaiXiu> botsXu = new ArrayList<BotTaiXiu>();
    private short forceBetSide = (short) -1;
    private List<String> listChatUsers = new ArrayList<String>();
    public static String CacheCurrentReference = "Tai_xiu_current_reference_md5";
    public static long moneyHu = 50000000; // được lấy từ trong database

    private int amountBotTaiFake = 0;
    private int amountBotXiuFake = 0;

    protected MiniGameService miniGameService = new MiniGameServiceImpl();

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(8);

    public void init() {
        try {
            long fundTxMD5 = miniGameService.getFund(Games.TAI_XIU_MD5.getName());
            cacheService.setValue(Games.TAI_XIU_MD5.getName(), fundTxMD5);
        } catch (Exception ignored) {
        }

        TaiXiuChatMsg taiXiuChatMsg = new TaiXiuChatMsg();
        cacheService.setObject("admin_lst_msg_md5", taiXiuChatMsg);
        cacheService.setObject("admin_msg_md5", taiXiuChatMsg);
        Debug.info("referentTaiXiuId là " + this.referenceTaiXiuId);
        this.rooms.put(MGRoomTaiXiu.getKeyRoom((short) 1), new MGRoomTaiXiu("TaiXiu_1", this.referenceTaiXiuId, (byte) 1, this));

        this.loadData();

        Debug.info("referentTaiXiuId sau khi load data là " + this.referenceTaiXiuId);
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.gameLoopTask, 10, 1, TimeUnit.SECONDS);
        BitZeroServer.getInstance().getTaskScheduler().schedule(this.serverReadyTask, 10, TimeUnit.SECONDS);
        Debug.trace("SERVER READY TASK RUNNING...");
        this.getParentExtension().addEventListener((IBZEventType) BZEventType.USER_DISCONNECT, (IBZEventListener) this);

//        scheduler.scheduleAtFixedRate(updateCacheTopDay, 2000, 3600, TimeUnit.SECONDS);
//        scheduler.scheduleAtFixedRate(updateCacheTopMonth, 2000, 86400, TimeUnit.SECONDS);
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
            this.lichSuPhienTX = this.txService.getListLichSuPhien(100, 1);
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
                this.subcribeMiniGame(user, dataCmd);
                break;
            }
            case 2001: {
                this.unsubscribeMiniGame(user, dataCmd);
                break;
            }
            case 2002: {
                this.changeRoom(user, dataCmd);
                break;
            }
            case 2110: {
                if (GameUtils.disablePlayMiniGame(user)) {
                    return;
                }
                this.betTaiXiu(user, dataCmd);  // nếu id là 2110 thì vào đánh bài
                break;
            }
            case 2116: {
                this.getLichSuPhienTX(user);
                break;
            }
            case 2118:
            case 2119: {
                break;
            }
        }
    }

    private void subcribeMiniGame(User user, DataCmd dataCmd) {
        SubcribeMinigameCmd cmd = new SubcribeMinigameCmd(dataCmd);
        this.doSubcribeMiniGame(user, cmd.gameId, cmd.roomId);
        LichSuPhienMsg msgLSGD = new LichSuPhienMsg();
        msgLSGD.data = TaiXiuUtils.buildLichSuPhien(this.lichSuPhienTX, 100);
        this.send(msgLSGD, user);
        UpdateRutLocMsg rutLocMsg = new UpdateRutLocMsg();
        try {
            rutLocMsg.soLuotRut = this.txService.getLuotRutLoc(user.getName());
        } catch (Exception e) {
            sendLogToTele(e.getMessage());
            Debug.trace(new Object[]{"Get so luot rut loc " + user.getName() + " error ", e.getMessage()});
        }
        this.send(rutLocMsg, user);
        UpdateFundTanLocMsg fundRLMsg = new UpdateFundTanLocMsg();
        fundRLMsg.value = this.fundRutLoc;
        this.send(fundRLMsg, user);
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
                CommonHandle.writeErrLog("Game TAI XIU not found");
                break;
            }
            default: {
                Debug.trace("Game id not found");
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
        ++this.referenceTaiXiuId;

        //update referenceId
        roomTXVin.startNewGame(this.referenceTaiXiuId);
        StartNewGameTaiXiuMsg msg = new StartNewGameTaiXiuMsg();
        msg.referenceId = this.referenceTaiXiuId;
        msg.moneyHu = moneyHu;

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
        } catch (Exception e) {
            sendLogToTele(e.getMessage());
            GameUtils.sendAlert("Bot tai xiu start error: " + e.getMessage() + ", time= " + DateTimeUtils.getCurrentTime());
        }
    }

    private void botBet(int count) {
        MGRoomTaiXiu roomVin = this.getRoomTX((short) 1);
        String[] strs = {"Jocelyn", "Kelsey", "Fallon", "Maynard", "Mildred", "Aubrey"};
        for (BotTaiXiu b : this.botsVin) {
            if (b.getTimeBetting() != 50 - count) continue;
            if (!ArrayUtils.contains(strs, b.getNickname())) {
                roomVin.betTaiXiu(b.getNickname(), 0, b.getBetValue(), b.getTimeBetting(), (short) 1, b.getBetSide(), true, 0);
            }
        }
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
        MGRoomTaiXiu roomVin = this.getRoomTX(typeBet);
        // lấy người chơi đặt tài
        TaiXiuAdminReportObj taiXiuAdminReportObj = new TaiXiuAdminReportObj(roomVin.getUserBetTai(),
                this.getRoomTX(typeBet).getUserBetXiu(), roomVin.getNumberUserRealTai(), this.getRoomTX(typeBet).getNumberUserRealXiu(),
                this.getRoomTX(typeBet).getTotalMoneyTai(), roomVin.getTotalMoneyXiu(), this.referenceTaiXiuId);
        taiXiuAdminReportObj.setNumberUserAndBotBetTai(roomVin.getNumberUerAndBotTai());
        taiXiuAdminReportObj.setNumberUserAndBotBetXiu(roomVin.getNumberUerAndBotXiu());
        taiXiuAdminReportObj.setContributors(roomVin.getListTransaction());
        taiXiuAdminReportObj.setRealTime(roomVin.getRemainTime());
        taiXiuAdminReportObj.setBettingRound(roomVin.bettingRound);
        List<TaiXiuChatMsg> listChat = new ArrayList<>();
        try {
            listChat = (List<TaiXiuChatMsg>) cacheService.getObject("lstTaiXiuAdminMsg");
        } catch (KeyNotFoundException e) {
            listChat = new ArrayList<>();
        }

        if (roomVin.resultTX != null) {
            taiXiuAdminReportObj.setDice1(roomVin.resultTX.dice1);
            taiXiuAdminReportObj.setDice2(roomVin.resultTX.dice2);
            taiXiuAdminReportObj.setDice3(roomVin.resultTX.dice3);
            taiXiuAdminReportObj.setSessionResult(roomVin.resultTX.dice1 + roomVin.resultTX.dice2 + roomVin.resultTX.dice3 > 10 ? "TAI" : "XIU");
        }

        taiXiuAdminReportObj.setLstMsg(listChat);
        taiXiuAdminReportObj.setGetListChatUsers(this.getListChatUsers());
        taiXiuAdminReportObj.setTaiXiuMd5Hash(roomVin.resultTX.getMd5TextResult());
        taiXiuAdminReportObj.setTaiXiuPlainResult(roomVin.resultTX.getPlantTextResult());
        cacheService.setValue("user_tai_xiu_md5", taiXiuAdminReportObj.toJson());
        cacheService.setObject("lstTaiXiuAdminMsg", new ArrayList<>());

        // thông tin soi cầu
        String sc = lichSuPhienTX.stream()
                .skip(Math.max(0, lichSuPhienTX.size() - 25))
                .map(resultTaiXiu -> resultTaiXiu.dice1 + resultTaiXiu.dice2 + resultTaiXiu.dice3 > 10 ? "T" : "X")
                .collect(Collectors.joining(","));
        cacheService.setValue("SC_TAI_XIU_MD5", sc);
    }

    public List<String> getListChatUsers() {
        return listChatUsers;
    }

    private synchronized void gameLoop() {
        try {
            MGRoomTaiXiu roomTXVin = this.getRoomTX((short) 1);
            if (count == 0) {
                this.generateResultBefore(roomTXVin);
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
                amountBotXiuFake = 0;
                amountBotTaiFake = 0;
            }

            roomTXVin.updateTaiXiuPerSecond(amountBotTaiFake, amountBotXiuFake, count);

            // lưu toàn bộ trạng thái của game vào cache service
            // trạng thái này phục vụ cho APIs và module wsreport
            this.getUserPotTaiXiu();
            this.sendTXTime(roomTXVin.getRemainTime(), roomTXVin.isBetting()); // todo tinh thoi gian con lai
            switch (this.count) {
                case 45: {
                    roomTXVin.disableBetting();
                    break;
                }
                case 48: {

                    break;
                }
                case 50: {
                    roomTXVin.finish();
                    this.generateResultAfter(roomTXVin);
                    break;
                }
                case 51: {
                    BitZeroServer.getInstance().getTaskScheduler().schedule(this.calculatingTXVinTask, 1, TimeUnit.SECONDS);
                    amountBotTaiFake = 0;
                    amountBotXiuFake = 0;
                    break;
                }
                case 53: {
                    ScheduleBotTask t = new ScheduleBotTask();
                    this.executor.execute(t);
                    break;
                }
                case 65: {
                    try {
                        this.startNewRoundTX();

                        mgService.saveFund(Games.TAI_XIU_MD5.getName(), getFunValue());


                        amountBotTaiFake = 0;
                        amountBotXiuFake = 0;
                        this.count = 0;
                        roomTXVin.resultTX = null;
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

    private void resetForceBalance() {
        this.forceBetSide = (short) -1;
    }

    private void generateResultBefore(MGRoomTaiXiu roomTXVin) {
        short[] dices;

        dices = this.generationTX.generateDices();
        String result = generationTX.buildPlainTextResult(dices);
        String md5 = GenerationTaiXiu.hashMD5(result);

        ResultTaiXiuMd5 resultTaiXiuMd5 = new ResultTaiXiuMd5();
        resultTaiXiuMd5.dice1 = dices[0];
        resultTaiXiuMd5.dice2 = dices[1];
        resultTaiXiuMd5.dice3 = dices[2];
        resultTaiXiuMd5.setMd5TextResult(md5);
        resultTaiXiuMd5.setPlantTextResult(result);
        roomTXVin.resultTX = resultTaiXiuMd5;

        if (dices[0] + dices[1] + dices[2] > 10) {
            this.result = 1;
        } else {
            this.result = 0;
        }
    }


    private void generateResultAfter(MGRoomTaiXiu roomTXVin) {

        String keyBeCang = "auto";
        try {
            keyBeCang = cacheService.getValueStr("tai_xiu_be_cang_md5");

        } catch (Exception r) {
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
            String result = generationTX.buildPlainTextResult(dices);
            roomTXVin.resultTX.setPlantTextResult(result);
        } else { // khong can thiep

            short typeBet = 1;
            // tinh toan chenh lenh user that
            List<TaiXiuAdmin> contributors = this.getRoomTX(typeBet).getListTransaction();
            long totalRealBetTai = 0;
            long totalRealBetXiu = 0;
            for (TaiXiuAdmin taiXiuAdmin : contributors) {
                if (taiXiuAdmin.getCuaDat() == 0) {
                    //bet xiu
                    totalRealBetXiu += taiXiuAdmin.getMoney();
                } else if (taiXiuAdmin.getCuaDat() == 1) {
                    // bet tai
                    totalRealBetTai += taiXiuAdmin.getMoney();
                }
            }

            // ve tai
            long chenhLech = 0;
            if (this.result == 1) {
                // ve tai
                chenhLech = totalRealBetTai - totalRealBetXiu;
            } else { // ve xiu
                chenhLech = totalRealBetXiu - totalRealBetTai;
            }

            if (chenhLech > 0 && getFunValue() - chenhLech < 0) {
                if (totalRealBetTai > totalRealBetXiu) {
                    this.forceBetSide = 0;
                } else {
                    this.forceBetSide = 1;
                }
                dices = this.generationTX.generateResult(this.forceBetSide);
            }
            // Ngau nhien khong can thiep
            else {
                dices[0] = (short) roomTXVin.resultTX.dice1;
                dices[1] = (short) roomTXVin.resultTX.dice2;
                dices[2] = (short) roomTXVin.resultTX.dice3;
            }

            String result = generationTX.buildPlainTextResult(dices);
            roomTXVin.resultTX.setPlantTextResult(result);
        }

        this.resetForceBalance();

        short total = (short) (dices[0] + dices[1] + dices[2]);
        this.result = total > 10 ? (short) 1 : 0;

        /**
         * Show ket qua ra man
         */
        roomTXVin.updateResultDices(dices, this.result);
        ResultTaiXiuMd5 resultTX = roomTXVin.resultTX;
        resultTX.referenceId = this.referenceTaiXiuId;
        resultTX.result = this.result;
        resultTX.dice1 = dices[0];
        resultTX.dice2 = dices[1];
        resultTX.dice3 = dices[2];
        Debug.trace("GENERATE RESULT DICES: " + dices[0] + " - " + dices[1] + " - " + dices[2] + "   " + this.result);
        this.lichSuPhienTX.add(resultTX);
        if (this.lichSuPhienTX.size() > 100) {
            this.lichSuPhienTX.remove(0);
        }
    }


    private void getLichSuPhienTX(User user) {
        LichSuPhienMsg msg = new LichSuPhienMsg();
        msg.data = TaiXiuUtils.buildLichSuPhien(this.lichSuPhienTX, 100);
//        Debug.trace("LSDG: " + TaiXiuUtils.logLichSuPhien(this.lichSuPhienTX, 120));
        this.send(msg, user);
    }

    private boolean updateFundRutLoc(long moneyExchagne) {
        boolean success = false;
        this.fundRutLoc += moneyExchagne;
        if (this.fundRutLoc < 0L) {
            Debug.trace("Quy rut loc " + this.fundRutLoc + " < 0");
        }
        try {
            this.txService.updatePotTanLoc(this.fundRutLoc);
            UpdateFundTanLocMsg msg = new UpdateFundTanLocMsg();
            msg.value = this.fundRutLoc;
            this.sendMessageToTaiXiu(msg);
            success = true;
        } catch (Exception e) {
            sendLogToTele(e.getMessage());
            Debug.trace("Update fund tan loc error ", e.getMessage());
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
        // todo : gửi message đến room xu ( CÁI NÀY KO DÙNG TRONG GAME HIỆN TẠI)
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
                gameLoop();
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
                TaiXiuModule.this.getRoomTX(this.roomId).calculatePrize(referenceTaiXiuId);
            } catch (Exception e) {
                sendLogToTele(e.getMessage() + "Calculate TX " + this.roomId + ", phien= " + TaiXiuModule.this.referenceTaiXiuId + " error: ");
                Debug.trace("Calculate TX " + this.roomId + ", phien= " + TaiXiuModule.this.referenceTaiXiuId + " error: " + e.getMessage());
            } finally {
                long endTime = System.currentTimeMillis();
                Debug.trace("CALCUALTE PRIZE, time handle= " + (endTime - startTime) + " (ms)" + " Room " + (roomId == 1 ? "vin" : "xu"));
            }
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
//            Debug.trace("Call SendMessageToTXThread " + this.all);
            if (this.all) {
                TaiXiuModule.this.sendMessageToAllUsers(this.msg);
            } else {
                TaiXiuModule.this.sendMessageToTaiXiu(this.msg);
            }
        }
    }

    private final class ScheduleBotTask extends Thread {
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

    /**
     * Schedule lấy message admin send
     */
    public void sendLogToTele(String log) {

    }

    public long getFunValue() {
        String key = Games.TAI_XIU_MD5.getName();
        return cacheService.getValueLong(key, 0);
    }

    public void setFunValue(long value) {
        String key = Games.TAI_XIU_MD5.getName();
        cacheService.setValue(key, value);
    }

    public void updateFunValue(long value) {
        setFunValue(getFunValue() + value);
    }

    public void saveFund() throws Exception {
        miniGameService.saveFund(Games.TAI_XIU_MD5.getName(), getFunValue());
    }
}



