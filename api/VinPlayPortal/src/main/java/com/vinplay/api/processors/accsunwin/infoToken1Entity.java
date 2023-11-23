package com.vinplay.api.processors.accsunwin;

public class infoToken1Entity {
    private String ipAddress;
    private String userId;
    private String username;
    private long timestamp;

    public infoToken1Entity() {
    }

    public infoToken1Entity(String ipAddress, String userId, String username, long timestamp) {
        this.ipAddress = ipAddress;
        this.userId = userId;
        this.username = username;
        this.timestamp = timestamp;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "infoToken1Entity{" +
                "ipAddress='" + ipAddress + '\'' +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
