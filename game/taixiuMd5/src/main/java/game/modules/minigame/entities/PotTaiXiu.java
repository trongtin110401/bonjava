/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.vinplay.dal.entities.taixiu.TransactionTaiXiuDetail
 */
package game.modules.minigame.entities;

import com.vinplay.dal.entities.taixiu.TransactionTaiXiuDetail;
import com.vinplay.vbee.common.response.minigame.TaiXiuAdmin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PotTaiXiu
        extends Pot {
    private long totalBotBet = 0L;
    private int numBot = 0;
    private int BETSIDE_TAI = 1;
    // list all transaction
    public List<TransactionTaiXiuDetail> contributors = new ArrayList<TransactionTaiXiuDetail>();
    public List<TaiXiuAdmin> contributorNotBot = new ArrayList<>();
    public List<String> users = new ArrayList<String>();
    public List<String> userNotBots = new ArrayList<String>();

    public int getNumberUserNotBot() {
        return userNotBots.size();
    }

    public int getNumberUserBot() {
        return users.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void bet(TransactionTaiXiuDetail trans, boolean isBot) {
        List<String> list = this.users;
        List<TransactionTaiXiuDetail> list2 = this.contributors;
        List<TaiXiuAdmin> list3 = this.contributorNotBot;

        synchronized (list2) {
            this.contributors.add(trans);
        }

        synchronized (list3) {
            if (!Objects.equals(isBot, Boolean.TRUE)) {
                if (!checkUserExist(this.users, trans.username)) {
                    TaiXiuAdmin taiXiuAdmin = new TaiXiuAdmin(trans.username, trans.betSide, trans.betValue);
                    this.contributorNotBot.add(taiXiuAdmin);
                } else {
                    contributorNotBot.forEach(it -> {
                        if (Objects.equals(it.getUsername().trim(), trans.username.trim()))
                            it.setMoney(it.getMoney() + trans.betValue);
                    });
                }
            }
        }

        synchronized (list) {
            if (!checkUserExist(this.users, trans.username)) {
                this.users.add(trans.username);
            }
            if (!Objects.equals(isBot, Boolean.TRUE) && !checkUserExist(this.userNotBots, trans.username)) {
                this.userNotBots.add(trans.username);
            }
        }

        this.totalValue += trans.betValue;
        if (isBot) {
            this.totalBotBet += trans.betValue;
            ++this.numBot;
        }
    }

    @Override
    public void renew() {
        super.renew();
        this.contributors.clear();
        this.contributorNotBot.clear();
        this.users.clear();
        this.userNotBots.clear();
        this.totalBotBet = 0L;
        this.numBot = 0;
    }

    public long getTotalBetByUsername(String username) {
        long totalValue = 0L;
        for (TransactionTaiXiuDetail tran : this.contributors) {
            if (!tran.username.equals(username)) continue;
            totalValue += tran.betValue;
        }
        return totalValue;
    }


    public boolean checkUserExist(List<String> lst, String username) {
        return lst.contains(username.trim());
    }

    public short getNumBet() {
        return (short) this.users.size();
    }

    public long getTotalBotBet() {
        return this.totalBotBet;
    }

    public int getNumBotBet() {
        return this.numBot;
    }

    public List<TaiXiuAdmin> getContributorNotBot() {
        return contributorNotBot;
    }
}

