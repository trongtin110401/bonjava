/*
 * Decompiled with CFR 0.144.
 */
package game.modules.minigame.cmd.send;

import bitzero.server.util.BinaryHelper;
import game.BaseMsgEx;
import org.apache.commons.lang.StringUtils;

import java.nio.ByteBuffer;

public class UpdateTaiXiuPerSecondMsg extends BaseMsgEx {
    public short remainTime;
    public boolean bettingState;
    public long potTai;
    public long potXiu;
    public long potChan;
    public long potLe;
    // số lượng người chơi mỗi phiên bên tài
    public long numBetTai;
    // số lượng người chơi mỗi phiên bên xỉu
    public long numBetXiu;
    public long numBetChan;
    // số lượng người chơi mỗi phiên bên xỉu
    public long numBetLe;
    public long moneyHu;

    public boolean hasPlaintTextResult = false;

    public UpdateTaiXiuPerSecondMsg() {
        super(2112);
    }

    public byte[] createData() {
        ByteBuffer buffer = this.makeBuffer();
        buffer.putShort(this.remainTime);
        this.putBoolean(buffer, this.bettingState);
        buffer.putLong(this.potTai);
        buffer.putLong(this.potXiu);
        buffer.putLong(this.potChan);
        buffer.putLong(this.potLe);
        buffer.putLong(this.numBetTai);
        buffer.putLong(this.numBetXiu);
        buffer.putLong(this.numBetChan);
        buffer.putLong(this.numBetLe);
        buffer.putLong(this.moneyHu);
        return this.packBuffer(buffer);
    }
}

