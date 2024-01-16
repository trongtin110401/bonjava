/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  bitzero.server.extensions.data.BaseMsg
 */
package game.modules.slot.cmd.send.slot25linebasic;

import bitzero.server.extensions.data.BaseMsg;

import java.nio.ByteBuffer;

public class UpdatePotMsg extends BaseMsg {
    public long value;
    public byte x2 = 0;

    public UpdatePotMsg(short type) {
        super(type);
    }

    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        bf.putLong(this.value);
        bf.put(this.x2);
        return this.packBuffer(bf);
    }
}

