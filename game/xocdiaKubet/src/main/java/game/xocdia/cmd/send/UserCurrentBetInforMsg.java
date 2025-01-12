package game.xocdia.cmd.send;

import bitzero.server.extensions.data.BaseMsg;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;

import java.nio.ByteBuffer;

public class UserCurrentBetInforMsg  extends BaseMsg {
    public  long chan;
    public  long le ;
    public  long trang4;
    public  long den4;
    public  long den3;
    public  long trang3;

    public UserCurrentBetInforMsg() {
        super((short) 3166);
    }
    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();

        bf.putLong(this.chan);
        bf.putLong(this.le);
        bf.putLong(this.trang4);
        bf.putLong(this.den4);
        bf.putLong(this.den3);
        bf.putLong(this.trang3);
        return this.packBuffer(bf);
    }
}
