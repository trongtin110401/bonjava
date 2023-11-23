package game.modules.minigame.cmd.send.baucua;

import game.BaseMsgEx;
import game.modules.minigame.entities.WinUser;
import java.nio.ByteBuffer;
import java.util.List;

public class UpdateBauCuaWinEffect
        extends BaseMsgEx {

    public List<WinUser> listWinUser;
    public  long funds;
    public boolean isNohu;
    public UpdateBauCuaWinEffect() {
        super(5010);
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();

        bf.putInt(listWinUser.size());

        for (WinUser w : listWinUser){
            this.putStr(bf,w.getUsername());
            bf.putLong(w.getTotalWinMoney());
            bf.putLong(w.getCurrentMoney());
        }
        bf.putLong(funds);
        this.putBoolean(bf,isNohu);
        return this.packBuffer(bf);
    }
}
