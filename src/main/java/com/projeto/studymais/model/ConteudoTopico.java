package com.projeto.studymais.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/** Material introdutório separado do banco de questões. Uma linha pode descrever
 * o tópico inteiro (subtopico nulo) ou um de seus subtópicos. */
@Entity
@Table(name="conteudos_topicos")
public class ConteudoTopico {
    @Id @Column(length=160) private String id;
    @Column(nullable=false,length=100) private String materia;
    @Column(nullable=false,length=100) private String topico;
    @Column(length=150) private String subtopico;
    @Column(length=2000) private String resumo;
    @ElementCollection
    @CollectionTable(name="conteudo_topico_formulas",joinColumns=@JoinColumn(name="conteudo_id"))
    @OrderColumn(name="ordem")
    private List<FormulaTopico> formulas=new ArrayList<>();
    @ElementCollection
    @CollectionTable(name="conteudo_topico_simbolos",joinColumns=@JoinColumn(name="conteudo_id"))
    @OrderColumn(name="ordem")
    private List<SignificadoSimbolo> significadosSimbolos=new ArrayList<>();
    @Column(name="exemplo_resolvido",length=3000) private String exemploResolvido;
    @Column(name="revisao_necessaria",nullable=false) private boolean revisaoNecessaria=true;
    @Column(name="nota_revisao",length=500) private String notaRevisao;
    protected ConteudoTopico() {}
    public ConteudoTopico(String id,String materia,String topico,String subtopico,String notaRevisao){this.id=id;this.materia=materia;this.topico=topico;this.subtopico=subtopico;this.notaRevisao=notaRevisao;}
    public ConteudoTopico(String id,String materia,String topico,String subtopico,String resumo,boolean revisaoNecessaria,String notaRevisao){this(id,materia,topico,subtopico,notaRevisao);this.resumo=resumo;this.revisaoNecessaria=revisaoNecessaria;}
    public String getId(){return id;} public String getMateria(){return materia;} public String getTopico(){return topico;} public String getSubtopico(){return subtopico;}
    public String getResumo(){return resumo;} public List<FormulaTopico> getFormulas(){return formulas;} public List<SignificadoSimbolo> getSignificadosSimbolos(){return significadosSimbolos;}
    public String getExemploResolvido(){return exemploResolvido;} public boolean isRevisaoNecessaria(){return revisaoNecessaria;} public String getNotaRevisao(){return notaRevisao;}
}
