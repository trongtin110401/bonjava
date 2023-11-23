/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.response.minigame;

import com.vinplay.vbee.common.response.BaseResponseModel;

import java.util.ArrayList;
import java.util.List;

public class TaiXiuAdminReportResponse
    extends BaseResponseModel {
  long moneyTai;
  long moneyXiu;
  long nguoiChoiBetTai;
  long nguoiChoiBetXiu;
  long moneyTaiFull;
  long moneyXiuFull;
  long phienId;
  long numberUserAndBotBetTai;
  long numberUserAndBotBetXiu;
  long realTime;
  boolean bettingRound;
  List<TaiXiuAdmin> contributors;
  List<TaiXiuChatMsg> lstMsg = new ArrayList<>();
  List<String> getListChatUsers;

  public TaiXiuAdminReportResponse(boolean success, String errorCode) {
    super(success, errorCode);
  }

  public List<TaiXiuAdmin> getContributors() {
    return contributors;
  }

  public void setContributors(List<TaiXiuAdmin> contributors) {
    this.contributors = contributors;
  }

  public TaiXiuAdminReportResponse(boolean success, String errorCode, long moneyTai, long moneyXiu, long nguoiChoiBetTai, long nguoiChoiBetXiu, long moneyTaiFull, long moneyXiuFull, long phienId) {
    super(success, errorCode);
    this.moneyTai = moneyTai;
    this.moneyXiu = moneyXiu;
    this.nguoiChoiBetTai = nguoiChoiBetTai;
    this.nguoiChoiBetXiu = nguoiChoiBetXiu;
    this.moneyTaiFull = moneyTaiFull;
    this.moneyXiuFull = moneyXiuFull;
    this.phienId = phienId;
  }

  public long getMoneyTai() {
    return moneyTai;
  }

  public void setMoneyTai(long moneyTai) {
    this.moneyTai = moneyTai;
  }

  public long getMoneyXiu() {
    return moneyXiu;
  }

  public void setMoneyXiu(long moneyXiu) {
    this.moneyXiu = moneyXiu;
  }

  public long getNguoiChoiBetTai() {
    return nguoiChoiBetTai;
  }

  public void setNguoiChoiBetTai(long nguoiChoiBetTai) {
    this.nguoiChoiBetTai = nguoiChoiBetTai;
  }

  public long getNguoiChoiBetXiu() {
    return nguoiChoiBetXiu;
  }

  public void setNguoiChoiBetXiu(long nguoiChoiBetXiu) {
    this.nguoiChoiBetXiu = nguoiChoiBetXiu;
  }

  public long getMoneyTaiFull() {
    return moneyTaiFull;
  }

  public void setMoneyTaiFull(long moneyTaiFull) {
    this.moneyTaiFull = moneyTaiFull;
  }

  public long getMoneyXiuFull() {
    return moneyXiuFull;
  }

  public void setMoneyXiuFull(long moneyXiuFull) {
    this.moneyXiuFull = moneyXiuFull;
  }

  public long getPhienId() {
    return phienId;
  }

  public void setPhienId(long phienId) {
    this.phienId = phienId;
  }

  public long getNumberUserAndBotBetTai() {
    return numberUserAndBotBetTai;
  }

  public void setNumberUserAndBotBetTai(long numberUserAndBotBetTai) {
    this.numberUserAndBotBetTai = numberUserAndBotBetTai;
  }

  public long getNumberUserAndBotBetXiu() {
    return numberUserAndBotBetXiu;
  }

  public void setNumberUserAndBotBetXiu(long numberUserAndBotBetXiu) {
    this.numberUserAndBotBetXiu = numberUserAndBotBetXiu;
  }

  public long getRealTime() {
    return realTime;
  }

  public void setRealTime(long realTime) {
    this.realTime = realTime;
  }

  public boolean isBettingRound() {
    return bettingRound;
  }

  public void setBettingRound(boolean bettingRound) {
    this.bettingRound = bettingRound;
  }

  public void setLstMsg(List<TaiXiuChatMsg> lstMsg) {
    this.lstMsg = lstMsg;
  }

  public List<TaiXiuChatMsg> getLstMsg() {
    return lstMsg;
  }

  public List<String> getGetListChatUsers() {
    return getListChatUsers;
  }

  public void setGetListChatUsers(List<String> getListChatUsers) {
    this.getListChatUsers = getListChatUsers;
  }

  @Override
  public String toString() {
    return "TaiXiuAdminReportResponse{" +
        "moneyTai=" + moneyTai +
        ", moneyXiu=" + moneyXiu +
        ", nguoiChoiBetTai=" + nguoiChoiBetTai +
        ", nguoiChoiBetXiu=" + nguoiChoiBetXiu +
        ", moneyTaiFull=" + moneyTaiFull +
        ", moneyXiuFull=" + moneyXiuFull +
        ", phienId=" + phienId +
        '}';
  }
}

