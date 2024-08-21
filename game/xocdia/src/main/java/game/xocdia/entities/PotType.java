/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  game.xocdia.conf.XocDiaConfig
 */
package game.xocdia.entities;

import game.xocdia.conf.XocDiaConfig;

public enum PotType {
    EVEN((byte)0, 2.0, XocDiaConfig.maxBetChanLe, "Chẵn"),
    ODD((byte)1, 2.0, XocDiaConfig.maxBetChanLe, "Lẻ"),
    FOUR_BLACK  ((byte)2, XocDiaConfig.ratio4, XocDiaConfig.maxBet4, "4 den"),
    FOUR_WHITE((byte)3, XocDiaConfig.ratio4, XocDiaConfig.maxBet4, "4 trang"),
    ONE_BLACK((byte)4, XocDiaConfig.ratio1, XocDiaConfig.maxBet1, "1 den"),
    ONE_WHITE ((byte)5, XocDiaConfig.ratio1, XocDiaConfig.maxBet1, "1 trang");
    
    private byte id;
    private double ratio;
    private double maxRatioBet;
    private String name;

    private PotType(byte id, double ratio, double maxRatioBet, String name) {
        this.id = id;
        this.ratio = ratio;
        this.maxRatioBet = maxRatioBet;
        this.name = name;
    }

    public byte getId() {
        return this.id;
    }

    public double getRatio() {
        return this.ratio;
    }

    public String getName() {
        return this.name;
    }

    public double getMaxRatioBet() {
        return this.maxRatioBet;
    }

    public static PotType findPotType(int id) {
        for (PotType entry : PotType.values()) {
            if (entry.getId() != id) continue;
            return entry;
        }
        return null;
    }
}

