/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line20basic;

import game.modules.slot.entities.slot.line25basic.Line25Item;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum Line20Item {
    NONE("NONE", 0, "NONE", (byte) -1),
    JACKPOT("FREE_SPIN", 1, "FREE_SPIN", (byte) 0),
    FREE_SPIN("BONUS", 2, "BONUS", (byte) 1),
    BONUS("JACKPOT", 3, "JACKPOT", (byte) 2),
    D("D", 4, "D", (byte) 3),
    C("C", 5, "C", (byte) 4),
    B("B", 6, "B", (byte) 5),
    A("A", 7, "A", (byte) 6);

    private String name;
    private byte id;

    private static Map<Integer, Line20Item> map = new HashMap<>();

    Line20Item(String s, int n2, String name, byte id) {
        this.name = name;
        this.id = id;
    }

    static {
        Arrays.stream(Line20Item.values()).forEach(item -> {
            map.put((int) item.getId(), item);
        });
    }

    public static Line20Item findItem(byte id) {
        return map.get((int) id);
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
}



