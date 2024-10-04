package com.ciliosencantados.model;

import com.ciliosencantados.bean.CalendarFactory;
import com.ciliosencantados.util.DateTimeUtil;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;

public class Calendario {
    public static final String CALENDAR_ID = "primary";
    private final Calendar service;
    private DateTime dateTime;

    public Calendario() {
        this.service = CalendarFactory.CALENDAR;
    }

    public Event insert(final Event event) throws IOException {
        checkExistentEvent();
        return insertEvent(event);
    }

    private void checkExistentEvent() throws IOException {
        final boolean existEvent = getEventsOfTheDay().getItems().stream().anyMatch(it -> this.dateTime.getValue() >= it.getStart().getDateTime().getValue() && this.dateTime.getValue() <= it.getEnd().getDateTime().getValue());

        if (existEvent) {
            throw new RuntimeException("Já existe um evento cadastrado para este horário.");
        }
    }

    private Event insertEvent(final Event event) throws IOException {
        event.setLocation("Servidão Joesi Guimarães da Silva, 254. Rio Vermelho, Florianópolis - SC CEP: 88060-252");
        final EventDateTime start = new EventDateTime().setDateTime(this.dateTime).setTimeZone("America/Sao_Paulo");
        event.setStart(start);
        final EventDateTime end = getEndEventDateTime();
        event.setEnd(end);
        getEvents().insert(CALENDAR_ID, event).execute();

        return event;
    }

    private Events getEventsOfTheDay() throws IOException {
        return getPrimaryCalendarEvents().setTimeMax(getTimeMax()).setTimeMin(getTimeMin()).setOrderBy("startTime").setSingleEvents(true).execute();
    }

    private Calendar.Events.List getPrimaryCalendarEvents() throws IOException {
        return getEvents().list(CALENDAR_ID);
    }

    private DateTime getTimeMax() {
        return DateTimeUtil.atEndOfDay(dateTime);
    }

    private DateTime getTimeMin() {
        return DateTimeUtil.atStartOfDay(dateTime);
    }

    private Calendar.Events getEvents() {
        return this.service.events();
    }

    private EventDateTime getEndEventDateTime() {
        return new EventDateTime().setDateTime(DateTimeUtil.addHours(dateTime, 2)).setTimeZone("America/Sao_Paulo");
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }
}
