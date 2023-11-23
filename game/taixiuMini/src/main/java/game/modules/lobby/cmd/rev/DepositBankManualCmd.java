package game.modules.lobby.cmd.rev;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class DepositBankManualCmd extends BaseCmd {
    public String bankNumber;
    public long amount;
    public String sender;

    public DepositBankManualCmd(DataCmd data) {
        super(data);
        this.unpackData();
    }

    public void unpackData() {
        ByteBuffer bf = this.makeBuffer();
        this.bankNumber = this.readString(bf);
        this.amount = bf.getLong();
        this.sender = this.readString(bf);

    }
}
