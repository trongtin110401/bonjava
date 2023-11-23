package game.entity.response;

import game.entity.report.RechargebybankAdminObj;


public class RechargebybankReportResponse {
    String code;
    RechargebybankAdminObj reportResponses;

    public RechargebybankReportResponse() {
    }

    public RechargebybankAdminObj getReportResponses() {
        return reportResponses;
    }

    public void setReportResponses(RechargebybankAdminObj reportResponses) {
        this.reportResponses = reportResponses;
    }

    public RechargebybankReportResponse(String code, RechargebybankAdminObj rechargebybankAdminObj) {
        this.code = code;
        this.reportResponses = rechargebybankAdminObj;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}
