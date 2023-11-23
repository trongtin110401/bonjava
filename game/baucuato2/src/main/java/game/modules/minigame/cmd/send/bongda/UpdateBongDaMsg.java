package game.modules.minigame.cmd.send.bongda;

import game.BaseMsgEx;

import java.nio.ByteBuffer;

public class UpdateBongDaMsg extends BaseMsgEx {
    public String data;
    public UpdateBongDaMsg() {
        super(3003);
    }
    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        this.putStr(bf, this.data);
        return this.packBuffer(bf);
    }
}
