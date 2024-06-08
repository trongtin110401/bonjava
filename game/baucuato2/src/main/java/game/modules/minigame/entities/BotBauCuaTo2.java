/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  bitzero.util.common.business.Debug
 *  com.vinplay.dal.service.BotService
 *  com.vinplay.dal.service.impl.BotServiceImpl
 *  com.vinplay.usercore.service.UserService
 *  com.vinplay.usercore.service.impl.UserServiceImpl
 *  com.vinplay.vbee.common.models.UserModel
 *  com.vinplay.vbee.common.response.MoneyResponse
 *  com.vinplay.vbee.common.utils.DateTimeUtils
 */
package game.modules.minigame.entities;

import bitzero.util.common.business.Debug;
import com.vinplay.dal.service.BotService;
import com.vinplay.dal.service.impl.BotServiceImpl;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.config.VBeePath;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.utils.DateTimeUtils;
import game.utils.ConfigGame;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;

public class BotBauCuaTo2 {
    private static List<String> bots = new ArrayList<String>();
    private static List<String> botsVipDaily = new ArrayList<String>();
    private static List<String> botsVip = new ArrayList<String>();
    private static UserService userService = new UserServiceImpl();
    private static BotService botService = new BotServiceImpl();
    private static List<Integer> betValueDefault = Arrays.asList(5555, 6666, 7777, 8888, 9999, 6789, 11111, 22222, 33333, 44444, 55555);
    private static long[] soVinBan = new long[]{3000000L, 35000000L, 4000000L, 50000000L};
    private static long updateTime = System.currentTimeMillis();

    public static void loadData() {
        String botName;
        BotServiceImpl service;
        BufferedReader br22;
        try {
            br22 = new BufferedReader(new FileReader(VBeePath.basePath + "config/bots.txt"));
            service = new BotServiceImpl();
            while ((botName = br22.readLine()) != null) {
                try {
                    UserModel userModel = service.login(botName);
                    if (userModel.getVin() > 0)
                        bots.add(botName);
                } catch (NoSuchAlgorithmException | SQLException e) {
                    Debug.trace((Object[]) new Object[]{"Load bot " + botName + " error: ", e});
                }
            }
            Collections.shuffle(bots);
            br22.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            // empty catch block
        }
        try {
            br22 = new BufferedReader(new FileReader(VBeePath.basePath + "config/bots_vip.txt"));
            service = new BotServiceImpl();
            while ((botName = br22.readLine()) != null) {
                try {
                    UserModel userModel = service.login(botName);
                    if (userModel.getVin() > 0)
                        botsVip.add(botName);
                } catch (NoSuchAlgorithmException | SQLException e) {
                    Debug.trace((Object[]) new Object[]{"Load vip bot " + botName + " error: ", e});
                }
            }
            Collections.shuffle(botsVip);
            br22.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            // empty catch block
        }
        BotBauCuaTo2.loadBotsVip();
        Debug.trace((Object) ("TOTAL BOTS: " + bots.size()));
    }

    public static void loadBotsVip() {
        botsVipDaily.clear();
        int maxBotVip = ConfigGame.getIntValue("tx_vip_max_vin");
        int numBotVip = maxBotVip + 10;
        Random rd = new Random();
        if (numBotVip >= botsVip.size()) {
            Debug.trace((Object) "Khong the tao bot vip hang ngay");
            return;
        }
        int i = 0;
        while (i < numBotVip) {
            int n = rd.nextInt(botsVip.size());
            String bot = botsVip.get(n);
            if (bot == null) continue;
            boolean exist = false;
            for (String str : botsVipDaily) {
                if (!str.equals(bot)) continue;
                exist = true;
                break;
            }
            if (exist) continue;
            botsVipDaily.add(bot);
            ++i;
        }
    }

    public static String getRandomBot(String moneyType) {
        Random rd = new Random();
        int index = rd.nextInt(bots.size());
        String nickname = bots.get(index);
        BotBauCuaTo2.pushMoneyToBot(nickname, moneyType);
        return nickname;
    }

    private static void pushMoneyToBot(String nickname, String moneyType) {
        long currentMoney = userService.getCurrentMoneyUserCache(nickname, moneyType);
        if (currentMoney < 1000000L) {
            botService.addMoney(nickname, 10000000L, moneyType, "Chuyen tien cho bot minigame");
        } else {
            BotBauCuaTo2.banVin(nickname, moneyType, currentMoney);
        }
    }

    private static void banVin(String nickname, String moneyType, long currentMoney) {
        if (currentMoney >= 60000000L) {
            Random rd = new Random();
            int index = rd.nextInt(soVinBan.length);
            long tienBan = soVinBan[index];
            botService.addMoney(nickname, -tienBan, moneyType, "Chuyen tien");
        }
    }

    private static void pushMoneyToBotVip(String nickname, String moneyType, long moneyPushed) {
        long currentMoney = userService.getCurrentMoneyUserCache(nickname, moneyType);
        if (currentMoney < moneyPushed) {
            botService.addMoney(nickname, moneyPushed, moneyType, "Cong tien cho bot minigame");
        } else {
            BotBauCuaTo2.banVin(nickname, moneyType, currentMoney);
        }
    }

