package game.modules.minigame;

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
import bitzero.util.common.business.CommonHandle;
import bitzero.util.common.business.Debug;
import com.vinplay.dal.entities.taixiu.ResultTaiXiu;
import com.vinplay.dal.service.MiniGameService;
import com.vinplay.dal.service.OverUnderService;
import com.vinplay.dal.service.impl.MiniGameServiceImpl;
import com.vinplay.dal.service.impl.OverUnderServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.response.MoneyResponse;
import com.vinplay.vbee.common.statics.TransType;
import com.vinplay.vbee.common.utils.DateTimeUtils;
import game.modules.chat.cmd.send.ChatMsg;
import game.modules.minigame.cmd.rev.BetTaiXiuCmd;
import game.modules.minigame.cmd.rev.ChangeRoomMinigameCmd;
import game.modules.minigame.cmd.rev.SubcribeMinigameCmd;
import game.modules.minigame.cmd.rev.TanLocCMD;
import game.modules.minigame.cmd.rev.UnsubscribeMiniGameCmd;
import game.modules.minigame.cmd.send.BroadcastTXTimeMsg;
import game.modules.minigame.cmd.send.EnableRutLocMsg;
import game.modules.minigame.cmd.send.LichSuPhienMsg;
import game.modules.minigame.cmd.send.ResultRutLocMsg;
import game.modules.minigame.cmd.send.StartNewGameTaiXiuMsg;
import game.modules.minigame.cmd.send.StartNewRoundRutLocMsg;
import game.modules.minigame.cmd.send.TanLocMsg;
import game.modules.minigame.cmd.send.UpdateFundTanLocMsg;
import game.modules.minigame.cmd.send.UpdateRutLocMsg;
import game.modules.minigame.entities.BotMinigame;
import game.modules.minigame.entities.BotTaiXiu;
import game.modules.minigame.room.MGRoom;
import game.modules.minigame.room.MGRoomTaiXiu;
import game.modules.minigame.utils.GenerationTaiXiu;
import game.modules.minigame.utils.MiniGameUtils;
import game.modules.minigame.utils.RutLocUtils;
import game.modules.minigame.utils.TaiXiuUtils;
import game.utils.GameUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TaiXiuModule
extends BaseClientRequestHandler {
    private Map<String, MGRoom> rooms = new HashMap<String, MGRoom>();
    private final Runnable gameLoopTask = new GameLoopTask();
    private final Runnable serverReadyTask = new ServerReadyTask();
    private final Runnable botChatTask = new ScheduleBotChatTask();
    private final Runnable calculatingTXVinTask = new CalculatingTaiXiuPrize((short)1);
    private final Runnable calculatingTXXuTask = new CalculatingTaiXiuPrize((short)0);
    //private final Runnable rewardThanhDuDailyTask = new RewardThanhDuDaily();
    private int count = 0;
    private boolean serverReady = false;
    private ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(10);
    private long referenceTaiXiuId;
    private OverUnderService txService = new OverUnderServiceImpl();
    private MiniGameService mgService = new MiniGameServiceImpl();
    private List<ResultTaiXiu> lichSuPhienTX = new ArrayList<ResultTaiXiu>();
    private GenerationTaiXiu generationTX = new GenerationTaiXiu();
    private short result = (short)-1;
    private long fundRutLoc = 0L;
    private int countRutLoc = 0;
    private int countReqRutLoc = 0;
    private int[] rutLocPrizes;
    private int[] phanBoGiaiThuong;
    private boolean enableRutLoc = false;
    private int tongSoNguoiRutLocLanTruoc = 30;
    private List<BotTaiXiu> botsVin = new ArrayList<BotTaiXiu>();
    private List<BotTaiXiu> botsXu = new ArrayList<BotTaiXiu>();
    private short forceBetSide = (short)-1;
    private long MinCtrl = 1000000L;
    private List<String> listChat = new ArrayList<String>();
    private List<String> listChatUsers = new ArrayList<String>();
    private static final org.apache.log4j.Logger logger = Logger.getLogger((String)"recharge");

    public void init() {
        this.rooms.put(MGRoomTaiXiu.getKeyRoom((short)1), new MGRoomTaiXiu("TaiXiu_1", this.referenceTaiXiuId, (byte)1));
        this.rooms.put(MGRoomTaiXiu.getKeyRoom((short)0), new MGRoomTaiXiu("TaiXiu_0", this.referenceTaiXiuId, (byte)0));
        this.loadData();
        this.loadChatData();
        this.loadChatUsers();
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.gameLoopTask, 10, 1, TimeUnit.SECONDS);
        BitZeroServer.getInstance().getTaskScheduler().schedule(this.botChatTask, 10, TimeUnit.SECONDS);        
        BitZeroServer.getInstance().getTaskScheduler().schedule(this.serverReadyTask, 10, TimeUnit.SECONDS);
        Debug.trace("SERVER READY TASK RUNNING...");
        this.getParentExtension().addEventListener((IBZEventType)BZEventType.USER_DISCONNECT, (IBZEventListener)this);
        try {
            int remainTimeTraThuongThanhDu = MiniGameUtils.calculateTimeRewardOnNextDay("");
          //  BitZeroServer.getInstance().getTaskScheduler().schedule(this.rewardThanhDuDailyTask, remainTimeTraThuongThanhDu, TimeUnit.SECONDS);
        }
        catch (ParseException e) {
            Debug.trace((Object[])new Object[]{"Calculate time reward Thanh du error ", e.getMessage()});
        }
    }
    
    public void loadChatData() {
        try {
            String entry;
            BufferedReader br2 = new BufferedReader(new InputStreamReader((InputStream)new FileInputStream("config/list_chat.txt"), "UTF8"));
            while ((entry = br2.readLine()) != null) {
                this.listChat.add(entry);
            }
            br2.close();
            Debug.trace("BOT CHAT :" + listChat.size());
        }
        catch (FileNotFoundException entry) {
        }
        catch (IOException entry) {
            // empty catch block
        }
    }
    
    public void loadChatUsers() {
        try {
            String entry;
            BufferedReader br2 = new BufferedReader(new InputStreamReader((InputStream)new FileInputStream("config/listchat.txt"), "UTF8"));
            while ((entry = br2.readLine()) != null) {
                this.listChatUsers.add(entry);
            }
            br2.close();
            Debug.trace("BOT CHAT USERS :" + listChatUsers.size());
        }
        catch (FileNotFoundException entry) {
        }
        catch (IOException entry) {
            // empty catch block
        }
    }

    public void handleServerEvent(IBZEvent ibzevent) throws BZException {
        if (ibzevent.getType() == BZEventType.USER_DISCONNECT) {
            User user = (User)ibzevent.getParameter((IBZEventParam)BZEventParam.USER);
            this.userDis(user);
        }
    }

    private void userDis(User user) {
        MGRoom room = (MGRoom)user.getProperty((Object)"MGROOM_TAI_XIU_INFO");
        if (room != null) {
            room.quitRoom(user);
        }
    }

    private void loadData() {
        this.referenceTaiXiuId = 1L;
        try {
            this.referenceTaiXiuId = this.mgService.getReferenceId(4);
            this.lichSuPhienTX = this.txService.getListLichSuPhien(120, 1);
        }
        catch (SQLException e) {
            Debug.trace((Object[])new Object[]{"Load reference error ", e.getMessage()});
        }
        try {
            this.generationTX.readConfig();
        }
        catch (IOException e) {
            Debug.trace((Object[])new Object[]{"Load cau tai xiu error ", e.getMessage()});
        }
        try {
            this.fundRutLoc = this.txService.getPotTanLoc();
            if (this.fundRutLoc < 100000L) {
                this.countRutLoc = -1;
            }
        }
        catch (SQLException e) {
            Debug.trace((Object[])new Object[]{"Load fund tan loc error ", e.getMessage()});
        }
        Debug.trace((Object)("Phien TX: " + this.referenceTaiXiuId));
        Debug.trace((Object)("SIZE LSDG: " + this.lichSuPhienTX.size()));
        Debug.trace((Object)("LSDG: " + TaiXiuUtils.logLichSuPhien(this.lichSuPhienTX, 120)));
        Debug.trace((Object)("Fund tan loc: " + this.fundRutLoc));
    }

    private void saveReferences() {
        try {
            this.mgService.saveReferenceId(this.referenceTaiXiuId, 4);
        }
        catch (SQLException e) {
            Debug.trace((Object)("Save reference error " + e.getMessage()));
        }
    }

    public void handleClientRequest(User user, DataCmd dataCmd) {
        switch (dataCmd.getId()) {
            case 2000: {
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
                this.betTaiXiu(user, dataCmd);
                break;
            }
            case 2116: {
                this.getLichSuPhienTX(user);
                break;
            }
            case 2118: {
                if (GameUtils.disablePlayMiniGame(user)) {
                    return;
                }
                this.tanLoc(user, dataCmd);
                break;
            }
            case 2119: {
                if (GameUtils.disablePlayMiniGame(user)) {
                    return;
                }
                this.rutLoc(user, dataCmd);
            }
        }
    }

    private void subcribeMiniGame(User user, DataCmd dataCmd) {
        SubcribeMinigameCmd cmd = new SubcribeMinigameCmd(dataCmd);
        this.doSubcribeMiniGame(user, cmd.gameId, cmd.roomId);
        LichSuPhienMsg msgLSGD = new LichSuPhienMsg();
        msgLSGD.data = TaiXiuUtils.buildLichSuPhien(this.lichSuPhienTX, 22);
        this.send((BaseMsg)msgLSGD, user);
        UpdateRutLocMsg rutLocMsg = new UpdateRutLocMsg();
        try {
            rutLocMsg.soLuotRut = this.txService.getLuotRutLoc(user.getName());
        }
        catch (Exception e) {
            Debug.trace((Object[])new Object[]{"Get so luot rut loc " + user.getName() + " error ", e.getMessage()});
        }
        this.send((BaseMsg)rutLocMsg, user);
        UpdateFundTanLocMsg fundRLMsg = new UpdateFundTanLocMsg();
        fundRLMsg.value = this.fundRutLoc;
        this.send((BaseMsg)fundRLMsg, user);
    }

    private void doSubcribeMiniGame(User user, short gameId, short roomId) {
        switch (gameId) {
            case 2: {
                short moneyType = MGRoomTaiXiu.getMoneyType(roomId);
                String keyRoom = MGRoomTaiXiu.getKeyRoom(moneyType);
                MGRoomTaiXiu roomTX = (MGRoomTaiXiu)this.getGame(keyRoom);
                if (roomTX != null) {
                    roomTX.joinRoom(user);
                    roomTX.updateTaiXiuInfo(user, this.getRemainTimeRutLoc());
                    break;
                }
                CommonHandle.writeErrLog((String)"Game TAI XIU not found");
                break;
            }
            default: {
                Debug.trace((Object)"Game id not found");
            }
        }
    }

    private void unsubscribeMiniGame(User user, DataCmd dataCmd) {
        UnsubscribeMiniGameCmd cmd = new UnsubscribeMiniGameCmd(dataCmd);
        this.doUnsubscribeMiniGame(user, cmd.gameId, cmd.roomId);
    }

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

    private void startNewRoundTX() {
        MGRoomTaiXiu roomTXVin = this.getRoomTX((short)1);
        MGRoomTaiXiu roomTXXu = this.getRoomTX((short)0);
        ++this.referenceTaiXiuId;
        roomTXVin.startNewGame(this.referenceTaiXiuId);
        roomTXXu.startNewGame(this.referenceTaiXiuId);
        StartNewGameTaiXiuMsg msg = new StartNewGameTaiXiuMsg();
        msg.referenceId = this.referenceTaiXiuId;
        msg.remainTimeRutLoc = this.getRemainTimeRutLoc();
        this.sendMessageToTaiXiuNewThread(msg);
        this.saveReferences();
    }

    private void scheduleBot() {
        try {
            this.botsXu.clear();
            this.botsVin.clear();
            this.botsVin = BotMinigame.getBotTaiXiu("vin");
            Debug.trace((Object)("BOTS VIN: " + this.botsVin.size()));
            List<BotTaiXiu> botsVip = BotMinigame.getVipBotTaiXiu();
            this.botsVin.addAll(botsVip);
            Debug.trace((Object)("TX BOTS VIP: " + botsVip.size()));
            this.botsXu = BotMinigame.getBotTaiXiu("xu");
            Debug.trace((Object)("BOTS XU: " + this.botsXu.size()));
        }
        catch (Exception e) {
            GameUtils.sendAlert("Bot tai xiu start error: " + e.getMessage() + ", time= " + DateTimeUtils.getCurrentTime());
        }
    }

    private void botBet(int count) {
        MGRoomTaiXiu roomVin = this.getRoomTX((short)1);
        MGRoomTaiXiu roomXu = this.getRoomTX((short)0);
        for (BotTaiXiu b : this.botsVin) {
            if (b.getTimeBetting() != 60 - count) continue;
            roomVin.betTaiXiu(b.getNickname(), 0, b.getBetValue(), b.getTimeBetting(), (short)1, b.getBetSide(), true);
        }
        for (BotTaiXiu b : this.botsXu) {
            if (b.getTimeBetting() != 60 - count) continue;
            roomXu.betTaiXiu(b.getNickname(), 0, b.getBetValue(), b.getTimeBetting(), (short)0, b.getBetSide(), true);
        }
    }

    public void betTaiXiu(User user, DataCmd dataCmd) {
        BetTaiXiuCmd cmd = new BetTaiXiuCmd(dataCmd);
        MGRoomTaiXiu roomTX = this.getRoomTX(cmd.moneyType);
        if (roomTX != null) {
            roomTX.betTaiXiu(user, cmd);
        }
    }

    private synchronized void gameLoop() {
        try {
            ++this.count;
            this.botBet(this.count);
            if (this.countRutLoc > -1) {
                ++this.countRutLoc;
            }
            MGRoomTaiXiu roomTXVin = this.getRoomTX((short)1);
            roomTXVin.updateTaiXiuPerSecond();
            MGRoomTaiXiu roomTXXu = this.getRoomTX((short)0);
            roomTXXu.updateTaiXiuPerSecond();
            this.sendTXTime(roomTXVin.getRemainTime(), roomTXVin.isBetting());
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
                case 48: {
                    break;
                }
                case 58: {
                    //this.forceBalanceLateGame(roomTXVin);
                    //this.forceBetSide = (short)(ThreadLocalRandom.current().nextInt(0, 1000560000) % 2);
                    this.forceBetSide = roomTXVin.suggestResult();
                    Debug.trace((Object)("tx2 force: " + this.forceBetSide));
                    break;
                }
                case 61: {
                    this.generateTaiXiuDices(roomTXVin, roomTXXu);
                    break;
                }
                case 65: {
                    logger.debug("Run calculatingTXVinTask");
                    BitZeroServer.getInstance().getTaskScheduler().schedule(this.calculatingTXVinTask, 1, TimeUnit.SECONDS);
                    BitZeroServer.getInstance().getTaskScheduler().schedule(this.calculatingTXXuTask, 1, TimeUnit.SECONDS);
                    break;
                }
                case 85: {
                    ScheduleBotTask t = new ScheduleBotTask();
                    this.executor.execute(t);
                    break;
                }
                case 90: {
                    roomTXVin.getBalanceTX().startNewRound();
                    this.startNewRoundTX();
                    this.count = 0;
                }
            }
            switch (this.countRutLoc) {
                case 869: {
                    Debug.trace((Object)"RUT LOC");
                    this.rutLocPrizes = RutLocUtils.getPrizes(this.fundRutLoc, 10);
                    this.phanBoGiaiThuong = RutLocUtils.phanBoGiaiThuong(this.tongSoNguoiRutLocLanTruoc, 10);
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < this.phanBoGiaiThuong.length; ++i) {
                        builder.append(this.phanBoGiaiThuong[i]);
                        builder.append(",");
                    }
                    Debug.trace((Object)("PHAN BO GIAI THUONG: " + builder.toString()));
                    this.enableRutLoc = true;
                    this.sendMessageToTaiXiuNewThread(new EnableRutLocMsg());
                    break;
                }
                case 900: {
                    Debug.trace((Object)"PHIEN RUT LOC MOI");
                    this.countRutLoc = 0;
                    if (this.fundRutLoc < 100000L) {
                        this.countRutLoc = -1;
                    }
                    this.tongSoNguoiRutLocLanTruoc = this.countReqRutLoc < 30 ? 30 : this.countReqRutLoc;
                    Debug.trace((Object)("Tong so nguoi rut loc: " + this.tongSoNguoiRutLocLanTruoc));
                    this.countReqRutLoc = 0;
                    this.enableRutLoc = false;
                    StartNewRoundRutLocMsg newRoundMsg = new StartNewRoundRutLocMsg();
                    newRoundMsg.remainTime = this.getRemainTimeRutLoc();
                    this.sendMessageToTaiXiuNewThread(newRoundMsg);
                }
            }
        }
        catch (Exception e) {
            Debug.trace((Object[])new Object[]{"Exception: " + e.getMessage(), e});
        }
    }

    private void forceBalanceLateGame(MGRoomTaiXiu roomTXVin) {
        int randomNum;
        this.forceBetSide = roomTXVin.nguoichoidatTai > roomTXVin.nguoichoidatXiu && roomTXVin.nguoichoidatTai - roomTXVin.nguoichoidatXiu > this.MinCtrl ? (short)0 : (roomTXVin.nguoichoidatTai < roomTXVin.nguoichoidatXiu && roomTXVin.nguoichoidatXiu - roomTXVin.nguoichoidatTai > this.MinCtrl ? (short)1 : ((randomNum = ThreadLocalRandom.current().nextInt(0, 1000560000)) % 2 == 0 ? (short)0 : 1));
        try {
            try (PrintStream out = new PrintStream(new FileOutputStream("logTaiXiu.txt", true));){
                out.println(roomTXVin.referenceId + "> Tai:" + roomTXVin.nguoichoidatTai + "(" + roomTXVin.getUserBetTai() + ") Xiu:" + roomTXVin.nguoichoidatXiu + "(" + roomTXVin.getUserBetXiu() + ") >" + this.forceBetSide);
                out.close();
            }
        }
        catch (Exception out) {
            // empty catch block
        }
    }

    private void resetForceBalance() {
        this.forceBetSide = (short)-1;
    }

    private void generateTaiXiuDices(MGRoomTaiXiu roomTXVin, MGRoomTaiXiu roomTXXu) {
        Debug.trace((Object)("FORCE==============" + this.forceBetSide));
        short[] dices = this.generationTX.generateResult(this.forceBetSide);
        this.resetForceBalance();
        short total = (short)(dices[0] + dices[1] + dices[2]);
        this.result = total > 10 ? (short)1 : 0;
        roomTXVin.updateResultDices(dices, this.result);
        roomTXXu.updateResultDices(dices, this.result);
        ResultTaiXiu resultTX = new ResultTaiXiu();
        resultTX.referenceId = this.referenceTaiXiuId;
        resultTX.result = this.result;
        resultTX.dice1 = dices[0];
        resultTX.dice2 = dices[1];
        resultTX.dice3 = dices[2];
        Debug.trace((Object)("GENERATE RESULT DICES: " + dices[0] + " - " + dices[1] + " - " + dices[2] + "   " + this.result));
        this.lichSuPhienTX.add(resultTX);
        if (this.lichSuPhienTX.size() > 120) {
            this.lichSuPhienTX.remove(0);
        }
    }

    private void getLichSuPhienTX(User user) {
        LichSuPhienMsg msg = new LichSuPhienMsg();
        msg.data = TaiXiuUtils.buildLichSuPhien(this.lichSuPhienTX, 22);
        Debug.trace((Object)("LSDG: " + TaiXiuUtils.logLichSuPhien(this.lichSuPhienTX, 120)));
        this.send((BaseMsg)msg, user);
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
                MoneyResponse response = userService.updateMoney(user.getName(), -cmd.money, "vin", "OverUnder", "Tài Xỉu - Tán lộc", "Tán lộc tài xỉu", 0L, null, TransType.NO_VIPPOINT);
                if (response != null && response.isSuccess() && (success = this.updateFundRutLoc(cmd.money))) {
                    curretnMoney = response.getCurrentMoney();
                    msg.result = 0;
                    try {
                        this.txService.logTanLoc(user.getName(), cmd.money);
                    }
                    catch (IOException | InterruptedException | TimeoutException exception) {
                        // empty catch block
                    }
                    if (this.countRutLoc == -1 && this.fundRutLoc >= 100000L) {
                        this.countRutLoc = 0;
                        StartNewRoundRutLocMsg newRoundMsg = new StartNewRoundRutLocMsg();
                        newRoundMsg.remainTime = this.getRemainTimeRutLoc();
                        this.sendMessageToTaiXiu(newRoundMsg);
                    }
                }
            } else {
                msg.result = (short)3;
            }
        } else {
            msg.result = (short)2;
        }
        msg.currentMoney = curretnMoney;
        this.send((BaseMsg)msg, user);
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
                }
                catch (SQLException e) {
                    Debug.trace((Object[])new Object[]{"Get so luot rut loc error ", e.getMessage()});
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
                    if ((long)prize > this.fundRutLoc) {
                        prize = 0;
                    }
                    if (prize > 0 && (response = userService.updateMoney(user.getName(), (long)prize, "vin", "OverUnder", "Tài xỉu - Rút lộc", "C\u00e1\u00bb\u2122ng ti\u00e1\u00bb\ufffdn r\u00c3\u00bat l\u00e1\u00bb\u2122c", 0L, null, TransType.NO_VIPPOINT)) != null && response.isSuccess()) {
                        currentMoney = response.getCurrentMoney();
                    }
                    ++this.countReqRutLoc;
                    --soLuotRut;
                    msg.prize = prize;
                    try {
                        this.txService.updateLuotRutLoc(user.getName(), -1);
                        UpdateRutLocMsg soLuotRutMsg = new UpdateRutLocMsg();
                        soLuotRutMsg.soLuotRut = soLuotRut;
                        this.send((BaseMsg)soLuotRutMsg, user);
                    }
                    catch (IOException | InterruptedException | TimeoutException e) {
                        Debug.trace((Object[])new Object[]{"Update luot rut loc error ", e.getMessage()});
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
        this.send((BaseMsg)msg, user);
        if (msg.prize > 0) {
            this.updateFundRutLoc(-msg.prize);
            try {
                this.txService.logRutLoc(user.getName(), (long)msg.prize, this.countReqRutLoc, this.fundRutLoc);
            }
            catch (IOException | InterruptedException | TimeoutException e) {
                Debug.trace((Object[])new Object[]{"Log rut loc error ", e.getMessage()});
            }
        }
    }

    private boolean updateFundRutLoc(long moneyExchagne) {
        boolean success = false;
        this.fundRutLoc += moneyExchagne;
        if (this.fundRutLoc < 0L) {
            Debug.trace((Object)("Quy rut loc " + this.fundRutLoc + " < 0"));
        }
        try {
            this.txService.updatePotTanLoc(this.fundRutLoc);
            UpdateFundTanLocMsg msg = new UpdateFundTanLocMsg();
            msg.value = this.fundRutLoc;
            this.sendMessageToTaiXiu(msg);
            success = true;
        }
        catch (Exception e) {
            Debug.trace((Object[])new Object[]{"Update fund tan loc error ", e.getMessage()});
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
        return (short)remainTime;
    }

    private void sendMessageToTaiXiuNewThread(BaseMsg msg) {
        SendMessageToTXThread t = new SendMessageToTXThread(false, msg);
        this.executor.execute(t);
    }

    private void sendTXTime(short remainTime, boolean betting) {
        BroadcastTXTimeMsg msg = new BroadcastTXTimeMsg();
        msg.remainTime = (byte)remainTime;
        msg.betting = betting;
        SendMessageToTXThread t = new SendMessageToTXThread(true, msg);
        this.executor.execute(t);
    }

    private void sendMessageToAllUsers(BaseMsg msg) {
        List users = ExtensionUtility.globalUserManager.getAllUsers();
        if (users != null) {
            this.send(msg, users);
        }
    }

    private void sendMessageToTaiXiu(BaseMsg msg) {
        MGRoomTaiXiu roomTXVin = this.getRoomTX((short)1);
        roomTXVin.sendMessageToRoom(msg);
        MGRoomTaiXiu roomTXXu = this.getRoomTX((short)0);
        roomTXXu.sendMessageToRoom(msg);
    }

    public MGRoom getGame(String key) {
        return this.rooms.get(key);
    }

    private MGRoomTaiXiu getRoomTX(short moneyType) {
        String keyRoom = MGRoomTaiXiu.getKeyRoom(moneyType);
        return (MGRoomTaiXiu)this.getGame(keyRoom);
    }

    private final class GameLoopTask
    implements Runnable {
        private GameLoopTask() {
        }

        @Override
        public void run() {
            try {
                TaiXiuModule.this.gameLoop();
            }
            catch (Exception e) {
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
                Debug.trace((Object)"START MINI GAME");
                TaiXiuModule.this.serverReady = true;
                ScheduleBotTask t = new ScheduleBotTask();
                TaiXiuModule.this.executor.execute(t);
                TaiXiuModule.this.startNewRoundTX();
            }
        }
    }

    private final class CalculatingTaiXiuPrize
    implements Runnable {
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
            }
            catch (Exception e) {
                Debug.trace((Object)("Calculate TX " + this.roomId + ", phien= " + TaiXiuModule.this.referenceTaiXiuId + " error: " + e.getMessage()));
            }
            long endTime = System.currentTimeMillis();
            Debug.trace((Object)("CALCUALTE PRIZE, time handle= " + (endTime - startTime) + " (ms)"));
            logger.debug("Run txService.updateAllTop");
            TaiXiuModule.this.txService.updateAllTop();
        }
    }

    private final class RewardThanhDuDaily
    implements Runnable {
        private RewardThanhDuDaily() {
        }

        @Override
        public void run() {
          //  TaiXiuUtils.rewardThanhDu();
          //  BitZeroServer.getInstance().getTaskScheduler().schedule(TaiXiuModule.this.rewardThanhDuDailyTask, 24, TimeUnit.HOURS);
            Debug.trace((Object)"Tra thuong Thanh Du");
        }
    }

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
            try
            {
                Debug.trace("Schedule bot running ...");
                TaiXiuModule.this.scheduleBot();            
                Debug.trace("Schedule bot finished ...");
            }
            catch (Exception ex)
            {
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
            try
            {
                Debug.trace("Schedule bot chat running ...");
                TaiXiuModule.this.scheduleBotChat();
                Debug.trace("Schedule bot chat finished ...");
            }
            catch (Exception ex)
            {
                Debug.trace(ex.getMessage());
            }
        }
    }
    
    private void scheduleBotChat() {
        try {
            Random rand = new Random();            
            while (true)
            {
                if (listChatUsers.size() > 0)
                {
                    int sleep = rand.nextInt(2);
                    Thread.sleep(14000 + sleep * 1000);
                    MGRoomTaiXiu roomTXVin = this.getRoomTX((short)1);           
                    ChatMsg msg = new ChatMsg();                
                    String user = listChatUsers.get(rand.nextInt(listChatUsers.size()));
                    msg.nickname = user;
                    String randMessage = listChat.get(rand.nextInt(listChat.size()));
                    msg.mesasge = randMessage;
                    roomTXVin.sendMessageToRoom(msg);
                }
                else
                {
                    int sleep = rand.nextInt(5000);
                    Thread.sleep(sleep * 1000);
                }
            }
        }
        catch (Exception e) {
            Debug.trace(e.getMessage());
            // GameUtils.sendAlert("Bot tai xiu start error: " + e.getMessage() + ", time= " + DateTimeUtils.getCurrentTime());
        }
    }
}

