package com.projeto.studymais.controller;

import com.projeto.studymais.dto.usuario.UsuarioRequestDTO;
import com.projeto.studymais.dto.usuario.UsuarioResponseDTO;
import com.projeto.studymais.dto.usuario.AtualizarNomeRequestDTO;
import com.projeto.studymais.dto.usuario.AlterarSenhaRequestDTO;
import com.projeto.studymais.service.UsuarioService;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criar(@Valid @RequestBody UsuarioRequestDTO request) {
        UsuarioResponseDTO usuario = usuarioService.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(usuario.id())
                .toUri();
        return ResponseEntity.created(location).body(usuario);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> buscarTodos() {
        return ResponseEntity.ok(usuarioService.buscarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody UsuarioRequestDTO request
    ) {
        return ResponseEntity.ok(usuarioService.atualizar(id, request));
    }

    @PutMapping("/{id}/nome")
    public ResponseEntity<UsuarioResponseDTO> atualizarNome(
            @PathVariable Integer id,
            @Valid @RequestBody AtualizarNomeRequestDTO request
    ) {
        return ResponseEntity.ok(usuarioService.atualizarNome(id, request));
    }

    @PutMapping("/{id}/senha")
    public ResponseEntity<UsuarioResponseDTO> alterarSenha(
            @PathVariable Integer id,
            @Valid @RequestBody AlterarSenhaRequestDTO request
    ) {
        return ResponseEntity.ok(usuarioService.alterarSenha(id, request));
    }

    @PostMapping(value = "/{id}/foto", consumes = "multipart/form-data")
    public ResponseEntity<UsuarioResponseDTO> alterarFotoPerfil(
            @PathVariable Integer id,
            @RequestPart("foto") MultipartFile foto
    ) {
        return ResponseEntity.ok(usuarioService.alterarFotoPerfil(id, foto));
    }

    @DeleteMapping("/{id}/foto")
    public ResponseEntity<UsuarioResponseDTO> removerFotoPerfil(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.removerFotoPerfil(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
