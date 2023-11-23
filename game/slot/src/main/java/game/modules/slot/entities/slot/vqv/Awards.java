/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.vqv;

import game.modules.slot.entities.slot.vqv.VQVAward;
import game.modules.slot.entities.slot.vqv.VQVItem;
import java.util.ArrayList;
import java.util.List;

public class Awards {
    private static List<VQVAward> awards = new ArrayList<VQVAward>();

    public Awards() {
        for (VQVAward entry : VQVAward.values()) {
            awards.add(entry);
        }
    }

    public static List<VQVAward> list() {
        return awards;
    }

    public static VQVAward getAward(VQVItem item, int numItems) {
        for (VQVAward entry : VQVAward.values()) {
            if (entry.getItem() != item || entry.getDuplicate() != numItems) continue;
            return entry;
        }
        return null;
    }
}

