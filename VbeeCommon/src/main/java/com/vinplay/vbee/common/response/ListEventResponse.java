package com.vinplay.vbee.common.response;

import java.util.List;

public class ListEventResponse extends BaseResponseModel {

    private List<Event> events;

    public ListEventResponse(boolean success, String errorCode) {
        super(success, errorCode);
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
