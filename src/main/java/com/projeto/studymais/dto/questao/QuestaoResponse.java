package com.projeto.studymais.dto.questao;
import com.projeto.studymais.model.Questao;
import java.util.List;
public record QuestaoResponse(Long apresentacaoId, String sessaoId, String id, String materia, String topico, String subtopico, String textoBase, String enunciado, String tipoResposta, List<String> alternativas, String dificuldade) {
    public static QuestaoResponse de(Long apresentacaoId,String sessaoId,Questao q){return new QuestaoResponse(apresentacaoId,sessaoId,q.getId(),q.getMateria(),q.getTopico(),q.getSubtopico(),q.getTextoBase(),q.getEnunciado(),q.getTipoResposta().name(),q.getAlternativas(),q.getDificuldade());}
}
