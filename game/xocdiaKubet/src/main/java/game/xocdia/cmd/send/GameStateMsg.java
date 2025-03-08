/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  bitzero.server.extensions.data.BaseMsg
 */
package game.xocdia.cmd.send;

import bitzero.server.extensions.data.BaseMsg;

import java.nio.ByteBuffer;

public class GameStateMsg extends BaseMsg {

    public byte gameState;
    public byte time;
    public long sessionId;

//    public String kubetLiveUrl = "http://139.180.155.96/xocdia.html";
    public String kubetLiveUrl = System.getenv("XOC_DIA_KUBET_STREAM_URL");

    public GameStateMsg() {
        super((short) 3155);
    }

    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        bf.put(this.gameState);
        bf.put(this.time);
        bf.putLong(sessionId);
        this.putStr(bf, this.kubetLiveUrl);
        return this.packBuffer(bf);
    }
}

