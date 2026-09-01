package com.projeto.studymais.service;

import com.projeto.studymais.dto.usuario.UsuarioRequestDTO;
import com.projeto.studymais.dto.usuario.UsuarioResponseDTO;
import com.projeto.studymais.exception.DuplicateEmailException;
import com.projeto.studymais.exception.ResourceNotFoundException;
import com.projeto.studymais.model.Usuario;
import com.projeto.studymais.repository.UsuarioRepository;
import com.projeto.studymais.security.UsuarioAutenticadoHelper;
import java.util.List;
import java.util.Objects;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioAutenticadoHelper usuarioAutenticadoHelper;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            UsuarioAutenticadoHelper usuarioAutenticadoHelper
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioAutenticadoHelper = usuarioAutenticadoHelper;
    }

    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException();
        }
        Usuario usuario = new Usuario();
        preencherUsuario(usuario, request);
        try {
            return paraResponse(usuarioRepository.save(usuario));
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
        if (!Objects.equals(usuario.getEmail(), request.email())
                && usuarioRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException();
        }
        preencherUsuario(usuario, request);
        try {
            return paraResponse(usuarioRepository.save(usuario));
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateEmailException();
        }
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
        if (senhaInformada.equals(senhaAtual) || isBcryptHash(senhaInformada)) {
            return senhaInformada;
        }
        return passwordEncoder.encode(senhaInformada);
    }

    private boolean isBcryptHash(String senha) {
        return senha.matches("^\\$2[ayb]?\\$\\d{2}\\$[./A-Za-z0-9]{53}$");
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
                usuario.getConquistas()
        );
    }
}
