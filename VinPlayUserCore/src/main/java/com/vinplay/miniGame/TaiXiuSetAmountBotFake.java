package com.vinplay.miniGame;

import com.vinplay.vbee.common.response.BaseResponseModel;

import java.io.Serializable;

public class TaiXiuSetAmountBotFake extends BaseResponseModel implements Serializable {
  int numberBotTaiFake;
  int numberBotXiuFake;
  int numberBotChanFake;
  int numberBotLeFake;

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

  public int getNumberBotChanFake() {
    return numberBotChanFake;
  }

  public void setNumberBotChanFake(int numberBotChanFake) {
    this.numberBotChanFake = numberBotChanFake;
  }

  public int getNumberBotLeFake() {
    return numberBotLeFake;
  }

  public void setNumberBotLeFake(int numberBotLeFake) {
    this.numberBotLeFake = numberBotLeFake;
  }
}
