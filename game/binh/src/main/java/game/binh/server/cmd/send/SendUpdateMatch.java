// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server.cmd.send;

import java.nio.ByteBuffer;
import game.binh.server.GamePlayer;
import bitzero.server.extensions.data.BaseMsg;

public class SendUpdateMatch extends BaseMsg
{
    public byte chair;
    public boolean[] hasInfoAtChair;
    public GamePlayer[] pInfos;
    
    public SendUpdateMatch() {
        super((short)3123);
        this.hasInfoAtChair = new boolean[4];
        this.pInfos = new GamePlayer[4];
    }
    
    public byte[] createData() {
        final ByteBuffer bf = this.makeBuffer();
        bf.put(this.chair);
        this.putBooleanArray(bf, this.hasInfoAtChair);
        for (int i = 0; i < 4; ++i) {
            if (this.hasInfoAtChair[i]) {
                final GamePlayer gp = this.pInfos[i];
                this.putStr(bf, gp.pInfo.nickName);
                this.putStr(bf, gp.pInfo.avatarUrl);
                bf.putLong(gp.gameMoneyInfo.currentMoney);
                bf.putInt(gp.getPlayerStatus());
            }
        }
        return this.packBuffer(bf);
    }
}
