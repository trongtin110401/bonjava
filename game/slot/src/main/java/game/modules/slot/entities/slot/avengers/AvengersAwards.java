/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.avengers;

import game.modules.slot.entities.slot.avengers.AvengersAward;
import game.modules.slot.entities.slot.avengers.AvengersItem;
import java.util.ArrayList;
import java.util.List;

public class AvengersAwards {
    private static List<AvengersAward> awards = new ArrayList<AvengersAward>();

    public AvengersAwards() {
        for (AvengersAward entry : AvengersAward.values()) {
            awards.add(entry);
        }
    }

    public static List<AvengersAward> list() {
        return awards;
    }

    public static AvengersAward getAward(AvengersItem item, int numItems) {
        for (AvengersAward entry : AvengersAward.values()) {
            if (entry.getItem() != item || entry.getDuplicate() != numItems) continue;
            return entry;
        }
        return null;
    }
}

