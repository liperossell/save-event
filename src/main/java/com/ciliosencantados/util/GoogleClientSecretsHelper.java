package com.ciliosencantados.util;

import com.ciliosencantados.exception.SaveEventException;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;

import java.util.List;

import static com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants.AUTHORIZATION_SERVER_URL;
import static com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants.TOKEN_SERVER_URL;

public final class GoogleClientSecretsHelper {
    public static final GoogleClientSecrets INSTANCE;

    private GoogleClientSecretsHelper() {
    }

    public static final String HTTP_LOCALHOST = "http://localhost";

    static {
        final GoogleClientSecrets googleClientSecrets = new GoogleClientSecrets();
        GoogleClientSecrets.Details installed = new GoogleClientSecrets.Details();
        installed.setAuthUri(AUTHORIZATION_SERVER_URL);
        installed.setTokenUri(TOKEN_SERVER_URL);
        installed.setRedirectUris(List.of(HTTP_LOCALHOST));
        installed.setClientId(System.getenv("OAUTH_CLIENT_ID"));
        installed.setClientSecret(System.getenv("OAUTH_CLIENT_SECRET"));
        googleClientSecrets.setInstalled(installed);

        INSTANCE = googleClientSecrets;
    }

    public static String getClientId() {
        final String clientId = INSTANCE.getDetails().getClientId();

        if (clientId == null) {
            throw new SaveEventException("Client ID não encontrado");
        }

        return clientId;
    }

    public static String getClientSecret() {
        final String clientSecret = INSTANCE.getDetails().getClientSecret();

        if (clientSecret == null) {
            throw new SaveEventException("Client Secret não encontrado");
        }

        return clientSecret;
    }

    public static String getRefreshToken() {
        return System.getenv("OAUTH_REFRESH_TOKEN");
    }
}
