/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.avengers;

import game.modules.slot.entities.slot.avengers.AvengersItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AvengersWheel {
    private List<AvengersItem> items = new ArrayList<AvengersItem>();

    public void addItem(AvengersItem item) {
        this.items.add(item);
    }

    public AvengersItem random() {
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

