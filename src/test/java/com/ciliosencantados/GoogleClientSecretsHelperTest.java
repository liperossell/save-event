package com.ciliosencantados;

import com.ciliosencantados.util.GoogleClientSecretsHelper;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GoogleClientSecretsHelperTest {

    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @BeforeEach
    void setUp() {
        // Reset environment variables before each test
        System.clearProperty("OAUTH_CLIENT_ID");
        System.clearProperty("OAUTH_CLIENT_SECRET");
    }

    @Test
    void loadClientSecretsSuccessfully() throws IOException {
        InputStream file = getClass().getResourceAsStream(CREDENTIALS_FILE_PATH);
        assertNotNull(file, "Credentials file should be found");

        GoogleClientSecrets googleClientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(file));
        assertNotNull(googleClientSecrets, "GoogleClientSecrets should be loaded");
    }

    @Test
    void throwExceptionWhenCredentialsFileNotFound() {
        InputStream resourceAsStream = GoogleClientSecretsHelper.class.getResourceAsStream("/nonexistent.json");

        assertNull(resourceAsStream, "Resource should not be found");
    }
}