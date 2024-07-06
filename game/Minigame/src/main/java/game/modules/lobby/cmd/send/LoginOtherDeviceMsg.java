package game.modules.lobby.cmd.send;


import game.BaseMsgEx;
import game.modules.minigame.cmd.MiniGameCMD;

import java.nio.ByteBuffer;

public class LoginOtherDeviceMsg extends BaseMsgEx {

    public static final int TYPE_KICK = 1;
    public static final int TYPE_LOGIN_OTHER_DEVICE = 2;

    public String s = "1";
    public short type = 1;

    public LoginOtherDeviceMsg() {
        super(MiniGameCMD.LOGIN_OTHER_DEVICE);
    }

    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        this.putStr(bf, s);
        return this.packBuffer(bf);
    }

}
