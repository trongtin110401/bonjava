/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  bitzero.server.extensions.data.BaseCmd
 *  bitzero.server.extensions.data.DataCmd
 */
package game.modules.slot.cmd.rev.slot20line;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class Slot20MinimizeCmd extends BaseCmd {
    public byte roomId;

    public Slot20MinimizeCmd(DataCmd dataCmd) {
        super(dataCmd);
        this.unpackData();
    }

    public void unpackData() {
        ByteBuffer bf = this.makeBuffer();
        this.roomId = this.readByte(bf);
    }
}

