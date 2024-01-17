/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line20basic;

public enum Line20Award {

    PENTA_BAG("PENTA_BAG", 0, (byte) 1, Line20Item.BAG, (byte) 5, 1000.0f),
    PENTA_POUCH("PENTA_POUCH", 1, (byte) 1, Line20Item.POUCH, (byte) 5, -2.0f),
    QUADRA_POUCH("QUADRA_POUCH", 2, (byte) 1, Line20Item.POUCH, (byte) 4, 40.0f),
    TRIPLE_POUCH("TRIPLE_POUCH", 3, (byte) 1, Line20Item.POUCH, (byte) 3, 5.0f),

    PENTA_BOOK("PENTA_BOOK", 4, (byte) 1, Line20Item.BOOK, (byte) 5, 1000.0f),
    QUADRA_BOOK("QUADRA_BOOK", 5, (byte) 1, Line20Item.BOOK, (byte) 4, -1.0f),
    TRIPLE_BOOK("TRIPLE_BOOK", 6, (byte) 1, Line20Item.BOOK, (byte) 3, 4.0f),

    PENTA_BULLSEYE("PENTA_BULLSEYE", 7, (byte) 1, Line20Item.BULLSEYE, (byte) 5, 200.0f),
    QUADRA_BULLSEYE("QUADRA_BULLSEYE", 8, (byte) 1, Line20Item.BULLSEYE, (byte) 4, 20.0f),
    TRIPLE_BULLSEYE("TRIPLE_BULLSEYE", 9, (byte) 1, Line20Item.BULLSEYE, (byte) 3, 3.0f),

    PENTA_MAP("PENTA_MAP", 10, (byte) 1, Line20Item.MAP, (byte) 5, 55.0f),
    QUADRA_MAP("QUADRA_MAP", 11, (byte) 1, Line20Item.MAP, (byte) 4, 8.0f),
    TRIPLE_MAP("TRIPLE_MAP", 12, (byte) 1, Line20Item.MAP, (byte) 3, 2.0f),

    PENTA_BOTTLE("PENTA_BOTTLE", 13, (byte) 1, Line20Item.BOTTLE, (byte) 5, 15.0f),
    QUADRA_BOTTLE("QUADRA_BOTTLE", 14, (byte) 1, Line20Item.BOTTLE, (byte) 4, 4.0f),

    PENTA_ANVIL("PENTA_ANVIL", 15, (byte) 1, Line20Item.ANVIL, (byte) 5, 8.0f),
    QUADRA_ANVIL("QUADRA_ANVIL", 16, (byte) 1, Line20Item.ANVIL, (byte) 4, 3.0f);

    private byte id;
    private Line20Item item;
    private byte duplicate;
    private float ratio;

    private Line20Award(String s, int n2, byte id, Line20Item item, byte duplicate, float ratio) {
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

