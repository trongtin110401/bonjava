// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server.cmd.receive;

import java.nio.ByteBuffer;
import bitzero.server.extensions.data.DataCmd;
import bitzero.server.extensions.data.BaseCmd;

public class RevBinhSoChi extends BaseCmd
{
    public byte[] chi1;
    public byte[] chi2;
    public byte[] chi3;
    
    public RevBinhSoChi(final DataCmd dataCmd) {
        super(dataCmd);
        this.unpackData();
    }
    
    public void unpackData() {
        final ByteBuffer bf = this.makeBuffer();
        this.chi1 = this.readByteArray(bf);
        this.chi2 = this.readByteArray(bf);
        this.chi3 = this.readByteArray(bf);
    }
}
