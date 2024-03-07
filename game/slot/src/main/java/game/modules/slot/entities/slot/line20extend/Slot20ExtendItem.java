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
    E1("E2", 7, "E2", (byte) 5),
    E2("E1", 7, "E1", (byte) 6),
    D1("D2", 8, "D2", (byte) 7),
    D2("D1", 8, "D1", (byte) 8),
    C("C", 9, "C", (byte) 9),
    B1("B2", 10, "B2", (byte) 10),
    B2("B1", 10, "B1", (byte) 11),
    A2("A2", 11, "A2", (byte) 12),
    A1("A1", 11, "A1", (byte) 13);

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

