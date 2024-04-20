/*
 * Decompiled with CFR 0.144.
 */
package game.modules.minigame.cmd.send.baucua;

import game.BaseMsgEx;
import game.modules.minigame.entities.UserRoomInfo;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BauCuaInfoMsg extends BaseMsgEx {
    public long referenceId;
    public byte remainTime;
    public boolean bettingState;
    public String potData;
    public String betData;
    public String lichSuPhien;
    public byte dice1;
    public byte dice2;
    public byte dice3;
    public byte xPot;
    public byte xValue;
    public byte room;
    public Map<String,UserRoomInfo> userRoomInfoList;
    public long funds;
    public boolean isNohu;
    public List<BauCuaRealtimeTransaction> allTransaction;
    public byte totalTime = 0;
    public byte betTimeRemain = 0;

    public BauCuaInfoMsg() {
        super(5005);
    }

    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        bf.putLong(this.referenceId);
        bf.put(this.remainTime);
        this.putBoolean(bf, Boolean.valueOf(this.bettingState));
        this.putStr(bf, this.potData);
        this.putStr(bf, this.betData);
        this.putStr(bf, this.lichSuPhien);
        bf.put(this.dice1);
        bf.put(this.dice2);
        bf.put(this.dice3);
        bf.put(this.xPot);
        bf.put(this.xValue);
        bf.put(this.room);

        bf.putInt(userRoomInfoList.keySet().size());
        for(String key : userRoomInfoList.keySet()){
            this.putStr(bf,userRoomInfoList.get(key).getUsername());
            bf.putLong(userRoomInfoList.get(key).getCurrentMoney());
            this.putStr(bf,userRoomInfoList.get(key).getAvatar());
        }
        bf.putLong(funds);
        this.putBoolean(bf,isNohu);
        bf.putInt(allTransaction.size());
        for(BauCuaRealtimeTransaction bauCuaRealtimeTransaction : allTransaction){
            this.putStr(bf,bauCuaRealtimeTransaction.getUsername());
            this.putStr(bf,bauCuaRealtimeTransaction.getBetStr());
        }

        bf.put(totalTime);
        bf.put(betTimeRemain);

        return this.packBuffer(bf);
    }
}

