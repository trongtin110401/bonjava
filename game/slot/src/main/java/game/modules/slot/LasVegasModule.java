package game.modules.slot;

import bitzero.server.entities.User;
import bitzero.server.extensions.data.DataCmd;
import com.vinplay.dal.service.SlotMachineService;
import com.vinplay.dal.service.impl.SlotMachineServiceImpl;
import com.vinplay.vbee.common.enums.Games;
import game.modules.slot.cmd.Slot25CommandCollection;
import game.modules.slot.cmd.SlotCMD;
import game.modules.slot.listener.SlotLogListener;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class LasVegasModule extends Slot20ExtendModule {

    public LasVegasModule() {
        super(Games.LAS_VEGAS.getName());
    }

    @Override
    protected Slot25CommandCollection initMessageCommand() {
        Slot25CommandCollection commandCollection = new Slot25CommandCollection();
        commandCollection.TOTAL_FREE_SPIN_MESSAGE = SlotCMD.LASVEGAS_TOTAL_FREE_SPIN;
        commandCollection.BIG_WIN_MESSAGE = SlotCMD.BIG_WIN_LASVEGAS;
        commandCollection.FREE_DAILY_MESSAGE = SlotCMD.LASVEGAS_FREE_DAILY;
        commandCollection.RESULT_MESSAGE = SlotCMD.PLAY_LASVEGAS;
        commandCollection.UPDATE_POT_MESSAGE = SlotCMD.UPDATE_POT_LASVEGAS;
        commandCollection.FORCE_AUTO_PLAY_MESSAGE = SlotCMD.FORCE_STOP_PLAY_LASVEGAS;
        commandCollection.INFO_MESSAGE = SlotCMD.LASVEGAS_INFO;
        commandCollection.MINIMIZE_RESULT_MESSAGE = SlotCMD.LASVEGAS_RESULT_MINIMIZE;
        return commandCollection;
    }

    @Override
    protected SlotLogListener initLogListener() {
        return new SlotLogListener() {
            final SlotMachineService slotMachineService = new SlotMachineServiceImpl();
            @Override
            public void log(long referenceId, String username, long betValue, String linesBetting, String linesWin, String prizesOnLine, short result, long totalPrizes, String time, String matrix) throws IOException, TimeoutException, InterruptedException {
                slotMachineService.logLasVegas(referenceId, username, betValue, linesBetting, linesWin, prizesOnLine, result, totalPrizes, time, matrix);
            }
        };
    }

    @Override
    public void handleClientRequest(User user, DataCmd dataCmd) {
        switch (dataCmd.getId()) {
            case SlotCMD.SUBSCRIBE_LASVEGAS: {
                this.subScribe(user, dataCmd);
                break;
            }
            case SlotCMD.UNSUBSCRIBE_LASVEGAS: {
                this.unSubScribe(user, dataCmd);
                break;
            }
            case SlotCMD.CHANGE_ROOM_LASVEGAS: {
                this.changeRoom(user, dataCmd);
                break;
            }
            case SlotCMD.AUTO_PLAY_LASVEGAS: {
                this.autoPlay(user, dataCmd);
                break;
            }
            case SlotCMD.PLAY_LASVEGAS: {
                this.play(user, dataCmd);
                break;
            }
            case SlotCMD.LASVEGAS_MINIMIZE: {
                this.minimize(user, dataCmd);
            }
        }
    }

}
