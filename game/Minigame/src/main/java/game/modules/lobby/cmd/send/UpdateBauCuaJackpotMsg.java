package game.modules.lobby.cmd.send;

import game.BaseMsgEx;

import java.nio.ByteBuffer;

public class UpdateBauCuaJackpotMsg  extends BaseMsgEx {

    public  long baucuatofund =0L;

    public UpdateBauCuaJackpotMsg() {
        super(20111);
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        this.putLong(bf,this.baucuatofund);
        return this.packBuffer(bf);
    }
}
