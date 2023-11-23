// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server.logic;

import java.util.Collection;
import java.util.Random;
import game.utils.GameUtils;
import java.util.LinkedList;
import game.binh.server.logic.ai.BinhSuit;
import game.binh.server.logic.ai.BinhAuto;
import game.binh.server.logic.ai.BinhGroup;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class CardSuit
{
    public static final int SO_CHI = 2;
    public static final int MAX_PLAYERS = 4;
    public static final int SOLO_PLAYERS = 2;
    public static final int MAX_NUMBER_OF_CARDS = 13;
    public static final int MAX_NUMBER_OF_CARDS_CHI1 = 5;
    public static final int MAX_NUMBER_OF_CARDS_CHI2 = 5;
    public static final int MAX_NUMBER_OF_CARDS_CHI3 = 3;
    public List<Integer> ids;
    public List<Integer> chat;
    public int cheat;
    public volatile boolean noHu;
    public volatile int chairNoHu;
    
    public CardSuit() {
        this.ids = new ArrayList<Integer>();
        this.chat = new ArrayList<Integer>();
        this.cheat = 0;
        this.noHu = false;
        this.chairNoHu = -1;
        for (Integer i = 0; i < 52; ++i) {
            this.ids.add(i);
            final Integer n = i;
        }
        Collections.shuffle(this.ids);
    }
    
    public void initCard() {
        this.ids.clear();
        for (Integer i = 0; i < 52; ++i) {
            this.ids.add(i);
            final Integer n = i;
        }
        this.cheat = 0;
    }
    
    public boolean setOrder(final byte[] cards) {
        final boolean[] hasId = new boolean[52];
        this.ids.clear();
        for (int i = 0; i < cards.length; ++i) {
            this.ids.add((int)cards[i]);
            hasId[cards[i]] = true;
        }
        for (Integer j = 0; j < 52; ++j) {
            if (!hasId[j]) {
                this.ids.add(j);
            }
            final Integer n = j;
        }
        this.cheat = 500;
        return true;
    }
    
    public void setRandomFirstTurn() {
        Collections.shuffle(this.ids);
    }
    
    public void setRandom() {
    }
    
    public void removeRandom() {
        Collections.sort(this.ids);
    }
    
    public List<BinhGroup> dealCards(final int rule, final boolean canJackpot) {
        List<BinhGroup> groups = null;
        if (this.noHu) {
            final BinhSuit suit = BinhAuto.instance().getSuitJackpot(rule);
            groups = suit.getListGroup();
            int indexJackpot = 0;
            for (int i = 0; i < groups.size(); ++i) {
                final BinhGroup g = groups.get(i);
                if (g.isJackpot()) {
                    indexJackpot = i;
                    break;
                }
            }
            Collections.swap(groups, indexJackpot, this.chairNoHu);
            this.noHu = false;
            this.chairNoHu = -1;
        }
        else if (this.cheat > 0) {
            --this.cheat;
            groups = this.cheatCard();
        }
        else {
            final BinhSuit suit = BinhAuto.instance().getSuit(rule, canJackpot);
            groups = suit.getListGroup();
        }
        return groups;
    }
    
    public List<BinhGroup> cheatCard() {
        final LinkedList<BinhGroup> groups = new LinkedList<BinhGroup>();
        int currentIndex = 0;
        final int[] cards = new int[13];
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 13; ++j) {
                cards[j] = this.ids.get(currentIndex++);
            }
            final GroupCard gc = new GroupCard(cards);
            final BinhGroup bg = new BinhGroup(gc, 0);
            groups.add(bg);
        }
        return groups;
    }
    
    public List<GroupCard> dealCards(final int rule) {
        final ArrayList<GroupCard> groupCards = new ArrayList<GroupCard>();
        final int[] cards = new int[13];
        boolean flag = true;
        int count = 0;
        while (flag) {
            flag = false;
            if (count > 0) {
                this.setRandom();
            }
            groupCards.clear();
            int curentIndex = 0;
            for (int i = 0; i < 4; ++i) {
                for (int j = 0; j < 13; ++j) {
                    cards[j] = this.ids.get(curentIndex++);
                }
                final GroupCard gc = new GroupCard(cards);
                groupCards.add(gc);
                if (gc.isNoHu(rule) && !this.noHu) {
                    if (!GameUtils.isCheat) {
                        flag = true;
                        ++count;
                    }
                }
            }
        }
        return groupCards;
    }
    
    public String toCardString(final int size) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; ++i) {
            final Card c = Card.createCard(this.ids.get(i));
            sb.append(c.toString());
        }
        return sb.toString();
    }
    
    public String toCardString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.ids.size(); ++i) {
            final Card c = Card.createCard(this.ids.get(i));
            sb.append(c.toString());
        }
        return sb.toString();
    }
    
    public String toIdList() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.ids.size(); ++i) {
            sb.append(this.ids.get(i)).append(",");
        }
        return sb.toString();
    }
    
    public void fromIdList(final String s) {
        final String[] ss = s.split(",");
        this.ids.clear();
        for (int i = 0; i < ss.length; ++i) {
            final Integer b = Integer.parseInt(ss[i]);
            this.ids.add(b);
        }
    }
    
    public synchronized void noHuAt(final int chair) {
        this.setNoHu(chair);
        this.chairNoHu = chair;
        this.noHu = true;
    }
    
    public void setNoHu(final int chair) {
        final ArrayList<Integer> fullCard = new ArrayList<Integer>();
        final ArrayList<Integer> subCard = new ArrayList<Integer>();
        for (Integer i = 0; i < 52; ++i) {
            fullCard.add(i);
            final Integer n = i;
        }
        final Random rd = new Random();
        for (int i2 = 2; i2 <= 14; ++i2) {
            final int random = Math.abs(rd.nextInt() % 4);
            final Card c = Card.createCard(i2, random);
            subCard.add(c.ID);
        }
        fullCard.removeAll(subCard);
        Collections.shuffle(fullCard);
        this.ids.clear();
        int index1 = 0;
        int index2 = 0;
        for (int i3 = 0; i3 < 4; ++i3) {
            for (int j = 0; j < 13; ++j) {
                if (i3 == chair) {
                    this.ids.add(subCard.get(index2++));
                }
                else {
                    this.ids.add(fullCard.get(index1++));
                }
            }
        }
    }
}
