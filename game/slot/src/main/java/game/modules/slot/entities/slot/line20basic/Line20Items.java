/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line20basic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Line20Items {
    private int[] config = new int[]{15, 11, 25, 35, 50, 65, 70};
    public List<Line20Item> items = new ArrayList<Line20Item>();

    public Line20Items() {
        for (int i = 0; i < this.config.length; ++i) {
            for (int j = 0; j < this.config[i]; ++j) {
                this.items.add(Line20Item.findItem((byte) i));
            }
        }
    }

    public int size() {
        return this.items.size();
    }

    public Line20Item random() {
        Random rd = new Random();
        int index = rd.nextInt(this.items.size());
        return this.items.get(index);
    }
}

