/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  bitzero.server.extensions.data.BaseMsg
 */
package game.modules.slot.cmd.send.slot20line;

import bitzero.server.extensions.data.BaseMsg;

import java.nio.ByteBuffer;

public class Slot20InfoMsg extends BaseMsg {
    public String ngayX2;
    public byte remain;
    public long currentMoney;

    public Slot20InfoMsg(short type) {
        super(type);
    }

    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        bf.put(this.remain);
        bf.putLong(this.currentMoney);
        this.putStr(bf, this.ngayX2);
        return this.packBuffer(bf);
    }
}

