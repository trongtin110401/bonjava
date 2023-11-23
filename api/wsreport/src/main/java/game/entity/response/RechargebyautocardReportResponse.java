package game.entity.response;

import game.entity.report.RechargebyautocardAdminObj;


public class RechargebyautocardReportResponse {
    String code;
    RechargebyautocardAdminObj reportResponses;

    public RechargebyautocardReportResponse() {
    }

    public RechargebyautocardAdminObj getReportResponses() {
        return reportResponses;
    }

    public void setReportResponses(RechargebyautocardAdminObj reportResponses) {
        this.reportResponses = reportResponses;
    }

    public RechargebyautocardReportResponse(String code, RechargebyautocardAdminObj rechargebyautocardAdminObj) {
        this.code = code;
        this.reportResponses = rechargebyautocardAdminObj;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}
