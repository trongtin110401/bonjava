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
import com.vinplay.dal.service.impl.ServerInfoServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.response.CCUResponse;
import com.vinplay.vbee.common.response.UserOnlineResponse;
import com.vinplay.vbee.common.utils.VinPlayUtils;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public class GetCCUProcessor
        implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        UserOnlineResponse response = new UserOnlineResponse(false, "1001");
        HazelcastInstance instance = HazelcastClientFactory.getInstance();
        IMap userMap = instance.getMap("users");
        List<UserCacheModel> userResponse = new ArrayList<>();
        List<UserCacheModel> users = (List<UserCacheModel>) userMap.values();
        for (UserCacheModel user : users) {
            if (user.getAccessToken() == null || VinPlayUtils.sessionTimeout((long) user.getLastActive().getTime()) && !user.isBot()) {
                userResponse.add(user);
            }
        }
        response.setTransactions(userResponse);
        return response.toJson();
    }
}

