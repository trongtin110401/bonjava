package com.vinplay.vbee.common.response.minigame;

import java.io.Serializable;

public class TaiXiuAdmin implements Serializable {
  String username;
  int cuaDat;
  long money;

  public TaiXiuAdmin(String username, int cuaDat, long money) {
    this.username = username;
    this.cuaDat = cuaDat;
    this.money = money;
  }

  public TaiXiuAdmin() {
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public long getMoney() {
    return money;
  }

  public void setMoney(long money) {
    this.money = money;
  }

  public int getCuaDat() {
    return cuaDat;
  }

  public void setCuaDat(int cuaDat) {
    this.cuaDat = cuaDat;
  }
}
