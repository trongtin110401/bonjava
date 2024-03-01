/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line20basic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum Slot20Item {
    NONE("NONE", 0, "NONE", (byte) -1),
    JACKPOT("JACKPOT", 1, "FREE_SPIN", (byte) 0),
    FREE_SPIN("FREE_SPIN", 2, "BONUS", (byte) 1),
    BONUS("BONUS", 3, "JACKPOT", (byte) 2),
    D("D", 4, "D", (byte) 3),
    C("C", 5, "C", (byte) 4),
    B("B", 6, "B", (byte) 5),
    A("A", 7, "A", (byte) 6);

    private String name;
    private byte id;

    private static Map<Integer, Slot20Item> map = new HashMap<>();

    Slot20Item(String s, int n2, String name, byte id) {
        this.name = name;
        this.id = id;
    }

    static {
        Arrays.stream(Slot20Item.values()).forEach(item -> {
            map.put((int) item.getId(), item);
        });
    }

    public static Slot20Item findItem(byte id) {
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



