package com.vinplay.dichvuthe.response;

import org.python.parser.ast.Str;

public class MomoResponse {
    private String tid;
    private String receiverName;
    private String receiverPhone;
    private String comment;
    private int code;

    public MomoResponse(String tid, String receiverName, String receiverPhone, String comment, int code) {
        this.tid = tid;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.comment = comment;
        this.code = code;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
