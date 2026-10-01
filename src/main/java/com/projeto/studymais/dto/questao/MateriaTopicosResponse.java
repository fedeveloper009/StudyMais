package com.projeto.studymais.dto.questao;
import java.util.List;
public record MateriaTopicosResponse(String materia,List<TopicoResponse> topicos) {
    public record TopicoResponse(String nome,ConteudoIntrodutorioResponse conteudo,List<SubtopicoResponse> subtopicos) {}
    public record SubtopicoResponse(String nome,ConteudoIntrodutorioResponse conteudo) {}
}
