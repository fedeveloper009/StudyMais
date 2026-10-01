package com.projeto.studymais.controller;
import com.projeto.studymais.dto.questao.MateriaTopicosResponse;
import com.projeto.studymais.service.ConteudoTopicoService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/exercicios/topicos")
public class ConteudoTopicoController {
    private final ConteudoTopicoService service;
    public ConteudoTopicoController(ConteudoTopicoService service){this.service=service;}
    @GetMapping public List<MateriaTopicosResponse> listar(@RequestParam(required=false) String materia){return service.listar(materia);}
}
