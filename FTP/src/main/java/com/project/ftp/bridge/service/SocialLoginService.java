package com.project.ftp.bridge.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.project.ftp.bridge.config.SocialLoginConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class SocialLoginService {
    final static Logger logger = LoggerFactory.getLogger(SocialLoginService.class);
    private final SocialLoginConfig socialLoginConfig;
    public SocialLoginService(SocialLoginConfig socialLoginConfig) {
        this.socialLoginConfig = socialLoginConfig;
    }
    public String getEmailFromGoogleIdToken(String googleIdToken) {
        String email = null;
        if (socialLoginConfig == null) {
            logger.info("Invalid config parameter, socialLoginConfig: null");
            return null;
        }
        if (!socialLoginConfig.isLoginWithGmail()) {
            logger.info("Invalid config parameter, loginWithGmail = false");
            return null;
        }
        String googleLoginClientId = socialLoginConfig.getGoogleLoginClientId();
        if (googleLoginClientId == null) {
            logger.info("Invalid config parameter, googleLoginClientId = null");
            return null;
        }
        if (googleIdToken == null) {
            logger.info("Invalid googleIdToken: null");
            return null;
        }
        email = "";
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList(googleLoginClientId))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();
        try {
            GoogleIdToken idToken = verifier.verify(googleIdToken);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                // Print user identifier
                String userId = payload.getSubject();
                // Get profile information from payload
                email = payload.getEmail();
                boolean emailVerified = payload.getEmailVerified();
                logger.info("googleUserId: {}, email: {}, emailVerified: {}", userId, email, emailVerified);
//                String name = (String) payload.get("name");
//                String pictureUrl = (String) payload.get("picture");
//                String locale = (String) payload.get("locale");
//                String familyName = (String) payload.get("family_name");
//                String givenName = (String) payload.get("given_name");
//                logger.info("{}", email + emailVerified + name + pictureUrl + locale + familyName + givenName);
            } else {
                logger.info("Invalid google idToken.");
            }
        } catch (Exception e) {
            logger.info("Error in google idToken validation: {}", e.getMessage());
        }
        return email;
    }
}
