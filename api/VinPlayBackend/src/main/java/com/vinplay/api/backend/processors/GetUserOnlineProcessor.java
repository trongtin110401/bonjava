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

import com.google.gson.Gson;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.response.UserCCUResponse;
import com.vinplay.vbee.common.response.UserOnlineResponse;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class GetUserOnlineProcessor
        implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        UserOnlineResponse response = new UserOnlineResponse(true, "200");
        HazelcastInstance instance = HazelcastClientFactory.getInstance();
        IMap userOnline = instance.getMap("USER_ONLINE");


        List<UserCCUResponse> userOnlineResponse = new ArrayList<>();

        for (Object key : userOnline.keySet()) {
            UserCCUResponse userCCUResponse =  fromJsonString(userOnline.get(key).toString());
            userCCUResponse.setNickName(key.toString());
            userOnlineResponse.add(userCCUResponse);
        }
        response.setUsers(userOnlineResponse);
        return response.toJson();
    }

    public static UserCCUResponse fromJsonString(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, UserCCUResponse.class);
    }
}
