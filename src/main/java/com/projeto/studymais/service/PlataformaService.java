package com.projeto.studymais.service;

import com.projeto.studymais.dto.plataforma.PlataformaRequestDTO;
import com.projeto.studymais.dto.plataforma.PlataformaResponseDTO;
import com.projeto.studymais.exception.ResourceNotFoundException;
import com.projeto.studymais.model.Plataforma;
import com.projeto.studymais.model.Usuario;
import com.projeto.studymais.repository.PlataformaRepository;
import com.projeto.studymais.security.UsuarioAutenticadoHelper;
import java.util.List;
import java.util.Objects;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlataformaService {

    private final PlataformaRepository plataformaRepository;
    private final UsuarioAutenticadoHelper usuarioAutenticadoHelper;

    public PlataformaService(
            PlataformaRepository plataformaRepository,
            UsuarioAutenticadoHelper usuarioAutenticadoHelper
    ) {
        this.plataformaRepository = plataformaRepository;
        this.usuarioAutenticadoHelper = usuarioAutenticadoHelper;
    }

    @Transactional
    public PlataformaResponseDTO criar(PlataformaRequestDTO request) {
        Usuario usuario = usuarioAutenticadoHelper.obter();
        Plataforma plataforma = new Plataforma();
        preencherPlataforma(plataforma, request, usuario);
        return paraResponse(plataformaRepository.save(plataforma));
    }

    public PlataformaResponseDTO buscarPorId(Integer id) {
        return paraResponse(buscarEntidadeDoUsuario(id, usuarioAutenticadoHelper.obter()));
    }

    public List<PlataformaResponseDTO> buscarTodos() {
        Usuario usuario = usuarioAutenticadoHelper.obter();
        return plataformaRepository.findAllByUsuario(usuario).stream()
                .map(this::paraResponse)
                .toList();
    }

    @Transactional
    public PlataformaResponseDTO atualizar(Integer id, PlataformaRequestDTO request) {
        Usuario usuario = usuarioAutenticadoHelper.obter();
        Plataforma plataforma = buscarEntidadeDoUsuario(id, usuario);
        preencherPlataforma(plataforma, request, usuario);
        return paraResponse(plataformaRepository.save(plataforma));
    }

    @Transactional
    public void deletar(Integer id) {
        plataformaRepository.delete(buscarEntidadeDoUsuario(id, usuarioAutenticadoHelper.obter()));
    }

    private Plataforma buscarEntidadeDoUsuario(Integer id, Usuario usuario) {
        return plataformaRepository.findByPlataformaIdAndUsuario(id, usuario)
                .orElseThrow(() -> {
                    if (plataformaRepository.existsById(id)) {
                        return new AccessDeniedException("Acesso negado.");
                    }
                    return new ResourceNotFoundException("Plataforma nao encontrada.");
                });
    }

    private void preencherPlataforma(Plataforma plataforma, PlataformaRequestDTO request, Usuario usuario) {
        if (!Objects.equals(request.usuarioId(), usuario.getUser_id())) {
            throw new AccessDeniedException("Acesso negado.");
        }
        plataforma.setNomePlataforma(request.nomePlataforma());
        plataforma.setDescricao(request.descricao());
        plataforma.setUrl(request.url());
        plataforma.setUser_id(usuario);
    }

    private PlataformaResponseDTO paraResponse(Plataforma plataforma) {
        return new PlataformaResponseDTO(
                plataforma.getPlataforma_id(),
                plataforma.getNomePlataforma(),
                plataforma.getDescricao(),
                plataforma.getUrl(),
                plataforma.getUser_id().getUser_id()
        );
    }
}
