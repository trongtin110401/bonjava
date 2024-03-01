/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line20extend;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum Slot20ExtendItem {

    SCATTER("SCATTER", 1, "Scatter", (byte) 0),
    BONUS("BONUS", 2, "Bonus", (byte) 1),
    WILD("WILD", 3, "Wild", (byte) 2),
    JACKPOT("JACKPOT", 4, "Jackpot", (byte) 3),
    F("F", 6, "F", (byte) 4),
    E("E", 7, "E", (byte) 5),
    D("D", 8, "D", (byte) 6),
    C("C", 9, "C", (byte) 7),
    B("B", 10, "B", (byte) 8),
    A("A", 11, "A", (byte) 9);

    private String name;
    private byte id;

    private static Map<Integer, Slot20ExtendItem> map = new HashMap<>();

    Slot20ExtendItem(String s, int n2, String name, byte id) {
        this.name = name;
        this.id = id;
    }

    static {
        Arrays.stream(Slot20ExtendItem.values()).forEach(item -> {
            map.put((int) item.getId(), item);
        });
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

    public static Slot20ExtendItem findItem(byte id) {
        return map.get((int) id);
    }
}

