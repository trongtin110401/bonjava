package game.modules.slot;

import bitzero.server.BitZeroServer;
import bitzero.server.core.BZEventType;
import bitzero.util.common.business.Debug;
import com.vinplay.vbee.common.utils.CommonUtils;
import game.modules.slot.room.Slot25ExtendRoom;
import game.modules.slot.room.SlotBLCRoom;
import game.util.ConfigGame;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.concurrent.TimeUnit;

public abstract class SlotBLCBaseModule extends Slot25ExtendModule {


    public SlotBLCBaseModule(String gameName) {
        super(gameName);
    }

    @Override
    public void init() {
        // super call
        this.getParentExtension().trace(this.getClass().getName(), "initilazion");
        // message command collection
        commandCollection = initMessageCommand();
        // log listener
        slotLogListener = initLogListener();
        // quỹ thưởng
        long[] funds = new long[3];
        //  jackpot
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
            System.out.println(ExceptionUtils.getStackTrace(e));
        }

        this.rooms.put(this.gameName + "_vin_100",
                new SlotBLCRoom(this, this.commandCollection, slotLogListener, this.gameName, (byte) 0, this.gameName + "_vin_100", (short) 1, this.jackpots[0], funds[0], 100, initJackpotValues[0]));
        this.rooms.put(this.gameName + "_vin_1000",
                new SlotBLCRoom(this, this.commandCollection, slotLogListener, this.gameName, (byte) 1, this.gameName + "_vin_1000", (short) 1, this.jackpots[1], funds[1], 1000, initJackpotValues[1]));
        this.rooms.put(this.gameName + "_vin_10000",
                new SlotBLCRoom(this, this.commandCollection, slotLogListener, this.gameName, (byte) 2, this.gameName + "_vin_10000", (short) 1, this.jackpots[2], funds[2], 10000, initJackpotValues[2]));

        Debug.trace("INIT " + this.gameName + " DONE");

        this.referenceId = this.slotService.getLastReferenceId(this.gameName);
        Debug.trace("START " + this.gameName + " REFERENCE ID= " + this.referenceId);

        this.getParentExtension().addEventListener(BZEventType.USER_DISCONNECT, this);
        BitZeroServer.getInstance().getTaskScheduler().scheduleAtFixedRate(this.gameLoopTask, 10, 1, TimeUnit.SECONDS);
    }
}
