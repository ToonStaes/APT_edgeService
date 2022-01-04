package com.example.aptedgeservice.model;

import java.util.List;

public class CompleteBestelling {
    private String bestelNummer;
    private Personeel personeelslid;
    private List<Gerecht> gerechten;

    public CompleteBestelling(String bestelNummer, Personeel personeelslid, List<Gerecht> gerechten) {
        this.bestelNummer = bestelNummer;
        this.personeelslid = personeelslid;
        this.gerechten = gerechten;
    }

    public String getBestelNummer() {
        return bestelNummer;
    }

    public Personeel getPersoneelslid() {
        return personeelslid;
    }

    public List<Gerecht> getGerechten() {
        return gerechten;
    }
}
