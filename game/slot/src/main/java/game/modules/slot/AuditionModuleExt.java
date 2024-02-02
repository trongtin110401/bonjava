package game.modules.slot;

import bitzero.server.entities.User;
import bitzero.server.extensions.data.DataCmd;
import bitzero.util.common.business.Debug;
import com.vinplay.dal.service.SlotMachineService;
import com.vinplay.dal.service.impl.SlotMachineServiceImpl;
import com.vinplay.vbee.common.enums.Games;
import game.modules.slot.cmd.Slot20CommandCollection;
import game.modules.slot.cmd.SlotCMD;
import game.modules.slot.listener.SlotLogListener;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class AuditionModuleExt extends Slot20Module {

    public AuditionModuleExt() {
        super(Games.AUDITION.getName());
    }

    @Override
    protected Slot20CommandCollection initMessageCommand() {
        Slot20CommandCollection commandCollection = new Slot20CommandCollection();
        commandCollection.TOTAL_FREE_SPIN_MESSAGE = SlotCMD.KHO_BAU_TOTAL_FREE_SPIN;
        commandCollection.BIG_WIN_MESSAGE = SlotCMD.BIG_WIN_KHO_BAU;
        commandCollection.FREE_DAILY_MESSAGE = SlotCMD.KHO_BAU_FREE_DAILY;
        commandCollection.RESULT_MESSAGE = SlotCMD.RESULT_KHO_BAU;
        commandCollection.UPDATE_POT_MESSAGE = SlotCMD.UPDATE_POT_KHO_BAU;
        commandCollection.FORCE_AUTO_PLAY_MESSAGE = SlotCMD.FORCE_STOP_PLAY_KHO_BAU;
        commandCollection.INFO_MESSAGE = SlotCMD.KHO_BAU_INFO;
        commandCollection.MINIMIZE_RESULT_MESSAGE = SlotCMD.KHO_BAU_RESULT_MINIMIZE;
        return commandCollection;
    }

    @Override
    protected SlotLogListener initLogListener() {
        return new SlotLogListener() {
            final SlotMachineService slotMachineService = new SlotMachineServiceImpl();
            @Override
            public void log(long referenceId, String username, long betValue, String linesBetting, String linesWin, String prizesOnLine, short result, long totalPrizes, String time, String matrix) throws IOException, TimeoutException, InterruptedException {
                slotMachineService.logAudition(referenceId, username, betValue, linesBetting, linesWin, prizesOnLine, result, totalPrizes, time, matrix);
            }
        };
    }

    @Override
    public void handleClientRequest(User user, DataCmd dataCmd) {
        Debug.trace("audition handleClientRequest " + dataCmd.getId());
        switch (dataCmd.getId()) {
            case 2003: {
                this.subScribe(user, dataCmd);
                break;
            }
            case 2004: {
                this.unSubScribe(user, dataCmd);
                break;
            }
            case 2005: {
                this.changeRoom(user, dataCmd);
                break;
            }
            case 2006: {
                this.autoPlay(user, dataCmd);
                break;
            }
            case 2001: {
                this.play(user, dataCmd);
                break;
            }
            case 2013: {
                this.minimize(user, dataCmd);
            }
        }
    }
}
