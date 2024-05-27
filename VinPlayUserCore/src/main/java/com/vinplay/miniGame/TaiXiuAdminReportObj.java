package com.vinplay.miniGame;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinplay.vbee.common.response.minigame.TaiXiuAdmin;
import com.vinplay.vbee.common.response.minigame.TaiXiuChatMsg;

import java.io.Serializable;
import java.util.List;

public class TaiXiuAdminReportObj implements Serializable {
    long moneyTai;
    long moneyXiu;
    long numberUserRealTai;
    long numberUserAndBotBetTai;
    long numberUserRealXiu;
    long numberUserAndBotBetXiu;
    long moneyTaiFull;
    long moneyXiuFull;
    long phienId;
    long realTime;
    boolean bettingRound;

    String taiXiuMd5Hash;

    String taiXiuPlainResult;

    Integer dice1;
    Integer dice2;
    Integer dice3;
    String sessionResult;

    List<TaiXiuAdmin> contributors;
    List<TaiXiuChatMsg> lstMsg;
    List<String> getListChatUsers;

    public List<TaiXiuAdmin> getContributors() {
        return contributors;
    }

    public void setContributors(List<TaiXiuAdmin> contributors) {
        this.contributors = contributors;
    }

    public TaiXiuAdminReportObj(long moneyTai, long moneyXiu, long numberUserRealTai, long numberUserRealXiu, long moneyTaiFull, long moneyXiuFull, long phienId) {
        this.moneyTai = moneyTai;
        this.moneyXiu = moneyXiu;
        this.numberUserRealTai = numberUserRealTai;
        this.numberUserRealXiu = numberUserRealXiu;
        this.moneyTaiFull = moneyTaiFull;
        this.moneyXiuFull = moneyXiuFull;
        this.phienId = phienId;
    }

    public TaiXiuAdminReportObj() {
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

    public long getNumberUserRealTai() {
        return numberUserRealTai;
    }

    public void setNumberUserRealTai(long numberUserRealTai) {
        this.numberUserRealTai = numberUserRealTai;
    }

    public long getNumberUserRealXiu() {
        return numberUserRealXiu;
    }

    public void setNumberUserRealXiu(long numberUserRealXiu) {
        this.numberUserRealXiu = numberUserRealXiu;
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

    public List<TaiXiuChatMsg> getLstMsg() {
        return lstMsg;
    }

    public void setLstMsg(List<TaiXiuChatMsg> lstMsg) {
        this.lstMsg = lstMsg;
    }

    public List<String> getGetListChatUsers() {
        return getListChatUsers;
    }

    public void setGetListChatUsers(List<String> getListChatUsers) {
        this.getListChatUsers = getListChatUsers;
    }

    public String getTaiXiuMd5Hash() {
        return taiXiuMd5Hash;
    }

    public void setTaiXiuMd5Hash(String taiXiuMd5Hash) {
        this.taiXiuMd5Hash = taiXiuMd5Hash;
    }

    public String getTaiXiuPlainResult() {
        return taiXiuPlainResult;
    }

    public void setTaiXiuPlainResult(String taiXiuPlainResult) {
        this.taiXiuPlainResult = taiXiuPlainResult;
    }

    public Integer getDice1() {
        return dice1;
    }

    public void setDice1(Integer dice1) {
        this.dice1 = dice1;
    }

    public Integer getDice2() {
        return dice2;
    }

    public void setDice2(Integer dice2) {
        this.dice2 = dice2;
    }

    public Integer getDice3() {
        return dice3;
    }

    public void setDice3(Integer dice3) {
        this.dice3 = dice3;
    }

    public String getSessionResult() {
        return sessionResult;
    }

    public void setSessionResult(String sessionResult) {
        this.sessionResult = sessionResult;
    }

    @Override
    public String toString() {
        return "TaiXiuAdminReportObj{" +
                "moneyTai=" + moneyTai +
                ", moneyXiu=" + moneyXiu +
                ", nguoiChoiBetTai=" + numberUserRealTai +
                ", nguoiChoiBetXiu=" + numberUserRealXiu +
                ", moneyTaiFull=" + moneyTaiFull +
                ", moneyXiuFull=" + moneyXiuFull +
                ", phienId=" + phienId +
                '}';
    }

    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString((Object) this);
        } catch (JsonProcessingException mapper) {
            return "{\"success\":false,\"errorCode\":\"1001\"}";
        }
    }
}
