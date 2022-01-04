package com.example.aptedgeservice.model;

import java.util.List;

public class Bestelling {
    private String bestelNummer;
    private String personeelsNummer;
    private List<String> gerechten;

    public String getBestelNummer() {
        return bestelNummer;
    }

    public void setBestelNummer(String bestelNummer) {
        this.bestelNummer = bestelNummer;
    }

    public String getPersoneelsNummer() {
        return personeelsNummer;
    }

    public void setPersoneelsNummer(String personeelsNummer) {
        this.personeelsNummer = personeelsNummer;
    }

    public List<String> getGerechten() {
        return gerechten;
    }

    public void setGerechten(List<String> gerechten) {
        this.gerechten = gerechten;
    }
}
