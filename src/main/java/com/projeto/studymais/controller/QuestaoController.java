package com.projeto.studymais.controller;
import com.projeto.studymais.dto.questao.*;
import com.projeto.studymais.service.QuestaoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/exercicios/questoes")
public class QuestaoController {
    private final QuestaoService service;
    public QuestaoController(QuestaoService service){this.service=service;}
    @GetMapping("/proxima") public QuestaoResponse proxima(@RequestParam(required=false) String materia,@RequestParam(required=false) String topico,@RequestParam(required=false) String sessaoId){return service.proxima(materia,topico,sessaoId);}
    @PostMapping("/{apresentacaoId}/resposta") public CorrecaoQuestaoResponse responder(@PathVariable Long apresentacaoId,@Valid @RequestBody RespostaQuestaoRequest request){return service.responder(apresentacaoId,request.respostas());}
    @GetMapping("/historico") public List<Map<String,Object>> historico(){return service.historico();}
}
