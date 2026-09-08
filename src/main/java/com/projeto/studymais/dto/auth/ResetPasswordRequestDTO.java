package com.projeto.studymais.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonAlias;

public record ResetPasswordRequestDTO(
        @NotBlank(message = "O token e obrigatorio.") String token,
        @NotBlank(message = "A senha e obrigatoria.")
        @Size(min = 6, message = "A senha deve ter no minimo 6 caracteres.")
        @JsonAlias("senha") String novaSenha
) {
    public String senha() {
        return novaSenha;
    }
}
