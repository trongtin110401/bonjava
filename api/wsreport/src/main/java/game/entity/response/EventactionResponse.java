package game.entity.response;

import game.entity.report.EventactionAdminObj;


public class EventactionResponse {
    String code;
    EventactionAdminObj reportResponses;

    public EventactionResponse() {
    }

    public EventactionAdminObj getReportResponses() {
        return reportResponses;
    }

    public void setReportResponses(EventactionAdminObj reportResponses) {
        this.reportResponses = reportResponses;
    }

    public EventactionResponse(String code, EventactionAdminObj eventactionAdminObj) {
        this.code = code;
        this.reportResponses = eventactionAdminObj;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}
