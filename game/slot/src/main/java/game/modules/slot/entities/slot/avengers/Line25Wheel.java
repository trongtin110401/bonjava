/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.avengers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Line25Wheel {
    private List<Line25Item> items = new ArrayList<>();

    public void addItem(Line25Item item) {
        this.items.add(item);
    }

    public Line25Item random() {
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
        StringBuilder item = new StringBuilder();
        items.forEach(avengersItem -> {
            item.append(avengersItem.getName()).append("|");
        });
        item.deleteCharAt(item.length() - 1);
        System.out.println(item);
    }
}

