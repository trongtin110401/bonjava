package com.vinplay.api.backend.processors.baucuato2;

import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.exceptions.KeyNotFoundException;
import com.vinplay.vbee.common.response.BauCuaTo2.BauCuaListUserResponse;
import com.vinplay.vbee.common.response.BauCuaTo2.BauCuaUserInfomation;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class GetListUserInRoom implements BaseProcessor<HttpServletRequest, String> {
    @Override
    public String execute(Param<HttpServletRequest> var1) {
        CacheService cacheService = new CacheServiceImpl();
        BauCuaListUserResponse response = new BauCuaListUserResponse(true, "0");
        try {
            ArrayList<BauCuaUserInfomation> list = (ArrayList<BauCuaUserInfomation>) cacheService.getObject("baucualist");

            response.setListBauCuaInformation(list);
            return response.toJson();

        } catch (KeyNotFoundException e) {
            response.setListBauCuaInformation(new ArrayList<>());
        }
        return response.toJson();
    }
}
