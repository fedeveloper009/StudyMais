package com.projeto.studymais.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "tarefas")
public class Tarefa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tarefaId;
    private String titulo;
    private String descricao;
    private LocalDate dataEntrega;
    @ManyToOne
    @JoinColumn(name = "materia_id", nullable = false)
    private Materia materiaRelacionada;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    private StatusTarefa status;

    @Enumerated(EnumType.STRING)
    private Prioridade prioridade;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(LocalDate dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    public Materia getMateriaRelacionada() {
        return materiaRelacionada;
    }

    public void setMateriaRelacionada(Materia materiaRelacionada) {
        this.materiaRelacionada = materiaRelacionada;
    }

    public Usuario getUser_id() {
        return usuario;
    }

    public void setUser_id(Usuario user_id) {
        this.usuario = user_id;
    }

    public int getTarefa_id() {
        return tarefaId;
    }

    public void setTarefa_id(int tarefa_id) {
        this.tarefaId = tarefa_id;
    }

    public StatusTarefa getStatus() {
        return status;
    }

    public void setStatus(StatusTarefa status) {
        this.status = status;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Prioridade prioridade) {
        this.prioridade = prioridade;
    }
}
