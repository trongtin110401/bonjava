/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  bitzero.server.core.IBZEventParam
 */
package game.eventHandlers;

import bitzero.server.core.IBZEventParam;

public enum GameEventParam implements IBZEventParam
{
    USER("USER", 0),
    GAMEROOM("GAMEROOM", 1),
    IS_RECONNECT("IS_RECONNECT", 2),
    USER_SCORE("USER_SCORE", 3);
    

    private GameEventParam(String s, int n2) {
    }
}

