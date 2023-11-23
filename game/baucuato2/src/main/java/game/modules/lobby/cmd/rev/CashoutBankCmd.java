package game.modules.lobby.cmd.rev;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class CashoutBankCmd extends BaseCmd {
    public String BankName;
    public String BankNumber;
    public String BankAccountName;
    public int Amount;
    public CashoutBankCmd(DataCmd data) {
        super(data);
        this.unpackData();
    }
    public void unpackData() {
        ByteBuffer bf = this.makeBuffer();
        this.BankName = this.readString(bf);
        this.BankNumber = this.readString(bf);
        this.BankAccountName = this.readString(bf);
        this.Amount = this.readInt(bf);

    }
}
