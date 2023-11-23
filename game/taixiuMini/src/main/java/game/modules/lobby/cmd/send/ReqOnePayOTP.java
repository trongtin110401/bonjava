package game.modules.lobby.cmd.send;

import game.BaseMsgEx;

import java.nio.ByteBuffer;

public class ReqOnePayOTP extends BaseMsgEx {
    public String transId;
    public String techcombankTranS;
    public ReqOnePayOTP() {
        super(20298);
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        this.putStr(bf,transId);
        this.putStr(bf,techcombankTranS);
        return this.packBuffer(bf);
    }
}
