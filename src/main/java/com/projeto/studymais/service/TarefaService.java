package com.projeto.studymais.service;

import com.projeto.studymais.dto.tarefa.TarefaRequestDTO;
import com.projeto.studymais.dto.tarefa.TarefaResponseDTO;
import com.projeto.studymais.exception.ResourceNotFoundException;
import com.projeto.studymais.model.Materia;
import com.projeto.studymais.model.Tarefa;
import com.projeto.studymais.model.Usuario;
import com.projeto.studymais.repository.MateriaRepository;
import com.projeto.studymais.repository.TarefaRepository;
import com.projeto.studymais.repository.UsuarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TarefaService {

    private final TarefaRepository tarefaRepository;
    private final MateriaRepository materiaRepository;
    private final UsuarioRepository usuarioRepository;

    public TarefaService(
            TarefaRepository tarefaRepository,
            MateriaRepository materiaRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.tarefaRepository = tarefaRepository;
        this.materiaRepository = materiaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public TarefaResponseDTO criar(TarefaRequestDTO request) {
        Tarefa tarefa = new Tarefa();
        preencherTarefa(tarefa, request);
        return paraResponse(tarefaRepository.save(tarefa));
    }

    public TarefaResponseDTO buscarPorId(Integer id) {
        return paraResponse(buscarEntidadePorId(id));
    }

    public List<TarefaResponseDTO> buscarTodos() {
        return tarefaRepository.findAll().stream().map(this::paraResponse).toList();
    }

    @Transactional
    public TarefaResponseDTO atualizar(Integer id, TarefaRequestDTO request) {
        Tarefa tarefa = buscarEntidadePorId(id);
        preencherTarefa(tarefa, request);
        return paraResponse(tarefaRepository.save(tarefa));
    }

    @Transactional
    public void deletar(Integer id) {
        tarefaRepository.delete(buscarEntidadePorId(id));
    }

    private Tarefa buscarEntidadePorId(Integer id) {
        return tarefaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa nao encontrada."));
    }

    private void preencherTarefa(Tarefa tarefa, TarefaRequestDTO request) {
        Materia materia = materiaRepository.findById(request.materiaId())
                .orElseThrow(() -> new ResourceNotFoundException("Materia nao encontrada."));
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado."));
        tarefa.setTitulo(request.titulo());
        tarefa.setDescricao(request.descricao());
        tarefa.setDataEntrega(request.dataEntrega());
        tarefa.setMateriaRelacionada(materia);
        tarefa.setUser_id(usuario);
        tarefa.setStatus(request.status());
        tarefa.setPrioridade(request.prioridade());
    }

    private TarefaResponseDTO paraResponse(Tarefa tarefa) {
        return new TarefaResponseDTO(
                tarefa.getTarefa_id(),
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.getDataEntrega(),
                tarefa.getMateriaRelacionada().getMateria_id(),
                tarefa.getUser_id().getUser_id(),
                tarefa.getStatus(),
                tarefa.getPrioridade()
        );
    }
}
