/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  bitzero.server.BitZeroServer
 *  bitzero.server.entities.User
 *  bitzero.server.extensions.data.DataCmd
 *  bitzero.server.util.TaskScheduler
 *  bitzero.util.common.business.Debug
 *  com.vinplay.gamebai.entities.XocDiaBoss
 *  com.vinplay.usercore.logger.MoneyLogger
 *  com.vinplay.usercore.service.MoneyInGameService
 *  com.vinplay.usercore.service.XocDiaService
 *  com.vinplay.usercore.service.impl.MoneyInGameServiceImpl
 *  com.vinplay.usercore.service.impl.XocDiaServiceImpl
 *  com.vinplay.vbee.common.enums.FreezeInGame
 *  com.vinplay.vbee.common.response.MoneyResponse
 *  com.vinplay.vbee.common.statics.TransType
 *  com.vinplay.vbee.common.utils.VinPlayUtils
 *  game.entities.PlayerInfo
 *  game.entities.UserScore
 *  game.modules.bot.BotXocDiaManager
 *  game.modules.gameRoom.entities.BanUserManager
 *  game.modules.gameRoom.entities.BossManager
 *  game.modules.gameRoom.entities.GameMoneyInfo
 *  game.modules.gameRoom.entities.GameRoom
 *  game.modules.gameRoom.entities.GameRoomGroup
 *  game.modules.gameRoom.entities.GameRoomIdGenerator
 *  game.modules.gameRoom.entities.GameRoomManager
 *  game.modules.gameRoom.entities.GameRoomSetting
 *  game.modules.gameRoom.entities.GameServer
 *  game.modules.gameRoom.entities.ListGameMoneyInfo
 *  game.modules.gameRoom.entities.ThongTinThangLon
 *  game.utils.GameUtils
 *  game.utils.NumberUtils
 *  game.xocdia.conf.XocDiaBetBotModel
 *  game.xocdia.conf.XocDiaConfig
 *  game.xocdia.conf.XocDiaForceResult
 *  org.json.JSONObject
 *  scala.collection.mutable.StringBuilder
 *  scala.util.Random
 */
package game.xocdia.server;

import bitzero.server.BitZeroServer;
import bitzero.server.entities.User;
import bitzero.server.extensions.data.DataCmd;
import bitzero.util.common.business.Debug;
import com.vinplay.common.HttpCommon;
import com.vinplay.gamebai.entities.XocDiaBoss;
import com.vinplay.usercore.logger.MoneyLogger;
import com.vinplay.usercore.service.CacheService;
import com.vinplay.usercore.service.MoneyInGameService;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.XocDiaService;
import com.vinplay.usercore.service.impl.CacheServiceImpl;
import com.vinplay.usercore.service.impl.MoneyInGameServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.service.impl.XocDiaServiceImpl;
import com.vinplay.vbee.common.enums.FreezeInGame;
import com.vinplay.vbee.common.exceptions.KeyNotFoundException;
import com.vinplay.vbee.common.response.BauCuaTo2.SetBauCuaKetqua;
import com.vinplay.vbee.common.response.MoneyResponse;
import com.vinplay.vbee.common.statics.TransType;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import game.xocdia.bot.BotBettingModel;
import game.xocdia.cmd.send.*;
import game.xocdia.entities.*;
import game.xocdia.utils.XocDiaResult;
import game.entities.PlayerInfo;
import game.entities.UserScore;
import game.modules.bot.BotXocDiaManager;
import game.modules.gameRoom.entities.BanUserManager;
import game.modules.gameRoom.entities.BossManager;
import game.modules.gameRoom.entities.GameMoneyInfo;
import game.modules.gameRoom.entities.GameRoom;
import game.modules.gameRoom.entities.GameRoomIdGenerator;
import game.modules.gameRoom.entities.GameRoomManager;
import game.modules.gameRoom.entities.GameServer;
import game.modules.gameRoom.entities.ListGameMoneyInfo;
import game.modules.gameRoom.entities.ThongTinThangLon;
import game.utils.GameUtils;
import game.utils.NumberUtils;
import game.xocdia.bot.BotPurchaseModel;
import game.xocdia.bot.BotRejectModel;
import game.xocdia.bot.BotRequestBankerModel;
import game.xocdia.bot.BotSellPotModel;
import game.xocdia.cmd.rev.AllInCmd;
import game.xocdia.cmd.rev.BetCmd;
import game.xocdia.cmd.rev.BuyPotCmd;
import game.xocdia.cmd.rev.ChatCmd;
import game.xocdia.cmd.rev.CheatCmd;
import game.xocdia.cmd.rev.RegisChangeLockPotCmd;
import game.xocdia.cmd.rev.RejectCmd;
import game.xocdia.cmd.rev.ReqKickRoomCmd;
import game.xocdia.cmd.rev.SellPotCmd;
import game.xocdia.conf.XocDiaBetBotModel;
import game.xocdia.conf.XocDiaConfig;
import game.xocdia.conf.XocDiaForceResult;
import game.xocdia.utils.MsgUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import scala.collection.mutable.StringBuilder;
import scala.util.Random;

