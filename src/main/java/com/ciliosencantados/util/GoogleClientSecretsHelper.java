package com.ciliosencantados.util;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class GoogleClientSecretsHelper {

    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    public static final GoogleClientSecrets INSTANCE = load();

    private GoogleClientSecretsHelper() {
    }

    private static GoogleClientSecrets load() {
        try {
            InputStream file = GoogleClientSecretsHelper.class.getResourceAsStream(CREDENTIALS_FILE_PATH);

            if (file == null) {
                throw new RuntimeException("Arquivo de credenciais não encontrado");
            }

            GoogleClientSecrets googleClientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(file));
            googleClientSecrets.getDetails().setClientId(System.getenv("OAUTH_CLIENT_ID"));
            googleClientSecrets.getDetails().setClientSecret(System.getenv("OAUTH_CLIENT_SECRET"));

            return googleClientSecrets;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar credenciais", e);
        }
    }

    public static String getClientId() {
        return INSTANCE.getDetails().getClientId();
    }

    public static String getClientSecret() {
        return INSTANCE.getDetails().getClientSecret();
    }
}
