/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PickStarGifts {
    private static final int NUM_KEYS = 3;
    private static final int NUM_BOX = 4;
    private static final int NUM_GOLD = 23;
    private List<PickStarGiftItem> gifts = new ArrayList<PickStarGiftItem>();

    public PickStarGifts() {
        int i;
        for (i = 0; i < NUM_KEYS; ++i) {
            this.gifts.add(PickStarGiftItem.KEY);
        }
        for (i = 0; i < NUM_BOX; ++i) {
            this.gifts.add(PickStarGiftItem.BOX);
        }
        for (i = 0; i < NUM_GOLD; ++i) {
            this.gifts.add(PickStarGiftItem.GOLD);
        }
    }

    public PickStarGiftItem pickRandomAndRandomGift() {
        Random rd = new Random(System.currentTimeMillis());
        int n = rd.nextInt(this.gifts.size());
        PickStarGiftItem gift = this.gifts.get(n);
        this.gifts.remove(n);
        return gift;
    }
}

