// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server;

import java.util.Iterator;
import game.binh.server.logic.Card;
import game.binh.server.logic.PlayerCard;
import game.binh.server.logic.GroupCard;

public class sPlayerInfo
{
    public GroupCard handCards;
    public PlayerCard sorttedCard;
    
    public sPlayerInfo() {
        this.sorttedCard = new PlayerCard();
    }
    
    public void clearInfo() {
        this.handCards = null;
        this.sorttedCard = new PlayerCard();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("sorttedCard: ").append(this.sorttedCard.toString()).append("\n");
        return sb.toString();
    }
    
    public int getKind(final int rule) {
        return this.sorttedCard.GetPlayerCardsKind(rule);
    }
    
    public boolean checkSubCard(final GroupCard chi, final GroupCard handscard, final GroupCard newHandCard) {
        int count = 0;
        for (final Card c : chi.cards) {
            for (final Card c2 : this.handCards.cards) {
                if (c.ID != c2.ID) {
                    continue;
                }
                newHandCard.AddCard(c2);
                ++count;
                break;
            }
        }
        return count == chi.GetNumOfCards();
    }
    
    public boolean checkCardValid(final GroupCard chi1, final GroupCard chi2, final GroupCard chi3) {
        if (chi1.cards.size() != 5 && chi2.cards.size() != 5 && chi3.cards.size() != 3) {
            return false;
        }
        final GroupCard newHandCard = new GroupCard();
        for (final Card c : chi1.cards) {
            newHandCard.AddCard(c);
        }
        for (final Card c : chi2.cards) {
            newHandCard.AddCard(c);
        }
        for (final Card c : chi3.cards) {
            newHandCard.AddCard(c);
        }
        int count = 0;
        for (int i = 0; i < newHandCard.cards.size(); ++i) {
            final Card c2 = newHandCard.cards.get(i);
            for (int j = 0; j < this.handCards.cards.size(); ++j) {
                final Card c3 = this.handCards.cards.get(j);
                if (c2.ID == c3.ID) {
                    ++count;
                    break;
                }
            }
        }
        final boolean bl;
        final boolean result = bl = (count == this.handCards.cards.size());
        if (result) {
            this.handCards = newHandCard;
            return true;
        }
        return false;
    }
    
    public int autoSort(final int rule) {
        if (rule == 0) {
            this.sorttedCard.autoSort1();
        }
        if (rule == 1) {
            this.sorttedCard.autoSort2();
        }
        return this.getKind(rule);
    }
    
    public boolean hasTuQuyAt(final int rule) {
        if (this.sorttedCard.GetPlayerCardsKind(rule) == 6 || this.sorttedCard.GetPlayerCardsKind(rule) == 7) {
            for (int i = 1; i <= 2; ++i) {
                final GroupCard chi = this.sorttedCard.getChi(i);
                if (chi.coTuQuyAt()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    int demSoAt() {
        if (this.handCards != null) {
            return this.handCards.demSoAt();
        }
        return 0;
    }
}
