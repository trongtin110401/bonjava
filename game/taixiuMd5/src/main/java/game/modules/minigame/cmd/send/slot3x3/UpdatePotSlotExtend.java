package game.modules.minigame.cmd.send.slot3x3;

import bitzero.server.extensions.data.BaseMsg;
import game.modules.minigame.cmd.MiniGameCMD;

import java.nio.ByteBuffer;


public class UpdatePotSlotExtend
        extends BaseMsg {
    public long value1;
    public long value2;
    public long value3;

    public UpdatePotSlotExtend() {
        super(MiniGameCMD.CMD_SLOT_EXTEND_UPDATE_POT);
    }

    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        bf.putLong(this.value1);
        bf.putLong(this.value2);
        bf.putLong(this.value3);
        return packBuffer(bf);
    }
}
