package com.projeto.studymais.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="questoes_apresentadas", indexes=@Index(name="idx_apresentada_sessao", columnList="sessao_id"))
public class QuestaoApresentada {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="sessao_id", nullable=false, length=36) private String sessaoId;
    @ManyToOne(optional=false) @JoinColumn(name="usuario_id", nullable=false) private Usuario usuario;
    @ManyToOne(optional=false) @JoinColumn(name="questao_id", nullable=false) private Questao questao;
    @Column(nullable=false) private Instant apresentadaEm=Instant.now();
    private Boolean correta;
    @Column(length=2000) private String respostaEnviada;
    protected QuestaoApresentada() {}
    public QuestaoApresentada(String sessaoId, Usuario usuario, Questao questao){this.sessaoId=sessaoId;this.usuario=usuario;this.questao=questao;}
    public Long getId(){return id;} public String getSessaoId(){return sessaoId;} public Usuario getUsuario(){return usuario;} public Questao getQuestao(){return questao;} public Boolean getCorreta(){return correta;} public String getRespostaEnviada(){return respostaEnviada;} public Instant getApresentadaEm(){return apresentadaEm;}
    public void responder(String resposta, boolean correta){this.respostaEnviada=resposta;this.correta=correta;}
}
