package com.projeto.studymais.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record TokenRequestDTO(@NotBlank(message = "O token e obrigatorio.") String token) {}
