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
 *  bitzero.server.exceptions.BZException
 *  bitzero.server.extensions.BZExtension
 *  bitzero.server.extensions.data.DataCmd
 *  bitzero.server.util.TaskScheduler
 *  bitzero.util.ExtensionUtility
 *  bitzero.util.common.business.Debug
 *  bitzero.util.socialcontroller.bean.UserInfo
 *  com.vinplay.usercore.utils.GameCommon
 *  com.vinplay.vbee.common.hazelcast.HazelcastLoader
 *  com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory
 *  com.vinplay.vbee.common.pools.ConnectionPool
 *  com.vinplay.vbee.common.rmq.RMQApi
 */
package game;

import bitzero.engine.sessions.ISession;
import bitzero.server.BitZeroServer;
import bitzero.server.core.BZEventType;
import bitzero.server.core.IBZEventType;
import bitzero.server.entities.data.ISFSObject;
import bitzero.server.exceptions.BZException;
import bitzero.server.extensions.BZExtension;
import bitzero.server.extensions.data.DataCmd;
import bitzero.util.ExtensionUtility;
import bitzero.util.common.business.Debug;
import bitzero.util.socialcontroller.bean.UserInfo;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.usercore.utils.PartnerConfig;
import com.vinplay.vbee.common.hazelcast.HazelcastLoader;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.pools.ConnectionPool;
import com.vinplay.vbee.common.rmq.RMQApi;
import game.eventHandlers.LoginSuccessHandler;
import game.modules.slot.*;
import game.modules.slot.cmd.rev.LoginCmd;
import game.modules.slot.entities.BotMinigame;
import game.util.ConfigGame;
import game.util.GameUtils;

import java.util.concurrent.TimeUnit;

public class SlotMachineExtension
extends BZExtension {
    private int countReloadConfig = 0;
    private final Runnable gameLoopTask = new GameLoopTask();

    public void init() {
        try {
            RMQApi.start((String)"config/rmq.properties");
            HazelcastLoader.start();
            MongoDBConnectionFactory.init();
            ConnectionPool.start((String)"config/db_pool.properties");
            ConfigGame.reload();
            BotMinigame.loadData();
            GameCommon.init();
            try {
//                PartnerConfig.ReadConfig("config/partner.properties");
                PartnerConfig.ReadConfig();
            }
            catch (Exception e) {
                Debug.trace((Object)("init partnerconfig event error " + e));
            }        
        }
        catch (Exception e) {
            Debug.trace((Object)("INIT MINIGAME ERROR " + e.getMessage()));
        }
        this.addRequestHandler((short)10000, HallSlotModule.class);
        this.addRequestHandler((short)2000, AuditionModule.class);
        this.addRequestHandler((short)3000, MayBachModule.class);
        this.addRequestHandler((short)4000, BenleyModule.class);
        this.addRequestHandler((short)5000, RollRoyModule.class);
        this.addRequestHandler((short)12000, SpartanModule.class);
        this.addRequestHandler((short)13000, RangeRoverModule.class);
        this.addRequestHandler((short)14000, TamHungModule.class);



        this.addEventHandler((IBZEventType)BZEventType.USER_LOGIN, LoginSuccessHandler.class);
        this.addEventHandler((IBZEventType)BZEventType.USER_DISCONNECT, LoginSuccessHandler.class);
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.gameLoopTask, 10, 1, TimeUnit.SECONDS);
    }

    public void doLogin(short s, ISession iSession, DataCmd dataCmd) throws BZException {
        if (s != 1) {
            return;
        }
        LoginCmd cmd = new LoginCmd(dataCmd);
        UserInfo info = GameUtils.getUserInfo(cmd.nickname, cmd.sessionKey);
        if (info != null) {
            ExtensionUtility.instance().canLogin(info, "", iSession);
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
            SlotMachineExtension.this.gameLoop();
        }
    }

}

