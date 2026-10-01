package com.projeto.studymais.dto.questao;
import java.util.List;
public record CorrecaoQuestaoResponse(Long apresentacaoId, boolean correta, List<String> respostasCorretas, String explicacao) {}
