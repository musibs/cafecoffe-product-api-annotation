package com.cafecoffe.product.api.model;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CoffeeEvent)) return false;
        CoffeeEvent that = (CoffeeEvent) o;
        return Objects.equals(getEventId(), that.getEventId()) &&
                Objects.equals(getEventType(), that.getEventType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEventId(), getEventType());
    }

    @Override
    public String toString() {
        return "CoffeeEvent{" +
                "eventId=" + eventId +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}
