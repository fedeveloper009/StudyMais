package com.projeto.studymais.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlterarSenhaRequestDTO(
        @NotBlank(message = "A senha atual e obrigatoria.")
        String senhaAtual,
        @NotBlank(message = "A nova senha e obrigatoria.")
        @Size(min = 6, max = 72, message = "A nova senha deve ter entre 6 e 72 caracteres.")
        String novaSenha,
        @NotBlank(message = "A confirmacao da nova senha e obrigatoria.")
        String confirmacaoNovaSenha
) {
}
