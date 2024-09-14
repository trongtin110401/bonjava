/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  bitzero.server.core.BZEventParam
 *  bitzero.server.core.IBZEvent
 *  bitzero.server.core.IBZEventParam
 *  bitzero.server.entities.User
 *  bitzero.server.exceptions.BZException
 *  bitzero.server.extensions.BaseServerEventHandler
 *  bitzero.util.ExtensionUtility
 */
package game.eventHandlers;

import bitzero.server.core.BZEventParam;
import bitzero.server.core.IBZEvent;
import bitzero.server.core.IBZEventParam;
import bitzero.server.entities.User;
import com.vinplay.dal.common.UserInfo;
import bitzero.server.exceptions.BZException;
import bitzero.server.extensions.BaseServerEventHandler;
import bitzero.util.ExtensionUtility;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.dal.dao.impl.StatMoneyInOutDaoImpl;
import com.vinplay.dal.entities.report.StatMoneyInOut;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;

import java.util.List;

public class LoginSuccessHandler
        extends BaseServerEventHandler {
    public void handleServerEvent(IBZEvent ibzevent) throws BZException {
        this.onLoginSuccess((User) ibzevent.getParameter((IBZEventParam) BZEventParam.USER));
    }

    private void onLoginSuccess(User user) {

        StatMoneyInOutDaoImpl statMoneyInOutDao = new StatMoneyInOutDaoImpl();

        // th�m v�o th�ng tin user online
        ExtensionUtility.instance().sendLoginOK(user);
        HazelcastInstance instance = HazelcastClientFactory.getInstance();

        IMap userOnline = instance.getMap("USER_ONLINE");
        UserInfo info = new UserInfo();
        info.setLastLoginTime(user.getLastLoginTime());
        if (user.getJoinedRoom() != null) {
            info.setGameName(user.getJoinedRoom().getName());
        }

        //  stats money inout
        StatMoneyInOut statMoneyInOut = statMoneyInOutDao.find(user.getName());
        if (statMoneyInOut != null) {
            info.setTotalDepositBank(statMoneyInOut.depositBank);
            info.setTotalDepositMoMo(statMoneyInOut.withdrawMomo);
            info.setTotalDepositCard(statMoneyInOut.depositCard);
            info.setTotalCashoutBank(statMoneyInOut.withdrawBank);
            info.setTotalCashoutMoMo(statMoneyInOut.withdrawMomo);
        }

        userOnline.set(user.getName(), info.toJson());
    }

}

