package game.entity.response;

import game.entity.entitytaixiu.TaiXiuAdminReportResponse;

import java.util.ArrayList;


public class TaiXiuReportResponse {
    String code;
    ArrayList<TaiXiuAdminReportResponse> reportResponses;

    public TaiXiuReportResponse() {
    }

    public ArrayList<TaiXiuAdminReportResponse> getReportResponses() {
        return reportResponses;
    }

    public void setReportResponses(ArrayList<TaiXiuAdminReportResponse> reportResponses) {
        this.reportResponses = reportResponses;
    }

    public TaiXiuReportResponse(String code, ArrayList<TaiXiuAdminReportResponse> taiXiuAdminReportObjs) {
        this.code = code;
        this.reportResponses = taiXiuAdminReportObjs;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}
