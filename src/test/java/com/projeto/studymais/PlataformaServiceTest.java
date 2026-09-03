package com.projeto.studymais;

import com.projeto.studymais.dto.plataforma.PlataformaRequestDTO;
import com.projeto.studymais.model.Plataforma;
import com.projeto.studymais.model.Usuario;
import com.projeto.studymais.repository.PlataformaRepository;
import com.projeto.studymais.security.UsuarioAutenticadoHelper;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlataformaServiceTest {

    @Mock
    private PlataformaRepository plataformaRepository;

    @Mock
    private UsuarioAutenticadoHelper usuarioAutenticadoHelper;

    private PlataformaService plataformaService;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        plataformaService = new PlataformaService(plataformaRepository, usuarioAutenticadoHelper);
        usuario = new Usuario();
        usuario.setUser_id(1);
    }

    @Test
    void criarVinculaPlataformaAoUsuarioAutenticado() {
        when(usuarioAutenticadoHelper.obter()).thenReturn(usuario);
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
    void buscarPorIdRejeitaPlataformaDeOutroUsuario() {
        when(usuarioAutenticadoHelper.obter()).thenReturn(usuario);
        when(plataformaRepository.findByPlataformaIdAndUsuario(7, usuario))
                .thenReturn(Optional.empty());
        when(plataformaRepository.existsById(7)).thenReturn(true);

        assertThrows(AccessDeniedException.class, () -> plataformaService.buscarPorId(7));
    }
}
