package game.modules.lobby.cmd.rev;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class CashoutMomoCmd extends BaseCmd {
    public String PhoneNumber;

    public int Amount;
    public CashoutMomoCmd(DataCmd data) {
        super(data);
        this.unpackData();
    }
    public void unpackData() {
        ByteBuffer bf = this.makeBuffer();
        this.PhoneNumber = this.readString(bf);

        this.Amount = this.readInt(bf);

    }
}
