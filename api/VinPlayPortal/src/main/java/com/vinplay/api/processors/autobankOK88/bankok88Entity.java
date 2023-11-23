package com.vinplay.api.processors.autobankOK88;

import java.util.Arrays;

public class bankok88Entity {
    private BankInfo[] list_bank;

    public bankok88Entity() {
    }

    public bankok88Entity(BankInfo[] list_bank) {
        this.list_bank = list_bank;
    }

    public BankInfo[] getList_bank() {
        return list_bank;
    }

    public void setList_bank(BankInfo[] list_bank) {
        this.list_bank = list_bank;
    }

    @Override
    public String toString() {
        return "bankok88Entity{" +
                "list_bank=" + Arrays.toString(list_bank) +
                '}';
    }
}
