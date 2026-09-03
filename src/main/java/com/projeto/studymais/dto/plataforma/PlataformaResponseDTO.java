package com.projeto.studymais.dto.plataforma;

public record PlataformaResponseDTO(
        Integer id,
        String nomePlataforma,
        String descricao,
        String url,
        Integer usuarioId
) {
}
