/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.dichvuthe.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ZoanMomoResponse {
    private int errorCode;
    private String errorMessage;

    public ZoanMomoResponse(int code, String message) {
        this.errorCode = code;
        this.errorMessage = message;
    }

    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString((Object)this);
        }
        catch (JsonProcessingException e) {
            return "";
        }
    }
}

