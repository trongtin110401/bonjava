// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server.cmd.send;

import java.nio.ByteBuffer;
import game.modules.gameRoom.entities.GameMoneyInfo;
import game.entities.PlayerInfo;
import bitzero.server.extensions.data.BaseMsg;

public class SendJoinRoomSuccess extends BaseMsg
{
    public int uChair;
    public long moneyBet;
    public int gameId;
    public int roomId;
    public int moneyType;
    public int rule;
    public byte[] playerStatus;
    public PlayerInfo[] playerList;
    public GameMoneyInfo[] moneyInfoList;
    public byte gameState;
    public byte gameAction;
    public byte countDownTime;
    
    public SendJoinRoomSuccess() {
        super((short)3118);
        this.playerStatus = new byte[4];
        this.playerList = new PlayerInfo[4];
        this.moneyInfoList = new GameMoneyInfo[4];
    }
    
    public SendJoinRoomSuccess(final int i) {
        super((short)3118, i);
        this.playerStatus = new byte[4];
        this.playerList = new PlayerInfo[4];
        this.moneyInfoList = new GameMoneyInfo[4];
    }
    
    public byte[] createData() {
        final ByteBuffer bf = this.makeBuffer();
        bf.put((byte)this.uChair);
        bf.putLong(this.moneyBet);
        bf.putInt(this.roomId);
        bf.putInt(this.gameId);
        bf.put((byte)this.moneyType);
        bf.put((byte)this.rule);
        this.putByteArray(bf, this.playerStatus);
        bf.putShort((short)4);
        for (int i = 0; i < 4; ++i) {
            final PlayerInfo pInfo = this.playerList[i];
            final GameMoneyInfo mInfo = this.moneyInfoList[i];
            if (pInfo != null && mInfo != null) {
                this.putStr(bf, pInfo.nickName);
                this.putStr(bf, pInfo.avatarUrl);
                bf.putLong(mInfo.currentMoney);
            }
            else {
                this.putStr(bf, "");
                this.putStr(bf, "");
                bf.putLong(0L);
            }
        }
        bf.put(this.gameState);
        bf.put(this.gameAction);
        bf.put(this.countDownTime);
        return this.packBuffer(bf);
    }
}
