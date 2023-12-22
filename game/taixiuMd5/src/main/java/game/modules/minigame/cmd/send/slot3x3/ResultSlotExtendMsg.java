package game.modules.minigame.cmd.send.slot3x3;

import bitzero.server.extensions.data.BaseMsg;
import game.modules.minigame.LineWin;
import game.modules.minigame.cmd.MiniGameCMD;

import java.nio.ByteBuffer;
import java.util.List;


public class ResultSlotExtendMsg
        extends BaseMsg {
    public long prize;
    public int winType;
    public int result;
    public int mutil;
    public int mutil1;
    public int mutil2;
    public int spinId;
    public int[] showItem;
    public List<LineWin> listLineWin;
    public long currMoney;

    public ResultSlotExtendMsg() {
        super(MiniGameCMD.CMD_SLOT_EXTEND_SPIN);
    }

    public byte[] createData() {
        ByteBuffer bf = makeBuffer();

        bf.putInt(result);
        bf.putLong(prize);
        bf.putInt(winType);
        bf.putInt(mutil);
        bf.putInt(mutil1);
        bf.putInt(mutil2);
        bf.putInt(spinId);
        int size1 = showItem.length;
        bf.putInt(size1);
        for (int i = 0; i < size1; i++) {
            bf.putInt(showItem[i]);
        }

        int size = listLineWin.size();
        bf.putInt(size);
        for (int i = 0; i < size; i++) {
            bf.putInt(listLineWin.get(i).getLine());
            bf.putDouble(listLineWin.get(i).getPrizeAmount());
            if (listLineWin.get(i).isJackpot())
                bf.putInt(1);
            else
                bf.putInt(0);
        }
        putStr(bf, "");
        putStr(bf, "");
        putStr(bf, "");
        bf.putLong(currMoney);
        return packBuffer(bf);
    }
}

