// 
// Decompiled by Procyon v0.5.36
// 

package game.binh.server.logic;

import java.util.List;

public class LogicTest
{
    public static int count;
    
    public static void main(final String[] args) {
        for (int i = 0; i < 1000; ++i) {
            test(5);
        }
        System.out.println(LogicTest.count);
    }
    
    public static void test(final int groupType) {
        final CardSuit suit = new CardSuit();
        final List<GroupCard> cards = suit.dealCards(0);
        final GroupCard gc1 = cards.get(0);
        final GroupCard gc2 = cards.get(1);
        final GroupCard gc3 = cards.get(2);
        final GroupCard gc4 = cards.get(3);
        final PlayerCard pc1 = new PlayerCard();
        pc1.ApplyNewGroupCards(gc1, 0);
        pc1.autoSort2();
        final PlayerCard pc2 = new PlayerCard();
        pc2.ApplyNewGroupCards(gc2, 0);
        pc2.autoSort2();
        final PlayerCard pc3 = new PlayerCard();
        pc3.ApplyNewGroupCards(gc3, 0);
        pc3.autoSort2();
        final PlayerCard pc4 = new PlayerCard();
        pc4.ApplyNewGroupCards(gc4, 0);
        pc4.autoSort2();
        if (pc1.GetPlayerCardsKind(1) == groupType) {
            System.out.println("===============================================");
            System.out.println(pc1.fullCard);
            System.out.println("===============================================");
            ++LogicTest.count;
        }
        if (pc2.GetPlayerCardsKind(1) == groupType) {
            System.out.println("===============================================");
            System.out.println(pc2.fullCard);
            System.out.println("===============================================");
            ++LogicTest.count;
        }
        if (pc3.GetPlayerCardsKind(1) == groupType) {
            System.out.println("===============================================");
            System.out.println(pc3.fullCard);
            System.out.println("===============================================");
            ++LogicTest.count;
        }
        if (pc4.GetPlayerCardsKind(1) == groupType) {
            System.out.println("===============================================");
            System.out.println(pc4.fullCard);
            System.out.println("===============================================");
            ++LogicTest.count;
        }
    }
    
    static {
        LogicTest.count = 0;
    }
}
