package game.xocdia.cmd.send;

import bitzero.server.extensions.data.BaseMsg;
import game.entities.PlayerInfo;
import game.xocdia.entities.GamePlayer;
import game.xocdia.entities.GamePot;

import java.nio.ByteBuffer;
import java.util.Map;

public class GetListUserMsg extends BaseMsg {
    public Map<String, GamePlayer> playerList;
    public GetListUserMsg( ) {
        super((short) 3213);
    }
    public String nickName;

    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();

        bf.put((byte)(this.playerList.size() - 1));

        for (Map.Entry entry : this.playerList.entrySet()) {
            if (((String)entry.getKey()).equals(nickName)) continue;
            GamePlayer gp = (GamePlayer)entry.getValue();
            PlayerInfo pInfo = gp.pInfo;
            this.putStr(bf, pInfo.nickName);
            this.putStr(bf, pInfo.avatarUrl);
            bf.putLong(gp.getMoneyUseInGame());
            this.putBoolean(bf, Boolean.valueOf(gp.isBanker));
            this.putBoolean(bf, Boolean.valueOf(gp.isSubBanker));
            this.putBoolean(bf, Boolean.valueOf(gp.reqKickRoom));
        }

        return this.packBuffer(bf);
    }
}
