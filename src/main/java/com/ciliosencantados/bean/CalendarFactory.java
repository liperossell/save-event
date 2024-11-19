package com.ciliosencantados.bean;

import com.ciliosencantados.exception.SaveEventException;
import com.ciliosencantados.util.GoogleClientSecretsHelper;
import com.google.api.client.auth.oauth2.*;
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
            CALENDAR = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredential()).setApplicationName(APPLICATION_NAME).build();
        } catch (GeneralSecurityException | IOException e) {
            throw new SaveEventException(e);
        }
    }

    private static Credential getCredential() throws IOException {
        try {
            final String clientId = GoogleClientSecretsHelper.getClientId();
            final String clientSecret = GoogleClientSecretsHelper.getClientSecret();
            final String refreshToken = GoogleClientSecretsHelper.getRefreshToken();
            final Credential.Builder credentialBuilder = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod()).setJsonFactory(JSON_FACTORY).setTransport(HTTP_TRANSPORT).setClientAuthentication(new ClientParametersAuthentication(clientId, clientSecret)).setTokenServerEncodedUrl(GoogleOAuthConstants.TOKEN_SERVER_URL);

            final Credential credential = credentialBuilder.build().setRefreshToken(refreshToken);
            credential.refreshToken();

            return credential;
        } catch (Exception e) {
            return newAuthorization();
        }
    }

    private static Credential newAuthorization() throws IOException {
        MemoryDataStoreFactory dataStore = new MemoryDataStoreFactory();
        String clientId = GoogleClientSecretsHelper.getClientId();
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, GoogleClientSecretsHelper.INSTANCE, SCOPES).setDataStoreFactory(dataStore).addRefreshListener(new DataStoreCredentialRefreshListener(clientId, dataStore)).setAccessType("offline").build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8989).build();

        return new AuthorizationCodeInstalledApp(flow, receiver).authorize(clientId);
    }

    private CalendarFactory() {
    }
}
