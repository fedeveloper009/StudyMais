package com.projeto.studymais;

import com.projeto.studymais.model.AuthProvider;
import com.projeto.studymais.model.Usuario;
import com.projeto.studymais.security.OAuth2AuthenticationSuccessHandler;
import com.projeto.studymais.service.GoogleUserCreationService;
import com.projeto.studymais.service.GoogleUserService;
import com.projeto.studymais.service.JwtService;
import com.projeto.studymais.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleOAuth2Test {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private GoogleUserCreationService googleUserCreationService;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationFailureHandler failureHandler;

    @Mock
    private OAuth2User googleUser;

    @Mock
    private Authentication authentication;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Test
    void localizaUsuarioGoogleExistente() {
        Usuario existente = usuario("ana@example.com");
        existente.setGoogleSub("google-sub");
        existente.setAuthProvider(AuthProvider.GOOGLE);
        when(usuarioRepository.findByGoogleSub("google-sub")).thenReturn(Optional.of(existente));

        GoogleUserService service = new GoogleUserService(
                usuarioRepository,
                googleUserCreationService
        );

        assertEquals(
                existente,
                service.localizarOuCriar("Ana", "ANA@example.com", "google-sub")
        );
    }

    @Test
    void criaUsuarioGoogleSemSenhaQuandoNaoExiste() {
        Usuario criado = usuario("ana@example.com");
        criado.setGoogleSub("google-sub");
        criado.setAuthProvider(AuthProvider.GOOGLE);
        when(usuarioRepository.findByGoogleSub("google-sub")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmailIgnoreCase("ana@example.com")).thenReturn(Optional.empty());
        when(googleUserCreationService.criar("Ana", "ana@example.com", "google-sub"))
                .thenReturn(criado);

        GoogleUserService service = new GoogleUserService(
                usuarioRepository,
                googleUserCreationService
        );

        Usuario resultado = service.localizarOuCriar("Ana", "ANA@example.com", "google-sub");

        assertEquals(criado, resultado);
        verify(googleUserCreationService).criar("Ana", "ana@example.com", "google-sub");
    }

    @Test
    void sucessoGoogleGeraJwtERedirecionaFrontend() throws Exception {
        Usuario usuario = usuario("ana@example.com");
        when(authentication.getPrincipal()).thenReturn(googleUser);
        when(googleUser.getAttribute("email_verified")).thenReturn(Boolean.TRUE);
        when(googleUser.getAttribute("email")).thenReturn("ANA@example.com");
        when(googleUser.getAttribute("sub")).thenReturn("google-sub");
        when(googleUser.getAttribute("name")).thenReturn("Ana");

        GoogleUserService userService = org.mockito.Mockito.mock(GoogleUserService.class);
        when(userService.localizarOuCriar("Ana", "ana@example.com", "google-sub"))
                .thenReturn(usuario);
        when(jwtService.generateToken("ana@example.com")).thenReturn("jwt-token");

        OAuth2AuthenticationSuccessHandler handler = new OAuth2AuthenticationSuccessHandler(
                userService,
                jwtService,
                failureHandler,
                "http://localhost:3000/oauth2/callback"
        );
        handler.onAuthenticationSuccess(request, response, authentication);

        ArgumentCaptor<String> redirect = ArgumentCaptor.forClass(String.class);
        verify(response).sendRedirect(redirect.capture());
        assertTrue(redirect.getValue().startsWith("http://localhost:3000/oauth2/callback?token="));
        assertTrue(redirect.getValue().contains("jwt-token"));
    }

    private Usuario usuario(String email) {
        Usuario usuario = new Usuario();
        usuario.setNome("Ana");
        usuario.setEmail(email);
        return usuario;
    }
}
