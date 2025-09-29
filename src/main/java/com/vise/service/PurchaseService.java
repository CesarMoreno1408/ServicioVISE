package com.vise.service;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Service;

import com.vise.model.Client;
import com.vise.repository.ClientRepository;

@Service
public class PurchaseService {
    private final ClientRepository repo;

    public PurchaseService(ClientRepository repo) {
        this.repo = repo;
    }

    public Object processPurchase(PurchaseRequest request) {
        Client c = repo.findById(request.clientId)
                .orElse(null);
        if (c == null)
            return new Response("Rejected", "Cliente no encontrado");

        // 🔴 Validar restricciones por tarjeta antes de permitir la compra
        switch (c.getCardType()) {
            case Gold:
                if (c.getMonthlyIncome() < 500) {
                    return new Response("Rejected",
                            "El cliente no cumple ingreso mínimo de 500 USD para Gold");
                }
                break;
            case Platinum:
                if (c.getMonthlyIncome() < 1000) {
                    return new Response("Rejected",
                            "El cliente no cumple ingreso mínimo de 1000 USD para Platinum");
                }
                if (!c.isViseClub()) {
                    return new Response("Rejected",
                            "El cliente no tiene la suscripción VISE CLUB requerida para Platinum");
                }
                break;
            case Black:
                if (c.getMonthlyIncome() < 2000) {
                    return new Response("Rejected",
                            "El cliente no cumple ingreso mínimo de 2000 USD para Black");
                }
                if (!c.isViseClub()) {
                    return new Response("Rejected",
                            "El cliente no tiene la suscripción VISE CLUB requerida para Black");
                }
                // Aquí validamos país de la COMPRA, no de residencia
                if (isForbiddenCountry(request.purchaseCountry())) {
                    return new Response("Rejected",
                            "El cliente con tarjeta Black no puede realizar compras desde " + request.purchaseCountry());
                }
                break;
            case White:
                if (c.getMonthlyIncome() < 2000) {
                    return new Response("Rejected",
                            "El cliente no cumple ingreso mínimo de 2000 USD para White");
                }
                if (!c.isViseClub()) {
                    return new Response("Rejected",
                            "El cliente no tiene la suscripción VISE CLUB requerida para White");
                }
                if (isForbiddenCountry(request.purchaseCountry())) {
                    return new Response("Rejected",
                            "El cliente con tarjeta White no puede realizar compras desde " + request.purchaseCountry());
                }
                break;
            default:
                // Classic no tiene restricciones
                break;
        }

        // ✅ Calcular descuentos (beneficios)
        double discount = 0;
        String benefit = "";
        ZonedDateTime dt = ZonedDateTime.parse(request.purchaseDate());
        DayOfWeek day = dt.getDayOfWeek();
        boolean exterior = !request.purchaseCountry().equalsIgnoreCase(c.getCountry());

        switch (c.getCardType()) {
            case Gold:
                if ((day == DayOfWeek.MONDAY || day == DayOfWeek.TUESDAY || day == DayOfWeek.WEDNESDAY)
                        && request.amount() > 100) {
                    discount = request.amount() * 0.15;
                    benefit = "Lunes-Miércoles - 15%";
                }
                break;
            case Platinum:
                if ((day == DayOfWeek.MONDAY || day == DayOfWeek.TUESDAY || day == DayOfWeek.WEDNESDAY)
                        && request.amount() > 100) {
                    discount = request.amount() * 0.20;
                    benefit = "Lunes-Miércoles - 20%";
                } else if (day == DayOfWeek.SATURDAY && request.amount() > 200) {
                    discount = request.amount() * 0.30;
                    benefit = "Sábado - 30%";
                } else if (exterior) {
                    discount = request.amount() * 0.05;
                    benefit = "Compra exterior - 5%";
                }
                break;
            case Black:
                if ((day == DayOfWeek.MONDAY || day == DayOfWeek.TUESDAY || day == DayOfWeek.WEDNESDAY)
                        && request.amount() > 100) {
                    discount = request.amount() * 0.25;
                    benefit = "Lunes-Miércoles - 25%";
                } else if (day == DayOfWeek.SATURDAY && request.amount() > 200) {
                    discount = request.amount() * 0.35;
                    benefit = "Sábado - 35%";
                } else if (exterior) {
                    discount = request.amount() * 0.05;
                    benefit = "Compra exterior - 5%";
                }
                break;
            case White:
                if ((day == DayOfWeek.MONDAY || day == DayOfWeek.TUESDAY || day == DayOfWeek.WEDNESDAY
                        || day == DayOfWeek.THURSDAY || day == DayOfWeek.FRIDAY)
                        && request.amount() > 100) {
                    discount = request.amount() * 0.25;
                    benefit = "Lunes-Viernes - 25%";
                } else if ((day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) && request.amount() > 200) {
                    discount = request.amount() * 0.35;
                    benefit = "Fin de semana - 35%";
                } else if (exterior) {
                    discount = request.amount() * 0.05;
                    benefit = "Compra exterior - 5%";
                }
                break;
            default:
                // Classic no tiene beneficios
                break;
        }

        double finalAmount = request.amount() - discount;
        return new PurchaseResponse("Approved", request.clientId, request.amount(),
                discount, finalAmount, benefit);
    }

    // Helper para países prohibidos
    private boolean isForbiddenCountry(String country) {
        return country.equalsIgnoreCase("China")
                || country.equalsIgnoreCase("Vietnam")
                || country.equalsIgnoreCase("India")
                || country.equalsIgnoreCase("Irán")
                || country.equalsIgnoreCase("Iran"); // por si viene sin tilde
    }

    // DTOs
    public static record PurchaseRequest(Long clientId, double amount, String currency,
                                         String purchaseDate, String purchaseCountry) {
    }

    public static class Response {
        public String status;
        public String error;

        public Response(String status, String error) {
            this.status = status;
            this.error = error;
        }
    }

    public static class PurchaseResponse {
        public String status;
        public Purchase purchase;

        public PurchaseResponse(String status, Long clientId, double originalAmount,
                                double discountApplied, double finalAmount, String benefit) {
            this.status = status;
            this.purchase = new Purchase(clientId, originalAmount, discountApplied, finalAmount, benefit);
        }

        public static class Purchase {
            public Long clientId;
            public double originalAmount;
            public double discountApplied;
            public double finalAmount;
            public String benefit;

            public Purchase(Long clientId, double originalAmount, double discountApplied,
                            double finalAmount, String benefit) {
                this.clientId = clientId;
                this.originalAmount = originalAmount;
                this.discountApplied = discountApplied;
                this.finalAmount = finalAmount;
                this.benefit = benefit;
            }
        }
    }
}
