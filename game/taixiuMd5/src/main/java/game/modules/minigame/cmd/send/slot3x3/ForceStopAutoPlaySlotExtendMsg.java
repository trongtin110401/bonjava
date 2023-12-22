package game.modules.minigame.cmd.send.slot3x3;

import bitzero.server.extensions.data.BaseMsg;
import game.modules.minigame.cmd.MiniGameCMD;


public class ForceStopAutoPlaySlotExtendMsg
        extends BaseMsg {
    public ForceStopAutoPlaySlotExtendMsg() {
        super(MiniGameCMD.CMD_SLOT_EXTEND_FORCE_STOP);
    }
}

