package com.projeto.studymais.dto.plataforma;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PlataformaRequestDTO(
        @NotBlank(message = "O nome da plataforma e obrigatorio.")
        String nomePlataforma,
        String descricao,
        @NotBlank(message = "A URL da plataforma e obrigatoria.")
        String url,
        @NotNull(message = "O usuarioId e obrigatorio.")
        Integer usuarioId
) {
}
