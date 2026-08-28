package com.projeto.studymais.service;

import com.projeto.studymais.dto.materia.MateriaRequestDTO;
import com.projeto.studymais.dto.materia.MateriaResponseDTO;
import com.projeto.studymais.exception.ResourceNotFoundException;
import com.projeto.studymais.model.Materia;
import com.projeto.studymais.model.Usuario;
import com.projeto.studymais.repository.MateriaRepository;
import com.projeto.studymais.security.UsuarioAutenticadoHelper;
import java.util.List;
import java.util.Objects;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MateriaService {

    private final MateriaRepository materiaRepository;
    private final UsuarioAutenticadoHelper usuarioAutenticadoHelper;

    public MateriaService(
            MateriaRepository materiaRepository,
            UsuarioAutenticadoHelper usuarioAutenticadoHelper
    ) {
        this.materiaRepository = materiaRepository;
        this.usuarioAutenticadoHelper = usuarioAutenticadoHelper;
    }

    @Transactional
    public MateriaResponseDTO criar(MateriaRequestDTO request) {
        Usuario usuario = usuarioAutenticadoHelper.obter();
        Materia materia = new Materia();
        preencherMateria(materia, request, usuario);
        return paraResponse(materiaRepository.save(materia));
    }

    public MateriaResponseDTO buscarPorId(Integer id) {
        return paraResponse(buscarEntidadeDoUsuario(id, usuarioAutenticadoHelper.obter()));
    }

    public List<MateriaResponseDTO> buscarTodos() {
        Usuario usuario = usuarioAutenticadoHelper.obter();
        return materiaRepository.findAllByUsuario(usuario).stream().map(this::paraResponse).toList();
    }

    @Transactional
    public MateriaResponseDTO atualizar(Integer id, MateriaRequestDTO request) {
        Usuario usuario = usuarioAutenticadoHelper.obter();
        Materia materia = buscarEntidadeDoUsuario(id, usuario);
        preencherMateria(materia, request, usuario);
        return paraResponse(materiaRepository.save(materia));
    }

    @Transactional
    public void deletar(Integer id) {
        materiaRepository.delete(buscarEntidadeDoUsuario(id, usuarioAutenticadoHelper.obter()));
    }

    private Materia buscarEntidadeDoUsuario(Integer id, Usuario usuario) {
        return materiaRepository.findByMateriaIdAndUsuario(id, usuario)
                .orElseThrow(() -> {
                    if (materiaRepository.existsById(id)) {
                        return new AccessDeniedException("Acesso negado.");
                    }
                    return new ResourceNotFoundException("Materia nao encontrada.");
                });
    }

    private void preencherMateria(Materia materia, MateriaRequestDTO request, Usuario usuario) {
        if (!Objects.equals(request.usuarioId(), usuario.getUser_id())) {
            throw new AccessDeniedException("Acesso negado.");
        }
        materia.setNomeMateria(request.nomeMateria());
        materia.setDescricao(request.descricao());
        materia.setCor(request.cor());
        materia.setUser_id(usuario);
    }

    private MateriaResponseDTO paraResponse(Materia materia) {
        return new MateriaResponseDTO(
                materia.getMateria_id(),
                materia.getNomeMateria(),
                materia.getDescricao(),
                materia.getCor(),
                materia.getUser_id().getUser_id()
        );
    }
}
