package game.entity.response;

import game.entity.entitynotification.NotificationAdminObj;


public class NotifyReportResponse {
    String code;
    NotificationAdminObj reportResponses;

    public NotifyReportResponse() {
    }

    public NotificationAdminObj getReportResponses() {
        return reportResponses;
    }

    public void setReportResponses(NotificationAdminObj reportResponses) {
        this.reportResponses = reportResponses;
    }

    public NotifyReportResponse(String code, NotificationAdminObj notificationAdminObjs) {
        this.code = code;
        this.reportResponses = notificationAdminObjs;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}
