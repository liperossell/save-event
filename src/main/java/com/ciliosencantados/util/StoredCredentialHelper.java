package com.ciliosencantados.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.StoredCredential;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public final class StoredCredentialHelper {

    private static final String STORED_CREDENTIALS_FILE_PATH = "/tokens.json";

    private StoredCredentialHelper() { }

    public static void write(final StoredCredential storedCredential) {
        try {
            File file = new File(STORED_CREDENTIALS_FILE_PATH);
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(file, storedCredential);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar credenciais", e);
        }
    }

    public static StoredCredential load() {
        try {
            InputStream file = StoredCredentialHelper.class.getResourceAsStream(STORED_CREDENTIALS_FILE_PATH);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(file, StoredCredential.class);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar credenciais", e);
        }
    }
}
