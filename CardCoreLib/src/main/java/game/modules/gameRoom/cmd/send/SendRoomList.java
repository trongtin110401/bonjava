/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  bitzero.server.extensions.data.BaseMsg
 */
package game.modules.gameRoom.cmd.send;

import bitzero.server.extensions.data.BaseMsg;
import game.modules.gameRoom.entities.GameRoom;
import game.modules.gameRoom.entities.GameRoomSetting;
import game.utils.GameUtils;
import game.xocdia.conf.XocDiaGameUtils;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class SendRoomList
extends BaseMsg {
    public List<GameRoom> roomList = new LinkedList<GameRoom>();

    public SendRoomList() {
        super((short)3014);
    }

    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        int size = this.roomList.size();
        bf.putShort((short)size);
        for (int i = 0; i < size; ++i) {
            GameRoom room = this.roomList.get(i);
            bf.putInt(room.getId());
            bf.put((byte)room.getUserCount());
            bf.put((byte)room.setting.limitPlayer);
            bf.putInt(room.setting.maxUserPerRoom);
            bf.put((byte)room.setting.moneyType);
            bf.putInt((int)room.setting.moneyBet);
            if (room.setting.requiredMoney != 0) {
                bf.putInt((int)room.setting.requiredMoney);
            } else if (GameUtils.gameName.equalsIgnoreCase("Poker")) {
                bf.putInt((int)(40 * room.setting.moneyBet));
            } else if (GameUtils.gameName.equalsIgnoreCase("Lieng")) {
                bf.putInt((int)(5 * room.setting.moneyBet));
            }
            bf.put((byte)room.setting.rule);
            this.putStr(bf, room.setting.roomName);
            if (room.setting.password.length() > 0) {
                this.putBoolean(bf, Boolean.valueOf(true));
            } else {
                this.putBoolean(bf, Boolean.valueOf(false));
            }
            bf.putLong(XocDiaGameUtils.getFundByName(room.setting.roomName, room.getId(), room.setting.rule));
        }
        return this.packBuffer(bf);
    }
}

