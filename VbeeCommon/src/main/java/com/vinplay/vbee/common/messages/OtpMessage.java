/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.messages;

import com.vinplay.vbee.common.messages.BaseMessage;

public class OtpMessage
extends BaseMessage {
    private static final long serialVersionUID = 1L;
    private String requestId;
    private String mobile;
    private String commandCode;
    private String messageMO;
    private String responseMO;
    private String messageMT;
    private String responseMT;

    public OtpMessage(String requestId, String mobile, String commandCode, String messageMO) {
        this.requestId = requestId;
        this.mobile = mobile;
        this.commandCode = commandCode;
        this.messageMO = messageMO;
    }

    public String getRequestId() {
        return this.requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCommandCode() {
        return this.commandCode;
    }

    public void setCommandCode(String commandCode) {
        this.commandCode = commandCode;
    }

    public String getMessageMO() {
        return this.messageMO;
    }

    public void setMessageMO(String messageMO) {
        this.messageMO = messageMO;
    }

    public String getResponseMO() {
        return this.responseMO;
    }

    public void setResponseMO(String responseMO) {
        this.responseMO = responseMO;
    }

    public String getMessageMT() {
        return this.messageMT;
    }

    public void setMessageMT(String messageMT) {
        this.messageMT = messageMT;
    }

    public String getResponseMT() {
        return this.responseMT;
    }

    public void setResponseMT(String responseMT) {
        this.responseMT = responseMT;
    }
}

