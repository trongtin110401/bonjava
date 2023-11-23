package com.vinplay.dal.common;

import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.CacheServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;

public class BroadCastUserState {
    public static void pushBroadCast(String user,String userState) {
        CacheService cacheService = new CacheServiceImpl();
        HashMap<String,String> listUser;
        try {
            listUser = (HashMap<String, String>) cacheService.getObject("List_UserState_Slot");
        } catch (Exception e) {
            listUser = new HashMap<>();
        }

        listUser.put(user,userState);
        cacheService.setObject("List_UserState_Slot", listUser);
    }
    public static void popBroadCast(String user) {
        CacheService cacheService = new CacheServiceImpl();
        HashMap<String,String> listUser;
        try {
            listUser = (HashMap<String, String>) cacheService.getObject("List_UserState_Slot");
        } catch (Exception e) {
            listUser = new HashMap<>();
        }
        listUser.remove(user,listUser.get(user));
        cacheService.setObject("List_UserState_Slot", listUser);
    }
}
