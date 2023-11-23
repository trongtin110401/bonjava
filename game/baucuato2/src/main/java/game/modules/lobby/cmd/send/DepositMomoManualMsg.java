package game.modules.lobby.cmd.send;

import game.BaseMsgEx;

import static game.modules.minigame.cmd.MiniGameCMD.CMD_DEPOSIT_MOMO_MANUAL;

public class DepositMomoManualMsg extends BaseMsgEx {
    public DepositMomoManualMsg() {
        super(CMD_DEPOSIT_MOMO_MANUAL);
    }
}
