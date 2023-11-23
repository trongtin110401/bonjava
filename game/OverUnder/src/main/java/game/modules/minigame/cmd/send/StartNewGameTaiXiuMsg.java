/*
 * Decompiled with CFR 0.144.
 */
package game.modules.minigame.cmd.send;

import game.BaseMsgEx;
import java.nio.ByteBuffer;

public class StartNewGameTaiXiuMsg
extends BaseMsgEx {
    public long referenceId;
    public short remainTimeRutLoc;

    public StartNewGameTaiXiuMsg() {
        super(2115);
    }

    public byte[] createData() {
        ByteBuffer buffer = this.makeBuffer();
        buffer.putLong(this.referenceId);
        buffer.putShort(this.remainTimeRutLoc);
        return this.packBuffer(buffer);
    }
}

