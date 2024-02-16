/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line25extend;

public enum Slot25ExtendAward {

    PENTA_SCATTER("PENTA_SCATTER", 0, (byte) 1, Slot25ExtendItem.SCATTER, (byte) 5, 8f),
    QUADAR_SCATTER("QUADAR_SCATTER", 1, (byte) 2, Slot25ExtendItem.SCATTER, (byte) 4, 8f),

    PENTA_BONUS("PENTA_BONUS", 3, (byte) 4, Slot25ExtendItem.BONUS, (byte) 5, 0f),
    QUADAR_BONUS("QUADAR_BONUS", 4, (byte) 5, Slot25ExtendItem.BONUS, (byte) 4, 0f),
    TRIPLE_BONUS("TRIPLE_BONUS", 5, (byte) 6, Slot25ExtendItem.BONUS, (byte) 3, 0f),

    PENTA_JACKPOT("PENTA_JACK_POT", 11, (byte) 10, Slot25ExtendItem.JACKPOT, (byte) 5, 0f),
    QUADAR_JACKPOT("QUADAR_JACK_POT", 12, (byte) 11, Slot25ExtendItem.JACKPOT, (byte) 4, 200.0f),
    TRIPLE_JACKPOT("TRIPLE_JACK_POT", 13, (byte) 12, Slot25ExtendItem.JACKPOT, (byte) 3, 50.0f),
    DOUBLE_JACKPOT("DOUBLE_JACK_POT", 14, (byte) 12, Slot25ExtendItem.JACKPOT, (byte) 2, 5.0f),

    PENTA_G("PENTA_G", 14, (byte) 15, Slot25ExtendItem.G, (byte) 5, 200.0f),
    QUADAR_G("QUADAR_G", 15, (byte) 16, Slot25ExtendItem.G, (byte) 4, 100.0f),
    TRIPLE_G("TRIPLE_G", 16, (byte) 17, Slot25ExtendItem.G, (byte) 3, 15.0f),
    DOUBLE_G("DOUBLE_G", 16, (byte) 17, Slot25ExtendItem.G, (byte) 2, 2.0f),

    PENTA_F("PENTA_F", 17, (byte) 18, Slot25ExtendItem.F, (byte) 5, 150.0f),
    QUADAR_F("QUADAR_F", 18, (byte) 19, Slot25ExtendItem.F, (byte) 4, 55.0f),
    TRIPLE_F("TRIPLE_F", 19, (byte) 20, Slot25ExtendItem.F, (byte) 3, 10.0f),
    DOUBLE_F("DOUBLE_F", 19, (byte) 20, Slot25ExtendItem.F, (byte) 2, 2.0f),

    PENTA_E("PENTA_E", 20, (byte) 21, Slot25ExtendItem.E, (byte) 5, 100.0f),
    QUADAR_E("QUADAR_E", 21, (byte) 22, Slot25ExtendItem.E, (byte) 4, 40.0f),
    TRIPLE_E("TRIPLE_E", 22, (byte) 23, Slot25ExtendItem.E, (byte) 3, 10.0f),
    DOUBLE_E("DOUBLE_E", 22, (byte) 23, Slot25ExtendItem.E, (byte) 2, 2.0f),

    PENTA_D("PENTA_D", 23, (byte) 24, Slot25ExtendItem.D, (byte) 5, 70.0f),
    QUADAR_D("QUADAR_D", 24, (byte) 25, Slot25ExtendItem.D, (byte) 4, 30.0f),
    TRIPLE_D("TRIPLE_D", 25, (byte) 26, Slot25ExtendItem.D, (byte) 3, 5.0f),

    PENTA_C("PENTA_C", 26, (byte) 27, Slot25ExtendItem.C, (byte) 5, 55.0f),
    QUADAR_C("QUADAR_C", 27, (byte) 28, Slot25ExtendItem.C, (byte) 4, 20.0f),
    TRIPLE_C("TRIPLE_C", 28, (byte) 29, Slot25ExtendItem.C, (byte) 3, 5.0f),

    PENTA_B("PENTA_B", 29, (byte) 30, Slot25ExtendItem.B, (byte) 5, 40.0f),
    QUADAR_B("QUADAR_B", 30, (byte) 31, Slot25ExtendItem.B, (byte) 4, 15.0f),
    TRIPLE_B("TRIPLE_B", 31, (byte) 32, Slot25ExtendItem.B, (byte) 3, 3.0f),

    PENTA_A("PENTA_A", 32, (byte) 33, Slot25ExtendItem.A, (byte) 5, 30.0f),
    QUADAR_A("QUADAR_A", 33, (byte) 34, Slot25ExtendItem.A, (byte) 4, 10.0f),
    TRIPLE_A("TRIPLE_A", 34, (byte) 35, Slot25ExtendItem.A, (byte) 3, 3.0f);

    private byte id;
    private Slot25ExtendItem item;
    private byte duplicate;
    private float ratio;

    Slot25ExtendAward(String name, int index, byte id, Slot25ExtendItem item, byte duplicate, float ratio) {
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

    public void setItem(Slot25ExtendItem item) {
        this.item = item;
    }

    public Slot25ExtendItem getItem() {
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

