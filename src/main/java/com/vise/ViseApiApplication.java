package com.vise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.vise.config.OtelConfiguration;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

@SpringBootApplication
public class ViseApiApplication {

    public static void main(String[] args) {
        OpenTelemetry openTelemetry = OtelConfiguration.initializeOpenTelemetry();
        Tracer tracer = openTelemetry.getTracer("com.vise.ClientController");

        Span span = tracer.spanBuilder("AppStartup").startSpan();
        try (Scope scope = span.makeCurrent()) {
            SpringApplication.run(ViseApiApplication.class, args);
            Span.current().addEvent("Aplicacion iniciada Correctamente");
        } finally{
            span.end();
        }   

    }

}
