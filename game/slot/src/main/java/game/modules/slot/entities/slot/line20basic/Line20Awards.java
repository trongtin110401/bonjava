/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line20basic;

import java.util.ArrayList;
import java.util.List;

public class Line20Awards {
    private static List<Line20Award> awards = new ArrayList<Line20Award>();

    public Line20Awards() {
        for (Line20Award entry : Line20Award.values()) {
            awards.add(entry);
        }
    }

    public static List<Line20Award> list() {
        return awards;
    }

    public static Line20Award getAward(Line20Item item, int numItems) {
        for (Line20Award entry : Line20Award.values()) {
            if (entry.getItem() != item || entry.getDuplicate() != numItems) continue;
            return entry;
        }
        return null;
    }
}

