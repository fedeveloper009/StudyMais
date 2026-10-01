package com.projeto.studymais.repository;
import com.projeto.studymais.model.ConteudoTopico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ConteudoTopicoRepository extends JpaRepository<ConteudoTopico,String> {
    List<ConteudoTopico> findByMateriaOrderByTopicoAscSubtopicoAsc(String materia);
    List<ConteudoTopico> findAllByOrderByMateriaAscTopicoAscSubtopicoAsc();
}
