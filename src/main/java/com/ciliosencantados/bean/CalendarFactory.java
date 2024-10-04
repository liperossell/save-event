package com.ciliosencantados.bean;

import com.ciliosencantados.util.GoogleClientSecretsHelper;
import com.ciliosencantados.util.StoredCredentialHelper;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.DataStoreCredentialRefreshListener;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Set;

@ApplicationScoped
public class CalendarFactory {
    private static final NetHttpTransport HTTP_TRANSPORT;
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String APPLICATION_NAME = "EncantadasLash";
    private static final Set<String> SCOPES = Set.of(CalendarScopes.CALENDAR);

    public static final Calendar CALENDAR;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            CALENDAR = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials()).setApplicationName(APPLICATION_NAME).build();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Credential getCredentials() throws IOException {
        StoredCredential storedCredential = StoredCredentialHelper.load();
        if (storedCredential == null || storedCredential.getAccessToken() == null || storedCredential.getAccessToken().isBlank()) {
            Credential credential = newAuthorization();

            storeNewAuthorizationToken(credential);

            return credential;
        }

        final String clientId = GoogleClientSecretsHelper.getClientId();
        final String clientSecret = GoogleClientSecretsHelper.getClientSecret();

        return getCredential(clientId, clientSecret, storedCredential);
    }

    private static void storeNewAuthorizationToken(Credential credential) {
        StoredCredential storedCredential;
        storedCredential = new StoredCredential();
        storedCredential.setAccessToken(credential.getAccessToken());
        storedCredential.setRefreshToken(credential.getRefreshToken());
        storedCredential.setExpirationTimeMilliseconds(credential.getExpirationTimeMilliseconds());

        StoredCredentialHelper.write(storedCredential);
    }

    private static Credential getCredential(String clientId, String clientSecret, StoredCredential storedCredential) {
        Credential.Builder credentialBuilder = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                .setJsonFactory(JSON_FACTORY)
                .setTransport(HTTP_TRANSPORT)
                .setClientAuthentication(new ClientParametersAuthentication(clientId, clientSecret))
                .setTokenServerEncodedUrl(GoogleOAuthConstants.TOKEN_SERVER_URL); // Use TOKEN_SERVER_URL instead of AUTHORIZATION_SERVER_URL

        Credential credential = credentialBuilder.build()
                .setAccessToken(storedCredential.getAccessToken())
                .setRefreshToken(storedCredential.getRefreshToken())
                .setExpirationTimeMilliseconds(storedCredential.getExpirationTimeMilliseconds());

        // Refresh the token only if it has expired
        if (System.currentTimeMillis() > credential.getExpirationTimeMilliseconds()) {
            try {
                credential.refreshToken();
                // Update stored credential with new access token and expiration
                storeNewAuthorizationToken(credential);
            } catch (IOException e) {
                    throw new RuntimeException(e);
            }
        }

        return credential;
    }

    private static Credential newAuthorization() throws IOException {
        MemoryDataStoreFactory dataStore = new MemoryDataStoreFactory();
        String clientId = GoogleClientSecretsHelper.getClientId();
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, GoogleClientSecretsHelper.INSTANCE, SCOPES).setDataStoreFactory(dataStore).addRefreshListener(new DataStoreCredentialRefreshListener(clientId, dataStore)).setAccessType("offline").build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8989).build();

        return new AuthorizationCodeInstalledApp(flow, receiver).authorize(clientId);
    }
}
