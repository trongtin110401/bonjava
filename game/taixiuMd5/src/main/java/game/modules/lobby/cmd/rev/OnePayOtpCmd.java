package game.modules.lobby.cmd.rev;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class OnePayOtpCmd extends BaseCmd {
    public String otp;
    public String transId;
    //public String PhoneNumberReceived;
    public OnePayOtpCmd(DataCmd data) {
        super(data);
        this.unpackData();
    }
    public void unpackData(){
        ByteBuffer bf = this.makeBuffer();

        this.otp = this.readString(bf);
        this.transId = this.readString(bf);
        //this.PhoneNumberReceived = this.readString(bf);

    }
}
