/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.avengers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AvengersFreeSpinAwards {
    private static List<AvengersFreeSpinAward> awards = new ArrayList<AvengersFreeSpinAward>();

    public AvengersFreeSpinAwards() {
        awards.addAll(Arrays.asList(AvengersFreeSpinAward.values()));
    }

    public static List<AvengersFreeSpinAward> list() {
        return awards;
    }

    public static AvengersFreeSpinAward getAward(AvengersItem item, int numItems) {
        for (AvengersFreeSpinAward entry : AvengersFreeSpinAward.values()) {
            if (entry.getItem() != item || entry.getDuplicate() != numItems) continue;
            return entry;
        }
        return null;
    }
}

