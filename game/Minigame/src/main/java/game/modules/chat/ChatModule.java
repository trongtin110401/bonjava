/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  bitzero.server.core.BZEventParam
 *  bitzero.server.core.BZEventType
 *  bitzero.server.core.IBZEvent
 *  bitzero.server.core.IBZEventListener
 *  bitzero.server.core.IBZEventParam
 *  bitzero.server.core.IBZEventType
 *  bitzero.server.entities.User
 *  bitzero.server.exceptions.BZException
 *  bitzero.server.extensions.BZExtension
 *  bitzero.server.extensions.BaseClientRequestHandler
 *  bitzero.server.extensions.data.BaseMsg
 *  bitzero.server.extensions.data.DataCmd
 *  com.vinplay.dal.service.ChatLobbyService
 *  com.vinplay.dal.service.impl.ChatLobbyServiceImpl
 *  com.vinplay.usercore.service.UserService
 *  com.vinplay.usercore.service.impl.UserServiceImpl
 *  com.vinplay.vbee.common.utils.DateTimeUtils
 *  org.apache.log4j.Logger
 *  org.json.simple.JSONArray
 *  org.json.simple.JSONObject
 */
package game.modules.chat;

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
import bitzero.util.common.business.Debug;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.ChatLobbyService;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.dal.service.impl.ChatLobbyServiceImpl;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.config.VBeePath;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.response.minigame.TaiXiuChatMsg;
import com.vinplay.vbee.common.utils.DateTimeUtils;
import game.modules.chat.cmd.rev.ChatCmd;
import game.modules.chat.cmd.send.ChatInfoMsg;
import game.modules.chat.cmd.send.ChatMsg;
import game.modules.chat.entities.ChatEntry;
import game.utils.ConfigGame;
import game.utils.ServerUtil;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ChatModule
        extends BaseClientRequestHandler {
    private static final int MAX_LOG_CHAT = 10;
    private ChatLobbyService chatService = new ChatLobbyServiceImpl();
    private List<ChatEntry> entries = new ArrayList<ChatEntry>();
    private Set<User> users = new HashSet<User>();
    private UserService userService = new UserServiceImpl();
    private List<String> listChat = new ArrayList<String>();
    private Logger logger = Logger.getLogger((String) "BlockChat");
    public static List<TaiXiuChatMsg> lstTaiXiuAdminMsg = new ArrayList<>();
    private CacheService cacheService = new CacheServiceImpl();
    private final Runnable botChatTask = new ChatModule.ScheduleBotChatTask();
    private List<String> listChatUsers = new ArrayList<String>();
    private List<String> listChatBot = new ArrayList<String>();
    private final Runnable adminChatRunnable = new AdminChat();

    public void init() {
        super.init();
        this.loadData();
        this.loadChatUsers();
        this.loadChatData();

        this.getParentExtension().addEventListener(BZEventType.USER_DISCONNECT, this);
        BitZeroServer.getInstance().getTaskScheduler().schedule(this.botChatTask, 10, TimeUnit.SECONDS);
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(adminChatRunnable, 2000, 500, TimeUnit.MILLISECONDS);

    }

    public void loadData() {
        try {
            String entry;
            BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream("config/list_chat.txt"), "UTF8"));
            while ((entry = br2.readLine()) != null) {
                this.listChat.add(entry);
            }
            br2.close();
        } catch (FileNotFoundException entry) {
        } catch (IOException entry) {
            // empty catch block
        }
    }

    public void loadChatData() {
        try {
            String entry;
            BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream(VBeePath.basePath.concat("config/list_chat.txt")), "UTF8"));
            while ((entry = br2.readLine()) != null) {
                this.listChatBot.add(entry);
            }
            br2.close();
            Debug.trace("BOT CHAT :" + listChatBot.size());
        } catch (IOException entry) {
            //sendLogToTele(entry.getMessage());
            entry.printStackTrace();
        }
    }

    public void loadChatUsers() {  // load chat user
        try {
            String entry;
            BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream(VBeePath.basePath.concat("config/bots.txt")), "UTF8")); // đọc từ file bots.txt
            while ((entry = br2.readLine()) != null) {
                this.listChatUsers.add(entry);
            }
            br2.close();
            Debug.trace("BOT CHAT USERS :" + listChatUsers.size());
        } catch (FileNotFoundException entry) {
        } catch (IOException entry) {
        }
    }

    public void handleServerEvent(IBZEvent ibzevent) throws BZException {
        if (ibzevent.getType() == BZEventType.USER_DISCONNECT) {
            User user = (User) ibzevent.getParameter((IBZEventParam) BZEventParam.USER);
            this.userDis(user);
        }
    }

    private void userDis(User user) {
        this.unsubscribe(user);
    }

    public void handleClientRequest(User user, DataCmd dataCmd) {
        switch (dataCmd.getId()) {
            case 18001: {
                this.subscribe(user);
                break;
            }
            case 18002: {
                this.unsubscribe(user);
                break;
            }
            case 18000: {
                this.chat(user, dataCmd);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void subscribe(User user) {
        Set<User> set;
        Set<User> set2 = set = this.users;
        synchronized (set2) {
            this.users.add(user);
        }
        this.sendChatLobbyInfo(user);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void unsubscribe(User user) {
        Set<User> set;
        Set<User> set2 = set = this.users;
        synchronized (set2) {
            this.users.remove((Object) user);
        }
    }

    private final class ScheduleBotChatTask
            extends Thread {
        private ScheduleBotChatTask() {
        }

        @Override
        public void run() {
            try {
                Debug.trace("Schedule bot chat running ...");
                ChatModule.this.scheduleBotChat();
                Debug.trace("Schedule bot chat finished ...");
            } catch (Exception ex) {
                // sendLogToTele(ex.getMessage());
                Debug.trace(ex.getMessage());
            }
        }
    }

    private void scheduleBotChat() { // todo:fake chat user
        try {
            Random rand = new Random();
            while (true) if (listChatUsers.size() > 0) {
                int sleep = rand.nextInt(2);
                Thread.sleep(7000 + sleep * 1000);
                String user = listChatUsers.get(rand.nextInt(listChatUsers.size()));

                String randMessage = listChatBot.get(rand.nextInt(listChatBot.size()));

                this.chat(user, randMessage);
            } else {
                int sleep = rand.nextInt(5000);
                Thread.sleep(sleep * 1000);
            }
        } catch (Exception e) {
            System.out.println("exception scheduleBotChat " + e);
            Debug.trace(e.getMessage());
        }
    }

    private void chat(User user, DataCmd dataCmd) {
        int daiLy = this.getStatusDaiLy(user);
        ChatCmd cmd = new ChatCmd(dataCmd);
        String username = user.getName();
        ChatMsg msg = new ChatMsg();

        if (this.containBadword(username, cmd.message)) {
            msg.Error = 5;
            this.send(msg, user);
        } else if ((cmd.message.length() >= 40)) {
            msg.Error = 6;
            this.send(msg, user);
        } else if (this.allowUserChat(user.getName(), daiLy)) {
            try {
                HazelcastInstance client = HazelcastClientFactory.getInstance();
                IMap<String, UserModel> userMap = client.getMap("users");
                if (userMap.containsKey(username)) {
                    UserModel model = userMap.get(username);
                    if (model.getVinTotal() < 1000) {
                        msg.Error = 7;
                        this.send(msg, user);
                        return;
                    }
                }

                cmd.message = cmd.message.replaceAll("[\\.\\|,-]", "");
                cmd.message = cmd.message.replaceAll("\\d{9,11}", "xxx");
                cmd.message = cmd.message.replaceAll("(\\d{2,} )+", "xxx");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (daiLy == 100) {
                username = "Admin";
            }
            TaiXiuChatMsg obj = new TaiXiuChatMsg(username, cmd.message);
            lstTaiXiuAdminMsg.add(obj);
            this.chat(username, cmd.message);
            this.chatService.banChatUser(username, 5000);
            //cacheService.setObject("lstTaiXiuAdminMsg",lstTaiXiuAdminMsg);
        } else {
            msg.Error = 2;
            this.send(msg, user);
        }

    }

    class AdminChat implements Runnable {
        public AdminChat() {
        }

        @Override
        public void run() {
            ChatModule.this.scheduleGetChatAdmin();
        }
    }


    private void scheduleGetChatAdmin() {
        try {
            TaiXiuChatMsg obj = (TaiXiuChatMsg) cacheService.getObject("admin_msg");
            //kiểm tra trạng thái chưa gửi và tên không null thì được phép gửi tới client
            if (!obj.getNickname().isEmpty() && !Objects.equals(obj.getStatus(), 1)) {
                ChatMsg msg = new ChatMsg();
                msg.nickname = obj.getNickname();
                msg.mesasge = obj.getMesasge();
                ChatModule.lstTaiXiuAdminMsg.add(obj);
                this.chat(obj.getNickname(), obj.getMesasge());
                obj.setStatus(1);
                cacheService.setObject("admin_msg", obj);
            }
            Thread.sleep(500);
        } catch (Exception e) {
            //   sendLogToTele(e.getMessage());
            Debug.trace(e.getMessage());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void chat(String username, String content) {
        Set<User> set;
        ChatEntry newEntry = new ChatEntry(username, content);
        ChatMsg msg = new ChatMsg();

        String displayName = username;

        msg.nickname = displayName;
        msg.mesasge = content;
        Set<User> set2 = set = this.users;
        synchronized (set2) {
            for (User u : this.users) {
                if (u == null) continue;
                ServerUtil.sendMsgToUser((BaseMsg) msg, u);
            }
        }
        newEntry = new ChatEntry(displayName, content);
        this.logChat(newEntry);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void logChat(ChatEntry newEntry) {
        List<ChatEntry> list;
        List<ChatEntry> list2 = list = this.entries;
        synchronized (list2) {
            this.entries.add(newEntry);
            while (this.entries.size() > 10) {
                this.entries.remove(0);
            }
        }
    }

    private boolean containBadword(String username, String content) {
        if (ConfigGame.checkBadword(content)) {
            this.logger.debug((Object) (DateTimeUtils.getCurrentTime() + "\t" + username + " \t " + content));
            this.chatService.banChatUser(username, (long) (ConfigGame.TIME_BLOCK_CHAT * 60 * 1000));
            return true;
        }
        return false;
    }

    private void sendChatLobbyInfo(User user) {
        JSONArray arr = new JSONArray();
        for (ChatEntry entry : this.entries) {
            arr.add((Object) entry.toJson());
        }
        String str = arr.toString();
        ChatInfoMsg chatInfoMsg = new ChatInfoMsg();
        chatInfoMsg.msg = str;
        chatInfoMsg.minVipPointRequire = (byte) ConfigGame.getIntValue("chat_min_vp_require", 20);
        chatInfoMsg.timeUnBan = this.chatService.getBanTime(user.getName());
        chatInfoMsg.userType = (byte) this.getStatusDaiLy(user);
        this.send((BaseMsg) chatInfoMsg, user);
    }

    private boolean allowUserChat(String username, int daiLy) {
        long timeUnBan;
        if (daiLy == 100) {
            return true;
        }
        boolean allow = true;
        int minVPRequire = ConfigGame.getIntValue("chat_min_vp_require", 20000);
        long vipPointSave = this.userService.getMoneyUser(username).getVin();
        if (vipPointSave < minVPRequire) {
            allow = false;
        }
        if (0 < daiLy && daiLy < 100) {
            allow = false;
        }
        if (this.chatService.getBanTime(username) != 0L) {
            allow = false;
        }
        return allow;
    }

    private int getStatusDaiLy(User user) {
        String status = (String) user.getProperty((Object) "dai_ly");
        int daiLy = 0;
        if (status != null && !status.isEmpty()) {
            daiLy = Integer.parseInt(status);
        }
        return daiLy;
    }
}

