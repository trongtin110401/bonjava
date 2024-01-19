/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line25basic;

import java.util.*;

public class Slot25BasicFreeSpinAwardManager {

    private static List<Slot25BasicFreeSpinAward> awards = new ArrayList<>();
    private static Map<String, Slot25BasicFreeSpinAward> awardMap = new HashMap<>();

    static {
        Arrays.stream(Slot25BasicFreeSpinAward.values()).forEach(avengersAward -> {
            String key = avengersAward.getId() + "_" + avengersAward.getDuplicate();
            awardMap.put(key, avengersAward);
        });
    }

    public Slot25BasicFreeSpinAwardManager() {
        awards.addAll(Arrays.asList(Slot25BasicFreeSpinAward.values()));
    }

    public static List<Slot25BasicFreeSpinAward> list() {
        return awards;
    }

    public static Slot25BasicFreeSpinAward getAward(SlotBasic25Item item, int numItems) {
//        for (AvengersFreeSpinAward entry : AvengersFreeSpinAward.values()) {
//            if (entry.getItem() != item || entry.getDuplicate() != numItems) continue;
//            return entry;
//        }
//        return null;
        return awardMap.get(item.getId() + "_" + numItems);
    }
}

