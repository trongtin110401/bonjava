package com.vinplay.api.processors.accsunwin;

public class accuser1 {
    private datauser1 data;

    public accuser1(datauser1 data) {
        this.data = data;
    }

    public accuser1() {
    }

    public datauser1 getData() {
        return data;
    }

    public void setData(datauser1 data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "accuser1{" +
                "data=" + data +
                '}';
    }
}
