/*
 * Decompiled with CFR 0.144.
 */
package game.modules.chat.cmd.send;

import game.BaseMsgEx;

import java.nio.ByteBuffer;

public class ChatInfoKubetMsg
extends BaseMsgEx {
    public String msg;
    public byte minVipPointRequire;
    public long timeUnBan;
    public byte userType;

    public ChatInfoKubetMsg() {
        super(19003);
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

