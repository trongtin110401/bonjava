package game.modules.lobby.cmd.send;

import game.BaseMsgEx;

import static game.modules.minigame.cmd.MiniGameCMD.CMD_DEPOSIT_BANK_MANUAL;

public class DepositBankManualMsg extends BaseMsgEx {
    public DepositBankManualMsg() {
        super(CMD_DEPOSIT_BANK_MANUAL);
    }
}
