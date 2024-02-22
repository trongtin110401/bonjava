package game.modules.slot;

import bitzero.server.entities.User;
import bitzero.server.extensions.data.DataCmd;
import bitzero.util.common.business.Debug;
import com.vinplay.dal.service.SlotMachineService;
import com.vinplay.dal.service.impl.SlotMachineServiceImpl;
import com.vinplay.vbee.common.enums.Games;
import game.modules.slot.cmd.Slot25CommandCollection;
import game.modules.slot.cmd.SlotCMD;
import game.modules.slot.listener.SlotLogListener;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class CowboyModule extends Slot25BasicModule {

    public CowboyModule() {
        super(Games.BENTLEY.getName());
    }

    @Override
    protected Slot25CommandCollection initMessageCommand() {
        Slot25CommandCollection commandCollection = new Slot25CommandCollection();
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
    protected SlotLogListener initLogListener() {
        return new SlotLogListener() {
            final SlotMachineService slotMachineService = new SlotMachineServiceImpl();

            @Override
            public void log(long referenceId, String username, long betValue, String linesBetting, String linesWin, String prizesOnLine, short result, long totalPrizes, String time, String matrix) throws IOException, TimeoutException, InterruptedException {
                slotMachineService.logBenley(referenceId, username, betValue, linesBetting, linesWin, prizesOnLine, result, totalPrizes, time, matrix);
            }
        };
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
