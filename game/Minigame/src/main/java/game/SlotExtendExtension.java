package game;

import bitzero.engine.sessions.ISession;
import bitzero.server.BitZeroServer;
import bitzero.server.core.BZEventType;
import bitzero.server.entities.User;
import bitzero.server.entities.data.ISFSObject;
import bitzero.server.exceptions.BZException;
import bitzero.server.extensions.BZExtension;
import bitzero.server.extensions.data.DataCmd;
import bitzero.util.ExtensionUtility;
import bitzero.util.common.business.Debug;
import bitzero.util.socialcontroller.bean.UserInfo;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.hazelcast.HazelcastLoader;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.pools.ConnectionPool;
import com.vinplay.vbee.common.rmq.RMQApi;
import game.eventHandlers.LoginSuccessHandler;
import game.eventHandlers.UserDisconnectHandler;
import game.modules.minigame.Slot3x3ExtendModule;
import game.modules.player.cmd.rev.LoginCmd;
import game.utils.ConfigGame;
import game.utils.GameUtils;

import java.util.concurrent.TimeUnit;


public class SlotExtendExtension
        extends BZExtension {
    private int countReloadConfig = 0;
    private final Runnable gameLoopTask;


    public SlotExtendExtension() {
        this.gameLoopTask = new GameLoopTask();
    }

    public void init() {
        try {
            RMQApi.start("config/rmq.properties");
            HazelcastLoader.start();
            MongoDBConnectionFactory.init();
            ConnectionPool.start("config/db_pool.properties");
            ConfigGame.reload();
            GameCommon.init();

        } catch (Exception e) {
            Debug.trace("INIT SLOT EXTEND ERROR" + e.getMessage());
        }
        //addRequestHandler((short) 9000, Slot3x3ExtendModule.class);

        addEventHandler(BZEventType.USER_LOGIN, LoginSuccessHandler.class);
        addEventHandler(BZEventType.USER_DISCONNECT, UserDisconnectHandler.class);
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.gameLoopTask, 10, 1, TimeUnit.SECONDS);
    }

    public void doLogin(short s, ISession iSession, DataCmd dataCmd) throws BZException {
        if (s != 1) {
            return;
        }
        LoginCmd cmd = new LoginCmd(dataCmd);
        UserInfo info = GameUtils.getUserInfo(cmd.nickname, cmd.sessionKey);
        User user = ExtensionUtility.instance().canLogin(info, "", iSession);
        if ((info != null) && user != null) {
            user.setProperty("dai_ly", info.getStatus());
        }
    }

    @Override
    public void doLogin(ISession iSession, ISFSObject isfsObject) throws Exception {

    }

    private void gameLoop() {
        this.countReloadConfig += 1;
        if (this.countReloadConfig == 300) {
            Debug.trace("reload config");
            ConfigGame.reload();
            this.countReloadConfig = 0;
        }

    }

    private final class GameLoopTask
            implements Runnable {
        private GameLoopTask() {
        }

        public void run() {
            SlotExtendExtension.this.gameLoop();
        }
    }
}
