package com.cafecoffe.product.api.model;

public class CoffeeEvent {

    private Long eventId;
    private String eventType;

    public CoffeeEvent() {
    }

    public CoffeeEvent(Long eventId, String eventType) {
        this.eventId = eventId;
        this.eventType = eventType;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    @Override
    public String toString() {
        return "CoffeeEvent{" +
                "eventId=" + eventId +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}
