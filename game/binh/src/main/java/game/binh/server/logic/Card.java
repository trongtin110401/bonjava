// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server.logic;

import game.utils.LoggerUtils;

public class Card implements Comparable
{
    public static final String[][] card_name;
    public static final int eCARD_2 = 2;
    public static final int eCARD_3 = 3;
    public static final int eCARD_4 = 4;
    public static final int eCARD_5 = 5;
    public static final int eCARD_6 = 6;
    public static final int eCARD_7 = 7;
    public static final int eCARD_8 = 8;
    public static final int eCARD_9 = 9;
    public static final int eCARD_10 = 10;
    public static final int eCARD_J = 11;
    public static final int eCARD_Q = 12;
    public static final int eCARD_K = 13;
    public static final int eCARD_A = 14;
    public static final int eCARD_NONE = 15;
    public static final byte eBICH = 0;
    public static final byte eCHUON = 1;
    public static final byte eRO = 2;
    public static final byte eCO = 3;
    public static final byte eSHAPE_NONE = 4;
    public static final int eDO = 0;
    public static final int eDEN = 1;
    public static final int eNONE = 2;
    public static final int eID_NONE = 52;
    public int ID;
    public int SO;
    public int CHAT;
    public String name;
    public static Card[] pool;
    
    public static Card createCard(final Card c) {
        return createCard(c.ID);
    }
    
    public static Card createCard(int id) {
        if (id < 0 || id >= 52) {
            id = 0;
            LoggerUtils.error("createCard ERROR", (Object[])new Object[] { id });
        }
        Card c;
        if ((c = Card.pool[id]) != null) {
            return c;
        }
        c = (Card.pool[id] = new Card(id));
        return c;
    }
    
    public static Card createCard(final int so, final int chat) {
        final int id = (so - 2) * 4 + chat;
        return createCard(id);
    }
    
    private Card(final int id) {
        this.name = "no";
        if (id >= 0 && id < 52) {
            this.ID = id;
            this.SO = this.ID / 4 + 2;
            this.CHAT = this.ID % 4;
            this.name = Card.card_name[this.CHAT][this.SO - 2];
        }
        else {
            this.ID = 52;
            this.CHAT = 4;
            this.SO = 15;
        }
    }
    
    @Override
    public int compareTo(final Object card) {
        if (!(card instanceof Card)) {
            return 0;
        }
        final Card other = (Card)card;
        if (this.SO != other.SO) {
            return this.SO - other.SO;
        }
        return this.CHAT - other.CHAT;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    public boolean nextTo(final Card next) {
        if (this.SO == 14) {
            return next.SO == 2;
        }
        return this.SO + 1 == next.SO;
    }
    
    public int soSanhSo(final Card card) {
        if (this.SO > card.SO) {
            return 1;
        }
        if (this.SO < card.SO) {
            return -1;
        }
        return 0;
    }
    
    public boolean isRed() {
        return this.CHAT == 2 || this.CHAT == 3;
    }
    
    public boolean dongSo(final Card card) {
        return this.SO == card.SO;
    }
    
    public boolean dongChat(final Card card) {
        return this.CHAT == card.CHAT;
    }
    
    public boolean dongMau(final Card card) {
        if (this.CHAT == 0 || this.CHAT == 1) {
            return card.CHAT == 0 || card.CHAT == 1;
        }
        return card.CHAT == 2 || card.CHAT == 3;
    }
    
    public int GetSuit() {
        return this.CHAT;
    }
    
    public int GetNumber() {
        return this.SO;
    }
    
    int soSanhAtNhoNhat(final Card c) {
        if (this.SO == 14 && c.SO != 14) {
            return -1;
        }
        if (this.SO != 14 && c.SO == 14) {
            return 1;
        }
        return this.compareTo(c);
    }
    
    @Override
    public int hashCode() {
        return this.ID;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof Card) {
            final Card c = (Card)o;
            return c.ID == this.ID;
        }
        return false;
    }
    
    static {
        card_name = new String[][] { { "2b", "3b", "4b", "5b", "6b", "7b", "8b", "9b", "10b", "Jb", "Qb", "Kb", "Ab" }, { "2t", "3t", "4t", "5t", "6t", "7t", "8t", "9t", "10t", "Jt", "Qt", "Kt", "At" }, { "2r", "3r", "4r", "5r", "6r", "7r", "8r", "9r", "10r", "Jr", "Qr", "Kr", "Ar" }, { "2c", "3c", "4c", "5c", "6c", "7c", "8c", "9c", "10c", "Jc", "Qc", "Kc", "Ac" } };
        Card.pool = new Card[52];
    }
}
