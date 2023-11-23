/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  org.json.JSONException
 */
package com.vinplay.usercore.service.impl;

import com.vinplay.gamebai.entities.BossXocDiaModel;
import com.vinplay.gamebai.entities.XocDiaBoss;
import com.vinplay.usercore.dao.impl.XocDiaDaoImpl;
import com.vinplay.usercore.service.XocDiaService;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.json.JSONException;

public class XocDiaServiceImpl
implements XocDiaService {
    @Override
    public boolean saveRoomBoss(XocDiaBoss boss) throws SQLException {
        XocDiaDaoImpl dao = new XocDiaDaoImpl();
        return dao.saveRoomBoss(boss);
    }

    @Override
    public boolean updateRoomBoss(String sessionId, String nickname, long fundInitial, int fee, long revenue, int type) throws SQLException {
        XocDiaDaoImpl dao = new XocDiaDaoImpl();
        return dao.updateRoomBoss(sessionId, nickname, fundInitial, fee, revenue, type);
    }

    @Override
    public XocDiaBoss getRoomBoss(String nickname, int roomId) throws SQLException {
        XocDiaDaoImpl dao = new XocDiaDaoImpl();
        return dao.getRoomBoss(nickname, roomId);
    }

    @Override
    public Map<Integer, XocDiaBoss> getListRoomBossActive() throws SQLException {
        XocDiaDaoImpl dao = new XocDiaDaoImpl();
        return dao.getListRoomBossActive();
    }

    @Override
    public List<String> getListBossActive() throws SQLException {
        XocDiaDaoImpl dao = new XocDiaDaoImpl();
        return dao.getListBossActive();
    }

    @Override
    public List<String> getListSessionActive() throws SQLException {
        XocDiaDaoImpl dao = new XocDiaDaoImpl();
        return dao.getListSessionActive();
    }

    @Override
    public List<BossXocDiaModel> getListRoomBoss(String nickname, int roomId, int status, int moneyBet) throws SQLException, JSONException {
        XocDiaDaoImpl dao = new XocDiaDaoImpl();
        return dao.getListRoomBoss(nickname, roomId, status, moneyBet);
    }
}

