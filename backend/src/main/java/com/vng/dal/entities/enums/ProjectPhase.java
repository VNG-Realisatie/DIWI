package com.vng.dal.entities.enums;

public enum ProjectPhase {
    INITIATIEF_FASE("1_Initiatieffase"),
    PROJECT_FASE("2_projectfase"),
    VERGUNNINGS_FASE("3_vergunningsfase"),
    REALISATIE_FASE("4_realisatiefase"),
    OPLEVERINGS_FASE("5_opleveringsfase");

    public final String name;

    private ProjectPhase(String name) {
        this.name = name;
    }
}
