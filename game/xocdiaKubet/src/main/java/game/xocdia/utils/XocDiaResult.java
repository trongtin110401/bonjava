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
import com.vinplay.dal.service.MiniGameService;
import com.vinplay.dal.service.impl.MiniGameServiceImpl;
import com.vinplay.usercore.service.CacheService;
import com.vinplay.usercore.service.impl.CacheServiceImpl;
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.exceptions.KeyNotFoundException;
import com.vinplay.vbee.common.response.BauCuaTo2.SetBauCuaKetqua;
import game.xocdia.conf.XocDiaForceResult;
import game.xocdia.entities.GamePot;
import game.xocdia.entities.PotType;

import java.util.*;

public class XocDiaResult {

    private MiniGameService mgService = new MiniGameServiceImpl();
    private byte blackCount;
    private List<Integer> dinces;
    private CacheService cacheService = new CacheServiceImpl();

    public XocDiaResult(byte blackCount) {
        this.blackCount = blackCount;
    }

    public byte getBlackCount() {
        return this.blackCount;
    }

    public void setBlackCount(byte blackCount) {
        this.blackCount = blackCount;
    }

    public List<Integer> getDinces() {
        return this.dinces;
    }

    public void setDinces(List<Integer> dinces) {
        this.dinces = dinces;
    }

    public XocDiaResult() {
        this.blackCount = 0;
        this.dinces = new ArrayList<Integer>();
        this.dinces.clear();
    }

    /**
     * sinh kết quả và trả về tiền chênh lệch
     * nếu tiền chênh lệch âm, có nghĩa là nhà cái thua
     * nều tiền chênh lệch dương, có nghĩa là nhà thắng
     *
     * @param potList
     * @return
     */
    public long sinhKetQuaVaTraVeTienChenhLech(Vector<GamePot> potList, int dice1, int dice2, int dice3, int dice4) {
//        try {

            dinces.add(dice1);
            dinces.add(dice2);
            dinces.add(dice3);
            dinces.add(dice4);
            this.blackCount = (byte) dinces.stream().mapToInt(value -> value).sum();
            return 0;

//            SetBauCuaKetqua setBauCuaKetqua = checkBeCauTuCms();
//            if (setBauCuaKetqua != null && setBauCuaKetqua.getStatus().equals("be")) {  // nếu có lệnh bẻ cầu từ CMS
//                this.dinces = new ArrayList<>();
//                byte[] listDices = setBauCuaKetqua.getListDices();
//                dinces.add((int) listDices[0]);
//                dinces.add((int) listDices[1]);
//                dinces.add((int) listDices[2]);
//                dinces.add((int) listDices[3]);
//                this.blackCount = (byte) dinces.stream().mapToInt(value -> value).sum();
//                cacheService.setObject("BeCauXocDia", new SetBauCuaKetqua("auto", new byte[]{}));
//                return tinhToanTienChenhLech(potList);
//            } else { // Không có lệnh bẻ cầu từ CMS, tự động sinh kết quả
//                return this.autoGenerateValue2(potList);
//            }
//        } catch (Exception e) {
//            Debug.trace(e);
//            throw new RuntimeException(e);
//        }
    }

    /**
     * Kiểm tra xem liệu lệnh can thiệp kêt quả từ CMS không
     *
     * @return
     */
    private SetBauCuaKetqua checkBeCauTuCms() {
        SetBauCuaKetqua setBauCuaKetqua = null;
        try {
            setBauCuaKetqua = (SetBauCuaKetqua) cacheService.getObject("BeCauXocDia");
        } catch (KeyNotFoundException e) {
        }
        return setBauCuaKetqua;
    }

    public void autoGenerateValue(List<Integer> rsCheat, XocDiaForceResult xdForce) {
        {
            this.dinces = new ArrayList<Integer>();
            this.dinces.clear();
            this.blackCount = 0;
            for (int i = 0; i < 4; ++i) {
                int value = (int) (Math.round(Math.random()) + 0); // random value
                if (value % 2 == 0) {
                    this.dinces.add(0);
                    this.blackCount = (byte) (this.blackCount + 1);
                    continue;
                }
                this.dinces.add(1);
            }
        }
    }

