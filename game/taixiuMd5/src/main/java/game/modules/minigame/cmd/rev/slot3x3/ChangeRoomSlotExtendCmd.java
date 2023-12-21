package game.modules.minigame.cmd.rev.slot3x3;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;


public class ChangeRoomSlotExtendCmd
        extends BaseCmd {
    public byte roomLeavedId;
    public byte roomJoinedId;

    public ChangeRoomSlotExtendCmd(DataCmd dataCmd) {
        super(dataCmd);
        unpackData();
    }

    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        this.roomLeavedId = readByte(bf);
        this.roomJoinedId = readByte(bf);
    }
}
