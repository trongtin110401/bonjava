/*
 * Decompiled with CFR 0.144.
 */
package game.modules.chat.cmd.send;

import game.BaseMsgEx;

import java.nio.ByteBuffer;

public class ChatMd5Msg
extends BaseMsgEx {
    public String nickname = "";
    public String mesasge = "";

    public ChatMd5Msg() {
        super(18000);
    }

    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        this.putStr(bf, this.nickname);
        this.putStr(bf, this.mesasge);
        return this.packBuffer(bf);
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMesasge() {
        return mesasge;
    }

    public void setMesasge(String mesasge) {
        this.mesasge = mesasge;
    }
}

