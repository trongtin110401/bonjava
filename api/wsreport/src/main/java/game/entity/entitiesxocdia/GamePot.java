/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  org.jboss.netty.util.internal.ConcurrentHashMap
 *  org.json.JSONObject
 */
package game.entity.entitiesxocdia;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;


public class GamePot implements Serializable {
    public byte id;
    public double ratio;
    public String name;
    public long maxMoneyBet;
    public BlockingDeque<BettingModel> betList;
    public Map<String, Long> betMap;
    public Map<String, Long> userBetMap;
    public Map<String,RefundModel> refundMap;
    public long moneyRefund;
    public long totalMoney;
    public boolean isWin;
    public boolean isLock;
    public boolean regisChangeLock;
    public long totalMoneyBotBet;
    public long totalMoneyUserBet;
    public long totalMoneyBotBuy;
    public long totalMoneyUserBuy;

    public GamePot() {
    }

    public GamePot(byte id, double ratio, String name, long maxMoneyBet, BlockingDeque<BettingModel> betList, Map<String, Long> betMap, Map<String, Long> userBetMap, Map<String, RefundModel> refundMap, long moneyRefund, long totalMoney, boolean isWin, boolean isLock, boolean regisChangeLock, long totalMoneyBotBet, long totalMoneyUserBet, long totalMoneyBotBuy, long totalMoneyUserBuy) {
        this.id = id;
        this.ratio = ratio;
        this.name = name;
        this.maxMoneyBet = maxMoneyBet;
        this.betList = betList;
        this.betMap = betMap;
        this.userBetMap = userBetMap;
        this.refundMap = refundMap;
        this.moneyRefund = moneyRefund;
        this.totalMoney = totalMoney;
        this.isWin = isWin;
        this.isLock = isLock;
        this.regisChangeLock = regisChangeLock;
        this.totalMoneyBotBet = totalMoneyBotBet;
        this.totalMoneyUserBet = totalMoneyUserBet;
        this.totalMoneyBotBuy = totalMoneyBotBuy;
        this.totalMoneyUserBuy = totalMoneyUserBuy;
    }

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMaxMoneyBet() {
        return maxMoneyBet;
    }

    public void setMaxMoneyBet(long maxMoneyBet) {
        this.maxMoneyBet = maxMoneyBet;
    }

    public BlockingDeque<BettingModel> getBetList() {
        return betList;
    }

    public void setBetList(BlockingDeque<BettingModel> betList) {
        this.betList = betList;
    }

    public Map<String, Long> getBetMap() {
        return betMap;
    }

    public void setBetMap(Map<String, Long> betMap) {
        this.betMap = betMap;
    }

    public Map<String, Long> getUserBetMap() {
        return userBetMap;
    }

    public void setUserBetMap(Map<String, Long> userBetMap) {
        this.userBetMap = userBetMap;
    }

    public Map<String, RefundModel> getRefundMap() {
        return refundMap;
    }

    public void setRefundMap(Map<String, RefundModel> refundMap) {
        this.refundMap = refundMap;
    }

    public long getMoneyRefund() {
        return moneyRefund;
    }

    public void setMoneyRefund(long moneyRefund) {
        this.moneyRefund = moneyRefund;
    }

    public long getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(long totalMoney) {
        this.totalMoney = totalMoney;
    }

    public boolean isWin() {
        return isWin;
    }

    public void setWin(boolean win) {
        isWin = win;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    public boolean isRegisChangeLock() {
        return regisChangeLock;
    }

    public void setRegisChangeLock(boolean regisChangeLock) {
        this.regisChangeLock = regisChangeLock;
    }

    public long getTotalMoneyBotBet() {
        return totalMoneyBotBet;
    }

    public void setTotalMoneyBotBet(long totalMoneyBotBet) {
        this.totalMoneyBotBet = totalMoneyBotBet;
    }

    public long getTotalMoneyUserBet() {
        return totalMoneyUserBet;
    }

    public void setTotalMoneyUserBet(long totalMoneyUserBet) {
        this.totalMoneyUserBet = totalMoneyUserBet;
    }

    public long getTotalMoneyBotBuy() {
        return totalMoneyBotBuy;
    }

    public void setTotalMoneyBotBuy(long totalMoneyBotBuy) {
        this.totalMoneyBotBuy = totalMoneyBotBuy;
    }

    public long getTotalMoneyUserBuy() {
        return totalMoneyUserBuy;
    }

    public void setTotalMoneyUserBuy(long totalMoneyUserBuy) {
        this.totalMoneyUserBuy = totalMoneyUserBuy;
    }
}

