/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line25basic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum Line25Item {

    SCATTER("SCATTER", 1, "Scatter", (byte) 0),
    BONUS("BONUS", 2, "Bonus", (byte) 1),
    WILD("WILD", 3, "Wild", (byte) 2),
    JACKPOT("JACK_POT", 4, "Jackpot", (byte) 3),
    G("G", 5, "G", (byte) 4),
    F("F", 6, "F", (byte) 5),
    E("E", 7, "E", (byte) 6),
    D("D", 8, "D", (byte) 7),
    C("C", 9, "C", (byte) 8),
    B("B", 10, "B", (byte) 9),
    A("A", 11, "A", (byte) 10);

    private String name;
    private byte id;

    private static Map<Integer, Line25Item> map = new HashMap<>();

    Line25Item(String s, int n2, String name, byte id) {
        this.name = name;
        this.id = id;
    }

    static {
        Arrays.stream(Line25Item.values()).forEach(item -> {
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

    public static Line25Item findItem(byte id) {
        return map.get((int) id);
    }
}

