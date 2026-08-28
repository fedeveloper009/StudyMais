package com.projeto.studymais.repository;

import com.projeto.studymais.model.Tarefa;
import com.projeto.studymais.model.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TarefaRepository extends JpaRepository<Tarefa, Integer> {

    List<Tarefa> findAllByUsuario(Usuario usuario);

    Optional<Tarefa> findByTarefaIdAndUsuario(Integer tarefaId, Usuario usuario);
}
