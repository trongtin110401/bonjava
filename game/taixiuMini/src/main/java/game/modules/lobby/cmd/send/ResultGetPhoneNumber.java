/*
 * Decompiled with CFR 0.144.
 */
package game.modules.lobby.cmd.send;

import game.BaseMsgEx;
import game.modules.minigame.cmd.MiniGameCMD;

import java.nio.ByteBuffer;

public class ResultGetPhoneNumber
        extends BaseMsgEx {
    public long phoneNumber = 0;


    public ResultGetPhoneNumber() {
        super(MiniGameCMD.CMD_GET_PHONE_NUMBER);
    }

    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        bf.putLong(this.phoneNumber);

        return this.packBuffer(bf);
    }
}

