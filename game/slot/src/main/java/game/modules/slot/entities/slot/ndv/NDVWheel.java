/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.ndv;

import game.modules.slot.entities.slot.avengers.AvengersItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NDVWheel {
    private List<NDVItem> items = new ArrayList<NDVItem>();

    public void addItem(NDVItem item) {
        this.items.add(item);
    }

    public NDVItem random() {
        Random rd = new Random();
        int n = rd.nextInt(this.items.size());
        return this.items.remove(n);
    }

    public void remove(int n) {
        if (n >= 0 && n < this.items.size()) {
            this.items.remove(n);
        }
    }
}

