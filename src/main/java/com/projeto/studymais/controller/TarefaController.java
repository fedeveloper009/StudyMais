package com.projeto.studymais.controller;

import com.projeto.studymais.dto.tarefa.TarefaRequestDTO;
import com.projeto.studymais.dto.tarefa.TarefaResponseDTO;
import com.projeto.studymais.service.TarefaService;
import java.net.URI;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {

    private final TarefaService tarefaService;

    public TarefaController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }

    @PostMapping
    public ResponseEntity<TarefaResponseDTO> criar(@Valid @RequestBody TarefaRequestDTO request) {
        TarefaResponseDTO tarefa = tarefaService.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(tarefa.id())
                .toUri();
        return ResponseEntity.created(location).body(tarefa);
    }

    @GetMapping
    public ResponseEntity<List<TarefaResponseDTO>> buscarTodos() {
        return ResponseEntity.ok(tarefaService.buscarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarefaResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(tarefaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarefaResponseDTO> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody TarefaRequestDTO request
    ) {
        return ResponseEntity.ok(tarefaService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        tarefaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
