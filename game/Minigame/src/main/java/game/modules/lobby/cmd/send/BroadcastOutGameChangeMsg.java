package game.modules.lobby.cmd.send;

import game.BaseMsgEx;

import java.nio.ByteBuffer;

public class BroadcastOutGameChangeMsg extends BaseMsgEx {
    public String idGame;

    public BroadcastOutGameChangeMsg() {
        super(21444);
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        this.putStr(bf, this.idGame);
        return this.packBuffer(bf);
    }
}
