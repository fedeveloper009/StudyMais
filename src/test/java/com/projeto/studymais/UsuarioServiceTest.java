package com.projeto.studymais;

import com.projeto.studymais.dto.usuario.UsuarioRequestDTO;
import com.projeto.studymais.dto.usuario.UsuarioResponseDTO;
import com.projeto.studymais.dto.usuario.AtualizarNomeRequestDTO;
import com.projeto.studymais.dto.usuario.AlterarSenhaRequestDTO;
import com.projeto.studymais.exception.DuplicateEmailException;
import com.projeto.studymais.model.Usuario;
import com.projeto.studymais.repository.UsuarioRepository;
import com.projeto.studymais.security.UsuarioAutenticadoHelper;
import com.projeto.studymais.service.AvatarStorageService;
import com.projeto.studymais.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    @Mock
    private AvatarStorageService avatarStorageService;

    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService(
                usuarioRepository,
                passwordEncoder,
                usuarioAutenticadoHelper,
                avatarStorageService
        );
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

    @Test
    void atualizarNomePermiteAlterarSomenteOProprioNome() {
        Usuario atual = usuario(1, "ana@example.com");
        atual.setXp(320);
        atual.setDiasDeSequencia(8);
        atual.setTempoEstudado(5400L);
        atual.setMateriaEstudada("Matematica");
        atual.setConquistas(java.util.List.of("Constancia"));
        String senha = atual.getSenha();
        when(usuarioAutenticadoHelper.obter()).thenReturn(atual);
        when(usuarioRepository.findById(1)).thenReturn(java.util.Optional.of(atual));
        when(usuarioRepository.atualizarNomePorId(1, "Novo Nome")).thenReturn(1);

        UsuarioResponseDTO response = usuarioService.atualizarNome(
                1,
                new AtualizarNomeRequestDTO("  Novo Nome  ")
        );

        assertEquals("Novo Nome", response.nome());
        assertEquals("ana@example.com", response.email());
        assertEquals(320, response.xp());
        assertEquals(8, response.diasDeSequencia());
        assertEquals(5400L, response.tempoEstudado());
        assertEquals("Matematica", response.materiaEstudada());
        assertEquals(java.util.List.of("Constancia"), response.conquistas());
        assertEquals(senha, atual.getSenha());
        verify(usuarioRepository).atualizarNomePorId(1, "Novo Nome");
    }

    @Test
    void atualizarNomeRejeitaUsuarioNaoAutenticado() {
        when(usuarioAutenticadoHelper.obter())
                .thenThrow(new org.springframework.security.authentication.AuthenticationCredentialsNotFoundException(
                        "Autenticacao necessaria."
                ));

        assertThrows(
                org.springframework.security.authentication.AuthenticationCredentialsNotFoundException.class,
                () -> usuarioService.atualizarNome(1, new AtualizarNomeRequestDTO("Novo Nome"))
        );
    }

    @Test
    void atualizarNomeImpedeAlterarOutroUsuario() {
        Usuario autenticado = usuario(1, "ana@example.com");
        Usuario outroUsuario = usuario(2, "bruno@example.com");
        when(usuarioAutenticadoHelper.obter()).thenReturn(autenticado);
        when(usuarioRepository.findById(2)).thenReturn(java.util.Optional.of(outroUsuario));

        assertThrows(
                org.springframework.security.access.AccessDeniedException.class,
                () -> usuarioService.atualizarNome(2, new AtualizarNomeRequestDTO("Novo Nome"))
        );
    }

    @Test
    void atualizarNomeRejeitaNomeInvalido() {
        Usuario atual = usuario(1, "ana@example.com");
        when(usuarioAutenticadoHelper.obter()).thenReturn(atual);
        when(usuarioRepository.findById(1)).thenReturn(java.util.Optional.of(atual));

        assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.atualizarNome(1, new AtualizarNomeRequestDTO(null))
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.atualizarNome(1, new AtualizarNomeRequestDTO(" "))
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.atualizarNome(1, new AtualizarNomeRequestDTO("A".repeat(101)))
        );
    }

    @Test
    void atualizarNomeRetorna404ParaUsuarioInexistente() {
        Usuario autenticado = usuario(1, "ana@example.com");
        when(usuarioAutenticadoHelper.obter()).thenReturn(autenticado);
        when(usuarioRepository.findById(2)).thenReturn(java.util.Optional.empty());

        assertThrows(
                com.projeto.studymais.exception.ResourceNotFoundException.class,
                () -> usuarioService.atualizarNome(2, new AtualizarNomeRequestDTO("Novo Nome"))
        );
    }

    @Test
    void alterarSenhaValidaSenhaAtualECodificaSomenteANovaSenha() {
        Usuario atual = usuario(1, "ana@example.com");
        atual.setXp(320);
        atual.setDiasDeSequencia(8);
        atual.setTempoEstudado(5400L);
        atual.setMateriaEstudada("Matematica");
        atual.setConquistas(java.util.List.of("Constancia"));
        String senhaAnterior = atual.getSenha();
        String senhaCodificada = "$2a$10$novo-hash";
        when(usuarioAutenticadoHelper.obter()).thenReturn(atual);
        when(usuarioRepository.findById(1)).thenReturn(java.util.Optional.of(atual));
        when(passwordEncoder.matches("senha123", senhaAnterior)).thenReturn(true);
        when(passwordEncoder.encode("nova123")).thenReturn(senhaCodificada);
        when(usuarioRepository.save(atual)).thenReturn(atual);

        UsuarioResponseDTO response = usuarioService.alterarSenha(
                1,
                new AlterarSenhaRequestDTO("senha123", "nova123", "nova123")
        );

        assertEquals(senhaCodificada, atual.getSenha());
        assertEquals("ana@example.com", response.email());
        assertEquals(320, response.xp());
        assertEquals(8, response.diasDeSequencia());
        assertEquals(5400L, response.tempoEstudado());
        assertEquals("Matematica", response.materiaEstudada());
        assertEquals(java.util.List.of("Constancia"), response.conquistas());
        verify(passwordEncoder).matches("senha123", senhaAnterior);
        verify(passwordEncoder).encode("nova123");
        verify(usuarioRepository).save(atual);
    }

    @Test
    void alterarSenhaRejeitaSenhaAtualInvalidaSemSalvar() {
        Usuario atual = usuario(1, "ana@example.com");
        when(usuarioAutenticadoHelper.obter()).thenReturn(atual);
        when(usuarioRepository.findById(1)).thenReturn(java.util.Optional.of(atual));
        when(passwordEncoder.matches("errada", atual.getSenha())).thenReturn(false);

        assertThrows(
                org.springframework.security.authentication.BadCredentialsException.class,
                () -> usuarioService.alterarSenha(
                        1,
                        new AlterarSenhaRequestDTO("errada", "nova123", "nova123")
                )
        );
        verify(usuarioRepository, org.mockito.Mockito.never()).save(org.mockito.ArgumentMatchers.any());
        verify(passwordEncoder, org.mockito.Mockito.never()).encode("nova123");
    }

    @Test
    void alterarSenhaRejeitaConfirmacaoDiferente() {
        Usuario atual = usuario(1, "ana@example.com");
        when(usuarioAutenticadoHelper.obter()).thenReturn(atual);
        when(usuarioRepository.findById(1)).thenReturn(java.util.Optional.of(atual));

        assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.alterarSenha(
                        1,
                        new AlterarSenhaRequestDTO("senha123", "nova123", "outra123")
                )
        );
        verify(passwordEncoder, org.mockito.Mockito.never()).matches(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString()
        );
    }

    @Test
    void alterarSenhaImpedeAlterarOutroUsuario() {
        Usuario autenticado = usuario(1, "ana@example.com");
        Usuario outroUsuario = usuario(2, "bruno@example.com");
        when(usuarioAutenticadoHelper.obter()).thenReturn(autenticado);
        when(usuarioRepository.findById(2)).thenReturn(java.util.Optional.of(outroUsuario));

        assertThrows(
                org.springframework.security.access.AccessDeniedException.class,
                () -> usuarioService.alterarSenha(
                        2,
                        new AlterarSenhaRequestDTO("senha123", "nova123", "nova123")
                )
        );
    }

    @Test
    void alterarFotoPerfilSalvaSomenteAUrlDaFotoDoProprioUsuario() {
        Usuario atual = usuario(1, "ana@example.com");
        atual.setFotoPerfilUrl(null);
        MockMultipartFile foto = new MockMultipartFile(
                "foto",
                "avatar.png",
                "image/png",
                new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A}
        );
        when(usuarioAutenticadoHelper.obter()).thenReturn(atual);
        when(usuarioRepository.findById(1)).thenReturn(java.util.Optional.of(atual));
        when(avatarStorageService.upload(1, foto)).thenReturn("https://storage.example/avatar");
        when(usuarioRepository.save(atual)).thenReturn(atual);

        UsuarioResponseDTO response = usuarioService.alterarFotoPerfil(1, foto);

        assertEquals("https://storage.example/avatar", response.fotoPerfilUrl());
        verify(avatarStorageService).upload(1, foto);
        verify(usuarioRepository).save(atual);
    }

    @Test
    void removerFotoPerfilApagaArquivoEReferenciaDoProprioUsuario() {
        Usuario atual = usuario(1, "ana@example.com");
        atual.setFotoPerfilUrl("https://storage.example/avatar");
        when(usuarioAutenticadoHelper.obter()).thenReturn(atual);
        when(usuarioRepository.findById(1)).thenReturn(java.util.Optional.of(atual));
        when(usuarioRepository.save(atual)).thenReturn(atual);

        UsuarioResponseDTO response = usuarioService.removerFotoPerfil(1);

        assertEquals(null, response.fotoPerfilUrl());
        verify(avatarStorageService).delete(1);
        verify(usuarioRepository).save(atual);
    }

    @Test
    void criarPreservaXpDiasDeSequenciaTempoEstudadoMateriaEConquistas() {
        when(usuarioRepository.existsByEmail("ana@example.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioResponseDTO response = usuarioService.criar(
                new UsuarioRequestDTO(
                        "Ana",
                        "ana@example.com",
                        "senha123",
                        320,
                        8,
                        5400L,
                        "Matematica",
                        java.util.List.of("Primeiro passo", "Constancia")
                )
        );

        assertDoesNotThrow(() -> {
            org.junit.jupiter.api.Assertions.assertEquals(320, response.xp());
            org.junit.jupiter.api.Assertions.assertEquals(8, response.diasDeSequencia());
            org.junit.jupiter.api.Assertions.assertEquals(5400L, response.tempoEstudado());
            org.junit.jupiter.api.Assertions.assertEquals("Matematica", response.materiaEstudada());
            org.junit.jupiter.api.Assertions.assertEquals(java.util.List.of("Primeiro passo", "Constancia"), response.conquistas());
        });
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
