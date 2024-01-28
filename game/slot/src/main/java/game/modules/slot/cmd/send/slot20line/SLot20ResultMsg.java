/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  bitzero.server.extensions.data.BaseMsg
 */
package game.modules.slot.cmd.send.slot20line;

import bitzero.server.extensions.data.BaseMsg;

import java.nio.ByteBuffer;

public class SLot20ResultMsg extends BaseMsg {
    public long referenceId;
    public byte result;
    public String matrix = "";
    public String linesWin = "";
    public String haiSao = "";
    public long prize;
    public long currentMoney;
    public boolean isFreeSpin;
    public int freeSpin = 0;

    public SLot20ResultMsg(short type) {
        super(type);
    }

    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        bf.putLong(this.referenceId);
        bf.put(this.result);
        this.putStr(bf, this.matrix);
        this.putStr(bf, this.linesWin);
        this.putStr(bf, this.haiSao);
        bf.putLong(this.prize);
        bf.putLong(this.currentMoney);
        this.putBoolean(bf, isFreeSpin);
        bf.putInt(freeSpin);
        return this.packBuffer(bf);
    }

    public long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(long referenceId) {
        this.referenceId = referenceId;
    }

    public byte getResult() {
        return result;
    }

    public void setResult(byte result) {
        this.result = result;
    }

    public String getMatrix() {
        return matrix;
    }

    public void setMatrix(String matrix) {
        this.matrix = matrix;
    }

    public String getLinesWin() {
        return linesWin;
    }

    public void setLinesWin(String linesWin) {
        this.linesWin = linesWin;
    }

    public String getHaiSao() {
        return haiSao;
    }

    public void setHaiSao(String haiSao) {
        this.haiSao = haiSao;
    }

    public long getPrize() {
        return prize;
    }

    public void setPrize(long prize) {
        this.prize = prize;
    }

    public long getCurrentMoney() {
        return currentMoney;
    }

    public void setCurrentMoney(long currentMoney) {
        this.currentMoney = currentMoney;
    }

    public boolean isFreeSpin() {
        return isFreeSpin;
    }

    public void setFreeSpin(boolean freeSpin) {
        isFreeSpin = freeSpin;
    }

    public int getFreeSpin() {
        return freeSpin;
    }

    public void setFreeSpin(int freeSpin) {
        this.freeSpin = freeSpin;
    }
}

