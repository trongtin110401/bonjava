/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.dal.entities.taixiu;

import java.io.Serializable;

public class ResultTaiXiuMd5 extends ResultTaiXiu
        implements Serializable {
    private static final long serialVersionUID = 1L;
    private String plantTextResult;
    private String md5TextResult;

    public String getPlantTextResult() {
        return plantTextResult;
    }

    public void setPlantTextResult(String plantTextResult) {
        this.plantTextResult = plantTextResult;
    }

    public String getMd5TextResult() {
        return md5TextResult;
    }

    public void setMd5TextResult(String md5TextResult) {
        this.md5TextResult = md5TextResult;
    }
}

