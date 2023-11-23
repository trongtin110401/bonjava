/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.dichvuthe.service;

import com.vinplay.dichvuthe.entities.CardResponse;

public interface ExchangeService {
    public long getExchangeMoney(String var1, String var2, String var3, String var4);
    public CardResponse BuyCardFromKhoThe(String nickName,String telco, int amount, int quantity);
}

