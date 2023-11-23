// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server.cmd.send;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import game.binh.server.logic.KetQuaSoBai;
import java.util.List;
import bitzero.server.extensions.data.BaseMsg;

public class SendEndGame extends BaseMsg
{
    public List<KetQuaSoBai> ketqua;
    public long[] moneyArray;
    public int countdownsochi;
    
    public SendEndGame() {
        super((short)3103);
        this.ketqua = new ArrayList<KetQuaSoBai>();
        this.moneyArray = new long[4];
    }
    
    public byte[] createData() {
        final ByteBuffer bf = this.makeBuffer();
        bf.putShort((short)this.ketqua.size());
        for (int i = 0; i < this.ketqua.size(); ++i) {
            final KetQuaSoBai kq = this.ketqua.get(i);
            bf.put((byte)kq.chair);
            bf.putInt(kq.maubinhType);
            this.putByteArray(bf, kq.chi1);
            this.putByteArray(bf, kq.chi2);
            this.putByteArray(bf, kq.chi3);
            this.putLongArray(bf, kq.moneyInChi);
            bf.putLong(kq.moneyAt);
            bf.putLong(kq.moneyCommon);
            bf.putLong(kq.getMoneySapTong());
            bf.putLong(this.moneyArray[kq.chair]);
        }
        bf.put((byte)this.countdownsochi);
        return this.packBuffer(bf);
    }
}
