package game.modules.gameRoom.cmd.send;

import bitzero.server.extensions.data.BaseMsg;

import java.nio.ByteBuffer;

public class PongMessage extends BaseMsg {

    public PongMessage() {
        super((short) 3050);
    }

    public byte[] createData() {
        ByteBuffer buffer = this.makeBuffer();
        return this.packBuffer(buffer);
    }

}
