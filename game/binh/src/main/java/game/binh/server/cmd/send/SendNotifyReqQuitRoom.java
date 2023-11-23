// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server.cmd.send;

import java.nio.ByteBuffer;
import bitzero.server.extensions.data.BaseMsg;

public class SendNotifyReqQuitRoom extends BaseMsg
{
    public byte chair;
    public boolean reqQuitRoom;
    
    public SendNotifyReqQuitRoom() {
        super((short)3111);
    }
    
    public byte[] createData() {
        final ByteBuffer bf = this.makeBuffer();
        bf.put(this.chair);
        this.putBoolean(bf, Boolean.valueOf(this.reqQuitRoom));
        return this.packBuffer(bf);
    }
}
