package game.modules.slot.listener.impl;

import com.vinplay.dal.service.SlotMachineService;
import com.vinplay.dal.service.impl.SlotMachineServiceImpl;
import game.modules.slot.listener.SlotLogListener;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class BenleySlotLogListener implements SlotLogListener {

    @Override
    public void log(long referenceId, String username, long betValue, String linesBetting, String linesWin, String prizesOnLine, short result, long totalPrizes, String time) throws IOException, TimeoutException, InterruptedException {
        SlotMachineService slotService = new SlotMachineServiceImpl();
        slotService.logBenley(referenceId, username, betValue, linesBetting, linesWin, prizesOnLine, result, totalPrizes, time);
    }

}
