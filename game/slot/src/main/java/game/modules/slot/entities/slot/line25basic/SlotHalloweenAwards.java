/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line25basic;

import java.util.*;

public class SlotHalloweenAwards {
    private static List<SlotHalloweenAward> awards = new ArrayList<>();
    private static Map<String, SlotHalloweenAward> awardMap = new HashMap<>();

    static {
        Arrays.stream(SlotHalloweenAward.values())
                .forEach(avengersAward -> {
                    String key = avengersAward.getItem().getId() + "_" + avengersAward.getDuplicate();
                    awardMap.put(key, avengersAward);
                });
    }

    public SlotHalloweenAwards() {
        Collections.addAll(awards, SlotHalloweenAward.values());
    }

    public static SlotHalloweenAward getAward(SlotBasic25Item item, int numItems) {
        return awardMap.get(item.getId() + "_" + numItems);
    }
}

