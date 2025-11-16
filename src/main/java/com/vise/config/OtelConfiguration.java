package com.vise.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;

public class OtelConfiguration {

    public static OpenTelemetry initializeOpenTelemetry() {
        // ✅ Exportador OTLP/HTTP para Axiom
        OtlpHttpSpanExporter spanExporter = OtlpHttpSpanExporter.builder()
            .setEndpoint("https://api.axiom.co/v1/traces")
            .addHeader("Authorization", "Bearer xaat-541962ba-96d3-4bf1-ae40-eb55d5e6ccbb")
            .addHeader("X-Axiom-Dataset", "serviciovise")
            .build();

        // ✅ Metadatos del servicio
        Resource resource = Resource.getDefault().merge(Resource.create(Attributes.of(
            AttributeKey.stringKey("service.name"), "vise-api",
            AttributeKey.stringKey("service.namespace"), "com.vise"
        )));

        // ✅ Proveedor de trazas
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
            .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
            .setResource(resource)
            .build();

        // ✅ Inicializa y registra globalmente
        OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
            .setTracerProvider(tracerProvider)
            .buildAndRegisterGlobal();

        System.out.println("✅ OpenTelemetry inicializado y conectado a Axiom (HTTP Exporter).");
        return openTelemetry;
    }
}
