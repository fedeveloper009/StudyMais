package com.projeto.studymais.service;

import com.projeto.studymais.dto.questao.*;
import com.projeto.studymais.exception.ResourceNotFoundException;
import com.projeto.studymais.model.*;
import com.projeto.studymais.repository.*;
import com.projeto.studymais.security.UsuarioAutenticadoHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class QuestaoService {
    private final QuestaoRepository questoes; private final QuestaoApresentadaRepository apresentadas; private final UsuarioAutenticadoHelper auth;
    private final boolean reiniciarCiclo; private final boolean revelarCorrecao;
    public QuestaoService(QuestaoRepository q,QuestaoApresentadaRepository a,UsuarioAutenticadoHelper auth,
            @Value("${app.exercicios.reiniciar-ciclo:true}") boolean reiniciarCiclo,
            @Value("${app.exercicios.revelar-correcao:true}") boolean revelarCorrecao){this.questoes=q;this.apresentadas=a;this.auth=auth;this.reiniciarCiclo=reiniciarCiclo;this.revelarCorrecao=revelarCorrecao;}
    @Transactional
    public QuestaoResponse proxima(String materia,String topico,String sessaoId){
        Usuario usuario=auth.obter(); String sessao=sessaoId==null||sessaoId.isBlank()?UUID.randomUUID().toString():sessaoId;
        List<Questao> elegiveis;
        boolean filtraMateria=materia!=null&&!materia.isBlank(), filtraTopico=topico!=null&&!topico.isBlank();
        if(filtraMateria&&filtraTopico) elegiveis=questoes.findByMateriaAndTopico(materia,topico);
        else if(filtraMateria) elegiveis=questoes.findByMateria(materia);
        else if(filtraTopico) elegiveis=questoes.findByTopico(topico);
        else elegiveis=questoes.findAll();
        if(elegiveis.isEmpty()) throw new ResourceNotFoundException("Nenhuma questao elegivel encontrada.");
        Set<String> usadas=new HashSet<>(); apresentadas.findBySessaoIdAndUsuario(sessao,usuario).forEach(a->usadas.add(a.getQuestao().getId()));
        List<Questao> restantes=elegiveis.stream().filter(q->!usadas.contains(q.getId())).toList();
        if(restantes.isEmpty()&&!reiniciarCiclo) throw new ResourceNotFoundException("Todas as questoes elegiveis desta sessao ja foram apresentadas.");
        // Quando habilitado, a mesma sessão inicia um novo ciclo ao esgotar as questões.
        List<Questao> pool=restantes.isEmpty()?elegiveis:restantes;
        Questao escolhida=pool.get(ThreadLocalRandom.current().nextInt(pool.size()));
        QuestaoApresentada registro=apresentadas.save(new QuestaoApresentada(sessao,usuario,escolhida));
        return QuestaoResponse.de(registro.getId(),sessao,escolhida);
    }
    @Transactional
    public CorrecaoQuestaoResponse responder(Long apresentacaoId,List<String> resposta){
        Usuario usuario=auth.obter(); QuestaoApresentada registro=apresentadas.findByIdAndUsuario(apresentacaoId,usuario).orElseThrow(()->new ResourceNotFoundException("Questao apresentada nao encontrada."));
        if(registro.getCorreta()!=null) throw new ResponseStatusException(HttpStatus.CONFLICT,"Esta resposta ja foi enviada.");
        Questao q=registro.getQuestao();
        if(resposta==null||resposta.isEmpty()||resposta.stream().anyMatch(r->!q.getAlternativas().contains(r))) throw new IllegalArgumentException("Selecione apenas alternativas exibidas.");
        if(q.getTipoResposta()==TipoRespostaQuestao.ALTERNATIVA_UNICA && resposta.size()!=1) throw new IllegalArgumentException("Envie exatamente uma alternativa.");
        Set<String> enviada=new HashSet<>(resposta), correta=new HashSet<>(q.getRespostasCorretas());
        boolean certo=enviada.size()==resposta.size() && enviada.equals(correta);
        registro.responder(String.join(" | ",resposta),certo);
        return new CorrecaoQuestaoResponse(apresentacaoId,certo,revelarCorrecao?q.getRespostasCorretas():null,revelarCorrecao?q.getExplicacao():null);
    }
    public List<Map<String,Object>> historico(){return apresentadas.findTop100ByUsuarioOrderByApresentadaEmDesc(auth.obter()).stream().map(a->{Map<String,Object> m=new LinkedHashMap<>();m.put("apresentacaoId",a.getId());m.put("questaoId",a.getQuestao().getId());m.put("materia",a.getQuestao().getMateria());m.put("topico",a.getQuestao().getTopico());m.put("subtopico",a.getQuestao().getSubtopico());m.put("apresentadaEm",a.getApresentadaEm());m.put("correta",a.getCorreta());m.put("respostaEnviada",a.getRespostaEnviada());return m;}).toList();}
}
