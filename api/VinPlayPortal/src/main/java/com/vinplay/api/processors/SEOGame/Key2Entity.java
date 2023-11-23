package com.vinplay.api.processors.SEOGame;

import java.util.HashMap;

public class Key2Entity {
    private String code_dl;
    private Key3Entity key1;
    private Key3Entity key2;
    private Key3Entity key3;
    private Key3Entity key4;
    private Key3Entity key5;
    private Key3Entity key6;
    private Key3Entity key7;
    private Key3Entity key8;
    private Key3Entity key9;
    private Key3Entity key10;

    public Key2Entity() {
    }

    public Key2Entity(String code_dl, Key3Entity key1, Key3Entity key2, Key3Entity key3, Key3Entity key4, Key3Entity key5, Key3Entity key6, Key3Entity key7, Key3Entity key8, Key3Entity key9, Key3Entity key10) {
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

    public Key3Entity getKey1() {
        return key1;
    }

    public void setKey1(Key3Entity key1) {
        this.key1 = key1;
    }

    public Key3Entity getKey2() {
        return key2;
    }

    public void setKey2(Key3Entity key2) {
        this.key2 = key2;
    }

    public Key3Entity getKey3() {
        return key3;
    }

    public void setKey3(Key3Entity key3) {
        this.key3 = key3;
    }

    public Key3Entity getKey4() {
        return key4;
    }

    public void setKey4(Key3Entity key4) {
        this.key4 = key4;
    }

    public Key3Entity getKey5() {
        return key5;
    }

    public void setKey5(Key3Entity key5) {
        this.key5 = key5;
    }

    public Key3Entity getKey6() {
        return key6;
    }

    public void setKey6(Key3Entity key6) {
        this.key6 = key6;
    }

    public Key3Entity getKey7() {
        return key7;
    }

    public void setKey7(Key3Entity key7) {
        this.key7 = key7;
    }

    public Key3Entity getKey8() {
        return key8;
    }

    public void setKey8(Key3Entity key8) {
        this.key8 = key8;
    }

    public Key3Entity getKey9() {
        return key9;
    }

    public void setKey9(Key3Entity key9) {
        this.key9 = key9;
    }

    public Key3Entity getKey10() {
        return key10;
    }

    public void setKey10(Key3Entity key10) {
        this.key10 = key10;
    }

    @Override
    public String toString() {
        return "Key2Entity{" +
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
