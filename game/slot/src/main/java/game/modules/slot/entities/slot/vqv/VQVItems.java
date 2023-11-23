/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.vqv;

import game.modules.slot.entities.slot.vqv.VQVItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VQVItems {
    private int[] config = new int[]{30, 12, 12, 38, 45, 70, 75};
    public List<VQVItem> items = new ArrayList<VQVItem>();

    public VQVItems() {
        for (int i = 0; i < this.config.length; ++i) {
            for (int j = 0; j < this.config[i]; ++j) {
                this.items.add(VQVItem.findItem((byte)i));
            }
        }
    }

    public int size() {
        return this.items.size();
    }

    public VQVItem random() {
        Random rd = new Random();
        int index = rd.nextInt(this.items.size());
        return this.items.get(index);
    }
}

