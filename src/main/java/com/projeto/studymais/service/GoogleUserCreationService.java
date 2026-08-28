package com.projeto.studymais.service;

import com.projeto.studymais.model.AuthProvider;
import com.projeto.studymais.model.Usuario;
import com.projeto.studymais.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GoogleUserCreationService {

    private final UsuarioRepository usuarioRepository;

    public GoogleUserCreationService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Usuario criar(String nome, String email, String googleSub) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setGoogleSub(googleSub);
        usuario.setAuthProvider(AuthProvider.GOOGLE);
        usuario.setSenha(null);
        return usuarioRepository.saveAndFlush(usuario);
    }
}
