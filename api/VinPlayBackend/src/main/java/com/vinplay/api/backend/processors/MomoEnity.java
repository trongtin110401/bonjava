package com.vinplay.api.backend.processors;

public class MomoEnity {
    private String TrainID;
    private String character;
    private String comment;
    private String numberPhone;
    private String status;

    public MomoEnity() {
    }

    public MomoEnity(String trainID, String character, String comment, String numberPhone, String status) {
        TrainID = trainID;
        this.character = character;
        this.comment = comment;
        this.numberPhone = numberPhone;
        this.status = status;
    }

    public String getTrainID() {
        return TrainID;
    }

    public void setTrainID(String trainID) {
        TrainID = trainID;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getNumberPhone() {
        return numberPhone;
    }

    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "MomoEnity{" +
                "TrainID='" + TrainID + '\'' +
                ", character='" + character + '\'' +
                ", comment='" + comment + '\'' +
                ", numberPhone='" + numberPhone + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
