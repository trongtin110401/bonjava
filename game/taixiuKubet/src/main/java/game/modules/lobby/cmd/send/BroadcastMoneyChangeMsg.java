package game.modules.lobby.cmd.send;

import game.BaseMsgEx;

import java.nio.ByteBuffer;

public class BroadcastMoneyChangeMsg extends BaseMsgEx {


    public Long currentMoney;

    public BroadcastMoneyChangeMsg() {
        super(20333);
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        bf.putLong(currentMoney);
        return this.packBuffer(bf);
    }
}
