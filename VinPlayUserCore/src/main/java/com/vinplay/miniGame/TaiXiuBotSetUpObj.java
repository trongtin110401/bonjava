package com.vinplay.miniGame;

import com.vinplay.vbee.common.response.BaseResponseModel;

import java.io.Serializable;

public class TaiXiuBotSetUpObj  extends BaseResponseModel implements Serializable {
  long moneyMin;
  long moneyMax;
  int numberUserTaiMax;
  int numberUserXiuMax;
  int numberUserChan;
  int numberUserLe;


  public TaiXiuBotSetUpObj(boolean success, String errorCode) {
    super(success, errorCode);
  }

  public long getMoneyMin() {
    return moneyMin;
  }

  public void setMoneyMin(long moneyMin) {
    this.moneyMin = moneyMin;
  }

  public long getMoneyMax() {
    return moneyMax;
  }

  public void setMoneyMax(long moneyMax) {
    this.moneyMax = moneyMax;
  }

  public int getNumberUserTaiMax() {
    return numberUserTaiMax;
  }

  public void setNumberUserTaiMax(int numberUserTaiMax) {
    this.numberUserTaiMax = numberUserTaiMax;
  }

  public int getNumberUserXiuMax() {
    return numberUserXiuMax;
  }

  public void setNumberUserXiuMax(int numberUserXiuMax) {
    this.numberUserXiuMax = numberUserXiuMax;
  }

  public int getNumberUserChan() {
    return numberUserChan;
  }

  public void setNumberUserChan(int numberUserChan) {
    this.numberUserChan = numberUserChan;
  }

  public int getNumberUserLe() {
    return numberUserLe;
  }

  public void setNumberUserLe(int numberUserLe) {
    this.numberUserLe = numberUserLe;
  }
}
