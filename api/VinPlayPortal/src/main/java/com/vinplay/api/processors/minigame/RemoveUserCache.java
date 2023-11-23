package com.vinplay.api.processors.minigame;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.models.UserModel;

import javax.servlet.http.HttpServletRequest;

public class RemoveUserCache
        implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = (HttpServletRequest)param.get();
            String nickname = request.getParameter("nickname");
            HazelcastInstance client = HazelcastClientFactory.getInstance();
            IMap<String, UserModel> userMap = client.getMap("users");
            userMap.lock(nickname);
            UserModel removed = userMap.remove(nickname);
            userMap.unlock(nickname);
            return  removed.toJson();
        }
        catch (Exception ex) {
            return ex.getMessage();
        }
    }
}
