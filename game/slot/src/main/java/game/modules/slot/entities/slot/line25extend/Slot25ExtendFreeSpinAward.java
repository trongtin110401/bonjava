/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line25extend;

public enum Slot25ExtendFreeSpinAward {

    PENTA_H("PENTA_H", 14, (byte) 15, Slot25ExtendFreeSpinItem.H, (byte) 5, 5000.0f),
    QUADAR_H("QUADAR_H", 15, (byte) 16, Slot25ExtendFreeSpinItem.G, (byte) 4, 500.0f),
    TRIPLE_H("TRIPLE_H", 16, (byte) 17, Slot25ExtendFreeSpinItem.G, (byte) 3, 300.0f),

    PENTA_G("PENTA_G", 14, (byte) 15, Slot25ExtendFreeSpinItem.G, (byte) 5, 300.0f),
    QUADAR_G("QUADAR_G", 15, (byte) 16, Slot25ExtendFreeSpinItem.G, (byte) 4, 250.0f),
    TRIPLE_G("TRIPLE_G", 16, (byte) 17, Slot25ExtendFreeSpinItem.G, (byte) 3, 200.0f),

    PENTA_F("PENTA_F", 17, (byte) 18, Slot25ExtendFreeSpinItem.F, (byte) 5, 250.0f),
    QUADAR_F("QUADAR_F", 18, (byte) 19, Slot25ExtendFreeSpinItem.F, (byte) 4, 200.0f),
    TRIPLE_F("TRIPLE_F", 19, (byte) 20, Slot25ExtendFreeSpinItem.F, (byte) 3, 100.0f),

    PENTA_E("PENTA_E", 20, (byte) 21, Slot25ExtendFreeSpinItem.E, (byte) 5, 175.0f),
    QUADAR_E("QUADAR_E", 21, (byte) 22, Slot25ExtendFreeSpinItem.E, (byte) 4, 125.0f),
    TRIPLE_E("TRIPLE_E", 22, (byte) 23, Slot25ExtendFreeSpinItem.E, (byte) 3, 85.0f),

    PENTA_D("PENTA_D", 23, (byte) 24, Slot25ExtendFreeSpinItem.D, (byte) 5, 150.0f),
    QUADAR_D("QUADAR_D", 24, (byte) 25, Slot25ExtendFreeSpinItem.D, (byte) 4, 100.0f),
    TRIPLE_D("TRIPLE_D", 25, (byte) 26, Slot25ExtendFreeSpinItem.D, (byte) 3, 80.0f),

    PENTA_C("PENTA_C", 26, (byte) 27, Slot25ExtendFreeSpinItem.C, (byte) 5, 100.0f),
    QUADAR_C("QUADAR_C", 27, (byte) 28, Slot25ExtendFreeSpinItem.C, (byte) 4, 80.0f),
    TRIPLE_C("TRIPLE_C", 28, (byte) 29, Slot25ExtendFreeSpinItem.C, (byte) 3, 50.0f),

    PENTA_B("PENTA_B", 29, (byte) 30, Slot25ExtendFreeSpinItem.B, (byte) 5, 45.0f),
    QUADAR_B("QUADAR_B", 30, (byte) 31, Slot25ExtendFreeSpinItem.B, (byte) 4, 35.0f),
    TRIPLE_B("TRIPLE_B", 31, (byte) 32, Slot25ExtendFreeSpinItem.B, (byte) 3, 25.0f),

    PENTA_A("PENTA_A", 32, (byte) 33, Slot25ExtendFreeSpinItem.A, (byte) 5, 40.0f),
    QUADAR_A("QUADAR_A", 33, (byte) 34, Slot25ExtendFreeSpinItem.A, (byte) 4, 30.0f),
    TRIPLE_A("TRIPLE_A", 34, (byte) 35, Slot25ExtendFreeSpinItem.A, (byte) 3, 20.0f);

    private byte id;
    private Slot25ExtendFreeSpinItem item;
    private byte duplicate;
    private float ratio;

    Slot25ExtendFreeSpinAward(String s, int n2, byte id, Slot25ExtendFreeSpinItem item, byte duplicate, float ratio) {
        this.id = id;
        this.item = item;
        this.duplicate = duplicate;
        this.ratio = ratio;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public byte getId() {
        return this.id;
    }

    public void setItem(Slot25ExtendFreeSpinItem item) {
        this.item = item;
    }

    public Slot25ExtendFreeSpinItem getItem() {
        return this.item;
    }

    public void setDuplicate(byte duplicate) {
        this.duplicate = duplicate;
    }

    public byte getDuplicate() {
        return this.duplicate;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    public float getRatio() {
        return this.ratio;
    }
}

