package com.vise.model;

import jakarta.persistence.*; // Importa las anotaciones de JPA

@Entity
@Table(name = "clients") // nombre de la tabla en la DB
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String country;

    private double monthlyIncome;

    private boolean viseClub;

    @Enumerated(EnumType.STRING) // para guardar el enum como texto
    private CardType cardType;

    public Client() {
    }

    public Client(Long id, String name, String country, double monthlyIncome, boolean viseClub, CardType cardType) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.monthlyIncome = monthlyIncome;
        this.viseClub = viseClub;
        this.cardType = cardType;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(double monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public boolean isViseClub() {
        return viseClub;
    }

    public void setViseClub(boolean viseClub) {
        this.viseClub = viseClub;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }
}
