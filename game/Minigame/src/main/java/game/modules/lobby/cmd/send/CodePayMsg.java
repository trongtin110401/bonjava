package game.modules.lobby.cmd.send;

import game.BaseMsgEx;
import game.modules.minigame.cmd.MiniGameCMD;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class CodePayMsg extends BaseMsgEx {
    public String comment;

    public CodePayMsg() {
        super(MiniGameCMD.CMD_DEPOSIT_CODEPAY_MANUAL);
    }
    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        this.putStr(bf,comment);
        return this.packBuffer(bf);
    }
}
