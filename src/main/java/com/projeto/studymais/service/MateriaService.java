package com.projeto.studymais.service;

import com.projeto.studymais.dto.materia.MateriaRequestDTO;
import com.projeto.studymais.dto.materia.MateriaResponseDTO;
import com.projeto.studymais.exception.ResourceNotFoundException;
import com.projeto.studymais.model.Materia;
import com.projeto.studymais.model.Usuario;
import com.projeto.studymais.repository.MateriaRepository;
import com.projeto.studymais.repository.UsuarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MateriaService {

    private final MateriaRepository materiaRepository;
    private final UsuarioRepository usuarioRepository;

    public MateriaService(MateriaRepository materiaRepository, UsuarioRepository usuarioRepository) {
        this.materiaRepository = materiaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public MateriaResponseDTO criar(MateriaRequestDTO request) {
        Materia materia = new Materia();
        preencherMateria(materia, request);
        return paraResponse(materiaRepository.save(materia));
    }

    public MateriaResponseDTO buscarPorId(Integer id) {
        return paraResponse(buscarEntidadePorId(id));
    }

    public List<MateriaResponseDTO> buscarTodos() {
        return materiaRepository.findAll().stream().map(this::paraResponse).toList();
    }

    @Transactional
    public MateriaResponseDTO atualizar(Integer id, MateriaRequestDTO request) {
        Materia materia = buscarEntidadePorId(id);
        preencherMateria(materia, request);
        return paraResponse(materiaRepository.save(materia));
    }

    @Transactional
    public void deletar(Integer id) {
        materiaRepository.delete(buscarEntidadePorId(id));
    }

    private Materia buscarEntidadePorId(Integer id) {
        return materiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Materia nao encontrada."));
    }

    private void preencherMateria(Materia materia, MateriaRequestDTO request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado."));
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
