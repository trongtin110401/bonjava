// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server.logic;

public class KetQuaTinhSap
{
    public int tinhSap1;
    public int tinhSap2;
    public int tongChiThang;
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("tinhSap1:").append(this.tinhSap1).append("/");
        sb.append("tinhSap2:").append(this.tinhSap2).append("/");
        sb.append("tongChiThang:").append(this.tongChiThang).append("/");
        return sb.toString();
    }
}
