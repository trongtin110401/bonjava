package com.vinplay.api.processors.SEOGame;

public class ThongKeEntity {
    private String code_dl;
    private int key1;
    private int key2;
    private int key3;
    private int key4;
    private int key5;
    private int key6;
    private int key7;
    private int key8;
    private int key9;
    private int key10;

    public ThongKeEntity() {
    }

    public ThongKeEntity(String code_dl, int key1, int key2, int key3, int key4, int key5, int key6, int key7, int key8, int key9, int key10) {
        this.code_dl = code_dl;
        this.key1 = key1;
        this.key2 = key2;
        this.key3 = key3;
        this.key4 = key4;
        this.key5 = key5;
        this.key6 = key6;
        this.key7 = key7;
        this.key8 = key8;
        this.key9 = key9;
        this.key10 = key10;
    }

    public String getCode_dl() {
        return code_dl;
    }

    public void setCode_dl(String code_dl) {
        this.code_dl = code_dl;
    }

    public int getKey1() {
        return key1;
    }

    public void setKey1(int key1) {
        this.key1 = key1;
    }

    public int getKey2() {
        return key2;
    }

    public void setKey2(int key2) {
        this.key2 = key2;
    }

    public int getKey3() {
        return key3;
    }

    public void setKey3(int key3) {
        this.key3 = key3;
    }

    public int getKey4() {
        return key4;
    }

    public void setKey4(int key4) {
        this.key4 = key4;
    }

    public int getKey5() {
        return key5;
    }

    public void setKey5(int key5) {
        this.key5 = key5;
    }

    public int getKey6() {
        return key6;
    }

    public void setKey6(int key6) {
        this.key6 = key6;
    }

    public int getKey7() {
        return key7;
    }

    public void setKey7(int key7) {
        this.key7 = key7;
    }

    public int getKey8() {
        return key8;
    }

    public void setKey8(int key8) {
        this.key8 = key8;
    }

    public int getKey9() {
        return key9;
    }

    public void setKey9(int key9) {
        this.key9 = key9;
    }

    public int getKey10() {
        return key10;
    }

    public void setKey10(int key10) {
        this.key10 = key10;
    }

    @Override
    public String toString() {
        return "ThongKeEntity{" +
                "code_dl='" + code_dl + '\'' +
                ", key1=" + key1 +
                ", key2=" + key2 +
                ", key3=" + key3 +
                ", key4=" + key4 +
                ", key5=" + key5 +
                ", key6=" + key6 +
                ", key7=" + key7 +
                ", key8=" + key8 +
                ", key9=" + key9 +
                ", key10=" + key10 +
                '}';
    }
}
