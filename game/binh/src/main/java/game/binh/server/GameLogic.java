// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server;

import java.util.Random;
import game.binh.server.logic.CardSuit;

public class GameLogic
{
    public CardSuit cardSuit;
    public int firstTurn;
    public boolean sam;
    public Random rd;
    
    public GameLogic() {
        this.cardSuit = new CardSuit();
        this.firstTurn = -1;
        this.sam = false;
        this.rd = new Random();
    }
    
    public byte[] genFirstTurn() {
        this.cardSuit.setRandomFirstTurn();
        final byte[] cards = new byte[4];
        int i = 0;
        int count = 0;
        final int CHAT = this.rd.nextInt(4);
        while (i < 4) {
            final int cardId = this.cardSuit.ids.get(count++);
            final int so = cardId / 4;
            final int chat = cardId % 4;
            if (so != 2) {
                if (chat != CHAT) {
                    continue;
                }
                cards[i++] = (byte)cardId;
            }
        }
        return cards;
    }
}
