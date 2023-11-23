/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.enums;

public enum Games {
    MINIGAME(0, "MiniGame", "Minigame"),
    MINI_POKER(1, "MiniPoker", "Mini poker"),
    TAI_XIU(2, "TaiXiu", "T\u00e0i x\u1ec9u"),
    BAU_CUA(3, "BauCua", "B\u1ea7u cua"),
    CAO_THAP(4, "CaoThap", "Cao th\u1ea5p"),
    POKE_GO(5, "PokeGo", "Pokego"),
    CANDY(5, "CANDY", "CANDY"),
    VQMM(7, "VQMM", "V\u00f2ng quay may m\u1eafn"),
    SAM(8, "Sam", "S\u00e2m - S\u00e2m (Solo)"),
    BA_CAY(9, "BaCay", "Ba c\u00e2y"),
    BINH(10, "Binh", "M\u1eadu binh"),
    TLMN(11, "Tlmn", "TLMN - TLMN (Solo)"),
    TA_LA(12, "TaLa", "T\u00e1 l\u1ea3"),
    LIENG(13, "Lieng", "Li\u00eang"),
    XI_TO(14, "XiTo", "X\u00ec t\u1ed1"),
    XOC_DIA(15, "XocDia", "X\u00f3c \u0111\u0129a"),
    BAI_CAO(16, "BaiCao", "B\u00e0i c\u00e0o"),
    POKER(17, "Poker", "Poker"),
    AVENGERS(18, "SieuAnhHung", "Si\u00eau anh h\u00f9ng"),
    MY_NHAN_NGU(19, "MyNhanNgu", "M\u1ef9 nh\u00e2n ng\u01b0"),
    KHO_BAU(20, "KhoBau", "Kho b\u00e1u"),
    NU_DIEP_VIEN(21, "NuDiepVien", "N\u1eef \u0111i\u1ec7p vi\u00ean"),
    VUONG_QUOC_VIN(22, "VuongQuocVin", "\u0054\u0068\u1ed5 \u0044\u00e2\u006e"),
    XI_DZACH(23, "XiDzach", "X\u00ec D\u00e1ch"),
    CARO(25, "Caro", "C\u1edd Caro"),
    CO_TUONG(26, "CoTuong", "C\u1edd T\u01b0\u1edbng"),
    CO_VUA(27, "CoVua", "C\u1edd Vua"),
    POKER_TOUR(28, "PokerTour", "Poker Tour"),
    CO_UP(29, "CoUp", "C\u1edd \u00dap"),
    HAM_CA_MAP(30, "HamCaMap", "H\u00e0m C\u00e1 M\u1eadp"),
    OVER_UNDER(102, "OverUnder", "Over-Under"),
    SPARTAN(120, "Spartan", "Spartan"),
    AUDITION(110, "Audition", "Audition"),
    SAMTRUYEN(130, "SamTruyen", "SamTruyen"),
    RANGE_ROVER(140, "RANGE_ROVER", "RANGE_ROVER"),
    MAYBACH(150, "MAYBACH", "MAYBACH"),
    TAMHUNG(160, "TAMHUNG", "TAMHUNG"),
    BENLEY(170, "BENLEY", "BENLEY"),
    ROLL_ROYE(180, "ROLL_ROYE", "ROLL_ROYE"),
    TAI_XIU_VINH_DANH_BY_DAY(181, "tx_vinh_danh_day", "TAI XIU VINH DANH THEO NGAY"),
    TAI_XIU_VINH_DANH_BY_MONTH(182, "tx_vinh_danh_month", "TAI XIU VINH DANH THEO THANG");

    private int id;
    private String name;
    private String description;

    private Games(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static Games findGameById(int id) {
        for (Games entry : Games.values()) {
            if (entry.getId() != id) continue;
            return entry;
        }
        return null;
    }

    public static Games findGameByName(String name) {
        for (Games entry : Games.values()) {
            if (!entry.getName().equalsIgnoreCase(name)) continue;
            return entry;
        }
        return null;
    }
}

