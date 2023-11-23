// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server;

import bitzero.server.BitZeroServer;
import com.vinplay.vbee.common.config.VBeePath;

public class BinhMain
{
    public static void main(final String[] args) {
        VBeePath.initBasePath((Class)BinhMain.class);
        boolean clusterMode = false;
        boolean useConsole = false;
        if (args.length > 0) {
            clusterMode = args[0].equalsIgnoreCase("cluster");
            useConsole = (args.length > 1 && args[1].equalsIgnoreCase("console"));
        }
        final BitZeroServer bzServer = BitZeroServer.getInstance();
        bzServer.setClustered(clusterMode);
        if (useConsole) {
            bzServer.startDebugConsole();
        }
        bzServer.start();
    }
}
