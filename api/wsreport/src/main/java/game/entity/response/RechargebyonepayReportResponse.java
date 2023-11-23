package game.entity.response;

import game.entity.report.RechargebyonepayAdminObj;


public class RechargebyonepayReportResponse {
    String code;
    RechargebyonepayAdminObj reportResponses;

    public RechargebyonepayReportResponse() {
    }

    public RechargebyonepayAdminObj getReportResponses() {
        return reportResponses;
    }

    public void setReportResponses(RechargebyonepayAdminObj reportResponses) {
        this.reportResponses = reportResponses;
    }

    public RechargebyonepayReportResponse(String code, RechargebyonepayAdminObj rechargebyonepayAdminObj) {
        this.code = code;
        this.reportResponses = rechargebyonepayAdminObj;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}
