package com.projeto.studymais.service;

import com.projeto.studymais.dto.usuario.UsuarioRequestDTO;
import com.projeto.studymais.dto.usuario.UsuarioResponseDTO;
import com.projeto.studymais.exception.ResourceNotFoundException;
import com.projeto.studymais.model.Usuario;
import com.projeto.studymais.repository.UsuarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO request) {
        Usuario usuario = new Usuario();
        preencherUsuario(usuario, request);
        return paraResponse(usuarioRepository.save(usuario));
    }

    public UsuarioResponseDTO buscarPorId(Integer id) {
        return paraResponse(buscarEntidadePorId(id));
    }

    public List<UsuarioResponseDTO> buscarTodos() {
        return usuarioRepository.findAll().stream().map(this::paraResponse).toList();
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Integer id, UsuarioRequestDTO request) {
        Usuario usuario = buscarEntidadePorId(id);
        preencherUsuario(usuario, request);
        return paraResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public void deletar(Integer id) {
        usuarioRepository.delete(buscarEntidadePorId(id));
    }

    private Usuario buscarEntidadePorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado."));
    }

    private void preencherUsuario(Usuario usuario, UsuarioRequestDTO request) {
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(request.senha());
    }

    private UsuarioResponseDTO paraResponse(Usuario usuario) {
        return new UsuarioResponseDTO(usuario.getUser_id(), usuario.getNome(), usuario.getEmail());
    }
}
