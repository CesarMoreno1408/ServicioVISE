package com.vise.service;

import org.springframework.stereotype.Service;

import com.vise.model.Client;
import com.vise.repository.ClientRepository;

@Service
public class ClientService {

    private final ClientRepository repo;

    public ClientService(ClientRepository repo) {
        this.repo = repo;
    }

    public Object registerClient(Client client) {
        String error = validateClient(client);
        if (error != null) {
            return new Response("Rejected", error);
        }
        Client saved = repo.save(client);
        return new ResponseRegistered(saved.getId(), saved.getName(),
                saved.getCardType().name(), "Registered",
                "Cliente apto para tarjeta " + saved.getCardType().name());
    }

    private String validateClient(Client c) {
        switch (c.getCardType()) {
            case GOLD:
                if (c.getMonthlyIncome() < 500)
                    return "Ingreso mínimo de 500 USD requerido para Gold";
                break;
            case PLATINUM:
                if (c.getMonthlyIncome() < 1000)
                    return "Ingreso mínimo de 1000 USD requerido para Platinum";
                if (!c.isViseClub())
                    return "Suscripción VISE CLUB requerida para Platinum";
                break;
            case BLACK:
                if (c.getMonthlyIncome() < 2000)
                    return "Ingreso mínimo de 2000 USD requerido para Black";
                if (!c.isViseClub())
                    return "Suscripción VISE CLUB requerida para Black";
                if (c.getCountry().equalsIgnoreCase("China")
                        || c.getCountry().equalsIgnoreCase("Vietnam")
                        || c.getCountry().equalsIgnoreCase("India")
                        || c.getCountry().equalsIgnoreCase("Irán"))
                    return "Cliente no puede residir en China, Vietnam, India o Irán con tarjeta Black";
                break;
            case WHITE:
                if (c.getMonthlyIncome() < 2000)
                    return "Ingreso mínimo de 2000 USD requerido para White";
                if (!c.isViseClub())
                    return "Suscripción VISE CLUB requerida para White";
                if (c.getCountry().equalsIgnoreCase("China")
                        || c.getCountry().equalsIgnoreCase("Vietnam")
                        || c.getCountry().equalsIgnoreCase("India")
                        || c.getCountry().equalsIgnoreCase("Irán"))
                    return "Cliente no puede residir en China, Vietnam, India o Irán con tarjeta White";
                break;
            default:
                break;
        }
        return null; // válido
    }

    // Clases auxiliares para respuesta JSON
    public static class Response {
        public String status;
        public String error;
        public Response(String status, String error) {
            this.status = status;
            this.error = error;
        }
    }

    public static class ResponseRegistered {
        public Long clientId;
        public String name;
        public String cardType;
        public String status;
        public String message;
        public ResponseRegistered(Long clientId, String name, String cardType, String status, String message) {
            this.clientId = clientId;
            this.name = name;
            this.cardType = cardType;
            this.status = status;
            this.message = message;
        }
    }
}
