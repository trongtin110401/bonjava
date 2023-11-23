package com.vinplay.api.processors.accnhatvip;

public class proxyx {
    private boolean success;
    private String proxy;
    private String location;
    private String next_change;
    private int timeout;

    public proxyx() {
    }

    public proxyx(boolean success, String proxy, String location, String next_change, int timeout) {
        this.success = success;
        this.proxy = proxy;
        this.location = location;
        this.next_change = next_change;
        this.timeout = timeout;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNext_change() {
        return next_change;
    }

    public void setNext_change(String next_change) {
        this.next_change = next_change;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return "proxyx{" +
                "success=" + success +
                ", proxy='" + proxy + '\'' +
                ", location='" + location + '\'' +
                ", next_change='" + next_change + '\'' +
                ", timeout=" + timeout +
                '}';
    }
}
