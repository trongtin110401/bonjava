/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.response;

public class FundInfoResponse
        extends BaseResponseModel {
    public FundInfoResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    private long fundTaiXiu = 0;
    private long fundTaiXiuMd5 = 0;

    private long fundXocDia = 0;

    private long fundBauCua = 0;

    public long getFundTaiXiu() {
        return fundTaiXiu;
    }

    public void setFundTaiXiu(long fundTaiXiu) {
        this.fundTaiXiu = fundTaiXiu;
    }

    public long getFundTaiXiuMd5() {
        return fundTaiXiuMd5;
    }

    public void setFundTaiXiuMd5(long fundTaiXiuMd5) {
        this.fundTaiXiuMd5 = fundTaiXiuMd5;
    }

    public long getFundXocDia() {
        return fundXocDia;
    }

    public void setFundXocDia(long fundXocDia) {
        this.fundXocDia = fundXocDia;
    }

    public long getFundBauCua() {
        return fundBauCua;
    }

    public void setFundBauCua(long fundBauCua) {
        this.fundBauCua = fundBauCua;
    }
}

