package game.entity.response;

import game.entity.report.CashoutbycardmanualAdminObj;


public class CashoutbycardmanualReportResponse {
    String code;
    CashoutbycardmanualAdminObj reportResponses;

    public CashoutbycardmanualReportResponse() {
    }

    public CashoutbycardmanualAdminObj getReportResponses() {
        return reportResponses;
    }

    public void setReportResponses(CashoutbycardmanualAdminObj reportResponses) {
        this.reportResponses = reportResponses;
    }

    public CashoutbycardmanualReportResponse(String code, CashoutbycardmanualAdminObj cashoutbycardmanualAdminObj) {
        this.code = code;
        this.reportResponses = cashoutbycardmanualAdminObj;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}
