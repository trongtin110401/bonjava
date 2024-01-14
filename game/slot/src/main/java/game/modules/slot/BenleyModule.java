
package game.modules.slot;

import bitzero.server.BitZeroServer;
import bitzero.server.core.BZEventParam;
import bitzero.server.core.BZEventType;
import bitzero.server.core.IBZEvent;
import bitzero.server.entities.User;
import bitzero.server.extensions.data.DataCmd;
import bitzero.util.common.business.Debug;
import com.vinplay.dal.common.BroadCastUserState;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.exceptions.KeyNotFoundException;
import com.vinplay.vbee.common.models.slot.SlotFreeSpin;
import com.vinplay.vbee.common.utils.CommonUtils;
import game.modules.slot.cmd.rev.audition.MinimizeAuditionCmd;
import game.modules.slot.cmd.rev.benley.*;
import game.modules.slot.cmd.send.benley.BenleyInfoMsg;
import game.modules.slot.entities.BotMinigame;
import game.modules.slot.room.BenleyRoom;
import game.modules.slot.utils.SlotUtils;
import game.util.ConfigGame;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BenleyModule extends SlotModule {

    private long referenceId = 1L;
    private final String fullLines = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25";

    public BenleyModule() {
        this.gameName = Games.BENLEY.getName();
    }

    public void init() {

        super.init();

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
            Debug.trace(this.gameName + " POTS: " + CommonUtils.arrayLongToString(this.jackpots));
            funds = this.service.getFunds(this.gameName);
            Debug.trace(this.gameName + ": " + CommonUtils.arrayLongToString(funds));
        } catch (Exception e) {
            Debug.trace("Init " + this.gameName + " error ", e);
        }

        this.rooms.put(this.gameName + "_vin_100", new BenleyRoom(this, (byte) 0, this.gameName + "_vin_100", (short) 1, this.jackpots[0], funds[0], 100, initJackpotValues[0]));
        this.rooms.put(this.gameName + "_vin_1000", new BenleyRoom(this, (byte) 1, this.gameName + "_vin_1000", (short) 1, this.jackpots[1], funds[1], 1000, initJackpotValues[1]));
        this.rooms.put(this.gameName + "_vin_10000", new BenleyRoom(this, (byte) 2, this.gameName + "_vin_10000", (short) 1, this.jackpots[2], funds[2], 10000, initJackpotValues[2]));
        Debug.trace("INIT " + this.gameName + " DONE");

        this.getParentExtension().addEventListener(BZEventType.USER_DISCONNECT, this);

        this.referenceId = this.slotService.getLastReferenceId(this.gameName);
        Debug.trace("START " + this.gameName + " REFERENCE ID= " + this.referenceId);

        try {
            CacheServiceImpl sv = new CacheServiceImpl();
            sv.removeKey(this.gameName + "_last_day_x2");
        } catch (KeyNotFoundException e2) {
            Debug.trace("KEY NOT FOUND");
        }

        int lastDayFinish = SlotUtils.getLastDayX2(this.gameName);
        this.ngayX2 = SlotUtils.calculateTimePokeGoX2AsString(this.gameName, SlotUtils.getX2Days(this.gameName), lastDayFinish);
        int nextX2Time = SlotUtils.calculateTimePokeGoX2(this.gameName, SlotUtils.getX2Days(this.gameName), lastDayFinish);
        Debug.trace(this.gameName + " Ngay X2: " + this.ngayX2 + ", remain time = " + nextX2Time);

        this.getParentExtension().addEventListener(BZEventType.USER_DISCONNECT, this);
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.gameLoopTask, 10, 1, TimeUnit.SECONDS);
    }

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
        BenleyRoom room = (BenleyRoom) user.getProperty("MGROOM_" + this.gameName + "_INFO");
        if (room != null) {
            room.quitRoom(user);
            room.stopAutoPlay(user);
        }
        BroadCastUserState.popBroadCast(user.getName());

    }

    @Override
    public void handleClientRequest(User user, DataCmd dataCmd) {
        Debug.trace("Avenger handleClientRequest " + dataCmd.getId());

        switch (dataCmd.getId()) {
            case 4003: {
                this.subScribe(user, dataCmd);
                break;
            }
            case 4004: {
                this.unSubScribe(user, dataCmd);
                break;
            }
            case 4005: {
                this.changeRoom(user, dataCmd);
                break;
            }
            case 4006: {
                this.autoPlay(user, dataCmd);
                break;
            }
            case 4001: {
                this.play(user, dataCmd);
                break;
            }
            case 4013: {
                this.minimize(user, dataCmd);
            }
        }
    }

    @Override
    public long getNewReferenceId() {
        return ++this.referenceId;
    }

    protected void subScribe(User user, DataCmd dataCmd) {
        SubscribeBenleyCmd cmd = new SubscribeBenleyCmd(dataCmd);
        BenleyRoom room = (BenleyRoom) this.getRoom(cmd.roomId);
        if (room != null) {
            room.joinRoom(user);
            room.userMaximize(user);
            room.updatePot(user);
            this.updateAvengerInfo(user, room);
        } else {
            Debug.trace(this.gameName + " SUBSCRIBE: room " + cmd.roomId + " not found");
        }
        BroadCastUserState.popBroadCast(user.getName());
    }

    private void updateAvengerInfo(User user, BenleyRoom room) {
        BenleyInfoMsg msg = new BenleyInfoMsg();
        msg.ngayX2 = this.ngayX2;
        msg.remain = 0;
        msg.currentMoney = this.userService.getMoneyUserCache(user.getName(), "vin");
        SlotFreeSpin freeSpin = this.slotService.getLuotQuayFreeSlot(String.valueOf(this.gameName) + room.getBetValue(), user.getName());
        if (freeSpin != null && freeSpin.getLines() != null) {
            msg.freeSpin = (byte) freeSpin.getNum();
            msg.lines = freeSpin.getLines();
        }
        this.send(msg, user);
    }

    protected void unSubScribe(User user, DataCmd dataCmd) {
        UnSubscribeBenleyCmd cmd = new UnSubscribeBenleyCmd(dataCmd);
        BenleyRoom room = (BenleyRoom) this.getRoom(cmd.roomId);
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
        BenleyRoom room = (BenleyRoom) this.getRoom(cmd.roomId);
        if (room != null) {
            room.quitRoom(user);
            room.userMinimize(user);
        } else {
            Debug.trace(this.gameName + " MINIMIZE: room " + cmd.roomId + " not found");
        }


    }

    protected void changeRoom(User user, DataCmd dataCmd) {
        ChangeRoomBenleyCmd cmd = new ChangeRoomBenleyCmd(dataCmd);
        BenleyRoom roomLeaved = (BenleyRoom) this.getRoom(cmd.roomLeavedId);
        BenleyRoom roomJoined = (BenleyRoom) this.getRoom(cmd.roomJoinedId);
        if (roomLeaved != null && roomJoined != null) {
            roomLeaved.stopAutoPlay(user);
            roomLeaved.quitRoom(user);
            roomJoined.joinRoom(user);
            roomJoined.updatePot(user);
            this.updateAvengerInfo(user, roomJoined);
        } else {
            Debug.trace(this.gameName + ": change room error, leaved= " + cmd.roomLeavedId + ", joined= " + cmd.roomJoinedId);
        }
        assert roomJoined != null;
        BroadCastUserState.pushBroadCast(user.getName(), user.getName() + " play " + gameName + " " + roomJoined.getBetValue());
    }

    private void play(User user, DataCmd dataCmd) {
        PlayBenleyCmd cmd = new PlayBenleyCmd(dataCmd);
        BenleyRoom room = (BenleyRoom) user.getProperty("MGROOM_" + this.gameName + "_INFO");
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

    private void autoPlay(User user, DataCmd dataCMD) {
        AutoPlayBenleyCmd cmd = new AutoPlayBenleyCmd(dataCMD);
        BenleyRoom room = (BenleyRoom) user.getProperty("MGROOM_" + this.gameName + "_INFO");
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
                    Logger.getLogger(BenleyModule.class.getName()).log(Level.SEVERE, null, ex);
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
        List<String> bots;
        BenleyRoom room;
        ++this.countBot100;
        if (this.countBot100 >= this.getCountTimeBot(this.gameName + "_bot_100")) {
            if (this.countBot100 == this.getCountTimeBot(this.gameName + "_bot_100")) {
                bots = BotMinigame.getBots(ConfigGame.getIntValue(this.gameName + "_num_bot_100"), "vin");
                for (String bot : bots) {
                    if (bot == null) continue;
                    room = (BenleyRoom) this.rooms.get(this.gameName + "_vin_100");
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
                    room = (BenleyRoom) this.rooms.get(this.gameName + "_vin_1000");
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
                    room = (BenleyRoom) this.rooms.get(this.gameName + "_vin_10000");
                    long referenceId = getNewReferenceId();
                    room.playNormal(bot, this.fullLines, referenceId);
                }
            }
            this.countBot10000 = 0;
        }
    }
}

