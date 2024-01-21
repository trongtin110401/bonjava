/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line20basic;

import java.util.*;

public class Slot20Awards {

    private static List<Slot20Award> awards = new ArrayList<Slot20Award>();
    private static Map<String, Slot20Award> awardMap = new HashMap<>();

    static {
        Arrays.stream(Slot20Award.values())
                .forEach(award -> {
                    String key = award.getItem().getId() + "_" + award.getDuplicate();
                    awardMap.put(key, award);
                });
    }

    public Slot20Awards() {
        Collections.addAll(awards, Slot20Award.values());
    }

    public static List<Slot20Award> list() {
        return awards;
    }

    public static Slot20Award getAward(Slot20Item item, int numItems) {
        return awardMap.get(item.getId() + "_" + numItems);
    }
}

