package com.vinplay.api.processors.momo;

public class GenBank {
    String c;
    String bank;
    int mount;

    public GenBank(String c) {
        this.c = c;
    }

    public GenBank(String c, String bank, int mount) {
        this.c = c;
        this.bank = bank;
        this.mount = mount;
    }

    public GenBank(String bank, int mount) {
        this.bank = bank;
        this.mount = mount;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public int getMount() {
        return mount;
    }

    public void setMount(int mount) {
        this.mount = mount;
    }

    @Override
    public String toString() {
        return "GenBank{" +
                "c='" + c + '\'' +
                ", bank='" + bank + '\'' +
                ", mount=" + mount +
                '}';
    }
}
