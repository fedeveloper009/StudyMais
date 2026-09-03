package com.projeto.studymais.controller;

import com.projeto.studymais.dto.plataforma.PlataformaRequestDTO;
import com.projeto.studymais.dto.plataforma.PlataformaResponseDTO;
import com.projeto.studymais.service.PlataformaService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
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
@RequestMapping("/api/plataformas")
public class PlataformaController {

    private final PlataformaService plataformaService;

    public PlataformaController(PlataformaService plataformaService) {
        this.plataformaService = plataformaService;
    }

    @PostMapping
    public ResponseEntity<PlataformaResponseDTO> criar(@Valid @RequestBody PlataformaRequestDTO request) {
        PlataformaResponseDTO plataforma = plataformaService.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(plataforma.id())
                .toUri();
        return ResponseEntity.created(location).body(plataforma);
    }

    @GetMapping
    public ResponseEntity<List<PlataformaResponseDTO>> buscarTodos() {
        return ResponseEntity.ok(plataformaService.buscarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlataformaResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(plataformaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlataformaResponseDTO> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody PlataformaRequestDTO request
    ) {
        return ResponseEntity.ok(plataformaService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        plataformaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
