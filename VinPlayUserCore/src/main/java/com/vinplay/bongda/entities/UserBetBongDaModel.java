package com.vinplay.bongda.entities;

public class UserBetBongDaModel {
    public UserBetBongDa userBetBongDa;
    public KeoBongDa keoBongDa;

    public UserBetBongDaModel(UserBetBongDa userBetBongDa, KeoBongDa keoBongDa) {
        this.userBetBongDa = userBetBongDa;
        this.keoBongDa = keoBongDa;
    }

    public UserBetBongDa getUserBetBongDa() {
        return userBetBongDa;
    }

    public void setUserBetBongDa(UserBetBongDa userBetBongDa) {
        this.userBetBongDa = userBetBongDa;
    }

    public KeoBongDa getKeoBongDa() {
        return keoBongDa;
    }

    public void setKeoBongDa(KeoBongDa keoBongDa) {
        this.keoBongDa = keoBongDa;
    }
}
