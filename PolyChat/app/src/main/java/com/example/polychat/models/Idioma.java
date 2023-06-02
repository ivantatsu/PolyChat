package com.example.polychat.models;

public class Idioma {
    private String codIdioma;

    public String getLabelIdioma() {
        return labelIdioma;
    }

    public void setLabelIdioma(String labelIdioma) {
        this.labelIdioma = labelIdioma;
    }

    private String labelIdioma;

    public Idioma() {
    }

    public Idioma(String codIdioma, String labelIdioma) {
        this.codIdioma = codIdioma;
        this.labelIdioma = labelIdioma;
    }

    public String getCodIdioma() {
        return codIdioma;
    }

    public void setCodIdioma(String codIdioma) {
        this.codIdioma = codIdioma;
    }
}