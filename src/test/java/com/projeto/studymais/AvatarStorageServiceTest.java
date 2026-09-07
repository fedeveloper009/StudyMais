package com.projeto.studymais;

import com.projeto.studymais.service.AvatarStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AvatarStorageServiceTest {

    @Test
    void rejeitaArquivoVazio() {
        AvatarStorageService service = new AvatarStorageService(
                "",
                "",
                "avatars",
                5 * 1024 * 1024
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.upload(
                        1,
                        new MockMultipartFile("foto", "avatar.png", "image/png", new byte[0])
                )
        );
    }

    @Test
    void rejeitaConteudoQueNaoCorrespondeAoFormatoDeclarado() {
        AvatarStorageService service = new AvatarStorageService(
                "",
                "",
                "avatars",
                5 * 1024 * 1024
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.upload(
                        1,
                        new MockMultipartFile("foto", "avatar.png", "image/png", "nao e png".getBytes())
                )
        );
    }

    @Test
    void rejeitaArquivoAcimaDoLimite() {
        AvatarStorageService service = new AvatarStorageService(
                "",
                "",
                "avatars",
                4
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.upload(
                        1,
                        new MockMultipartFile("foto", "avatar.png", "image/png", new byte[5])
                )
        );
    }
}
