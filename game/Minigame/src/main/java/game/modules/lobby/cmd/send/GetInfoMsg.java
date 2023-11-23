/*
 * Decompiled with CFR 0.144.
 */
package game.modules.lobby.cmd.send;

import game.BaseMsgEx;
import java.nio.ByteBuffer;

public class GetInfoMsg
extends BaseMsgEx {
    public String username;
    public String cmt;
    public String email;
    public String mobile;
    public byte mobileSecure;
    public byte emailSecure;
    public byte appSecure;
    public byte loginSecure;
    public long moneyLoginotp;
    public long moneyUse;
    public long safe;
    public String configGame;

    public GetInfoMsg() {
        super(20050);
    }

    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        this.putStr(bf, this.username);
        this.putStr(bf, this.cmt);
        this.putStr(bf, this.email);
        this.putStr(bf, this.mobile);
        bf.put(this.mobileSecure);
        bf.put(this.emailSecure);
        bf.put(this.appSecure);
        bf.put(this.loginSecure);
        bf.putLong(this.moneyLoginotp);
        bf.putLong(this.moneyUse);
        bf.putLong(this.safe);
        this.putStr(bf, this.configGame);
        return this.packBuffer(bf);
    }
}

