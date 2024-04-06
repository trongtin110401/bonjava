/*
 * Decompiled with CFR 0.144.
 */
package game.modules.minigame.cmd.send;

import game.BaseMsgEx;
import java.nio.ByteBuffer;

public class UpdateTaiXiuPerSecondMsg extends BaseMsgEx {
    public short remainTime;
    public boolean bettingState;
    public long potTai;
    public long potXiu;
    // số lượng người chơi mỗi phiên bên tài
    public long numBetTai;
    // số lượng người chơi mỗi phiên bên xỉu
    public long numBetXiu;
    public long moneyHu;

    public UpdateTaiXiuPerSecondMsg() {
        super(2112);
    }

    public byte[] createData() {
        ByteBuffer buffer = this.makeBuffer();
        buffer.putShort(this.remainTime);
        this.putBoolean(buffer, Boolean.valueOf(this.bettingState));
        buffer.putLong(this.potTai);
        buffer.putLong(this.potXiu);
        buffer.putLong(this.numBetTai);
        buffer.putLong(this.numBetXiu);
        buffer.putLong(this.moneyHu);
        return this.packBuffer(buffer);
    }
}

