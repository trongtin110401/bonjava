package game.modules.lobby.cmd.send;

import game.BaseMsgEx;
import game.modules.minigame.cmd.MiniGameCMD;

import java.nio.ByteBuffer;

public class CashoutCardMsg extends BaseMsgEx {
    public long CurrentMoney;
    public String ListCard;
    public CashoutCardMsg() {
        super(MiniGameCMD.CMD_WITHDRAW_CARD_MANUAL);
    }

    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        bf.putLong(this.CurrentMoney);
        this.putStr(bf, this.ListCard);
        return this.packBuffer(bf);
    }

}
