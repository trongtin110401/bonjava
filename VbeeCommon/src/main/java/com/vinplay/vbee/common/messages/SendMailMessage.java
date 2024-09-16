/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.messages;

public class SendMailMessage extends BaseMessage {

    private static final long serialVersionUID = 1L;

    String nickName;
    String title;
    String content;
    String id;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}

