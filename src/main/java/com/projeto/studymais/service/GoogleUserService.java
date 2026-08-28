package com.projeto.studymais.service;

import com.projeto.studymais.model.AuthProvider;
import com.projeto.studymais.model.Usuario;
import com.projeto.studymais.repository.UsuarioRepository;
import java.util.Locale;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GoogleUserService {

    private final UsuarioRepository usuarioRepository;
    private final GoogleUserCreationService googleUserCreationService;

    public GoogleUserService(
            UsuarioRepository usuarioRepository,
            GoogleUserCreationService googleUserCreationService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.googleUserCreationService = googleUserCreationService;
    }

    /**
     * Vincula o sub do Google a um email existente ou cria uma conta sem senha.
     * A constraint unique do banco e usada como ultima protecao contra corridas.
     */
    @Transactional
    public Usuario localizarOuCriar(String nome, String email, String googleSub) {
        String emailNormalizado = email.trim().toLowerCase(Locale.ROOT);

        Usuario porGoogle = usuarioRepository.findByGoogleSub(googleSub).orElse(null);
        if (porGoogle != null) {
            if (!porGoogle.getEmail().equalsIgnoreCase(emailNormalizado)) {
                throw new GoogleOAuth2Exception("A identidade Google nao corresponde ao email informado.");
            }
            return porGoogle;
        }

        Usuario porEmail = usuarioRepository.findByEmailIgnoreCase(emailNormalizado).orElse(null);
        if (porEmail != null) {
            if (porEmail.getGoogleSub() != null && !porEmail.getGoogleSub().equals(googleSub)) {
                throw new GoogleOAuth2Exception("O email ja esta vinculado a outra conta Google.");
            }
            porEmail.setGoogleSub(googleSub);
            porEmail.setAuthProvider(AuthProvider.GOOGLE);
            if (porEmail.getNome() == null || porEmail.getNome().isBlank()) {
                porEmail.setNome(nome);
            }
            return usuarioRepository.save(porEmail);
        }

        try {
            return googleUserCreationService.criar(nome, emailNormalizado, googleSub);
        } catch (DataIntegrityViolationException exception) {
            return usuarioRepository.findByGoogleSub(googleSub)
                    .or(() -> usuarioRepository.findByEmailIgnoreCase(emailNormalizado))
                    .orElseThrow(() -> new GoogleOAuth2Exception("Nao foi possivel criar a conta Google.", exception));
        }
    }
}
