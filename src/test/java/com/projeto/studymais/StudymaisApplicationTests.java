package com.projeto.studymais;

import com.projeto.studymais.controller.AuthController;
import com.projeto.studymais.controller.UsuarioController;
import com.projeto.studymais.exception.DuplicateEmailException;
import com.projeto.studymais.exception.GlobalExceptionHandler;
import com.projeto.studymais.security.JsonAuthenticationEntryPoint;
import com.projeto.studymais.service.JwtService;
import com.projeto.studymais.service.UsuarioService;
import io.jsonwebtoken.JwtException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AuthController.class, UsuarioController.class})
@Import({
        com.projeto.studymais.config.SecurityConfig.class,
        GlobalExceptionHandler.class,
        JsonAuthenticationEntryPoint.class
})
class StudymaisApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private UsuarioService usuarioService;

    @Test
    void loginComCredenciaisValidasRetornaToken() throws Exception {
        Authentication authentication = new org.springframework.security.authentication
                .UsernamePasswordAuthenticationToken("ana@example.com", null, List.of());
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(jwtService.generateToken("ana@example.com")).thenReturn("jwt-valido");

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"ana@example.com","senha":"senha123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-valido"));
    }

    @Test
    void loginComCredenciaisInvalidasRetorna401() throws Exception {
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("invalid"));

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"ana@example.com","senha":"errada"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void rotaProtegidaSemTokenRetorna401() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void tokenInvalidoOuExpiradoRetorna401() throws Exception {
        when(jwtService.extractUsername("jwt-expirado"))
                .thenThrow(new JwtException("Token expirado."));

        mockMvc.perform(get("/api/usuarios")
                        .header("Authorization", "Bearer jwt-expirado"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void acessoAoRecursoDeOutroUsuarioRetorna403() throws Exception {
        UserDetails user = User.withUsername("ana@example.com")
                .password("senha")
                .roles("USER")
                .build();
        when(jwtService.extractUsername("jwt-valido")).thenReturn(user.getUsername());
        when(userDetailsService.loadUserByUsername(user.getUsername())).thenReturn(user);
        when(jwtService.isTokenValid("jwt-valido", user)).thenReturn(true);
        doThrow(new org.springframework.security.access.AccessDeniedException("Acesso negado."))
                .when(usuarioService).buscarPorId(2);

        mockMvc.perform(get("/api/usuarios/2")
                        .header("Authorization", "Bearer jwt-valido"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void cadastroComEmailDuplicadoRetorna409Padronizado() throws Exception {
        when(usuarioService.criar(any()))
                .thenThrow(new DuplicateEmailException());

        mockMvc.perform(post("/api/usuarios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Ana","email":"ana@example.com","senha":"senha123"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Email ja cadastrado."));
    }
}
