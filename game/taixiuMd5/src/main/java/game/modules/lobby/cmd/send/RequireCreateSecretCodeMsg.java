package game.modules.lobby.cmd.send;

import game.BaseMsgEx;

import java.nio.ByteBuffer;

public class RequireCreateSecretCodeMsg  extends BaseMsgEx {
    public int stepCode;
    public RequireCreateSecretCodeMsg() {
        super(20302);
    }
    @Override
    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        bf.putInt(this.stepCode);
        return this.packBuffer(bf);
    }
}
