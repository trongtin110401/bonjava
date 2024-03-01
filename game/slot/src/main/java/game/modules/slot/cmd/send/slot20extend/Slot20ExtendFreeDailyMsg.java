/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  bitzero.server.extensions.data.BaseMsg
 */
package game.modules.slot.cmd.send.slot20extend;

import bitzero.server.extensions.data.BaseMsg;

import java.nio.ByteBuffer;

public class Slot20ExtendFreeDailyMsg extends BaseMsg {
    public byte remain = 0;

    public Slot20ExtendFreeDailyMsg(short type) {
        super((short) type);
    }

    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        bf.put(this.remain);
        return this.packBuffer(bf);
    }
}

