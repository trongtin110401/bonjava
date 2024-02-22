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
 *  bitzero.server.extensions.data.BaseMsg
 *  bitzero.server.extensions.data.DataCmd
 *  bitzero.server.util.TaskScheduler
 *  bitzero.util.common.business.Debug
 *  com.vinplay.dal.service.MiniGameService
 *  com.vinplay.dal.service.SlotMachineService
 *  com.vinplay.dal.service.impl.CacheServiceImpl
 *  com.vinplay.usercore.service.UserService
 *  com.vinplay.vbee.common.enums.Games
 *  com.vinplay.vbee.common.exceptions.KeyNotFoundException
 *  com.vinplay.vbee.common.utils.CommonUtils
 */
package game.modules.slot;

import bitzero.server.BitZeroServer;
import bitzero.server.core.BZEventParam;
import bitzero.server.core.BZEventType;
import bitzero.server.core.IBZEvent;
import bitzero.server.entities.User;
import bitzero.server.exceptions.BZException;
import bitzero.server.extensions.data.DataCmd;
import bitzero.util.common.business.Debug;
import com.vinplay.dal.common.BroadCastUserState;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.exceptions.KeyNotFoundException;
import com.vinplay.vbee.common.utils.CommonUtils;
import game.modules.slot.cmd.rev.maybach.*;
import game.modules.slot.cmd.send.maybach.MaybachX2Msg;
import game.modules.slot.entities.BotMinigame;
import game.modules.slot.room.MayBachRoom;
import game.util.ConfigGame;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MayBachModule extends SlotModule {
    private long referenceId = 1L;
    private String fullLines = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20";

    public MayBachModule() {
        this.gameName = Games.MAYBACH.getName();
    }

    public void init() {
        super.init();
        long[] funds = new long[3];
        int[] initPotValues = new int[6];
        try {
            String initPotValuesStr = ConfigGame.getValueString(this.gameName + "_init_pot_values");
            String[] arr = initPotValuesStr.split(",");
            for (int i = 0; i < arr.length; ++i) {
                initPotValues[i] = Integer.parseInt(arr[i]);
            }
            this.jackpots = this.service.getPots(this.gameName);
            Debug.trace(this.gameName + "POTS: " + CommonUtils.arrayLongToString(this.jackpots));
            funds = this.service.getFunds(this.gameName);
            Debug.trace(this.gameName + ": " + CommonUtils.arrayLongToString(funds));
        } catch (Exception e) {
            Debug.trace("Init POKE GO error ", e);
        }
        this.rooms.put(this.gameName + "_vin_100", new MayBachRoom(this, (byte) 0, this.gameName + "_vin_100", (short) 1, this.jackpots[0], funds[0], 100, initPotValues[0]));
        this.rooms.put(this.gameName + "_vin_1000", new MayBachRoom(this, (byte) 1, this.gameName + "_vin_1000", (short) 1, this.jackpots[1], funds[1], 1000, initPotValues[1]));
        this.rooms.put(this.gameName + "_vin_10000", new MayBachRoom(this, (byte) 2, this.gameName + "_vin_10000", (short) 1, this.jackpots[2], funds[2], 10000, initPotValues[2]));
        Debug.trace("INIT " + this.gameName + " DONE");
        this.getParentExtension().addEventListener(BZEventType.USER_DISCONNECT, this);
        this.referenceId = this.slotService.getLastReferenceId(this.gameName);
        Debug.trace("START " + this.gameName + " REFERENCE ID= " + this.referenceId);
        CacheServiceImpl sv = new CacheServiceImpl();
        try {
            sv.removeKey(this.gameName + "_last_day_x2");
        } catch (KeyNotFoundException e2) {
            Debug.trace("KEY NOT FOUND");
        }

        this.getParentExtension().addEventListener(BZEventType.USER_DISCONNECT, this);
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.gameLoopTask, 10, 1, TimeUnit.SECONDS);
    }

    @Override
    public long getBaseBetting(byte roomId) {
        switch (roomId) {
            case 0: {
                return 100L;
            }
            case 1: {
                return 1000L;
            }
            case 2: {
                return 10000L;
            }
            case 3: {
                return 10000L;
            }
            case 4: {
                return 1000L;
            }
            case 5: {
                return 10000L;
            }
            case 6: {
                return 100000L;
            }
        }
        return 0L;
    }

    public void handleServerEvent(IBZEvent ibzevent) throws BZException {
        if (ibzevent.getType() == BZEventType.USER_DISCONNECT) {
            User user = (User) ibzevent.getParameter(BZEventParam.USER);
            this.userDis(user);
        }
    }

    private void userDis(User user) {
        MayBachRoom room = (MayBachRoom) user.getProperty("MGROOM_" + this.gameName + "_INFO");
        if (room != null) {
            room.quitRoom(user);
            room.stopAutoPlay(user);
        }
        BroadCastUserState.popBroadCast(user.getName());

    }

    @Override
    public void handleClientRequest(User user, DataCmd dataCmd) {
        Debug.trace(this.gameName + " handleClientRequest " + dataCmd.getId());

        switch (dataCmd.getId()) {
            case 3003: {
                this.subScribe(user, dataCmd);
                break;
            }
            case 3004: {
                this.unSubScribe(user, dataCmd);
                break;
            }
            case 3005: {
                this.changeRoom(user, dataCmd);
                break;
            }
            case 3006: {
                this.autoPlay(user, dataCmd);
                break;
            }
            case 3001: {
                this.playMaybach(user, dataCmd);
                break;
            }
            case 3013: {
                this.minimize(user, dataCmd);
            }
        }
    }

    @Override
    public long getNewReferenceId() {
        return ++this.referenceId;
    }

    protected void subScribe(User user, DataCmd dataCmd) {
        SubscribeMaybachCmd cmd = new SubscribeMaybachCmd(dataCmd);
        MayBachRoom room = (MayBachRoom) this.getRoom(cmd.roomId);
        if (room != null) {
            room.joinRoom(user);
            room.userMaximize(user);
            room.updatePot(user);
            MaybachX2Msg msg = new MaybachX2Msg();
            msg.ngayX2 = "";
            msg.remain = 0;
            msg.currentMoney = this.userService.getMoneyUserCache(user.getName(), "vin");
            this.send(msg, user);
        } else {
            Debug.trace("MayBack SUBSCRIBE: room " + cmd.roomId + " not found");
        }
        BroadCastUserState.popBroadCast(user.getName());
    }

    protected void unSubScribe(User user, DataCmd dataCmd) {
        UnSubscribeMaybachCmd cmd = new UnSubscribeMaybachCmd(dataCmd);
        MayBachRoom room = (MayBachRoom) this.getRoom(cmd.roomId);
        if (room != null) {
            room.stopAutoPlay(user);
            room.quitRoom(user);
        } else {
            Debug.trace("MayBack UNSUBSCRIBE: room " + cmd.roomId + " not found");
        }
        BroadCastUserState.popBroadCast(user.getName());

    }

    protected void minimize(User user, DataCmd dataCmd) {
        MinimizeMaybachCmd cmd = new MinimizeMaybachCmd(dataCmd);
        MayBachRoom room = (MayBachRoom) this.getRoom(cmd.roomId);
        if (room != null) {
            room.quitRoom(user);
            room.userMinimize(user);
        } else {
            Debug.trace(this.gameName + " MINIMIZE: room " + cmd.roomId + " not found");
        }
    }

    protected void changeRoom(User user, DataCmd dataCmd) {
        ChangeRoomMaybachCmd cmd = new ChangeRoomMaybachCmd(dataCmd);
        MayBachRoom roomLeaved = (MayBachRoom) this.getRoom(cmd.roomLeavedId);
        MayBachRoom roomJoined = (MayBachRoom) this.getRoom(cmd.roomJoinedId);
        if (roomLeaved != null && roomJoined != null) {
            roomLeaved.stopAutoPlay(user);
            roomLeaved.quitRoom(user);
            roomJoined.joinRoom(user);
            roomJoined.updatePot(user);
        } else {
            Debug.trace("MayBack: change room error, leaved= " + cmd.roomLeavedId + ", joined= " + cmd.roomJoinedId);
        }
        if (roomJoined != null) {
            BroadCastUserState.pushBroadCast(user.getName(), user.getName() + " play " + gameName + " " + roomJoined.getBetValue());
        }
    }

    private void playMaybach(User user, DataCmd dataCmd) {
        PlayMaybachCmd cmd = new PlayMaybachCmd(dataCmd);
        MayBachRoom room = (MayBachRoom) user.getProperty("MGROOM_" + this.gameName + "_INFO");
        if (room != null) {
            room.play(user, cmd.lines);
            BroadCastUserState.pushBroadCast(user.getName(), user.getName() + " play " + gameName + " " + room.getBetValue());
        }
    }

    private void autoPlay(User user, DataCmd dataCMD) {
        AutoPlayMaybachCmd cmd = new AutoPlayMaybachCmd(dataCMD);
        MayBachRoom room = (MayBachRoom) user.getProperty("MGROOM_" + this.gameName + "_INFO");
        if (room != null) {
            if (cmd.autoPlay == 1) {
                short result = room.play(user, cmd.lines);
                if (result != 3 && result != 4 && result != 101 && result != 102 && result != 100) {
                    room.autoPlay(user, cmd.lines, result);
                } else {
                    room.forceStopAutoPlay(user);
                }
            } else {
                room.stopAutoPlay(user);
            }
            BroadCastUserState.pushBroadCast(user.getName(), user.getName() + " play " + gameName + " " + room.getBetValue());
        }
    }

    @Override
    protected String getRoomName(short moneyType, long baseBetting) {
        String moneyTypeStr = "xu";
        if (moneyType == 1) {
            moneyTypeStr = "vin";
        }
        return this.gameName + "_" + moneyTypeStr + "_" + baseBetting;
    }

    @Override
    protected void gameLoop() {
        List<String> bots;
        MayBachRoom room;
        ++this.countBot100;
        if (this.countBot100 >= this.getCountTimeBot(this.gameName + "_bot_100")) {
            if (this.countBot100 == this.getCountTimeBot(this.gameName + "_bot_100")) {
                bots = BotMinigame.getBots(ConfigGame.getIntValue(this.gameName + "_num_bot_100"), "vin");
                for (String bot : bots) {
                    if (bot == null) continue;
                    room = (MayBachRoom) this.rooms.get(this.gameName + "_vin_100");
                    room.play(bot, this.fullLines);
                }
            }
            this.countBot100 = 0;
        }
        ++this.countBot1000;
        if (this.countBot1000 >= this.getCountTimeBot(this.gameName + "_bot_1000")) {
            if (this.countBot1000 == this.getCountTimeBot(this.gameName + "_bot_1000")) {
                bots = BotMinigame.getBots(ConfigGame.getIntValue(this.gameName + "_num_bot_1000"), "vin");
                for (String bot : bots) {
                    if (bot == null) continue;
                    room = (MayBachRoom) this.rooms.get(this.gameName + "_vin_1000");
                    room.play(bot, this.fullLines);
                }
            }
            this.countBot1000 = 0;
        }
        ++this.countBot10000;
        if (this.countBot10000 >= this.getCountTimeBot(this.gameName + "_bot_10000")) {
            if (this.countBot10000 == this.getCountTimeBot(this.gameName + "_bot_10000")) {
                bots = BotMinigame.getBots(ConfigGame.getIntValue(this.gameName + "_num_bot_10000"), "vin");
                for (String bot : bots) {
                    if (bot == null) continue;
                    room = (MayBachRoom) this.rooms.get(this.gameName + "_vin_10000");
                    room.play(bot, this.fullLines);
                }
            }
            this.countBot10000 = 0;
        }
    }
}

