// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server.cmd.send;

import java.nio.ByteBuffer;
import bitzero.server.extensions.data.BaseMsg;

public class SendUpdateAutoStart extends BaseMsg
{
    public boolean isAutoStart;
    public byte autoStartTime;
    
    public SendUpdateAutoStart() {
        super((short)3107);
    }
    
    public byte[] createData() {
        final ByteBuffer bf = this.makeBuffer();
        this.putBoolean(bf, Boolean.valueOf(this.isAutoStart));
        bf.put(this.autoStartTime);
        return this.packBuffer(bf);
    }
}
