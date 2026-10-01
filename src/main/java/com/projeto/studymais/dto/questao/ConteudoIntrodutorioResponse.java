package com.projeto.studymais.dto.questao;
import java.util.List;
public record ConteudoIntrodutorioResponse(String resumo,List<FormularioResponse> formulas,List<SignificadoSimboloResponse> significadosSimbolos,String exemploResolvido,boolean revisaoNecessaria,String notaRevisao) {
    public record FormularioResponse(String expressao,String descricao) {}
    public record SignificadoSimboloResponse(String simbolo,String significado) {}
}
