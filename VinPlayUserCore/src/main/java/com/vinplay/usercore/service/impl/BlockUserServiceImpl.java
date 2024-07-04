/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  com.vinplay.vbee.common.models.UserModel
 */
package com.vinplay.usercore.service.impl;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.vinplay.usercore.service.BlockUserService;
import com.vinplay.usercore.service.impl.SecurityServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.models.UserModel;
import java.sql.SQLException;

public class BlockUserServiceImpl
implements BlockUserService {
    @Override
    public String listBlockUser(String nickName, String action, String type) throws SQLException {
        String lstError = "";
        UserServiceImpl user = new UserServiceImpl();
        SecurityServiceImpl service = new SecurityServiceImpl();
        String[] keys = nickName.split(",");
        String[] vals = action.split(",");
        for (int i = 0; i < keys.length; ++i) {
            boolean flag = true;
            UserModel usermodel = user.getUserByNickName(keys[i]);
            if (usermodel == null) {
                lstError = lstError + keys[i] + ",";
                flag = false;
            }
            if (!flag) continue;
            keys[i] = usermodel.getNickname();
            for (int j = 0; j < vals.length; ++j) {
                service.updateStatusUser(keys[i], Integer.parseInt(vals[j]), type);
                kickSession(usermodel);
            }
        }
        return lstError;
    }

    private static void kickSession(UserModel userModel) {
        String nickname = userModel.getNickname();
        HazelcastInstance instance = HazelcastClientFactory.getInstance();

        IMap<String, String> map = instance.getMap("LOGIN_OTHER_DEVICE_MAP");
        map.put(nickname, nickname);

        IQueue queue = instance.getQueue("LOGIN_OTHER_DEVICE_QUEUE");
        if (queue != null) {
            queue.offer(nickname);
        }

        while (map.containsKey(nickname)) {
            try {
                Thread.sleep(50);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

