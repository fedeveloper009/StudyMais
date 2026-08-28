package com.projeto.studymais.repository;

import com.projeto.studymais.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByEmailIgnoreCase(String email);

    Optional<Usuario> findByGoogleSub(String googleSub);

    boolean existsByEmail(String email);
}
