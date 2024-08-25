// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server;

import java.util.*;
import java.util.concurrent.TimeUnit;

import bitzero.server.BitZeroServer;
import game.modules.bot.Bot;
import game.binh.server.cmd.receive.RevCheatCard;
import game.binh.server.cmd.send.SendUpdateMatch;
import game.binh.server.cmd.send.SendKickRoom;
import game.binh.server.logic.PlayerCard;
import game.binh.server.logic.SoSanhChi;
import game.binh.server.logic.KetQuaTinhSap;
import game.modules.gameRoom.config.GameRoomConfig;
import game.binh.server.cmd.send.SendEndGame;
import bitzero.server.core.IBZEvent;
import bitzero.server.core.IBZEventType;
import bitzero.server.core.BZEvent;
import game.eventHandlers.GameEventType;
import game.eventHandlers.GameEventParam;

import com.vinplay.vbee.common.statics.TransType;
import game.modules.bot.BotManager;
import game.modules.gameRoom.entities.MoneyException;
import game.entities.UserScore;
import game.binh.server.cmd.send.SendNotifyReqQuitRoom;
import game.binh.server.cmd.send.SendGameInfo;
import game.binh.server.cmd.send.SendUserExitRoom;
import game.binh.server.cmd.send.SendJoinRoomSuccess;
import game.binh.server.cmd.send.SendNewUserJoin;
import game.binh.server.logic.KetQuaSoBai;
import game.utils.GameUtils;
import game.binh.server.cmd.send.SendDealCard;
import bitzero.util.ExtensionUtility;
import game.binh.server.cmd.send.SendUpdateOwnerRoom;

import bitzero.util.common.business.CommonHandle;
import game.modules.gameRoom.cmd.send.SendNoHu;
import game.entities.PlayerInfo;
import game.modules.gameRoom.entities.ListGameMoneyInfo;
import game.modules.gameRoom.entities.GameMoneyInfo;
import game.modules.gameRoom.entities.GameRoomManager;
import game.binh.server.logic.ai.BinhAuto;
import game.modules.gameRoom.entities.GameRoom;
import game.utils.LoggerUtils;
import game.binh.server.logic.GroupCard;
import game.binh.server.cmd.receive.RevBinhSoChi;
import game.binh.server.logic.BinhRule;
import game.binh.server.cmd.send.SendBinhSoChiSuccess;
import bitzero.server.extensions.data.BaseMsg;
import game.binh.server.cmd.send.SendXepLai;
import bitzero.server.extensions.data.DataCmd;
import bitzero.server.entities.User;

import org.json.JSONArray;
import org.json.JSONObject;
import game.modules.gameRoom.entities.ThongTinThangLon;

import java.util.concurrent.ScheduledFuture;

import game.modules.gameRoom.entities.GameServer;

public class BinhGameServer extends GameServer {
    public volatile boolean isRegisterLoop;
    private ScheduledFuture<?> task;
    public static final int gsNoPlay = 0;
    public static final int gsPlay = 1;
    public static final int gsResult = 2;
    private final GameManager gameMgr;
    public final Vector<GamePlayer> playerList;
    public int playingCount;
    private volatile int serverState;
    public volatile int playerCount;
    public ThongTinThangLon thongTinNoHu;
    StringBuilder gameLog;
    private final Runnable gameLoopTask;

    public BinhGameServer() {
        this.isRegisterLoop = false;
        this.gameMgr = new GameManager();
        this.playerList = new Vector<GamePlayer>(4);
        this.playingCount = 0;
        this.serverState = 0;
        this.thongTinNoHu = null;
        this.gameLog = new StringBuilder();
        this.gameLoopTask = new GameLoopTask();
    }

    public String toString() {
        try {
            final JSONObject json = this.toJONObject();
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
            final JSONObject json = new JSONObject();
            json.put("gameState", this.gameMgr.gameState);
            json.put("gameAction", this.gameMgr.gameAction);
            final JSONArray arr = new JSONArray();
            for (int i = 0; i < 4; ++i) {
                final GamePlayer gp = this.getPlayerByChair(i);
                arr.put((Map) gp.toJSONObject());
            }
            json.put("players", (Object) arr);
            return json;
        } catch (Exception e) {
            return null;
        }
    }

    public synchronized void onGameMessage(final User user, final DataCmd dataCmd) {
        switch (dataCmd.getId()) {
            case 3101: {
                this.soChi(user, dataCmd);
                break;
            }
            case 3111: {
                this.pOutRoom(user, dataCmd);
                break;
            }
            case 3102: {
                this.pBatDau(user, dataCmd);
                break;
            }
            case 3115: {
                this.pCheatCards(user, dataCmd);
                break;
            }
            case 3116: {
                this.pDangKyChoiTiep(user, dataCmd);
                break;
            }
            case 3104: {
                this.binhSoChiTuDong(user, dataCmd);
                break;
            }
            case 3106: {
                this.baoBinh(user, dataCmd);
                break;
            }
            case 3108: {
                this.xepLai(user, dataCmd);
                break;
            }
        }
    }

    private void xepLai(final User user, final DataCmd cmd) {
        if (this.gameMgr.gameAction != 2) {
            return;
        }
        final GamePlayer gp = this.getPlayerByUser(user);
        if (gp != null) {
            gp.sochi = false;
            final SendXepLai msg = new SendXepLai();
            msg.chair = gp.chair;
            this.send(msg);
        }
    }

    private void baoBinh(final User user, final DataCmd dataCmd) {
        if (this.gameMgr.gameAction != 2) {
            return;
        }
        final GamePlayer gp = this.getPlayerByUser(user);
        final SendBinhSoChiSuccess msg = new SendBinhSoChiSuccess();
        if (gp != null) {
            msg.chair = gp.chair;
            final int kind = gp.spInfo.autoSort(this.room.setting.rule);
            if (BinhRule.isMauBinh(kind)) {
                this.send(msg);
                gp.sochi = true;
                this.kiemTraHoanThanhSoChi();
                this.logSoChi(gp, true);
            } else {
                msg.Error = 1;
                this.send((BaseMsg) msg, user);
            }
        } else {
            msg.Error = 2;
            this.send((BaseMsg) msg, user);
        }
    }

