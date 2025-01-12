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
    TAI_XIU(2, "TaiXiu", "T�i x?u"),
    TAI_XIU_MD5(2000, "TaiXiuMd5", "T�i x?u Md5"),
    TAI_XIU_KUBET(2001, "TaiXiuKubet", "Tài Xỉu Kubet"),
    BAU_CUA(3, "BauCua", "B?u cua"),
    CAO_THAP(4, "cao_thap", "Cao th?p"),
    POKE_GO(5, "PokeGo", "Pokego"),
    CANDY(5, "CANDY", "CANDY"),
    VQMM(7, "VQMM", "V�ng quay may m?n"),
    SAM(8, "Sam", "S�m - S�m (Solo)"),
    BA_CAY(9, "BaCay", "Ba c�y"),
    BINH(10, "Binh", "M?u binh"),
    TLMN(11, "Tlmn", "TLMN - TLMN (Solo)"),
    TA_LA(12, "TaLa", "T� l?"),
    LIENG(13, "Lieng", "Li�ng"),
    XI_TO(14, "XiTo", "X� t?"),
    XOC_DIA(15, "XocDia", "X�c ??a"),
    XOC_DIA_KUBET(1500, "XocDiaKubet", "Xóc Đĩa Kubet"),
    BAI_CAO(16, "BaiCao", "B�i c�o"),
    POKER(17, "Poker", "Poker"),
    AVENGERS(18, "SieuAnhHung", "Si�u anh h�ng"),
    MY_NHAN_NGU(19, "MyNhanNgu", "M? nh�n ng?"),
    KHO_BAU(20, "KhoBau", "Kho b�u"),
    NU_DIEP_VIEN(21, "NuDiepVien", "N? ?i?p vi�n"),
    VUONG_QUOC_VIN(22, "VuongQuocVin", "Th? D�n"),
    XI_DZACH(23, "XiDzach", "X� D�ch"),
    CARO(25, "Caro", "C? Caro"),
    CO_TUONG(26, "CoTuong", "C? T??ng"),
    CO_VUA(27, "CoVua", "C? Vua"),
    POKER_TOUR(28, "PokerTour", "Poker Tour"),
    CO_UP(29, "CoUp", "C? �p"),
    HAM_CA_MAP(30, "HamCaMap", "H�m C� M?p"),
    OVER_UNDER(102, "OverUnder", "Over-Under"),
    SAMTRUYEN(130, "SamTruyen", "SamTruyen"),
    RANGE_ROVER(140, "RANGE_ROVER", "RANGE_ROVER"),
    MAYBACH(150, "MAYBACH", "MAYBACH"),
    TAMHUNG(160, "TAMHUNG", "TAMHUNG"),
    // Start slot new
    COWBOY(170, "Cowboy", "Cowboy"),
    FAST_AND_FURIOUS(180, "FastAndFurious", "Fast And Furious"),
    LADY_NIGHT(120, "LadyNight", "Lady Night"),
    BIG_CITY_BOY(190, "BigCityBoy", "Big City Boy"),
    BONG_LAI_CAC(200, "BongLaiCac", "B?ng Lai C�c"),
    HALLOWEEN(210, "Halloween", "Halloween"),
    LAS_VEGAS(220, "LasVegas", "Thần bài Las Vegas"),
    SEXY_DANCE(230, "SexyDance", "Sexy Dance"),
    LIEN_MINH(110, "LienMinh", "Li�n Minh Huy?n Tho?i"),

    // End slot new
    TAI_XIU_VINH_DANH_BY_DAY(181, "tx_vinh_danh_day", "TAI XIU VINH DANH THEO NGAY"),
    TAI_XIU_VINH_DANH_BY_MONTH(182, "tx_vinh_danh_month", "TAI XIU VINH DANH THEO THANG"),
    TAI_XIU_MD5_VINH_DANH_BY_DAY(181, "tx_md5_vinh_danh_day", "TAI XIU MD5 VINH DANH THEO NGAY"),
    TAI_XIU_MD5_VINH_DANH_BY_MONTH(182, "tx_md5_vinh_danh_month", "TAI XIU MD5 VINH DANH THEO THANG"),
    TAI_XIU_KUBET_VINH_DANH_BY_DAY(181, "tx_kubet_vinh_danh_day", "TAI XIU KUBET VINH DANH THEO NGAY"),
    TAI_XIU_KUBET_VINH_DANH_BY_MONTH(182, "tx_kubet_vinh_danh_month", "TAI XIU KUBET VINH DANH THEO THANG"),
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

