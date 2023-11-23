// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server.cmd.send;

import java.nio.ByteBuffer;
import bitzero.server.extensions.data.BaseMsg;

public class SendBinhSoChiSuccess extends BaseMsg
{
    public static final byte BAI_KHONG_HOP_LE = 1;
    public static final byte NGUOI_CHOI_KHONG_HOP_LE = 2;
    public int chair;
    
    public SendBinhSoChiSuccess() {
        super((short)3101);
    }
    
    public byte[] createData() {
        final ByteBuffer bf = this.makeBuffer();
        bf.put((byte)this.chair);
        return this.packBuffer(bf);
    }
}