    public long autoGenerateValue2(Vector<GamePot> potList) {
        // tinh toan hu
        long fund = getFundValue();
        ArrayList<List<Integer>> listDiceRandom = listDicesRandom();
        long tienChenhLech = 0;
        int TONG_SO_KET_QUA = 16;
        for (int i = 0; i < TONG_SO_KET_QUA; i++) {
            // random ket qua
            this.dinces = listDiceRandom.get(i);
            // tổng số vị màu đen
            this.blackCount = (byte) dinces.stream().mapToInt(value -> value).sum();
            // tính toán tiền chênh lệch
            tienChenhLech = tinhToanTienChenhLech(potList);
            if (tienChenhLech >= 0) { // nhà cái thắng
                break;
            } else if (fund >= tienChenhLech * -1) { // Qũy vẫn còn đủ để bù lỗ
                break;
            }
        }
        return tienChenhLech;
    }

    private long getFundValue() {
        try {
            return getFunValue();
        } catch (Exception e) {
            cacheService.setValue("min_fund_xd_auto", 0);
            cacheService.setValue("max_fund_xd_auto", 0);
            setFunValue(0);
            return 0;
        }
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

    public long tinhToanTienChenhLech(Vector<GamePot> potList) {
        try {
            long totalLai = 0;
            long totalLo = 0;
            int TONG_SO_CUA = 6;
            List<Byte> listGateWin = getPotsWin();
            for (byte gateId = 0; gateId < TONG_SO_CUA; gateId++) {
                boolean isGateWin = isGateWin(listGateWin, gateId);
                Map<String, Long> betWinUsers = potList.get(gateId).userBetMap;
                if (isGateWin) {
                    // tinh toan tien lo
                    for (String key : betWinUsers.keySet()) {
                        if (gateId == PotType.EVEN.getId() || gateId == PotType.ODD.getId()) {         // User đánh sấp đôi hoặc lẻ
                            totalLo += betWinUsers.get(key);
                        } else if (gateId == PotType.FOUR_BLACK.getId() || gateId == PotType.FOUR_WHITE.getId()) { // User đánh vị tứ tử
                            totalLo += betWinUsers.get(key) * 15;
                        } else if (gateId == PotType.ONE_WHITE.getId() || gateId == PotType.ONE_BLACK.getId()) {  // User đánh vị sấp 3
                            totalLo += betWinUsers.get(key) * 3;
                        }

                    }
                } else {
                    // tinh toan tien lai
                    for (String key : betWinUsers.keySet()) {
                        totalLai += betWinUsers.get(key);
                    }
                }
            }
            return totalLai - totalLo;
        } catch (Exception e) {
            return 0;
        }
    }

    private static boolean isGateWin(List<Byte> listGateWin, byte gateId) {
        boolean isGateWin = false;
        for (byte gateWinId : listGateWin) {
            if (gateId == gateWinId) {
                isGateWin = true;
                break;
            }
        }
        return isGateWin;
    }


    public List<Byte> getPotsWin() {
        ArrayList<Byte> potsId = new ArrayList<>();
        switch (this.blackCount) {
            case 0: {
                potsId.add(PotType.EVEN.getId());
                potsId.add(PotType.FOUR_WHITE.getId());
                break;
            }
            case 1: {
                potsId.add(PotType.ODD.getId());
                potsId.add(PotType.ONE_BLACK.getId());
                break;
            }
            case 2: {
                potsId.add(PotType.EVEN.getId());
                break;
            }
            case 3: {
                potsId.add(PotType.ODD.getId());
                potsId.add(PotType.ONE_WHITE.getId());
                break;
            }
            case 4: {
                potsId.add(PotType.EVEN.getId());
                potsId.add(PotType.FOUR_BLACK.getId());
                break;
            }
        }
        return potsId;
    }

    public String getResult() {
        String result = "";
        switch (this.blackCount) {
            case 0: {
                result = "fourWhite";
                break;
            }
            case 1: {
                result = "threeWhite";
                break;
            }
            case 2: {
                result = "even";
                break;
            }
            case 3: {
                result = "oneWhite";
                break;
            }
            case 4: {
                result = "zeroWhite";
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

