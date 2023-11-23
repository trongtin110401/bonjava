package game.modules.minigame.cmd.rev.slot3x3;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;
import java.nio.ByteBuffer;


public class AutoPlaySlotExtendCmd
        extends BaseCmd {
  public byte autoPlay;
  public long gold;

  public AutoPlaySlotExtendCmd(DataCmd dataCmd) {
    super(dataCmd);
    unpackData();
  }

  public void unpackData() {
    ByteBuffer bf = makeBuffer();
    this.autoPlay = bf.get();
    this.gold = readLong(bf);
  }
}
