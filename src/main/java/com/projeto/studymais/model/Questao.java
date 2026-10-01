package com.projeto.studymais.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questoes")
public class Questao {
    @Id
    @Column(length = 80)
    private String id;
    @Column(name="materia", nullable=false, length=100, columnDefinition="varchar(100) default 'Matemática'")
    private String materia;
    @Column(name="topico", nullable=false, length=100, columnDefinition="varchar(100) default 'Frações'")
    private String topico;
    @Column(name="subtopico", nullable=false, length=150, columnDefinition="varchar(150) default 'Geral'")
    private String subtopico;
    @Column(nullable = false, length = 2000)
    private String enunciado;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoRespostaQuestao tipoResposta;
    @ElementCollection
    @CollectionTable(name = "questao_alternativas", joinColumns = @JoinColumn(name = "questao_id"))
    @OrderColumn(name = "ordem")
    @Column(name = "alternativa", nullable = false, length = 1000)
    private List<String> alternativas = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name = "questao_respostas", joinColumns = @JoinColumn(name = "questao_id"))
    @Column(name = "resposta", nullable = false, length = 1000)
    private List<String> respostasCorretas = new ArrayList<>();
    @Column(length = 2000)
    private String explicacao;
    @Column(length = 30)
    private String dificuldade;

    protected Questao() {}
    public Questao(String id, String subtopico, String enunciado, TipoRespostaQuestao tipo, List<String> alternativas, List<String> respostas, String explicacao, String dificuldade) {
        this.id=id; this.materia="Matemática"; this.topico="Frações"; this.subtopico=subtopico; this.enunciado=enunciado; this.tipoResposta=tipo;
        this.alternativas=new ArrayList<>(alternativas); this.respostasCorretas=new ArrayList<>(respostas); this.explicacao=explicacao; this.dificuldade=dificuldade;
    }
    public String getId(){return id;} public String getMateria(){return materia;} public String getTopico(){return topico;} public String getSubtopico(){return subtopico;} public String getEnunciado(){return enunciado;}
    public void definirHierarquia(String materia,String topico,String subtopico){this.materia=materia;this.topico=topico;this.subtopico=subtopico;}
    public TipoRespostaQuestao getTipoResposta(){return tipoResposta;} public List<String> getAlternativas(){return alternativas;}
    public List<String> getRespostasCorretas(){return respostasCorretas;} public String getExplicacao(){return explicacao;} public String getDificuldade(){return dificuldade;}
}
