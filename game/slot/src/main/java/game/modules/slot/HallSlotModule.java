
package game.modules.slot;

import bitzero.server.BitZeroServer;
import bitzero.server.entities.User;
import bitzero.server.extensions.BaseClientRequestHandler;
import bitzero.server.extensions.data.BaseMsg;
import bitzero.server.extensions.data.DataCmd;
import bitzero.util.common.business.Debug;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.vbee.common.enums.Games;
import game.modules.slot.cmd.SlotCMD;
import game.modules.slot.cmd.send.hall.ListAutoPlayInfoMsg;
import game.modules.slot.cmd.send.hall.UpdateJackpotsMsg;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import game.modules.slot.room.SlotRoom;
import org.json.simple.JSONObject;

public class HallSlotModule extends BaseClientRequestHandler {

    private final Set<User> usersSub = new HashSet<>();

    public HallSlotModule() {
        Runnable updateJackpotsTask = new UpdateJackpotsTask();
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(updateJackpotsTask, 10, 4, TimeUnit.SECONDS);
    }

    public void handleClientRequest(User user, DataCmd dataCmd) {
        switch (dataCmd.getId()) {
            case SlotCMD.SUBSCRIBE_HALL: {
                this.subScribe(user);
                break;
            }
            case SlotCMD.UNSUBSCRIBE_HALL: {
                this.unSubscribe(user);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void subScribe(User user) {

        synchronized (this.usersSub) {
            this.usersSub.add(user);
        }

        UpdateJackpotsMsg msg = new UpdateJackpotsMsg();
        msg.json = this.buildJsonJackpots();
        this.send(msg, user);
        ListAutoPlayInfoMsg listAutoMsg = new ListAutoPlayInfoMsg();
        this.send(listAutoMsg, user);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected synchronized void unSubscribe(User user) {
        this.usersSub.remove(user);
    }

    private String buildJsonJackpots() {

        JSONObject json = new JSONObject();
        JSONObject jsonLienMinh = this.buildGameSlotInfo(Games.LIEN_MINH.getName());
        json.put(Games.LIEN_MINH.getName(), jsonLienMinh);

        JSONObject jsonCowboy = this.buildGameSlotInfo(Games.COWBOY.getName());
        json.put(Games.COWBOY.getName(), jsonCowboy);

        JSONObject jsonFast = this.buildGameSlotInfo(Games.FAST_AND_FURIOUS.getName());
        json.put(Games.FAST_AND_FURIOUS.getName(), jsonFast);

        JSONObject jsonLadyNight = this.buildGameSlotInfo(Games.LADY_NIGHT.getName());
        json.put(Games.LADY_NIGHT.getName(), jsonLadyNight);

        JSONObject jsonSexyDance = this.buildGameSlotInfo(Games.SEXY_DANCE.getName());
        json.put(Games.SEXY_DANCE.getName(), jsonSexyDance);

        JSONObject jsonBLC = this.buildGameSlotInfo(Games.BONG_LAI_CAC.getName());
        json.put(Games.BONG_LAI_CAC.getName(), jsonBLC);

        JSONObject jsonLasVegas = this.buildGameSlotInfo(Games.LAS_VEGAS.getName());
        json.put(Games.LAS_VEGAS.getName(), jsonLasVegas);

        JSONObject jsonHalloween = this.buildGameSlotInfo(Games.HALLOWEEN.getName());
        json.put(Games.HALLOWEEN.getName(), jsonHalloween);

        JSONObject jsonBigCityBoy = this.buildGameSlotInfo(Games.BIG_CITY_BOY.getName());
        json.put(Games.BIG_CITY_BOY.getName(), jsonBigCityBoy);

        return json.toJSONString();
    }

    private JSONObject buildGameSlotInfo(String gameName) {
        JSONObject jsonGame = new JSONObject();
        try {
            JSONObject room100 = this.buildRoomSlotInfo(gameName, 100);
            jsonGame.put("100", room100);
            JSONObject room101 = this.buildRoomSlotInfo(gameName, 1000);
            jsonGame.put("1000", room101);
            JSONObject room102 = this.buildRoomSlotInfo(gameName, 10000);
            jsonGame.put("10000", room102);
        } catch (Exception ignored) {
        }
        return jsonGame;
    }

    private JSONObject buildRoomSlotInfo(String gameName, int room) {
        CacheServiceImpl cacheService = new CacheServiceImpl();
        JSONObject jsonValue = new JSONObject();
        try {
            int pot = cacheService.getValueInt(SlotRoom.CACHE_JACK_POT_VALUE_SLOT + "_" + room + "_" + gameName);
            jsonValue.put("p", pot);
            int x2 = cacheService.getValueInt(SlotRoom.CACHE_JACK_POT_VALUE_SLOT + "_" + room + "_" + gameName + "_x2");
            jsonValue.put("x2", x2);
        } catch (Exception ignored) {
        }
        return jsonValue;
    }

    static void access$2(HallSlotModule hallSlotModule, BaseMsg baseMsg, User user) {
        hallSlotModule.send(baseMsg, user);
    }

    private class UpdateJackpotsTask implements Runnable {
        private UpdateJackpotsTask() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            try {
                String result = HallSlotModule.this.buildJsonJackpots();
                UpdateJackpotsMsg msg = new UpdateJackpotsMsg();
                msg.json = result;
                synchronized (HallSlotModule.this.usersSub) {
                    for (User user : HallSlotModule.this.usersSub) {
                        if (user == null) continue;
                        HallSlotModule.access$2(HallSlotModule.this, msg, user);
                    }
                }
            } catch (Exception e) {
                Debug.trace("Update slot exception: " + e.getMessage());
            }
        }
    }

}

