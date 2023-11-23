package game.entity.response;

import game.entity.report.RechargebyonepayotpAdminObj;


public class RechargebyonepayotpReportResponse {
    String code;
    RechargebyonepayotpAdminObj reportResponses;

    public RechargebyonepayotpReportResponse() {
    }

    public RechargebyonepayotpAdminObj getReportResponses() {
        return reportResponses;
    }

    public void setReportResponses(RechargebyonepayotpAdminObj reportResponses) {
        this.reportResponses = reportResponses;
    }

    public RechargebyonepayotpReportResponse(String code, RechargebyonepayotpAdminObj rechargebyonepayotpAdminObj) {
        this.code = code;
        this.reportResponses = rechargebyonepayotpAdminObj;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}
