/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  bitzero.server.extensions.data.BaseMsg
 */
package game.modules.slot.cmd.send.tamhung;

import bitzero.server.extensions.data.BaseMsg;

import java.nio.ByteBuffer;

public class TamHungFreeDailyMsg
extends BaseMsg {
    public byte remain = 0;

    public TamHungFreeDailyMsg() {
        super((short)14012);
    }

    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        bf.put(this.remain);
        return this.packBuffer(bf);
    }
}

