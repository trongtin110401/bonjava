
package game.modules.minigame.cmd.send.baucua;

import game.BaseMsgEx;
import java.nio.ByteBuffer;
import java.util.List;

public class UpdateBauCuaPerSecondMsg extends BaseMsgEx {
    public String potData;
    public byte remainTime;
    public boolean bettingState;
    public List<BauCuaRealtimeTransaction> listBet;

    public UpdateBauCuaPerSecondMsg() {
        super(5006);
    }

    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        this.putStr(bf, this.potData);
        bf.put(this.remainTime);
        this.putBoolean(bf, this.bettingState);
        bf.putInt(listBet.size());
        for(BauCuaRealtimeTransaction b : listBet){
            this.putStr(bf,b.getUsername());
            this.putStr(bf,b.getBetStr());
        }
        return this.packBuffer(bf);
    }
}

