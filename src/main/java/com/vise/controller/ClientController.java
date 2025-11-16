package com.vise.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vise.model.Client;
import com.vise.service.ClientService;
import com.vise.service.ClientService.Response;
import com.vise.service.ClientService.ResponseRegistered;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

@RestController
@RequestMapping("/client")
public class ClientController {

    private final ClientService service;
    private final Tracer tracer;

    public ClientController(ClientService service) {
        this.service = service;
        // Inicializamos el tracer solo una vez
        this.tracer = GlobalOpenTelemetry.getTracer("com.vise.controller.ClientController");
    }

    @PostMapping
    public Object register(@RequestBody Client client) {
        // Creamos un span principal por solicitud
        Span span = tracer.spanBuilder("ClientRegistration")
                .setSpanKind(SpanKind.SERVER)
                .startSpan();

        try (Scope scope = span.makeCurrent()) {
            // 🧾 Atributos del cliente (modelo completo)
            span.setAttribute("client.name", client.getName());
            span.setAttribute("client.country", client.getCountry());
            span.setAttribute("client.monthlyIncome", client.getMonthlyIncome());
            span.setAttribute("client.viseClub", client.isViseClub());
            span.setAttribute("client.cardType", client.getCardType().name());

            // Evento de inicio
            span.addEvent("Procesando solicitud de registro");

            // Llamamos al servicio
            Object response = service.registerClient(client);

            // Analizamos la respuesta y agregamos información al span
            if (response instanceof ResponseRegistered registered) {
                span.addEvent("Registro completado exitosamente");
                span.setAttribute("client.registered.id", registered.clientId);
                span.setAttribute("client.registered.name", registered.name);
                span.setAttribute("client.registered.cardType", registered.cardType);
                span.setAttribute("response.status", registered.status);
                span.setAttribute("response.message", registered.message);
                span.setStatus(StatusCode.OK, "Cliente registrado correctamente");
            } else if (response instanceof Response errorResponse) {
                span.addEvent("Registro rechazado");
                span.setAttribute("response.status", errorResponse.status);
                span.setAttribute("response.error", errorResponse.error);
                span.setStatus(StatusCode.ERROR, errorResponse.error);
            }

            return response;

        } catch (Exception e) {
            // Captura y registro de errores inesperados
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            throw e;
        } finally {
            span.end(); // Cierre del span
        }
    }
}
