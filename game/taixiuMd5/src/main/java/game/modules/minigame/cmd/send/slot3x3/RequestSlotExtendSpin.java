package game.modules.minigame.cmd.send.slot3x3;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;


public class RequestSlotExtendSpin extends BaseCmd {
    public long gold;

    public RequestSlotExtendSpin(DataCmd data) {
        super(data);
        unpackData();
    }

    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        gold = readLong(bf);

    }
}

