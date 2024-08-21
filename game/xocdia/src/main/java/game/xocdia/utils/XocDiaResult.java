/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  game.utils.GameUtils
 *  game.utils.NumberUtils
 *  game.xocdia.conf.XocDiaConfig
 *  game.xocdia.conf.XocDiaForceResult
 *  scala.util.Random
 */
package game.xocdia.utils;

import bitzero.util.common.business.Debug;
import com.google.gson.Gson;
import com.vinplay.dal.service.MiniGameService;
import com.vinplay.dal.service.impl.MiniGameServiceImpl;
import com.vinplay.usercore.service.CacheService;
import com.vinplay.usercore.service.impl.CacheServiceImpl;
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.exceptions.KeyNotFoundException;
import com.vinplay.vbee.common.response.BauCuaTo2.SetBauCuaKetqua;
import game.xocdia.conf.XocDiaForceResult;
import game.xocdia.entities.GamePot;
import game.xocdia.entities.GamePotReportModel;
import game.xocdia.entities.PotType;

import java.util.*;

public class XocDiaResult {

    private MiniGameService mgService = new MiniGameServiceImpl();
    private byte count;
    private List<Integer> dinces;
    private CacheService cacheService = new CacheServiceImpl();

    public XocDiaResult(byte count) {
        this.count = count;
    }

    public byte getCount() {
        return this.count;
    }

    public void setCount(byte count) {
        this.count = count;
    }

    public List<Integer> getDinces() {
        return this.dinces;
    }

    public void setDinces(List<Integer> dinces) {
        this.dinces = dinces;
    }

    public XocDiaResult() {
        this.count = 0;
        this.dinces = new ArrayList<Integer>();
        this.dinces.clear();
    }

    public long generateResult2(Vector<GamePot> potList) {

        SetBauCuaKetqua setBauCuaKetqua = null;

        try {
            try {
                setBauCuaKetqua = (SetBauCuaKetqua) cacheService.getObject("BeCauXocDia");
            } catch (KeyNotFoundException e) {
            }
            if (setBauCuaKetqua != null) {
                if (setBauCuaKetqua.getStatus().equals("be")) {
                    this.dinces = new ArrayList<>();
                    byte[] listDices = setBauCuaKetqua.getListDices();
                    dinces.add((int) listDices[0]);
                    dinces.add((int) listDices[1]);
                    dinces.add((int) listDices[2]);
                    dinces.add((int) listDices[3]);
                    this.count = (byte) dinces.stream().mapToInt(value -> value).sum();
//                    for (Integer i : this.dinces) {
//                        if (i % 2 != 0) {
//                            continue;
//                        } else {
//                            this.count = (byte) (this.count + 1);
//                        }
//
//                    }
                    cacheService.setObject("BeCauXocDia", new SetBauCuaKetqua("auto", new byte[]{}));

                    return tinhToanTienChechLech(potList);
                } else {
                    return this.autoGenerateValue2(potList);
                }
            } else {
                return this.autoGenerateValue2(potList);
            }

        } catch (Exception e) {
            Debug.trace((Object) e);
            return this.autoGenerateValue2(potList);
        }
    }

    public void autoGenerateValue(List<Integer> rsCheat, XocDiaForceResult xdForce) {
        {
            this.dinces = new ArrayList<Integer>();
            this.dinces.clear();
            this.count = 0;
            for (int i = 0; i < 4; ++i) {
                int value = (int) (Math.round(Math.random()) + 0); // random value
                if (value % 2 == 0) {
                    this.dinces.add(0);
                    this.count = (byte) (this.count + 1);
                    continue;
                }
                this.dinces.add(1);
            }
        }
    }

    public long autoGenerateValue2(Vector<GamePot> potList) {
        // tinh toan hu
        long fund = 0;
        try {
            fund = getFunValue();
        } catch (Exception e) {
            cacheService.setValue("min_fund_xd_auto", 0);
            cacheService.setValue("max_fund_xd_auto", 0);
            setFunValue(0);
        }

        ArrayList<List<Integer>> listDiceRandom = listDicesRandom();
        long tienChenhLech = 0;
        for (int index = 0; index < 16; index++) {
            // random ket qua
            this.dinces = listDiceRandom.get(index);
            this.count = 0;
            this.count = (byte) dinces.stream().mapToInt(value -> value).sum();
//            for (int i : dinces) {
//                if (i == 0) {
//                    this.count = (byte) (this.count + 1);
//                }
//            }

            tienChenhLech = tinhToanTienChechLech(potList);
            if (tienChenhLech >= 0) { // nhà cái thắng
                break;
            } else if (fund >= tienChenhLech * -1) { // Qũy vẫn còn đủ để bù lỗ
                break;
            }
        }
        return tienChenhLech;
    }

