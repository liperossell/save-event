package com.ciliosencantados.model;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;

import java.util.List;

public class Evento {
    private String time;
    private String name;
    private String email;
    private String phone;

    public Evento(String time, String name, String email, String phone) {
        this.time = time;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Event toEvent() {
        return new Event()
                .setSummary("Agendamento - " + this.name)
                .setDescription("Email: " + this.email + "\nTelefone: " + this.phone)
                .setAttendees(List.of(new EventAttendee().setEmail(this.email).setDisplayName(this.name)));
    }
}
