package com.vinplay.vbee.common.response.aceModule;

import com.vinplay.vbee.common.response.BaseResponseModel;

public class AuthenACEResponse extends BaseResponseModel {
    private int user_id;
    private String user_name;
    private String display_name;
    private Long money;
    private String token;
    private String avatar ="1";
    public AuthenACEResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    public AuthenACEResponse(boolean success, String errorCode, String nickname, Long money) {
        super(success, errorCode);
        this.display_name = nickname;
        this.money = money;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    public Long getMoney() {
        return money;
    }

    public void setMoney(Long money) {
        this.money = money;
    }
}
