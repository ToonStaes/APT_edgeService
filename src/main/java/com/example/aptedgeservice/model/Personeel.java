package com.example.aptedgeservice.model;

public class Personeel {
    private String voornaam;
    private String achternaam;
    private Functie functie;
    private String personeelsnummer;

    public Personeel() {
    }

    public Personeel(String voornaam, String achternaam, Functie functie, String personeelsnummer) {
        this.voornaam = voornaam;
        this.achternaam = achternaam;
        this.functie = functie;
        this.personeelsnummer = personeelsnummer;
    }

    public String getVoornaam() {
        return voornaam;
    }

    public void setVoornaam(String voornaam) {
        this.voornaam = voornaam;
    }

    public String getAchternaam() {
        return achternaam;
    }

    public void setAchternaam(String achternaam) {
        this.achternaam = achternaam;
    }

    public Functie getFunctie() {
        return functie;
    }

    public void setFunctie(Functie functie) {
        this.functie = functie;
    }

    public String getPersoneelsnummer() {
        return personeelsnummer;
    }

    public void setPersoneelsnummer(String personeelsnummer) {
        this.personeelsnummer = personeelsnummer;
    }
}
