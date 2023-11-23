package game.modules.lobby.cmd.send;

import game.BaseMsgEx;

import java.nio.ByteBuffer;

public class BroadcastTimeChangeMsg extends BaseMsgEx {
    public int time;
    public BroadcastTimeChangeMsg() {
        super(20444);
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        bf.putInt(time);
        return this.packBuffer(bf);
    }
}
