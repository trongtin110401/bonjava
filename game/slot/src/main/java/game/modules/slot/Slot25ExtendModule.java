
package game.modules.slot;

import bitzero.server.BitZeroServer;
import bitzero.server.core.BZEventParam;
import bitzero.server.core.BZEventType;
import bitzero.server.core.IBZEvent;
import bitzero.server.entities.User;
import bitzero.server.extensions.data.DataCmd;
import bitzero.util.common.business.Debug;
import com.vinplay.dal.common.BroadCastUserState;
import com.vinplay.vbee.common.models.slot.SlotFreeSpin;
import com.vinplay.vbee.common.utils.CommonUtils;
import game.modules.slot.cmd.Slot25CommandCollection;
import game.modules.slot.cmd.rev.audition.MinimizeAuditionCmd;
import game.modules.slot.cmd.rev.slot25extend.*;
import game.modules.slot.cmd.send.slot25extend.Slot25InfoMsg;
import game.modules.slot.cmd.send.slot25extend.Slot25UpdatePotMsg;
import game.modules.slot.entities.BotMinigame;
import game.modules.slot.listener.SlotLogListener;
import game.modules.slot.room.Slot25ExtendRoom;
import game.modules.slot.utils.SlotUtils;
import game.util.ConfigGame;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Slot25ExtendModule extends SlotModule {
    private long referenceId = 1L;
    private final String fullLines = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25";

    private final byte[] x2Arr = new byte[4];

    private Slot25CommandCollection commandCollection;
    private SlotLogListener slotLogListener;

    public Slot25ExtendModule(String gameName) {
        this.gameName = gameName;
    }


    public void init() {
        // super call
        super.init();
        // message command collection
        commandCollection = initMessageCommand();
        // log listener
        slotLogListener = initLogListener();
        // quỹ thưởng
        long[] funds = new long[3];
        //  jacpot
        int[] initJackpotValues = new int[6];
        try {
            String initPotValuesStr = ConfigGame.getValueString(this.gameName + "_init_pot_values");
            String[] arr = initPotValuesStr.split(",");
            for (int i = 0; i < arr.length; ++i) {
                initJackpotValues[i] = Integer.parseInt(arr[i]);
            }

            this.jackpots = this.service.getPots(this.gameName);
            if (jackpots == null || jackpots.length == 0) {
                jackpots = new long[4];
                jackpots[0] = 5000000;
                jackpots[1] = 50000000;
                jackpots[2] = 500000000;
                jackpots[3] = 0;
            }

            Debug.trace(this.gameName + " POTS: " + CommonUtils.arrayLongToString(this.jackpots));
            funds = this.service.getFunds(this.gameName);
            Debug.trace(this.gameName + ": " + CommonUtils.arrayLongToString(funds));
        } catch (Exception e) {
            Debug.trace("Init " + this.gameName + " error ", e);
        }

//        this.rooms.put(this.gameName + "_vin_100",
//                new Slot25ExtendRoom(this, this.commandCollection, slotLogListener, this.gameName, (byte) 0, this.gameName + "_vin_100", (short) 1, this.jackpots[0], funds[0], 100, initJackpotValues[0]));
//        this.rooms.put(this.gameName + "_vin_1000",
//                new Slot25ExtendRoom(this, this.commandCollection, slotLogListener, this.gameName, (byte) 1, this.gameName + "_vin_1000", (short) 1, this.jackpots[1], funds[1], 1000, initJackpotValues[1]));
//        this.rooms.put(this.gameName + "_vin_10000",
//                new Slot25ExtendRoom(this, this.commandCollection, slotLogListener, this.gameName, (byte) 2, this.gameName + "_vin_10000", (short) 1, this.jackpots[2], funds[2], 10000, initJackpotValues[2]));

        this.rooms.put(this.gameName + "_vin_1000",
                new Slot25ExtendRoom(this, this.commandCollection, slotLogListener, this.gameName, (byte) 0, this.gameName + "_vin_1000", (short) 1, this.jackpots[0], 10000000000L, 1000, initJackpotValues[0]));
        this.rooms.put(this.gameName + "_vin_10000",
                new Slot25ExtendRoom(this, this.commandCollection, slotLogListener, this.gameName, (byte) 1, this.gameName + "_vin_10000", (short) 1, this.jackpots[1], 10000000000L, 10000, initJackpotValues[1]));
        this.rooms.put(this.gameName + "_vin_100000",
                new Slot25ExtendRoom(this, this.commandCollection, slotLogListener, this.gameName, (byte) 2, this.gameName + "_vin_100000", (short) 1, this.jackpots[2], 10000000000L, 100000, initJackpotValues[2]));

        Debug.trace("INIT " + this.gameName + " DONE");

        this.referenceId = this.slotService.getLastReferenceId(this.gameName);
        Debug.trace("START " + this.gameName + " REFERENCE ID= " + this.referenceId);

        this.getParentExtension().addEventListener(BZEventType.USER_DISCONNECT, this);
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.gameLoopTask, 10, 1, TimeUnit.SECONDS);
    }

    protected abstract Slot25CommandCollection initMessageCommand();

    protected abstract SlotLogListener initLogListener();

    @Override
    public long getBaseBetting(byte roomId) {
        switch (roomId) {
            case 0: {
                return 100L;
            }
            case 1:
            case 4: {
                return 1000L;
            }
            case 2:
            case 3:
            case 5: {
                return 10000L;
            }
            case 6: {
                return 100000L;
            }
        }
        return 0L;
    }

    public void handleServerEvent(IBZEvent ibzevent) {
        if (ibzevent.getType() == BZEventType.USER_DISCONNECT) {
            User user = (User) ibzevent.getParameter(BZEventParam.USER);
            this.userDis(user);
        }
    }

    private void userDis(User user) {
        Slot25ExtendRoom room = (Slot25ExtendRoom) user.getProperty("MGROOM_" + this.gameName + "_INFO");
        if (room != null) {
            room.quitRoom(user);
            room.stopAutoPlay(user);
        }
        BroadCastUserState.popBroadCast(user.getName());
    }

    @Override
    public long getNewReferenceId() {
        return ++this.referenceId;
    }

    protected void subScribe(User user, DataCmd dataCmd) {
        SubscribeCmd cmd = new SubscribeCmd(dataCmd);
        Slot25ExtendRoom room = (Slot25ExtendRoom) this.getRoom(cmd.roomId);
        if (room != null) {
            room.joinRoom(user);
            room.userMaximize(user);
            this.updatePotToUser(user);
            this.updateRoomInfo(user, room);
        } else {
            Debug.trace(this.gameName + " SUBSCRIBE: room " + cmd.roomId + " not found");
        }
        BroadCastUserState.popBroadCast(user.getName());
    }

    private void updateRoomInfo(User user, Slot25ExtendRoom room) {
        Slot25InfoMsg msg = new Slot25InfoMsg(commandCollection.INFO_MESSAGE);
        msg.ngayX2 = "";
        msg.remain = 0;
        msg.currentMoney = this.userService.getMoneyUserCache(user.getName(), "vin");
        SlotFreeSpin freeSpin = this.slotService.getLuotQuayFreeSlot(this.gameName + room.getBetValue(), user.getName());
        if (freeSpin != null && freeSpin.getLines() != null) {
            msg.freeSpin = (byte) freeSpin.getNum();
            msg.lines = freeSpin.getLines();
        }
        this.send(msg, user);
    }

    protected void unSubScribe(User user, DataCmd dataCmd) {
        UnSubscribeCmd cmd = new UnSubscribeCmd(dataCmd);
        Slot25ExtendRoom room = (Slot25ExtendRoom) this.getRoom(cmd.roomId);
        if (room != null) {
            room.stopAutoPlay(user);
            room.quitRoom(user);
        } else {
            Debug.trace(this.gameName + " UNSUBSCRIBE: room " + cmd.roomId + " not found");
        }
        BroadCastUserState.popBroadCast(user.getName());
    }

    protected void minimize(User user, DataCmd dataCmd) {
        MinimizeAuditionCmd cmd = new MinimizeAuditionCmd(dataCmd);
        Slot25ExtendRoom room = (Slot25ExtendRoom) this.getRoom(cmd.roomId);
        if (room != null) {
            room.quitRoom(user);
            room.userMinimize(user);
        } else {
            Debug.trace(this.gameName + " MINIMIZE: room " + cmd.roomId + " not found");
        }
    }

    protected void changeRoom(User user, DataCmd dataCmd) {
        ChangeRoomCmd cmd = new ChangeRoomCmd(dataCmd);
        Slot25ExtendRoom roomLeaved = (Slot25ExtendRoom) this.getRoom(cmd.roomLeavedId);
        Slot25ExtendRoom roomJoined = (Slot25ExtendRoom) this.getRoom(cmd.roomJoinedId);
        if (roomLeaved != null && roomJoined != null) {
            roomLeaved.stopAutoPlay(user);
            roomLeaved.quitRoom(user);
            roomJoined.joinRoom(user);
            this.updatePotToUser(user);
            this.updateRoomInfo(user, roomJoined);
        } else {
            Debug.trace(this.gameName + ": change room error, leaved= " + cmd.roomLeavedId + ", joined= " + cmd.roomJoinedId);
        }

        if (roomJoined != null) {
            BroadCastUserState.pushBroadCast(user.getName(), user.getName() + " play " + gameName + " " + roomJoined.getBetValue());
        }
    }

    protected void play(User user, DataCmd dataCmd) {
        PlayCmd cmd = new PlayCmd(dataCmd);
        Slot25ExtendRoom room = (Slot25ExtendRoom) user.getProperty("MGROOM_" + this.gameName + "_INFO");
        if (room != null) {
            try {
                room.play(user, cmd.lines);
            } catch (Exception ex) {
                Debug.trace(ExceptionUtils.getStackTrace(ex));
            }
        }
        assert room != null;
        BroadCastUserState.pushBroadCast(user.getName(), user.getName() + " play " + gameName + " " + room.getBetValue());
    }

    protected void autoPlay(User user, DataCmd dataCMD) {
        AutoPlayCmd cmd = new AutoPlayCmd(dataCMD);
        Slot25ExtendRoom room = (Slot25ExtendRoom) user.getProperty("MGROOM_" + this.gameName + "_INFO");
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
                    Logger.getLogger(Slot25BasicModule.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                room.stopAutoPlay(user);
            }
        }
        assert room != null;
        BroadCastUserState.pushBroadCast(user.getName(), user.getName() + " play " + gameName + " " + room.getBetValue());
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
        // FORCE - R

        List<String> bots;
        Slot25ExtendRoom room;
        ++this.countBot100;
        if (this.countBot100 >= this.getCountTimeBot(this.gameName + "_bot_100")) {
            if (this.countBot100 == this.getCountTimeBot(this.gameName + "_bot_100")) {
                bots = BotMinigame.getBots(ConfigGame.getIntValue(this.gameName + "_num_bot_100"), "vin");
                for (String bot : bots) {
                    if (bot == null) continue;
                    room = (Slot25ExtendRoom) this.rooms.get(this.gameName + "_vin_100");
                    long referenceId = getNewReferenceId();
                    room.playNormal(bot, this.fullLines, referenceId);
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
                    room = (Slot25ExtendRoom) this.rooms.get(this.gameName + "_vin_1000");
                    long referenceId = getNewReferenceId();
                    room.playNormal(bot, this.fullLines, referenceId);
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
                    room = (Slot25ExtendRoom) this.rooms.get(this.gameName + "_vin_10000");
                    long referenceId = getNewReferenceId();
                    room.playNormal(bot, this.fullLines, referenceId);
                }
            }
            this.countBot10000 = 0;
        }
    }

    public void updatePot(byte id, long value, byte x2) {
        this.jackpots[id] = value;
        this.x2Arr[id] = x2;
        long currentTime = System.currentTimeMillis();
        Slot25UpdatePotMsg msg = this.getPotsInfo();
        this.lastTimeUpdatePotToRoom = System.currentTimeMillis();
        SendMsgToAlLUsersThread t = new SendMsgToAlLUsersThread(msg);
        t.start();
    }

    public void updatePotToUser(User user) {
        System.out.println("update pot to user: " + jackpots[0] + "-" + jackpots[1] + "-" + jackpots[2]);
        Slot25UpdatePotMsg msg = this.getPotsInfo();
        SlotUtils.sendMessageToUser(msg, user);
    }

    public Slot25UpdatePotMsg getPotsInfo() {
        Slot25UpdatePotMsg msg = new Slot25UpdatePotMsg(commandCollection.UPDATE_POT_MESSAGE);
        msg.value100 = this.jackpots[0];
        msg.value1000 = this.jackpots[1];
        msg.value5000 = 0;
        msg.value10000 = this.jackpots[2];
        msg.x2Room100 = this.x2Arr[0];
        msg.x2Room1000 = this.x2Arr[1];
        return msg;
    }
}

