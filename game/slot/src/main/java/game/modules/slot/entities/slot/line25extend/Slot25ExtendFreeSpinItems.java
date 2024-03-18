/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line25extend;

public class Slot25ExtendFreeSpinItems {

    private static int[][] config = new int[][]
            {
                    {0, 0, 0, 1, 5, 11, 12, 13, 15},
                    {0, 0, 1, 1, 5, 12, 15, 16, 18},
                    {0, 0, 1, 1, 4, 11, 12, 13, 14},
                    {0, 0, 1, 1, 5, 11, 13, 14, 15},
                    {0, 0, 1, 1, 5, 11, 13, 14, 14}
            };
    private Slot25ExtendFreeSpinWheel[] wheels = new Slot25ExtendFreeSpinWheel[5];

    public Slot25ExtendFreeSpinItems() {
        for (int col = 0; col < 5; ++col) {
            this.wheels[col] = new Slot25ExtendFreeSpinWheel();
            for (int j = 0; j < 9; ++j) {
                for (int k = 0; k < config[col][j]; ++k) {
                    this.wheels[col].addItem(Slot25ExtendFreeSpinItem.findItem((byte) j));
                }
            }
        }
    }

    public Slot25ExtendFreeSpinItem random(int wheel) {
        return this.wheels[wheel].random();
    }

    public void refundItem(Slot25ExtendFreeSpinItem item, int wheel) {
        this.wheels[wheel].addItem(item);
    }
}

