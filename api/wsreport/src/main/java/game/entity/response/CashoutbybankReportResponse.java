package game.entity.response;

import game.entity.report.CashoutbybankAdminObj;


public class CashoutbybankReportResponse {
    String code;
    CashoutbybankAdminObj reportResponses;

    public CashoutbybankReportResponse() {
    }

    public CashoutbybankAdminObj getReportResponses() {
        return reportResponses;
    }

    public void setReportResponses(CashoutbybankAdminObj reportResponses) {
        this.reportResponses = reportResponses;
    }

    public CashoutbybankReportResponse(String code, CashoutbybankAdminObj cashoutbybankAdminObj) {
        this.code = code;
        this.reportResponses = cashoutbybankAdminObj;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}
