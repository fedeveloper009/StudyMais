package com.projeto.studymais.service;

import com.projeto.studymais.model.Usuario;
import com.projeto.studymais.repository.UsuarioRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SequenciaLoginService {

    private static final ZoneId FUSO_BRASILIA = ZoneId.of("America/Sao_Paulo");
    private final UsuarioRepository usuarioRepository;

    public SequenciaLoginService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
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

        LocalDate hoje = LocalDate.now(FUSO_BRASILIA);
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
