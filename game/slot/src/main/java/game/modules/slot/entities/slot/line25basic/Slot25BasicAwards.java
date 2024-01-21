/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line25basic;

import java.util.*;

public class Slot25BasicAwards {
    private static List<Slot25BasicAward> awards = new ArrayList<Slot25BasicAward>();
    private static Map<String, Slot25BasicAward> awardMap = new HashMap<>();

    static {
        Arrays.stream(Slot25BasicAward.values())
                .forEach(avengersAward -> {
                    String key = avengersAward.getItem().getId() + "_" + avengersAward.getDuplicate();
                    awardMap.put(key, avengersAward);
                });
    }

    public Slot25BasicAwards() {
        Collections.addAll(awards, Slot25BasicAward.values());
    }

    public static Slot25BasicAward getAward(SlotBasic25Item item, int numItems) {
        return awardMap.get(item.getId() + "_" + numItems);
    }
}

