package game.modules.lobby.cmd.send;

import game.BaseMsgEx;

import java.nio.ByteBuffer;

import static game.modules.minigame.cmd.MiniGameCMD.CMD_DEPOSIT_MOMO_MANUAL;

public class DepositMomoManualMsg extends BaseMsgEx {
    public int code;
    public String name;
    public String receiverPhone;
    public String comment;
    public Long currentMoney;
    public String transId;

    public DepositMomoManualMsg() {
        super(CMD_DEPOSIT_MOMO_MANUAL);
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        bf.putInt(code);
        this.putStr(bf, name);
        this.putStr(bf, receiverPhone);
        this.putStr(bf, comment);
        bf.putLong(currentMoney);
        this.putStr(bf,transId);
        return this.packBuffer(bf);
    }
}
