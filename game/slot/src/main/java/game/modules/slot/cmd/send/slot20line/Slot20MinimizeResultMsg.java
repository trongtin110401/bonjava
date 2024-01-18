/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  bitzero.server.extensions.data.BaseMsg
 */
package game.modules.slot.cmd.send.slot20line;

import bitzero.server.extensions.data.BaseMsg;

import java.nio.ByteBuffer;

public class Slot20MinimizeResultMsg extends BaseMsg {
    public byte result;
    public long prize;
    public long currentMoney;

    public Slot20MinimizeResultMsg(short type) {
        super(type);
    }

    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        bf.put(this.result);
        this.putLong(bf, this.prize);
        this.putLong(bf, this.currentMoney);
        return this.packBuffer(bf);
    }
}

