package com.vinplay.momo.response;

import com.vinplay.momo.entities.CardEntity;

import java.util.List;

public class CardResponse {
    String message;
    List<CardEntity> body;

    public String getMessage() {
        return message;
    }

    public CardResponse(String message, List<CardEntity> body) {
        this.message = message;
        this.body = body;
    }

    public CardResponse() {
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<CardEntity> getBody() {
        return body;
    }

    public void setBody(List<CardEntity> body) {
        this.body = body;
    }
}
