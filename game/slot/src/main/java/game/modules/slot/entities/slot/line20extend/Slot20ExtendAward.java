/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line20extend;

public enum Slot20ExtendAward {

    PENTA_SCATTER("PENTA_SCATTER", 0, (byte) 1, Slot20ExtendItem.SCATTER, (byte) 5, 200f),
    QUADAR_SCATTER("QUADAR_SCATTER", 1, (byte) 2, Slot20ExtendItem.SCATTER, (byte) 4, 100f),
    TRIPLE_SCATTER("TRIPLE_SCATTER", 1, (byte) 2, Slot20ExtendItem.SCATTER, (byte) 4, 40f),

    PENTA_F("PENTA_F", 17, (byte) 18, Slot20ExtendItem.F, (byte) 5, 1000.0f),
    QUADAR_F("QUADAR_F", 18, (byte) 19, Slot20ExtendItem.F, (byte) 4, 200.0f),
    TRIPLE_F("TRIPLE_F", 19, (byte) 20, Slot20ExtendItem.F, (byte) 3, 40.0f),
    DOUBLE_F("DOUBLE_F", 19, (byte) 20, Slot20ExtendItem.F, (byte) 2, 2.0f),

    PENTA_E("PENTA_E", 20, (byte) 21, Slot20ExtendItem.E, (byte) 5, 400.0f),
    QUADAR_E("QUADAR_E", 21, (byte) 22, Slot20ExtendItem.E, (byte) 4, 100.0f),
    TRIPLE_E("TRIPLE_E", 22, (byte) 23, Slot20ExtendItem.E, (byte) 3, 30.0f),

    PENTA_D("PENTA_D", 23, (byte) 24, Slot20ExtendItem.D, (byte) 5, 250.0f),
    QUADAR_D("QUADAR_D", 24, (byte) 25, Slot20ExtendItem.D, (byte) 4, 100.0f),
    TRIPLE_D("TRIPLE_D", 25, (byte) 26, Slot20ExtendItem.D, (byte) 3, 20.0f),

    PENTA_C("PENTA_C", 26, (byte) 27, Slot20ExtendItem.C, (byte) 5, 200.0f),
    QUADAR_C("QUADAR_C", 27, (byte) 28, Slot20ExtendItem.C, (byte) 4, 35.0f),
    TRIPLE_C("TRIPLE_C", 28, (byte) 29, Slot20ExtendItem.C, (byte) 3, 10.0f),

    PENTA_B("PENTA_B", 29, (byte) 30, Slot20ExtendItem.B, (byte) 5, 150.0f),
    QUADAR_B("QUADAR_B", 30, (byte) 31, Slot20ExtendItem.B, (byte) 4, 30.0f),
    TRIPLE_B("TRIPLE_B", 31, (byte) 32, Slot20ExtendItem.B, (byte) 3, 5.0f),

    PENTA_A("PENTA_A", 32, (byte) 33, Slot20ExtendItem.A, (byte) 5, 50.0f),
    QUADAR_A("QUADAR_A", 33, (byte) 34, Slot20ExtendItem.A, (byte) 4, 20.0f),
    TRIPLE_A("TRIPLE_A", 34, (byte) 35, Slot20ExtendItem.A, (byte) 3, 5.0f);

    private byte id;
    private Slot20ExtendItem item;
    private byte duplicate;
    private float ratio;

    Slot20ExtendAward(String name, int index, byte id, Slot20ExtendItem item, byte duplicate, float ratio) {
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

    public void setItem(Slot20ExtendItem item) {
        this.item = item;
    }

    public Slot20ExtendItem getItem() {
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

