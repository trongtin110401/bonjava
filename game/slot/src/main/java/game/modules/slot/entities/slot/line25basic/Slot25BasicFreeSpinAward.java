/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line25basic;

public enum Slot25BasicFreeSpinAward {

    PENTA_SCATTER("PENTA_SCATTER", 0, (byte) 1, SlotBasic25Item.SCATTER, (byte) 5, 22f),
    QUADAR_SCATTER("QUADAR_SCATTER", 1, (byte) 2, SlotBasic25Item.SCATTER, (byte) 4, 8f),
    TRIPLE_SCATTER("TRIPLE_SCATTER", 2, (byte) 3, SlotBasic25Item.SCATTER, (byte) 3, 4f),

    PENTA_BONUS("PENTA_BONUS", 3, (byte) 4, SlotBasic25Item.BONUS, (byte) 5, 0f),
    QUADAR_BONUS("QUADAR_BONUS", 4, (byte) 5, SlotBasic25Item.BONUS, (byte) 4, 0f),
    TRIPLE_BONUS("TRIPLE_BONUS", 5, (byte) 6, SlotBasic25Item.BONUS, (byte) 3, 0f),

    PENTA_WILD("PENTA_WILD", 6, (byte) 7, SlotBasic25Item.WILD, (byte) 5, 5000.0f),
    QUADAR_WILD("QUADAR_WILD", 7, (byte) 8, SlotBasic25Item.WILD, (byte) 4, 1000.0f),
    TRIPLE_WILD("TRIPLE_WILD", 8, (byte) 9, SlotBasic25Item.WILD, (byte) 3, 50.0f),
    DOUBLE_WILD("TRIPLE_WILD", 9, (byte) 10, SlotBasic25Item.WILD, (byte) 2, 10.0f),

    PENTA_JACKPOT("PENTA_JACK_POT", 11, (byte) 10, SlotBasic25Item.JACKPOT, (byte) 5, -1.0f),
    QUADAR_JACKPOT("QUADAR_JACK_POT", 12, (byte) 11, SlotBasic25Item.JACKPOT, (byte) 4, 100.0f),
    TRIPLE_JACKPOT("TRIPLE_JACK_POT", 13, (byte) 12, SlotBasic25Item.JACKPOT, (byte) 3, 25.0f),
    DOUBLE_JACKPOT("DOUBLE_JACK_POT", 14, (byte) 12, SlotBasic25Item.JACKPOT, (byte) 2, 4.0f),

    PENTA_G("PENTA_G", 14, (byte) 15, SlotBasic25Item.G, (byte) 5, 500.0f),
    QUADAR_G("QUADAR_G", 15, (byte) 16, SlotBasic25Item.G, (byte) 4, 75.0f),
    TRIPLE_G("TRIPLE_G", 16, (byte) 17, SlotBasic25Item.G, (byte) 3, 20.0f),

    PENTA_F("PENTA_F", 17, (byte) 18, SlotBasic25Item.F, (byte) 5, 375.0f),
    QUADAR_F("QUADAR_F", 18, (byte) 19, SlotBasic25Item.F, (byte) 4, 60.0f),
    TRIPLE_F("TRIPLE_F", 19, (byte) 20, SlotBasic25Item.F, (byte) 3, 16.0f),

    PENTA_E("PENTA_E", 20, (byte) 21, SlotBasic25Item.E, (byte) 5, 275.0f),
    QUADAR_E("QUADAR_E", 21, (byte) 22, SlotBasic25Item.E, (byte) 4, 45.0f),
    TRIPLE_E("TRIPLE_E", 22, (byte) 23, SlotBasic25Item.E, (byte) 3, 12.0f),

    PENTA_D("PENTA_D", 23, (byte) 24, SlotBasic25Item.D, (byte) 5, 150.0f),
    QUADAR_D("QUADAR_D", 24, (byte) 25, SlotBasic25Item.D, (byte) 4, 30.0f),
    TRIPLE_D("TRIPLE_D", 25, (byte) 26, SlotBasic25Item.D, (byte) 3, 10.0f),

    PENTA_C("PENTA_C", 26, (byte) 27, SlotBasic25Item.C, (byte) 5, 50.0f),
    QUADAR_C("QUADAR_C", 27, (byte) 28, SlotBasic25Item.C, (byte) 4, 25.0f),
    TRIPLE_C("TRIPLE_C", 28, (byte) 29, SlotBasic25Item.C, (byte) 3, 5.0f),

    PENTA_B("PENTA_B", 29, (byte) 30, SlotBasic25Item.B, (byte) 5, 25.0f),
    QUADAR_B("QUADAR_B", 30, (byte) 31, SlotBasic25Item.B, (byte) 4, 10.0f),
    TRIPLE_B("TRIPLE_B", 31, (byte) 32, SlotBasic25Item.B, (byte) 3, 3.0f),

    PENTA_A("PENTA_A", 32, (byte) 33, SlotBasic25Item.A, (byte) 5, 10.0f),
    QUADAR_A("QUADAR_A", 33, (byte) 34, SlotBasic25Item.A, (byte) 4, 5.0f),
    TRIPLE_A("TRIPLE_A", 34, (byte) 35, SlotBasic25Item.A, (byte) 3, 2.0f);

    private byte id;
    private SlotBasic25Item item;
    private byte duplicate;
    private float ratio;

    Slot25BasicFreeSpinAward(String s, int n2, byte id, SlotBasic25Item item, byte duplicate, float ratio) {
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

    public void setItem(SlotBasic25Item item) {
        this.item = item;
    }

    public SlotBasic25Item getItem() {
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

