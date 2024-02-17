/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line25extend;

import java.util.*;

public class Slot25ExtendAwards {
    private static List<Slot25ExtendAward> awards = new ArrayList<Slot25ExtendAward>();
    private static Map<String, Slot25ExtendAward> awardMap = new HashMap<>();

    static {
        Arrays.stream(Slot25ExtendAward.values())
                .forEach(avengersAward -> {
                    String key = avengersAward.getItem().getId() + "_" + avengersAward.getDuplicate();
                    awardMap.put(key, avengersAward);
                });
    }

    public Slot25ExtendAwards() {
        Collections.addAll(awards, Slot25ExtendAward.values());
    }

    public static Slot25ExtendAward getAward(Slot25ExtendItem item, int numItems) {
        return awardMap.get(item.getId() + "_" + numItems);
    }
}

