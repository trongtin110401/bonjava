/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum
Games {
    MINIGAME(0, "MiniGame", "Minigame"),
    MINI_POKER(1, "MiniPoker", "Mini poker"),
    TAI_XIU(2, "TaiXiu", "Tài x?u"),
    TAI_XIU_MD5(2000, "TaiXiuMd5", "Tài x?u Md5"),
    BAU_CUA(3, "BauCua", "B?u cua"),
    CAO_THAP(4, "cao_thap", "Cao th?p"),
    POKE_GO(5, "PokeGo", "Pokego"),
    CANDY(5, "CANDY", "CANDY"),
    VQMM(7, "VQMM", "Vòng quay may m?n"),
    SAM(8, "Sam", "Sâm - Sâm (Solo)"),
    BA_CAY(9, "BaCay", "Ba cây"),
    BINH(10, "Binh", "M?u binh"),
    TLMN(11, "Tlmn", "TLMN - TLMN (Solo)"),
    TA_LA(12, "TaLa", "Tá l?"),
    LIENG(13, "Lieng", "Liêng"),
    XI_TO(14, "XiTo", "Xì t?"),
    XOC_DIA(15, "XocDia", "Xóc ??a"),
    BAI_CAO(16, "BaiCao", "Bài cào"),
    POKER(17, "Poker", "Poker"),
    AVENGERS(18, "SieuAnhHung", "Siêu anh hùng"),
    MY_NHAN_NGU(19, "MyNhanNgu", "M? nhân ng?"),
    KHO_BAU(20, "KhoBau", "Kho báu"),
    NU_DIEP_VIEN(21, "NuDiepVien", "N? ?i?p viên"),
    VUONG_QUOC_VIN(22, "VuongQuocVin", "Th? Dân"),
    XI_DZACH(23, "XiDzach", "Xì Dách"),
    CARO(25, "Caro", "C? Caro"),
    CO_TUONG(26, "CoTuong", "C? T??ng"),
    CO_VUA(27, "CoVua", "C? Vua"),
    POKER_TOUR(28, "PokerTour", "Poker Tour"),
    CO_UP(29, "CoUp", "C? Úp"),
    HAM_CA_MAP(30, "HamCaMap", "Hàm Cá M?p"),
    OVER_UNDER(102, "OverUnder", "Over-Under"),
    SAMTRUYEN(130, "SamTruyen", "SamTruyen"),
    RANGE_ROVER(140, "RANGE_ROVER", "RANGE_ROVER"),
    MAYBACH(150, "MAYBACH", "MAYBACH"),
    TAMHUNG(160, "TAMHUNG", "TAMHUNG"),
    // Start slot new
    COWBOY(170, "Cowboy", "Cowboy"),
    FAST_AND_FURIOUS(180, "FastAndFurious", "Fast And Furious"),
    LADY_NIGHT(120, "LadyNight", "Lady Night"),
    CARIBE(190, "Caribe", "C??p Bi?n Caribe"),
    BONG_LAI_CAC(200, "BongLaiCac", "B?ng Lai Các"),
    HALLOWEEN(210, "Halloween", "Halloween"),
    LAS_VEGAS(220, "LasVegas", "Th?n Bài Las Vegas"),
    SEXY_DANCE(230, "SexyDance", "Sexy Dance"),
    LIEN_MINH(110, "LienMinh", "Liên Minh Huy?n Tho?i"),

    // End slot new
    TAI_XIU_VINH_DANH_BY_DAY(181, "tx_vinh_danh_day", "TAI XIU VINH DANH THEO NGAY"),
    TAI_XIU_VINH_DANH_BY_MONTH(182, "tx_vinh_danh_month", "TAI XIU VINH DANH THEO THANG"),
    TAI_XIU_MD5_VINH_DANH_BY_DAY(181, "tx_md5_vinh_danh_day", "TAI XIU MD5 VINH DANH THEO NGAY"),
    TAI_XIU_MD5_VINH_DANH_BY_MONTH(182, "tx_md5_vinh_danh_month", "TAI XIU MD5 VINH DANH THEO THANG"),
    ;

    private int id;
    private String name;
    private String description;

    static Map<Integer, Games> id2Game = new HashMap();
    static Map<String, Games> name2Game = new HashMap<>();

    private Games(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    static {
        Arrays.stream(Games.values())
                .forEach(game -> {
                    id2Game.put(game.id, game);
                    name2Game.put(game.getName(), game);
                });
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
        return id2Game.get(id);
    }

    public static Games findGameByName(String name) {
        return name2Game.get(name);
    }
}

