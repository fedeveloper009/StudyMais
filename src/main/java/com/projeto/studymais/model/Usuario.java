package com.projeto.studymais.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {
    private String nome;
    @Column(nullable = false, unique = true)
    private String email;
    private String senha;
    @Column(name = "google_sub", unique = true)
    private String googleSub;
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider")
    private AuthProvider authProvider = AuthProvider.LOCAL;
    @Column(nullable = false)
    private Integer xp = 0;
    @Column(name = "dias_de_sequencia", nullable = false)
    private Integer diasDeSequencia = 0;
    @Column(name = "tempo_estudado", nullable = false)
    private Long tempoEstudado = 0L;
    @Column(name = "materia_estudada")
    private String materiaEstudada;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_conquistas", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "conquista")
    private List<String> conquistas = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getGoogleSub() {
        return googleSub;
    }

    public void setGoogleSub(String googleSub) {
        this.googleSub = googleSub;
    }

    public AuthProvider getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(AuthProvider authProvider) {
        this.authProvider = authProvider;
    }

    public AuthProvider getProvider() {
        return authProvider;
    }

    public void setProvider(AuthProvider provider) {
        this.authProvider = provider;
    }

    public AuthProvider getProvedor() {
        return authProvider;
    }

    public void setProvedor(AuthProvider provedor) {
        this.authProvider = provedor;
    }

    public Integer getXp() {
        return xp == null ? 0 : xp;
    }

    public void setXp(Integer xp) {
        this.xp = xp == null ? 0 : xp;
    }

    public Integer getDiasDeSequencia() {
        return diasDeSequencia == null ? 0 : diasDeSequencia;
    }

    public void setDiasDeSequencia(Integer diasDeSequencia) {
        this.diasDeSequencia = diasDeSequencia == null ? 0 : diasDeSequencia;
    }

    public Long getTempoEstudado() {
        return tempoEstudado == null ? 0L : tempoEstudado;
    }

    public void setTempoEstudado(Long tempoEstudado) {
        this.tempoEstudado = tempoEstudado == null ? 0L : tempoEstudado;
    }

    public String getMateriaEstudada() {
        return materiaEstudada;
    }

    public void setMateriaEstudada(String materiaEstudada) {
        this.materiaEstudada = materiaEstudada;
    }

    public List<String> getConquistas() {
        return conquistas == null ? new ArrayList<>() : conquistas;
    }

    public void setConquistas(List<String> conquistas) {
        this.conquistas = conquistas == null ? new ArrayList<>() : conquistas;
    }

    public int getUser_id() {
        return userId;
    }

    public void setUser_id(int user_id) {
        this.userId = user_id;
    }
}
