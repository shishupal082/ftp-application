package com.project.ftp.bridge.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.project.ftp.bridge.config.GoogleOAuthClientConfig;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GoogleSheetsOAuthApi {
    final static Logger logger = LoggerFactory.getLogger(GoogleSheetsOAuthApi.class);
    private final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private final List<String> SCOPES =
            Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private final GoogleOAuthClientConfig googleOAuthClientConfig;
    public GoogleSheetsOAuthApi(GoogleOAuthClientConfig googleOAuthClientConfig) {
        this.googleOAuthClientConfig = googleOAuthClientConfig;
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the service-account-credentials.json file cannot be found.
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets.
        InputStream in = GoogleSheetsOAuthApi.class.getResourceAsStream(
                googleOAuthClientConfig.getCredentialFilePath());
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + googleOAuthClientConfig.getCredentialFilePath());
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(
                        googleOAuthClientConfig.getTokenDirPath())))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(
                googleOAuthClientConfig.getLocalServerPort()).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
    private void isValidGoogleOAuthConfig() throws AppException {
        String applicationName = googleOAuthClientConfig.getApplicationName();
        String tokenDirPath = googleOAuthClientConfig.getTokenDirPath();
        String credentialsFilePath = googleOAuthClientConfig.getCredentialFilePath();
        int localServerPort = googleOAuthClientConfig.getLocalServerPort();
        if (tokenDirPath == null || tokenDirPath.isEmpty()) {
            logger.info("Invalid google token directory path (null or empty): {}, googleOAuthClientConfig: {}",
                    tokenDirPath, googleOAuthClientConfig);
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        File file = new File(tokenDirPath);
        if (!file.isDirectory()) {
            logger.info("Invalid google token directory path: {}, googleOAuthClientConfig: {}",
                    tokenDirPath, googleOAuthClientConfig);
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        if (localServerPort < 5000 || localServerPort > 9999) {
            logger.info("Invalid local server port (5000 to 9999): {}, , googleOAuthClientConfig: {}",
                    localServerPort, googleOAuthClientConfig);
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        if (applicationName == null || applicationName.isEmpty()) {
            logger.info("Invalid google application name: {}, googleOAuthClientConfig: {}",
                    applicationName, googleOAuthClientConfig);
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        if (credentialsFilePath == null || credentialsFilePath.isEmpty()) {
            logger.info("Invalid google credentials file path: {}, googleOAuthClientConfig: {}",
                    credentialsFilePath, googleOAuthClientConfig);
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        logger.info("googleOAuthClientConfig: {}", googleOAuthClientConfig);
    }
    public ArrayList<ArrayList<String>> readSheetData(String spreadSheetId, String sheetName) throws AppException {
        // Build a new authorized API client service.
        this.isValidGoogleOAuthConfig();
        List<List<Object>> values = null;
        try {
            NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Credential credential = this.getCredentials(HTTP_TRANSPORT);
            Sheets service =
                    new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                            .setApplicationName(googleOAuthClientConfig.getApplicationName())
                            .build();
            ValueRange response = service.spreadsheets().values()
                    .get(spreadSheetId, sheetName)
                    .execute();
            values = response.getValues();
        } catch (Exception e) {
            logger.info("Error in reading google sheet data: {}, {}, errorMessage: {}",
                    spreadSheetId, sheetName, e.getMessage());
        }

        ArrayList<ArrayList<String>> result = new ArrayList<>();
        ArrayList<String> row;
        if (values != null) {
            for (List<Object> rowTemp : values) {
                row = new ArrayList<>();
                if (rowTemp != null) {
                    for (int j = 0; j < rowTemp.size(); j++) {
                        if (rowTemp.get(j) != null) {
                            row.add(j, (String) rowTemp.get(j));
                        }
                    }
                }
                result.add(row);
            }
        }
        return result;
    }
}
