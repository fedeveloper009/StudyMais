package com.projeto.studymais.repository;

import com.projeto.studymais.model.Materia;
import com.projeto.studymais.model.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MateriaRepository extends JpaRepository<Materia, Integer> {

    List<Materia> findAllByUsuario(Usuario usuario);

    Optional<Materia> findByMateriaIdAndUsuario(Integer materiaId, Usuario usuario);
}
