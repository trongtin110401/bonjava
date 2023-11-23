package com.vinplay.api.processors.cashout;

import java.util.Arrays;

public class MomoEnty {
    private boolean isError;
    private String message;
    private Mo2Enty[] data;

    public MomoEnty() {
    }

    public MomoEnty(boolean isError, String message, Mo2Enty[] data) {
        this.isError = isError;
        this.message = message;
        this.data = data;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Mo2Enty[] getData() {
        return data;
    }

    public void setData(Mo2Enty[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "MomoEnty{" +
                "isError=" + isError +
                ", message='" + message + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
