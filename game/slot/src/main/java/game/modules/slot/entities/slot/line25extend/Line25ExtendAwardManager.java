/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line25extend;

import java.util.*;

public class Line25ExtendAwardManager {
    private static List<Line25ExtendAward> awards = new ArrayList<Line25ExtendAward>();
    private static Map<String, Line25ExtendAward> awardMap = new HashMap<>();

    static {
        Arrays.stream(Line25ExtendAward.values())
                .forEach(avengersAward -> {
                    String key = avengersAward.getId() + "_" + avengersAward.getDuplicate();
                    awardMap.put(key, avengersAward);
                });
    }

    public Line25ExtendAwardManager() {
        for (Line25ExtendAward entry : Line25ExtendAward.values()) {
            awards.add(entry);
        }
    }

    public static Line25ExtendAward getAward(Line25ExtendItem item, int numItems) {
        return awardMap.get(item.getId() + "_" + numItems);
    }
}

