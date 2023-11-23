// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server.cmd.send;

import java.nio.ByteBuffer;
import bitzero.server.extensions.data.BaseMsg;

public class SendUpdateOwnerRoom extends BaseMsg
{
    public int ownerChair;
    
    public SendUpdateOwnerRoom() {
        super((short)3117);
    }
    
    public byte[] createData() {
        final ByteBuffer bf = this.makeBuffer();
        bf.put((byte)this.ownerChair);
        return this.packBuffer(bf);
    }
}
