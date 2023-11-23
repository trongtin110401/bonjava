 package game.modules.minigame.cmd.rev.slot3x3;
 import bitzero.server.extensions.data.BaseCmd;
 import bitzero.server.extensions.data.DataCmd;
 import java.nio.ByteBuffer;


 public class UnSubscribeSlotExtendCmd
   extends BaseCmd
 {
   public byte roomId;

   public UnSubscribeSlotExtendCmd(DataCmd dataCmd)
   {
     super(dataCmd);
     unpackData();
   }

   public void unpackData() {
     ByteBuffer bf = makeBuffer();
     this.roomId = bf.get();
   }
 }
