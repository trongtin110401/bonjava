/*
 * Decompiled with CFR 0.144.
 */
package game.modules.lobby.cmd.send;

import game.BaseMsgEx;
import game.modules.minigame.cmd.MiniGameCMD;

import java.nio.ByteBuffer;

public class ResultCashoutBank
        extends BaseMsgEx {
    public long currentMoney;


    public ResultCashoutBank() {
        super(MiniGameCMD.CMD_WITHDRAW_BANK_MANUAL);
    }

    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        bf.putLong(this.currentMoney);

        return this.packBuffer(bf);
    }
}