    private void soChi(final User user, final DataCmd dataCmd) {
        try {
            if (this.gameMgr.gameAction != 2) {
                return;
            }
            final RevBinhSoChi cmd = new RevBinhSoChi(dataCmd);
            final GamePlayer gp = this.getPlayerByUser(user);
            final SendBinhSoChiSuccess msg = new SendBinhSoChiSuccess();
            if (gp == null) {
                msg.Error = 2;
                this.send(msg, user);
                return;
            }
            msg.chair = gp.chair;
            final GroupCard chi1 = new GroupCard(cmd.chi1);
            chi1.kiemtraBo(this.room.setting.rule);
            final GroupCard chi2 = new GroupCard(cmd.chi2);
            chi2.kiemtraBo(this.room.setting.rule);
            final GroupCard chi3 = new GroupCard(cmd.chi3);
            chi3.kiemtraBo(this.room.setting.rule);
            final boolean checkValidCard = gp.spInfo.checkCardValid(chi1, chi2, chi3);
            if (checkValidCard) {
                gp.spInfo.sorttedCard.ApplyNew3GroupCards(chi1, chi2, chi3, this.room.setting.rule);
                gp.kiemTraMauBinh(this.room.setting.rule);
                this.send(msg);
                gp.sochi = true;
                this.kiemTraHoanThanhSoChi();
                this.logSoChi(gp, false);
            } else {
                msg.Error = 1;
                LoggerUtils.error("binh", new Object[]{"so chi ERROR", chi1, chi2, chi3});
                this.send(msg, user);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void init(final GameRoom ro) {
        this.room = ro;
        this.gameMgr.gameServer = this;
        int i = 0;
        while (i < 4) {
            final GamePlayer gp = new GamePlayer();
            gp.chair = i++;
            this.playerList.add(gp);
        }
        BinhAuto.instance();
        this.init();
    }

    public GameManager getGameManager() {
        return this.gameMgr;
    }

    public int getServerState() {
        return this.serverState;
    }

    public GamePlayer getPlayerByChair(final int i) {
        if (i >= 0 && i < 4) {
            return this.playerList.get(i);
        }
        return null;
    }

    public long getMoneyBet() {
        return this.gameMgr.gameServer.room.setting.moneyBet;
    }

    public byte getPlayerCount() {
        return (byte) this.playerCount;
    }

    public boolean checkPlayerChair(final int chair) {
        return chair >= 0 && chair < 4;
    }

    public synchronized void onGameUserDis(final User user) {
        final Integer chair = (Integer) user.getProperty((Object) "user_chair");
        if (chair == null) {
            return;
        }
        final GamePlayer gp = this.getPlayerByChair(chair);
        if (gp == null) {
            return;
        }
        if (gp.isPlaying()) {
            gp.reqQuitRoom = true;
            final GamePlayer gamePlayer = gp;
            ++gamePlayer.tuDongChoi;
            this.gameLog.append("DIS<").append(chair).append(">");
        } else {
            GameRoomManager.instance().leaveRoom(user, this.room);
        }
    }

    public synchronized void onGameUserExit(final User user) {
        final Integer chair = (Integer) user.getProperty((Object) "user_chair");
        if (chair == null) {
            return;
        }
        final GamePlayer gp = this.getPlayerByChair(chair);
        if (gp == null) {
            return;
        }
        if (gp.isPlaying()) {
            gp.reqQuitRoom = true;
            final GamePlayer gamePlayer = gp;
            ++gamePlayer.tuDongChoi;
            this.gameLog.append("DIS<").append(chair).append(">");
        } else {
            final boolean disconnect;
            this.removePlayerAtChair(chair, !(disconnect = user.isConnected()));
        }
        if (this.room.userManager.size() == 0) {
            this.resetPlayDisconnect();
            this.destroy();
        }
    }

    public void resetPlayDisconnect() {
        for (int i = 0; i < 4; ++i) {
            final GamePlayer gp = this.getPlayerByChair(i);
            if (gp.pInfo != null) {
                gp.pInfo.setIsHold(false);
            }
        }
    }

    public synchronized void onGameUserReturn(final User user) {
        if (user == null) {
            return;
        }
        if (this.room.setting.maxUserPerRoom == 4) {
            for (int i = 0; i < 4; ++i) {
                final GamePlayer gp = this.playerList.get(i);
                if (gp.getPlayerStatus() != 0 && gp.pInfo != null && gp.pInfo.nickName.equalsIgnoreCase(user.getName())) {
                    this.gameLog.append("RE<").append(i).append(">");
                    final GameMoneyInfo moneyInfo = (GameMoneyInfo) user.getProperty((Object) "GAME_MONEY_INFO");
                    if (moneyInfo != null && gp.gameMoneyInfo.sessionId != moneyInfo.sessionId) {
                        ListGameMoneyInfo.instance().removeGameMoneyInfo(moneyInfo, -1);
                    }
                    user.setProperty((Object) "user_chair", (Object) gp.chair);
                    gp.user = user;
                    gp.tuDongChoi = 0;
                    gp.reqQuitRoom = false;
                    user.setProperty((Object) "GAME_MONEY_INFO", (Object) gp.gameMoneyInfo);
                    this.sendGameInfo(gp.chair);
                    return;
                }
            }
        }
        user.removeProperty((Object) "GAME_ROOM");
    }

    public synchronized void onGameUserEnter(final User user) {
        if (user == null) {
            return;
        }
        final PlayerInfo pInfo = PlayerInfo.getInfo(user);
        if (pInfo == null) {
            return;
        }
        final GameMoneyInfo moneyInfo = (GameMoneyInfo) user.getProperty("GAME_MONEY_INFO");
        if (moneyInfo == null) {
            return;
        }
        for (int i = 0; i < 4; ++i) {
            final GamePlayer gp = this.playerList.get(i);
            if (gp.getPlayerStatus() != GamePlayer.psNO_LOGIN && gp.pInfo != null && gp.pInfo.nickName.equalsIgnoreCase(user.getName())) {
                this.gameLog.append("RE<").append(i).append(">");
                if (!Objects.equals(gp.gameMoneyInfo.sessionId, moneyInfo.sessionId)) {
                    ListGameMoneyInfo.instance().removeGameMoneyInfo(moneyInfo, -1);
                }
                user.setProperty("user_chair", gp.chair);
                gp.user = user;
                gp.tuDongChoi = 0;
                gp.reqQuitRoom = false;
                user.setProperty("GAME_MONEY_INFO", gp.gameMoneyInfo);
                if (this.serverState == 1) {
                    this.sendGameInfo(gp.chair);
                } else {
                    this.notifyUserEnter(gp);
                }
                return;
            }
        }
        for (int i = 0; i < 4; ++i) {
            final GamePlayer gp = this.playerList.get(i);
            if (gp.getPlayerStatus() == GamePlayer.psNO_LOGIN) {
                if (this.serverState == gsNoPlay) {
                    gp.setPlayerStatus(GamePlayer.psSIT);
                } else {
                    gp.setPlayerStatus(GamePlayer.psVIEW);
                }
                gp.takeChair(user, pInfo, moneyInfo);
                ++this.playerCount;
                if (this.playerCount == 1) {
                    this.gameMgr.roomCreatorUserId = user.getId();
                    this.gameMgr.roomOwnerChair = i;
                    this.init();
                }
                this.notifyUserEnter(gp);
                break;
            }
        }
        this.kiemTraTuDongBatDau(5);
    }

    public synchronized void onNoHu(final ThongTinThangLon info) {
        this.thongTinNoHu = info;
    }

    public void notifyNoHu() {
        try {
            if (this.thongTinNoHu != null) {
                for (int i = 0; i < 4; ++i) {
                    final GamePlayer gp = this.getPlayerByChair(i);
                    if (gp.gameMoneyInfo != null && gp.gameMoneyInfo.sessionId.equalsIgnoreCase(this.thongTinNoHu.moneySessionId) && gp.gameMoneyInfo.nickName.equalsIgnoreCase(this.thongTinNoHu.nickName)) {
                        gp.gameMoneyInfo.currentMoney = this.thongTinNoHu.currentMoney;
                        break;
                    }
                }
                final SendNoHu msg = new SendNoHu();
                msg.info = this.thongTinNoHu;
                for (final Map.Entry entry : this.room.userManager.entrySet()) {
                    final User u = (User) entry.getValue();
                    if (u == null) {
                        continue;
                    }
                    this.send((BaseMsg) msg, u);
                }
            }
        } catch (Exception e) {
            CommonHandle.writeErrLog((Throwable) e);
        } finally {
            this.thongTinNoHu = null;
        }
    }

    public void updateOwnerRoom(final int chair) {
        final SendUpdateOwnerRoom msg = new SendUpdateOwnerRoom();
        msg.ownerChair = chair;
        this.send(msg);
    }

    public int getNumTotalPlayer() {
        return this.playerCount;
    }

    public void sendMsgToPlayingUser(final BaseMsg msg) {
        for (int i = 0; i < 4; ++i) {
            final GamePlayer gp = this.getPlayerByChair(i);
            if (gp.isPlaying()) {
                this.send(msg, gp.getUser());
            }
        }
    }

    public void send(final BaseMsg msg) {
        for (int i = 0; i < 4; ++i) {
            final GamePlayer gp = this.getPlayerByChair(i);
            if (gp.getUser() != null) {
                ExtensionUtility.getExtension().send(msg, gp.getUser());
            }
        }
    }

    public void chiabai() {
        this.gameLog.append("CB<");
        final SendDealCard msg = new SendDealCard();
        msg.gameId = this.gameMgr.game.id;
        for (int i = 0; i < 4; ++i) {
            final GamePlayer gp = this.playerList.get(i);
            if (gp.isPlaying()) {
                final User user = gp.getUser();
                msg.cards = gp.spInfo.handCards.toByteArray();
                if (!user.isBot()) {
                    msg.maubinh = gp.kiemTraMauBinh(this.room.setting.rule);
                } else {
                    final GroupCard gc = new GroupCard(gp.getHandCards());
                    final GroupCard handCards = gp.spInfo.handCards;
                    final SendDealCard sendDealCard = msg;
                    final int kiemtraBo = gc.kiemtraBo(this.room.setting.rule);
                    sendDealCard.maubinh = kiemtraBo;
                    handCards.BO = kiemtraBo;
                }
                this.gameLog.append(gp.chair).append("/");
                this.gameLog.append(gp.spInfo.handCards.toString()).append("/");
                this.gameLog.append(msg.maubinh).append(";");
                this.send((BaseMsg) msg, user);
            }
        }
        this.gameLog.append(">");
    }

    public void start() {
        this.gameLog.setLength(0);
        this.gameLog.append("BD<");
        this.playingCount = 0;
        this.serverState = 1;
        for (int i = 0; i < 4; ++i) {
            final GamePlayer gp = this.getPlayerByChair(i);
            gp.tuDongChoi = 0;
            if (this.coTheChoiTiep(gp)) {
                gp.setPlayerStatus(3);
                ++this.playingCount;
                gp.pInfo.setIsHold(true);
                PlayerInfo.setRoomId(gp.pInfo.nickName, this.room.getId());
                this.gameLog.append(gp.pInfo.nickName).append("/");
                this.gameLog.append(i).append(";");
                gp.choiTiepVanSau = false;
            }
        }
        this.gameLog.append(this.room.setting.moneyType);
        this.gameLog.append(">");
        this.logStartGame();
        this.gameMgr.gameAction = 1;
        this.gameMgr.countDown = 0;
        this.botStartGame();
    }

    private void logStartGame() {
        for (int i = 0; i < 4; ++i) {
            final GamePlayer gp = this.getPlayerByChair(i);
            if (gp.isPlaying()) {
                GameUtils.logStartGame(this.gameMgr.game.id, gp.pInfo.nickName, this.gameMgr.game.logTime, this.room.setting.moneyType);
            }
        }
    }

    private void logEndGame() {
        this.gameLog.append("KT<");
        this.gameLog.append(0).append(";");
        for (int i = 0; i < 4; ++i) {
            final GamePlayer gp = this.getPlayerByChair(i);
            if (gp.isPlaying()) {
                final KetQuaSoBai kq = gp.spRes.getResultWithPlayer(gp.chair);
                this.gameLog.append(gp.chair).append("/").append(kq.moneyCommon).append("/").append(gp.spInfo.sorttedCard.fullCard).append(";");
            }
        }
        this.gameLog.append(">");
        GameUtils.logEndGame(this.gameMgr.game.id, this.gameLog.toString(), this.gameMgr.game.logTime);
    }

    public int demSoNguoiChoiTiep() {
        int count = 0;
        for (int i = 0; i < 4; ++i) {
            final GamePlayer gp = this.getPlayerByChair(i);
            if (this.coTheChoiTiep(gp)) {
                ++count;
            }
        }
        return count;
    }

    public int demSoNguoiDangChoi() {
        int count = 0;
        for (int i = 0; i < 4; ++i) {
            final GamePlayer gp = this.getPlayerByChair(i);
            ++count;
        }
        return count;
    }

    public void kiemTraTuDongBatDau(final int after) {
        if (this.gameMgr.gameState == 0) {
            if (this.demSoNguoiChoiTiep() < 2) {
                this.gameMgr.cancelAutoStart();
            } else {
                this.gameMgr.makeAutoStart(after);
            }
        }
    }

    private boolean coTheChoiTiep(final GamePlayer gp) {
        return gp.hasUser() && gp.canPlayNextGame();
    }

    private synchronized void removePlayerAtChair(final int chair, final boolean disconnect) {
        if (!this.checkPlayerChair(chair)) {
            return;
        }
        final GamePlayer gp = this.playerList.get(chair);
        gp.choiTiepVanSau = true;
        this.notifyUserExit(gp, disconnect);
        if (gp.user != null) {
            gp.user.removeProperty((Object) "user_chair");
            gp.user.removeProperty((Object) "GAME_ROOM");
            gp.user.removeProperty((Object) "GAME_MONEY_INFO");
        }
        gp.user = null;
        gp.pInfo = null;
        if (gp.gameMoneyInfo != null) {
            ListGameMoneyInfo.instance().removeGameMoneyInfo(gp.gameMoneyInfo, this.room.getId());
        }
        gp.gameMoneyInfo = null;
        gp.setPlayerStatus(0);
        gp.boSoChi = 0;
        --this.playerCount;
        this.kiemTraTuDongBatDau(5);
    }

    private void notifyUserEnter(final GamePlayer gamePlayer) {
        final User user = gamePlayer.getUser();
        if (user == null) {
            return;
        }
        final SendNewUserJoin msg = new SendNewUserJoin();
        msg.money = gamePlayer.gameMoneyInfo.currentMoney;
        msg.uStatus = gamePlayer.getPlayerStatus();
        msg.setBaseInfo(gamePlayer.pInfo);
        msg.uChair = gamePlayer.chair;
        this.sendMsgExceptMe((BaseMsg) msg, user);
        this.notifyJoinRoomSuccess(gamePlayer);
    }

    public void notifyJoinRoomSuccess(final GamePlayer gamePlayer) {
        final SendJoinRoomSuccess msg = new SendJoinRoomSuccess();
        msg.uChair = gamePlayer.chair;
        msg.roomId = this.room.getId();
        msg.moneyType = this.gameMgr.gameServer.room.setting.moneyType;
        msg.gameId = this.gameMgr.game.id;
        msg.moneyBet = this.gameMgr.gameServer.room.setting.moneyBet;
        msg.rule = this.room.setting.rule;
        for (int i = 0; i < 4; ++i) {
            final GamePlayer gp = this.getPlayerByChair(i);
            msg.playerStatus[i] = (byte) gp.getPlayerStatus();
            msg.playerList[i] = gp.getPlayerInfo();
            msg.moneyInfoList[i] = gp.gameMoneyInfo;
        }
        msg.gameState = (byte) this.gameMgr.gameState;
        msg.gameAction = (byte) this.gameMgr.gameAction;
        msg.countDownTime = (byte) this.gameMgr.countDown;
        this.send((BaseMsg) msg, gamePlayer.getUser());
    }

    private void notifyUserExit(final GamePlayer gamePlayer, final boolean disconnect) {
        if (gamePlayer.pInfo != null) {
            gamePlayer.pInfo.setIsHold(false);
            final SendUserExitRoom msg = new SendUserExitRoom();
            msg.nChair = (byte) gamePlayer.chair;
            msg.nickName = gamePlayer.pInfo.nickName;
            this.send(msg);
        }
    }

    public GamePlayer getPlayerByUser(final User user) {
        final Integer chair = (Integer) user.getProperty((Object) "user_chair");
        if (chair == null) {
            return null;
        }
        final GamePlayer gp = this.getPlayerByChair(chair);
        if (gp != null && gp.pInfo != null && gp.pInfo.nickName.equalsIgnoreCase(user.getName())) {
            return gp;
        }
        return null;
    }

    private void sendGameInfo(final int chair) {
        final GamePlayer gamePlayer = this.getPlayerByChair(chair);
        final SendGameInfo msg = new SendGameInfo();
        msg.gameState = this.gameMgr.gameState;
        msg.gameAction = this.gameMgr.gameAction;
        msg.countdownTime = this.gameMgr.countDown;
        msg.chair = (byte) gamePlayer.chair;
        msg.roomId = this.room.getId();
        msg.rule = this.room.setting.rule;
        msg.gameId = this.gameMgr.game.id;
        msg.moneyBet = this.getMoneyBet();
        msg.moneyType = this.room.setting.moneyType;
        for (int i = 0; i < 4; ++i) {
            final GamePlayer gp = this.getPlayerByChair(i);
            if (gp.hasUser()) {
                msg.pInfos[i] = gp;
                msg.hasInfoAtChair[i] = true;
            } else {
                msg.hasInfoAtChair[i] = false;
            }
        }
        this.send((BaseMsg) msg, gamePlayer.getUser());
    }

    private void pOutRoom(final User user, final DataCmd dataCmd) {
        final GamePlayer gp = this.getPlayerByUser(user);
        this.pOutRoom(gp);
    }

    private void pOutRoom(final GamePlayer gp) {
        if (gp != null) {
            if (gp.getPlayerStatus() == 3) {
                gp.reqQuitRoom = !gp.reqQuitRoom;
                this.notifyRegisterOutRoom(gp);
            } else {
                GameRoomManager.instance().leaveRoom(gp.getUser(), this.room);
            }
        }
    }

    private void notifyRegisterOutRoom(final GamePlayer gp) {
        final SendNotifyReqQuitRoom msg = new SendNotifyReqQuitRoom();
        msg.chair = (byte) gp.chair;
        msg.reqQuitRoom = gp.reqQuitRoom;
        this.send(msg);
    }

    private void logSoChi(final GamePlayer gp, final boolean baoBinh) {
        if (baoBinh) {
            this.gameLog.append("BB<");
        } else {
            this.gameLog.append("SC<");
        }
        this.gameLog.append(gp.chair).append(";");
        this.gameLog.append(gp.spInfo.getKind(this.room.setting.rule)).append(";");
        this.gameLog.append(gp.spInfo.sorttedCard.ChiMot()).append(";");
        this.gameLog.append(gp.spInfo.sorttedCard.ChiHai()).append(";");
        this.gameLog.append(gp.spInfo.sorttedCard.ChiBa()).append(">");
    }

    private void binhSoChiTuDong(final User user, final DataCmd dataCmd) {
        if (this.gameMgr.gameAction != 2) {
            return;
        }
        final RevBinhSoChi cmd = new RevBinhSoChi(dataCmd);
        final GamePlayer gp = this.getPlayerByUser(user);
        if (gp == null) {
            return;
        }
        final GroupCard chi1 = new GroupCard(cmd.chi1);
        chi1.kiemtraBo(this.room.setting.rule);
        final GroupCard chi2 = new GroupCard(cmd.chi2);
        chi2.kiemtraBo(this.room.setting.rule);
        final GroupCard chi3 = new GroupCard(cmd.chi3);
        chi3.kiemtraBo(this.room.setting.rule);
        final boolean checkValidCard = gp.spInfo.checkCardValid(chi1, chi2, chi3);
        if (checkValidCard) {
            gp.spInfo.sorttedCard.ApplyNew3GroupCards(chi1, chi2, chi3, this.room.setting.rule);
            gp.kiemTraMauBinh(this.room.setting.rule);
            if (gp.spInfo.sorttedCard.kiemTraBinhLung(this.room.setting.rule)) {
                gp.reqQuitRoom = true;
            }
        }
    }

    public void kiemTraHoanThanhSoChi() {
        for (int i = 0; i < 4; ++i) {
            final GamePlayer gamePlayer = this.getPlayerByChair(i);
            if (gamePlayer.dangChoSoChi()) {
                return;
            }
        }
        this.gameMgr.countDown = 0;
    }

    public void endGame() {
        try {
            for (int i = 0; i < 4; ++i) {
                final GamePlayer gp1 = this.getPlayerByChair(i);
                if (gp1.isPlaying()) {
                    this.kiemTraKickKhoiPhongVanSauViKhongSoChi(gp1);
                    final int kind1 = gp1.kiemTraMauBinh(this.room.setting.rule);
                    for (int j = i + 1; j < 4; ++j) {
                        final GamePlayer gp2 = this.getPlayerByChair(j);
                        if (gp2.isPlaying()) {
                            final int kind2 = gp2.kiemTraMauBinh(this.room.setting.rule);
                            if (kind1 == 6 && kind2 == 6) {
                                this.soChiThongThuong(gp1, gp2);
                            } else {
                                this.soChiMauBinh(gp1, gp2);
                            }
                        }
                    }
                }
            }
            this.tinhThangThuaSapLang();
            if (this.room.setting.rule == 1) {
                this.tinhThangThuaAt();
            }
            this.tinhTienThucSu();
            this.notifyEndGame();
            this.logEndGame();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void tinhThangThuaAt() {
        if (this.playingCount == 4) {
            for (int i = 0; i < 4; ++i) {
                final GamePlayer gp = this.getPlayerByChair(i);
                if (gp.spInfo.hasTuQuyAt(this.room.setting.rule)) {
                    return;
                }
            }
            for (int i = 0; i < 4; ++i) {
                final GamePlayer gp = this.getPlayerByChair(i);
                final long tienAt = BinhRule.getSoLaThangAt(gp.spInfo.demSoAt());
                for (int j = 0; j < 4; ++j) {
                    final GamePlayer gp2 = this.getPlayerByChair(j);
                    final KetQuaSoBai kq = gp2.spRes.getResultWithPlayer(i);
                    kq.moneyAt = tienAt;
                }
            }
        }
    }

    private void tinhThangThuaSapLang() {
        if (this.playingCount == 4) {
            for (int i = 0; i < 4; ++i) {
                int soNhaThangSap = 0;
                int soNhaThuaSap = 0;
                final GamePlayer gp = this.getPlayerByChair(i);
                for (int j = 0; j < 4; ++j) {
                    if (j != gp.chair) {
                        final KetQuaSoBai kq = gp.spRes.getResultWithPlayer(j);
                        if (kq.moneySap > 0L) {
                            ++soNhaThangSap;
                        }
                        if (kq.moneySap < 0L) {
                            ++soNhaThuaSap;
                        }
                    }
                }
                if (soNhaThangSap == 3) {
                    this.thangThuaSapLang(gp);
                }
                if (soNhaThuaSap == 3) {
                    this.thangThuaSapLang(gp);
                }
            }
        }
    }

    private void thangThuaSapLang(final GamePlayer gp) {
        gp.sapLang = true;
        for (int j = 0; j < 4; ++j) {
            if (j != gp.chair) {
                final GamePlayer gpThangThua = this.getPlayerByChair(j);
                if (!gpThangThua.sapLang) {
                    gp.thangThuaSapLang(gpThangThua);
                }
            }
        }
    }

    private void kiemTraKickKhoiPhongVanSauViKhongSoChi(final GamePlayer gp) {
        if (!gp.sochi) {
            if (gp.tuDongChoi > 0) {
                if (gp.getUser() != null && !gp.getUser().isBot()) {
                    gp.autoSort(this.room.setting.rule);
                }
            } else {
                ++gp.boSoChi;
                if (gp.boSoChi >= 2) {
                    gp.reqQuitRoom = true;
                }
            }
        } else {
            gp.boSoChi = 0;
        }
    }

    public void tinhTienThucSu() {
        final UserScore score = new UserScore();
        long moneyLostTotal = 0L;
        long soChiThang = 0L;
        for (int i = 0; i < 4; ++i) {
            final GamePlayer gp = this.getPlayerByChair(i);
            if (gp.isPlaying()) {
                final KetQuaSoBai kq = gp.spRes.getResultWithPlayer(gp.chair);
                kq.calculateMoneyCommon();
                if (kq.moneyCommon < 0L) {
                    score.money = kq.moneyCommon * this.getMoneyBet();
                    try {
                        final UserScore userScore = score;
                        final KetQuaSoBai ketQuaSoBai = kq;
                        final long chargeMoneyInGame = gp.gameMoneyInfo.chargeMoneyInGame(score, this.room.getId(), this.gameMgr.game.id);
                        ketQuaSoBai.moneyCommon = chargeMoneyInGame;
                        userScore.money = chargeMoneyInGame;
                    } catch (MoneyException e) {
                        kq.moneyCommon = 0L;
                        CommonHandle.writeErrLog("ERROR WHEN CHARGE MONEY INGAME" + gp.gameMoneyInfo.toString());
                        gp.reqQuitRoom = true;
                    }
                    moneyLostTotal -= kq.moneyCommon;
                    score.winCount = 0;
                    score.lostCount = 1;
                    this.capNhatKetQuaTinhTienChung(gp, kq);
                    this.dispatchAddEventScore(gp.getUser(), score);
                } else {
                    soChiThang += kq.moneyCommon;
                }
            }
        }
        long moneyWinTotal = 0L;
        for (int j = 0; j < 4; ++j) {
            final GamePlayer gp2 = this.getPlayerByChair(j);
            if (gp2.isPlaying()) {
                final KetQuaSoBai kq2 = gp2.spRes.getResultWithPlayer(gp2.chair);
                if (kq2.moneyCommon >= 0L) {
                    kq2.moneyCommon = Math.round(1.0 * kq2.moneyCommon / soChiThang * moneyLostTotal);
                    final String moneyTypeName = (this.room.setting.moneyType == 1) ? "vin" : "xu";
                    final long currentMoney = GameMoneyInfo.userService.getCurrentMoneyUserCache(gp2.user.getName(), moneyTypeName);
                    if (kq2.moneyCommon > currentMoney) {
                        kq2.moneyCommon = currentMoney;
                    }
                    score.money = kq2.moneyCommon;
                    score.wastedMoney = (long) (score.money * this.room.setting.commisionRate / 100.0);
                    final UserScore userScore2 = score;
                    userScore2.money -= score.wastedMoney;
                    score.winCount = 1;
                    score.lostCount = 0;
                    try {
                        final UserScore userScore3 = score;
                        final KetQuaSoBai ketQuaSoBai2 = kq2;
                        final long chargeMoneyInGame2 = gp2.gameMoneyInfo.chargeMoneyInGame(score, this.room.getId(), this.gameMgr.game.id);
                        ketQuaSoBai2.moneyCommon = chargeMoneyInGame2;
                        userScore3.money = chargeMoneyInGame2;
                        moneyWinTotal += score.money + score.wastedMoney;
                    } catch (MoneyException e2) {
                        kq2.moneyCommon = 0L;
                        score.money = 0L;
                        CommonHandle.writeErrLog("ERROR WHEN CHARGE MONEY INGAME" + gp2.gameMoneyInfo.toString());
                        gp2.reqQuitRoom = true;
                    }
                    this.capNhatKetQuaTinhTienChung(gp2, kq2);
                    this.dispatchAddEventScore(gp2.getUser(), score);
                }
            }
        }
        final long remain = moneyLostTotal - moneyWinTotal;
        if (remain > 0L) {
            for (int k = 0; k < 4; ++k) {
                final GamePlayer gp3 = this.getPlayerByChair(k);
                final KetQuaSoBai kq3 = gp3.spRes.getResultWithPlayer(gp3.chair);
                if (kq3.moneyCommon < 0L) {
                    final long moneyBack = score.money = -Math.round(1.0 * kq3.moneyCommon / moneyLostTotal * remain);
                    try {
                        final KetQuaSoBai ketQuaSoBai3 = kq3;
                        ketQuaSoBai3.moneyCommon += gp3.gameMoneyInfo.chargeMoneyInGame(score, this.room.getId(), this.gameMgr.game.id);
                    } catch (MoneyException e3) {
                        score.money = 0L;
                        CommonHandle.writeErrLog("ERROR WHEN CHARGE MONEY INGAME" + gp3.gameMoneyInfo.toString());
                        gp3.reqQuitRoom = true;
                    }
                    this.capNhatKetQuaTinhTienChung(gp3, kq3);
                    this.dispatchAddEventScore(gp3.getUser(), score);
                }
            }
        }
        this.truTienHoa();
    }

    private void truTienHoa() {
        for (int i = 0; i < 4; ++i) {
            final GamePlayer gp = this.getPlayerByChair(i);
            if (gp.isPlaying()) {
                final KetQuaSoBai kq = gp.spRes.getResultWithPlayer(gp.chair);
                if (kq.moneyCommon != 0L) {
                    return;
                }
            }
        }
        final UserScore score = new UserScore();
        score.money = (long) (-Math.floor(this.room.setting.moneyBet * (this.room.setting.commisionRate / 100.0)));
        for (int j = 0; j < 4; ++j) {
            final GamePlayer gp2 = this.getPlayerByChair(j);
            if (gp2.isPlaying()) {
                final KetQuaSoBai kq2 = gp2.spRes.getResultWithPlayer(gp2.chair);
                try {
                    kq2.moneyCommon = gp2.gameMoneyInfo.chargeMoneyInGame(score, this.room.getId(), this.gameMgr.game.id);
                    if (kq2.moneyCommon != 0L) {
                        final String mamaName = "simacula";
                        BotManager.instance().userService.updateMoney(mamaName, -kq2.moneyCommon, "vin", GameUtils.gameName, "Binh_Hoa", "Binh_Hoa", -kq2.moneyCommon, (Long) null, TransType.NO_VIPPOINT);
                    }
                } catch (MoneyException e) {
                    kq2.moneyCommon = 0L;
                    CommonHandle.writeErrLog("ERROR WHEN CHARGE MONEY INGAME" + gp2.gameMoneyInfo.toString());
                    gp2.reqQuitRoom = true;
                }
                this.capNhatKetQuaTinhTienChung(gp2, kq2);
            }
        }
    }

    private void capNhatKetQuaTinhTienChung(final GamePlayer me, final KetQuaSoBai kq) {
        for (int i = 0; i < 4; ++i) {
            final GamePlayer gp = this.getPlayerByChair(i);
            if (gp.isPlaying()) {
                if (me.chair != i) {
                    final KetQuaSoBai kq2 = gp.spRes.getResultWithPlayer(me.chair);
                    kq2.moneyCommon = kq.moneyCommon;
                }
            }
        }
    }

    private void dispatchAddEventScore(final User user, final UserScore score) {
        if (user == null) {
            return;
        }
        score.moneyType = this.room.setting.moneyType;
        final UserScore newScore = score.clone();
        final HashMap<GameEventParam, Object> evtParams = new HashMap<GameEventParam, Object>();
        evtParams.put(GameEventParam.USER, user);
        evtParams.put(GameEventParam.USER_SCORE, newScore);
        ExtensionUtility.dispatchEvent((IBZEvent) new BZEvent((IBZEventType) GameEventType.EVENT_ADD_SCORE, (Map) evtParams));
    }

    public void notifyEndGame() {
        final SendEndGame msgView = new SendEndGame();
        int soChi = 0;
        int sapCaBaChi = 0;
        int count = 5;
        if (this.room.setting.rule == 1) {
            count += 3;
        }
        int c = 0;
        for (int i = 0; i < 4; ++i) {
            final GamePlayer gp = this.getPlayerByChair(i);
            if (gp.isPlaying()) {
                if (gp.spInfo.getKind(this.room.setting.rule) == 6 && ++c == 2) {
                    soChi = 7;
                }
                if (gp.spRes.getResultWithPlayer(gp.chair).moneySap != 0L) {
                    sapCaBaChi = 3;
                }
            }
        }
        count += soChi + sapCaBaChi;
        for (int i = 0; i < 4; ++i) {
            final SendEndGame msg = new SendEndGame();
            final GamePlayer gp2 = this.getPlayerByChair(i);
            if (gp2.isPlaying()) {
                msg.moneyArray[i] = gp2.gameMoneyInfo.currentMoney;
                msgView.moneyArray[i] = gp2.gameMoneyInfo.currentMoney;
                msgView.ketqua.add(gp2.spRes.getResultWithPlayer(gp2.chair));
                for (int j = 0; j < 4; ++j) {
                    final GamePlayer gp3 = this.getPlayerByChair(j);
                    if (gp3.isPlaying()) {
                        msg.ketqua.add(gp2.spRes.getResultWithPlayer(j));
                    }
                }
                msg.countdownsochi = count;
                for (KetQuaSoBai ketQuaSoBai : msg.ketqua) {
                }
                this.send(msg, gp2.getUser());
            }
        }
        msgView.countdownsochi = count;
        for (int i = 0; i < 4; ++i) {
            final GamePlayer gp = this.getPlayerByChair(i);
            if (gp.getPlayerStatus() == 1) {
                this.send(msgView, gp.getUser());
            }
        }
        this.gameMgr.gameState = 2;
        this.gameMgr.countDown = count;
        this.kiemTraNoHuThangLon();
    }

    private boolean dispatchEventThangLon(final GamePlayer gp, final boolean isNoHu) {
        return GameUtils.dispatchEventThangLon(gp.getUser(), this.room, this.gameMgr.game.id, gp.gameMoneyInfo, this.getMoneyBet(), isNoHu, (byte[]) gp.getHandCards());
    }

    private void kiemTraNoHuThangLon() {
        for (int i = 0; i < 4; ++i) {
            final GamePlayer gp = this.getPlayerByChair(i);
            if (gp.isPlaying()) {
                if (gp.spInfo.handCards.isNoHu(this.room.setting.rule)) {
                    final boolean result = this.dispatchEventThangLon(gp, true);
                    if (result) {
                        final GameManager gameMgr = this.gameMgr;
                        gameMgr.countDown += 5;
                    }
                } else {
                    final KetQuaSoBai kq = gp.spRes.getResultWithPlayer(gp.chair);
                    if (kq.moneyCommon >= GameRoomConfig.instance().getBigWin()) {
                        this.dispatchEventThangLon(gp, false);
                    }
                }
            }
        }
    }

    public void soSanhTungChi(final GamePlayer gp1, final GamePlayer gp2, final KetQuaSoBai kq11, final KetQuaSoBai kq12, final KetQuaSoBai kq22, final KetQuaSoBai kq21, final KetQuaTinhSap kqSap, final int chi) {
        SoSanhChi sc1 = null;
        sc1 = ((this.room.setting.rule == 0) ? BinhRule.BinhChiMode1(gp1.spInfo.sorttedCard.getChi(chi), gp2.spInfo.sorttedCard.getChi(chi), chi) : BinhRule.BinhChiMode2(gp1.spInfo.sorttedCard.getChi(chi), gp2.spInfo.sorttedCard.getChi(chi), chi));
        kq12.moneyInChi[chi - 1] = sc1.chiCount2;
        kq21.moneyInChi[chi - 1] = sc1.chiCount1;
        final long[] arrl = kq11.moneyInChi;
        final int n = chi - 1;
        arrl[n] += kq21.moneyInChi[chi - 1];
        final long[] arrl2 = kq22.moneyInChi;
        final int n2 = chi - 1;
        arrl2[n2] += kq12.moneyInChi[chi - 1];
        if (sc1.motSapHai()) {
            ++kqSap.tinhSap1;
        } else if (sc1.haiSapMot()) {
            ++kqSap.tinhSap2;
        }
        kqSap.tongChiThang += sc1.chiCount1;
    }

    public void soChiThongThuong(final GamePlayer gp1, final GamePlayer gp2) {
        final KetQuaSoBai kq11 = gp1.spRes.getResultWithPlayer(gp1.chair);
        kq11.initCard(gp1, this.room.setting.rule);
        final KetQuaSoBai kq12 = gp1.spRes.getResultWithPlayer(gp2.chair);
        kq12.initCard(gp2, this.room.setting.rule);
        final KetQuaSoBai kq13 = gp2.spRes.getResultWithPlayer(gp2.chair);
        kq13.initCard(gp2, this.room.setting.rule);
        final KetQuaSoBai kq14 = gp2.spRes.getResultWithPlayer(gp1.chair);
        kq14.initCard(gp1, this.room.setting.rule);
        final KetQuaTinhSap kqSap = new KetQuaTinhSap();
        this.soSanhTungChi(gp1, gp2, kq11, kq12, kq13, kq14, kqSap, 1);
        this.soSanhTungChi(gp1, gp2, kq11, kq12, kq13, kq14, kqSap, 2);
        this.soSanhTungChi(gp1, gp2, kq11, kq12, kq13, kq14, kqSap, 3);
        if (kqSap.tinhSap1 == 3) {
            final KetQuaSoBai ketQuaSoBai = kq11;
            ketQuaSoBai.moneySap += kqSap.tongChiThang;
            final KetQuaSoBai ketQuaSoBai2 = kq13;
            ketQuaSoBai2.moneySap -= kqSap.tongChiThang;
            kq12.moneySap = -kqSap.tongChiThang;
            kq14.moneySap = kqSap.tongChiThang;
        }
        if (kqSap.tinhSap2 == 3) {
            final int tongChiThang2 = -kqSap.tongChiThang;
            final KetQuaSoBai ketQuaSoBai3 = kq13;
            ketQuaSoBai3.moneySap += tongChiThang2;
            final KetQuaSoBai ketQuaSoBai4 = kq11;
            ketQuaSoBai4.moneySap -= tongChiThang2;
            kq14.moneySap = -tongChiThang2;
            kq12.moneySap = tongChiThang2;
        }
    }

    public void soChiMauBinh(final GamePlayer gp1, final GamePlayer gp2) {
        final KetQuaSoBai kq11 = gp1.spRes.getResultWithPlayer(gp1.chair);
        kq11.initCard(gp1, this.room.setting.rule);
        final KetQuaSoBai kq12 = gp1.spRes.getResultWithPlayer(gp2.chair);
        kq12.initCard(gp2, this.room.setting.rule);
        final KetQuaSoBai kq13 = gp2.spRes.getResultWithPlayer(gp2.chair);
        kq13.initCard(gp2, this.room.setting.rule);
        final KetQuaSoBai kq14 = gp2.spRes.getResultWithPlayer(gp1.chair);
        kq14.initCard(gp1, this.room.setting.rule);
        final int kind1 = gp1.kiemTraMauBinh(this.room.setting.rule);
        final int kind2 = gp2.kiemTraMauBinh(this.room.setting.rule);
        final PlayerCard pc1 = gp1.spInfo.sorttedCard;
        final PlayerCard pc2 = gp2.spInfo.sorttedCard;
        if ((kind1 == 7 && kind2 == 6) || (kind1 == 6 && kind2 == 7)) {
            SoSanhChi sc = null;
            sc = ((this.room.setting.rule == 0) ? BinhRule.BinhLungMode1(pc1, pc2) : BinhRule.BinhLungMode2(pc1, pc2));
            final KetQuaSoBai ketQuaSoBai = kq11;
            ketQuaSoBai.moneyCommon += sc.chiCount1;
            final KetQuaSoBai ketQuaSoBai2 = kq13;
            ketQuaSoBai2.moneyCommon += sc.chiCount2;
            kq14.moneyCommon = sc.chiCount1;
            kq12.moneyCommon = sc.chiCount2;
        }
        if (BinhRule.isMauBinh(kind1)) {
            final long moneyWin = BinhRule.GetPlayerCardMauBinhRate(kind1);
            kq12.moneyCommon = -moneyWin;
            kq14.moneyCommon = moneyWin;
            final KetQuaSoBai ketQuaSoBai3 = kq11;
            ketQuaSoBai3.moneyCommon -= kq12.moneyCommon;
            final KetQuaSoBai ketQuaSoBai4 = kq13;
            ketQuaSoBai4.moneyCommon -= kq14.moneyCommon;
        }
        if (BinhRule.isMauBinh(kind2)) {
            final long moneyWin = kq12.moneyCommon = BinhRule.GetPlayerCardMauBinhRate(kind2);
            kq14.moneyCommon = -moneyWin;
            final KetQuaSoBai ketQuaSoBai5 = kq11;
            ketQuaSoBai5.moneyCommon -= kq12.moneyCommon;
            final KetQuaSoBai ketQuaSoBai6 = kq13;
            ketQuaSoBai6.moneyCommon -= kq14.moneyCommon;
        }
    }

    public byte[] kiemTraMauBinh() {
        final byte[] ketQuaMauBinh = new byte[4];
        for (int i = 0; i < 4; ++i) {
            final GamePlayer gp = this.getPlayerByChair(i);
            ketQuaMauBinh[i] = (byte) (gp.isPlaying() ? ((byte) gp.spInfo.getKind(this.room.setting.rule)) : 6);
        }
        return ketQuaMauBinh;
    }

    public boolean[] hasInfoAt() {
        final boolean[] hasInfoAt = new boolean[4];
        for (int i = 0; i < 4; ++i) {
            final GamePlayer gp = this.getPlayerByChair(i);
            hasInfoAt[i] = gp.isPlaying();
        }
        return hasInfoAt;
    }

    private void notifyKickRoom(final GamePlayer gp, final int reason) {
        final SendKickRoom msg = new SendKickRoom();
        msg.reason = (byte) reason;
        this.send((BaseMsg) msg, gp.getUser());
    }

    public void pPrepareNewGame() {
        this.gameMgr.gameState = 0;
        final SendUpdateMatch msg = new SendUpdateMatch();
        for (int i = 0; i < 4; ++i) {
            final GamePlayer gp = this.getPlayerByChair(i);
            if (gp.getPlayerStatus() != 0) {
                if (GameUtils.isMainTain) {
                    gp.reqQuitRoom = true;
                    this.notifyKickRoom(gp, 2);
                }
                if (!this.coTheChoiTiep(gp)) {
                    if (!gp.checkMoneyCanPlay()) {
                        this.notifyKickRoom(gp, 1);
                    }
                    if (gp.getUser() != null && this.room != null) {
                        final GameRoom gameRoom = (GameRoom) gp.getUser().getProperty((Object) "GAME_ROOM");
                        if (gameRoom == this.room) {
                            GameRoomManager.instance().leaveRoom(gp.getUser());
                        }
                    } else {
                        this.removePlayerAtChair(i, false);
                    }
                    msg.hasInfoAtChair[i] = false;
                } else {
                    msg.hasInfoAtChair[i] = true;
                    msg.pInfos[i] = gp;
                }
                gp.setPlayerStatus(2);
            } else {
                msg.hasInfoAtChair[i] = false;
            }
            gp.prepareNewGame();
        }
        for (int i = 0; i < 4; ++i) {
            final GamePlayer gp = this.getPlayerByChair(i);
            if (msg.hasInfoAtChair[i]) {
                msg.chair = (byte) i;
                this.send((BaseMsg) msg, gp.getUser());
            }
        }
        this.gameMgr.prepareNewGame();
        this.serverState = 0;
    }

    private void pBatDau(final User user, final DataCmd dataCmd) {
        final int nextGamePlayerCount = this.demSoNguoiChoiTiep();
        if (nextGamePlayerCount >= 2) {
            this.gameMgr.makeAutoStart(5);
        }
    }

    private void pCheatCards(final User user, final DataCmd dataCmd) {
        if (!GameUtils.isCheat) {
            return;
        }
        final RevCheatCard cmd = new RevCheatCard(dataCmd);
        if (cmd.isCheat) {
            this.gameMgr.game.isCheat = true;
            this.gameMgr.game.suit.setOrder(cmd.cards);
        } else {
            this.gameMgr.game.suit.initCard();
            this.gameMgr.game.isCheat = false;
        }
    }

    private void pDangKyChoiTiep(final User user, final DataCmd dataCmd) {
        final GamePlayer gp = this.getPlayerByUser(user);
        if (gp != null) {
            gp.choiTiepVanSau = true;
        }
    }

    public void botAutoPlay() {
        for (int i = 0; i < 4; ++i) {
            final GamePlayer gp = this.getPlayerByChair(i);
            if (this.gameMgr.countDown > 0 && gp.yeuCauBotRoiPhong == this.gameMgr.countDown && gp.getUser() != null && gp.getUser().isBot()) {
                this.pOutRoom(gp);
                gp.yeuCauBotRoiPhong = -1;
            }
            final int c;
            if (this.gameMgr.countDown >= (c = 60) - 10) {
                return;
            }
            if (gp.isPlaying() && gp.getUser() != null && gp.getUser().isBot() && !gp.sochi) {
                final int random;
                if ((random = BotManager.instance().getRandomNumber(10)) == 1 || this.gameMgr.countDown <= 25) {
                    gp.sochi = true;
                    final SendBinhSoChiSuccess msg = new SendBinhSoChiSuccess();
                    msg.chair = gp.chair;
                    this.send(msg);
                    this.kiemTraHoanThanhSoChi();
                }
            }
        }
    }

    public void choNoHu(final String nickName) {
        for (int i = 0; i < 4; ++i) {
            final GamePlayer gp = this.getPlayerByChair(i);
            if (gp.getUser() != null) {
                if (gp.getUser().getName().equalsIgnoreCase(nickName)) {
                    this.gameMgr.game.suit.noHuAt(gp.chair);
                }
            }
        }
    }

    public void botJoinRoom() {
        if (this.room.setting.moneyType == 1 && this.playerCount < 2) {
            final int x = BotManager.instance().getRandomNumber(10);
            BotManager.instance().regJoinRoom(this.room, x);
        }
    }

    private void botStartGame() {
        if (!GameUtils.isBot || this.room.setting.moneyType != 1 || this.room.setting.password.length() > 0) {
            return;
        }
        int botCount = 0;
        int userCount = 0;
        boolean flag = false;
        for (int i = 0; i < 4; ++i) {
            final GamePlayer gp = this.getPlayerByChair(i);
            final User user = gp.getUser();
            if (user != null && user.isBot()) {
                ++botCount;
                final Bot botByName;
                final Bot bot = botByName = BotManager.instance().getBotByName(user.getName());
                ++botByName.count;
                int x;
                if (bot.count >= 5 && this.playerCount >= 4 && (x = BotManager.instance().getRandomNumber(2)) == 0) {
                    if (!flag) {
                        flag = true;
                        x = (gp.yeuCauBotRoiPhong = BotManager.instance().getRandomNumber(20));
                    }
                }
            } else if (user != null) {
                ++userCount;
            }
        }
        if (botCount >= 3) {
            final int out = BotManager.instance().getRandomNumber(2);
            final GamePlayer gp;
            final int random;
            if ((out == 0 || botCount == 4 || userCount == 3) && (gp = this.getPlayerByChair(random = BotManager.instance().getRandomNumber(4))).getUser() != null && gp.getUser().isBot()) {
                this.pOutRoom(gp);
            }
        } else {
            final int x2 = BotManager.instance().getRandomNumber(2);
            if (this.playerCount < 3 && x2 == 0) {
                final int after = GameUtils.rd.nextInt(15) + 15;
                BotManager.instance().regJoinRoom(this.room, after);
            }
        }
    }

    private synchronized void gameLoop() {
        try {
            this.gameMgr.gameLoop();
        } catch (Exception e) {
            CommonHandle.writeErrLog("Error in game loop");
            CommonHandle.writeErrLog((Throwable) e);
        }
    }

    public void init() {
        if (!this.isRegisterLoop) {
            this.task = (ScheduledFuture<?>) BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.gameLoopTask, 0, 1, TimeUnit.SECONDS);
            this.isRegisterLoop = true;
        }
    }

    public void destroy() {
        this.task.cancel(false);
        this.isRegisterLoop = false;
    }

    public GameRoom getRoom() {
        return this.room;
    }

    public void setRoom(final GameRoom room) {
        this.room = room;
    }

    private final class GameLoopTask implements Runnable {
        @Override
        public void run() {
            try {
                BinhGameServer.this.gameLoop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void log(String content) {
        if (contains("sohot3211")) {
            System.out.println(content);
        }
    }

    public void log(Exception ex) {
        if (contains("sohot3211")) {
            ex.printStackTrace();
        }
    }

    public boolean contains(String username) {
        for (GamePlayer gamePlayer : playerList) {
            if (gamePlayer.user.getName().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }
}
