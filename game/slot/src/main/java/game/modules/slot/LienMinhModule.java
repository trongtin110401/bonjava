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

public class LienMinhModule extends Slot20Module {

    public LienMinhModule() {
        super(Games.LIEN_MINH.getName());
    }

    @Override
    protected Slot20CommandCollection initMessageCommand() {
        Slot20CommandCollection commandCollection = new Slot20CommandCollection();
        commandCollection.TOTAL_FREE_SPIN_MESSAGE = SlotCMD.LIEN_MINH_TOTAL_FREE_SPIN;
        commandCollection.BIG_WIN_MESSAGE = SlotCMD.BIG_WIN_LIEN_MINH;
        commandCollection.FREE_DAILY_MESSAGE = SlotCMD.LIEN_MINH_FREE_DAILY;
        commandCollection.RESULT_MESSAGE = SlotCMD.RESULT_LIEN_MINH;
        commandCollection.UPDATE_POT_MESSAGE = SlotCMD.UPDATE_POT_LIEN_MINH;
        commandCollection.FORCE_AUTO_PLAY_MESSAGE = SlotCMD.FORCE_STOP_PLAY_LIEN_MINH;
        commandCollection.INFO_MESSAGE = SlotCMD.LIEN_MINH_INFO;
        commandCollection.MINIMIZE_RESULT_MESSAGE = SlotCMD.LIEN_MINH_RESULT_MINIMIZE;
        return commandCollection;
    }

    @Override
    protected SlotLogListener initLogListener() {
        return new SlotLogListener() {
            final SlotMachineService slotMachineService = new SlotMachineServiceImpl();

            @Override
            public void log(long referenceId, String username, long betValue, String linesBetting, String linesWin, String prizesOnLine, short result, long totalPrizes, String time, String matrix) throws IOException, TimeoutException, InterruptedException {
                slotMachineService.logLienMinh(referenceId, username, betValue, linesBetting, linesWin, prizesOnLine, result, totalPrizes, time, matrix);
            }
        };
    }

    @Override
    public void handleClientRequest(User user, DataCmd dataCmd) {
        switch (dataCmd.getId()) {
            case SlotCMD.SUBSCRIBE_LIEN_MINH: {
                this.subScribe(user, dataCmd);
                break;
            }
            case SlotCMD.UNSUBSCRIBE_LIEN_MINH: {
                this.unSubScribe(user, dataCmd);
                break;
            }
            case SlotCMD.CHANGE_ROOM_LIEN_MINH: {
                this.changeRoom(user, dataCmd);
                break;
            }
            case SlotCMD.AUTO_PLAY_LIEN_MINH: {
                this.autoPlay(user, dataCmd);
                break;
            }
            case SlotCMD.PLAY_LIEN_MINH: {
                this.play(user, dataCmd);
                break;
            }
            case SlotCMD.LIEN_MINH_MINIMIZE: {
                this.minimize(user, dataCmd);
            }
        }
    }
}
