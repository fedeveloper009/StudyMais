package com.projeto.studymais.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.projeto.studymais.model.Usuario;
import com.projeto.studymais.repository.UsuarioRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SequenciaLoginServiceTest {

    private static final ZoneId BRASILIA = ZoneId.of("America/Sao_Paulo");
    private static final String EMAIL = "ana@example.com";

    @Mock
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;
    private SequenciaLoginService service;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setUser_id(1);
        usuario.setEmail(EMAIL);
        when(usuarioRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByIdForUpdate(1)).thenReturn(Optional.of(usuario));
    }

    @Test
    void primeiroLoginDoDiaIniciaSequenciaEmUmNoHorarioDeBrasilia() {
        serviceEm("2026-10-02T02:30:00Z"); // 23:30 do dia anterior em Brasilia

        service.registrarLogin(EMAIL);

        assertEquals(LocalDate.of(2026, 10, 1), usuario.getUltimaDataLogin());
        assertEquals(1, usuario.getDiasDeSequencia());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void loginEmDiasConsecutivosIncrementaUmaVezPorDia() {
        usuario.setUltimaDataLogin(LocalDate.of(2026, 10, 1));
        usuario.setDiasDeSequencia(4);
        serviceEm("2026-10-02T15:00:00Z");

        service.registrarLogin(EMAIL);
        service.registrarLogin(EMAIL);

        assertEquals(LocalDate.of(2026, 10, 2), usuario.getUltimaDataLogin());
        assertEquals(5, usuario.getDiasDeSequencia());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void diaSemLoginReiniciaSequenciaEmUm() {
        usuario.setUltimaDataLogin(LocalDate.of(2026, 9, 30));
        usuario.setDiasDeSequencia(12);
        serviceEm("2026-10-02T15:00:00Z");

        service.registrarLogin(EMAIL);

        assertEquals(LocalDate.of(2026, 10, 2), usuario.getUltimaDataLogin());
        assertEquals(1, usuario.getDiasDeSequencia());
        verify(usuarioRepository).save(usuario);
    }

    private void serviceEm(String instant) {
        service = new SequenciaLoginService(
                usuarioRepository,
                Clock.fixed(Instant.parse(instant), BRASILIA)
        );
    }
}
