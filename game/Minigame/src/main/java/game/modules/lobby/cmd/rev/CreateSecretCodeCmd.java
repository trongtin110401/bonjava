package game.modules.lobby.cmd.rev;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;
import game.BaseMsgEx;

import java.nio.ByteBuffer;

public class CreateSecretCodeCmd  extends BaseCmd {

    public String password;
    public String secretCode;
    public CreateSecretCodeCmd(DataCmd data) {
        super(data);
        this.unpackData();
    }
    public void unpackData() {
        ByteBuffer bf = this.makeBuffer();
        this.secretCode = this.readString(bf);
        this.password = this.readString(bf);
    }
}
