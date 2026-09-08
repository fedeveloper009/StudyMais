package com.projeto.studymais;

import com.projeto.studymais.dto.plataforma.PlataformaRequestDTO;
import com.projeto.studymais.model.Plano;
import com.projeto.studymais.model.Plataforma;
import com.projeto.studymais.model.Usuario;
import com.projeto.studymais.repository.PlataformaRepository;
import com.projeto.studymais.repository.UsuarioRepository;
import com.projeto.studymais.security.UsuarioAutenticadoHelper;
import com.projeto.studymais.service.LimitePlanoService;
import com.projeto.studymais.service.PlataformaService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlataformaServiceTest {

    @Mock
    private PlataformaRepository plataformaRepository;

    @Mock
    private UsuarioAutenticadoHelper usuarioAutenticadoHelper;

    @Mock
    private UsuarioRepository usuarioRepository;

    private PlataformaService plataformaService;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        plataformaService = new PlataformaService(
                plataformaRepository,
                usuarioAutenticadoHelper,
                usuarioRepository,
                new LimitePlanoService()
        );
        usuario = new Usuario();
        usuario.setUser_id(1);
        lenient().when(usuarioRepository.findByIdForUpdate(1)).thenReturn(Optional.of(usuario));
    }

    @Test
    void criarVinculaPlataformaAoUsuarioAutenticado() {
        when(usuarioAutenticadoHelper.obter()).thenReturn(usuario);
        when(plataformaRepository.countByUsuario(usuario)).thenReturn(0L);
        when(plataformaRepository.save(any(Plataforma.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = plataformaService.criar(
                new PlataformaRequestDTO("Moodle", "Ambiente virtual", "https://moodle.example", 1)
        );

        assertEquals("Moodle", response.nomePlataforma());
        assertEquals(1, response.usuarioId());
    }

    @Test
    void criarRejeitaUsuarioDiferenteDoAutenticado() {
        when(usuarioAutenticadoHelper.obter()).thenReturn(usuario);

        assertThrows(
                AccessDeniedException.class,
                () -> plataformaService.criar(
                        new PlataformaRequestDTO("Moodle", null, "https://moodle.example", 2)
                )
        );
    }

    @Test
    void freeNaoPodeCriarSextaPlataforma() {
        when(usuarioAutenticadoHelper.obter()).thenReturn(usuario);
        when(plataformaRepository.countByUsuario(usuario)).thenReturn(5L);

        assertThrows(
                com.projeto.studymais.exception.LimitePlataformasException.class,
                () -> plataformaService.criar(
                        new PlataformaRequestDTO("Moodle", null, "https://moodle.example", 1)
                )
        );
    }

    @Test
    void usuarioSemPlanoUsaLimiteFree() {
        usuario.setPlano(null);
        when(usuarioAutenticadoHelper.obter()).thenReturn(usuario);
        when(plataformaRepository.countByUsuario(usuario)).thenReturn(5L);

        assertThrows(
                com.projeto.studymais.exception.LimitePlataformasException.class,
                () -> plataformaService.criar(
                        new PlataformaRequestDTO("Moodle", null, "https://moodle.example", 1)
                )
        );
    }

    @Test
    void proNaoPodeCriarDecimaPrimeiraPlataforma() {
        usuario.setPlano(Plano.PRO);
        when(usuarioAutenticadoHelper.obter()).thenReturn(usuario);
        when(plataformaRepository.countByUsuario(usuario)).thenReturn(10L);

        assertThrows(
                com.projeto.studymais.exception.LimitePlataformasException.class,
                () -> plataformaService.criar(
                        new PlataformaRequestDTO("Moodle", null, "https://moodle.example", 1)
                )
        );
    }

    @Test
    void premiumNaoPossuiLimite() {
        usuario.setPlano(Plano.PREMIUM);
        when(usuarioAutenticadoHelper.obter()).thenReturn(usuario);
        when(plataformaRepository.countByUsuario(usuario)).thenReturn(100L);
        when(plataformaRepository.save(any(Plataforma.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertEquals(
                1,
                plataformaService.criar(
                        new PlataformaRequestDTO("Moodle", null, "https://moodle.example", 1)
                ).usuarioId()
        );
    }

    @Test
    void buscarPorIdRejeitaPlataformaDeOutroUsuario() {
        when(usuarioAutenticadoHelper.obter()).thenReturn(usuario);
        when(plataformaRepository.findByPlataformaIdAndUsuario(7, usuario))
                .thenReturn(Optional.empty());
        when(plataformaRepository.existsById(7)).thenReturn(true);

        assertThrows(AccessDeniedException.class, () -> plataformaService.buscarPorId(7));
    }
}
