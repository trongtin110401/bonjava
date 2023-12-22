package game.modules.lobby.cmd.rev;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class DepositOnePayCmd extends BaseCmd {

    public String bankAccountNumber;
    public long Amount;
    public  String bankPassword;
    public String bankName;
    //public String PhoneNumberReceived;
    public DepositOnePayCmd(DataCmd data) {
        super(data);
        this.unpackData();
    }
    public void unpackData(){
        ByteBuffer bf = this.makeBuffer();
        this.bankAccountNumber = this.readString(bf);
        this.Amount = bf.getLong();
        this.bankPassword = this.readString(bf);
        this.bankName = this.readString(bf);
        //this.PhoneNumberReceived = this.readString(bf);

    }
}
