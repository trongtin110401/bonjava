/*
 * Decompiled with CFR 0.144.
 */
package game.modules.minigame.cmd.send;

import game.BaseMsgEx;

import java.nio.ByteBuffer;

public class StartNewGameTaiXiuMsg
        extends BaseMsgEx {
    public long referenceId;
    public long moneyHu;
    public short remainTimeRutLoc;
    public String startSessionTime;

    public StartNewGameTaiXiuMsg() {
        super(2115);
    }

    public byte[] createData() {
        ByteBuffer buffer = this.makeBuffer();
        buffer.putLong(this.referenceId);
        buffer.putLong(this.moneyHu);
        buffer.putShort(this.remainTimeRutLoc);
        this.putStr(buffer, startSessionTime);
        return this.packBuffer(buffer);
    }
}

