package com.projeto.studymais.dto.materia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MateriaRequestDTO(
        @NotBlank(message = "O nome da materia e obrigatorio.")
        String nomeMateria,
        String descricao,
        @NotBlank(message = "A cor e obrigatoria.")
        String cor,
        @NotNull(message = "O usuarioId e obrigatorio.")
        Integer usuarioId
) {
}
