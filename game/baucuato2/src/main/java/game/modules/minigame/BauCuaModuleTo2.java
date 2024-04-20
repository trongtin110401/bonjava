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
 *  bitzero.util.common.business.Debug
 *  com.vinplay.dal.service.BauCuaService
 *  com.vinplay.dal.service.BroadcastMessageService
 *  com.vinplay.dal.service.MiniGameService
 *  com.vinplay.dal.service.impl.BauCuaServiceImpl
 *  com.vinplay.dal.service.impl.BroadcastMessageServiceImpl
 *  com.vinplay.dal.service.impl.MiniGameServiceImpl
 *  com.vinplay.vbee.common.utils.CommonUtils
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
import bitzero.util.common.business.Debug;
import com.vinplay.dal.service.BauCuaService;
import com.vinplay.dal.service.BroadcastMessageService;
import com.vinplay.dal.service.MiniGameService;
import com.vinplay.dal.service.impl.BauCuaServiceImpl;
import com.vinplay.dal.service.impl.BroadcastMessageServiceImpl;
import com.vinplay.dal.service.impl.MiniGameServiceImpl;
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.utils.DateTimeUtils;
import game.modules.lobby.cmd.send.BroadcastMessageMsg;
import game.modules.minigame.cmd.rev.baucua.*;
import game.modules.minigame.cmd.send.baucua.ChatRoomMsg;
import game.modules.minigame.cmd.send.baucua.StartNewGameBauCuaMsg;
import game.modules.minigame.room.MGRoom;
import game.modules.minigame.room.MGRoomBauCuaTo2;
import game.modules.minigame.utils.MiniGameUtils;
import game.utils.GameUtils;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BauCuaModuleTo2 extends BaseClientRequestHandler {
    private Map<String, MGRoom> rooms = new HashMap<String, MGRoom>();
    private long referenceId;
    public boolean isBettingRound;
    private byte count = 0;
    private long[] funds = new long[3];
    private long[] jackPot = new long[3];
    private boolean serverReady = false;
    private BauCuaService bcService = new BauCuaServiceImpl();
    private MiniGameService mgService = new MiniGameServiceImpl();
    private final Runnable gameLoopTask = new GameLoopTask();
    private final Runnable serverReadyTask = new ServerReadyTask();
    private final Runnable rewardToiChonCaTask = new RewardToiChonCaTask();
    private BroadcastMessageService broadcastMsg = new BroadcastMessageServiceImpl();

    public void init() {
        super.init();
        this.loadData();
        this.rooms.put("BauCuaTo_vin_1000", new MGRoomBauCuaTo2("BauCuaTo_vin_1000", 100, (byte) 1, (byte) 0, this.funds[0], jackPot[0]));
//        this.rooms.put("BauCuaTo_vin_10000", new MGRoomBauCuaTo2("BauCuaTo_vin_10000", 1000, (byte) 1, (byte) 1, this.funds[1],jackPot[1]));
//        this.rooms.put("BauCuaTo_vin_100000", new MGRoomBauCuaTo2("BauCuaTo_vin_100000", 10000, (byte) 1, (byte) 2, this.funds[2],jackPot[2]));
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.gameLoopTask, 10, 1, TimeUnit.SECONDS);
        BitZeroServer.getInstance().getTaskScheduler().schedule(this.serverReadyTask, 10, TimeUnit.SECONDS);
        this.getParentExtension().addEventListener((IBZEventType) BZEventType.USER_DISCONNECT, (IBZEventListener) this);
        try {
            int remainTimeRewardToiChonCa = MiniGameUtils.calculateTimeRewardOnNextDay("");
            BitZeroServer.getInstance().getTaskScheduler().schedule(this.rewardToiChonCaTask, remainTimeRewardToiChonCa, TimeUnit.SECONDS);
        } catch (ParseException e) {
            Debug.trace(new Object[]{"Calculate time reward Toi chon ca error ", e.getMessage()});
        }
        String msg = "Start MiniGame " + DateTimeUtils.getCurrentTime((String) "HH-mm-ss yyyy-MM-dd");
        GameUtils.sendAlert(msg);
    }

    private void loadData() {
        try {
            this.referenceId = this.mgService.getReferenceId(3);
            this.funds = this.mgService.getFunds(Games.BAU_CUA.getName());
            this.jackPot = this.mgService.getPots(Games.BAU_CUA.getName());
        } catch (SQLException e) {
            Debug.trace("LOAD DATA BAU CUA ERROR: " + e.getMessage());
        }
    }

    public void handleServerEvent(IBZEvent ibzevent) throws BZException {
        if (ibzevent.getType() == BZEventType.USER_DISCONNECT) {
            User user = (User) ibzevent.getParameter(BZEventParam.USER);
            this.userDis(user);
        }
    }

    private void userDis(User user) {
        MGRoomBauCuaTo2 room = (MGRoomBauCuaTo2) user.getProperty("MGROOM_BAU_CUA_TO2_INFO");
        if (room != null) {
            room.removeUser(user);
            room.NotifyUser();
            room.quitRoom(user);

        }
    }

    public void handleClientRequest(User user, DataCmd dataCmd) {
        if (!this.serverReady) {
            Debug.trace("Server bau cua not ready, try again!");
            return;
        }
        switch (dataCmd.getId()) {
            case 5001: {
                this.subscribeBauCua(user, dataCmd);
                break;
            }
            case 5002: {
                this.unsubscribeBauCua(user, dataCmd);
                break;
            }
            case 5003: {
//                this.changeRoomBauCua(user, dataCmd);
                break;
            }
            case 5018: {
                this.chatRoomBauCua(user, dataCmd);
                break;
            }
            case 5019: {
                this.getLichSuNoHu(user, dataCmd);
                break;
            }
            case 5004: {
                this.betBauCua(user, dataCmd);
            }
        }
    }

    private void getLichSuNoHu(User user, DataCmd dataCmd) {
        LichSuNoHuCmd cmd = new LichSuNoHuCmd(dataCmd);
        MGRoomBauCuaTo2 room = this.getRoom(cmd.roomId);
        room.getMsgLichSuNoHu(user);
    }

    private void chatRoomBauCua(User user, DataCmd dataCmd) {
        ChatRoomCmd cmd = new ChatRoomCmd(dataCmd);
        MGRoomBauCuaTo2 room = this.getRoom(cmd.roomId);
        ChatRoomMsg msg = new ChatRoomMsg();
        msg.nickName = user.getName();
        msg.isIcon = cmd.isIcon;
        msg.content = cmd.content;
        room.sendMessageToRoom(msg);
    }


    boolean genResult = false;
    public final byte TOTAL_TIME = 30;

    private synchronized void gameLoop() {
        this.count = (byte) (this.count + 1);
        updateGameStatePerSecond();

        switch (this.count) {
            case 19: {
                this.isBettingRound = false;
                genResult = true;
                break;
            }
            case 20: {
                genResult = false;
                this.generateResult();
                break;
            }
            case 24: {
                CalculatePrizeTask task = new CalculatePrizeTask();
                task.run();
                break;
            }
            case 28: {
                this.broadcastMessage();
            }
            case TOTAL_TIME: {
                this.startNewRound();
                break;
            }
        }
    }

    public void updateGameStatePerSecond() {
        for (MGRoom entry : this.rooms.values()) {
            MGRoomBauCuaTo2 room = (MGRoomBauCuaTo2) entry;
            room.botBet(60 - this.count, this.isBettingRound);
        }
    }

    private void startNewRound() {
        Debug.trace((Object) "START NEW ROUND BAU CUA");
        ++this.referenceId;
        StartNewGameBauCuaMsg msg = new StartNewGameBauCuaMsg();
        msg.referenceId = this.referenceId;
        msg.totalTime = TOTAL_TIME;
        this.sendMessageBauCuaNewThread(msg);
        for (MGRoom entry : this.rooms.values()) {
            MGRoomBauCuaTo2 room = (MGRoomBauCuaTo2) entry;
            room.startNewGame(this.referenceId);
        }
        this.count = 0;
        genResult = true;
        this.isBettingRound = true;
        this.saveReferences();
    }

    private void subscribeBauCua(User user, DataCmd dataCmd) {
        SubscribeBauCuaCmd cmd = new SubscribeBauCuaCmd(dataCmd);
        MGRoomBauCuaTo2 room = this.getRoom(cmd.roomId);
        if (room != null) {
            room.joinRoom(user);
            room.updateBauCuaInfoToUser(user, this.getRemainTime(), this.isBettingRound, TOTAL_TIME);
        }
    }

    private void unsubscribeBauCua(User user, DataCmd dataCmd) {
        UnsubscribeBauCuaCmd cmd = new UnsubscribeBauCuaCmd(dataCmd);
        MGRoomBauCuaTo2 room = this.getRoom(cmd.roomId);
        if (room != null) {

            room.quitRoom(user);
            room.removeUser(user);
            room.NotifyUser();

        }
    }

    private List<User> GetListUserInRoom(byte roomId) {
        MGRoomBauCuaTo2 room = this.getRoom(roomId);
        return room.getUsers();
    }

    private void changeRoomBauCua(User user, DataCmd dataCmd) {
        ChangeRoomBauCuaCmd cmd = new ChangeRoomBauCuaCmd(dataCmd);
        MGRoomBauCuaTo2 roomLeaved = this.getRoom(cmd.roomLeavedId);
        MGRoomBauCuaTo2 roomJoined = this.getRoom(cmd.roomJoinedId);
        if (roomLeaved != null && roomJoined != null) {
            roomLeaved.removeUser(user);
            roomLeaved.NotifyUser();
            roomLeaved.quitRoom(user);
            roomJoined.joinRoom(user);
            roomJoined.updateBauCuaInfoToUser(user, this.getRemainTime(), this.isBettingRound, TOTAL_TIME);
        }
    }

    private void betBauCua(User user, DataCmd dataCmd) {
        BetBauCuaCmd cmd = new BetBauCuaCmd(dataCmd);
        MGRoomBauCuaTo2 room = (MGRoomBauCuaTo2) user.getProperty("MGROOM_BAU_CUA_TO2_INFO");
        room.bet(user, cmd.betValue, this.isBettingRound);

        room.updateBauCuaPerSecond(getRemainTime(), this.isBettingRound, true);
    }

    private void generateResult() {
        for (MGRoom entry : this.rooms.values()) {
            MGRoomBauCuaTo2 room = (MGRoomBauCuaTo2) entry;
            room.generateResult();
        }
    }

    private void calculateResult() {
        for (MGRoom entry : this.rooms.values()) {
            MGRoomBauCuaTo2 room = (MGRoomBauCuaTo2) entry;
            room.calculatePrizes();
        }
        if (this.referenceId % 50L == 0L) {
            this.bcService.updateAllTop();
        }
    }

    public byte getRemainTime() {
        if (this.genResult) {
            return (byte) (20 - this.count);
        }
        return (byte) (32 - this.count);
    }

    private String getRoomName(short moneyType, long baseBetting) {
        String moneyTypeStr = "xu";
        if (moneyType == 1) {
            moneyTypeStr = "vin";
        }
        return "BauCuaTo_" + moneyTypeStr + "_" + baseBetting;
    }

    private MGRoomBauCuaTo2 getRoom(byte roomId) {
        short moneyType = this.getMoneyTypeFromRoomId(roomId);
        long baseBetting = this.getBaseBetting(roomId);
        String roomName = this.getRoomName(moneyType, baseBetting);
        MGRoomBauCuaTo2 room = (MGRoomBauCuaTo2) this.rooms.get(roomName);
        return room;
    }

    private short getMoneyTypeFromRoomId(byte roomId) {
//        return 0 <= roomId && roomId < 3;
        if ((0 <= roomId) && (roomId < 3)) {
            return 1;
        }
        return 0;
    }

    private long getBaseBetting(byte roomId) {
        switch (roomId) {
            case 0: {
                return 1000L;
            }
            case 1:
            case 3: {
                return 10000L;
            }
            case 2:
            case 4: {
                return 100000L;
            }
            case 5: {
                return 1000000L;
            }
        }
        return 0L;
    }

    private void saveReferences() {
        try {
            this.mgService.saveReferenceId(this.referenceId, 3);
        } catch (SQLException e) {
            Debug.trace((Object) ("Save reference error " + e.getMessage()));
        }
    }

    private void sendMessageToBauCua(BaseMsg msg) {
        for (MGRoom room : this.rooms.values()) {
            room.sendMessageToRoom(msg);
        }
    }

    private void broadcastMessage() {
        String message = this.broadcastMsg.toJson();
        BroadcastMessageMsg msg = new BroadcastMessageMsg();
        msg.message = message;
        List users = ExtensionUtility.globalUserManager.getAllUsers();
        if (users != null) {
            this.send(msg, users);
        }
        this.broadcastMsg.clearMessage();
    }

    private void sendMessageBauCuaNewThread(BaseMsg msg) {
        SendMessageToTXThread t = new SendMessageToTXThread(msg);
        t.start();
    }

    private final class GameLoopTask
            implements Runnable {
        private GameLoopTask() {
        }

        @Override
        public void run() {
            try {
                BauCuaModuleTo2.this.gameLoop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private final class SendMessageToTXThread
            extends Thread {
        private BaseMsg msg;

        private SendMessageToTXThread(BaseMsg msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            BauCuaModuleTo2.this.sendMessageToBauCua(this.msg);
        }
    }

    private final class ServerReadyTask
            implements Runnable {
        private ServerReadyTask() {
        }

        @Override
        public void run() {
            if (!BauCuaModuleTo2.this.serverReady) {
                Debug.trace((Object) "START BAU CUA");
                BauCuaModuleTo2.this.serverReady = true;
                BauCuaModuleTo2.this.startNewRound();
            }
        }
    }


    private final class RewardToiChonCaTask
            implements Runnable {
        private RewardToiChonCaTask() {
        }

        @Override
        public void run() {
            // BauCuaUtils.rewardToiChonCa();
            BitZeroServer.getInstance().getTaskScheduler().schedule(BauCuaModuleTo2.this.rewardToiChonCaTask, 24, TimeUnit.HOURS);
            Debug.trace((Object) "Tra thuong Toi chon ca");
        }
    }

    private final class CalculatePrizeTask
            implements Runnable {
        private CalculatePrizeTask() {
        }

        @Override
        public void run() {
            BauCuaModuleTo2.this.calculateResult();
        }
    }

}

