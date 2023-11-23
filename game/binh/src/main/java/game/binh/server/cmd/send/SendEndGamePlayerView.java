// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server.cmd.send;

import bitzero.server.extensions.data.BaseMsg;

public class SendEndGamePlayerView extends BaseMsg
{
    public SendEndGamePlayerView(final short s, final int i) {
        super(s, i);
    }
    
    public SendEndGamePlayerView(final short s) {
        super(s);
    }
}
