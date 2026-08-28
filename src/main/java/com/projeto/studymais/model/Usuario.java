package com.projeto.studymais.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

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

    public int getUser_id() {
        return userId;
    }

    public void setUser_id(int user_id) {
        this.userId = user_id;
    }
}
