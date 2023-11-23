// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server.cmd.send;

import java.nio.ByteBuffer;
import bitzero.server.extensions.data.BaseMsg;

public class SendKickRoom extends BaseMsg
{
    public static final int ERROR_MONEY = 1;
    public static final int ERROR_BAO_TRI = 2;
    public byte reason;
    
    public SendKickRoom() {
        super((short)3120);
    }
    
    public byte[] createData() {
        final ByteBuffer bf = this.makeBuffer();
        bf.put(this.reason);
        return this.packBuffer(bf);
    }
}
