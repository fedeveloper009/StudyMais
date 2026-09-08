package com.projeto.studymais.service;

import com.projeto.studymais.dto.auth.EmailRequestDTO;
import com.projeto.studymais.dto.auth.ResetPasswordRequestDTO;
import com.projeto.studymais.exception.DuplicateEmailException;
import com.projeto.studymais.model.TokenConta;
import com.projeto.studymais.model.TokenTipo;
import com.projeto.studymais.model.Usuario;
import com.projeto.studymais.repository.TokenContaRepository;
import com.projeto.studymais.repository.UsuarioRepository;
import com.projeto.studymais.security.UsuarioAutenticadoHelper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContaSecurityService {
    private final UsuarioRepository usuarioRepository;
    private final TokenContaRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioAutenticadoHelper autenticadoHelper;
    private final EmailService emailService;
    private final SecureRandom random = new SecureRandom();
    private final Map<String, Instant> ultimoReenvioVerificacao = new ConcurrentHashMap<>();
    private final Duration tokenTtl = Duration.ofMinutes(30);

    public ContaSecurityService(
            UsuarioRepository usuarioRepository,
            TokenContaRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            UsuarioAutenticadoHelper autenticadoHelper,
            EmailService emailService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.autenticadoHelper = autenticadoHelper;
        this.emailService = emailService;
    }

    @Transactional
    public void solicitarVerificacao(String email) {
        usuarioRepository.findByEmailIgnoreCase(normalizar(email))
                .filter(usuario -> !usuario.isEmailVerificado())
                .ifPresent(usuario -> emitir(usuario, TokenTipo.VERIFICACAO_EMAIL, usuario.getEmail(), emailService::enviarVerificacao));
    }

    @Transactional
    public void reenviarVerificacao(String email) {
        String emailNormalizado = normalizar(email);
        Instant agora = Instant.now();
        Instant ultimoEnvio = ultimoReenvioVerificacao.get(emailNormalizado);
        if (ultimoEnvio != null && ultimoEnvio.plusSeconds(60).isAfter(agora)) {
            return;
        }
        usuarioRepository.findByEmailIgnoreCase(emailNormalizado)
                .filter(usuario -> !usuario.isEmailVerificado())
                .ifPresent(usuario -> {
                    emitir(usuario, TokenTipo.VERIFICACAO_EMAIL, usuario.getEmail(), emailService::enviarVerificacao);
                    ultimoReenvioVerificacao.put(emailNormalizado, agora);
                });
    }

    @Transactional
    public void verificarEmail(String token) {
        TokenConta conta = obterToken(token, TokenTipo.VERIFICACAO_EMAIL);
        conta.getUsuario().setEmailVerificado(true);
        conta.setUsadoEm(Instant.now());
        usuarioRepository.save(conta.getUsuario());
        tokenRepository.save(conta);
    }

    @Transactional
    public void solicitarAlteracaoEmail(EmailRequestDTO request) {
        Usuario usuario = autenticadoHelper.obter();
        String novoEmail = normalizar(request.email());
        if (usuario.getEmail().equalsIgnoreCase(novoEmail)) {
            throw new IllegalArgumentException("O novo email deve ser diferente do atual.");
        }
        if (usuarioRepository.existsByEmailIgnoreCase(novoEmail)) {
            throw new DuplicateEmailException();
        }
        emitir(usuario, TokenTipo.ALTERACAO_EMAIL, novoEmail, emailService::enviarAlteracaoEmail);
    }

    @Transactional
    public void confirmarAlteracaoEmail(String token) {
        TokenConta conta = obterToken(token, TokenTipo.ALTERACAO_EMAIL);
        if (usuarioRepository.existsByEmailIgnoreCase(conta.getEmailDestino())) {
            throw new DuplicateEmailException();
        }
        Usuario usuario = conta.getUsuario();
        usuario.setEmail(conta.getEmailDestino());
        usuario.setEmailVerificado(true);
        usuarioRepository.save(usuario);
        conta.setUsadoEm(Instant.now());
        tokenRepository.save(conta);
    }

    @Transactional
    public void solicitarRecuperacaoSenha(String email) {
        usuarioRepository.findByEmailIgnoreCase(normalizar(email))
                .filter(usuario -> usuario.getSenha() != null)
                .ifPresent(usuario -> emitir(usuario, TokenTipo.RECUPERACAO_SENHA, usuario.getEmail(), emailService::enviarRecuperacaoSenha));
    }

    @Transactional
    public void redefinirSenha(ResetPasswordRequestDTO request) {
        TokenConta conta = obterToken(request.token(), TokenTipo.RECUPERACAO_SENHA);
        Usuario usuario = conta.getUsuario();
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        usuario.setEmailVerificado(true);
        usuarioRepository.save(usuario);
        conta.setUsadoEm(Instant.now());
        tokenRepository.save(conta);
    }

    private void emitir(Usuario usuario, TokenTipo tipo, String emailDestino, TokenSender sender) {
        tokenRepository.removerPorUsuarioETipo(usuario.getUser_id(), tipo);
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = HexFormat.of().formatHex(bytes);
        TokenConta conta = new TokenConta();
        conta.setTokenHash(hash(token));
        conta.setTipo(tipo);
        conta.setUsuario(usuario);
        conta.setEmailDestino(emailDestino);
        conta.setExpiraEm(Instant.now().plus(tokenTtl));
        tokenRepository.save(conta);
        sender.send(emailDestino, token);
    }

    private TokenConta obterToken(String token, TokenTipo tipo) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token invalido ou expirado.");
        }
        TokenConta conta = tokenRepository.findByTokenHashAndTipo(hash(token), tipo)
                .orElseThrow(() -> new IllegalArgumentException("Token invalido ou expirado."));
        if (conta.getUsadoEm() != null || conta.getExpiraEm().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Token invalido ou expirado.");
        }
        return conta;
    }

    private String normalizar(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String hash(String token) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Algoritmo de hash indisponivel.", exception);
        }
    }

    @FunctionalInterface
    private interface TokenSender {
        void send(String email, String token);
    }
}
