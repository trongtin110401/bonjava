/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.ndv;

import game.modules.slot.entities.slot.ndv.NDVAward;
import game.modules.slot.entities.slot.ndv.NDVItem;
import java.util.ArrayList;
import java.util.List;

public class NDVAwards {
    private static List<NDVAward> awards = new ArrayList<NDVAward>();

    public NDVAwards() {
        for (NDVAward entry : NDVAward.values()) {
            awards.add(entry);
        }
    }

    public static List<NDVAward> list() {
        return awards;
    }

    public static NDVAward getAward(NDVItem item, int numItems) {
        for (NDVAward entry : NDVAward.values()) {
            if (entry.getItem() != item || entry.getDuplicate() != numItems) continue;
            return entry;
        }
        return null;
    }
}

