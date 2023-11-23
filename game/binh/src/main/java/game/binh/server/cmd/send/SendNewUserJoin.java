// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server.cmd.send;

import java.nio.ByteBuffer;
import game.entities.PlayerInfo;
import bitzero.server.extensions.data.BaseMsg;

public class SendNewUserJoin extends BaseMsg
{
    public long money;
    public String uName;
    public String avtUrl;
    public int uChair;
    public int uStatus;
    
    public SendNewUserJoin() {
        super((short)3121);
    }
    
    public void setBaseInfo(final PlayerInfo pInfo) {
        this.uName = pInfo.nickName;
        this.avtUrl = pInfo.avatarUrl;
    }
    
    public byte[] createData() {
        final ByteBuffer bf = this.makeBuffer();
        this.putStr(bf, this.uName);
        this.putStr(bf, this.avtUrl);
        this.putLong(bf, this.money);
        bf.put((byte)this.uChair);
        bf.put((byte)this.uStatus);
        return this.packBuffer(bf);
    }
}