public class XocDiaGameServer
        extends GameServer {
    private final Runnable gameLoopTask = new GameLoopTask();
    private ScheduledFuture<?> task;
    private final Runnable bossTask = new BossTask();
    private ScheduledFuture<?> taskBoss;
    private final Runnable botTask = new BotTask();
    private ScheduledFuture<?> taskBot;
    private MoneyInGameService service = new MoneyInGameServiceImpl();
    private XocDiaService xdService = new XocDiaServiceImpl();
    private Random rd = new Random();

    private GameRoom room;
    private int roomId;
    private int roomType;
    private int maxUsers;
    private long fundMin;
    private long fundInitial;
    private int moneyBet;
    private byte moneyType;
    private double feeWin;
    private double bankerFee;
    private String sMoneyType;
    private long moneyRegisBankerMin;
    private long moneyHoldBankerMin;
    private Vector<GamePot> potList;
    private BlockingDeque<String> reqBankerList;
    private volatile Map<String, Long> subBankerList;
    private volatile int purchaseStatus;
    private volatile long moneyDif;
    private volatile boolean finishStep;
    private volatile boolean enableBetting;
    private volatile byte gameState;
    private volatile int countTime;
    private volatile int gameId;
    private Map<String, GamePlayer> playerList;
    private Map<String, GameReportModel> playerReportList;
    private List<String> playerListrp;
    private volatile boolean isRegisterLoop;
    private List<Byte> rsList;
    private int totalEven;
    private int totalOdd;
    private volatile String bankerName;
    private volatile StringBuilder gameLog;
    private volatile String lastBetting;
    private BlockingDeque<LogBettingModel> logBettingList;
    private boolean isBetting;
    private List<Integer> rsCheat;
    private long totalReveneu;
    private long totalFee;
    private volatile long moneyPurchaseEven;
    private volatile long moneyPurchaseOdd;
    private volatile long moneySell;
    private volatile long moneyBuy;
    private volatile int potPurchase;
    private volatile boolean isBankerReject;
    private CauXocDia cauXD;
    private int cntDate;
    private int transInDay;
    private int transInWeek;
    private volatile boolean isLock;
    private volatile boolean reqDestroyGame;
    private List<BotBettingModel> botBettingList;
    private List<BotPurchaseModel> botPurchaseList;
    private List<BotRequestBankerModel> botReqBankerList;
    private BotSellPotModel botSellPot;
    private BotRejectModel botReject;
    private boolean emty;
    private long timeSellPot;
    private boolean bCheckBalanceBot;
    private List<Integer> listCoins;
    private CacheService cacheService = new CacheServiceImpl();
    private static UserService userService = new UserServiceImpl();

    public void init(GameRoom room) {
        try {
            this.room = room;
            this.roomId = room.getId();
            this.roomType = room.setting.rule;
            this.maxUsers = room.setting.maxUserPerRoom;
            this.moneyBet = (int) room.setting.moneyBet;
            this.fundMin = Math.round((double) this.moneyBet * XocDiaConfig.fundVipMinHold);
            this.fundInitial = 0L;
            this.moneyType = (byte) room.setting.moneyType;
            this.feeWin = (double) room.setting.commisionRate / 100.0;
            this.bankerFee = XocDiaConfig.bankerFee;
            this.sMoneyType = this.moneyType == 1 ? "vin" : "xu";
            this.moneyRegisBankerMin = Math.round(XocDiaConfig.minRegisBanker * this.moneyBet);
            this.moneyHoldBankerMin = Math.round(XocDiaConfig.minHoldBanker * this.moneyBet);
            Debug.trace((Object) ("START INIT ROOM, roomId: " + this.roomId + ", roomType: " + this.roomType + ", maxUsers: " + this.maxUsers + ", moneyBet: " + this.moneyBet + ", sMoneyType: " + this.sMoneyType + ", feeWin: " + this.feeWin + ", bankerFee: " + this.bankerFee + ", moneyRegisBankerMin: " + this.moneyRegisBankerMin + ", moneyHoldBankerMin: " + this.moneyHoldBankerMin + ", fundMin: " + this.fundMin));
            this.gameId = 0;
            this.potList = new Vector(6);
            for (int i = 0; i < 6; ++i) {
                PotType pt = PotType.findPotType(i);
                this.potList.add(new GamePot(pt.getId(), pt.getRatio(), pt.getName(), Math.round(pt.getMaxRatioBet() * (double) this.moneyBet)));
            }
            this.playerList = new ConcurrentHashMap<String, GamePlayer>();
            this.playerReportList = new ConcurrentHashMap<>();
            this.purchaseStatus = 0;
            this.moneyDif = 0L;
            this.finishStep = false;
            this.enableBetting = false;
            this.gameState = 0;
            this.countTime = -1;
            this.isRegisterLoop = false;
            this.rsList = new ArrayList<Byte>();
            this.totalEven = 0;
            this.totalOdd = 0;
            this.bankerName = "";
            this.reqBankerList = new LinkedBlockingDeque<String>();
            this.subBankerList = new ConcurrentHashMap<String, Long>();
            this.gameLog = new StringBuilder("");
            this.logBettingList = new LinkedBlockingDeque<LogBettingModel>();
            this.playerListrp = new ArrayList<>();
            this.isBetting = false;
            this.cauXD = null;
            this.cntDate = 0;
            this.transInDay = 0;
            this.transInWeek = 0;
            this.reqDestroyGame = false;
            this.isLock = false;
            this.botBettingList = new LinkedList<BotBettingModel>();
            this.botPurchaseList = new LinkedList<BotPurchaseModel>();
            this.botReqBankerList = new LinkedList<BotRequestBankerModel>();
            this.botSellPot = null;
            this.botReject = null;
            this.emty = true;
            this.timeSellPot = 0L;
            this.bCheckBalanceBot = this.bCheckBalanceBot();
            this.listCoins = this.roomType == 1 ? XocDiaConfig.listCoinsGlobal : XocDiaConfig.listCoinsNormal;
            Debug.trace((Object[]) new Object[]{"INIT ROOM SUCCESS", this.roomId, this.gameId});
            if (this.bCheckBalanceBot && this.moneyBet <= XocDiaConfig.normalMaxRoom) {
                BotXocDiaManager.instance().regJoinRoom(room);
            }
        } catch (Exception e) {
            Debug.trace((Object) ("INIT ROOM ERROR: " + e.getMessage()));
            Debug.trace((Object) e);
        }
    }

    public synchronized void init() {
        try {
            if (!this.isRegisterLoop) {
                GamePlayer banker;
                Debug.trace((Object[]) new Object[]{"START LOOP", this.roomId, this.gameId});
                if (!this.bankerName.isEmpty() && (banker = this.getPlayer(this.bankerName)) != null) {
                    banker.setPlaying(this.roomId);
                    this.setPlayer(this.bankerName, banker);
                }
                this.task = BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.gameLoopTask, 0, 1, TimeUnit.SECONDS);
                if (this.bCheckBalanceBot) {
                    this.taskBot = BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.botTask, 0, XocDiaConfig.timeTaskBotInGame, TimeUnit.MILLISECONDS);
                }
                this.isRegisterLoop = true;
            }
        } catch (Exception e) {
            String content = "Xoc Dia exception: " + e.getMessage() + ", function: init() " + this.roomId + " " + this.gameId;
            MsgUtils.alertServer(content, false, true);
            Debug.trace((Object) e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized void prepareNewGame(boolean isNextGame) {
        try {
            Map<String, GamePlayer> map = this.playerList;
            synchronized (map) {
                if (this.gameState == 0) {
                    Debug.trace((Object[]) new Object[]{"PREPARE NEW GAME", this.roomId, this.gameId});
                    this.destroyLoop();

                    for (Map.Entry<String, GamePlayer> entry : this.playerList.entrySet()) {
                        byte rs;
                        GamePlayer gp;
                        if ((rs = (gp = entry.getValue()).isLeaveRoom(this.reqDestroyGame)) == 0)
                            continue;
                        gp.playerStatus = 1;
                        this.setPlayer(entry.getKey(), gp);
                        this.leaveRoom(gp.user);
                        if (rs == 4) continue;
                        Debug.info("info ressult" + rs);
                        this.notifyOutRoom(gp, rs);
                    }
                    this.reset();
                    if (this.playerList.size() >= 2) {
                        Debug.trace((Object[]) new Object[]{"\u0110\u1ee7 2 ng\u01b0\u1eddi ch\u01a1i, b\u1eaft \u0111\u1ea7u game m\u1edbi", this.roomId, this.gameId});
                        this.init();
                    } else if (isNextGame) {
                        Debug.trace((Object[]) new Object[]{"Kh\u00f4ng \u0111\u1ee7 ng\u01b0\u1eddi ch\u01a1i => k\u1ebft th\u00fac game", this.roomId, this.gameId});
                        this.notifyStopGame();
                    }
                }
            }
        } catch (Exception e) {
            String content = "Xoc Dia exception: " + e.getMessage() + ", function: prepareNewGame() " + this.roomId + " " + this.gameId;
            MsgUtils.alertServer(content, false, true);
            Debug.trace((Object) e);
        }
    }


    private void reset() {
        try {
            Debug.trace((Object[]) new Object[]{"RESET NEW GAME", this.roomId, this.gameId});
            for (int i = 0; i < 6; ++i) {
                GamePot gPot = this.getPot(i);
                gPot.reset();
                this.setPot(i, gPot);
            }
            for (Map.Entry<String, GamePlayer> entry : this.playerList.entrySet()) {
                GamePlayer gp = entry.getValue();
                gp.newGame(this.roomId);
                this.setPlayer(entry.getKey(), gp);
            }
            this.purchaseStatus = 0;
            this.moneyDif = 0L;
            this.subBankerList.clear();
            this.gameLog = new StringBuilder("");
            this.finishStep = false;
            this.enableBetting = false;
        } catch (Exception e) {
            String content = "Xoc Dia exception: " + e.getMessage() + ", function: reset() " + this.roomId + " " + this.gameId;
            MsgUtils.alertServer(content, false, false);
            Debug.trace((Object) e);
        }
        this.notifyListUserBE();
    }

    // todo : game loop
    private void notifyListUserBE() { // như bình thường
        cacheService.setValue("Xocdia_Change", "ischange");
        //ArrayList<GamePot> list = new ArrayList<GamePot>(potList);

        cacheService.setValue("XocDia_Pot0", getPotReport(potList.get(0),0).toJson()); // cho nay phai luu dang json vi no ko cung model va no ko serilizable dc do model no
        cacheService.setValue("XocDia_Pot1",  getPotReport(potList.get(1),1).toJson());
        cacheService.setValue("XocDia_Pot2",  getPotReport(potList.get(2),2).toJson());
        cacheService.setValue("XocDia_Pot3",  getPotReport(potList.get(3),3).toJson());
        cacheService.setValue("XocDia_Pot4",  getPotReport(potList.get(4),4).toJson());
        cacheService.setValue("XocDia_Pot5",  getPotReport(potList.get(5),5).toJson());
        try {

            this.sendBEExcutueFlag(); // call đến api module bên kia
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    String sendBEExcutueFlag() throws IOException {
        OkHttpClient client =  HttpCommon.getInstance().getHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(XocDiaConfig.NOTIFY_TO_BACKEND_URL)
                .method("GET", null)
                .build();
      try( Response response = client.newCall(request).execute()){
        return response.body().string();
      }
    }



    private GamePotReportModel getPotReport(GamePot gamePot ,int i){
        GamePotReportModel model1 = new GamePotReportModel();
        model1.betMap =   new HashMap<String, Long>(potList.get(i).betMap);
        model1.totalMoney = potList.get(i).totalMoney;
        model1.totalMoneyBotBet = potList.get(i).totalMoneyBotBet;
        model1.userBetMap =  new HashMap<String, Long>(potList.get(i).userBetMap);
        model1.id = potList.get(i).id;
        model1.totalMoneyUserBet = potList.get(i).totalMoneyUserBet;
        return model1;
    }

    private synchronized void gameLoop() {
        try {
            ++this.countTime;
            cacheService.setValue("XocDia_Flag_Time", String.valueOf(this.getCountTime())); // get all time
            cacheService.setValue("XocDia_Flag_GameState", this.gameState); // get all time
            cacheService.setValue("XocDia_Flag_betting", String.valueOf(this.isBetting));
            cacheService.setValue("XocDia_Flag_session", String.valueOf(this.gameId ));

            // get when join user or betting
            switch (this.countTime) {
                case 0: {
                    this.startNewGame();
                    this.finishStep = true;
                    break;
                }
                case 2: {
                    if (this.finishStep) {
                        this.finishStep = false;
                        this.startBetting();
                        break;
                    }
                    Debug.trace((Object[]) new Object[]{"Waiting START NEW GAME", this.roomId, this.gameId});
                    --this.countTime;
                    break;
                }
                case 20: {
                    this.stopBetting();
                    this.finishStep = true;
                    break;
                }

                case 23: {
                    if (this.finishStep) {
                        this.finishStep = false;
                        this.startReward();
                        this.finishStep = true;
                        break;
                    }
                    Debug.trace((Object[]) new Object[]{"Waiting BALANCE", this.roomId, this.gameId});
                    --this.countTime;
                    break;
                }
                case 33: {
                    if (this.finishStep) {
                        this.finish();
                        break;
                    }
                    Debug.trace((Object[]) new Object[]{"Waiting REWARD", this.roomId, this.gameId});
                    --this.countTime;
                    break;
                }
            }
        } catch (Exception e) {
            String content = "Xoc Dia exception: " + e.getMessage() + ", function: gameLoop() " + this.roomId + " " + this.gameId;
            MsgUtils.alertServer(content, false, true);
            Debug.trace((Object) e);
        }
    }

    private synchronized void bossLoop() {
        try {
            ++this.cntDate;
            if (this.transInDay < XocDiaConfig.transMinInDay) {
                MsgUtils.alertBoss("Ban xoc dia " + this.room.setting.roomName + " khong du " + XocDiaConfig.transMinInDay + " giao dich 1 ngay. Vui long moi nguoi vao choi", this.bankerName);
            }
            this.transInDay = 0;
            if (this.cntDate % 7 == 0) {
                int transMin = XocDiaConfig.getTransMinInWeek((int) this.moneyBet);
                if (this.transInWeek < transMin) {
                    String content = "Ban xoc dia " + this.room.setting.roomName + " da bi huy do khong du " + transMin + " giao dich 1 tuan";
                    MsgUtils.alertBoss(content, this.bankerName);
                    this.reqDestroyGame = true;
                    if (this.gameState == 0) {
                        this.prepareNewGame(true);
                    }
                }
                this.transInWeek = 0;
            }
        } catch (Exception e) {
            String content = "Xoc Dia exception: " + e.getMessage() + ", function: bossLoop() " + this.roomId + " " + this.gameId;
            MsgUtils.alertServer(content, false, true);
            Debug.trace((Object) e);
        }
    }

    private synchronized void botLoop() {
        try {
            switch (this.gameState) {
                case 2: {
                    this.botBetting();
                    break;
                }

            }

        } catch (Exception e) {
            String content = "Xoc Dia exception: " + e.getMessage() + ", function: botLoop() " + this.roomId + " " + this.gameId;
            MsgUtils.alertServer(content, false, true);
            Debug.trace((Object) e);
        }
    }

    private synchronized void destroyLoop() {
        try {
            Debug.trace((Object[]) new Object[]{"DESTROY LOOP", this.roomId, this.gameId});
            this.countTime = -1;
            if (this.task != null && !this.task.isCancelled()) {
                this.task.cancel(false);
            }
            if (this.taskBot != null && !this.taskBot.isCancelled()) {
                this.taskBot.cancel(false);
            }
            this.isRegisterLoop = false;
        } catch (Exception e) {
            String content = "Xoc Dia exception: " + e.getMessage() + ", function: destroyLoop() " + this.roomId + " " + this.gameId;
            MsgUtils.alertServer(content, false, true);
            Debug.trace((Object) e);
        }
    }

    private synchronized void startNewGame() {
        try {
            this.gameId = GameRoomIdGenerator.instance().getId();
            if (this.moneyType == 1) {
                BotXocDiaManager.instance().startNewGame(this.roomId, this.gameId);
            }
            Debug.trace((Object[]) new Object[]{"START NEW GAME", VinPlayUtils.getCurrentDateTime(), this.roomId, this.gameId});
            this.gameLog.append("XDBD<").append(this.moneyType).append(";");
            this.botBettingList.clear();
            this.botPurchaseList.clear();
            this.botReqBankerList.clear();
            this.botSellPot = null;
            this.botReject = null;
            for (Map.Entry<String, GamePlayer> entry : this.playerList.entrySet()) {
                GamePlayer gp = entry.getValue();
                if (!gp.isSitting()) continue;
                this.gameLog.append(entry.getKey()).append("/").append(gp.isBanker ? 1 : 0).append("/").append(gp.getMoneyUseInGame()).append(";");
                if (!this.bCheckBalanceBot) continue;
                this.getBotPlayInGame(gp, this.getPot(2), this.getPot(3), this.getPot(4), this.getPot(5), this.bankerName.isEmpty() && this.roomType == 0 && BotXocDiaManager.instance().checkRequestBanker());
            }
            this.totalReveneu = 0L;
            this.totalFee = 0L;
            this.moneyPurchaseEven = 0L;
            this.moneyPurchaseOdd = 0L;
            this.moneySell = 0L;
            this.moneyBuy = 0L;
            this.potPurchase = -1;
            this.isBankerReject = false;
            this.timeSellPot = 0L;
            this.gameState = 1;
            this.notifyActionGamme((byte) 1, (byte) 5);
            this.notifyStartGame();
        } catch (Exception e) {
            String content = "Xoc Dia exception: " + e.getMessage() + ", function: startNewGame() " + this.roomId + " " + this.gameId;
            MsgUtils.alertServer(content, false, true);
            Debug.trace((Object) e);
        }
    }

    private void getBotPlayInGame(GamePlayer gp, GamePot pot4T, GamePot pot4D, GamePot pot1T, GamePot pot1D, boolean isCheckReqBanker) {
        try {
            if (gp == null) {
                return;
            }
            long moneyUser = gp.getMoneyUseInGame();
            long totalBet = 0L;
            boolean isNext = true;
            if (gp.isBot && !gp.user.getName().equals(this.bankerName) && moneyUser >= (long) this.moneyBet) {
                int betStartTime;
                long money;
                byte potChanLe = 10;
                if (NumberUtils.isDoWithRatio((double) XocDiaConfig.normalRatioBetChanLe)) {
                    potChanLe = (byte) this.rd.nextInt(2);
                    long money2 = 0L;
                    money2 = this.moneyBet == 50000 ? (long) (this.moneyBet * NumberUtils.randomIntLimit((int) XocDiaConfig._100BetChanLeMin, (int) XocDiaConfig._100BetChanLeMax)) : (long) (this.moneyBet * NumberUtils.randomIntLimit((int)500, (int) 1000));
                    if ((totalBet += money2) > moneyUser) {
                        money2 = moneyUser;
                        isNext = false;
                    }
                    int betStartTime2 = NumberUtils.randomIntLimit((int) 6, (int) 13);
                    this.botBettingList.add(new BotBettingModel(gp.user, potChanLe, money2, betStartTime2));
                }
                if (!(!isNext || potChanLe == PotType.EVEN.getId() || !NumberUtils.isDoWithRatio((double) XocDiaConfig.normalRatioBet1) )) {
                    byte potId = (byte) NumberUtils.randomIntLimit((int) 4, (int) 5);

                    money = 0L;
                    money = this.moneyBet == 50000 ? (long) (this.moneyBet * NumberUtils.randomIntLimit((int) XocDiaConfig._100Bet1Min, (int) XocDiaConfig._100Bet1Max)) : (long) (this.moneyBet * NumberUtils.randomIntLimit((int)500, (int) 1000));
                    if ((totalBet += money) > moneyUser) {
                        money = moneyUser - totalBet + money;
                        isNext = false;
                    }
                    betStartTime = NumberUtils.randomIntLimit((int) 6, (int) 13);
                    this.botBettingList.add(new BotBettingModel(gp.user, potId, money, betStartTime));
                }
                if (!(!isNext || potChanLe == PotType.ODD.getId() || !NumberUtils.isDoWithRatio((double) XocDiaConfig.normalRatioBet4))) {
                    byte potId = (byte) NumberUtils.randomIntLimit((int) 2, (int) 3);
//                    if (pot4T.isLock) {
//                        potId = 3;
//                    } else if (pot4D.isLock) {
//                        potId = 2;
//                    }
                    money = 0L;
                    money = this.moneyBet == 100 ? (long) (this.moneyBet * NumberUtils.randomIntLimit((int) XocDiaConfig._100Bet4Min, (int) XocDiaConfig._100Bet4Max)) : (long) (this.moneyBet * NumberUtils.randomIntLimit((int) XocDiaConfig.normalBet4Min, (int) XocDiaConfig.normalBet4Max));
                    if ((totalBet += money) > moneyUser) {
                        money = moneyUser - totalBet + money;
                        isNext = false;
                    }
                    betStartTime = NumberUtils.randomIntLimit((int) 6, (int) 13);
                    this.botBettingList.add(new BotBettingModel(gp.user, potId, money, betStartTime));
                }
                if (isNext && NumberUtils.isDoWithRatio((double) XocDiaConfig.normalRatioBuyPot)) {
                    int moneyBuy = 0;
                    moneyBuy = this.moneyBet == 100 ? NumberUtils.randomIntLimit((int) XocDiaConfig._100BuyPotMin, (int) XocDiaConfig._100BuyPotMax) : NumberUtils.randomIntLimit((int) XocDiaConfig.normalBuyPotMin, (int) XocDiaConfig.normalBuyPotMax);
                    int buyStartTime = NumberUtils.randomIntLimit((int) 37, (int) 44);
                    this.botPurchaseList.add(new BotPurchaseModel(gp.user, moneyBuy, buyStartTime));
                }
                if (isCheckReqBanker && this.botReqBankerList.size() < XocDiaConfig.bkNumBotReqBankerMax && gp.getMoneyUseInGame() > (long) Math.round(XocDiaConfig.bkMoneyRequestBankerMin * this.moneyBet)) {
                    int reqStartTime = NumberUtils.randomIntLimit((int) 6, (int) 65);
                    this.botReqBankerList.add(new BotRequestBankerModel(gp.user, reqStartTime));
                }
            }
            if (this.roomType == 0 && gp.isBot && this.bankerName.equals(gp.user.getName())) {
                boolean isSellPot = NumberUtils.isDoWithRatio((double) XocDiaConfig.bkRatioSellPot);
                int sellStartTime = NumberUtils.randomIntLimit((int) 36, (int) 40);
                this.botSellPot = new BotSellPotModel(gp.user, isSellPot, 0.0, sellStartTime);
                byte action = NumberUtils.isDoWithRatio((double) XocDiaConfig.bkRatioReject) ? (byte) 2 : 1;
                int rejectStartTime = NumberUtils.randomIntLimit((int) 46, (int) 49);
                this.botReject = new BotRejectModel(gp.user, action, rejectStartTime);
            }
        } catch (Exception e) {
            String content = "Xoc Dia exception: " + e.getMessage() + ", function: getBotPlayInGame() " + this.roomId + " " + this.gameId;
            MsgUtils.alertServer(content, false, false);
            Debug.trace((Object) e);
        }
    }

    // todo : bot betting
    private synchronized void botBetting() {
        try {
            for (int i = 0; i < this.botBettingList.size(); ++i) {
                BotBettingModel model;
                if (!NumberUtils.isDoWithRatio((double) XocDiaConfig.ratioBotBettingInGame) || (model = this.botBettingList.get(i)) == null || this.countTime < model.betStartTime)
                    continue;
                if (model.money < (long) this.moneyBet) {
                    this.botBettingList.remove(i);
                    continue;
                }
                long money = this.listCoins.get(this.rd.nextInt(this.listCoins.size())) * this.moneyBet;
                boolean remove = false;
                if (money >= model.money) {
                    money = model.money;
                    remove = true;
                } else {
                    model.money -= money;
                    this.botBettingList.set(i, model);
                }
                if (!this.uBet(model.user, model.potId, money)) {
                    remove = true;
                }
                if (!remove) continue;
                this.botBettingList.remove(i);
            }
        } catch (Exception e) {
            String content = "Xoc Dia exception: " + e.getMessage() + ", function: botBetting() " + this.roomId + " " + this.gameId;
            MsgUtils.alertServer(content, false, false);
            Debug.trace((Object) e);
        }
    }



    private synchronized void startBetting() {
        try {
            Debug.trace((Object[]) new Object[]{"START BETTING", VinPlayUtils.getCurrentDateTime(), this.roomId, this.gameId});
            this.gameLog.append(">").append("XDDC<");
            this.lastBetting = "";
            this.logBettingList.clear();
            this.isBetting = false;
            this.gameState = (byte) 2;
            this.enableBetting = true;
            this.notifyActionGamme((byte) 2, (byte) 20);
        } catch (Exception e) {
            String content = "Xoc Dia exception: " + e.getMessage() + ", function: startBetting() " + this.roomId + " " + this.gameId;
            MsgUtils.alertServer(content, false, true);
            Debug.trace((Object) e);
        }
    }

    private synchronized void stopBetting() {
        try {
            Debug.trace((Object[]) new Object[]{"STOP BETTING", VinPlayUtils.getCurrentDateTime(), this.roomId, this.gameId});
            this.enableBetting = false;
            int logSize = this.logBettingList.size();
            if (logSize > 0) {
                this.isBetting = true;
                for (int i = 0; i < logSize; ++i) {
                    LogBettingModel model = this.logBettingList.poll();
                    this.gameLog.append(model.getNickname()).append("/").append(model.getPotId()).append("/").append(model.getMoney()).append("/").append(model.getType()).append(";");
                }
            }
            this.gameLog.append(">").append("XDDDC<");
            for (GamePot pot : this.potList) {
                this.gameLog.append(pot.id).append("/").append(pot.totalMoney).append(";");
            }
        } catch (Exception e) {
            String content = "Xoc Dia exception: " + e.getMessage() + ", function: stopBetting() " + this.roomId + " " + this.gameId;
            MsgUtils.alertServer(content, false, true);
            Debug.trace((Object) e);
        }
    }

    private void calculateMoneyDif(GamePlayer banker) {
        try {
            long moneyPotEven = this.getPot((int) PotType.EVEN.getId()).totalMoney;
            long moneyPotOdd = this.getPot((int) PotType.ODD.getId()).totalMoney;
            this.moneyDif = moneyPotEven - moneyPotOdd;
            if (banker != null) {
                long moneyBanker = banker.getMoneyUseInGame();
                long moneyEx = moneyBanker + Math.abs(this.moneyDif);
                if (this.moneyDif > 0L) {
                    this.moneyPurchaseEven = moneyEx >= moneyPotEven ? moneyPotEven : moneyEx;
                    this.moneyPurchaseOdd = moneyBanker >= moneyPotOdd ? moneyPotOdd : moneyBanker;
                } else {
                    this.moneyPurchaseOdd = moneyEx >= moneyPotOdd ? moneyPotOdd : moneyEx;
                    this.moneyPurchaseEven = moneyBanker >= moneyPotEven ? moneyPotEven : moneyBanker;
                }
            }
        } catch (Exception e) {
            String content = "Xoc Dia exception: " + e.getMessage() + ", function: calculateMoneyDif() " + this.roomId + " " + this.gameId;
            MsgUtils.alertServer(content, false, true);
            Debug.trace((Object) e);
        }
    }



    private synchronized void startReward() {
        try {
            {
                Debug.trace((Object[]) new Object[]{"START REWARD", VinPlayUtils.getCurrentDateTime(), this.roomId, this.gameId});
                this.gameState = (byte) 6;
                this.gameLog.append(">").append("XDKQ<");
                this.notifyActionGamme((byte) 6, (byte) 10);
                this.reward();
            }
        } catch (Exception e) {
            String content = "Xoc Dia exception: " + e.getMessage() + ", function: startReward() " + this.roomId + " " + this.gameId;
            MsgUtils.alertServer(content, false, true);
            Debug.trace((Object) e);
        }
    }

    private synchronized void finish() {
        try {
            this.gameLog.append(">");
            if (this.isBetting) {
                long logTime = System.currentTimeMillis();
                for (Map.Entry<String, GamePlayer> entry : this.playerList.entrySet()) {
                    GamePlayer gp = entry.getValue();
                    if (!gp.isSitting()) continue;
                    GameUtils.logStartGame((int) this.gameId, (String) entry.getKey(), (long) logTime, (int) this.moneyType);
                }
                GameUtils.logEndGame((int) this.gameId, (String) this.gameLog.toString(), (long) logTime);
            }

            if (this.totalFee > 0L && this.roomType == 2) {
                ++this.transInDay;
                ++this.transInWeek;
            }
            Debug.trace((Object[]) new Object[]{"FINISH", VinPlayUtils.getCurrentDateTime(), this.roomId, this.gameId});
            this.gameState = 0;
            this.prepareNewGame(true);
        } catch (Exception e) {
            String content = "Xoc Dia exception: " + e.getMessage() + ", function: finish() " + this.roomId + " " + this.gameId;
            MsgUtils.alertServer(content, false, true);
            Debug.trace((Object) e);
        }
    }

    public synchronized void onGameMessage(User user, DataCmd dataCmd) {
        try {
            switch (dataCmd.getId()) {
                case 3100: {
                    this.regisLeaveRoom(user, dataCmd);
                    break;
                }

                case 3106: {
                    this.bet(user, dataCmd);
                    break;
                }

                case 3119: {
                    this.getTime(user, dataCmd);
                    break;
                }
                case 3121: {
                    this.getRsList(user, dataCmd);
                    break;
                }
                case 3080: {
                    this.chat(user, dataCmd);
                    break;
                }
                case 3134: {
                 //   this.getRevenue(user, dataCmd);
                    break;
                }
                case 3166 :{
                    this.getCurrentBetMap(user,dataCmd);
                    break;
                }
                case  3213:{
                   this.getListCurrentUser(user);
                    break;
                }
            }
        } catch (Exception e) {
            String content = "Xoc Dia exception: " + e.getMessage() + ", function: onGameMessage() " + this.roomId + " " + this.gameId;
            MsgUtils.alertServer(content, false, false);
            Debug.trace((Object) e);
        }
    }

    private void getListCurrentUser(User user) {
        GetListUserMsg msg = new GetListUserMsg();
        msg.nickName = user.getName();
        msg.playerList = this.playerList;
        MsgUtils.send(msg,user,true);
    }

    private void getCurrentBetMap(User user, DataCmd dataCmd) {
        UserCurrentBetInforMsg msg = new UserCurrentBetInforMsg();

        msg.chan =getMoneyBetOnPot(0,user.getName());
        msg.le =getMoneyBetOnPot(1,user.getName());
        msg.trang4 =getMoneyBetOnPot(2,user.getName());
        msg.den4 =getMoneyBetOnPot(3,user.getName());
        msg.den3 =getMoneyBetOnPot(4,user.getName());
        msg.trang3 =getMoneyBetOnPot(5,user.getName());

        MsgUtils.send(msg, user,true);


    }

    long getMoneyBetOnPot(int index, String nickname){
        if(potList.get(index).betMap.containsKey(nickname)){
            return  potList.get(index).betMap.get(nickname);
        }
        return 0;
    }

    public synchronized void onGameUserEnter(User user) {
        block12:
        {
            try {
                Debug.trace((Object[]) new Object[]{"onGameUserEnter", user.getName(), this.roomId, this.gameId, user.getId()});
                if (this.playerList.size() >= this.maxUsers || this.isLock) break block12;
                PlayerInfo pInfo = PlayerInfo.getInfo((User) user);
                GameMoneyInfo moneyInfo = (GameMoneyInfo) user.getProperty((Object) "GAME_MONEY_INFO");
                GamePlayer gp = new GamePlayer(user, pInfo, moneyInfo);
                if (this.gameState != 0) {
                    gp.playerStatus = 2;
                }
                if (this.emty && this.roomType == 2) {
                    this.emty = false;
                    this.bankerName = gp.user.getName();
                    gp.isBanker = true;
                    gp.isBoss = true;
                    gp.pInfo.setIsHold(true);
                    PlayerInfo.setRoomId((String) user.getName(), (int) this.roomId);
                    this.taskBoss = BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.bossTask, 24, 24, TimeUnit.HOURS);
                    XocDiaBoss boss = BossManager.instance().getBoss(this.roomId);
                    if (boss != null) {
                        this.fundInitial = boss.getFundInitial();
                        Debug.trace((Object) ("BOSS JOIN ROOM roomId: " + this.roomId + ", sessionId: " + boss.getSessionId() + ", nickname: " + boss.getNickname() + ", setting: " + boss.getRoomSetting() + ", fundInitial: " + this.fundInitial));
                        if (BossManager.instance().checkBossSysLogin(this.roomId)) {
                            gp.revMsg = false;
                        }
                    } else {
                        try {
                            boss = new XocDiaBoss(moneyInfo.sessionId, user.getName(), this.roomId, this.room.setting.toString(), moneyInfo.freezeMoney, 1, 0, 0L, VinPlayUtils.getCurrentDateTime());
                            this.fundInitial = boss.getFundInitial();
                            BossManager.instance().putBoss(boss);
                        } catch (Exception e) {
                            this.isLock = true;
                            String content = "Xoc Dia create boss exception: " + e.getMessage() + ", function: onGameUserEnter() " + this.roomId + " " + this.gameId;
                            MsgUtils.alertServer(content, false, true);
                        }
                    }
                }
                this.setPlayer(user.getName(), gp);
                this.notifyUserEnter(gp);
                if (this.playerList.size() == 2) {
                    this.prepareNewGame(false);
                } else if (this.playerList.size() == 1 && user.isBot() && this.bankerName.isEmpty() && this.roomType == 0 && NumberUtils.isDoWithRatio((double) XocDiaConfig.bkRatioRequestBanker) && gp.getMoneyUseInGame() >= (long) Math.round(XocDiaConfig.bkMoneyRequestBankerMin * this.moneyBet)) {
                    //this.botRequestBanker(user);
                }
            } catch (Exception e) {
                String content = "Xoc Dia exception: " + e.getMessage() + ", function: onGameUserEnter() " + this.roomId + " " + this.gameId;
                MsgUtils.alertServer(content, false, true);
                Debug.trace((Object) e);
            }
            this.notifyListUserBE();

        }

    }

    public synchronized void onGameUserDis(User user) {
        try {
            Debug.trace((Object[]) new Object[]{"onGameUserDis", user.getName(), this.roomId, this.gameId});

            GamePlayer gp = this.getPlayer(user.getName());
            if (gp != null && !gp.isPlaying()) {
                this.leaveRoom(user);
            }
        } catch (Exception e) {
            String content = "Xoc Dia exception: " + e.getMessage() + ", function: onGameUserDis() " + this.roomId + " " + this.gameId;
            MsgUtils.alertServer(content, false, false);
            Debug.trace((Object) e);
        }
        this.notifyListUserBE();
    }

    public synchronized void onGameUserReturn(User user) {
        try {
            Debug.trace((Object[]) new Object[]{"onGameUserReturn", user.getName(), this.roomId, this.gameId, user.getId()});
            GamePlayer gp = this.getPlayer(user.getName());
            if (gp != null) {
                gp.revMsg = true;
                gp.user = user;
                gp.reqLeaveRoom = false;
                this.setPlayer(user.getName(), gp);
                this.notifyJoinRoom(gp);
            }
        } catch (Exception e) {
            String content = "Xoc Dia exception: " + e.getMessage() + ", function: onGameUserReturn() " + this.roomId + " " + this.gameId;
            MsgUtils.alertServer(content, false, false);
            Debug.trace((Object) e);
        }
        this.notifyListUserBE();
    }

    public synchronized void onGameUserExit(User user) {
        try {
            Debug.trace((Object[]) new Object[]{"onGameUserExit: ", user.getName(), this.roomId, this.gameId});
            GamePlayer gp = this.getPlayer(user.getName());
            if (gp != null) {
                if (gp.isPlaying()) {
                    gp.reqLeaveRoom = true;
                    this.setPlayer(user.getName(), gp);
                } else {
                    this.removePlayerFromRoom(user);
                }
            }
            if (this.playerList.size() == 0) {
                this.destroy();
            }
        } catch (Exception e) {
            String content = "Xoc Dia exception: " + e.getMessage() + ", function: onGameUserExit() " + this.roomId + " " + this.gameId;
            MsgUtils.alertServer(content, false, false);
            Debug.trace((Object) e);
        }
        this.notifyListUserBE();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void regisLeaveRoom(User user, DataCmd dataCmd) {
        try {
            if (this.isBoss(user.getName())) {
                GamePlayer gBoss = this.getPlayer(this.bankerName);
                if (gBoss != null) {
                    gBoss.revMsg = false;
                    this.setPlayer(this.bankerName, gBoss);
                }
                return;
            }
            Map<String, GamePlayer> gBoss = this.playerList;
            synchronized (gBoss) {
                GamePlayer gp = this.getPlayer(user.getName());
                if (gp != null) {
                    if (gp.isPlaying() || gp.isBanker && this.playerList.size() > 1) {
                        gp.reqLeaveRoom = true;
                        this.setPlayer(user.getName(), gp);
                        this.notifyRegisLeaveRoom(gp);
                    } else {
                        this.leaveRoom(user);
                        this.notifyOutRoom(gp, (byte) 3);
                    }
                }
            }
        } catch (Exception e) {
            String content = "Xoc Dia exception: " + e.getMessage() + ", function: regisLeaveRoom() " + this.roomId + " " + this.gameId;
            MsgUtils.alertServer(content, false, false);
            Debug.trace((Object) e);
        }
        this.notifyListUserBE();
    }


    public void bet(User user, DataCmd dataCmd) {
        try {
             long currentMoney = userService.getCurrentMoneyUserCache(user.getName(), "vin");
            BetCmd cmd = new BetCmd(dataCmd);
            byte potId = cmd.pot;
            long money = cmd.money;
            if(money > currentMoney) return;
            this.uBet(user, potId, money);
        } catch (Exception e) {
            String content = "Xoc Dia exception: " + e.getMessage() + ", function: bet() " + this.roomId + " " + this.gameId;
            MsgUtils.alertServer(content, false, false);
            Debug.trace((Object) e);
        }
        this.notifyListUserBE();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean uBet(User user, byte potId, long money) {
        boolean res = false;
        try {
            Map<String, GamePlayer> map = this.playerList;
            synchronized (map) {
                GamePot gPot = this.getPot(potId);
                GamePlayer gp = this.getPlayer(user.getName());
                if(gp.getCurrentMoney() < money) return false; // check money can bet
                if (this.checkBetting(gPot, gp) && money >= (long) this.moneyBet) { // money bet phai lon hon money bet min
                    BetMsg msg = new BetMsg(user.getName());
                    msg.potId = potId;
                    MoneyResponse response = new MoneyResponse(false, "1001");
                    response = this.userService.updateMoney(user.getName(), -money,  "vin", "XocDia", "xoc dia : \u0110\u1eb7t c\u01b0\u1ee3c", "Phi\u00ean " + this.gameId, 0L, Long.valueOf(this.gameId), TransType.START_TRANS);
                   // MoneyResponse mnres = gp.gameMoneyInfo.updateMoney(-money, this.roomId, this.gameId, 0L, false);
                    if (response.isSuccess()) {
                        gp.setPlaying(this.roomId);
                 //       long moneySub = mnres.getSubtractMoney();
                        long mnBet = gPot.bet(user.getName(), money, false, this.moneyType, gp.isBot);
                        if (mnBet > 0L) {
                            this.setPot(potId, gPot);

                            this.totalReveneu -= money;
                            this.logBetting(user.getName(), potId, money, 0);
                            msg.betMoney = money;
                            msg.currentMoney = userService.getCurrentMoneyUserCache(user.getName(), "vin");
                            msg.potMoney = gPot.totalMoney;
                            msg.Error = 0;
                            MsgUtils.sendToRoom(msg, this.playerList);
                            res = true;
                        } else {
                            msg.currentMoney =userService.getCurrentMoneyUserCache(user.getName(), "vin");;
                            msg.potMoney = gPot.totalMoney;
                            msg.Error = 2;
                            MsgUtils.send(msg, user, gp.revMsg);
                        }
                    } else {
                        msg.currentMoney = userService.getCurrentMoneyUserCache(user.getName(), "vin");;
                        msg.potMoney = gPot.totalMoney;
                        msg.Error = 1;
                        MsgUtils.send(msg, user, gp.revMsg);
                    }
                    this.setPlayer(user.getName(), gp);
                }
            }
        } catch (Exception e) {
            String content = "Xoc Dia exception: " + e.getMessage() + ", function: uBet() " + this.roomId + " " + this.gameId;
            MsgUtils.alertServer(content, false, false);
            Debug.trace((Object) e);
        }
        return res;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    // todo : x2 mone
    public boolean checkBetting(GamePot gPot, GamePlayer gp) {
        if (!(this.gameState != 2 || !this.enableBetting || gp == null || !gp.isSitting() || gPot == null )) {
            if (gPot.checkBetLimitUser(gp.user.getName(), !this.bankerName.isEmpty())) {
                BetMsg msg = new BetMsg(gp.user.getName());
                msg.potId = gPot.id;
                msg.potMoney = gPot.totalMoney;
                msg.Error = 2;
                MsgUtils.send(msg, gp.user, gp.revMsg);
                return false;
            }
            return true;
        }
        return false;
    }



    private void getTime(User user, DataCmd dataCmd) {
        try {
            TimeMsg msg = new TimeMsg();
            msg.time = (byte) this.getCountTime();
            MsgUtils.send(msg, user, true);
        } catch (Exception e) {
            Debug.trace((Object) e);
        }
    }

    private void getRsList(User user, DataCmd dataCmd) {
        try {
            RsListMsg msg = new RsListMsg();
            msg.totalEven = this.totalEven;
            msg.totalOdd = this.totalOdd;
            msg.rsList = this.rsList;
            MsgUtils.send(msg, user, true);
        } catch (Exception e) {
            Debug.trace((Object) e);
        }
    }
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void chat(User user, DataCmd data) {
        try {
            Map<String, GamePlayer> map = this.playerList;
            synchronized (map) {
                long now;
                GamePlayer gp = this.getPlayer(user.getName());
                if (gp != null && (now = System.currentTimeMillis()) - gp.lastChatTime >= 3000L) {
                    gp.lastChatTime = now;
                    this.setPlayer(user.getName(), gp);
                    ChatCmd cmd = new ChatCmd(data);
                    ChatMsg msg = new ChatMsg();
                    msg.nickname = user.getName();
                    msg.isIcon = cmd.isIcon;
                    try {
                        msg.content = URLDecoder.decode(cmd.content, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        Debug.trace((Object) e);
                        msg.content = cmd.content;
                    }
                    MsgUtils.sendToRoom(msg, this.playerList);
                }
            }
        } catch (Exception e) {
            Debug.trace((Object) e);
        }
        this.notifyListUserBE();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */


    private boolean bCheckBalanceBot() {
        return GameUtils.isBot && this.moneyType == 1;
    }

    private XocDiaForceResult checkForceResult() { // todo checkforce
        int forceType = -1;
        XocDiaForceResult xdForce = new XocDiaForceResult(forceType, null);
        try {
            if (this.bCheckBalanceBot) {
                GamePlayer banker;
                boolean hasBanker;
                GamePot potPC;
                boolean isBankerIsBot = true;
                boolean bl = hasBanker = !this.bankerName.isEmpty();

                if (this.moneySell > 0L && this.potPurchase >= 0 && (potPC = this.getPot(this.potPurchase)) != null) {
                    GamePot potRM;
                    int potRMid;
                    long moneyBuyRemain = potPC.getMoneyBuyRemain(isBankerIsBot, this.roomId, this.gameId);
                    this.setPot(this.potPurchase, potPC);
                    if (moneyBuyRemain > 0L && (potRM = this.getPot(potRMid = this.potPurchase == 0 ? 1 : 0)) != null) {
                        potRM.addBetMoney(moneyBuyRemain, isBankerIsBot, this.roomId, this.gameId);
                    }
                }
                ArrayList<XocDiaBetBotModel> potBetList = new ArrayList<XocDiaBetBotModel>();
                long totalUserBet = 0L;
                long totalBotBet = 0L;
                for (GamePot gPot : this.potList) {
                    totalUserBet += gPot.totalMoneyUserBet;
                    totalBotBet += gPot.totalMoneyBotBet;
                    if (isBankerIsBot) {
                        potBetList.add(new XocDiaBetBotModel(gPot.id, gPot.totalMoneyUserBet, gPot.ratio));
                        continue;
                    }
                    potBetList.add(new XocDiaBetBotModel(gPot.id, gPot.totalMoneyBotBet, gPot.ratio));
                }
                boolean bCheckForceTypeForBot = true;
                if (hasBanker) {
                    if (isBankerIsBot) {
                        if (totalUserBet == 0L) {
                            bCheckForceTypeForBot = false;
                        }
                    } else if (totalBotBet == 0L) {
                        bCheckForceTypeForBot = false;
                    }
                } else if (totalBotBet == 0L || totalUserBet == 0L) {
                    bCheckForceTypeForBot = false;
                }
                if (bCheckForceTypeForBot) {
                    xdForce = BotXocDiaManager.instance().getForceResultForBot(this.roomId, this.gameId, isBankerIsBot, hasBanker, potBetList);
                    forceType = xdForce.forceType;
                } else {
                    Debug.trace((Object) (this.roomId + " " + this.gameId + " bCheckForceTypeForBot: " + bCheckForceTypeForBot));
                }
            }
            if (forceType == -1) {
                if (this.cauXD != null) {
                    forceType = this.cauXD.getResult();
                    if (forceType == -1) {
                        this.cauXD = null;
                    } else if (this.moneyType == 1) {
                        Debug.trace((Object) (this.roomId + " " + this.gameId + ", forceByCau: " + forceType));
                    }
                } else {
                    Random rd = new Random();
                    int n = rd.nextInt(XocDiaConfig.numRandomResult);
                    if (n == 0 && XocDiaConfig.listCau.size() > 0) {
                        int cau = rd.nextInt(XocDiaConfig.listCau.size());
                        this.cauXD = new CauXocDia();
                        String data = (String) XocDiaConfig.listCau.get(cau);
                        this.cauXD.setData(data);
                        forceType = this.cauXD.getResult();
                        if (this.moneyType == 1) {
                            Debug.trace((Object) (this.roomId + " " + this.gameId + ", cxd= " + this.cauXD.getData()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            String content = "Xoc Dia exception: " + e.getMessage() + ", function: checkForceResult() " + this.roomId + " " + this.gameId;
            MsgUtils.alertServer(content, false, true);
            Debug.trace((Object) e);
        }
        xdForce.forceType = forceType;
        if (this.moneyType == 1) {
            Debug.trace((Object) (this.roomId + " " + this.gameId + " checkForceResult: " + xdForce.toJson()));
        }
        return xdForce;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void reward() {
        try {
            Map<String, GamePlayer> map = this.playerList;
            synchronized (map) {
                MoneyResponse mnres;
                XocDiaResult xdResult = new XocDiaResult();
                xdResult.generateResult2(this.rsCheat, this.checkForceResult(),this.potList);
                List<Integer> dinces = xdResult.getDinces();

                Iterator<Integer> iterator = dinces.iterator();
                while (iterator.hasNext()) {
                    int dc = iterator.next();
                    this.gameLog.append(dc).append(";");
                }
                this.gameLog.append(">").append("XDTT<");
                List<Byte> potsWin = xdResult.getPotsWin();
                for (Byte bt : potsWin) {
                    GamePot gPot = this.getPot(bt.byteValue());
                    gPot.isWin = true;
                    this.setPot(bt.byteValue(), gPot);
                    if (bt.byteValue() == PotType.EVEN.getId()) {
                        ++this.totalEven;
                        if(rsList.size()>=32){
                            this.rsList.remove(0);
                        }
                        this.rsList.add(bt);

                        continue;
                    }
                    if (bt.byteValue() != PotType.ODD.getId()) continue;
                    ++this.totalOdd;
                    if(rsList.size()>=32){
                        this.rsList.remove(0);
                    }
                    this.rsList.add(bt);
                }
                ResultMsg msg = new ResultMsg();
                msg.dinces = dinces;
                msg.potList = this.potList;
                HashMap<String, RewardModel> rewardMap = new HashMap<String, RewardModel>();
                long moneyBankerExchange = 0L;

                for (GamePot gPot : this.potList) {
                    System.out.println("=========XocDia============nick name"+ gPot.name);
                    System.out.println("=========XocDia============money bet"+ gPot.getMoneyBet(gPot.name));
                    System.out.println("=========XocDia============bet map"+ gPot.betMap);
                    System.out.println("=========XocDia============total money"+ gPot.totalMoney);
                    System.out.println("=========XocDia============user bet map"+ gPot.userBetMap);
                    System.out.println("=========XocDia============bet list"+ gPot.betList);
                    String nickname;

                    long moneyBet;
                    if (gPot.isWin) {
                        for (Map.Entry<String, Long> entry : gPot.betMap.entrySet()) {
                            nickname = entry.getKey();
                            moneyBet = entry.getValue();
                            long moneyWin = Math.round((double) moneyBet * gPot.ratio);
                            long fee = Math.round((double) (moneyWin - moneyBet) * this.feeWin);
                            moneyWin -= fee;
                            RewardModel model = new RewardModel();
                            if (rewardMap.containsKey(nickname)) {
                                model = (RewardModel) rewardMap.get(nickname);
                                model.moneyWin += moneyWin;
                                model.moneyBet += moneyBet;
                                model.fee += fee;
                                model.potsWin = model.potsWin != null ? new StringBuilder(model.potsWin).append(",").append(gPot.id).toString() : String.valueOf(gPot.id);
                                model.moneyWinPots = model.moneyWinPots != null ? new StringBuilder(model.moneyWinPots).append(",").append(moneyWin).toString() : String.valueOf(moneyWin);
                            } else {
                                model.moneyWin = moneyWin;
                                model.moneyBet = moneyBet;
                                model.fee = fee;
                                model.potsWin = String.valueOf(gPot.id);
                                model.moneyWinPots = String.valueOf(moneyWin);
                            }
                            rewardMap.put(nickname, model);
                        }
                        continue;
                    }
                     // tra money win
                    // hoan tien
                    for (Map.Entry<String, Long> entry : gPot.betMap.entrySet()) {
                        nickname = entry.getKey();
                        moneyBet = entry.getValue();
                        RewardModel model = new RewardModel();
                        if (rewardMap.containsKey(nickname)) {
                            model = (RewardModel) rewardMap.get(nickname);
                            model.moneyBet += moneyBet;
                        } else {
                            model.moneyBet = moneyBet;
                        }
                        rewardMap.put(nickname, model);
                    }
                }
                long totalFeeUser = 0L;
                long totalRevenueUser = 0L;
                long moneyBankerBefore = 0L;
                long moneyBankerAfter = 0L;

                Debug.trace((Object[]) new Object[]{"T\u00ednh ti\u1ec1n nh\u00e0 c\u00e1i: " + this.bankerName + " " + moneyBankerExchange, this.roomId, this.gameId});
                ArrayList<SubBanker> subListMsg = new ArrayList<SubBanker>();

                ArrayList<String> removeRW = new ArrayList();
                for (Map.Entry entry : rewardMap.entrySet()) {
                    GamePlayer gPlayer = this.getPlayer((String) entry.getKey());
                    RewardModel model = (RewardModel) entry.getValue();
                    if (model.moneyWin > 0L) {
                        mnres =this.userService.updateMoney(gPlayer.user.getName(),model.moneyWin,  "vin", "XocDia", "xoc dia : Tra thuong ", "Phi\u00ean " + this.gameId, 0L, Long.valueOf(this.gameId), TransType.START_TRANS);;
                        this.totalFee += model.fee;
                        this.totalReveneu += model.moneyWin;
                        model.currentMoney = userService.getCurrentMoneyUserCache(gPlayer.user.getName(), "vin");;
                        this.gameLog.append((String) entry.getKey()).append("/").append(model.moneyWin).append("/").append(mnres.getErrorCode()).append(";");
                        rewardMap.put((String) entry.getKey(), model);
                        this.setPlayer((String) entry.getKey(), gPlayer);
                        Debug.trace((Object[]) new Object[]{"T\u00ednh ti\u1ec1n ng\u01b0\u1eddi ch\u01a1i: " + (String) entry.getKey() + " " + model.moneyWin, this.roomId, this.gameId});
                        if (!gPlayer.isBot) {
                            totalFeeUser += model.fee;
                            totalRevenueUser += model.moneyWin - model.moneyBet;
                        }
                    } else {
                        removeRW.add((String) entry.getKey());
                        if (!gPlayer.isBot) {
                            totalRevenueUser -= model.moneyBet;
                        }
                    }

                }
                if (this.moneyType == 1) {
                    BotXocDiaManager.instance().finishGame(this.roomId, this.gameId, totalFeeUser, totalRevenueUser);
                }
                this.gameLog.append("vinplay").append("/").append("Fee: ").append(this.totalFee).append(". Revenueu: ").append(this.totalReveneu).append("/").append("").append(";");

                for (String nickname : removeRW) { // clean map reward
                    rewardMap.remove(nickname);
                }
                msg.moneyBankerBefore = moneyBankerBefore;
                msg.moneyBankerAfter = moneyBankerAfter;
                msg.moneyBankerExchange = moneyBankerExchange;
                msg.rewardMap = rewardMap;
                msg.subListMsg = subListMsg;
                MsgUtils.sendToRoom(msg, this.playerList);
            }
        } catch (Exception e) {
            String content = "Xoc Dia exception: " + e.getMessage() + ", function: reward() " + this.roomId + " " + this.gameId;
            MsgUtils.alertServer(content, false, true);
            Debug.trace((Object) e);
        }
    }

    private void removePlayerFromRoom(User user) {
        try {
            GamePlayer gp = this.getPlayer(user.getName());
            if (gp != null) {
                GameRoom gameRoom;
                this.playerList.remove(user.getName());
                this.playerListrp.remove(user.getName());
                this.notifyUserLeaveRoom(gp);
                gp.user.removeProperty((Object) "GAME_ROOM");
                gp.user.removeProperty((Object) "GAME_MONEY_INFO");
                gp.pInfo.setIsHold(false);
                if (gp.isBanker) {
                    this.bankerName = "";
                }
                this.restoreMoney(gp.gameMoneyInfo);
                int roomId = BossManager.instance().getRoomIdByBossName(user.getName());
                if (roomId > 0 && (gameRoom = GameRoomManager.instance().getGameRoomById(roomId)) != null) {
                    user.setProperty((Object) "GAME_ROOM", (Object) gameRoom);
                    gp.pInfo.setIsHold(true);
                }
            }
        } catch (Exception e) {
            String content = "Xoc Dia exception: " + e.getMessage() + ", function: removePlayerFromRoom() " + this.roomId + " " + this.gameId;
            MsgUtils.alertServer(content, false, false);
            Debug.trace((Object) e);
        }
        this.notifyListUserBE();
    }

    private void leaveRoom(User user) {
        try {
            this.removePlayerFromRoom(user);
            this.room.group.leaveRoom(user, this.room);
        } catch (Exception e) {
            Debug.trace((Object) e);
        }
        this.notifyListUserBE();
    }

    private void restoreMoney(GameMoneyInfo info) {
        try {
            ListGameMoneyInfo.instance().removeGameMoneyInfo(info, this.roomId);
        } catch (Exception e) {
            Debug.trace((Object) e);
        }
        this.notifyListUserBE();
    }


    private GamePot getPotBigger() {
        if (this.moneyDif != 0L) {
            byte potId = this.moneyDif > 0L ? PotType.EVEN.getId() : PotType.ODD.getId();
            GamePot gPot = this.getPot(potId);
            return gPot;
        }
        return null;
    }

    private int getPotBiggerId() {
        if (this.moneyDif != 0L) {
            if (this.moneyDif > 0L) {
                return PotType.EVEN.getId();
            }
            return PotType.ODD.getId();
        }
        return 100;
    }

    private boolean checkPotEmty() {
        boolean emty = true;
        for (GamePot gPot : this.potList) {
            if (gPot.totalMoney <= 0L) continue;
            emty = false;
            break;
        }
        return emty;
    }

    private synchronized void logBetting(String nickname, int potId, long money, int type) {
        try {
            String key = new StringBuilder(nickname).append(potId).append(type).toString();
            if (key.equals(this.lastBetting)) {
                LogBettingModel model = (LogBettingModel) this.logBettingList.pollLast();
                model.setMoney(model.getMoney() + money);
                this.logBettingList.offer(model);
            } else {
                this.lastBetting = key;
                this.logBettingList.offer(new LogBettingModel(nickname, potId, money, type));
            }
            this.logGameCSV(nickname, money, "XDDC<", new StringBuilder("'").append(type).append(";").append(potId).toString());
        } catch (Exception e) {
            String content = "Xoc Dia exception: " + e.getMessage() + ", function: logBetting() " + this.roomId + " " + this.gameId;
            MsgUtils.alertServer(content, false, false);
            Debug.trace((Object) e);
        }
        this.notifyListUserBE();
    }

    private synchronized void logGameCSV(String nickname, long money, String action, String description) {
        try {
            MoneyLogger.logGame((String) this.sMoneyType, (String) String.valueOf(this.moneyBet), (String) String.valueOf(this.roomId), (String) String.valueOf(this.gameId), (String) nickname, (String) String.valueOf(money), (String) action, (String) description);
        } catch (Exception e) {
            Debug.trace((Object) e);
        }
    }

    private boolean isBoss(String nickname) {
        return this.roomType == 2 && this.bankerName.equals(nickname);
    }

    private GamePlayer getPlayer(String nickname) {
        return this.playerList.get(nickname);
    }

    private void setPlayer(String key, GamePlayer gp) {
        this.playerList.put(key, gp);
        if (!gp.isBot) {
            this.playerListrp.add(key);
        }
    }

    private GamePot getPot(int potId) {
        return this.potList.get(potId);
    }

    private void setPot(int potId, GamePot gPot) {
        this.potList.set(potId, gPot);
    }

    private int getCountTime() {
        int cnt = 0;
        try {
            switch (this.gameState) {
                case 0: {
                    cnt = 0;
                    break;
                }
                case 1: {
                    cnt = 2 - this.countTime;
                    break;
                }
                case 2: {
                    cnt = 35 - this.countTime;
                    break;
                }
                case 3: {
                    cnt = 35 - this.countTime;
                    break;
                }
                case 4: {
                    cnt = 35 - this.countTime;
                    break;
                }
                case 5: {
                    cnt = 35 - this.countTime;
                    break;
                }
                case 6: {
                    cnt = 50 - this.countTime;
                    break;
                }
            }
            if (cnt < 0) {
                cnt = 0;
            }
        } catch (Exception e) {
            Debug.trace((Object) e);
        }
        return cnt;
    }

    private void notifyUserEnter(GamePlayer gamePlayer) {
        try {
            User user = gamePlayer.user;
            UserJoinRoomMsg msg = new UserJoinRoomMsg(gamePlayer.pInfo);
            msg.money = gamePlayer.getMoneyUseInGame();
            MsgUtils.sendExceptMe(msg, user, this.playerList);
            this.notifyJoinRoom(gamePlayer);
        } catch (Exception e) {
            Debug.trace((Object) e);
        }
        this.notifyListUserBE();
    }

    private void notifyJoinRoom(GamePlayer gamePlayer) {
        try {
            GamePlayer banker;
            JoinRoomMsg msg = new JoinRoomMsg();
            msg.me = gamePlayer;
            msg.moneyBet = this.moneyBet;
            msg.roomId = this.roomId;
            msg.gameId = this.gameId;
            msg.moneyType = this.moneyType;
            msg.gameState = this.gameState;
            msg.countTime = this.getCountTime();
            msg.playerList = this.playerList;
            msg.potList = this.potList;
            msg.purchaseStatus = this.purchaseStatus;
            msg.potPurchase = this.potPurchase;
            msg.moneyPurchaseEven = this.moneyPurchaseEven;
            msg.moneyPurchaseOdd = this.moneyPurchaseOdd;
            msg.moneyRemain = this.moneySell - this.moneyBuy;
            msg.subBankerList = this.subBankerList;
            boolean bankerReqDestroy = false;
            boolean bossReqDestroy = false;
            if (!this.bankerName.isEmpty() && (banker = this.getPlayer(this.bankerName)) != null) {
                bankerReqDestroy = banker.reqDestroyBanker;
                if (banker.isBoss) {
                    bossReqDestroy = banker.reqDestroyRoom;
                }
            }
            msg.bankerReqDestroy = bankerReqDestroy;
            msg.bossReqDestroy = bossReqDestroy;
            msg.roomType = this.roomType;
            MsgUtils.send(msg, gamePlayer.user, gamePlayer.revMsg);
        } catch (Exception e) {
            Debug.trace((Object) e);
        }
        this.notifyListUserBE();
    }

    private void notifyUserLeaveRoom(GamePlayer gamePlayer) {
        try {
            UserLeaveRoomMsg msg = new UserLeaveRoomMsg(gamePlayer.user.getName());
            MsgUtils.sendToRoom(msg, this.playerList);
        } catch (Exception e) {
            Debug.trace((Object) e);
        }
        this.notifyListUserBE();
    }

    private void notifyRegisLeaveRoom(GamePlayer gp) {
        try {
            RegisLeaveRoomMsg msg = new RegisLeaveRoomMsg(gp.user.getName());
            msg.reqLeaveRoom = gp.reqLeaveRoom;
            MsgUtils.send(msg,gp.user, gp.revMsg);
        } catch (Exception e) {
            Debug.trace((Object) e);
        }
        this.notifyListUserBE();
    }

    private void notifyOutRoom(GamePlayer gp, byte reason) {
        try {
            Debug.info("ban user " + gp.pInfo.nickName +" reason " + reason);
            LeaveRoomMsg msg = new LeaveRoomMsg();
            msg.reason = reason;
            MsgUtils.send(msg, gp.user, gp.revMsg);
            if (reason == 2 || reason == 1 || reason == 7 || reason == 6) {
                if (reason == 7) {
                    BanUserManager.instance().banUser(this.roomId, gp.user.getName());
                }
                OutRoomMsg outMsg = new OutRoomMsg();
                outMsg.reason = reason;
                MsgUtils.send(outMsg, gp.user, gp.revMsg);
            }
        } catch (Exception e) {
            Debug.trace((Object) e);
        }
        this.notifyListUserBE();
    }

    private void notifyActionGamme(byte action, byte time) {
        try {
            ActionGameMsg msg = new ActionGameMsg();
            msg.action = action;
            msg.time = time;
            MsgUtils.sendToRoom(msg, this.playerList);
        } catch (Exception e) {
            Debug.trace((Object) e);
        }
    }

    private void notifyRequestBankerFail(GamePlayer gp, byte error) {
        try {
            RequestBankerFailMsg msgFail = new RequestBankerFailMsg();
            msgFail.Error = error;
            MsgUtils.send(msgFail, gp.user, gp.revMsg);
        } catch (Exception e) {
            Debug.trace((Object) e);
        }
    }

    private void notifyStartGame() {
        try {
            GamePlayer banker;
            StartGameMsg msg = new StartGameMsg(this.bankerName);
            msg.gameId = this.gameId;
            if (!this.bankerName.isEmpty() && (banker = this.getPlayer(this.bankerName)) != null) {
                msg.moneyBanker = banker.getMoneyUseInGame();
            }
            msg.potList = this.potList;
            MsgUtils.sendToRoom(msg, this.playerList);
        } catch (Exception e) {
            Debug.trace((Object) e);
        }
    }

    private void notifyRequestBanker(GamePlayer gp, byte error) {
        try {
            RequestBankerMsg msg = new RequestBankerMsg();
            msg.Error = error;
            MsgUtils.send(msg, gp.user, gp.revMsg);
        } catch (Exception e) {
            Debug.trace((Object) e);
        }
    }

    private void notifyDestroyBanker(boolean reqDestroyBanker) {
        try {
            DestroyBankerMsg msg = new DestroyBankerMsg(this.bankerName);
            msg.bDestroy = reqDestroyBanker;
            MsgUtils.sendToRoom(msg, this.playerList);
        } catch (Exception e) {
            Debug.trace((Object) e);
        }
    }

    private void notifyStopGame() {
        try {
            StopGameMsg msg = new StopGameMsg(this.bankerName);
            MsgUtils.sendToRoom(msg, this.playerList);
        } catch (Exception e) {
            Debug.trace((Object) e);
        }
    }

    public void onNoHu(ThongTinThangLon info) {
    }

    public void choNoHu(String nickName) {
    }

    public String toString() {
        try {
            JSONObject json = this.toJONObject();
            if (json != null) {
                return json.toString();
            }
            return "{}";
        } catch (Exception e) {
            return "{}";
        }
    }

    public JSONObject toJONObject() {
        try {
            JSONObject json = new JSONObject();
            json.put("gameId", this.gameId);
            json.put("gameState", (int) this.gameState);
            json.put("countTime", this.countTime);
            json.put("banker", (Object) this.bankerName);
            json.put("moneyDif", this.moneyDif);
            json.put("enableBetting", this.enableBetting);
            json.put("isBetting", this.isBetting);
            json.put("purchaseStatus", this.purchaseStatus);
            json.put("moneyPurchaseEven", this.moneyPurchaseEven);
            json.put("moneyPurchaseOdd", this.moneyPurchaseOdd);
            json.put("moneySell", this.moneySell);
            json.put("moneyBuy", this.moneyBuy);
            json.put("potPurchase", this.potPurchase);
            json.put("isBankerReject", this.isBankerReject);
            json.put("finishStep", this.finishStep);
            json.put("gameLog", (Object) this.gameLog);
            int i = 1;
            for (Map.Entry<String, GamePlayer> entry : this.playerList.entrySet()) {
                json.put("_u" + i + "_" + entry.getKey(), (Object) entry.getValue().toString());
                ++i;
            }
            for (GamePot gp : this.potList) {
                json.put("_p" + gp.id + "_" + gp.name, (Object) gp.toString());
            }
            return json;
        } catch (Exception e) {
            return null;
        }
    }

    public void destroy() {
        try {
            Debug.trace((Object[]) new Object[]{"DESTROY GAME", this.roomId, this.gameId});
            this.purchaseStatus = 0;
            this.moneyDif = 0L;
            this.finishStep = false;
            this.enableBetting = false;
            this.gameState = 0;
            this.gameId = 0;
            this.rsList.clear();
            this.totalEven = 0;
            this.totalOdd = 0;
            this.bankerName = "";
            this.subBankerList.clear();
            this.reqBankerList.clear();
            this.gameLog = new StringBuilder("");
            this.cauXD = null;
            this.transInDay = 0;
            this.transInWeek = 0;
            this.cntDate = 0;
            this.destroyLoop();
            BanUserManager.instance().removeBanList(this.roomId);
            if (this.roomType == 2) {
                if (this.taskBoss != null && !this.taskBoss.isCancelled()) {
                    this.taskBoss.cancel(false);
                }
                GameRoomManager.instance().destroyGameRoom(this.roomId);
            }
        } catch (Exception e) {
            String content = "Xoc Dia exception: " + e.getMessage() + ", function: destroy() " + this.roomId + " " + this.gameId;
            MsgUtils.alertServer(content, false, false);
            Debug.trace((Object) e);
        }
    }

    private final class BotTask
            implements Runnable {
        @Override
        public void run() {
            try {
                XocDiaGameServer.this.botLoop();
            } catch (Exception e) {
                Debug.trace((Object) e);
            }
        }
    }

    private final class BossTask
            implements Runnable {
        @Override
        public void run() {
            try {
                XocDiaGameServer.this.bossLoop();
            } catch (Exception e) {
                Debug.trace((Object) e);
            }
        }
    }

    private final class GameLoopTask
            implements Runnable {
        @Override
        public void run() {
            try {
                XocDiaGameServer.this.gameLoop();
            } catch (Exception e) {
                Debug.trace((Object) e);
            }
        }
    }

}

