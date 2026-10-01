package com.projeto.studymais.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class SignificadoSimbolo {
    private String simbolo;
    private String significado;
    protected SignificadoSimbolo() {}
    public SignificadoSimbolo(String simbolo,String significado){this.simbolo=simbolo;this.significado=significado;}
    public String getSimbolo(){return simbolo;} public String getSignificado(){return significado;}
}
