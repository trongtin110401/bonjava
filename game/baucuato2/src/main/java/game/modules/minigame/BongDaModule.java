package game.modules.minigame;

import bitzero.server.BitZeroServer;
import bitzero.server.core.BZEventParam;
import bitzero.server.core.BZEventType;
import bitzero.server.core.IBZEvent;
import bitzero.server.core.IBZEventParam;
import bitzero.server.entities.User;
import bitzero.server.exceptions.BZException;
import bitzero.server.extensions.BaseClientRequestHandler;
import bitzero.server.extensions.data.BaseMsg;
import bitzero.server.extensions.data.DataCmd;
import bitzero.util.ExtensionUtility;
import com.vinplay.dal.service.BroadcastMessageService;
import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.BroadcastMessageServiceImpl;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import game.modules.lobby.cmd.send.BroadcastMessageMsg;
import game.modules.minigame.cmd.rev.baucua.SubscribeBauCuaCmd;
import game.modules.minigame.cmd.rev.baucua.UnsubscribeBauCuaCmd;
import game.modules.minigame.cmd.send.bongda.UpdateBongDaMsg;
import game.modules.minigame.room.MGRoom;
import game.modules.minigame.room.MGRoomBauCuaTo2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BongDaModule extends BaseClientRequestHandler {

    private BroadcastMessageService broadcastMsg = new BroadcastMessageServiceImpl();
    protected List<User> users = new ArrayList<User>();
    private final Runnable gameLoopTask = new BongDaModule.GameLoopTask();
    private CacheService cacheService = new CacheServiceImpl();

    public void init() {
        super.init();
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.gameLoopTask, 10, 1, TimeUnit.SECONDS);

    }

    @Override
    public void handleClientRequest(User user, DataCmd dataCmd) {
        switch (dataCmd.getId()) {
            case 3001: {
                this.subscribeBauCua(user, dataCmd);
                break;
            }
            case 3002: {
                this.unsubscribeBauCua(user, dataCmd);
                break;
            }

        }
    }


    private final class GameLoopTask
            implements Runnable {
        private GameLoopTask() {
        }

        @Override
        public void run() {
            try {
                BongDaModule.this.gameLoop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void handleServerEvent(IBZEvent ibzevent) throws BZException {
        if (ibzevent.getType() == BZEventType.USER_DISCONNECT) {
            User user = (User) ibzevent.getParameter((IBZEventParam) BZEventParam.USER);
            this.userDis(user);
        }
    }

    private void userDis(User user) {
        this.quitRoom(user);
    }

    // todo : game loop life-cycle
    private synchronized void gameLoop() {
        try {
            int flagValue = cacheService.getValueInt("Bong_Da_Update_BE");
            if (flagValue > 0) {
                UpdateBongDaMsg msg = new UpdateBongDaMsg();
                msg.data = "1";
                this.sendMessageToRoom(msg);
                this.sendAllUser(msg);
                cacheService.setValue("Bong_Da_Update_BE", 0);
            }
        } catch (Exception e) {
            cacheService.setValue("Bong_Da_Update_BE", 0);
        }


    }


    private void subscribeBauCua(User user, DataCmd dataCmd) {
        this.joinRoom(user);
    }

    private void unsubscribeBauCua(User user, DataCmd dataCmd) {
        this.quitRoom(user);

    }

    public boolean joinRoom(User user) {
        List<User> list;
        List<User> list2 = list = this.users;
        synchronized (list2) {
            if (!this.users.contains((Object) user)) {
                this.users.add(user);
                return true;
            }
        }
        return false;
    }

    public boolean quitRoom(User user) {
        List<User> list;
        List<User> list2 = list = this.users;
        synchronized (list2) {
            if (this.users.contains((Object) user)) {
                this.users.remove((Object) user);
                return true;
            }
        }
        return false;
    }


    public synchronized void sendMessageToRoom(BaseMsg msg) {
        ArrayList<User> usersCopy = new ArrayList<User>(this.users);
        for (User user : usersCopy) {
            if (user == null) continue;
            ExtensionUtility.getExtension().send(msg, user);
        }
    }

    private void sendAllUser(BaseMsg msg) {
        List users = ExtensionUtility.globalUserManager.getAllUsers();
        if (users != null) {
            this.send((BaseMsg) msg, users);
        }
    }

    private void broadcastMessage() {
        String message = this.broadcastMsg.toJson();
        BroadcastMessageMsg msg = new BroadcastMessageMsg();
        msg.message = message;
        List users = ExtensionUtility.globalUserManager.getAllUsers();
        if (users != null) {
            this.send((BaseMsg) msg, users);
        }
        this.broadcastMsg.clearMessage();
    }
}
