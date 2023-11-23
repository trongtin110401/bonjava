/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.vqv;

public enum VQVItem {
    NONE("NONE", 0, "none", (byte)-1),
    JACKPOT("JACKPOT", 1, "jackpot", (byte)0),
    BONUS("BONUS", 2, "bonus", (byte)1),
    SCATTER("SCATTER", 3, "scatter", (byte)2),
    FISH("FISH", 4, "fish", (byte)3),
    HAT("HAT", 5, "hat", (byte)4),
    SCARF("SCARF", 6, "scarf", (byte)5),
    MASK("MASK", 7, "mask", (byte)6);
    
    private String name;
    private byte id;

    private VQVItem(String s, int n2, String name, byte id) {
        this.name = name;
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public byte getId() {
        return this.id;
    }

    public static VQVItem findItem(byte id) {
        for (VQVItem entry : VQVItem.values()) {
            if (entry.getId() != id) continue;
            return entry;
        }
        return null;
    }
}

