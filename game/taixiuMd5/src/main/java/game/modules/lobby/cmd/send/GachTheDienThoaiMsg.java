/*
 * Decompiled with CFR 0.144.
 */
package game.modules.lobby.cmd.send;

import game.BaseMsgEx;

import java.nio.ByteBuffer;

public class GachTheDienThoaiMsg
extends BaseMsgEx {
    public long currentMoney;
    public long timeFail;
    public int numFail;

    public GachTheDienThoaiMsg() {
        super(20200);
    }

    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        bf.putLong(this.currentMoney);
        bf.putLong(this.timeFail);
        bf.putInt(this.numFail);
        return this.packBuffer(bf);
    }
}

