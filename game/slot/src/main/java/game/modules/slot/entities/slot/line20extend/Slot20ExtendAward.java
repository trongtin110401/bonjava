/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line20extend;

public enum Slot20ExtendAward {

    JACKPOT("JACKPOT", 0, (byte) 1, Slot20ExtendItem.JACKPOT, (byte) 5, 0f),

    PENTA_SCATTER("PENTA_SCATTER", 0, (byte) 1, Slot20ExtendItem.SCATTER, (byte) 5, 200f),
    QUADAR_SCATTER("QUADAR_SCATTER", 1, (byte) 2, Slot20ExtendItem.SCATTER, (byte) 4, 100f),
    TRIPLE_SCATTER("TRIPLE_SCATTER", 1, (byte) 2, Slot20ExtendItem.SCATTER, (byte) 4, 40f),

    PENTA_BONUS("PENTA_SCATTER", 0, (byte) 1, Slot20ExtendItem.BONUS, (byte) 5, 0f),
    QUADAR_BONUS("QUADAR_SCATTER", 1, (byte) 2, Slot20ExtendItem.BONUS, (byte) 4, 0f),
    TRIPLE_BONUS("TRIPLE_SCATTER", 1, (byte) 2, Slot20ExtendItem.BONUS, (byte) 4, 0f),

    PENTA_F("PENTA_F", 17, (byte) 18, Slot20ExtendItem.F, (byte) 5, 1000.0f),
    QUADAR_F("QUADAR_F", 18, (byte) 19, Slot20ExtendItem.F, (byte) 4, 200.0f),
    TRIPLE_F("TRIPLE_F", 19, (byte) 20, Slot20ExtendItem.F, (byte) 3, 40.0f),
    DOUBLE_F("DOUBLE_F", 19, (byte) 20, Slot20ExtendItem.F, (byte) 2, 2.0f),

    PENTA_E2("PENTA_E2", 20, (byte) 21, Slot20ExtendItem.E2, (byte) 5, 400.0f),
    QUADAR_E2("QUADAR_E2", 21, (byte) 22, Slot20ExtendItem.E2, (byte) 4, 100.0f),
    TRIPLE_E2("TRIPLE_E2", 22, (byte) 23, Slot20ExtendItem.E2, (byte) 3, 30.0f),

    PENTA_E1("PENTA_E1", 20, (byte) 21, Slot20ExtendItem.E1, (byte) 5, 400.0f),
    QUADAR_E1("QUADAR_E1", 21, (byte) 22, Slot20ExtendItem.E1, (byte) 4, 100.0f),
    TRIPLE_E1("TRIPLE_E1", 22, (byte) 23, Slot20ExtendItem.E1, (byte) 3, 30.0f),

    PENTA_D2("PENTA_D2", 23, (byte) 24, Slot20ExtendItem.D2, (byte) 5, 250.0f),
    QUADAR_D2("QUADAR_D2", 24, (byte) 25, Slot20ExtendItem.D2, (byte) 4, 100.0f),
    TRIPLE_D2("TRIPLE_D2", 25, (byte) 26, Slot20ExtendItem.D2, (byte) 3, 20.0f),

    PENTA_D1("PENTA_D1", 23, (byte) 24, Slot20ExtendItem.D1, (byte) 5, 250.0f),
    QUADAR_D1("QUADAR_D1", 24, (byte) 25, Slot20ExtendItem.D1, (byte) 4, 100.0f),
    TRIPLE_D1("TRIPLE_D1", 25, (byte) 26, Slot20ExtendItem.D1, (byte) 3, 20.0f),

    PENTA_C("PENTA_C", 26, (byte) 27, Slot20ExtendItem.C, (byte) 5, 200.0f),
    QUADAR_C("QUADAR_C", 27, (byte) 28, Slot20ExtendItem.C, (byte) 4, 35.0f),
    TRIPLE_C("TRIPLE_C", 28, (byte) 29, Slot20ExtendItem.C, (byte) 3, 10.0f),

    PENTA_B2("PENTA_B2", 29, (byte) 30, Slot20ExtendItem.B2, (byte) 5, 150.0f),
    QUADAR_B2("QUADAR_B2", 30, (byte) 31, Slot20ExtendItem.B2, (byte) 4, 30.0f),
    TRIPLE_B2("TRIPLE_B2", 31, (byte) 32, Slot20ExtendItem.B2, (byte) 3, 5.0f),

    PENTA_B1("PENTA_B1", 29, (byte) 30, Slot20ExtendItem.B1, (byte) 5, 150.0f),
    QUADAR_B1("QUADAR_B1", 30, (byte) 31, Slot20ExtendItem.B1, (byte) 4, 30.0f),
    TRIPLE_B1("TRIPLE_B1", 31, (byte) 32, Slot20ExtendItem.B1, (byte) 3, 5.0f),

    PENTA_A2("PENTA_A", 32, (byte) 33, Slot20ExtendItem.A2, (byte) 5, 50.0f),
    QUADAR_A2("QUADAR_A", 33, (byte) 34, Slot20ExtendItem.A2, (byte) 4, 20.0f),
    TRIPLE_A2("TRIPLE_A", 34, (byte) 35, Slot20ExtendItem.A2, (byte) 3, 5.0f),

    PENTA_A1("PENTA_A1", 32, (byte) 33, Slot20ExtendItem.A1, (byte) 5, 50.0f),
    QUADAR_A1("QUADAR_A1", 33, (byte) 34, Slot20ExtendItem.A1, (byte) 4, 20.0f),
    TRIPLE_A1("TRIPLE_A1", 34, (byte) 35, Slot20ExtendItem.A1, (byte) 3, 5.0f);

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

