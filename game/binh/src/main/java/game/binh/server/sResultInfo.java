// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server;

import game.binh.server.logic.KetQuaSoBai;

public class sResultInfo
{
    KetQuaSoBai[] resultWithPlayer;
    
    public sResultInfo() {
        this.resultWithPlayer = new KetQuaSoBai[4];
        for (int i = 0; i < this.resultWithPlayer.length; ++i) {
            this.resultWithPlayer[i] = new KetQuaSoBai();
        }
    }
    
    public KetQuaSoBai getResultWithPlayer(final int chair) {
        this.resultWithPlayer[chair].chair = chair;
        return this.resultWithPlayer[chair];
    }
    
    public void resetResult() {
        for (int i = 0; i < this.resultWithPlayer.length; ++i) {
            this.resultWithPlayer[i] = new KetQuaSoBai();
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("sResultInfo:------------------------------------------------------------------------------------\n");
        for (int i = 0; i < this.resultWithPlayer.length; ++i) {
            sb.append(this.resultWithPlayer[i]);
        }
        sb.append("------------------------------------------------------------------------------------------------\n");
        return sb.toString();
    }
}
