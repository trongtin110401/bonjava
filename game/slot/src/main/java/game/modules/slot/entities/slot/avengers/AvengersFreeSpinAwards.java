/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.avengers;

import java.util.*;

public class AvengersFreeSpinAwards {

    private static List<AvengersFreeSpinAward> awards = new ArrayList<>();
    private static Map<String, AvengersFreeSpinAward> awardMap = new HashMap<>();

    static {
        Arrays.stream(AvengersFreeSpinAward.values()).forEach(avengersAward -> {
            String key = avengersAward.getId() + "_" + avengersAward.getDuplicate();
            awardMap.put(key, avengersAward);
        });
    }

    public AvengersFreeSpinAwards() {
        awards.addAll(Arrays.asList(AvengersFreeSpinAward.values()));
    }

    public static List<AvengersFreeSpinAward> list() {
        return awards;
    }

    public static AvengersFreeSpinAward getAward(AvengersItem item, int numItems) {
//        for (AvengersFreeSpinAward entry : AvengersFreeSpinAward.values()) {
//            if (entry.getItem() != item || entry.getDuplicate() != numItems) continue;
//            return entry;
//        }
//        return null;
        return awardMap.get(item.getId() + "_" + numItems);
    }
}

