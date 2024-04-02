/*
 * Decompiled with CFR 0.144.
 */
package game.modules.lobby.cmd.send;

import game.BaseMsgEx;

import java.nio.ByteBuffer;

public class UpdateJackpotMsg extends BaseMsgEx {
    public long potMiniPoker100 = 0L;
    public long potMiniPoker1000 = 0L;
    public long potMiniPoker10000 = 0L;
    public long potPokeGo100 = 0L;
    public long potPokeGo1000 = 0L;
    public long potPokeGo10000 = 0L;
    public long potKhoBau100 = 0L;
    public long potKhoBau1000 = 0L;
    public long potKhoBau10000 = 0L;
    public long potNDV100 = 0L;
    public long potNDV1000 = 0L;
    public long potNDV10000 = 0L;
    public long potAvengers100 = 0L;
    public long potAvengers1000 = 0L;
    public long potAvengers10000 = 0L;
    public long vqv100 = 0L;
    public long vqv1000 = 0L;
    public long vqv10000 = 0L;
    public long fish100 = 0L;
    public long fish1000 = 0L;
    //sparta game
    public long sparta100 = 0L;
    public long sparta1000 = 0L;
    public long sparta5000 = 0L;
    public long sparta10000 = 0L;
    // pot bau cua to
    public long baucuatofund = 0L;
    public long txHu = 0L;
    public long txTai = 0L;
    public long txXiu = 0L;

    public long potCaoThap1000 = 0L;
    public long potCaoThap10000 = 0L;
    public long potCaoThap50000 = 0L;
    public long potCaoThap100000 = 0L;
    public long potCaoThap500000 = 0L;

    public UpdateJackpotMsg() {
        super(20101);
    }

    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();

        // MINI POKER
        this.putLong(bf, this.potMiniPoker100);
        this.putLong(bf, this.potMiniPoker1000);
        this.putLong(bf, this.potMiniPoker10000);

        // WISHKEY
        this.putLong(bf, this.potPokeGo100);
        this.putLong(bf, this.potPokeGo1000);
        this.putLong(bf, this.potPokeGo10000);

        this.putLong(bf, this.potKhoBau100);
        this.putLong(bf, this.potKhoBau1000);
        this.putLong(bf, this.potKhoBau10000);
        this.putLong(bf, this.potNDV100);
        this.putLong(bf, this.potNDV1000);
        this.putLong(bf, this.potNDV10000);
        this.putLong(bf, this.potAvengers100);
        this.putLong(bf, this.potAvengers1000);
        this.putLong(bf, this.potAvengers10000);
        this.putLong(bf, this.vqv100);
        this.putLong(bf, this.vqv1000);
        this.putLong(bf, this.vqv10000);
        this.putLong(bf, this.fish100);
        this.putLong(bf, this.fish1000);

        //spartan game
        this.putLong(bf, this.sparta100);
        this.putLong(bf, this.sparta1000);
        this.putLong(bf, this.sparta5000);
        this.putLong(bf, this.sparta10000);
        this.putLong(bf, this.baucuatofund);

        this.putLong(bf, this.txHu);
        this.putLong(bf, this.txTai);
        this.putLong(bf, this.txXiu);

        // cao thap
        this.putLong(bf, this.potCaoThap1000);
        this.putLong(bf, this.potCaoThap10000);
        this.putLong(bf, this.potCaoThap50000);
        this.putLong(bf, this.potCaoThap100000);
        this.putLong(bf, this.potCaoThap500000);

