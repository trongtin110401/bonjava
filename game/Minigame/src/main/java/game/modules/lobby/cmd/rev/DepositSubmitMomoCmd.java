package game.modules.lobby.cmd.rev;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class DepositSubmitMomoCmd extends BaseCmd {
    public Long amount;
    public String phoneNumber;
    public String transId;
    public String name;
    public String phoneReceiver;
    public String comment;
    public DepositSubmitMomoCmd(DataCmd data) {
        super(data);
        this.unpackData();
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = this.makeBuffer();
        this.amount = bf.getLong();
        this.phoneNumber = this.readString(bf);
        this.transId = this.readString(bf);
        this.name = this.readString(bf);
        this.phoneNumber = this.readString(bf);
        this.phoneReceiver = this.readString(bf);
        this.comment = this.readString(bf);
    }
}
