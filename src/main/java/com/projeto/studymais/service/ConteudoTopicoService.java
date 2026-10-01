package com.projeto.studymais.service;

import com.projeto.studymais.dto.questao.*;
import com.projeto.studymais.model.ConteudoTopico;
import com.projeto.studymais.repository.ConteudoTopicoRepository;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConteudoTopicoService {
    private final ConteudoTopicoRepository repository;
    public ConteudoTopicoService(ConteudoTopicoRepository repository){this.repository=repository;}
    public List<MateriaTopicosResponse> listar(String materia){
        List<ConteudoTopico> rows=materia==null||materia.isBlank()?repository.findAllByOrderByMateriaAscTopicoAscSubtopicoAsc():repository.findByMateriaOrderByTopicoAscSubtopicoAsc(materia);
        Map<String,List<ConteudoTopico>> porMateria=rows.stream().collect(Collectors.groupingBy(ConteudoTopico::getMateria,TreeMap::new,Collectors.toList()));
        return porMateria.entrySet().stream().map(m->new MateriaTopicosResponse(m.getKey(),agruparTopicos(m.getValue()))).toList();
    }
    private List<MateriaTopicosResponse.TopicoResponse> agruparTopicos(List<ConteudoTopico> rows){
        Map<String,List<ConteudoTopico>> porTopico=rows.stream().collect(Collectors.groupingBy(ConteudoTopico::getTopico,TreeMap::new,Collectors.toList()));
        return porTopico.entrySet().stream().map(t->{
            ConteudoTopico raiz=t.getValue().stream().filter(x->x.getSubtopico()==null).findFirst().orElse(null);
            List<MateriaTopicosResponse.SubtopicoResponse> subs=t.getValue().stream().filter(x->x.getSubtopico()!=null).map(x->new MateriaTopicosResponse.SubtopicoResponse(x.getSubtopico(),conteudo(x))).toList();
            return new MateriaTopicosResponse.TopicoResponse(t.getKey(),raiz==null?null:conteudo(raiz),subs);
        }).toList();
    }
    private ConteudoIntrodutorioResponse conteudo(ConteudoTopico c){
        return new ConteudoIntrodutorioResponse(c.getResumo(),c.getFormulas().stream().map(f->new ConteudoIntrodutorioResponse.FormularioResponse(f.getExpressao(),f.getDescricao())).toList(),c.getSignificadosSimbolos().stream().map(s->new ConteudoIntrodutorioResponse.SignificadoSimboloResponse(s.getSimbolo(),s.getSignificado())).toList(),c.getExemploResolvido(),c.isRevisaoNecessaria(),c.getNotaRevisao());
    }
}
