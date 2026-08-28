package com.projeto.studymais;

import com.projeto.studymais.dto.usuario.UsuarioRequestDTO;
import com.projeto.studymais.exception.DuplicateEmailException;
import com.projeto.studymais.model.Usuario;
import com.projeto.studymais.repository.UsuarioRepository;
import com.projeto.studymais.security.UsuarioAutenticadoHelper;
import com.projeto.studymais.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioAutenticadoHelper usuarioAutenticadoHelper;

    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService(usuarioRepository, passwordEncoder, usuarioAutenticadoHelper);
        lenient().when(passwordEncoder.encode(any()))
                .thenReturn("$2a$10$12345678901234567890123456789012345678901234567890123");
    }

    @Test
    void criarRejeitaEmailDuplicadoAntesDeSalvar() {
        when(usuarioRepository.existsByEmail("ana@example.com")).thenReturn(true);

        assertThrows(
                DuplicateEmailException.class,
                () -> usuarioService.criar(new UsuarioRequestDTO("Ana", "ana@example.com", "senha123"))
        );
    }

    @Test
    void criarTrataViolacaoDeUnicidadeComoDuplicidade() {
        when(usuarioRepository.existsByEmail("ana@example.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class)))
                .thenThrow(new DataIntegrityViolationException("unique email"));

        assertThrows(
                DuplicateEmailException.class,
                () -> usuarioService.criar(new UsuarioRequestDTO("Ana", "ana@example.com", "senha123"))
        );
    }

    @Test
    void atualizarPermiteManterProprioEmail() {
        Usuario atual = usuario(1, "ana@example.com");
        when(usuarioAutenticadoHelper.obter()).thenReturn(atual);
        when(usuarioRepository.findById(1)).thenReturn(java.util.Optional.of(atual));
        when(usuarioRepository.save(atual)).thenReturn(atual);

        assertDoesNotThrow(() -> usuarioService.atualizar(
                1,
                new UsuarioRequestDTO("Ana", "ana@example.com", "senha123")
        ));
        verify(usuarioRepository, org.mockito.Mockito.never()).existsByEmail("ana@example.com");
    }

    @Test
    void atualizarRejeitaEmailDeOutroUsuario() {
        Usuario atual = usuario(1, "ana@example.com");
        when(usuarioAutenticadoHelper.obter()).thenReturn(atual);
        when(usuarioRepository.findById(1)).thenReturn(java.util.Optional.of(atual));
        when(usuarioRepository.existsByEmail("bruno@example.com")).thenReturn(true);

        assertThrows(
                DuplicateEmailException.class,
                () -> usuarioService.atualizar(
                        1,
                        new UsuarioRequestDTO("Ana", "bruno@example.com", "senha123")
                )
        );
    }

    private Usuario usuario(int id, String email) {
        Usuario usuario = new Usuario();
        usuario.setUser_id(id);
        usuario.setNome("Usuario");
        usuario.setEmail(email);
        usuario.setSenha("senha123");
        return usuario;
    }
}
