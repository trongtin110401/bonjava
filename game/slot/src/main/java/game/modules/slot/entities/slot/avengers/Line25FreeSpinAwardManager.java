/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.avengers;

import java.util.*;

public class Line25FreeSpinAwardManager {

    private static List<Line25FreeSpinAward> awards = new ArrayList<>();
    private static Map<String, Line25FreeSpinAward> awardMap = new HashMap<>();

    static {
        Arrays.stream(Line25FreeSpinAward.values()).forEach(avengersAward -> {
            String key = avengersAward.getId() + "_" + avengersAward.getDuplicate();
            awardMap.put(key, avengersAward);
        });
    }

    public Line25FreeSpinAwardManager() {
        awards.addAll(Arrays.asList(Line25FreeSpinAward.values()));
    }

    public static List<Line25FreeSpinAward> list() {
        return awards;
    }

    public static Line25FreeSpinAward getAward(Line25Item item, int numItems) {
//        for (AvengersFreeSpinAward entry : AvengersFreeSpinAward.values()) {
//            if (entry.getItem() != item || entry.getDuplicate() != numItems) continue;
//            return entry;
//        }
//        return null;
        return awardMap.get(item.getId() + "_" + numItems);
    }
}

