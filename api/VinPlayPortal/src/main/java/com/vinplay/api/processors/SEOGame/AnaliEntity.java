package com.vinplay.api.processors.SEOGame;

public class AnaliEntity {
    private String code_dl;
    private String source;
    private String action;
    private String chiendich;
    private String ip;
    private String device;


    public AnaliEntity() {
    }

    public AnaliEntity(String code_dl, String source, String action, String chiendich, String ip, String device) {
        this.code_dl = code_dl;
        this.source = source;
        this.action = action;
        this.chiendich = chiendich;
        this.ip = ip;
        this.device = device;
    }

    public String getCode_dl() {
        return code_dl;
    }

    public void setCode_dl(String code_dl) {
        this.code_dl = code_dl;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getChiendich() {
        return chiendich;
    }

    public void setChiendich(String chiendich) {
        this.chiendich = chiendich;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    @Override
    public String toString() {
        return "AnaliEntity{" +
                "code_dl='" + code_dl + '\'' +
                ", source='" + source + '\'' +
                ", action='" + action + '\'' +
                ", chiendich='" + chiendich + '\'' +
                ", ip='" + ip + '\'' +
                ", device='" + device + '\'' +
                '}';
    }
}
