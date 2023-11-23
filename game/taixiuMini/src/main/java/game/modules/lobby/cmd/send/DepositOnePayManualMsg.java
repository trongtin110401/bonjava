package game.modules.lobby.cmd.send;

import game.BaseMsgEx;

import java.nio.ByteBuffer;

import static game.modules.minigame.cmd.MiniGameCMD.CMD_DEPOSIT_ONE_PAY_BANK_MANUAL;

public class DepositOnePayManualMsg extends BaseMsgEx {
    public String transId;
    public DepositOnePayManualMsg() {
        super(CMD_DEPOSIT_ONE_PAY_BANK_MANUAL);
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        this.putStr(bf,transId);
        return this.packBuffer(bf);
    }
}
