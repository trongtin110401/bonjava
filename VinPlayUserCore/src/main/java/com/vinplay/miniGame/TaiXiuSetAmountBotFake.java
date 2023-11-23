package com.vinplay.miniGame;

import com.vinplay.vbee.common.response.BaseResponseModel;

import java.io.Serializable;

public class TaiXiuSetAmountBotFake extends BaseResponseModel implements Serializable {
  int numberBotTaiFake;
  int numberBotXiuFake;

  public TaiXiuSetAmountBotFake(boolean success, String errorCode, int numberBotTaiFake, int numberBotXiuFake) {
    super(success, errorCode);
    this.numberBotTaiFake = numberBotTaiFake;
    this.numberBotXiuFake = numberBotXiuFake;
  }

  public TaiXiuSetAmountBotFake(boolean success, String errorCode) {
    super(success, errorCode);
  }

  public int getNumberBotTaiFake() {
    return numberBotTaiFake;
  }

  public void setNumberBotTaiFake(int numberBotTaiFake) {
    this.numberBotTaiFake = numberBotTaiFake;
  }

  public int getNumberBotXiuFake() {
    return numberBotXiuFake;
  }

  public void setNumberBotXiuFake(int numberBotXiuFake) {
    this.numberBotXiuFake = numberBotXiuFake;
  }
}
