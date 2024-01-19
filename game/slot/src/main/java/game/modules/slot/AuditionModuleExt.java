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
        commandCollection.TOTAL_FREE_SPIN_MESSAGE = SlotCMD.RANGE_ROVER_TOTAL_FREE_SPIN;
        commandCollection.BIG_WIN_MESSAGE = SlotCMD.BIG_WIN_RANGE_ROVER;
        commandCollection.FREE_DAILY_MESSAGE = SlotCMD.RANGE_ROVER_FREE_DAILY;
        commandCollection.RESULT_MESSAGE = SlotCMD.RANGE_ROVER_INFO;
        commandCollection.UPDATE_POT_MESSAGE = SlotCMD.UPDATE_POT_RANGE_ROVER;
        commandCollection.FORCE_AUTO_PLAY_MESSAGE = SlotCMD.FORCE_STOP_PLAY_RANGE_ROVER;
        commandCollection.INFO_MESSAGE = SlotCMD.RANGE_ROVER_INFO
        commandCollection.MINIMIZE_RESULT_MESSAGE = SlotCMD.RANGE_ROVER_RESULT_MINIMIZE;
        return commandCollection;
    }

    @Override
    protected SlotLogListener initLogListener() {
        return new SlotLogListener() {
            final SlotMachineService slotMachineService = new SlotMachineServiceImpl();
            @Override
            public void log(long referenceId, String username, long betValue, String linesBetting, String linesWin, String prizesOnLine, short result, long totalPrizes, String time) throws IOException, TimeoutException, InterruptedException {
                slotMachineService.logAudition(referenceId, username, betValue, linesBetting, linesWin, prizesOnLine, result, totalPrizes, time);
            }
        };
    }

    @Override
    public void handleClientRequest(User user, DataCmd dataCmd) {
        Debug.trace("audition handleClientRequest " + dataCmd.getId());
        switch (dataCmd.getId()) {
            case SlotCMD.SUBSCRIBE_RANGE_ROVER: {
                subScribe(user, dataCmd);
                break;
            }
            case SlotCMD.UNSUBSCRIBE_RANGE_ROVER: {
                unSubScribe(user, dataCmd);
                break;
            }
            case SlotCMD.CHANGE_ROOM_RANGE_ROVER: {
                changeRoom(user, dataCmd);
                break;
            }
            case SlotCMD.AUTO_PLAY_RANGE_ROVER: {
                autoPlay(user, dataCmd);
                break;
            }
            case SlotCMD.PLAY_RANGE_ROVER: {
                play(user, dataCmd);
                break;
            }
            case SlotCMD.RANGE_ROVER_MINIMIZE: {
                minimize(user, dataCmd);
            }
        }
    }
}
