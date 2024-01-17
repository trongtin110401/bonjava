package game.modules.slot;

import bitzero.server.entities.User;
import bitzero.server.extensions.data.DataCmd;
import bitzero.util.common.business.Debug;
import com.vinplay.vbee.common.enums.Games;
import game.modules.slot.cmd.Slot25BasicCommandCollection;
import game.modules.slot.cmd.SlotCMD;

public class BentleyModuleExt extends Slot25BasicModule {

    public BentleyModuleExt() {
        super(Games.BENTLEY.getName());
    }

    @Override
    protected Slot25BasicCommandCollection initMessageCommand() {
        Slot25BasicCommandCollection commandCollection = new Slot25BasicCommandCollection();
        commandCollection.TOTAL_FREE_SPIN_MESSAGE = SlotCMD.AVENGERS_TOTAL_FREE_SPIN;
        commandCollection.BIG_WIN_MESSAGE = SlotCMD.BIG_WIN_AVENGER;
        commandCollection.FREE_DAILY_MESSAGE = SlotCMD.AVENGER_FREE_DAILY;
        commandCollection.RESULT_MESSAGE = SlotCMD.PLAY_AVENGER;
        commandCollection.UPDATE_POT_MESSAGE = SlotCMD.UPDATE_POT_AVENGER;
        commandCollection.FORCE_AUTO_PLAY_MESSAGE = SlotCMD.FORCE_STOP_PLAY_AVENGER;
        commandCollection.INFO_MESSAGE = SlotCMD.AVENGER_INFO;
        commandCollection.MINIMIZE_RESULT_MESSAGE = SlotCMD.AVENGER_RESULT_MINIMIZE;
        return commandCollection;
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
}
