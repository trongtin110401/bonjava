package com.vinplay.dichvuthe.service;

import bitzero.server.entities.User;
import com.vinplay.dichvuthe.dao.RechargeDao;
import com.vinplay.dichvuthe.dao.impl.RechargeDaoImpl;
import com.vinplay.dichvuthe.entities.DepositMomoModel;
import com.vinplay.dichvuthe.response.MomoResponse;

import java.util.ArrayList;

public interface RechargeMomoService {
    MomoResponse rechargeByMomoManual(User nickName, long amount, String sendFromNumber);
      ArrayList<DepositMomoModel> GetListDepositPendingMomo();
    boolean  UpdateDepositMomoManualStatus(String transId, int status, String des, String userApprove);
    // find transaction in db
    DepositMomoModel  FindDepositMomoById(String transId);
}
