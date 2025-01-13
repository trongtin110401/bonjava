/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  bitzero.server.extensions.data.BaseMsg
 */
package game.xocdia.cmd.send;

import bitzero.server.extensions.data.BaseMsg;
import java.nio.ByteBuffer;

public class ChatMsg extends BaseMsg {
    public String nickname;
    public boolean isIcon;
    public String content;
    public short type = 0;
    public long money = 0;


    public ChatMsg() {
        super((short)3090);
    }

    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        this.putStr(bf, this.nickname);
        this.putBoolean(bf, Boolean.valueOf(this.isIcon));
        this.putStr(bf, this.content);
        bf.putShort(type);
        bf.putLong(money);
        return this.packBuffer(bf);
    }
}

