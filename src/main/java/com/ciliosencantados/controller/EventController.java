package com.ciliosencantados.controller;

import com.ciliosencantados.exception.SaveEventException;
import com.ciliosencantados.model.Calendario;
import com.ciliosencantados.model.Evento;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.jboss.resteasy.reactive.RestResponse;

import java.io.IOException;

@Path("/")
public class EventController {

    @POST
    public RestResponse<Event> save(final Evento evento) throws IOException, SaveEventException {
        Calendario calendario = new Calendario();
        calendario.setDateTime(DateTime.parseRfc3339(evento.getTime()));
        return RestResponse.ok(calendario.insert(evento.toEvent()));
    }
}
