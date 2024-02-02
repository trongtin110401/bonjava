package game.modules.slot.listener;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface SlotLogListener {

    void log(long referenceId, String username, long betValue, String linesBetting, String linesWin, String prizesOnLine,
             short result, long totalPrizes, String time, String matrix) throws IOException, TimeoutException, InterruptedException;

}
