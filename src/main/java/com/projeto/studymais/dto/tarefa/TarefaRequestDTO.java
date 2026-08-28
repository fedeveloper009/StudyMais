package com.projeto.studymais.dto.tarefa;

import com.projeto.studymais.model.Prioridade;
import com.projeto.studymais.model.StatusTarefa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record TarefaRequestDTO(
        @NotBlank(message = "O titulo e obrigatorio.")
        String titulo,
        String descricao,
        @NotNull(message = "A data de entrega e obrigatoria.")
        LocalDate dataEntrega,
        @NotNull(message = "O materiaId e obrigatorio.")
        Integer materiaId,
        @NotNull(message = "O usuarioId e obrigatorio.")
        Integer usuarioId,
        @NotNull(message = "O status e obrigatorio.")
        StatusTarefa status,
        @NotNull(message = "A prioridade e obrigatoria.")
        Prioridade prioridade
) {
}
