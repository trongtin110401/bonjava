// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server.cmd.send;

import java.nio.ByteBuffer;
import bitzero.server.extensions.data.BaseMsg;

public class SendUserExitRoom extends BaseMsg
{
    public byte nChair;
    public String nickName;
    
    public SendUserExitRoom() {
        super((short)3119);
    }
    
    public byte[] createData() {
        final ByteBuffer bf = this.makeBuffer();
        bf.put(this.nChair);
        this.putStr(bf, this.nickName);
        return this.packBuffer(bf);
    }
}
