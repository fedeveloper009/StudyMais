package com.projeto.studymais.repository;
import com.projeto.studymais.model.Questao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface QuestaoRepository extends JpaRepository<Questao,String> {
    List<Questao> findByMateria(String materia);
    List<Questao> findByTopico(String topico);
    List<Questao> findByMateriaAndTopico(String materia,String topico);
}
