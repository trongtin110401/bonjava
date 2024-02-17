/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line25extend;

import java.util.*;

public class Slot25ExtendFreeSpinAwards {

    private static List<Slot25ExtendFreeSpinAward> awards = new ArrayList<>();
    private static Map<String, Slot25ExtendFreeSpinAward> awardMap = new HashMap<>();

    static {
        Arrays.stream(Slot25ExtendFreeSpinAward.values()).forEach(avengersAward -> {
            String key = avengersAward.getId() + "_" + avengersAward.getDuplicate();
            awardMap.put(key, avengersAward);
        });
    }

    public Slot25ExtendFreeSpinAwards() {
        awards.addAll(Arrays.asList(Slot25ExtendFreeSpinAward.values()));
    }

    public static List<Slot25ExtendFreeSpinAward> list() {
        return awards;
    }

    public static Slot25ExtendFreeSpinAward getAward(Slot25ExtendFreeSpinItem item, int numItems) {
        return awardMap.get(item.getId() + "_" + numItems);
    }
}

