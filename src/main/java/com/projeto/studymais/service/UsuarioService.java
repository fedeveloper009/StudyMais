package com.projeto.studymais.service;

import com.projeto.studymais.dto.usuario.UsuarioRequestDTO;
import com.projeto.studymais.dto.usuario.UsuarioResponseDTO;
import com.projeto.studymais.dto.usuario.AtualizarNomeRequestDTO;
import com.projeto.studymais.dto.usuario.AlterarSenhaRequestDTO;
import com.projeto.studymais.exception.DuplicateEmailException;
import com.projeto.studymais.exception.ResourceNotFoundException;
import com.projeto.studymais.model.Usuario;
import com.projeto.studymais.repository.UsuarioRepository;
import com.projeto.studymais.security.UsuarioAutenticadoHelper;
import java.util.List;
import java.util.Objects;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioAutenticadoHelper usuarioAutenticadoHelper;
    private final AvatarStorageService avatarStorageService;
    private final ContaSecurityService contaSecurityService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                          UsuarioAutenticadoHelper usuarioAutenticadoHelper) {
        this(usuarioRepository, passwordEncoder, usuarioAutenticadoHelper, null, null);
    }

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            UsuarioAutenticadoHelper usuarioAutenticadoHelper,
            AvatarStorageService avatarStorageService
    ) {
        this(usuarioRepository, passwordEncoder, usuarioAutenticadoHelper, avatarStorageService, null);
    }

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            UsuarioAutenticadoHelper usuarioAutenticadoHelper,
            ContaSecurityService contaSecurityService
    ) {
        this(usuarioRepository, passwordEncoder, usuarioAutenticadoHelper, null, contaSecurityService);
    }

    @Autowired
    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            UsuarioAutenticadoHelper usuarioAutenticadoHelper,
            AvatarStorageService avatarStorageService,
            ContaSecurityService contaSecurityService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioAutenticadoHelper = usuarioAutenticadoHelper;
        this.avatarStorageService = avatarStorageService;
        this.contaSecurityService = contaSecurityService;
    }

    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException();
        }
        Usuario usuario = new Usuario();
        usuario.setEmailVerificado(false);
        preencherUsuario(usuario, request);
        try {
            Usuario salvo = usuarioRepository.save(usuario);
            if (contaSecurityService != null) {
                contaSecurityService.solicitarVerificacao(salvo.getEmail());
            }
            return paraResponse(salvo);
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateEmailException();
        }
    }

    public UsuarioResponseDTO buscarPorId(Integer id) {
        return paraResponse(buscarEntidadeDoUsuario(id, usuarioAutenticadoHelper.obter()));
    }

    public List<UsuarioResponseDTO> buscarTodos() {
        return List.of(paraResponse(usuarioAutenticadoHelper.obter()));
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Integer id, UsuarioRequestDTO request) {
        Usuario usuario = buscarEntidadeDoUsuario(id, usuarioAutenticadoHelper.obter());
        if (!Objects.equals(usuario.getEmail(), request.email())) {
            if (usuarioRepository.existsByEmail(request.email())) {
                throw new DuplicateEmailException();
            }
            throw new IllegalArgumentException("Use o fluxo de alteracao de email para trocar o email.");
        }
        preencherUsuario(usuario, request);
        try {
            return paraResponse(usuarioRepository.save(usuario));
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateEmailException();
        }
    }

    @Transactional
    public UsuarioResponseDTO atualizarNome(Integer id, AtualizarNomeRequestDTO request) {
        Usuario usuarioAutenticado = usuarioAutenticadoHelper.obter();
        Usuario usuario = buscarEntidadeDoUsuario(id, usuarioAutenticado);
        String nome = request.nome() == null ? null : request.nome().trim();
        if (nome == null || nome.length() < 2 || nome.length() > 100) {
            throw new IllegalArgumentException("O nome deve ter entre 2 e 100 caracteres.");
        }
        usuarioRepository.atualizarNomePorId(usuario.getUser_id(), nome);
        usuario.setNome(nome);
        return paraResponse(usuario);
    }

    @Transactional
    public UsuarioResponseDTO alterarSenha(Integer id, AlterarSenhaRequestDTO request) {
        Usuario usuario = buscarEntidadeDoUsuario(id, usuarioAutenticadoHelper.obter());
        if (request == null
                || request.senhaAtual() == null
                || request.senhaAtual().isBlank()
                || request.novaSenha() == null
                || request.novaSenha().length() < 6
                || request.novaSenha().length() > 72
                || request.confirmacaoNovaSenha() == null
                || request.confirmacaoNovaSenha().isBlank()) {
            throw new IllegalArgumentException("Os dados da nova senha sao invalidos.");
        }
        if (!request.novaSenha().equals(request.confirmacaoNovaSenha())) {
            throw new IllegalArgumentException("A confirmacao da nova senha nao coincide.");
        }
        if (usuario.getSenha() == null
                || !passwordEncoder.matches(request.senhaAtual(), usuario.getSenha())) {
            throw new BadCredentialsException("A senha atual e invalida.");
        }
        usuario.setSenha(passwordEncoder.encode(request.novaSenha()));
        return paraResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO alterarFotoPerfil(Integer id, MultipartFile foto) {
        Usuario usuario = buscarEntidadeDoUsuario(id, usuarioAutenticadoHelper.obter());
        String fotoUrl = avatarStorageService.upload(usuario.getUser_id(), foto);
        usuario.setFotoPerfilUrl(fotoUrl);
        return paraResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO removerFotoPerfil(Integer id) {
        Usuario usuario = buscarEntidadeDoUsuario(id, usuarioAutenticadoHelper.obter());
        avatarStorageService.delete(usuario.getUser_id());
        usuario.setFotoPerfilUrl(null);
        return paraResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public void deletar(Integer id) {
        usuarioRepository.delete(buscarEntidadeDoUsuario(id, usuarioAutenticadoHelper.obter()));
    }

    private Usuario buscarEntidadeDoUsuario(Integer id, Usuario usuarioAutenticado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado."));
        if (!Objects.equals(usuario.getUser_id(), usuarioAutenticado.getUser_id())) {
            throw new AccessDeniedException("Acesso negado.");
        }
        return usuario;
    }

    private void preencherUsuario(Usuario usuario, UsuarioRequestDTO request) {
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(codificarSenha(usuario.getSenha(), request.senha()));
        usuario.setXp(request.xp() == null ? usuario.getXp() : request.xp());
        usuario.setDiasDeSequencia(request.diasDeSequencia() == null ? usuario.getDiasDeSequencia() : request.diasDeSequencia());
        usuario.setTempoEstudado(request.tempoEstudado() == null ? usuario.getTempoEstudado() : request.tempoEstudado());
        usuario.setMateriaEstudada(request.materiaEstudada() == null ? usuario.getMateriaEstudada() : request.materiaEstudada());
        usuario.setConquistas(request.conquistas() == null ? usuario.getConquistas() : request.conquistas());
    }

    private String codificarSenha(String senhaAtual, String senhaInformada) {
        return passwordEncoder.encode(senhaInformada);
    }

    private UsuarioResponseDTO paraResponse(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getUser_id(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getXp(),
                usuario.getDiasDeSequencia(),
                usuario.getTempoEstudado(),
                usuario.getMateriaEstudada(),
                usuario.getConquistas(),
                usuario.getFotoPerfilUrl()
        );
    }
}
