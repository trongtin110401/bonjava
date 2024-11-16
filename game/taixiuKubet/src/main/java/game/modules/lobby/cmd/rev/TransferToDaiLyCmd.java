package game.modules.lobby.cmd.rev;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class TransferToDaiLyCmd extends BaseCmd {
    public Long moneyExchange;
    public String userReceiver;
    public String description;
    public String secretCode;
    public TransferToDaiLyCmd(DataCmd data) {
        super(data);
        this.unpackData();
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = this.makeBuffer();
        this.moneyExchange = bf.getLong();
        this.userReceiver = this.readString(bf);
        this.description = this.readString(bf);
        this.secretCode = this.readString(bf);
    }
}
