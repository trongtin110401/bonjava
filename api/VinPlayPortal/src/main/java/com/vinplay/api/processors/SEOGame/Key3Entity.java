package com.vinplay.api.processors.SEOGame;

public class Key3Entity {
    private String value;
    private int count;

    public Key3Entity() {
    }

    public Key3Entity(String value, int count) {
        this.value = value;
        this.count = count;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Key3Entity{" +
                "value='" + value + '\'' +
                ", count=" + count +
                '}';
    }
}
