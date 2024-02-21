/*
 * Decompiled with CFR 0.144.
 */
package com.vinplay.vbee.common.response;

public class LinkSocialResponse
        extends BaseResponseModel {
    public LinkSocialResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    private int id = 1;

    private String fanPage;
    private String groupFacebook;

    private String teleCSKH;

    private String botTele;

    private String groupTele;

    private String liveChat;

    public String getFanPage() {
        return fanPage;
    }

    public void setFanPage(String fanPage) {
        this.fanPage = fanPage;
    }

    public String getGroupFacebook() {
        return groupFacebook;
    }

    public void setGroupFacebook(String groupFacebook) {
        this.groupFacebook = groupFacebook;
    }

    public String getTeleCSKH() {
        return teleCSKH;
    }

    public void setTeleCSKH(String teleCSKH) {
        this.teleCSKH = teleCSKH;
    }

    public String getBotTele() {
        return botTele;
    }

    public void setBotTele(String botTele) {
        this.botTele = botTele;
    }

    public String getGroupTele() {
        return groupTele;
    }

    public void setGroupTele(String groupTele) {
        this.groupTele = groupTele;
    }

    public String getLiveChat() {
        return liveChat;
    }

    public void setLiveChat(String liveChat) {
        this.liveChat = liveChat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

