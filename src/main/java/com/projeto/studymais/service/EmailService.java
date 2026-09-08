package com.projeto.studymais.service;

public interface EmailService {
    void enviarVerificacao(String destinatario, String token);

    void enviarAlteracaoEmail(String destinatario, String token);

    void enviarRecuperacaoSenha(String destinatario, String token);
}
