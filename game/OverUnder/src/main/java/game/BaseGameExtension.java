/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  bitzero.engine.sessions.ISession
 *  bitzero.server.BitZeroServer
 *  bitzero.server.core.BZEventType
 *  bitzero.server.core.IBZEventType
 *  bitzero.server.entities.User
 *  bitzero.server.entities.data.ISFSObject
 *  bitzero.server.entities.managers.IUserManager
 *  bitzero.server.exceptions.BZException
 *  bitzero.server.extensions.BZExtension
 *  bitzero.server.extensions.data.DataCmd
 *  bitzero.server.util.TaskScheduler
 *  bitzero.util.ExtensionUtility
 *  bitzero.util.common.business.Debug
 *  bitzero.util.socialcontroller.bean.UserInfo
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IMap
 *  com.vinplay.dal.service.ServerInfoService
 *  com.vinplay.dal.service.impl.ServerInfoServiceImpl
 *  com.vinplay.usercore.utils.GameCommon
 *  com.vinplay.vbee.common.enums.Platform
 *  com.vinplay.vbee.common.hazelcast.HazelcastClientFactory
 *  com.vinplay.vbee.common.hazelcast.HazelcastLoader
 *  com.vinplay.vbee.common.models.cache.UserExtraInfoModel
 *  com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory
 *  com.vinplay.vbee.common.pools.ConnectionPool
 *  com.vinplay.vbee.common.rmq.RMQApi
 *  com.vinplay.vbee.common.utils.DateTimeUtils
 */
package game;

import bitzero.engine.sessions.ISession;
import bitzero.server.BitZeroServer;
import bitzero.server.core.BZEventType;
import bitzero.server.core.IBZEventType;
import bitzero.server.entities.User;
import bitzero.server.entities.data.ISFSObject;
import bitzero.server.exceptions.BZException;
import bitzero.server.extensions.BZExtension;
import bitzero.server.extensions.data.DataCmd;
import bitzero.util.ExtensionUtility;
import bitzero.util.common.business.Debug;
import bitzero.util.socialcontroller.bean.UserInfo;
import com.vinplay.dal.service.ServerInfoService;
import com.vinplay.dal.service.impl.ServerInfoServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.hazelcast.HazelcastLoader;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.pools.ConnectionPool;
import com.vinplay.vbee.common.rmq.RMQApi;
import game.eventHandlers.LoginSuccessHandler;
import game.eventHandlers.UserDisconnectHandler;
import game.modules.chat.ChatModule;
import game.modules.minigame.*;
import game.modules.minigame.entities.BotMinigame;
import game.modules.player.PlayerModule;
import game.modules.player.cmd.rev.LoginCmd;
import game.utils.ConfigGame;
import game.utils.GameUtils;
import java.util.concurrent.TimeUnit;

public class BaseGameExtension
extends BZExtension {
    private int countReloadConfig = 0;
    private int countLogCCU = 0;
    private final Runnable gameLoopTask = new GameLoopTask();
    private ServerInfoService serverInfoSrv = new ServerInfoServiceImpl();    

    public void init() {
        try {
            RMQApi.start((String)"config/rmq.properties");
            HazelcastLoader.start();
            MongoDBConnectionFactory.init();            
            ConnectionPool.start((String)"config/db_pool.properties");
            ConfigGame.reload();
            BotMinigame.loadData();
            GameCommon.init();
        }
        catch (Exception e) {
            Debug.trace((Object)("INIT MINIGAME ERROR " + e.getMessage()));
        }
        this.addRequestHandler((short)1000, PlayerModule.class);        
        this.addRequestHandler((short)2000, TaiXiuModule.class);            
        this.addRequestHandler((short)18000, ChatModule.class);            
        this.addEventHandler((IBZEventType)BZEventType.USER_LOGIN, LoginSuccessHandler.class);
        this.addEventHandler((IBZEventType)BZEventType.USER_DISCONNECT, UserDisconnectHandler.class);
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.gameLoopTask, 10, 1, TimeUnit.SECONDS);
    }

    public void doLogin(short s, ISession iSession, DataCmd dataCmd) throws BZException {
        User user;
        if (s != 1) {
            return;
        }
        LoginCmd cmd = new LoginCmd(dataCmd);
        UserInfo info = GameUtils.getUserInfo(cmd.nickname, cmd.sessionKey);
        if (info != null && (user = ExtensionUtility.instance().canLogin(info, "", iSession)) != null) {
            user.setProperty((Object)"dai_ly", (Object)info.getStatus());           
        }
    }

    public void doLogin(ISession iSession, ISFSObject iSFSObject) throws Exception {
    }

    private void gameLoop() {
        ++this.countReloadConfig;
        if (this.countReloadConfig == 300) {
            Debug.trace((Object)"reload config");
            ConfigGame.reload();
            this.countReloadConfig = 0;
        }        
    }

    private final class GameLoopTask
    implements Runnable {
        private GameLoopTask() {
        }

        @Override
        public void run() {
            BaseGameExtension.this.gameLoop();
        }
    }

}

