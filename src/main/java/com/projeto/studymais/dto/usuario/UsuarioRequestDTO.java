package com.projeto.studymais.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record UsuarioRequestDTO(
        @NotBlank(message = "O nome e obrigatorio.")
        String nome,
        @NotBlank(message = "O email e obrigatorio.")
        @Email(message = "O email deve ser valido.")
        String email,
        @NotBlank(message = "A senha e obrigatoria.")
        @Size(min = 6, message = "A senha deve ter no minimo 6 caracteres.")
        String senha,
        Integer xp,
        Integer diasDeSequencia,
        Long tempoEstudado,
        String materiaEstudada,
        List<String> conquistas
) {
    public UsuarioRequestDTO(String nome, String email, String senha) {
        this(nome, email, senha, 0, 0, 0L, null, List.of());
    }
}
