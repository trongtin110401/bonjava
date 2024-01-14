/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.avengers;

import java.util.*;

public class AvengersAwardManager {
    private static List<AvengersAward> awards = new ArrayList<AvengersAward>();
    private static Map<String, AvengersAward> awardMap = new HashMap<>();

    static {
        Arrays.stream(AvengersAward.values())
                .forEach(avengersAward -> {
                    String key = avengersAward.getId() + "_" + avengersAward.getDuplicate();
                    awardMap.put(key, avengersAward);
                });
    }

    public AvengersAwardManager() {
        for (AvengersAward entry : AvengersAward.values()) {
            awards.add(entry);
        }
    }

    public static AvengersAward getAward(AvengersItem item, int numItems) {
        return awardMap.get(item.getId() + "_" + numItems);
    }
}

