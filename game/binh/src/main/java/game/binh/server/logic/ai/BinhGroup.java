// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server.logic.ai;

import java.util.List;
import java.util.Collections;
import java.util.LinkedList;
import game.binh.server.logic.Card;
import game.binh.server.logic.GroupCard;
import java.util.Comparator;

public class BinhGroup
{
    public static final Comparator<BinhGroup> SORT_COMPARATOR;
    private GroupCard chi1;
    private GroupCard chi2;
    private GroupCard chi3;
    private int score;
    
    public static void main(final String[] args) {
        final String x = "#S:10rJrQrKbAb$|#D:8c8t9c7b2r$|#D:3t3b10t$|327";
        final BinhGroup group = new BinhGroup(x);
        System.out.println(group);
        final GroupCard gc1 = new GroupCard(group.getOrder());
        gc1.DecreaseSort();
        System.out.println(gc1);
        final GroupCard gc2 = new GroupCard(group.getRandom());
        gc2.DecreaseSort();
        System.out.println(gc2);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.chi1).append("|");
        sb.append(this.chi2).append("|");
        sb.append(this.chi3).append("|");
        sb.append(this.score).append("|");
        return sb.toString();
    }
    
    public BinhGroup(final String input) {
        final String[] xx = input.split("\\|");
        this.chi1 = new GroupCard(xx[0]);
        this.chi2 = new GroupCard(xx[1]);
        this.chi3 = new GroupCard(xx[2]);
        this.score = Integer.parseInt(xx[3]);
    }
    
    public BinhGroup(final GroupCard gc, final int score) {
        this.chi1 = new GroupCard();
        this.chi2 = new GroupCard();
        this.chi3 = new GroupCard();
        int index = 0;
        for (int i = 0; i < 5; ++i) {
            this.chi1.AddCard(gc.Cards().get(index++));
        }
        for (int i = 0; i < 5; ++i) {
            this.chi1.AddCard(gc.Cards().get(index++));
        }
        for (int i = 0; i < 3; ++i) {
            this.chi1.AddCard(gc.Cards().get(index++));
        }
        this.score = score;
    }
    
    public int getScore() {
        return this.score;
    }
    
    public int[] getOrder() {
        int index = 0;
        final int[] order = new int[13];
        for (int i = 0; i < this.chi1.GetNumOfCards(); ++i) {
            final Card c = this.chi1.Cards().get(i);
            order[index++] = c.ID;
        }
        for (int i = 0; i < this.chi2.GetNumOfCards(); ++i) {
            final Card c = this.chi2.Cards().get(i);
            order[index++] = c.ID;
        }
        for (int i = 0; i < this.chi3.GetNumOfCards(); ++i) {
            final Card c = this.chi3.Cards().get(i);
            order[index++] = c.ID;
        }
        return order;
    }
    
    public int[] getRandom() {
        final int[] order = this.getOrder();
        final LinkedList<Integer> ids = new LinkedList<Integer>();
        for (int i = 0; i < order.length; ++i) {
            ids.add(order[i]);
        }
        Collections.shuffle(ids);
        final int[] random = new int[13];
        for (int j = 0; j < random.length; ++j) {
            random[j] = ids.get(j);
        }
        return random;
    }
    
    public boolean isJackpot() {
        return this.score == 1005;
    }
    
    public GroupCard getRandomGroupCard() {
        final GroupCard gc = new GroupCard(this.getRandom());
        return gc;
    }
    
    public GroupCard getOrderGroupCard() {
        final GroupCard gc = new GroupCard(this.getOrder());
        return gc;
    }
    
    static {
        SORT_COMPARATOR = new Comparator<BinhGroup>() {
            @Override
            public int compare(final BinhGroup c1, final BinhGroup c2) {
                if (c1.getScore() < c2.getScore()) {
                    return 1;
                }
                if (c1.getScore() > c2.getScore()) {
                    return -1;
                }
                return 0;
            }
        };
    }
}
