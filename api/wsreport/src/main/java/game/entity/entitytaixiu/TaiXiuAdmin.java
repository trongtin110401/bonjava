package game.entity.entitytaixiu;

import java.io.Serializable;

public class TaiXiuAdmin implements Serializable {
  String username;
  int cuaDat;
  long money;

  long reportMoneyToday;
  long totalMoney;
  public TaiXiuAdmin(String username, int cuaDat, long money) {
    this.username = username;
    this.cuaDat = cuaDat;
    this.money = money;
  }

  public TaiXiuAdmin(String username, int cuaDat, long money, long reportMoneyToday) {
    this.username = username;
    this.cuaDat = cuaDat;
    this.money = money;
    this.reportMoneyToday = reportMoneyToday;
  }

  public TaiXiuAdmin(String username, int cuaDat, long money, long reportMoneyToday, long totalMoney) {
    this.username = username;
    this.cuaDat = cuaDat;
    this.money = money;
    this.reportMoneyToday = reportMoneyToday;
    this.totalMoney = totalMoney;
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

  public long getReportMoneyToday() {
    return reportMoneyToday;
  }

  public void setReportMoneyToday(long reportMoneyToday) {
    this.reportMoneyToday = reportMoneyToday;
  }

  @Override
  public String toString() {
    return "TaiXiuAdmin{" +
            "username='" + username + '\'' +
            ", cuaDat=" + cuaDat +
            ", money=" + money +
            ", reportMoneyToday=" + reportMoneyToday +
            ", totalMoney=" + totalMoney +
            '}';
  }


  public long getTotalMoney() {
    return totalMoney;
  }

  public void setTotalMoney(long totalMoney) {
    this.totalMoney = totalMoney;
  }
}
