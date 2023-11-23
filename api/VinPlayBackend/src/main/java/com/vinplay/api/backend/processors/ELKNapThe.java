package com.vinplay.api.backend.processors;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;

import javax.servlet.http.HttpServletRequest;

public class ELKNapThe implements BaseProcessor<HttpServletRequest, String> {
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest)param.get();
        String congnap = request.getParameter("congnap");
        String type = request.getParameter("type");
        String key = "CONFIGURE_CONGTHE_BECANG";
        HazelcastInstance instance = HazelcastClientFactory.getInstance();
        IMap map = instance.getMap("cacheConfig");

        if(type.equalsIgnoreCase("update")) {
            if (map.containsKey((Object) key)) {
                map.replace(key,congnap);
                return "SUCCESS";
            }else {
                map.put(key,congnap);
                return "SUCCESS_NEW";
            }
        }else {
            if (map.containsKey((Object) key)) {
                return (String) map.get((Object) key);
            }else {
                map.put(key,"1");
                return "1";
            }
        }
    }
}
