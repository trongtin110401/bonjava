// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server.logic;

import game.utils.GameUtils;
import game.modules.gameRoom.entities.GameRoomIdGenerator;

public class Gamble
{
    public CardSuit suit;
    public int id;
    public boolean isCheat;
    public long logTime;
    
    public Gamble() {
        this.suit = new CardSuit();
        this.id = getID();
        this.isCheat = false;
        this.logTime = System.currentTimeMillis();
    }
    
    private static int getID() {
        final int id = GameRoomIdGenerator.instance().getId();
        return id;
    }
    
    public void reset() {
        this.id = getID();
        this.logTime = System.currentTimeMillis();
        if (!this.isCheat || !GameUtils.isCheat) {
            this.suit.setRandom();
        }
    }
}
