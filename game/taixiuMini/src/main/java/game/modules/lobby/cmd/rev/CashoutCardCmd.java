package game.modules.lobby.cmd.rev;


import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class CashoutCardCmd extends BaseCmd {
    public String TelcoId;
    public int Amount;
    public int Quantity;
    public String secretCode;
    public CashoutCardCmd(DataCmd data) {
        super(data);
        this.unpackData();
    }
    public void unpackData() {
        ByteBuffer bf = this.makeBuffer();
        this.TelcoId = this.readString(bf);
        this.Amount = this.readInt(bf);
        this.Quantity = this.readInt(bf);
        this.secretCode = this.readString(bf);
    }
}
