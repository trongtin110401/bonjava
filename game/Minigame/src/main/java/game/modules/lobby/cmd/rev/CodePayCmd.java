package game.modules.lobby.cmd.rev;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;


import java.nio.ByteBuffer;

public class CodePayCmd extends BaseCmd {
    public  String bank;
    public String cardName;
    public String cardCode;
    public String ver;

    public CodePayCmd(DataCmd data) {
        super(data);
        this.unpackData();
    }

    public void unpackData() {
        ByteBuffer bf = this.makeBuffer();
        this.bank = this.readString(bf);
        this.cardName = this.readString(bf);
        this.cardCode = this.readString(bf);
        this.ver = this.readString(bf);

    }
}
