package com.projeto.studymais.controller;

import com.projeto.studymais.dto.auth.LoginRequestDTO;
import com.projeto.studymais.dto.auth.LoginResponseDTO;
import com.projeto.studymais.service.JwtService;
import com.projeto.studymais.service.ContaSecurityService;
import com.projeto.studymais.dto.auth.EmailRequestDTO;
import com.projeto.studymais.dto.auth.ResetPasswordRequestDTO;
import com.projeto.studymais.dto.auth.TokenRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ObjectProvider<ContaSecurityService> contaSecurityService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
                          ObjectProvider<ContaSecurityService> contaSecurityService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.contaSecurityService = contaSecurityService;
    }

    /**
     * Valida email e senha e devolve um JWT para as requisicoes protegidas.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha())
        );
        return ResponseEntity.ok(new LoginResponseDTO(jwtService.generateToken(authentication.getName())));
    }

    @PostMapping("/verify-email/request")
    public ResponseEntity<Void> solicitarVerificacao(@Valid @RequestBody EmailRequestDTO request) {
        contaSecurityService.getObject().reenviarVerificacao(request.email());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/verify-email")
    public ResponseEntity<Void> verificarEmail(@Valid @RequestBody TokenRequestDTO request) {
        contaSecurityService.getObject().verificarEmail(request.token());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/verificar-email")
    public ResponseEntity<Void> verificarEmailPorLink(@RequestParam String token) {
        contaSecurityService.getObject().verificarEmail(token);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reenviar-verificacao")
    public ResponseEntity<Void> reenviarVerificacao(@Valid @RequestBody EmailRequestDTO request) {
        contaSecurityService.getObject().reenviarVerificacao(request.email());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/email-change/request")
    public ResponseEntity<Void> solicitarAlteracaoEmail(@Valid @RequestBody EmailRequestDTO request) {
        contaSecurityService.getObject().solicitarAlteracaoEmail(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/email-change/confirm")
    public ResponseEntity<Void> confirmarAlteracaoEmail(@Valid @RequestBody TokenRequestDTO request) {
        contaSecurityService.getObject().confirmarAlteracaoEmail(request.token());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password-reset/request")
    public ResponseEntity<Void> solicitarRecuperacaoSenha(@Valid @RequestBody EmailRequestDTO request) {
        contaSecurityService.getObject().solicitarRecuperacaoSenha(request.email());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/esqueci-senha")
    public ResponseEntity<String> esqueciSenha(@Valid @RequestBody EmailRequestDTO request) {
        contaSecurityService.getObject().solicitarRecuperacaoSenha(request.email());
        return ResponseEntity.accepted()
                .body("Se o e-mail estiver cadastrado, voce recebera um link para redefinir sua senha.");
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<Void> redefinirSenha(@Valid @RequestBody ResetPasswordRequestDTO request) {
        contaSecurityService.getObject().redefinirSenha(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<Void> redefinirSenhaPorLink(@Valid @RequestBody ResetPasswordRequestDTO request) {
        contaSecurityService.getObject().redefinirSenha(request);
        return ResponseEntity.noContent().build();
    }
}
