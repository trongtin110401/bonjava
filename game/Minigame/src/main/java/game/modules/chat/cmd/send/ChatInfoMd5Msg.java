/*
 * Decompiled with CFR 0.144.
 */
package game.modules.chat.cmd.send;

import game.BaseMsgEx;

import java.nio.ByteBuffer;

public class ChatInfoMd5Msg
extends BaseMsgEx {
    public String msg;
    public byte minVipPointRequire;
    public long timeUnBan;
    public byte userType;

    public ChatInfoMd5Msg() {
        super(18013);
    }

    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        this.putStr(bf, this.msg);
        bf.put(this.minVipPointRequire);
        bf.putLong(this.timeUnBan);
        bf.put(this.userType);
        return this.packBuffer(bf);
    }
}

