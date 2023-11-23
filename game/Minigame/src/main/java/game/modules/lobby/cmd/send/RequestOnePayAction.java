package game.modules.lobby.cmd.send;

import game.BaseMsgEx;
import org.python.parser.ast.Str;

import java.nio.ByteBuffer;

public class RequestOnePayAction  extends BaseMsgEx {
    public int stepCode;
    public long currentMoney;
    public String techcombankTrans;
    public RequestOnePayAction() {
        super(20290);
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        bf.putInt(this.stepCode);
        bf.putLong(this.currentMoney);
        this.putStr(bf,techcombankTrans);
        return this.packBuffer(bf);
    }
}
