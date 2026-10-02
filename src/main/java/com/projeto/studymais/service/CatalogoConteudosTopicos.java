package com.projeto.studymais.service;

import com.projeto.studymais.model.ConteudoTopico;
import com.projeto.studymais.model.SignificadoSimbolo;
import com.projeto.studymais.repository.ConteudoTopicoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/** Cria nós extensíveis da hierarquia; conteúdos ficam pendentes até revisão da fonte didática. */
@Component
public class CatalogoConteudosTopicos implements CommandLineRunner {
    private static final String AVISO="Material didático de referência não localizado; preencher e revisar este conteúdo antes de exibi-lo como material aprovado.";
    private static final String RESUMO_FRACOES="Uma fração representa partes iguais de um todo. O número de cima, chamado numerador, mostra quantas partes foram consideradas. O número de baixo, chamado denominador, mostra em quantas partes iguais o todo foi dividido.";
    private static final String EXEMPLO_FRACOES="Em 3/4, o todo foi dividido em 4 partes iguais e 3 foram consideradas.";
    private final ConteudoTopicoRepository repository;
    public CatalogoConteudosTopicos(ConteudoTopicoRepository repository){this.repository=repository;}
    @Transactional
    @Override public void run(String... args){
        List<ConteudoTopico> nos=List.of(
            new ConteudoTopico("matematica-fracoes","Matemática","Frações",null,AVISO),
            new ConteudoTopico("matematica-fracoes-inteiro","Matemática","Frações","Frações equivalentes a 1 inteiro",AVISO),
            new ConteudoTopico("matematica-fracoes-menores","Matemática","Frações","Frações menores que 1 inteiro",AVISO),
            new ConteudoTopico("matematica-fracoes-equivalentes","Matemática","Frações","Frações equivalentes",AVISO),
            new ConteudoTopico("matematica-fracoes-operacoes-iguais","Matemática","Frações","Adição e subtração com denominadores iguais",AVISO),
            new ConteudoTopico("matematica-fracoes-operacoes-diferentes","Matemática","Frações","Adição e subtração com denominadores diferentes",AVISO),
            new ConteudoTopico("matematica-fracoes-multiplicacao","Matemática","Frações","Multiplicação de frações",AVISO),
            new ConteudoTopico("matematica-fracoes-divisao","Matemática","Frações","Divisão de frações",AVISO),
            new ConteudoTopico("portugues-interpretacao-texto","Português","Interpretação de texto",null,
                "Interpretar um texto é compreender o que ele diz e perceber informações que aparecem de forma indireta. Para isso, leia com atenção, observe quem participa, onde e quando a história acontece e use pistas do texto para entender as ideias.",false,null)
        );
        nos.forEach(no->repository.findById(no.getId()).ifPresentOrElse(existente->{
            if("matematica-fracoes".equals(no.getId())) {
                existente.atualizarConteudo(RESUMO_FRACOES,List.of(),List.of(
                    new SignificadoSimbolo("numerador","Número de cima; mostra quantas partes foram consideradas."),
                    new SignificadoSimbolo("denominador","Número de baixo; mostra em quantas partes iguais o todo foi dividido.")
                ),EXEMPLO_FRACOES,false,null);
            }
            repository.save(existente);
        },()->{
            if("matematica-fracoes".equals(no.getId())) no.atualizarConteudo(RESUMO_FRACOES,List.of(),List.of(
                new SignificadoSimbolo("numerador","Número de cima; mostra quantas partes foram consideradas."),
                new SignificadoSimbolo("denominador","Número de baixo; mostra em quantas partes iguais o todo foi dividido.")
            ),EXEMPLO_FRACOES,false,null);
            repository.save(no);
        }));
    }
}
