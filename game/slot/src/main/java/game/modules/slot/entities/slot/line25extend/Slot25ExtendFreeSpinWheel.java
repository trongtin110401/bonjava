/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line25extend;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Slot25ExtendFreeSpinWheel {
    private List<Slot25ExtendFreeSpinItem> items = new ArrayList<>();

    public void addItem(Slot25ExtendFreeSpinItem item) {
        this.items.add(item);
    }

    public Slot25ExtendFreeSpinItem random() {
        Random rd = new Random();
        int n = rd.nextInt(this.items.size());
        return this.items.remove(n);
    }

    public void remove(int n) {
        if (n >= 0 && n < this.items.size()) {
            this.items.remove(n);
        }
    }

    public void print() {
        StringBuilder itemStringBuilder = new StringBuilder();
        items.forEach(item -> {
            itemStringBuilder.append(item.getName()).append("|");
        });
        itemStringBuilder.deleteCharAt(itemStringBuilder.length() - 1);
        System.out.println(itemStringBuilder);
    }
}

