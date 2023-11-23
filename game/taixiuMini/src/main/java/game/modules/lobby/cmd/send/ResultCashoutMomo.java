/*
 * Decompiled with CFR 0.144.
 */
package game.modules.lobby.cmd.send;

import game.BaseMsgEx;
import game.modules.minigame.cmd.MiniGameCMD;

import java.nio.ByteBuffer;

public class ResultCashoutMomo
        extends BaseMsgEx {
    public long currentMoney;


    public ResultCashoutMomo() {
        super(MiniGameCMD.CMD_WITHDRAW_MOMO_MANUAL);
    }

    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        bf.putLong(this.currentMoney);

        return this.packBuffer(bf);
    }
}

