/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  bitzero.engine.sessions.ISession
 *  bitzero.server.entities.User
 *  bitzero.util.socialcontroller.bean.UserInfo
 */
package game.modules.cluster.util;

import bitzero.engine.sessions.ISession;
import bitzero.server.entities.User;
import bitzero.util.socialcontroller.bean.UserInfo;
import game.modules.cluster.cmd.SampleLogin;

public class ClusterUtil {
    public static UserInfo getUserInfo(ISession iSession, String string) {
        return null;
    }

    public static String genSessionKey() {
        return "index";
    }

    public static UserInfo getSampleInfo(SampleLogin cmd) {
        UserInfo clusterInfo = new UserInfo();
        clusterInfo.setUserId(String.valueOf(cmd.userId));
        String userName = String.format("%s:%d|%d", cmd.ip, cmd.port, cmd.userId);
        String displayName = String.format("%s:%d", cmd.ip, cmd.port);
        clusterInfo.setUsername(userName);
        clusterInfo.setDisplayname(displayName);
        return clusterInfo;
    }

    public static String getSocketName(User user) {
        String keyUserInfo = "user_info";
        UserInfo info = (UserInfo)user.getProperty((Object)"user_info");
        if (info != null) {
            return info.getDisplayname();
        }
        return user.getName().split("|")[0];
    }
}

