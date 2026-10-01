package com.projeto.studymais.model;
import jakarta.persistence.Embeddable;
@Embeddable
public class FormulaTopico {
    private String expressao;
    private String descricao;
    protected FormulaTopico() {}
    public FormulaTopico(String expressao,String descricao){this.expressao=expressao;this.descricao=descricao;}
    public String getExpressao(){return expressao;} public String getDescricao(){return descricao;}
}
