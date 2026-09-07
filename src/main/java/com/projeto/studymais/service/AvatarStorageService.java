package com.projeto.studymais.service;

import com.projeto.studymais.exception.StorageOperationException;
import java.net.URI;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AvatarStorageService {

    private static final long DEFAULT_MAX_FILE_SIZE = 5L * 1024 * 1024;

    private final RestClient restClient;
    private final String supabaseUrl;
    private final String serviceRoleKey;
    private final String bucket;
    private final long maxFileSize;

    public AvatarStorageService(
            @Value("${supabase.url:}") String supabaseUrl,
            @Value("${supabase.service-role-key:}") String serviceRoleKey,
            @Value("${supabase.storage.bucket:avatars}") String bucket,
            @Value("${supabase.storage.max-file-size-bytes:5242880}") long maxFileSize
    ) {
        this.restClient = RestClient.create();
        this.supabaseUrl = removeTrailingSlash(supabaseUrl);
        this.serviceRoleKey = serviceRoleKey;
        this.bucket = bucket;
        this.maxFileSize = maxFileSize > 0 ? maxFileSize : DEFAULT_MAX_FILE_SIZE;
    }

    public String upload(Integer userId, MultipartFile file) {
        FileFormat format = validarArquivo(file);
        garantirConfiguracao();
        String objectPath = objectPath(userId);

        try {
            restClient.post()
                    .uri(storageObjectUri(objectPath))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + serviceRoleKey)
                    .header("apikey", serviceRoleKey)
                    .header("x-upsert", "true")
                    .contentType(MediaType.parseMediaType(format.contentType()))
                    .body(file.getBytes())
                    .retrieve()
                    .toBodilessEntity();
            return publicObjectUrl(objectPath);
        } catch (RestClientResponseException | java.io.IOException exception) {
            throw new StorageOperationException("Nao foi possivel armazenar a foto de perfil.", exception);
        }
    }

    public void delete(Integer userId) {
        garantirConfiguracao();
        try {
            restClient.delete()
                    .uri(storageObjectUri(objectPath(userId)))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + serviceRoleKey)
                    .header("apikey", serviceRoleKey)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().value() != 404) {
                throw new StorageOperationException("Nao foi possivel remover a foto de perfil.", exception);
            }
        }
    }

    private FileFormat validarArquivo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("A foto de perfil e obrigatoria.");
        }
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("A foto de perfil excede o tamanho permitido.");
        }

        String contentType = file.getContentType() == null
                ? ""
                : file.getContentType().toLowerCase(Locale.ROOT);
        try {
            byte[] bytes = file.getBytes();
            if (isJpeg(bytes) && ("image/jpeg".equals(contentType) || "image/jpg".equals(contentType))) {
                return new FileFormat("image/jpg");
            }
            if (isPng(bytes) && MediaType.IMAGE_PNG_VALUE.equals(contentType)) {
                return new FileFormat(MediaType.IMAGE_PNG_VALUE);
            }
            if (isWebp(bytes) && "image/webp".equals(contentType)) {
                return new FileFormat("image/webp");
            }
        } catch (java.io.IOException exception) {
            throw new StorageOperationException("Nao foi possivel ler a foto de perfil.", exception);
        }
        throw new IllegalArgumentException("Formato de imagem invalido. Use JPG, PNG ou WEBP.");
    }

    private void garantirConfiguracao() {
        if (supabaseUrl.isBlank() || serviceRoleKey.isBlank() || bucket.isBlank()) {
            throw new StorageOperationException("O Supabase Storage nao esta configurado.");
        }
    }

    private URI storageObjectUri(String objectPath) {
        return URI.create(supabaseUrl + "/storage/v1/object/" + bucket + "/" + objectPath);
    }

    private String publicObjectUrl(String objectPath) {
        return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + objectPath;
    }

    private String objectPath(Integer userId) {
        return "user-" + userId + "/avatar";
    }

    private String removeTrailingSlash(String value) {
        if (value == null) {
            return "";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private boolean isJpeg(byte[] bytes) {
        return bytes.length >= 3
                && (bytes[0] & 0xFF) == 0xFF
                && (bytes[1] & 0xFF) == 0xD8
                && (bytes[2] & 0xFF) == 0xFF;
    }

    private boolean isPng(byte[] bytes) {
        byte[] signature = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        if (bytes.length < signature.length) {
            return false;
        }
        for (int index = 0; index < signature.length; index++) {
            if (bytes[index] != signature[index]) {
                return false;
            }
        }
        return true;
    }

    private boolean isWebp(byte[] bytes) {
        return bytes.length >= 12
                && bytes[0] == 'R'
                && bytes[1] == 'I'
                && bytes[2] == 'F'
                && bytes[3] == 'F'
                && bytes[8] == 'W'
                && bytes[9] == 'E'
                && bytes[10] == 'B'
                && bytes[11] == 'P';
    }

    private record FileFormat(String contentType) {
    }
}