    public static List<String> getBots(int amount, String moneyType) {
        ArrayList<String> results = new ArrayList<String>();
        ArrayList<String> copyBots = new ArrayList<String>(bots);
        for (int i = 0; i < amount; ++i) {
            Random rd = new Random();
            int index = rd.nextInt(copyBots.size());
            String nickname = copyBots.get(index);
            BotBauCuaTo2.pushMoneyToBot(nickname, moneyType);
            results.add(copyBots.remove(index));
        }
        return results;
    }


    private static int randomBettingTime(int minTime, int maxTime, int phanTramVaoSom) {
        Random rd = new Random();
        int n = rd.nextInt(100);
        if (n > phanTramVaoSom) {
            int minTime5s = maxTime - 5;
            return rd.nextInt(maxTime - minTime5s) + minTime5s;
        }
        return rd.nextInt(maxTime - minTime) + minTime;
    }

    public static List<BotBauCua> getBotBauCua(int roomId) {
        String moneyType = "xu";
        if (roomId < 3) {
            moneyType = "vin";
        }
        long baseBetValue = BotBauCuaTo2.getBaseBettingBC(roomId);
        ArrayList<BotBauCua> results = new ArrayList<BotBauCua>();
        Random rd = new Random();
        int minBot = ConfigGame.getIntValue("bc_min_bot_" + roomId);
        int maxBot = ConfigGame.getIntValue("bc_max_bot_" + roomId);
        if (maxBot <= minBot || maxBot == 0) {
            return new ArrayList<BotBauCua>();
        }
        int minRatio = ConfigGame.getIntValue("bc_min_ratio_" + roomId);
        int maxRatio = ConfigGame.getIntValue("bc_max_ratio_" + roomId);
        int minBettingTime = ConfigGame.getIntValue("bc_min_betting_time");
        int maxBettingTime = ConfigGame.getIntValue("bc_max_betting_time");
        int numBots = rd.nextInt(maxBot - minBot) + minBot;
        int maxBetSide = ConfigGame.getIntValue("bc_max_bet_side");
        List<String> botsName = BotBauCuaTo2.getBots(numBots, moneyType);
        for (int i = 0; i < numBots && i < botsName.size(); ++i) {
            String nickname = botsName.get(i);
            short bettingTime = (short) BotBauCuaTo2.randomBettingTime(minBettingTime, maxBettingTime, 70);
            long[] betArr = new long[6];
            int j = maxBetSide;
            while (j > 0) {
                short betSide = (short) rd.nextInt(6);
                if (betArr[betSide] != 0L) continue;
                betArr[betSide] = getBetValue(rd.nextInt(6));
                --j;
            }
            StringBuilder builder = new StringBuilder();
            for (j = 0; j < 6; ++j) {
                builder.append(",");
                builder.append(betArr[j]);
            }
            if (builder.length() > 0) {
                builder.deleteCharAt(0);
            }
            BotBauCua bot = new BotBauCua(nickname, bettingTime, builder.toString());
            bot.setMoneyCurrent(userService.getCurrentMoneyUserCache(nickname, moneyType));
            results.add(bot);
        }
        return results;
    }

    private static long getBaseBettingBC(int roomId) {
        switch (roomId) {
            case 0:
            case 1: {
                return 1000L;
            }
            case 2:
            case 3: {
                return 10000L;
            }
            case 4: {
                return 100000L;
            }
            case 5: {
                return 1000000L;
            }
        }
        return 1000L;
    }

    private static long getBetValue(int roomId) {
        switch (roomId) {
            case 0: {
                return 1000;
            }
            case 1: {
                return 5000;
            }
            case 2: {
                return 10000L;
            }
            case 3: {
                return 50000L;
            }
            case 4: {
                return 100000L;
            }

        }
        return 100000L;
    }


    public static List<String> getBotChat() {
        int number = 0;
        Random rd = new Random();
        if (BotBauCuaTo2.isNight()) {
            int n = rd.nextInt(5);
            if (n == 0) {
                number = 1;
            }
        } else {
            number = rd.nextInt(3);
        }
        ArrayList<String> results = new ArrayList<String>();
        if (number > 0) {
            for (int i = 0; i < number; ++i) {
                int n = rd.nextInt(10);
                if (n == 0) {
                    n = rd.nextInt(botsVip.size());
                    results.add(botsVip.get(n));
                    continue;
                }
                n = rd.nextInt(bots.size());
                results.add(bots.get(n));
            }
        }
        return results;
    }

    public static boolean isNight() {
        Calendar cal = Calendar.getInstance();
        int hourOfDay = cal.get(11);
        return 2 <= hourOfDay && hourOfDay <= 8;
    }

    public static int ratioTXInNight() {
        Calendar cal = Calendar.getInstance();
        int hourOfDay = cal.get(11);
        Random rd = new Random();
        if (2 <= hourOfDay && hourOfDay <= 8) {
            switch (hourOfDay) {
                case 2:
                case 8: {
                    return rd.nextInt(20) + 80;
                }
                case 3:
                case 7: {
                    return rd.nextInt(30) + 50;
                }
                case 4:
                case 5:
                case 6: {
                    return rd.nextInt(20) + 30;
                }
            }
        }
        return 100;
    }

    public static void main(String[] args) {
        BotBauCuaTo2.isNight();
    }
}

