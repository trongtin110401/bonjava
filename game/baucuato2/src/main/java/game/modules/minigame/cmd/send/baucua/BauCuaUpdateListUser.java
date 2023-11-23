package game.modules.minigame.cmd.send.baucua;

import game.BaseMsgEx;
import game.modules.minigame.entities.UserRoomInfo;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Map;

public class BauCuaUpdateListUser extends BaseMsgEx {
    public Map<String,UserRoomInfo> userRoomInfoList;
    public BauCuaUpdateListUser() {
        super(5011);
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        bf.putInt(userRoomInfoList.keySet().size());
        for(String key : userRoomInfoList.keySet()){

            this.putStr(bf,userRoomInfoList.get(key).getUsername());
            bf.putLong(userRoomInfoList.get(key).getCurrentMoney());
            this.putStr(bf,userRoomInfoList.get(key).getAvatar());
        }
        return  this.packBuffer(bf);
    }
}
