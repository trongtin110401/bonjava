// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server.logic;

import game.binh.server.GamePlayer;

public class KetQuaSoBai
{
    public int chair;
    public int maubinhType;
    public byte[] chi1;
    public byte[] chi2;
    public byte[] chi3;
    public long[] moneyInChi;
    public long chiCommon;
    public long moneyCommon;
    public long moneySap;
    public long[] moneySapLang;
    public long moneyAt;
    public boolean isInit;
    
    public KetQuaSoBai() {
        this.moneyInChi = new long[3];
        this.moneySapLang = new long[4];
        this.moneyAt = 0L;
        this.isInit = false;
    }
    
    public synchronized void initCard(final GamePlayer gp1, final int rule) {
        if (!this.isInit) {
            this.isInit = true;
            this.chi1 = gp1.spInfo.sorttedCard.ChiMot().toByteArray();
            this.chi2 = gp1.spInfo.sorttedCard.ChiHai().toByteArray();
            this.chi3 = gp1.spInfo.sorttedCard.ChiBa().toByteArray();
            this.maubinhType = gp1.kiemTraMauBinh(rule);
        }
    }
    
    public void calculateMoneyCommon() {
        for (int i = 0; i < this.moneyInChi.length; ++i) {
            this.moneyCommon += this.moneyInChi[i];
        }
        this.moneyCommon += this.getMoneySapTong();
        this.moneyCommon += this.moneyAt;
        this.chiCommon = this.moneyCommon;
    }
    
    public String toString(final int rule) {
        final StringBuilder sb = new StringBuilder();
        sb.append("char:").append(this.chair).append("/");
        sb.append("mauBinh:").append(this.maubinhType).append("/");
        if (this.chi1 != null && this.chi2 != null && this.chi3 != null) {
            final GroupCard gc1 = new GroupCard(this.chi1);
            gc1.kiemtraBo(rule);
            sb.append("chi1:").append(gc1).append("/");
            final GroupCard gc2 = new GroupCard(this.chi2);
            gc2.kiemtraBo(rule);
            sb.append("chi2:").append(gc2).append("/");
            final GroupCard gc3 = new GroupCard(this.chi3);
            gc3.kiemtraBo(rule);
            sb.append("chi3:").append(gc3).append("/");
        }
        sb.append("moneyInChi:").append(this.moneyInChi[0]).append("*").append(this.moneyInChi[1]).append("*").append(this.moneyInChi[2]).append("*");
        sb.append("moneySap:").append(this.moneySap).append("/");
        for (int i = 0; i < 4; ++i) {
            sb.append("saplang").append(i).append(":").append(this.moneySapLang[i]).append("/");
        }
        sb.append("TongSap:").append(this.getMoneySapTong()).append("/");
        sb.append("moneyCommon:").append(this.moneyCommon).append("/\n");
        return sb.toString();
    }
    
    public long getMoneySapTong() {
        long res = this.moneySap;
        for (int i = 0; i < 4; ++i) {
            res += this.moneySapLang[i];
        }
        return res;
    }
    
    public void thangThuaSapLang(final int chair) {
        for (int i = 0; i < 3; ++i) {
            final long[] arrl = this.moneySapLang;
            final int n = chair;
            arrl[n] += this.moneyInChi[i] * 1L;
        }
        final long[] arrl2 = this.moneySapLang;
        final int n2 = chair;
        arrl2[n2] += this.moneySap * 1L;
    }
    
    public void thangThuaSapLang(final KetQuaSoBai kq21) {
        final long[] arrl = this.moneySapLang;
        final int n = kq21.chair;
        arrl[n] -= kq21.moneySapLang[this.chair];
    }
}
