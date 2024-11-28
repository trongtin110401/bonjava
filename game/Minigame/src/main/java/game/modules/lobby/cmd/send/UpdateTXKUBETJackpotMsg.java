package game.modules.lobby.cmd.send;

import game.BaseMsgEx;

import java.nio.ByteBuffer;

public class UpdateTXKUBETJackpotMsg extends BaseMsgEx {

    public long moneyHu = 0L;
    public long moneyTai = 0L;
    public long moneyXiu = 0L;

    public UpdateTXKUBETJackpotMsg() {
        super(20115);
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        this.putLong(bf, this.moneyHu);
        this.putLong(bf, this.moneyTai);
        this.putLong(bf, this.moneyXiu);
        return this.packBuffer(bf);
    }
}
