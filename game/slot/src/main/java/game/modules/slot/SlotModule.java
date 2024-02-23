/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  bitzero.server.BitZeroServer
 *  bitzero.server.entities.User
 *  bitzero.server.extensions.BaseClientRequestHandler
 *  bitzero.server.extensions.data.BaseMsg
 *  bitzero.server.extensions.data.DataCmd
 *  bitzero.server.util.TaskScheduler
 *  com.vinplay.dal.service.MiniGameService
 *  com.vinplay.dal.service.SlotMachineService
 *  com.vinplay.dal.service.impl.MiniGameServiceImpl
 *  com.vinplay.dal.service.impl.SlotMachineServiceImpl
 *  com.vinplay.usercore.service.UserService
 *  com.vinplay.usercore.service.impl.UserServiceImpl
 */
package game.modules.slot;

import bitzero.server.entities.User;
import bitzero.server.extensions.BaseClientRequestHandler;
import bitzero.server.extensions.data.BaseMsg;
import bitzero.server.extensions.data.DataCmd;
import com.vinplay.dal.service.MiniGameService;
import com.vinplay.dal.service.SlotMachineService;
import com.vinplay.dal.service.impl.MiniGameServiceImpl;
import com.vinplay.dal.service.impl.SlotMachineServiceImpl;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import game.modules.slot.room.SlotRoom;
import game.util.ConfigGame;

import java.util.HashMap;
import java.util.Map;

public abstract class SlotModule extends BaseClientRequestHandler {

    protected String gameName;
    protected long[] jackpots = new long[4];
    protected long lastTimeUpdatePotToRoom = 0L;
    protected int countBot100;
    protected int countBot1000;
    protected int countBot5000;
    protected int countBot10000;

    protected Map<String, SlotRoom> rooms = new HashMap<>();
    protected GameLoopTask gameLoopTask = new GameLoopTask();

    protected MiniGameService service = new MiniGameServiceImpl();
    protected SlotMachineService slotService = new SlotMachineServiceImpl();
    protected UserService userService = new UserServiceImpl();

    public abstract void handleClientRequest(User user, DataCmd dataCmd);

    protected abstract String getRoomName(short var1, long var2);

    protected abstract void gameLoop();

    public abstract long getNewReferenceId();

    protected int getCountTimeBot(String name) {
        return ConfigGame.getIntValue(name, 0);
    }

    protected SlotRoom getRoom(byte roomId) {
        short moneyType = this.getMoneyTypeFromRoomId(roomId);
        long baseBetting = this.getBaseBetting(roomId);
        String roomName = this.getRoomName(moneyType, baseBetting);
        return this.rooms.get(roomName);
    }

    protected short getMoneyTypeFromRoomId(byte roomId) {
        if (roomId >= 0 && roomId < 4)
            return 1;
        return 0;
    }

    protected long getBaseBetting(byte roomId) {
        switch (roomId) {
            case 0: {
                return 100L;
            }
            case 1:
            case 4: {
                return 1000L;
            }
            case 2: {
                return 5000L;
            }
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

    protected void sendMessageToTS(BaseMsg msg) {
        for (SlotRoom room : this.rooms.values()) {
            room.sendMessageToRoom(msg);
        }
    }

    public void sendMsgToAllUsers(BaseMsg msg) {
        SendMsgToAlLUsersThread t = new SendMsgToAlLUsersThread(msg);
        t.start();
    }

    protected final class SendMsgToAlLUsersThread extends Thread {
        private BaseMsg msg;

        protected SendMsgToAlLUsersThread(BaseMsg msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            SlotModule.this.sendMessageToTS(this.msg);
        }
    }

    protected final class GameLoopTask implements Runnable {

        protected GameLoopTask() {
        }

        @Override
        public void run() {
            SlotModule.this.gameLoop();
        }
    }

}

