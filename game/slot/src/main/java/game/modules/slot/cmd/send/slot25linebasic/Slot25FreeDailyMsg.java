/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  bitzero.server.extensions.data.BaseMsg
 */
package game.modules.slot.cmd.send.slot25linebasic;

import bitzero.server.extensions.data.BaseMsg;

import java.nio.ByteBuffer;

public class Slot25FreeDailyMsg extends BaseMsg {
    public byte remain = 0;

    public Slot25FreeDailyMsg(short type) {
        super((short) type);
    }

    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        bf.put(this.remain);
        return this.packBuffer(bf);
    }
}

