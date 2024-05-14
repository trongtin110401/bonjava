package game.modules.lobby.cmd.send;


import game.BaseMsgEx;
import game.modules.minigame.cmd.MiniGameCMD;

import java.nio.ByteBuffer;

public class LoginOtherDeviceMsg extends BaseMsgEx {

    public String s = "1";

    public LoginOtherDeviceMsg() {
        super(MiniGameCMD.LOGIN_OTHER_DEVICE);
    }

    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        this.putStr(bf, s);
        return this.packBuffer(bf);
    }

}
