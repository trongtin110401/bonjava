/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.dal.service.impl.ServerInfoServiceImpl
 *  com.vinplay.vbee.common.cp.BaseProcessor
 *  com.vinplay.vbee.common.cp.Param
 *  com.vinplay.vbee.common.response.CCUResponse
 *  javax.servlet.http.HttpServletRequest
 */
package com.vinplay.api.backend.processors;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.response.UserOnlineResponse;
import bitzero.server.entities.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class GetUserOnlineProcessor
        implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        UserOnlineResponse response = new UserOnlineResponse(true, "200");
        HazelcastInstance instance = HazelcastClientFactory.getInstance();
        IMap userOnline = instance.getMap("USER_ONLINE");

        response.setUsers((List<String>) userOnline.values());
//
//        for (Object key : userOnline.keySet()){
//            User user = new User();
//            HazelcastInstance client = HazelcastClientFactory.getInstance();
//            IMap<String, UserModel> userMap = client.getMap("users");
//            UserCacheModel userCacheModelr = (UserCacheModel)   userMap.get(message.username);
//        }
//        userMap.get(user.getName());
        return response.toJson();
    }
}
