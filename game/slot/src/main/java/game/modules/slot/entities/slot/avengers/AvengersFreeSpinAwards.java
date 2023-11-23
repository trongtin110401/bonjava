/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.avengers;

import game.modules.slot.entities.slot.avengers.AvengersFreeSpinAward;
import game.modules.slot.entities.slot.avengers.AvengersItem;
import java.util.ArrayList;
import java.util.List;

public class AvengersFreeSpinAwards {
    private static List<AvengersFreeSpinAward> awards = new ArrayList<AvengersFreeSpinAward>();

    public AvengersFreeSpinAwards() {
        for (AvengersFreeSpinAward entry : AvengersFreeSpinAward.values()) {
            awards.add(entry);
        }
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

