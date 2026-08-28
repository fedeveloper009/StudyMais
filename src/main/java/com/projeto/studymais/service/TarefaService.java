package com.projeto.studymais.service;

import com.projeto.studymais.dto.tarefa.TarefaRequestDTO;
import com.projeto.studymais.dto.tarefa.TarefaResponseDTO;
import com.projeto.studymais.exception.ResourceNotFoundException;
import com.projeto.studymais.model.Materia;
import com.projeto.studymais.model.Tarefa;
import com.projeto.studymais.model.Usuario;
import com.projeto.studymais.repository.MateriaRepository;
import com.projeto.studymais.repository.TarefaRepository;
import com.projeto.studymais.security.UsuarioAutenticadoHelper;
import java.util.List;
import java.util.Objects;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TarefaService {

    private final TarefaRepository tarefaRepository;
    private final MateriaRepository materiaRepository;
    private final UsuarioAutenticadoHelper usuarioAutenticadoHelper;

    public TarefaService(
            TarefaRepository tarefaRepository,
            MateriaRepository materiaRepository,
            UsuarioAutenticadoHelper usuarioAutenticadoHelper
    ) {
        this.tarefaRepository = tarefaRepository;
        this.materiaRepository = materiaRepository;
        this.usuarioAutenticadoHelper = usuarioAutenticadoHelper;
    }

    @Transactional
    public TarefaResponseDTO criar(TarefaRequestDTO request) {
        Usuario usuario = usuarioAutenticadoHelper.obter();
        Tarefa tarefa = new Tarefa();
        preencherTarefa(tarefa, request, usuario);
        return paraResponse(tarefaRepository.save(tarefa));
    }

    public TarefaResponseDTO buscarPorId(Integer id) {
        return paraResponse(buscarEntidadeDoUsuario(id, usuarioAutenticadoHelper.obter()));
    }

    public List<TarefaResponseDTO> buscarTodos() {
        Usuario usuario = usuarioAutenticadoHelper.obter();
        return tarefaRepository.findAllByUsuario(usuario).stream().map(this::paraResponse).toList();
    }

    @Transactional
    public TarefaResponseDTO atualizar(Integer id, TarefaRequestDTO request) {
        Usuario usuario = usuarioAutenticadoHelper.obter();
        Tarefa tarefa = buscarEntidadeDoUsuario(id, usuario);
        preencherTarefa(tarefa, request, usuario);
        return paraResponse(tarefaRepository.save(tarefa));
    }

    @Transactional
    public void deletar(Integer id) {
        tarefaRepository.delete(buscarEntidadeDoUsuario(id, usuarioAutenticadoHelper.obter()));
    }

    private Tarefa buscarEntidadeDoUsuario(Integer id, Usuario usuario) {
        return tarefaRepository.findByTarefaIdAndUsuario(id, usuario)
                .orElseThrow(() -> {
                    if (tarefaRepository.existsById(id)) {
                        return new AccessDeniedException("Acesso negado.");
                    }
                    return new ResourceNotFoundException("Tarefa nao encontrada.");
                });
    }

    private void preencherTarefa(Tarefa tarefa, TarefaRequestDTO request, Usuario usuario) {
        if (!Objects.equals(request.usuarioId(), usuario.getUser_id())) {
            throw new AccessDeniedException("Acesso negado.");
        }
        Materia materia = materiaRepository.findByMateriaIdAndUsuario(request.materiaId(), usuario)
                .orElseThrow(() -> {
                    if (materiaRepository.existsById(request.materiaId())) {
                        return new AccessDeniedException("Acesso negado.");
                    }
                    return new ResourceNotFoundException("Materia nao encontrada.");
                });
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
