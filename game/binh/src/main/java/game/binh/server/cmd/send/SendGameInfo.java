// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server.cmd.send;

import game.entities.PlayerInfo;
import java.nio.ByteBuffer;
import game.binh.server.GamePlayer;
import bitzero.server.extensions.data.BaseMsg;

public class SendGameInfo extends BaseMsg
{
    public int moneyType;
    public int chair;
    public int gameState;
    public int gameAction;
    public int countdownTime;
    public long moneyBet;
    public int gameId;
    public int roomId;
    public int rule;
    public boolean[] hasInfoAtChair;
    public GamePlayer[] pInfos;
    
    public SendGameInfo() {
        super((short)3110);
        this.hasInfoAtChair = new boolean[4];
        this.pInfos = new GamePlayer[4];
    }
    
    public byte[] createData() {
        final ByteBuffer bf = this.makeBuffer();
        bf.put((byte)this.chair);
        bf.put((byte)this.gameState);
        bf.put((byte)this.gameAction);
        bf.put((byte)this.countdownTime);
        this.putLong(bf, this.moneyBet);
        bf.put((byte)this.moneyType);
        bf.putInt(this.gameId);
        bf.putInt(this.roomId);
        bf.put((byte)this.rule);
        this.putBooleanArray(bf, this.hasInfoAtChair);
        for (int i = 0; i < 4; ++i) {
            if (this.hasInfoAtChair[i]) {
                final GamePlayer gp = this.pInfos[i];
                if (this.gameState == 1) {
                    if (i == this.chair) {
                        if (gp.spInfo.handCards != null) {
                            this.putByteArray(bf, gp.spInfo.handCards.toByteArray());
                        }
                        else {
                            this.putByteArray(bf, new byte[13]);
                        }
                    }
                }
                else if (this.gameState == 2) {
                    this.putByteArray(bf, gp.spInfo.handCards.toByteArray());
                    bf.put((byte)gp.kiemTraMauBinh(this.rule));
                    this.putLong(bf, gp.spRes.getResultWithPlayer(gp.chair).moneyCommon);
                }
                this.putBoolean(bf, Boolean.valueOf(gp.sochi));
                bf.put((byte)gp.getPlayerStatus());
                final PlayerInfo pInfo = gp.pInfo;
                this.putStr(bf, pInfo.avatarUrl);
                bf.putInt(pInfo.userId);
                this.putStr(bf, pInfo.nickName);
                this.putLong(bf, gp.gameMoneyInfo.currentMoney);
            }
        }
        return this.packBuffer(bf);
    }
}
