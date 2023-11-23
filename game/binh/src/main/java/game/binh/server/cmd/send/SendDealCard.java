// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server.cmd.send;

import java.nio.ByteBuffer;
import bitzero.server.extensions.data.BaseMsg;

public class SendDealCard extends BaseMsg
{
    public byte[] cards;
    public int maubinh;
    public int gameId;
    
    public SendDealCard() {
        super((short)3105);
        this.cards = new byte[13];
        this.maubinh = 0;
    }
    
    public byte[] createData() {
        final ByteBuffer bf = this.makeBuffer();
        this.putByteArray(bf, this.cards);
        bf.put((byte)this.maubinh);
        bf.putInt(this.gameId);
        bf.put((byte)60);
        return this.packBuffer(bf);
    }
}
