// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server.logic;

import bitzero.util.common.business.CommonHandle;
import org.json.JSONArray;
import org.json.JSONObject;

public class LevelConfig
{
    public static JSONObject addLevelScoreJson;
    public static int maxLevel;
    public static JSONObject leveScoreConfig;
    public static JSONArray levelBonus;
    
    public static int getLevelScore(final int channel, final int winType) {
        try {
            final String channelId = String.valueOf(channel);
            final JSONArray arr = LevelConfig.addLevelScoreJson.getJSONArray(channelId);
            return arr.getInt(winType);
        }
        catch (Exception e) {
            CommonHandle.writeErrLog((Throwable)e);
            return 0;
        }
    }
}
