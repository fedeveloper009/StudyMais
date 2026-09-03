package com.projeto.studymais.repository;

import com.projeto.studymais.model.Plataforma;
import com.projeto.studymais.model.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlataformaRepository extends JpaRepository<Plataforma, Integer> {

    List<Plataforma> findAllByUsuario(Usuario usuario);

    Optional<Plataforma> findByPlataformaIdAndUsuario(Integer plataformaId, Usuario usuario);
}
