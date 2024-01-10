/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.avengers;

public enum AvengersFreeSpinAward {
    PENTA_SCATTER("PENTA_SCATTER", 0, (byte) 1, AvengersItem.SCATTER, (byte) 5, 0.0f),
    QUADAR_SCATTER("QUADAR_SCATTER", 1, (byte) 2, AvengersItem.SCATTER, (byte) 4, 0.0f),
    TRIPLE_SCATTER("TRIPLE_SCATTER", 2, (byte) 3, AvengersItem.SCATTER, (byte) 3, 0.0f),

    PENTA_BONUS("PENTA_BONUS", 3, (byte) 4, AvengersItem.BONUS, (byte) 5, 0.0f),
    QUADAR_BONUS("QUADAR_BONUS", 4, (byte) 5, AvengersItem.BONUS, (byte) 4, 0.0f),
    TRIPLE_BONUS("TRIPLE_BONUS", 5, (byte) 6, AvengersItem.BONUS, (byte) 3, 0.0f),

    PENTA_WILD("PENTA_WILD", 6, (byte) 7, AvengersItem.WILD, (byte) 5, 0.0f),
    QUADAR_WILD("QUADAR_WILD", 7, (byte) 8, AvengersItem.WILD, (byte) 4, 0.0f),
    TRIPLE_WILD("TRIPLE_WILD", 8, (byte) 9, AvengersItem.WILD, (byte) 3, 0.0f),

    PENTA_JACK_POT("PENTA_JACK_POT", 9, (byte) 10, AvengersItem.JACK_POT, (byte) 5, 200.0f),
    QUADAR_JACK_POT("QUADAR_JACK_POT", 10, (byte) 11, AvengersItem.JACK_POT, (byte) 4, 50.0f),
    TRIPLE_JACK_POT("TRIPLE_JACK_POT", 11, (byte) 12, AvengersItem.JACK_POT, (byte) 3, 15.0f),

    PENTA_G("PENTA_NAM_TAY", 14, (byte) 13, AvengersItem.NAM_TAY, (byte) 5, 500.0f),
    QUADAR_G("QUADAR_NAM_TAY", 15, (byte) 14, AvengersItem.NAM_TAY, (byte) 4, 75.0f),
    TRIPLE_G("TRIPLE_NAM_TAY", 16, (byte) 15, AvengersItem.NAM_TAY, (byte) 3, 20.0f),

    PENTA_F("PENTA_BUA", 17, (byte) 16, AvengersItem.BUA, (byte) 5, 375.0f),
    QUADAR_F("QUADAR_BUA", 18, (byte) 17, AvengersItem.BUA, (byte) 4, 60.0f),
    TRIPLE_F("TRIPLE_BUA", 19, (byte) 18, AvengersItem.BUA, (byte) 3, 16.0f),

    PENTA_E("PENTA_KHIEN", 20, (byte) 19, AvengersItem.KHIEN, (byte) 5, 275.0f),
    QUADAR_E("QUADAR_KHIEN", 21, (byte) 20, AvengersItem.KHIEN, (byte) 4, 45.0f),
    TRIPLE_E("TRIPLE_KHIEN", 22, (byte) 21, AvengersItem.KHIEN, (byte) 3, 12.0f),

    PENTA_D("PENTA_KIM_CUONg", 23, (byte) 22, AvengersItem.KIM_CUONG, (byte) 5, 150.0f),
    QUADAR_D("QUADAR_KIM_CUONg", 24, (byte) 23, AvengersItem.KIM_CUONG, (byte) 4, 30.0f),
    TRIPLE_D("TRIPLE_KIM_CUONg", 25, (byte) 24, AvengersItem.KIM_CUONG, (byte) 3, 10.0f),

    PENTA_C("PENTA_DAI_BANG", 26, (byte) 25, AvengersItem.DAI_BANG, (byte) 5, 50.0f),
    QUADAR_C("QUADAR_DAI_BANG", 27, (byte) 26, AvengersItem.DAI_BANG, (byte) 4, 25.0f),
    TRIPLE_C("TRIPLE_DAI_BANG", 28, (byte) 27, AvengersItem.DAI_BANG, (byte) 3, 5.0f),

    PENTA_B("PENTA_NGUOI_NHEN", 29, (byte) 28, AvengersItem.NGUOI_NHEN, (byte) 5, 25.0f),
    QUADAR_B("QUADAR_NGUOI_NHEN", 30, (byte) 29, AvengersItem.NGUOI_NHEN, (byte) 4, 10.0f),
    TRIPLE_B("TRIPLE_NGUOI_NHEN", 31, (byte) 30, AvengersItem.NGUOI_NHEN, (byte) 3, 3.0f),

    PENTA_A("PENTA_RADAR", 32, (byte) 31, AvengersItem.RADAR, (byte) 5, 10.0f),
    QUADAR_A("QUADAR_RADAR", 33, (byte) 32, AvengersItem.RADAR, (byte) 4, 5.0f),
    TRIPLE_A("TRIPLE_RADAR", 34, (byte) 33, AvengersItem.RADAR, (byte) 3, 2.0f);

    private byte id;
    private AvengersItem item;
    private byte duplicate;
    private float ratio;

    private AvengersFreeSpinAward(String s, int n2, byte id, AvengersItem item, byte duplicate, float ratio) {
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

    public void setItem(AvengersItem item) {
        this.item = item;
    }

    public AvengersItem getItem() {
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

