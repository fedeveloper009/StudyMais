package com.projeto.studymais.repository;

import com.projeto.studymais.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByEmailIgnoreCase(String email);

    Optional<Usuario> findByGoogleSub(String googleSub);

    boolean existsByEmail(String email);

    @Modifying
    @Query("update Usuario u set u.nome = :nome where u.userId = :id")
    int atualizarNomePorId(@Param("id") Integer id, @Param("nome") String nome);
}
