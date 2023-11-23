package com.vinplay.api.processors.xulylinhtinh;

public class accsun {
    private String username;
    private String pass;
    private String codedl;
    private String phone;

    public accsun() {
    }

    public accsun(String username, String pass, String codedl, String phone) {
        this.username = username;
        this.pass = pass;
        this.codedl = codedl;
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getCodedl() {
        return codedl;
    }

    public void setCodedl(String codedl) {
        this.codedl = codedl;
    }

    @Override
    public String toString() {
        return username+"-"+pass+"-"+codedl+"-"+phone;
    }
}
