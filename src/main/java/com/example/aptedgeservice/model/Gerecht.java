package com.example.aptedgeservice.model;

public class Gerecht {
    private String naam;
    private double prijs;
    private String gerechtNummer;

    public Gerecht() {
    }

    public Gerecht(String naam, double prijs, String gerechtNummer) {
        this.naam = naam;
        this.prijs = prijs;
        this.gerechtNummer = gerechtNummer;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public double getPrijs() {
        return prijs;
    }

    public void setPrijs(double prijs) {
        this.prijs = prijs;
    }

    public String getGerechtNummer() {
        return gerechtNummer;
    }

    public void setGerechtNummer(String gerechtNummer) {
        this.gerechtNummer = gerechtNummer;
    }
}
