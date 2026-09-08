package com.projeto.studymais.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DevEmailService implements EmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DevEmailService.class);

    private final String frontendUrl;

    public DevEmailService(@Value("${app.frontend-url:http://localhost:3000}") String frontendUrl) {
        this.frontendUrl = frontendUrl.replaceAll("/$", "");
    }

    @Override
    public void enviarVerificacao(String destinatario, String token) {
        registrar(destinatario, "VERIFICACAO", frontendUrl + "/verificar-email?token=" + token);
    }

    @Override
    public void enviarAlteracaoEmail(String destinatario, String token) {
        registrar(destinatario, "ALTERACAO_EMAIL", frontendUrl + "/change-email?token=" + token);
    }

    @Override
    public void enviarRecuperacaoSenha(String destinatario, String token) {
        registrar(destinatario, "RECUPERACAO_SENHA", frontendUrl + "/reset-password?token=" + token);
    }

    private void registrar(String destinatario, String tipo, String link) {
        LOGGER.info("[DEV EMAIL] destinatario={} tipo={} link={}", destinatario, tipo, link);
    }
}
