/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line20basic;

public enum Line20Award {

    PENTA_BONUS("PENTA_BAG", 0, (byte) 1, Line20Item.FREE_SPIN, (byte) 5, 1000.0f),
    QUADRA_BONUS("PENTA_BAG", 0, (byte) 1, Line20Item.FREE_SPIN, (byte) 5, 1000.0f),
    TRIPLE_BONUS("PENTA_BAG", 0, (byte) 1, Line20Item.FREE_SPIN, (byte) 5, 1000.0f),

    PENTA_FREE_SPIN("PENTA_JACKPOT", 1, (byte) 1, Line20Item.JACKPOT, (byte) 5, -2.0f),
    QUADRA_FREE_SPIN("QUADRA_JACKPOT", 2, (byte) 1, Line20Item.JACKPOT, (byte) 4, 40.0f),
    TRIPLE_FREE_SPIN("TRIPLE_JACKPOT", 3, (byte) 1, Line20Item.JACKPOT, (byte) 3, 5.0f),

    PENTA_JACKPOT("PENTA_JACKPOT", 4, (byte) 1, Line20Item.JACKPOT, (byte) 5, 0f),
    QUADRA_JACKPOT("QUADRA_JACKPOT", 5, (byte) 1, Line20Item.JACKPOT, (byte) 4, 30.0f),
    TRIPLE_JACKPOT("TRIPLE_JACKPOT", 6, (byte) 1, Line20Item.JACKPOT, (byte) 3, 5.0f),

    PENTA_D("PENTA_D", 7, (byte) 1, Line20Item.D, (byte) 5, 500.0f),
    QUADRA_D("QUADRA_D", 8, (byte) 1, Line20Item.D, (byte) 4, 20.0f),
    TRIPLE_D("TRIPLE_D", 9, (byte) 1, Line20Item.D, (byte) 3, 4.0f),

    PENTA_C("PENTA_C", 10, (byte) 1, Line20Item.C, (byte) 5, 200.0f),
    QUADRA_C("QUADRA_C", 11, (byte) 1, Line20Item.C, (byte) 4, 16.0f),
    TRIPLE_C("TRIPLE_C", 12, (byte) 1, Line20Item.C, (byte) 3, 3.0f),

    PENTA_B("PENTA_B", 13, (byte) 1, Line20Item.B, (byte) 5, 75.0f),
    QUADRA_B("QUADRA_B", 14, (byte) 1, Line20Item.B, (byte) 4, 10.0f),
    TRIPPE_B("TRIPPE_B", 15, (byte) 1, Line20Item.B, (byte) 3, 2.0f),

    PENTA_A("PENTA_A", 16, (byte) 1, Line20Item.A, (byte) 5, 30.0f),
    QUADRA_A("QUADRA_A", 17, (byte) 1, Line20Item.A, (byte) 4, 5.0f);

    private byte id;
    private Line20Item item;
    private byte duplicate;
    private float ratio;

    Line20Award(String s, int n2, byte id, Line20Item item, byte duplicate, float ratio) {
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

    public void setItem(Line20Item item) {
        this.item = item;
    }

    public Line20Item getItem() {
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