        return this.packBuffer(bf);
    }

    public long getPotMiniPoker100() {
        return potMiniPoker100;
    }

    public void setPotMiniPoker100(long potMiniPoker100) {
        this.potMiniPoker100 = potMiniPoker100;
    }

    public long getPotMiniPoker1000() {
        return potMiniPoker1000;
    }

    public void setPotMiniPoker1000(long potMiniPoker1000) {
        this.potMiniPoker1000 = potMiniPoker1000;
    }

    public long getPotMiniPoker10000() {
        return potMiniPoker10000;
    }

    public void setPotMiniPoker10000(long potMiniPoker10000) {
        this.potMiniPoker10000 = potMiniPoker10000;
    }

    public long getPotPokeGo100() {
        return potPokeGo100;
    }

    public void setPotPokeGo100(long potPokeGo100) {
        this.potPokeGo100 = potPokeGo100;
    }

    public long getPotPokeGo1000() {
        return potPokeGo1000;
    }

    public void setPotPokeGo1000(long potPokeGo1000) {
        this.potPokeGo1000 = potPokeGo1000;
    }

    public long getPotPokeGo10000() {
        return potPokeGo10000;
    }

    public void setPotPokeGo10000(long potPokeGo10000) {
        this.potPokeGo10000 = potPokeGo10000;
    }

    public long getPotKhoBau100() {
        return potKhoBau100;
    }

    public void setPotKhoBau100(long potKhoBau100) {
        this.potKhoBau100 = potKhoBau100;
    }

    public long getPotKhoBau1000() {
        return potKhoBau1000;
    }

    public void setPotKhoBau1000(long potKhoBau1000) {
        this.potKhoBau1000 = potKhoBau1000;
    }

    public long getPotKhoBau10000() {
        return potKhoBau10000;
    }

    public void setPotKhoBau10000(long potKhoBau10000) {
        this.potKhoBau10000 = potKhoBau10000;
    }

    public long getPotNDV100() {
        return potNDV100;
    }

    public void setPotNDV100(long potNDV100) {
        this.potNDV100 = potNDV100;
    }

    public long getPotNDV1000() {
        return potNDV1000;
    }

    public void setPotNDV1000(long potNDV1000) {
        this.potNDV1000 = potNDV1000;
    }

    public long getPotNDV10000() {
        return potNDV10000;
    }

    public void setPotNDV10000(long potNDV10000) {
        this.potNDV10000 = potNDV10000;
    }

    public long getPotAvengers100() {
        return potAvengers100;
    }

    public void setPotAvengers100(long potAvengers100) {
        this.potAvengers100 = potAvengers100;
    }

    public long getPotAvengers1000() {
        return potAvengers1000;
    }

    public void setPotAvengers1000(long potAvengers1000) {
        this.potAvengers1000 = potAvengers1000;
    }

    public long getPotAvengers10000() {
        return potAvengers10000;
    }

    public void setPotAvengers10000(long potAvengers10000) {
        this.potAvengers10000 = potAvengers10000;
    }

    public long getVqv100() {
        return vqv100;
    }

    public void setVqv100(long vqv100) {
        this.vqv100 = vqv100;
    }

    public long getVqv1000() {
        return vqv1000;
    }

    public void setVqv1000(long vqv1000) {
        this.vqv1000 = vqv1000;
    }

    public long getVqv10000() {
        return vqv10000;
    }

    public void setVqv10000(long vqv10000) {
        this.vqv10000 = vqv10000;
    }

    public long getFish100() {
        return fish100;
    }

    public void setFish100(long fish100) {
        this.fish100 = fish100;
    }

    public long getFish1000() {
        return fish1000;
    }

    public void setFish1000(long fish1000) {
        this.fish1000 = fish1000;
    }

    public long getSparta100() {
        return sparta100;
    }

    public void setSparta100(long sparta100) {
        this.sparta100 = sparta100;
    }

    public long getSparta1000() {
        return sparta1000;
    }

    public void setSparta1000(long sparta1000) {
        this.sparta1000 = sparta1000;
    }

    public long getSparta5000() {
        return sparta5000;
    }

    public void setSparta5000(long sparta5000) {
        this.sparta5000 = sparta5000;
    }

    public long getSparta10000() {
        return sparta10000;
    }

    public void setSparta10000(long sparta10000) {
        this.sparta10000 = sparta10000;
    }

    public long getBaucuatofund() {
        return baucuatofund;
    }

    public void setBaucuatofund(long baucuatofund) {
        this.baucuatofund = baucuatofund;
    }

    public long getTxHu() {
        return txHu;
    }

    public void setTxHu(long txHu) {
        this.txHu = txHu;
    }

    public long getTxTai() {
        return txTai;
    }

    public void setTxTai(long txTai) {
        this.txTai = txTai;
    }

    public long getTxXiu() {
        return txXiu;
    }

    public void setTxXiu(long txXiu) {
        this.txXiu = txXiu;
    }

    public long getPotCaoThap1000() {
        return potCaoThap1000;
    }

    public void setPotCaoThap1000(long potCaoThap1000) {
        this.potCaoThap1000 = potCaoThap1000;
    }

    public long getPotCaoThap10000() {
        return potCaoThap10000;
    }

    public void setPotCaoThap10000(long potCaoThap10000) {
        this.potCaoThap10000 = potCaoThap10000;
    }

    public long getPotCaoThap50000() {
        return potCaoThap50000;
    }

    public void setPotCaoThap50000(long potCaoThap50000) {
        this.potCaoThap50000 = potCaoThap50000;
    }

    public long getPotCaoThap100000() {
        return potCaoThap100000;
    }

    public void setPotCaoThap100000(long potCaoThap100000) {
        this.potCaoThap100000 = potCaoThap100000;
    }

    public long getPotCaoThap500000() {
        return potCaoThap500000;
    }

    public void setPotCaoThap500000(long potCaoThap500000) {
        this.potCaoThap500000 = potCaoThap500000;
    }
}

