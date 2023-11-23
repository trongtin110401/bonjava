/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  bitzero.server.core.BZEventParam
 *  bitzero.server.core.BZEventType
 *  bitzero.server.core.IBZEvent
 *  bitzero.server.core.IBZEventListener
 *  bitzero.server.core.IBZEventParam
 *  bitzero.server.core.IBZEventType
 *  bitzero.server.entities.User
 *  bitzero.server.exceptions.BZException
 *  bitzero.server.extensions.BZExtension
 *  bitzero.server.extensions.BaseClientRequestHandler
 *  bitzero.server.extensions.data.BaseMsg
 *  bitzero.server.extensions.data.DataCmd
 *  bitzero.util.ExtensionUtility
 */
package game.modules.cluster;

import bitzero.server.core.BZEventParam;
import bitzero.server.core.BZEventType;
import bitzero.server.core.IBZEvent;
import bitzero.server.core.IBZEventListener;
import bitzero.server.core.IBZEventParam;
import bitzero.server.core.IBZEventType;
import bitzero.server.entities.User;
import bitzero.server.exceptions.BZException;
import bitzero.server.extensions.BZExtension;
import bitzero.server.extensions.BaseClientRequestHandler;
import bitzero.server.extensions.data.BaseMsg;
import bitzero.server.extensions.data.DataCmd;
import bitzero.util.ExtensionUtility;
import game.modules.cluster.cmd.RevSample;
import game.modules.cluster.cmd.SendSample;

public class SampleModule
extends BaseClientRequestHandler {
    public void init() {
        this.getParentExtension().addEventListener((IBZEventType)BZEventType.USER_LOGIN, (IBZEventListener)this);
    }

    public void handleServerEvent(IBZEvent ibzevent) throws BZException {
        if (ibzevent.getType() == BZEventType.USER_LOGIN) {
            this.userLogin((User)ibzevent.getParameter((IBZEventParam)BZEventParam.USER));
        }
    }

    public void handleClientRequest(User user, DataCmd dataCmd) {
        switch (dataCmd.getId()) {
            case 1001: {
                this.sampleProcess(user, dataCmd);
            }
        }
    }

    private void sampleProcess(User user, DataCmd dataCmd) {
        RevSample cmd = new RevSample(dataCmd);
        SendSample msg = new SendSample();
        msg.copy(cmd);
        this.send((BaseMsg)msg, user);
    }

    private void userLogin(User user) {
        ExtensionUtility.instance().sendLoginOK(user);
    }
}

