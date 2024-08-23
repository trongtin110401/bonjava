/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  org.jboss.netty.util.internal.ConcurrentHashMap
 *  org.json.JSONObject
 */
package game.xocdia.entities;

import game.xocdia.utils.MsgUtils;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class GamePot {
    // pot id
    public byte id;
    // hệ số nhân nếu thắng
    public double ratio;
    // pot name
    public String name;
    // trong trường hợp người chơi bị giới hạn đặt cược THÌ không đợc đặt cược quá giá trị maxMoneyBet
    public long maxMoneyBet;
    // danh sách đặt cược của user (bao gồm cả BOT) trên cửa này
    public BlockingDeque<BettingModel> betList;
    // ánh xạ giữa username và số tiền đặt cược của user đó (bao gồm cả BOT) trên cửa này
    public Map<String, Long> betMap;
    public Map<String, Long> userBetMap;
    public Map<String, RefundModel> refundMap;
    public long moneyRefund;
    public long totalMoney;
    public boolean isWin;
    // public boolean isLock;
    public boolean regisChangeLock;
    public long totalMoneyBotBet;
    public long totalMoneyUserBet;
    public long totalMoneyBotBuy;
    public long totalMoneyUserBuy;

    public GamePot(byte id, double ratio, String name, long maxMoneyBet) {
        this.id = id;
        this.ratio = ratio;
        this.name = name;
        this.maxMoneyBet = maxMoneyBet;
        this.betList = new LinkedBlockingDeque<BettingModel>();
        this.betMap = new ConcurrentHashMap();
        this.userBetMap = new ConcurrentHashMap();
        this.refundMap = new HashMap<String, RefundModel>();
        this.moneyRefund = 0L;
        this.totalMoney = 0L;
        this.isWin = false;
        //this.isLock = false;
        this.regisChangeLock = false;
        this.totalMoneyBotBet = 0L;
        this.totalMoneyUserBet = 0L;
        this.totalMoneyBotBuy = 0L;
        this.totalMoneyUserBuy = 0L;
    }

    public void reset() {
        this.betList.clear();
        this.betMap.clear();
        this.userBetMap.clear();
        this.refundMap.clear();
        this.moneyRefund = 0L;
        this.totalMoney = 0L;
        this.isWin = false;
        if (this.regisChangeLock) {
            //  this.isLock = !this.isLock;
        }
        this.regisChangeLock = false;
        this.totalMoneyBotBet = 0L;
        this.totalMoneyUserBet = 0L;
        this.totalMoneyBotBuy = 0L;
        this.totalMoneyUserBuy = 0L;
    }

    public boolean checkBetLimitUser(String nickname, boolean bLimit) {
        if (bLimit) {
            long moneyUserBet;
            long l = moneyUserBet = this.betMap.containsKey(nickname) ? this.betMap.get(nickname) : 0L;
            if (moneyUserBet >= this.maxMoneyBet) {
                return true;
            }
        }
        return false;
    }

    public synchronized long bet(String nickname, long money, boolean bLimit, int moneyType, boolean isBot) {
        try {

            if (money > 0L) {
                this.betList.offer(new BettingModel(nickname, money, isBot));
                if (this.betMap.containsKey(nickname)) {
                    this.betMap.put(nickname, this.betMap.get(nickname) + money);
                } else {
                    this.betMap.put(nickname, money);
                }
                this.totalMoney += money;
                if (isBot) {
                    this.totalMoneyBotBet += money;
                } else {
                    this.totalMoneyUserBet += money;
                    if (this.userBetMap.containsKey(nickname)) {
                        this.userBetMap.put(nickname, this.userBetMap.get(nickname) + money);
                    } else {
                        this.userBetMap.put(nickname, money);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            String content = "Xoc Dia exception: " + e.getMessage() + ", function: GamePot.bet()";
            MsgUtils.alertServer(content, false, true);
        }
        return money;
    }

    public long getMoneyBet(String nickname) {
        if (this.betMap.containsKey(nickname)) {
            return this.betMap.get(nickname);
        }
        return 0L;
    }

    public long getMoneyX2(String nickname, boolean bLimit) {
        if (this.betMap.containsKey(nickname)) {
            if (bLimit) {
                if (this.betMap.get(nickname) < this.maxMoneyBet) {
                    if (this.betMap.get(nickname) * 2L > this.maxMoneyBet) {
                        return this.maxMoneyBet - this.betMap.get(nickname);
                    }
                    return this.betMap.get(nickname);
                }
            } else {
                return this.betMap.get(nickname);
            }
        }
        return 0L;
    }


    public long getMoneyBuyRemain(boolean isBankerIsBot, int roomId, int gameId) {
        long moneyBuyRemain = 0L;
        if (isBankerIsBot) {
            if (this.totalMoneyUserBet >= this.totalMoneyUserBuy) {
                this.totalMoneyUserBet -= this.totalMoneyUserBuy;
            } else {
                this.totalMoneyUserBet = 0L;
                moneyBuyRemain = this.totalMoneyUserBuy - this.totalMoneyUserBet;
            }
        } else if (this.totalMoneyBotBet >= this.totalMoneyBotBuy) {
            this.totalMoneyBotBet -= this.totalMoneyBotBuy;
        } else {
            this.totalMoneyBotBet = 0L;
            moneyBuyRemain = this.totalMoneyBotBuy - this.totalMoneyBotBet;
        }
        return moneyBuyRemain;
    }

    public void addBetMoney(long money, boolean isBankerIsBot, int roomId, int gameId) {
        if (!isBankerIsBot) {
            this.totalMoneyBotBet += money;
        } else {
            this.totalMoneyUserBet += money;
        }
    }

    public String toString() {
        try {
            JSONObject json = this.toJSONObject();
            if (json != null) {
                return json.toString();
            }
            return "{}";
        } catch (Exception e) {
            return "{}";
        }
    }

    public JSONObject toJSONObject() {
        try {
            JSONObject json = new JSONObject();
            json.put("ratio", this.ratio);
            json.put("maxMoneyBet", this.maxMoneyBet);
            json.put("moneyRefund", this.moneyRefund);
            json.put("totalMoney", this.totalMoney);
            json.put("isWin", this.isWin);
            return json;
        } catch (Exception e) {
            return null;
        }
    }

    public String getPotName() {
        switch (name) {
            case "Chẵn":
                return "even";
            case "Lẻ":
                return "odd";
            case "4 den":
                return "zeroWhite";
            case "4 trang":
                return "fourWhite";
            case "1 den":
                return "threeWhite";
            case "1 trang":
                return "oneWhite";
        }
        return "";
    }
}