    public static ArrayList<List<Integer>> listDicesRandom() {

        List<Integer> array1 = Arrays.asList(0, 0, 0, 0);
        List<Integer> array2 = Arrays.asList(0, 0, 0, 1);
        List<Integer> array3 = Arrays.asList(0, 0, 1, 0);
        List<Integer> array4 = Arrays.asList(0, 0, 1, 1);
        List<Integer> array5 = Arrays.asList(0, 1, 0, 0);
        List<Integer> array6 = Arrays.asList(0, 1, 0, 1);
        List<Integer> array7 = Arrays.asList(0, 1, 1, 0);
        List<Integer> array8 = Arrays.asList(0, 1, 1, 1);
        List<Integer> array9 = Arrays.asList(1, 0, 0, 0);
        List<Integer> array10 = Arrays.asList(1, 0, 0, 1);
        List<Integer> array11 = Arrays.asList(1, 0, 1, 0);
        List<Integer> array12 = Arrays.asList(1, 0, 1, 1);
        List<Integer> array13 = Arrays.asList(1, 1, 0, 0);
        List<Integer> array14 = Arrays.asList(1, 1, 0, 1);
        List<Integer> array15 = Arrays.asList(1, 1, 1, 0);
        List<Integer> array16 = Arrays.asList(1, 1, 1, 1);

        ArrayList<List<Integer>> list = new ArrayList<>();
        list.add(array1);
        list.add(array2);
        list.add(array3);
        list.add(array4);
        list.add(array5);
        list.add(array6);
        list.add(array7);
        list.add(array8);
        list.add(array9);
        list.add(array10);
        list.add(array11);
        list.add(array12);
        list.add(array13);
        list.add(array14);
        list.add(array15);
        list.add(array16);
        Collections.shuffle(list);
        return list;
    }

    public long tinhToanTienChechLech(Vector<GamePot> potList) {
        try {
            long totalLai = 0;
            long totalLo = 0;
            List<Byte> listWin = getPotsWin();
            for (byte gateIndex = 0; gateIndex < 6; gateIndex++) {
                boolean isGateWin = false;
                for (Byte gateWin : listWin) {
                    if (gateIndex == gateWin) {
                        isGateWin = true;
                        break;
                    }
                }
                Map<String, Long> dataUser = potList.get(gateIndex).userBetMap;
                if (isGateWin) {
                    // tinh toan tien lo
                    for (String key : dataUser.keySet()) {
                        if (gateIndex == 0 || gateIndex == 1) {         // User đánh sấp đôi hoặc lẻ
                            totalLo += dataUser.get(key) * 2;
                        } else if (gateIndex == 2 || gateIndex == 3) { // User đánh vị tứ tử
                            totalLo += dataUser.get(key) * 16;
                        } else if (gateIndex == 4 || gateIndex == 5) {  // User đánh vị sấp 3
                            totalLo += dataUser.get(key) * 4;
                        } else {
                            // do nothing
                        }
                    }
                } else {
                    // tinh toan tien lai
                    for (String key : dataUser.keySet()) {
                        totalLai += dataUser.get(key);
                    }
                }
            }
            return totalLai - totalLo;
        } catch (Exception e) {
            return 0;
        }
    }


    private boolean checkListWinSuccess(List<Byte> listPWin) {
        List<Byte> listPotCanWin = this.getPotsWin();
        for (Byte pWin : listPotCanWin) {
            if (listPWin.contains(pWin)) continue;
            return false;
        }
        return true;
    }

    public List<Byte> getPotsWin() {
        ArrayList<Byte> potsId = new ArrayList<>();
        switch (this.count) {
            case 0: {
                potsId.add(PotType.EVEN.getId());
                potsId.add(PotType.FOUR_BLACK.getId());
                break;
            }
            case 1: {
                potsId.add(PotType.ODD.getId());
                potsId.add(PotType.ONE_WHITE.getId());
                break;
            }
            case 2: {
                potsId.add(PotType.EVEN.getId());
                break;
            }
            case 3: {
                potsId.add(PotType.ODD.getId());
                potsId.add(PotType.ONE_BLACK.getId());
                break;
            }
            case 4: {
                potsId.add(PotType.EVEN.getId());
                potsId.add(PotType.FOUR_WHITE.getId());
                break;
            }
        }
        return potsId;
    }

    public String getResult() {
        String result = "";
        switch (this.count) {
            case 0: {
                result = "zeroWhite";
                break;
            }
            case 1: {
                result = "oneWhite";
                break;
            }
            case 2: {
                result = "even";
                break;
            }
            case 3: {
                result = "threeWhite";
                break;
            }
            case 4: {
                result = "fourWhite";
                break;
            }
        }
        return result;
    }

    public long getFunValue() {
        String key = Games.XOC_DIA.getName();
        return cacheService.getValueLong(key, 0);
    }

    public void setFunValue(long value) {
        String key = Games.XOC_DIA.getName();
        cacheService.setValue(key, value);
    }
}

