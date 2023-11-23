package com.vinplay.api.backend.processors.taiYou88;

public class DataDoimainResponse {
    public String name;
    public long totalCount;
    public long countPlayWeb;
    public long countDowloadIos;
    public long countDowloadAndroid;
    public long countRegisterFormtai;
    public long click_login;
    public long click_register;
    public long click_download_app;

    public DataDoimainResponse(String name, long totalCount, long countPlayWeb, long countDowloadIos, long countDowloadAndroid, long countRegisterFormtai) {
        this.name = name;
        this.totalCount = totalCount;
        this.countPlayWeb = countPlayWeb;
        this.countDowloadIos = countDowloadIos;
        this.countDowloadAndroid = countDowloadAndroid;
        this.countRegisterFormtai = countRegisterFormtai;
    }

    public DataDoimainResponse(String name, long totalCount, long countPlayWeb, long countDowloadIos, long countDowloadAndroid, long countRegisterFormtai, long click_login, long click_register, long click_download_app) {
        this.name = name;
        this.totalCount = totalCount;
        this.countPlayWeb = countPlayWeb;
        this.countDowloadIos = countDowloadIos;
        this.countDowloadAndroid = countDowloadAndroid;
        this.countRegisterFormtai = countRegisterFormtai;
        this.click_login = click_login;
        this.click_register = click_register;
        this.click_download_app = click_download_app;
    }

    public long getClick_login() {
        return click_login;
    }

    public void setClick_login(long click_login) {
        this.click_login = click_login;
    }

    public long getClick_register() {
        return click_register;
    }

    public void setClick_register(long click_register) {
        this.click_register = click_register;
    }

    public long getClick_download_app() {
        return click_download_app;
    }

    public void setClick_download_app(long click_download_app) {
        this.click_download_app = click_download_app;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getCountPlayWeb() {
        return countPlayWeb;
    }

    public void setCountPlayWeb(long countPlayWeb) {
        this.countPlayWeb = countPlayWeb;
    }

    public long getCountDowloadIos() {
        return countDowloadIos;
    }

    public void setCountDowloadIos(long countDowloadIos) {
        this.countDowloadIos = countDowloadIos;
    }

    public long getCountDowloadAndroid() {
        return countDowloadAndroid;
    }

    public void setCountDowloadAndroid(long countDowloadAndroid) {
        this.countDowloadAndroid = countDowloadAndroid;
    }

    public long getCountRegisterFormtai() {
        return countRegisterFormtai;
    }

    public void setCountRegisterFormtai(long countRegisterFormtai) {
        this.countRegisterFormtai = countRegisterFormtai;
    }
}
