/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.khobau;

import game.modules.slot.entities.slot.khobau.KhoBauItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KhoBauItems {
    private int[] config = new int[]{15, 11, 25, 35, 50, 65, 70};
    public List<KhoBauItem> items = new ArrayList<KhoBauItem>();

    public KhoBauItems() {
        for (int i = 0; i < this.config.length; ++i) {
            for (int j = 0; j < this.config[i]; ++j) {
                this.items.add(KhoBauItem.findItem((byte)i));
            }
        }
    }

    public int size() {
        return this.items.size();
    }

    public KhoBauItem random() {
        Random rd = new Random();
        int index = rd.nextInt(this.items.size());
        return this.items.get(index);
    }
}

