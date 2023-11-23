package game.entity.response;

import game.entity.report.RechargebymomoAdminObj;


public class RechargebymomoReportResponse {
    String code;
    RechargebymomoAdminObj reportResponses;

    public RechargebymomoReportResponse() {
    }

    public RechargebymomoAdminObj getReportResponses() {
        return reportResponses;
    }

    public void setReportResponses(RechargebymomoAdminObj reportResponses) {
        this.reportResponses = reportResponses;
    }

    public RechargebymomoReportResponse(String code, RechargebymomoAdminObj rechargebymomoAdminObj) {
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
