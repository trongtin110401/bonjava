// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server.cmd.receive;

import java.nio.ByteBuffer;
import bitzero.server.extensions.data.DataCmd;
import bitzero.server.extensions.data.BaseCmd;

public class RevCheatCard extends BaseCmd
{
    public boolean isCheat;
    public byte[] cards;
    
    public RevCheatCard(final DataCmd dataCmd) {
        super(dataCmd);
        this.isCheat = false;
        this.unpackData();
    }
    
    public void unpackData() {
        final ByteBuffer bf = this.makeBuffer();
        this.isCheat = this.readBoolean(bf);
        if (this.isCheat) {
            this.cards = this.readByteArray(bf);
        }
    }
}
