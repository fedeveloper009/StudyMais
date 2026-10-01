package com.projeto.studymais.dto.questao;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
public record RespostaQuestaoRequest(@NotEmpty List<String> respostas) {}
