// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server.cmd.send;

import java.nio.ByteBuffer;
import bitzero.server.extensions.data.BaseMsg;

public class SendXepLai extends BaseMsg
{
    public int chair;
    
    public SendXepLai() {
        super((short)3108);
    }
    
    public byte[] createData() {
        final ByteBuffer bf = this.makeBuffer();
        bf.put((byte)this.chair);
        return this.packBuffer(bf);
    }
}
