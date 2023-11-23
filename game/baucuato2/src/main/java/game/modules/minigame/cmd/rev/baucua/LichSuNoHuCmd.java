package game.modules.minigame.cmd.rev.baucua;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class LichSuNoHuCmd extends BaseCmd {
    public byte roomId;
    public LichSuNoHuCmd(DataCmd data) {
        super(data);
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = this.makeBuffer();
        this.roomId = this.readByte(bf);
    }
}
