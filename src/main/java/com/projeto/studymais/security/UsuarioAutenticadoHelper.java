package com.projeto.studymais.security;

import com.projeto.studymais.model.Usuario;
import com.projeto.studymais.repository.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UsuarioAutenticadoHelper {

    private final UsuarioRepository usuarioRepository;

    public UsuarioAutenticadoHelper(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Retorna o usuario associado ao JWT da requisicao atual.
     */
    public Usuario obter() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken
                || authentication.getName() == null
                || authentication.getName().isBlank()) {
            throw new AuthenticationCredentialsNotFoundException("Autenticacao necessaria.");
        }

        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException(
                        "Usuario autenticado nao encontrado."
                ));
    }
}
