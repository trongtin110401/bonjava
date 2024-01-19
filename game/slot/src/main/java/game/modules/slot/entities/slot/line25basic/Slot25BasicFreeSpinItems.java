/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line25basic;

public class Slot25BasicFreeSpinItems {
    private static int[][] config = new int[][]
            {
                    {0, 0, 0, 1, 3, 5, 6, 11, 12, 13, 15},
                    {0, 0, 1, 1, 3, 5, 8, 12, 15, 16, 18},
                    {0, 0, 1, 1, 3, 4, 5, 11, 12, 13, 14},
                    {0, 0, 1, 1, 3, 5, 6, 11, 13, 14, 15},
                    {0, 0, 1, 1, 3, 5, 6, 11, 13, 14, 14}
            };
    private Slot25BasicWheel[] wheels = new Slot25BasicWheel[5];

    public Slot25BasicFreeSpinItems() {
        for (int i = 0; i < 5; ++i) {
            this.wheels[i] = new Slot25BasicWheel();
            for (int j = 0; j < 11; ++j) {
                for (int k = 0; k < config[i][j]; ++k) {
                    this.wheels[i].addItem(SlotBasic25Item.findItem((byte)j));
                }
            }
        }
    }

    public SlotBasic25Item random(int wheel) {
        return this.wheels[wheel].random();
    }

    public void refundItem(SlotBasic25Item item, int wheel) {
        this.wheels[wheel].addItem(item);
    }
}

