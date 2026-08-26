package com.projeto.studymais.dto.tarefa;

import com.projeto.studymais.model.Prioridade;
import com.projeto.studymais.model.StatusTarefa;
import java.time.LocalDate;

public record TarefaResponseDTO(
        Integer id,
        String titulo,
        String descricao,
        LocalDate dataEntrega,
        Integer materiaId,
        Integer usuarioId,
        StatusTarefa status,
        Prioridade prioridade
) {
}
