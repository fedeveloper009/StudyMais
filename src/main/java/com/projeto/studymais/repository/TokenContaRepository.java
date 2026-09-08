package com.projeto.studymais.repository;

import com.projeto.studymais.model.TokenConta;
import com.projeto.studymais.model.TokenTipo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

public interface TokenContaRepository extends JpaRepository<TokenConta, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<TokenConta> findByTokenHashAndTipo(String tokenHash, TokenTipo tipo);

    @Modifying
    @Query("delete from TokenConta t where t.usuario.userId = :usuarioId and t.tipo = :tipo")
    int removerPorUsuarioETipo(@Param("usuarioId") Integer usuarioId, @Param("tipo") TokenTipo tipo);
}
