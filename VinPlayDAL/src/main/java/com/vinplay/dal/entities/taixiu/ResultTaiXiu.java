/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.dal.entities.taixiu;

import java.io.Serializable;

public class ResultTaiXiu implements Serializable {
    private static final long serialVersionUID = 1L;
    public long referenceId;
    public int result;
    public int dice1;
    public int dice2;
    public int dice3;
    public long totalTai = 0L;
    public long totalXiu = 0L;
    public int numBetTai = 0;
    public int numBetXiu = 0;
    public long totalChan = 0L;
    public long totalLe = 0L;
    public int numBetChan = 0;
    public int numBetLe = 0;
    public long totalPrize = 0L;
    public long totalRefundTai = 0L;
    public long totalRefundXiu = 0L;
    public long totalRevenue = 0L;
    public int moneyType;
    public long moneyHu;
    public int statusHu = 0;
    public String timestamp;
    public long kubetSessionId;

    public ResultTaiXiu() {
    }

    public boolean isChan() {
        return (dice1 + dice2 + dice3) % 2 == 0;
    }
}

