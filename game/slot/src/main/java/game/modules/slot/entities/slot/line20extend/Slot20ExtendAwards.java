/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line20extend;

import java.util.*;

public class Slot20ExtendAwards {
    private static List<Slot20ExtendAward> awards = new ArrayList<Slot20ExtendAward>();
    private static Map<String, Slot20ExtendAward> awardMap = new HashMap<>();

    static {
        Arrays.stream(Slot20ExtendAward.values())
                .forEach(avengersAward -> {
                    String key = avengersAward.getItem().getId() + "_" + avengersAward.getDuplicate();
                    awardMap.put(key, avengersAward);
                });
    }

    public Slot20ExtendAwards() {
        Collections.addAll(awards, Slot20ExtendAward.values());
    }

    public static Slot20ExtendAward getAward(Slot20ExtendItem item, int numItems) {
        return awardMap.get(item.getId() + "_" + numItems);
    }
}

