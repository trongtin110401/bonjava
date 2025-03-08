/*
 * Decompiled with CFR 0.144.
 */
package game.modules.minigame.cmd.send;

import game.BaseMsgEx;

import java.nio.ByteBuffer;

public class TaiXiuInfoMsg extends BaseMsgEx {
    public short gameId;
    public short moneyType;
    public long referenceId;
    public short remainTime;
    public boolean bettingState;
    public long potTai;
    public long potXiu;
    public long myBetTai;
    public long myBetXiu;
    public long potChan;
    public long potLe;
    public long myBetChan;
    public long myBetLe;
    public short dice1 = 0;
    public short dice2 = 0;
    public short dice3 = 0;
    public short remainTimeRutLoc = 0;
    public long moneyHu;
    public String startSessionTime = "";

//    public String streamUrl = "http://139.180.155.96/taixiu.html";
    public String streamUrl = System.getenv("TAI_XIU_KUBET_STREAM_URL");

    public TaiXiuInfoMsg() {
        super(2111);
    }

    public byte[] createData() {
        ByteBuffer buffer = this.makeBuffer();
        buffer.putShort(this.gameId);
        buffer.putShort(this.moneyType);
        buffer.putLong(this.referenceId);
        buffer.putShort(this.remainTime);
        this.putBoolean(buffer, Boolean.valueOf(this.bettingState));
        buffer.putLong(this.potTai);
        buffer.putLong(this.potXiu);
        buffer.putLong(this.myBetTai);
        buffer.putLong(this.myBetXiu);
        buffer.putLong(this.potChan);
        buffer.putLong(this.potLe);
        buffer.putLong(this.myBetChan);
        buffer.putLong(this.myBetLe);
        buffer.putShort(this.dice1);
        buffer.putShort(this.dice2);
        buffer.putShort(this.dice3);
        buffer.putShort(this.remainTimeRutLoc);
        buffer.putLong(this.moneyHu);
        this.putStr(buffer, streamUrl);
        this.putStr(buffer, startSessionTime);
        return this.packBuffer(buffer);
    }
}

