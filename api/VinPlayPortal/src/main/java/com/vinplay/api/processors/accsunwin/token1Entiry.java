package com.vinplay.api.processors.accsunwin;

public class token1Entiry {
    private dataToken1Entity data;
    private long status;

    public token1Entiry() {
    }

    public token1Entiry(dataToken1Entity data, long status) {
        this.data = data;
        this.status = status;
    }

    public dataToken1Entity getData() {
        return data;
    }

    public void setData(dataToken1Entity data) {
        this.data = data;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "token1Entiry{" +
                "data=" + data +
                ", status=" + status +
                '}';
    }
}
