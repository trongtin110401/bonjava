/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  bitzero.server.extensions.data.BaseMsg
 */
package game.modules.slot.cmd.send.slot25extend;

import bitzero.server.extensions.data.BaseMsg;

import java.nio.ByteBuffer;

public class Slot25TotalFreeSpin extends BaseMsg {
    public int prize;
    public byte ratio;

    public Slot25TotalFreeSpin(short type) {
        super(type);
    }

    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        bf.putInt(this.prize);
        bf.put(this.ratio);
        return super.createData();
    }
}

