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

    public void generateResult2(List<Integer> rsCheat, XocDiaForceResult xdForce, Vector<GamePot> potList) {

        SetBauCuaKetqua setBauCuaKetqua = null;

        try {
            try {
                setBauCuaKetqua = (SetBauCuaKetqua) cacheService.getObject("BeCauXocDia");
            } catch (KeyNotFoundException e) {

            }
            if (setBauCuaKetqua != null) {
                if (setBauCuaKetqua.getStatus().equals("be")) {
                    this.dinces = new ArrayList<Integer>();
                    byte[] listDices = setBauCuaKetqua.getListDices();
                    dinces.add(Integer.valueOf(listDices[0]));
                    dinces.add(Integer.valueOf(listDices[1]));
                    dinces.add(Integer.valueOf(listDices[2]));
                    dinces.add(Integer.valueOf(listDices[3]));
                    this.count = 0;
                    for (Integer i : this.dinces) {
                        if (i % 2 != 0) {
                            continue;
                        } else {
                            this.count = (byte) (this.count + 1);
                        }

                    }
                    cacheService.setObject("BeCauXocDia", new SetBauCuaKetqua("auto", new byte[]{}));
                } else {
                    this.autoGenerateValue2(rsCheat, xdForce, potList);
                }
            } else {
                this.autoGenerateValue2(rsCheat, xdForce, potList);
            }

        } catch (Exception e) {
            Debug.trace((Object) e);
            this.autoGenerateValue2(rsCheat, xdForce, potList);
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

    public void generateResult(List<Integer> rsCheat, XocDiaForceResult xdForce) {

        SetBauCuaKetqua setBauCuaKetqua = null;

        List<Integer> isDinces = null;
        try {
            try {
                setBauCuaKetqua = (SetBauCuaKetqua) cacheService.getObject("BeCauXocDia");
            } catch (KeyNotFoundException e) {

            }
            if (setBauCuaKetqua != null) {
                if (setBauCuaKetqua.getStatus().equals("be")) {
                    this.dinces = new ArrayList<Integer>();
                    byte[] listDices = setBauCuaKetqua.getListDices();
                    dinces.add(Integer.valueOf(listDices[0]));
                    dinces.add(Integer.valueOf(listDices[1]));
                    dinces.add(Integer.valueOf(listDices[2]));
                    dinces.add(Integer.valueOf(listDices[3]));
                    this.count = 0;
                    for (Integer i : this.dinces) {
                        if (i % 2 != 0) {
                            continue;
                        } else {
                            this.count = (byte) (this.count + 1);
                        }

                    }
                    cacheService.setObject("BeCauXocDia", new SetBauCuaKetqua("auto", new byte[]{}));
                } else {
                    this.autoGenerateValue(rsCheat, xdForce);
                }
            } else {
                this.autoGenerateValue(rsCheat, xdForce);
            }

        } catch (Exception e) {
            Debug.trace((Object) e);
            this.autoGenerateValue(rsCheat, xdForce);
        }
    }

    public void autoGenerateValue2(List<Integer> rsCheat, XocDiaForceResult xdForce, Vector<GamePot> potList) {
        //tinh toan hu
        long fund = 0;
        try {
            String hu_tx = cacheService.getValueStr("fund_xd_auto");
            fund = Long.parseLong(hu_tx);
        } catch (Exception e) {
            cacheService.setValue("min_fund_xd_auto", 0);
            cacheService.setValue("max_fund_xd_auto", 0);
            cacheService.setValue("fund_xd_auto", 0);
        }
        ArrayList<List<Integer>> listDiceRandom = listDicesRandom();
        for (int index = 0; index < 16; index++) {
            //random ket qua
            this.dinces = listDiceRandom.get(index);
            this.count = 0;
            for (Integer i : dinces) {
                if (i % 2 == 0) {
                    this.count = (byte) (this.count + 1);
                }
            }
//            this.dinces.clear();
//            this.count = 0;
//            for (int i = 0; i < 4; ++i) {
//                int value = (int) (Math.round(Math.random()) + 0); // random value
//                if (value % 2 == 0) {
//                    this.dinces.add(0);
//                    this.count = (byte) (this.count + 1);
//                    continue;
//                }
//                this.dinces.add(1);
//            }
            //ket thuc ket qua random
            // tinh toan lai lo
            //tong tien thang thua
            try {
                long chenhLechTien = tinhToanTienChechLech(potList);
//                if (maxHu > minHu) {
//                    //neu ma hu am
//                    if (fund + chenhLechTien < minHu && chenhLechTien < 0) {
//                        //random lai tiep
//                        continue;
//                    } else if (fund + chenhLechTien > maxHu && chenhLechTien > 0) {
//                        //random tiep de be lai
//                        continue;
//                    } else {
//                        //khong can phai random nua
//                        break;
//                    }
//                } else {
//                    //khong can phai random
//                    break;
//                }

                if (chenhLechTien >= 0) {
                    break;
                } else if (fund >= chenhLechTien) {
                    break;
                } else {
                    continue;
                }

            } catch (Exception e) {
                break;
            }
        }

        //random xong cong tien lai hu xem dung k
        try {
            long chenhLechTien = tinhToanTienChechLech(potList);
            String hu_tx = cacheService.getValueStr("fund_xd_auto");
            fund = Long.parseLong(hu_tx);
            fund = chenhLechTien + fund;


            long updateFund = 0;
            try {
                updateFund = Long.parseLong(cacheService.getValueStr("update_fund_xd_auto"));
            } catch (Exception e) {
                updateFund = 0;
            } finally {
                cacheService.setValue("update_fund_xd_auto", 0);
            }
            fund += updateFund;
            mgService.saveFund(Games.XOC_DIA.getName(), fund);
            cacheService.setValue("fund_xd_auto", String.valueOf(fund));
        } catch (Exception e) {
            e.printStackTrace();
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

    public long tinhToanTienChechLech(Vector<GamePot> potList) {
        try {
            long chenhLechTien = 0;
            long totalLai = 0;
            long totalLo = 0;
            List<Byte> listWin = getPotsWin();
            for (int indexDoor = 0; indexDoor < 6; indexDoor++) {
                boolean checkDoorWin = false;
                for (Byte doorWin : listWin) {
                    int door = doorWin.intValue();
                    if (indexDoor == door) {
                        checkDoorWin = true;
                        break;
                    }
                }
                if (checkDoorWin) {
                    //tinh toan tien lo
                    Map<String, Long> dataUser = potList.get(indexDoor).userBetMap;
                    for (String key : dataUser.keySet()) {
                        if (indexDoor == 0 || indexDoor == 1) {         // s?p ?ôi
                            totalLo += dataUser.get(key) * 2;
                        } else if (indexDoor == 2 || indexDoor == 3) { // t? t?
                            totalLo += dataUser.get(key) * 16;
                        } else if (indexDoor == 4 || indexDoor == 5) {  // s?p 3
                            totalLo += dataUser.get(key) * 4;
                        }
                    }
                }

                //tinh toan tien lai
                Map<String, Long> dataUser = potList.get(indexDoor).userBetMap;
                for (String key : dataUser.keySet()) {
                    totalLai += dataUser.get(key);
                }
            }
            chenhLechTien = totalLai - totalLo;
            return chenhLechTien;
        } catch (Exception e) {
            //cacheService.setValue("loi_fund_xd_auto",e.getMessage()+" loi me o cho set moneu roi");
            e.getMessage();
        }
        return 11111;
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
}

