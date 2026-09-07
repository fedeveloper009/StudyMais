package com.projeto.studymais.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtualizarNomeRequestDTO(
        @NotBlank(message = "O nome e obrigatorio.")
        @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
        String nome
) {
}
