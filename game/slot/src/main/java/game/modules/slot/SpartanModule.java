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
 *  com.vinplay.vbee.common.models.slot.SlotFreeSpin
 *  com.vinplay.vbee.common.utils.CommonUtils
 */
package game.modules.slot;

import bitzero.server.BitZeroServer;
import bitzero.server.core.*;
import bitzero.server.entities.User;
import bitzero.server.exceptions.BZException;
import bitzero.server.extensions.data.BaseMsg;
import bitzero.server.extensions.data.DataCmd;
import bitzero.util.common.business.Debug;
import com.vinplay.dal.common.BroadCastUserState;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.exceptions.KeyNotFoundException;
import com.vinplay.vbee.common.models.slot.SlotFreeSpin;
import com.vinplay.vbee.common.utils.CommonUtils;
import game.modules.slot.cmd.rev.audition.MinimizeAuditionCmd;
import game.modules.slot.cmd.rev.spartan.*;
import game.modules.slot.cmd.send.spartan.SpartanInfoMsg;
import game.modules.slot.entities.BotMinigame;

import game.modules.slot.room.SpartanRoom;
import game.modules.slot.utils.SlotUtils;
import game.util.ConfigGame;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SpartanModule
extends SlotModule {
    private long referenceId = 1L;
    private String fullLines = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25";
    private Runnable pokeGoX2Task = new X2Task();
//    private Runnable pokeGoX2Task = new X2Task(this);

    public SpartanModule() {
        this.gameName = Games.SPARTAN.getName();
    }

    public void init() {
        super.init();
        long[] funds = new long[3];
        int[] initPotValues = new int[6];
        try {
            String initPotValuesStr = ConfigGame.getValueString(String.valueOf(this.gameName) + "_init_pot_values");
            String[] arr = initPotValuesStr.split(",");
            for (int i = 0; i < arr.length; ++i) {
                initPotValues[i] = Integer.parseInt(arr[i]);
            }
            this.jackpots = this.service.getPots(this.gameName);
            Debug.trace((Object)(String.valueOf(this.gameName) + " POTS: " + CommonUtils.arrayLongToString((long[])this.jackpots)));
            funds = this.service.getFunds(this.gameName);
            Debug.trace((Object)(String.valueOf(this.gameName) + ": " + CommonUtils.arrayLongToString((long[])funds)));
        }
        catch (Exception e) {
            Debug.trace((Object[])new Object[]{"Init " + this.gameName + " error ", e});
        }
        this.rooms.put(String.valueOf(this.gameName) + "_vin_100", new SpartanRoom(this, (byte)0, String.valueOf(this.gameName) + "_vin_100", (short)1, this.jackpots[0], funds[0], 100, initPotValues[0]));
        this.rooms.put(String.valueOf(this.gameName) + "_vin_1000", new SpartanRoom(this, (byte)1, String.valueOf(this.gameName) + "_vin_1000", (short)1, this.jackpots[1], funds[1], 1000, initPotValues[1]));
        this.rooms.put(String.valueOf(this.gameName) + "_vin_5000", new SpartanRoom(this, (byte)2, String.valueOf(this.gameName) + "_vin_5000", (short)1, this.jackpots[2], funds[2], 5000, initPotValues[2]));
        this.rooms.put(String.valueOf(this.gameName) + "_vin_10000", new SpartanRoom(this, (byte)3, String.valueOf(this.gameName) + "_vin_10000", (short)1, this.jackpots[3], funds[3], 10000, initPotValues[3]));
        Debug.trace((Object)("INIT " + this.gameName + " DONE"));
        this.getParentExtension().addEventListener((IBZEventType)BZEventType.USER_DISCONNECT, (IBZEventListener)this);
        this.referenceId = this.slotService.getLastReferenceId(this.gameName);
        Debug.trace((Object)("START " + this.gameName + " REFERENCE ID= " + this.referenceId));
        CacheServiceImpl sv = new CacheServiceImpl();
        try {
            sv.removeKey(String.valueOf(this.gameName) + "_last_day_x2");
        }
        catch (KeyNotFoundException e2) {
            Debug.trace((Object)"KEY NOT FOUND");
        }
        int lastDayFinish = SlotUtils.getLastDayX2(this.gameName);
        this.ngayX2 = SlotUtils.calculateTimePokeGoX2AsString(this.gameName, SlotUtils.getX2Days(this.gameName), lastDayFinish);
        int nextX2Time = SlotUtils.calculateTimePokeGoX2(this.gameName, SlotUtils.getX2Days(this.gameName), lastDayFinish);
        Debug.trace((Object)(String.valueOf(this.gameName) + " Ngay X2: " + this.ngayX2 + ", remain time = " + nextX2Time));
        /*if (nextX2Time >= 0) {
            BitZeroServer.getInstance().getTaskScheduler().schedule(this.pokeGoX2Task, nextX2Time, TimeUnit.SECONDS);
        } else {
            this.startX2();
        }*/
        this.getParentExtension().addEventListener((IBZEventType)BZEventType.USER_DISCONNECT, (IBZEventListener)this);
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate((Runnable)this.gameLoopTask, 10, 1, TimeUnit.SECONDS);        
    }

    public void handleServerEvent(IBZEvent ibzevent) throws BZException {
        if (ibzevent.getType() == BZEventType.USER_DISCONNECT) {
            User user = (User)ibzevent.getParameter((IBZEventParam)BZEventParam.USER);
            this.userDis(user);
        }
    }

    private void userDis(User user) {
        SpartanRoom room = (SpartanRoom)user.getProperty((Object)("MGROOM_" + this.gameName + "_INFO"));
        if (room != null) {
            room.quitRoom(user);
            room.stopAutoPlay(user);
        }
        BroadCastUserState.popBroadCast(user.getName());

    }

    @Override
    public void handleClientRequest(User user, DataCmd dataCmd) {
        Debug.trace((Object)("Spartan  handleClientRequest " + dataCmd.getId()));
        switch (dataCmd.getId()) {
            case 12003: {
                this.subScribe(user, dataCmd);
                break;
            }
            case 12004: {
                this.unSubScribe(user, dataCmd);
                break;
            }
            case 12005: {
                this.changeRoom(user, dataCmd);
                break;
            }
            case 12006: {
                this.autoPlay(user, dataCmd);
                break;
            }
            case 12001: {
                this.play(user, dataCmd);
                break;
            }
            case 12013: {
                this.minimize(user, dataCmd);
            }
        }
    }

    @Override
    public long getNewReferenceId() {
        return ++this.referenceId;
    }

    protected void subScribe(User user, DataCmd dataCmd) {
        SubscribeSpartanCmd cmd = new SubscribeSpartanCmd(dataCmd);
        SpartanRoom room = (SpartanRoom)this.getRoom(cmd.roomId);
        if (room != null) {
            room.joinRoom(user);
            room.userMaximize(user);
            room.updatePot(user);
            this.updateSpartanInfo(user, room);
        } else {
            Debug.trace((Object)(String.valueOf(this.gameName) + " SUBSCRIBE: room " + cmd.roomId + " not found"));
        }

        BroadCastUserState.popBroadCast(user.getName());
    }

    private void updateSpartanInfo(User user, SpartanRoom room) {
        SpartanInfoMsg msg = new SpartanInfoMsg();
        msg.ngayX2 = this.ngayX2;
        msg.remain = 0;
        msg.currentMoney = this.userService.getMoneyUserCache(user.getName(), "vin");
        SlotFreeSpin freeSpin = this.slotService.getLuotQuayFreeSlot(String.valueOf(this.gameName) + room.getBetValue(), user.getName());
        if (freeSpin != null && freeSpin.getLines() != null) {
            msg.freeSpin = (byte)freeSpin.getNum();
            msg.lines = freeSpin.getLines();
        }
        this.send((BaseMsg)msg, user);
    }

    protected void unSubScribe(User user, DataCmd dataCmd) {
        UnSubscribeSpartanCmd cmd = new UnSubscribeSpartanCmd(dataCmd);
        SpartanRoom room = (SpartanRoom)this.getRoom(cmd.roomId);
        if (room != null) {
            room.stopAutoPlay(user);
            room.quitRoom(user);
        } else {
            Debug.trace((Object)(String.valueOf(this.gameName) + " UNSUBSCRIBE: room " + cmd.roomId + " not found"));
        }
        BroadCastUserState.popBroadCast(user.getName());

    }

    protected void minimize(User user, DataCmd dataCmd) {
        MinimizeAuditionCmd cmd = new MinimizeAuditionCmd(dataCmd);
        SpartanRoom room = (SpartanRoom)this.getRoom(cmd.roomId);
        if (room != null) {
            room.quitRoom(user);
            room.userMinimize(user);
        } else {
            Debug.trace((Object)(String.valueOf(this.gameName) + " MINIMIZE: room " + cmd.roomId + " not found"));
        }
    }

    protected void changeRoom(User user, DataCmd dataCmd) {
        ChangeRoomSpartanCmd cmd = new ChangeRoomSpartanCmd(dataCmd);
        SpartanRoom roomLeaved = (SpartanRoom)this.getRoom(cmd.roomLeavedId);
        SpartanRoom roomJoined = (SpartanRoom)this.getRoom(cmd.roomJoinedId);
        if (roomLeaved != null && roomJoined != null) {
            roomLeaved.stopAutoPlay(user);
            roomLeaved.quitRoom(user);
            roomJoined.joinRoom(user);
            roomJoined.updatePot(user);
            this.updateSpartanInfo(user, roomJoined);
        } else {
            Debug.trace((Object)(String.valueOf(this.gameName) + ": change room error, leaved= " + cmd.roomLeavedId + ", joined= " + cmd.roomJoinedId));
        }

            BroadCastUserState.pushBroadCast(user.getName(),user.getName()+" play " + gameName +" " +roomJoined.getBetValue());


    }

    private void play(User user, DataCmd dataCmd) {
        PlaySpartanCmd cmd = new PlaySpartanCmd(dataCmd);
        SpartanRoom room = (SpartanRoom)user.getProperty((Object)("MGROOM_" + this.gameName + "_INFO"));
        if (room != null) {
            try {
                room.play(user, cmd.lines);
            } catch (Exception ex) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                String sStackTrace = sw.toString(); // stack trace as a string
                Debug.trace((Object)sStackTrace);               
                Debug.trace((Object)ex.getMessage());
            }
        }

            BroadCastUserState.pushBroadCast(user.getName(),user.getName()+" play " + gameName +" " +room.getBetValue());


    }

    private void autoPlay(User user, DataCmd dataCMD) {
        AutoPlaySpartanCmd cmd = new AutoPlaySpartanCmd(dataCMD);
        SpartanRoom room = (SpartanRoom)user.getProperty((Object)("MGROOM_" + this.gameName + "_INFO"));
        if (room != null) {
            if (cmd.autoPlay == 1) {
                try {
                    short result = room.play(user, cmd.lines);
                    if (result != 3 && result != 4 && result != 101 && result != 102 && result != 100) {
                        room.autoPlay(user, cmd.lines, result);
                    } else {
                        room.forceStopAutoPlay(user);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(SpartanModule.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                room.stopAutoPlay(user);
            }
        }

            BroadCastUserState.pushBroadCast(user.getName(),user.getName()+" play " + gameName +" " +room.getBetValue());


    }

    @Override
    protected String getRoomName(short moneyType, long baseBetting) {
        String moneyTypeStr = "xu";
        if (moneyType == 1) {
            moneyTypeStr = "vin";
        }
        return String.valueOf(this.gameName) + "_" + moneyTypeStr + "_" + baseBetting;
    }

    @Override
    protected void gameLoop() {
        List<String> bots;
        SpartanRoom room;
        ++this.countBot100;
        if (this.countBot100 >= this.getCountTimeBot(String.valueOf(this.gameName) + "_bot_100")) {
            if (this.countBot100 == this.getCountTimeBot(String.valueOf(this.gameName) + "_bot_100")) {
                bots = BotMinigame.getBots(ConfigGame.getIntValue(String.valueOf(this.gameName) + "_num_bot_100"), "vin");
                for (String bot : bots) {
                    if (bot == null) continue;
                    room = (SpartanRoom)this.rooms.get(String.valueOf(this.gameName) + "_vin_100");
                    long referenceId = getNewReferenceId();
                    room.playFull(bot, this.fullLines, referenceId);
                }
            }
            this.countBot100 = 0;
        }
        ++this.countBot1000;
        if (this.countBot1000 >= this.getCountTimeBot(String.valueOf(this.gameName) + "_bot_1000")) {
            if (this.countBot1000 == this.getCountTimeBot(String.valueOf(this.gameName) + "_bot_1000")) {
                bots = BotMinigame.getBots(ConfigGame.getIntValue(String.valueOf(this.gameName) + "_num_bot_1000"), "vin");
                for (String bot : bots) {
                    if (bot == null) continue;
                    room = (SpartanRoom)this.rooms.get(String.valueOf(this.gameName) + "_vin_1000");
                    long referenceId = getNewReferenceId();
                    room.playFull(bot, this.fullLines, referenceId);
                }
            }
            this.countBot1000 = 0;
        }
        ++this.countBot5000;
        if (this.countBot5000 >= this.getCountTimeBot(String.valueOf(this.gameName) + "_bot_5000")) {
            if (this.countBot5000 == this.getCountTimeBot(String.valueOf(this.gameName) + "_bot_5000")) {
                bots = BotMinigame.getBots(ConfigGame.getIntValue(String.valueOf(this.gameName) + "_num_bot_5000"), "vin");
                for (String bot : bots) {
                    if (bot == null) continue;
                    room = (SpartanRoom)this.rooms.get(String.valueOf(this.gameName) + "_vin_5000");
                    long referenceId = getNewReferenceId();
                    room.playFull(bot, this.fullLines, referenceId);
                }
            }
            this.countBot5000 = 0;
        }
        ++this.countBot10000;
        if (this.countBot10000 >= this.getCountTimeBot(String.valueOf(this.gameName) + "_bot_10000")) {
            if (this.countBot10000 == this.getCountTimeBot(String.valueOf(this.gameName) + "_bot_10000")) {
                bots = BotMinigame.getBots(ConfigGame.getIntValue(String.valueOf(this.gameName) + "_num_bot_10000"), "vin");
                for (String bot : bots) {
                    if (bot == null) continue;
                    room = (SpartanRoom)this.rooms.get(String.valueOf(this.gameName) + "_vin_10000");
                    long referenceId = getNewReferenceId();
                    room.playFull(bot, this.fullLines, referenceId);
                }
            }
            this.countBot10000 = 0;
        }
    }
}

