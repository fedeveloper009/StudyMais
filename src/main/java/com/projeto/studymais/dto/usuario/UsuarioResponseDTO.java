package com.projeto.studymais.dto.usuario;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public record UsuarioResponseDTO(Integer id, String nome, String email, Integer xp, Integer diasDeSequencia,
                                Long tempoEstudado, String materiaEstudada, List<String> conquistas,
                                String fotoPerfilUrl) {
    public UsuarioResponseDTO(Integer id, String nome, String email) {
        this(id, nome, email, 0, 0, 0L, null, List.of(), null);
    }

    @JsonProperty("fotoUrl")
    public String fotoUrl() {
        return fotoPerfilUrl;
    }
}
