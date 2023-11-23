package game.modules.lobby.cmd.rev;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class DepositMomoManualCmd extends BaseCmd {
    public String SendFrom;
    public long Amount;
    //public String PhoneNumberReceived;
    public DepositMomoManualCmd(DataCmd data) {
        super(data);
        this.unpackData();
    }
    public void unpackData(){
        ByteBuffer bf = this.makeBuffer();
        this.Amount = bf.getLong();
        this.SendFrom = this.readString(bf);

        //this.PhoneNumberReceived = this.readString(bf);

    }
}
