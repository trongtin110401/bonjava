package game.modules.minigame.cmd.send.baucua;

import game.BaseMsgEx;
import game.modules.minigame.entities.HuBauCuaWinTransaction;
import game.modules.minigame.entities.UserWinHuBauCua;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LichSuNoHuMsg extends BaseMsgEx {
   public Map<Integer,Integer> mapRate;
    public List<HuBauCuaWinTransaction> list50WinHu;

    public LichSuNoHuMsg() {
        super(5019);

    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        bf.putInt(mapRate.keySet().size());
        for (Integer key : mapRate.keySet()) {
            bf.putInt(mapRate.get(key));
        }
        bf.putInt(list50WinHu.size());

        for (HuBauCuaWinTransaction huBauCuaWinTransaction : list50WinHu) {
            bf.putLong(huBauCuaWinTransaction.getSession());
            this.putStr(bf, huBauCuaWinTransaction.getTime());
            bf.putInt(huBauCuaWinTransaction.getPotId());
            bf.putLong(huBauCuaWinTransaction.getTotalMoney());

            bf.putInt(huBauCuaWinTransaction.getUserWinHuBauCuaList().size());
            for (UserWinHuBauCua userWinHuBauCua : huBauCuaWinTransaction.getUserWinHuBauCuaList()) {

                this.putStr(bf, userWinHuBauCua.getUserName());
                bf.putLong(userWinHuBauCua.getMoneyWin());

            }
        }
        return this.packBuffer(bf);
    }
}
