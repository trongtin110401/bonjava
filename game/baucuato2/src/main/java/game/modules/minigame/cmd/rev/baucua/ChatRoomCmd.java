/*
 * Decompiled with CFR 0_116.
 *
 * Could not load the following classes:
 *  bitzero.server.extensions.data.BaseCmd
 *  bitzero.server.extensions.data.DataCmd
 */
package game.modules.minigame.cmd.rev.baucua;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class ChatRoomCmd
        extends BaseCmd {
    public byte roomId;
    public boolean isIcon;
    public String content;

    public ChatRoomCmd(DataCmd dataCmd) {
        super(dataCmd);
        this.unpackData();
    }

    public void unpackData() {
        ByteBuffer bf = this.makeBuffer();
        this.roomId = this.readByte(bf);
        this.isIcon = this.readBoolean(bf);
        this.content = this.readString(bf);
    }
}

