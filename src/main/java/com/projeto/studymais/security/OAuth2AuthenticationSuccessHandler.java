package com.projeto.studymais.security;

import com.projeto.studymais.model.Usuario;
import com.projeto.studymais.service.GoogleOAuth2Exception;
import com.projeto.studymais.service.GoogleUserService;
import com.projeto.studymais.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final GoogleUserService googleUserService;
    private final JwtService jwtService;
    private final AuthenticationFailureHandler failureHandler;
    private final String frontendSuccessUrl;

    public OAuth2AuthenticationSuccessHandler(
            GoogleUserService googleUserService,
            JwtService jwtService,
            AuthenticationFailureHandler failureHandler,
            @Value("${app.oauth2.frontend-success-url}") String frontendSuccessUrl
    ) {
        this.googleUserService = googleUserService;
        this.jwtService = jwtService;
        this.failureHandler = failureHandler;
        this.frontendSuccessUrl = frontendSuccessUrl;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        try {
            if (!(authentication.getPrincipal() instanceof OAuth2User googleUser)) {
                throw new GoogleOAuth2Exception("Principal OAuth2 invalido.");
            }
            Boolean emailVerified = googleUser.getAttribute("email_verified");
            String email = googleUser.getAttribute("email");
            String googleSub = googleUser.getAttribute("sub");
            if (!Boolean.TRUE.equals(emailVerified)
                    || email == null || email.isBlank()
                    || googleSub == null || googleSub.isBlank()) {
                throw new GoogleOAuth2Exception("O Google nao forneceu um email verificado.");
            }

            String nome = googleUser.getAttribute("name");
            if (nome == null || nome.isBlank()) {
                int at = email.indexOf('@');
                nome = at > 0 ? email.substring(0, at).trim() : "Usuario Google";
            }
            Usuario usuario = googleUserService.localizarOuCriar(
                    nome,
                    email.trim().toLowerCase(Locale.ROOT),
                    googleSub
            );
            String token = jwtService.generateToken(usuario.getEmail());
            String redirectUrl = UriComponentsBuilder.fromUriString(frontendSuccessUrl)
                    .queryParam("token", token)
                    .build()
                    .encode()
                    .toUriString();
            response.sendRedirect(redirectUrl);
        } catch (RuntimeException exception) {
            failureHandler.onAuthenticationFailure(
                    request,
                    response,
                    new BadCredentialsException("Falha no login OAuth2.", exception)
            );
        }
    }
}
