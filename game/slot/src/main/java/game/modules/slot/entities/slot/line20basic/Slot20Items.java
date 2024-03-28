/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line20basic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Slot20Items {
    private int[] config = new int[]{6, 11, 25, 35, 50, 65, 70};
//    private int[] config = new int[]{20, 20, 25, 35, 50, 50, 50};
//    private int[] config = new int[]{50};
    public List<Slot20Item> items = new ArrayList<Slot20Item>();

    public Slot20Items() {
        for (int i = 0; i < this.config.length; ++i) {
            for (int j = 0; j < this.config[i]; ++j) {
                this.items.add(Slot20Item.findItem((byte) i));
            }
        }
        Collections.shuffle(items);
    }

    public int size() {
        return this.items.size();
    }

    public Slot20Item random() {
        Random rd = new Random();
        int index = rd.nextInt(this.items.size());
        return this.items.get(index);
    }
}

