package com.vinplay.api.processors.accv28;

public class proxy {
    private boolean success;
    private String proxy;
    private String location;
    private int next_change;
    private int timeout;

    public proxy() {
    }

    public proxy(boolean success, String proxy, String location, int next_change, int timeout) {
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

    public int getNext_change() {
        return next_change;
    }

    public void setNext_change(int next_change) {
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
        return "proxy{" +
                "success=" + success +
                ", proxy='" + proxy + '\'' +
                ", location='" + location + '\'' +
                ", next_change=" + next_change +
                ", timeout=" + timeout +
                '}';
    }
}
