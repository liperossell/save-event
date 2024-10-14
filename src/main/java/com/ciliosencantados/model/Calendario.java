package com.ciliosencantados.model;

import com.ciliosencantados.bean.CalendarFactory;
import com.ciliosencantados.exception.SaveEventException;
import com.ciliosencantados.util.DateTimeUtil;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class Calendario {
    public static final String CALENDAR_ID = "primary";    public static final String RED_COLOR_ID = "11";
    private final Calendar service;
    private DateTime dateTime;

    public Calendario() {
        this.service = CalendarFactory.CALENDAR;
    }

    public Event insert(final Event event) throws IOException, SaveEventException {
        checkExistentEvent();
        deletePreviousEvent();
        return insertEvent(event);
    }

    private void deletePreviousEvent() throws IOException, SaveEventException {
        long startDateTime = this.dateTime.getValue();
        final Optional<Event> existEvent = getEventsOfTheDay().getItems().stream().filter(it -> startDateTime == it.getStart().getDateTime().getValue()).findFirst();

        if (existEvent.isPresent()) {
            final Event event = existEvent.get();
            getEvents().delete(CALENDAR_ID, event.getId()).execute();
        } else {
            throw new SaveEventException("Não existe um evento cadastrado para este horário.");
        }
    }

    private void checkExistentEvent() throws IOException, SaveEventException {
        final boolean existEvent = getEventsOfTheDay().getItems().stream().anyMatch(it -> this.dateTime.getValue() >= it.getStart().getDateTime().getValue() && this.dateTime.getValue() <= it.getEnd().getDateTime().getValue() && RED_COLOR_ID.equals(it.getColorId()));

        if (existEvent) {
            throw new SaveEventException("Já existe um evento cadastrado para este horário.");
        }
    }

    private Event insertEvent(final Event event) throws IOException {
        event.setLocation("Servidão Joesi Guimarães da Silva, 254. Rio Vermelho, Florianópolis - SC CEP: 88060-252");
        event.setStart(getStartEventDateTime());
        event.setEnd(getEndEventDateTime());
        event.setReminders(getDefaultReminders());
        event.setColorId(RED_COLOR_ID);

        return getEvents().insert(CALENDAR_ID, event).setSendNotifications(true).setSendUpdates("all").execute();
    }

    private EventDateTime getStartEventDateTime() {
        return new EventDateTime().setDateTime(this.dateTime).setTimeZone("America/Sao_Paulo");
    }

    private static Event.Reminders getDefaultReminders() {
        EventReminder popup1 = new EventReminder().setMethod("popup").setMinutes(30);
        EventReminder popup2 = new EventReminder().setMethod("popup").setMinutes(60);
        EventReminder email = new EventReminder().setMethod("email").setMinutes(1440);
        return new Event.Reminders().setUseDefault(false).setOverrides(List.of(popup1, popup2, email));
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
