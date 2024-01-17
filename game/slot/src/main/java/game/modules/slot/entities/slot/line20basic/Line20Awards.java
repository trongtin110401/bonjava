/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line20basic;

import java.util.*;

public class Line20Awards {

    private static List<Line20Award> awards = new ArrayList<Line20Award>();
    private static Map<String, Line20Award> awardMap = new HashMap<>();

    static {
        Arrays.stream(Line20Award.values())
                .forEach(award -> {
                    String key = award.getId() + "_" + award.getDuplicate();
                    awardMap.put(key, award);
                });
    }

    public Line20Awards() {
        Collections.addAll(awards, Line20Award.values());
    }

    public static List<Line20Award> list() {
        return awards;
    }

    public static Line20Award getAward(Line20Item item, int numItems) {
        return awardMap.get(item.getId() + "_" + numItems);
    }
}

