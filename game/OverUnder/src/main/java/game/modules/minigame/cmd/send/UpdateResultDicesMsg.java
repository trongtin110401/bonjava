/*
 * Decompiled with CFR 0.144.
 */
package game.modules.minigame.cmd.send;

import game.BaseMsgEx;
import java.nio.ByteBuffer;

public class UpdateResultDicesMsg
extends BaseMsgEx {
    public short result;
    public short dice1;
    public short dice2;
    public short dice3;

    public UpdateResultDicesMsg() {
        super(2113);
    }

    public byte[] createData() {
        ByteBuffer buffer = this.makeBuffer();
        buffer.putShort(this.result);
        buffer.putShort(this.dice1);
        buffer.putShort(this.dice2);
        buffer.putShort(this.dice3);
        return this.packBuffer(buffer);
    }
}

