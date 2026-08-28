package com.projeto.studymais.controller;

import com.projeto.studymais.dto.materia.MateriaRequestDTO;
import com.projeto.studymais.dto.materia.MateriaResponseDTO;
import com.projeto.studymais.service.MateriaService;
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
@RequestMapping("/api/materias")
public class MateriaController {

    private final MateriaService materiaService;

    public MateriaController(MateriaService materiaService) {
        this.materiaService = materiaService;
    }

    @PostMapping
    public ResponseEntity<MateriaResponseDTO> criar(@Valid @RequestBody MateriaRequestDTO request) {
        MateriaResponseDTO materia = materiaService.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(materia.id())
                .toUri();
        return ResponseEntity.created(location).body(materia);
    }

    @GetMapping
    public ResponseEntity<List<MateriaResponseDTO>> buscarTodos() {
        return ResponseEntity.ok(materiaService.buscarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MateriaResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(materiaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MateriaResponseDTO> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody MateriaRequestDTO request
    ) {
        return ResponseEntity.ok(materiaService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        materiaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
