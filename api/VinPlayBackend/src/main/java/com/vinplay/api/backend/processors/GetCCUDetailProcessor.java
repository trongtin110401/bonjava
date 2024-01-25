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
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.response.UserOnlineDetailResponse;
import com.vinplay.vbee.common.response.UserOnlineResponse;
import com.vinplay.vbee.common.utils.VinPlayUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class GetCCUDetailProcessor
        implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        UserOnlineDetailResponse response = new UserOnlineDetailResponse(false, "1001");
        HazelcastInstance instance = HazelcastClientFactory.getInstance();
        HttpServletRequest request = param.get();
        String nickName = request.getParameter("nickName");
        IMap userMap = instance.getMap("users");
        UserCacheModel user = (UserCacheModel) userMap.get(nickName);
        response.setUser(user);
        return response.toJson();
    }
}

