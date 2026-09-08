package com.projeto.studymais.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailRequestDTO(
        @NotBlank(message = "O email e obrigatorio.")
        @Email(message = "O email deve ser valido.")
        String email
) {}
