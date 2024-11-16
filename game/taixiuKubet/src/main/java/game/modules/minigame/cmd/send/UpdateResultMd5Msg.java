/*
 * Decompiled with CFR 0.144.
 */
package game.modules.minigame.cmd.send;

import game.BaseMsgEx;

import java.nio.ByteBuffer;

public class UpdateResultMd5Msg
        extends BaseMsgEx {
    public String md5;

    // todo : code 552113 là kết quả
    public UpdateResultMd5Msg() {
        super(2113);
    }

    public byte[] createData() {
        ByteBuffer buffer = this.makeBuffer();
        super.putStr(buffer, this.md5);
        return this.packBuffer(buffer);
    }
}

