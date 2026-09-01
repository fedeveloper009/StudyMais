package com.projeto.studymais.dto.usuario;

import java.util.List;

public record UsuarioResponseDTO(Integer id, String nome, String email, Integer xp, Integer diasDeSequencia,
                                Long tempoEstudado, String materiaEstudada, List<String> conquistas) {
    public UsuarioResponseDTO(Integer id, String nome, String email) {
        this(id, nome, email, 0, 0, 0L, null, List.of());
    }
}
