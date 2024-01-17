/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line25basic;

import java.util.*;

public class Line25AwardManager {
    private static List<Line25Award> awards = new ArrayList<Line25Award>();
    private static Map<String, Line25Award> awardMap = new HashMap<>();

    static {
        Arrays.stream(Line25Award.values())
                .forEach(avengersAward -> {
                    String key = avengersAward.getId() + "_" + avengersAward.getDuplicate();
                    awardMap.put(key, avengersAward);
                });
    }

    public Line25AwardManager() {
        for (Line25Award entry : Line25Award.values()) {
            awards.add(entry);
        }
    }

    public static Line25Award getAward(Line25Item item, int numItems) {
        return awardMap.get(item.getId() + "_" + numItems);
    }
}

