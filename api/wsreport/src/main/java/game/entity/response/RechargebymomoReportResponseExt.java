package game.entity.response;

import game.entity.report.RechargebymomoAdminObj;
import game.entity.report.UserWithdrawMomo;


public class RechargebymomoReportResponseExt {
    String code;
    UserWithdrawMomo reportResponses;

    public RechargebymomoReportResponseExt() {
    }

    public UserWithdrawMomo getReportResponses() {
        return reportResponses;
    }

    public void setReportResponses(UserWithdrawMomo reportResponses) {
        this.reportResponses = reportResponses;
    }

    public RechargebymomoReportResponseExt(String code, UserWithdrawMomo rechargebymomoAdminObj) {
        this.code = code;
        this.reportResponses = rechargebymomoAdminObj;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}
