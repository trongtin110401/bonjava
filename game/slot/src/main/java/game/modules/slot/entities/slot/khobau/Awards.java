/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.khobau;

import game.modules.slot.entities.slot.khobau.KhoBauAward;
import game.modules.slot.entities.slot.khobau.KhoBauItem;
import java.util.ArrayList;
import java.util.List;

public class Awards {
    private static List<KhoBauAward> awards = new ArrayList<KhoBauAward>();

    public Awards() {
        for (KhoBauAward entry : KhoBauAward.values()) {
            awards.add(entry);
        }
    }

    public static List<KhoBauAward> list() {
        return awards;
    }

    public static KhoBauAward getAward(KhoBauItem item, int numItems) {
        for (KhoBauAward entry : KhoBauAward.values()) {
            if (entry.getItem() != item || entry.getDuplicate() != numItems) continue;
            return entry;
        }
        return null;
    }
}

