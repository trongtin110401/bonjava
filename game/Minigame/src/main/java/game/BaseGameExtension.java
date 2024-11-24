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
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.dal.service.ServerInfoService;
import com.vinplay.dal.service.impl.ServerInfoServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.usercore.utils.PartnerConfig;
import com.vinplay.vbee.common.enums.Platform;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.hazelcast.HazelcastLoader;
import com.vinplay.vbee.common.models.cache.UserExtraInfoModel;
import com.vinplay.vbee.common.mongodb.MongoDBConnectionFactory;
import com.vinplay.vbee.common.pools.ConnectionPool;
import com.vinplay.vbee.common.rmq.RMQApi;
import com.vinplay.vbee.common.utils.DateTimeUtils;
import game.eventHandlers.LoginSuccessHandler;
import game.eventHandlers.UserDisconnectHandler;
import game.modules.admin.AdminModule;
import game.modules.chat.ChatMd5Module;
import game.modules.chat.ChatModule;
import game.modules.chat.ChatTXKubetModule;
import game.modules.gameRoom.GameRoomModule;
import game.modules.lobby.LobbyModule;
import game.modules.minigame.*;
import game.modules.minigame.entities.BotMinigame;
import game.modules.mission.MissionModule;
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
    private int lastCCU = 0;
    public static int ccuWeb = 0;
    public static int ccuAD = 0;
    public static int ccuIOS = 0;
    public static int ccuWP = 0;
    public static int ccuFB = 0;
    public static int ccuDT = 0;

    public void init() {
        try {
            Debug.trace((Object) ("INIT MINIGAME"));
            RMQApi.start((String) "config/rmq.properties");
            HazelcastLoader.start();
            MongoDBConnectionFactory.init();
            ConnectionPool.start((String) "config/db_pool.properties");
            ConfigGame.reload(); // todo : lấy config game từ trong mongo db
            BotMinigame.loadData(); // todo load bot
            PartnerConfig.ReadConfig(); // todo: đọc hả
            // config từ bên third party
            GameCommon.init(); // todo khởi tạo common bao gồm key ừ bên third party
        } catch (Exception e) {
            Debug.trace((Object) ("INIT MINIGAME ERROR " + e.getMessage()));
        }
        this.addRequestHandler((short) 1000, PlayerModule.class);
        if (GameUtils.gameName.equalsIgnoreCase("Minigame")) { // todo :lấy từ config cluster file conf
            this.addRequestHandler((short) 4000, MiniPokerModule.class);
            this.addRequestHandler((short) 6000, CaoThapModule.class);
            this.addRequestHandler((short) 7000, CandyModule.class);
            this.addRequestHandler((short) 18000, ChatModule.class);
            this.addRequestHandler((short) 17000, ChatMd5Module.class);
            this.addRequestHandler((short) 19000, ChatTXKubetModule.class);
            this.addRequestHandler((short) 20000, LobbyModule.class);
        }

        this.addEventHandler((IBZEventType) BZEventType.USER_LOGIN, LoginSuccessHandler.class);
        this.addEventHandler((IBZEventType) BZEventType.USER_DISCONNECT, UserDisconnectHandler.class);
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
            user.setProperty((Object) "dai_ly", (Object) info.getStatus());
            this.saveCCUPlatform(user);
        }
    }

    public void doLogin(ISession iSession, ISFSObject iSFSObject) throws Exception {
    }

    public void saveCCUPlatform(User user) {
        HazelcastInstance instance = HazelcastClientFactory.getInstance();
        IMap userExtraModel = instance.getMap("cache_user_extra_info");
        if (userExtraModel.containsKey((Object) user.getName())) {
            UserExtraInfoModel model = (UserExtraInfoModel) userExtraModel.get((Object) user.getName());
            if (model != null && model.getPlatfrom() != null) {
                user.setProperty((Object) "pf", (Object) model.getPlatfrom());
                Platform platform = Platform.find((String) model.getPlatfrom());
                switch (platform) {
                    case WEB: {
                        ++ccuWeb;
                        break;
                    }
                    case ANDROID: {
                        ++ccuAD;
                        break;
                    }
                    case IOS: {
                        ++ccuIOS;
                        break;
                    }
                    case WINPHONE: {
                        ++ccuWP;
                        break;
                    }
                    case FACEBOOK_APP: {
                        ++ccuFB;
                        break;
                    }
                    case DESKTOP: {
                        ++ccuDT;
                    }
                }
            }
        } else {
            Debug.trace((Object) ("Cannot find user's extra info " + user.getName()));
        }
    }

    private void gameLoop() { // todo :game loop luôn chạy thằng này
        ++this.countReloadConfig;
        if (this.countReloadConfig == 10) {
//            Debug.trace((Object) "reload config");
            ConfigGame.reload(); // todo luôn reload config khi 10
            PartnerConfig.ReadConfig();
            this.countReloadConfig = 0;
        }
        ++this.countLogCCU;
        if (this.countLogCCU == ConfigGame.getIntValue("update_log_ccu")) {
            int ccu = ExtensionUtility.globalUserManager.getUserCount();//  .getUserCountByName(); // lấy số lượng người chơi online
            long ccuGiam = this.lastCCU - ccu;
            if (ccuGiam >= (long) ConfigGame.getIntValue("min_so_ccu_giam", 50)) {
                GameUtils.sendAlertAndCall("CCU giam " + ccuGiam + " trong " + ConfigGame.getIntValue("update_log_ccu") + " (s), time= " + DateTimeUtils.getCurrentTime());
            }
            this.lastCCU = ccu;
            this.countLogCCU = 0;
            int ccuOT = ccu - (ccuWeb + ccuAD + ccuIOS + ccuWP + ccuFB + ccuDT);
            this.serverInfoSrv.logCCU(ccu, ccuWeb, ccuAD, ccuIOS, ccuWP, ccuFB, ccuDT, ccuOT); // todo : log ccu vào rabitmq
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

