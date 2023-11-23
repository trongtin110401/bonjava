package com.vinplay.vbee.common.response.minigame;

import java.io.Serializable;

public class TaiXiuChatMsg implements Serializable {
  private static final long serialVersionUID = -2028615703783062770L;
  public String nickname = "";
  public String mesasge = "";
  //1 là trạng thái đã gửi tới client (này chỉ dành cho chat tù bên admin sang client)
  public int status;

  public TaiXiuChatMsg(String nickname, String mesasge) {
    this.nickname = nickname;
    this.mesasge = mesasge;
  }

  public TaiXiuChatMsg() {
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getMesasge() {
    return mesasge;
  }

  public void setMesasge(String mesasge) {
    this.mesasge = mesasge;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return "TaiXiuChatMsg{" +
        "nickname='" + nickname + '\'' +
        ", mesasge='" + mesasge + '\'' +
        ", status=" + status +
        '}';
  }
}
