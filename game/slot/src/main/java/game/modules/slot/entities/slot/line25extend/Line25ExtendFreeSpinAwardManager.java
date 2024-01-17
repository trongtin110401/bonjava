/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line25extend;

import java.util.*;

public class Line25ExtendFreeSpinAwardManager {

    private static List<Line25ExtendFreeSpinAward> awards = new ArrayList<>();
    private static Map<String, Line25ExtendFreeSpinAward> awardMap = new HashMap<>();

    static {
        Arrays.stream(Line25ExtendFreeSpinAward.values()).forEach(avengersAward -> {
            String key = avengersAward.getId() + "_" + avengersAward.getDuplicate();
            awardMap.put(key, avengersAward);
        });
    }

    public Line25ExtendFreeSpinAwardManager() {
        awards.addAll(Arrays.asList(Line25ExtendFreeSpinAward.values()));
    }

    public static List<Line25ExtendFreeSpinAward> list() {
        return awards;
    }

    public static Line25ExtendFreeSpinAward getAward(Line25ExtendItem item, int numItems) {
//        for (AvengersFreeSpinAward entry : AvengersFreeSpinAward.values()) {
//            if (entry.getItem() != item || entry.getDuplicate() != numItems) continue;
//            return entry;
//        }
//        return null;
        return awardMap.get(item.getId() + "_" + numItems);
    }
}

