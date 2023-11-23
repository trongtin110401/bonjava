package game.modules.minigame.cmd.rev.bongda;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class UnsubscribeBongDaCmd extends BaseCmd {


    public UnsubscribeBongDaCmd(DataCmd dataCmd) {
        super(dataCmd);
        this.unpackData();
    }

    public void unpackData() {
        ByteBuffer bf = this.makeBuffer();

    }
}
