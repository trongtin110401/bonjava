
package game.modules.minigame;

import bitzero.server.BitZeroServer;
import bitzero.server.core.BZEventParam;
import bitzero.server.core.BZEventType;
import bitzero.server.core.IBZEvent;
import bitzero.server.entities.User;
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
import com.vinplay.dal.service.impl.TaiXiuKubetServiceImpl;
import com.vinplay.miniGame.TaiXiuAdminReportObj;
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.exceptions.KeyNotFoundException;
import com.vinplay.vbee.common.response.minigame.TaiXiuChatMsg;
import com.vinplay.vbee.common.utils.DateTimeUtils;
import game.modules.minigame.cmd.rev.BetTaiXiuCmd;
import game.modules.minigame.cmd.rev.ChangeRoomMinigameCmd;
import game.modules.minigame.cmd.rev.SubcribeMinigameCmd;
import game.modules.minigame.cmd.rev.UnsubscribeMiniGameCmd;
import game.modules.minigame.cmd.send.BroadcastTXTimeMsg;
import game.modules.minigame.cmd.send.LichSuPhienMsg;
import game.modules.minigame.cmd.send.StartNewGameTaiXiuMsg;
import game.modules.minigame.entities.BotMinigame;
import game.modules.minigame.entities.BotTaiXiu;
import game.modules.minigame.game79.DynamicReconnectWebSocketClient;
import game.modules.minigame.game79.DynamicXocDia88ReconnectWebSocketClient;
import game.modules.minigame.room.MGRoom;
import game.modules.minigame.room.MGRoomTaiXiu;
import game.modules.minigame.utils.GenerationTaiXiu;
import game.modules.minigame.utils.TaiXiuUtils;
import game.utils.GameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TaiXiuModule extends BaseClientRequestHandler {
    private final Map<String, MGRoom> rooms = new HashMap<>();
    private final Runnable gameLoopTask = new GameLoopTask();  // thread game loop
    private final Runnable serverReadyTask = new ServerReadyTask(); // thread
    private final Runnable calculatingTXVinTask = new CalculatingTaiXiuPrize((short) 1);  // thread tính tài xỉu vin
    private final CacheService cacheService = new CacheServiceImpl(); // caching hazelcast service

    public int count = 0;
    private boolean serverReady = false;
    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);  // thread pool 10 cái thread
    private long referenceTaiXiuId; // được lấy từ trong database
    private final TaiXiuService txService = new TaiXiuKubetServiceImpl();
    private final MiniGameService mgService = new MiniGameServiceImpl();
    private List<ResultTaiXiu> lichSuPhienTX = new ArrayList<>();
    private final GenerationTaiXiu generationTX = new GenerationTaiXiu();
    private short result = (short) -1;
    private long fundRutLoc = 0L;
    private int countRutLoc = 0;
    private List<BotTaiXiu> botsVin = new ArrayList<>();
    private final List<String> listChatUsers = new ArrayList<>();
    public static String CacheCurrentReference = "Tai_xiu_current_reference_kubet";
    public static long moneyHu = 50000000;
    private int amountBotTaiFake = 0;
    private int amountBotXiuFake = 0;
    private int amountBotChanFake = 0;
    private int amountBotLeFake = 0;

    public void init() {
        try {
            TaiXiuChatMsg taiXiuChatMsg = new TaiXiuChatMsg();
            cacheService.setObject("admin_lst_msg_kubet", taiXiuChatMsg);
            cacheService.setObject("admin_msg_kubet", taiXiuChatMsg);
            Debug.info("referentTaiXiuId là " + this.referenceTaiXiuId);
            this.rooms.put(MGRoomTaiXiu.getKeyRoom((short) 1), new MGRoomTaiXiu("TaiXiu_1", this.referenceTaiXiuId, (byte) 1, this));

            this.loadData();

            Debug.info("referentTaiXiuId sau khi load data là " + this.referenceTaiXiuId);
            BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.gameLoopTask, 10, 1, TimeUnit.SECONDS);
            BitZeroServer.getInstance().getTaskScheduler().schedule(this.serverReadyTask, 10, TimeUnit.SECONDS);
            Debug.trace("SERVER READY TASK RUNNING...");
            this.getParentExtension().addEventListener(BZEventType.USER_DISCONNECT, this);

            // websocket ket qua kubet
            URI uri = DynamicXocDia88ReconnectWebSocketClient.buildDynamicURI();
            DynamicXocDia88ReconnectWebSocketClient.currentClient = new DynamicXocDia88ReconnectWebSocketClient(this, uri);
            DynamicXocDia88ReconnectWebSocketClient.currentClient.connect();

            // websocket ket qua kubet
//            URI uri = DynamicReconnectWebSocketClient.buildDynamicURI();
//            DynamicReconnectWebSocketClient.currentClient = new DynamicReconnectWebSocketClient(this, uri);
//            DynamicReconnectWebSocketClient.currentClient.connect();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    public void handleServerEvent(IBZEvent ibzevent) {
        if (ibzevent.getType() == BZEventType.USER_DISCONNECT) {
            User user = (User) ibzevent.getParameter(BZEventParam.USER);
            this.userDis(user);
        }
    }

    // user out room todo: user rời khỏi phòng
    private void userDis(User user) {
        MGRoom room = (MGRoom) user.getProperty("MGROOM_TAI_XIU_INFO");  // lấy ra object room tài xỉu info trong map thuộc tính
        if (room != null) {
            room.quitRoom(user); // management room quit user xóa user khỏi list user trong phòng
        }
    }

    //todo : load referenceId và tài xỉu phiên từ trong database
    private void loadData() {
        this.referenceTaiXiuId = 1L;
        try {
            this.referenceTaiXiuId = this.mgService.getReferenceId(Games.TAI_XIU_KUBET.getId());
            this.lichSuPhienTX = this.txService.getListLichSuPhien(100, 1);
        } catch (SQLException e) {
            Debug.trace("Load reference error ", e.getMessage());
        }
        try {
            this.generationTX.readConfig();
        } catch (IOException e) {
            Debug.trace("Load cau tai xiu error ", e.getMessage());
        }
        this.fundRutLoc = 0;
    }

    private void saveReferences() {
        try {
            this.mgService.saveReferenceId(this.referenceTaiXiuId, Games.TAI_XIU_KUBET.getId());
        } catch (SQLException e) {
            Debug.trace("Save reference error " + e.getMessage());
        }
    }

    // todo : handle dựa trên data cmd
    public void handleClientRequest(User user, DataCmd dataCmd) {
        switch (dataCmd.getId()) {
            case 2000: {
                // mở game lên vào đây
                this.subscribeMiniGame(user, dataCmd);
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

    private void subscribeMiniGame(User user, DataCmd dataCmd) {
        SubcribeMinigameCmd cmd = new SubcribeMinigameCmd(dataCmd);

        this.doSubscribeMiniGame(user, cmd.gameId, cmd.roomId);

        LichSuPhienMsg msgLSGD = new LichSuPhienMsg();
        msgLSGD.data = TaiXiuUtils.buildLichSuPhien(this.lichSuPhienTX, 100);
        this.send(msgLSGD, user);
    }

    // vào room
    private void doSubscribeMiniGame(User user, short gameId, short roomId) {
        if (gameId == 2) {
            short moneyType = MGRoomTaiXiu.getMoneyType(roomId);
            String keyRoom = MGRoomTaiXiu.getKeyRoom(moneyType);
            MGRoomTaiXiu roomTX = (MGRoomTaiXiu) this.getGame(keyRoom);
            if (roomTX != null) {
                // room tài xỉu add user vào room
                roomTX.joinRoom(user);
                roomTX.updateTaiXiuInfo(user, this.getRemainTimeRutLoc());
                return;
            }
            CommonHandle.writeErrLog("Game TAI XIU not found");
        } else {
            Debug.trace("Game id not found");
        }
    }

    private void unsubscribeMiniGame(User user, DataCmd dataCmd) {
        UnsubscribeMiniGameCmd cmd = new UnsubscribeMiniGameCmd(dataCmd);
        this.doUnsubscribeMiniGame(user, cmd.gameId, cmd.roomId);
    }

    // todo : rời khỏi room
    private void doUnsubscribeMiniGame(User user, short gameId, short roomId) {
        if (gameId == 2) {
            short moneyType = MGRoomTaiXiu.getMoneyType(roomId);
            String keyRoom = MGRoomTaiXiu.getKeyRoom(moneyType);
            MGRoom room = this.getGame(keyRoom);
            if (room == null) return;
            room.quitRoom(user);
        }
    }

    private void changeRoom(User user, DataCmd dataCmd) {
        ChangeRoomMinigameCmd cmd = new ChangeRoomMinigameCmd(dataCmd);
        this.doUnsubscribeMiniGame(user, cmd.gameId, cmd.lastRoomId);
        this.doSubscribeMiniGame(user, cmd.gameId, cmd.newRoomId);
    }

    //todo : bắt đầu một round tài xỉu mới
    private void startNewRoundTX() {
        MGRoomTaiXiu roomTXVin = this.getRoomTX((short) 1);
        ++this.referenceTaiXiuId;

        //update referenceId
        roomTXVin.startNewGame(this.referenceTaiXiuId);
        StartNewGameTaiXiuMsg msg = new StartNewGameTaiXiuMsg();
        msg.referenceId = this.kubetSessionId;
        msg.moneyHu = moneyHu;
        msg.startSessionTime = this.startSessionTime;

        this.sendMessageToTaiXiuNewThread(msg);

        this.saveReferences();

        this.cacheService.setValue("kubet_allow_betting_" + this.referenceTaiXiuId, 1);
        this.cacheService.setValue(CacheCurrentReference, Long.toString(this.referenceTaiXiuId));
    }

    //todo : Schedule Bot tai xiu
    private void scheduleBot() {
        try {
            this.botsVin.clear();
            this.botsVin = BotMinigame.getBotTaiXiu("vin");
            Debug.trace("BOTS VIN: " + this.botsVin.size());
        } catch (Exception e) {
            GameUtils.sendAlert("Bot tai xiu start error: " + e.getMessage() + ", time= " + DateTimeUtils.getCurrentTime());
        }
    }

    private void botBet(int count) {
        MGRoomTaiXiu roomVin = this.getRoomTX((short) 1);
        String[] strs = {"Jocelyn", "Kelsey", "Fallon", "Maynard", "Mildred", "Aubrey"};
        for (BotTaiXiu b : this.botsVin) {
            if (b.getTimeBetting() < 4 || b.getTimeBetting() > 25 || b.getTimeBetting() != count) continue;
            if (!ArrayUtils.contains(strs, b.getNickname())) {
                roomVin.betTaiXiu(b.getNickname(), 0, b.getBetValue(), b.getTimeBetting(), (short) 1, b.getBetSide(), true, 0);
            }
        }
    }

    // todo : user đặt cược tiền
    public void betTaiXiu(User user, DataCmd dataCmd) {
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
        TaiXiuAdminReportObj taiXiuAdminReportObj = new TaiXiuAdminReportObj(
                roomVin.getUserBetTai(), this.getRoomTX(typeBet).getUserBetXiu(), this.getRoomTX(typeBet).getUserBetChan(), this.getRoomTX(typeBet).getUserBetLe(),
                roomVin.getNumberUserRealTai(), this.getRoomTX(typeBet).getNumberUserRealXiu(), this.getRoomTX(typeBet).getNumberUserRealChan(), this.getRoomTX(typeBet).getNumberUserRealLe(),
                this.getRoomTX(typeBet).getTotalMoneyTai(), roomVin.getTotalMoneyXiu(), this.getRoomTX(typeBet).getTotalMoneyChan(), roomVin.getTotalMoneyLe(),
                this.referenceTaiXiuId);
        taiXiuAdminReportObj.setNumberUserAndBotBetTai(roomVin.getNumberUserAndBotTai());
        taiXiuAdminReportObj.setNumberUserAndBotBetXiu(roomVin.getNumberUserAndBotXiu());
        taiXiuAdminReportObj.setNumberUserAndBotBetChan(roomVin.getNumberUserAndBotChan());
        taiXiuAdminReportObj.setNumberUserAndBotBetLe(roomVin.getNumberUserAndBotLe());

        taiXiuAdminReportObj.setContributors(roomVin.getListRealTransaction());
        taiXiuAdminReportObj.setRealTime(roomVin.getRemainTime());
        taiXiuAdminReportObj.setBettingRound(roomVin.bettingRound);
        List<TaiXiuChatMsg> listChat;
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
        cacheService.setValue("user_tai_xiu_kubet", taiXiuAdminReportObj.toJson());
        cacheService.setObject("lstTaiXiuAdminMsg", new ArrayList<>());

        // thông tin soi cầu
        String sc = lichSuPhienTX.stream()
                .skip(Math.max(0, lichSuPhienTX.size() - 25))
                .map(resultTaiXiu -> {
                    String tx = resultTaiXiu.dice1 + resultTaiXiu.dice2 + resultTaiXiu.dice3 > 10 ? "T" : "X";
                    String cl = (resultTaiXiu.dice1 + resultTaiXiu.dice2 + resultTaiXiu.dice3) % 2 == 0 ? "C" : "L";
                    return tx + cl;
                })
                .collect(Collectors.joining(","));
        cacheService.setValue("SC_TAI_XIU_KUBET", sc);
    }

    @SuppressWarnings("Unchecked")
    public List<String> getListChatUsers() {
        return listChatUsers;
    }

    public static final int TOTAL_BETTING_TIME = 21;

    public synchronized void gameLoop() {
        try {
            MGRoomTaiXiu roomTXVin = this.getRoomTX((short) 1);
            this.botBet(count);
            roomTXVin.updateTaiXiuPerSecond(amountBotTaiFake, amountBotXiuFake, amountBotChanFake, amountBotLeFake);

            // lưu toàn bộ trạng thái của game vào cache service
            // trạng thái này phục vụ cho APIs và module wsreport
            this.getUserPotTaiXiu();
            this.sendTXTime(roomTXVin.getRemainTime(), roomTXVin.isBetting()); // todo tinh thoi gian con lai
        } catch (Exception e) {
            Debug.trace("Exception: " + e.getMessage(), e);
            ExceptionUtils.printRootCauseStackTrace(e);
        }
    }

    public TxKubetState CURRENT_GAME_STATE = TxKubetState.INIT;
    public long kubetSessionId = 0;

    public String startSessionTime = "";

    public synchronized void handleGameState(long kubetSessionId, TxKubetState state, int count, int dice1, int dice2, int dice3) {
        try {
            if (CURRENT_GAME_STATE == TxKubetState.INIT && state != TxKubetState.GENERATE_RESULT && state != TxKubetState.BETTING) {
                System.out.println("Waiting for old session to end to starting new session...." + count);
                return;
            }

            this.kubetSessionId = referenceTaiXiuId;
            if (count == 1 && (this.count == 1 || this.count == 0)) {
                this.count = 0;
            } else {
                this.count = count;
            }

            MGRoomTaiXiu roomTXVin = this.getRoomTX((short) 1);
            switch (state) {
                case GENERATE_RESULT:
                    if (CURRENT_GAME_STATE == state) {
                        return;
                    }
                    CURRENT_GAME_STATE = state;
                    System.out.println("GENERATE_RESULT: " + this.count);
                    startSessionTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy"));
                    this.startNewRoundTX();
                    amountBotTaiFake = 0;
                    amountBotXiuFake = 0;
                    roomTXVin.resultTX = null;
                    break;
                case BETTING:
                    CURRENT_GAME_STATE = state;
                    if (!roomTXVin.isBetting()) {
                        startSessionTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy"));
                        this.startNewRoundTX();
                        amountBotTaiFake = 0;
                        amountBotXiuFake = 0;
                        roomTXVin.resultTX = null;
                    }
                    System.out.println("BETTING: " + this.count);
                    break;
                case SHOW_RESULT:
                    CURRENT_GAME_STATE = state;
                    System.out.println("SHOW_RESULT: " + this.count);
                    roomTXVin.disableBetting();
                    roomTXVin.finish();
                    break;
                case CONFIRM_RESULT:
                    if (CURRENT_GAME_STATE == state) {
                        return;
                    }
                    CURRENT_GAME_STATE = state;
                    System.out.println("CONFIRM_RESULT: " + this.count);
                    this.generateResult(dice1, dice2, dice3);
                    BitZeroServer.getInstance().getTaskScheduler().schedule(this.calculatingTXVinTask, 1, TimeUnit.SECONDS);
                    amountBotTaiFake = 0;
                    amountBotXiuFake = 0;

                    ScheduleBotTask t = new ScheduleBotTask();
                    this.executor.execute(t);
                    break;
            }
        } catch (Exception e) {
            Debug.trace("Exception: " + e.getMessage(), e);
            ExceptionUtils.printRootCauseStackTrace(e);
        }
    }


    public void generateResult(int dice1, int dice2, int dice3) {

        short[] dices = new short[3];
        dices[0] = (short) dice1;
        dices[1] = (short) dice2;
        dices[2] = (short) dice3;

        short total = (short) (dices[0] + dices[1] + dices[2]);
        this.result = total > 10 ? (short) 1 : 0;

        // Show ket qua ra man
        MGRoomTaiXiu roomTXVin = getRoomTX((short) 1);
        roomTXVin.updateResultDices(dices, this.result);
        ResultTaiXiu resultTX = roomTXVin.resultTX;
        resultTX.referenceId = this.referenceTaiXiuId;
        resultTX.kubetSessionId = kubetSessionId;
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
        this.send(msg, user);
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
        List<User> users = ExtensionUtility.globalUserManager.getAllUsers();
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
    private final class GameLoopTask implements Runnable {
        private GameLoopTask() {
        }

        @Override
        public void run() {
            try {
                gameLoop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private final class ServerReadyTask implements Runnable {
        private ServerReadyTask() {
        }

        @Override
        public void run() {
            if (!TaiXiuModule.this.serverReady) {
                Debug.trace("START MINI GAME");
                TaiXiuModule.this.serverReady = true;
                ScheduleBotTask t = new ScheduleBotTask();
                TaiXiuModule.this.executor.execute(t);
                TaiXiuModule.this.startNewRoundTX();
            }
        }
    }

    private final class CalculatingTaiXiuPrize implements Runnable {
        private final short roomId;

        public CalculatingTaiXiuPrize(short roomId) {
            this.roomId = roomId;
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            try {
                TaiXiuModule.this.getRoomTX(this.roomId).calculatePrize(referenceTaiXiuId);
            } catch (Exception e) {
                Debug.trace("Calculate TX " + this.roomId + ", phien= " + TaiXiuModule.this.referenceTaiXiuId + " error: " + e.getMessage());
            } finally {
                long endTime = System.currentTimeMillis();
                Debug.trace("CALCUALTE PRIZE, time handle= " + (endTime - startTime) + " (ms)" + " Room " + (roomId == 1 ? "vin" : "xu"));
            }
        }
    }

    // todo : thread gửi message tài xìu tời client
    private final class SendMessageToTXThread extends Thread {
        private final BaseMsg msg;
        private final boolean all;

        private SendMessageToTXThread(boolean all, BaseMsg msg) {
            this.msg = msg;
            this.all = all;
        }

        @Override
        public void run() {
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
                Debug.trace(ex.getMessage());
            }
        }
    }
}



