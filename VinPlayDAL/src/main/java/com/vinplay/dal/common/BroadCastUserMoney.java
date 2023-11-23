package com.vinplay.dal.common;

import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.CacheServiceImpl;

import java.util.ArrayList;

public class BroadCastUserMoney {

    public static void pushBroadCast(String username) {
        CacheService cacheService = new CacheServiceImpl();
        ArrayList<String> listUser = new ArrayList<>();
        try {
            listUser = (ArrayList<String>) cacheService.getObject("List_Money_Change");
        } catch (Exception e) {
            listUser = new ArrayList<>();
        }
        listUser.add(username);
        cacheService.setObject("List_Money_Change", listUser);
    }

    public static void pushBroadTime(String username) {
        CacheService cacheService = new CacheServiceImpl();
        ArrayList<String> listUser = new ArrayList<>();
        try {
            listUser = (ArrayList<String>) cacheService.getObject("List_Time_Change");
        } catch (Exception e) {
            listUser = new ArrayList<>();
        }
        listUser.add(username);
        cacheService.setObject("List_Time_Change", listUser);
    }

    public static void pushBroadTime2(String username) {
        CacheService cacheService = new CacheServiceImpl();
        ArrayList<String> listUser = new ArrayList<>();
        try {
            listUser = (ArrayList<String>) cacheService.getObject("List_Time_Change");
        } catch (Exception e) {
            listUser = new ArrayList<>();
        }
        listUser.add(username);
        cacheService.setObject("List_Time_Change", listUser);
    }
    public static void pushBroadOutGame(String username,String idGame) {
        CacheService cacheService = new CacheServiceImpl();
        ArrayList<String> listUser = new ArrayList<>();
        try {
            listUser = (ArrayList<String>) cacheService.getObject("List_Time_OutGame");
        } catch (Exception e) {
            listUser = new ArrayList<>();
        }
        listUser.add(username+"|"+idGame);
        cacheService.setObject("List_Time_OutGame", listUser);
    }
}
