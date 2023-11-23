package game.modules.lobby.cmd.send;

import bitzero.server.extensions.data.BaseMsg;

import java.nio.ByteBuffer;

public class SlotNoHuMsg extends BaseMsg {
    public String username;
    public byte type; // 1 là nổ hũ
    public long totalPrizes;
    public String gameName;


    public SlotNoHuMsg() {
        super((short) 14022);
    }

    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        this.putStr(bf, this.username);
        bf.put(this.type);

        bf.putLong(this.totalPrizes);
        this.putStr(bf, this.gameName);
        return this.packBuffer(bf);
    }
}
