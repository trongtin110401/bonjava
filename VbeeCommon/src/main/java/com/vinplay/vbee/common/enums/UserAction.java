package com.vinplay.vbee.common.enums;

import java.util.HashMap;
import java.util.Map;

public enum UserAction {

    NAP_TIEN(1, "NAP_TIEN"),
    RUT_TIEN(2, "RUT_TIEN"),
    TAI_XIU(3, "TAI_XIU"),
    TAI_XIU_MD5(4, "TAI_XIU_MD5"),
    TAI_XIU_KUBET(5, "TAI_XIU_KUBET"),
    XOC_DIA_KUBET(6, "XOC_DIA_KUBET"),
    XOC_DIA(7, "XOC_DIA"),
    BAU_CUA(8, "BAU_CUA"),
    SLOT_MACHINE(9, "SLOT_MACHINE"),
    BAN_CA(10, "BAN_CA");

    private int id;
    private String name;

    UserAction(int id, String name) {
        this.id = id;
        this.name = name;
    }

    private static final Map<Integer, UserAction> BY_ID = new HashMap<>();
    private static final Map<String, UserAction> BY_NAME = new HashMap<>();

    static {
        for (UserAction service : values()) {
            BY_ID.put(service.id, service);
            BY_NAME.put(service.name, service);
        }
    }

    /**
     * Lookup by ID
     */
    public static UserAction getById(int id) {
        return BY_ID.get(id);
    }

    /**
     * Lookup by Name
     */
    public static UserAction getByName(String name) {
        return BY_NAME.get(name);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
