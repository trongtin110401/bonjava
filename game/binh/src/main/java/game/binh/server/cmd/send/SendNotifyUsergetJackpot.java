// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server.cmd.send;

import java.nio.ByteBuffer;
import bitzero.server.extensions.data.BaseMsg;

public class SendNotifyUsergetJackpot extends BaseMsg
{
    public long jackpot;
    public int chair;
    
    public SendNotifyUsergetJackpot() {
        super((short)3122);
    }
    
    public byte[] createData() {
        final ByteBuffer bf = this.makeBuffer();
        this.putLong(bf, this.jackpot);
        bf.put((byte)this.chair);
        return this.packBuffer(bf);
    }
}
