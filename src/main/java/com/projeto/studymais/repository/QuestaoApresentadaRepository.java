package com.projeto.studymais.repository;
import com.projeto.studymais.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface QuestaoApresentadaRepository extends JpaRepository<QuestaoApresentada,Long> {
    List<QuestaoApresentada> findBySessaoIdAndUsuario(String sessaoId, Usuario usuario);
    Optional<QuestaoApresentada> findByIdAndUsuario(Long id, Usuario usuario);
    List<QuestaoApresentada> findTop100ByUsuarioOrderByApresentadaEmDesc(Usuario usuario);
}
