package com.projeto.studymais.service;

import com.projeto.studymais.model.*;
import com.projeto.studymais.repository.QuestaoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

/** Catálogo inicial revisado; pode ser substituído/importado do material didático oficial. */
@Component
public class CatalogoQuestoesFracoes implements CommandLineRunner {
    private final QuestaoRepository repository;
    public CatalogoQuestoesFracoes(QuestaoRepository repository){this.repository=repository;}
    @Override public void run(String... args){
        List<Questao> questoes=List.of(
            q("fracoes-inteiro-001","Frações equivalentes a 1 inteiro","Qual fração representa exatamente 1 inteiro?","3/3","2/3","4/3","1/3","3/3","Um inteiro é representado por numerador e denominador iguais.","FACIL"),
            q("fracoes-inteiro-002","Frações equivalentes a 1 inteiro","Complete: 5/5 = __.","0","1","5","1/5","1","Toda fração com numerador igual ao denominador vale 1.","FACIL"),
            q("fracoes-menores-001","Frações menores que 1 inteiro","Qual fração é menor que 1 inteiro?","5/4","3/7","8/8","9/5","3/7","Uma fração positiva é menor que 1 quando o numerador é menor que o denominador.","FACIL"),
            q("fracoes-menores-002","Frações menores que 1 inteiro","Em 4/9, o numerador é menor que o denominador. O que isso indica?","É menor que 1","É igual a 1","É maior que 1","É igual a 9","É menor que 1","Como 4 < 9, quatro nonos é menor que um inteiro.","FACIL"),
            q("fracoes-equivalentes-001","Frações equivalentes","Qual fração é equivalente a 2/3?","4/6","3/5","2/6","6/8","4/6","Multiplicando numerador e denominador de 2/3 por 2, obtemos 4/6.","FACIL"),
            q("fracoes-equivalentes-002","Frações equivalentes","Selecione todas as frações equivalentes a 3/4.","6/8","9/12","6/10","12/16","6/8","9/12","12/16","As três opções resultam de multiplicar numerador e denominador de 3/4 pelo mesmo número.","MEDIO"),
            q("soma-subtracao-iguais-001","Adição e subtração com denominadores iguais","Calcule 2/7 + 3/7.","5/7","5/14","1/7","6/7","5/7","Somamos os numeradores e mantemos o denominador: (2+3)/7.","FACIL"),
            q("soma-subtracao-iguais-002","Adição e subtração com denominadores iguais","Calcule 6/9 - 2/9.","4/9","4/0","8/9","4/18","4/9","Subtraímos os numeradores e mantemos o denominador: (6-2)/9.","FACIL"),
            q("soma-subtracao-diferentes-001","Adição e subtração com denominadores diferentes","Calcule 1/2 + 1/3.","2/5","5/6","2/6","1/6","5/6","Com denominador comum 6: 1/2=3/6 e 1/3=2/6; a soma é 5/6.","MEDIO"),
            q("soma-subtracao-diferentes-002","Adição e subtração com denominadores diferentes","Calcule 3/4 - 1/6.","2/10","7/12","1/2","2/12","7/12","Com denominador comum 12: 3/4=9/12 e 1/6=2/12; a diferença é 7/12.","MEDIO"),
            q("multiplicacao-fracoes-001","Multiplicação de frações","Calcule 2/3 × 3/5.","6/15","5/8","1/2","6/8","6/15","Multiplicamos numeradores e denominadores: (2×3)/(3×5)=6/15=2/5; a alternativa 6/15 representa o resultado.","MEDIO"),
            q("multiplicacao-fracoes-002","Multiplicação de frações","Calcule 1/4 × 2/3.","2/12","3/7","2/7","1/6","2/12","Multiplicamos os numeradores e os denominadores: (1×2)/(4×3)=2/12, que simplifica para 1/6.","MEDIO"),
            q("divisao-fracoes-001","Divisão de frações","Calcule 1/2 ÷ 1/4.","1/8","2","1/2","4","2","Dividir por 1/4 equivale a multiplicar por 4/1: 1/2 × 4 = 2.","MEDIO"),
            q("divisao-fracoes-002","Divisão de frações","Calcule 2/3 ÷ 4/5.","8/15","5/6","6/5","2/7","5/6","Multiplicamos pela fração inversa: 2/3 × 5/4 = 10/12 = 5/6.","MEDIO"),
            portugues("portugues-interpretacao-001","Onde Clara encontrou a caixa?",TipoRespostaQuestao.ALTERNATIVA_UNICA,
                List.of("a) No quarto da escola.","b) No armário da avó.","c) Dentro de uma mala.","d) Em uma loja antiga.","e) No jardim."),List.of("b) No armário da avó.")),
            portugues("portugues-interpretacao-002","O que havia dentro da caixa?",TipoRespostaQuestao.ALTERNATIVA_UNICA,
                List.of("a) Fotografias, um botão e um bilhete.","b) Um casaco, um livro e uma carta.","c) Brinquedos, moedas e um mapa.","d) Fotografias, uma chave e um caderno.","e) Um botão, uma mala e um brinquedo."),List.of("a) Fotografias, um botão e um bilhete.")),
            portugues("portugues-interpretacao-003","A quem pertencia o botão azul?",TipoRespostaQuestao.ALTERNATIVA_UNICA,
                List.of("a) A Clara.","b) À mãe de Clara.","c) À avó de Clara.","d) A uma amiga da avó.","e) Não é possível saber pelo texto."),List.of("c) À avó de Clara.")),
            portugues("portugues-interpretacao-004","O que podemos concluir sobre a avó ao final do texto?",TipoRespostaQuestao.ALTERNATIVA_UNICA,
                List.of("a) Ela ficou assustada com a caixa.","b) Ela não se lembrava das fotografias.","c) Ela gostou de contar suas lembranças.","d) Ela pediu que Clara jogasse o botão fora.","e) Ela queria esconder a caixa."),List.of("c) Ela gostou de contar suas lembranças.")),
            portugues("portugues-interpretacao-005","Selecione todas as informações verdadeiras de acordo com o texto.",TipoRespostaQuestao.SELECAO_MULTIPLA,
                List.of("a) Clara encontrou a caixa na sexta-feira.","b) O bilhete dizia que o botão veio de uma viagem da avó.","c) Clara encontrou a caixa na escola.","d) A avó começou a contar histórias da infância.","e) A caixa estava vazia."),List.of("a) Clara encontrou a caixa na sexta-feira.","b) O bilhete dizia que o botão veio de uma viagem da avó.","d) A avó começou a contar histórias da infância."))
        );
        questoes.forEach(q->repository.findById(q.getId()).ifPresentOrElse(
                existente->{
                    String subtopico="Frações".equals(existente.getTopico())?existente.getSubtopico():existente.getTopico();
                    existente.definirHierarquia("Matemática","Frações",subtopico);
                    repository.save(existente);
                },()->repository.save(q)));
    }
    private Questao q(String id,String topic,String prompt,String a,String b,String c,String d,String correct,String explanation,String level){return new Questao(id,topic,prompt,TipoRespostaQuestao.ALTERNATIVA_UNICA,List.of(a,b,c,d),List.of(correct),explanation,level);}
    private Questao q(String id,String topic,String prompt,String a,String b,String c,String d,String correct1,String correct2,String correct3,String explanation,String level){return new Questao(id,topic,prompt,TipoRespostaQuestao.SELECAO_MULTIPLA,List.of(a,b,c,d),List.of(correct1,correct2,correct3),explanation,level);}
    private static final String TEXTO_PORTUGUES="Na sexta-feira, Clara encontrou uma caixa de madeira no armário da avó. Dentro dela havia fotografias antigas, um botão azul e um bilhete: “Guarde este botão. Ele pertenceu ao casaco que usei na minha primeira viagem.” Clara chamou a avó, que sorriu e começou a contar histórias de quando era criança.";
    private Questao portugues(String id,String enunciado,TipoRespostaQuestao tipo,List<String> alternativas,List<String> respostas){return new Questao(id,"Português","Interpretação de texto",null,enunciado,TEXTO_PORTUGUES,tipo,alternativas,respostas,null,null);}
}
