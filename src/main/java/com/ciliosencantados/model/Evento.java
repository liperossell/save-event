package com.ciliosencantados.model;

import com.google.api.services.calendar.model.Event;

public class Evento {
    private String time;
    private Event event;

    public Evento(String time, Event event) {
        this.time = time;
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
