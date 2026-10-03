package com.projeto.studymais.service;

import com.projeto.studymais.model.Usuario;
import com.projeto.studymais.repository.UsuarioRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SequenciaLoginService {

    private final UsuarioRepository usuarioRepository;
    private final Clock relogio;

    public SequenciaLoginService(UsuarioRepository usuarioRepository) {
        this(usuarioRepository, Clock.system(ZoneId.of("America/Sao_Paulo")));
    }

    SequenciaLoginService(UsuarioRepository usuarioRepository, Clock relogio) {
        this.usuarioRepository = usuarioRepository;
        this.relogio = relogio;
    }

    @Transactional
    public void registrarLogin(String email) {
        Usuario encontrado = usuarioRepository.findByEmailIgnoreCase(email).orElse(null);
        if (encontrado == null) {
            return;
        }
        // Serializa logins concorrentes do mesmo usuario para nao perder incremento.
        Usuario usuario = usuarioRepository.findByIdForUpdate(encontrado.getUser_id())
                .orElse(encontrado);

        LocalDate hoje = LocalDate.now(relogio);
        LocalDate ultimoLogin = usuario.getUltimaDataLogin();
        if (hoje.equals(ultimoLogin)) {
            return;
        }
        usuario.setDiasDeSequencia(hoje.minusDays(1).equals(ultimoLogin)
                ? usuario.getDiasDeSequencia() + 1
                : 1);
        usuario.setUltimaDataLogin(hoje);
        usuarioRepository.save(usuario);
    }
}
