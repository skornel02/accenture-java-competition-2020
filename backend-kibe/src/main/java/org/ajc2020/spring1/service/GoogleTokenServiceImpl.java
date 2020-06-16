package org.ajc2020.spring1.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.ajc2020.spring1.config.KIBeConfig;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GoogleTokenServiceImpl implements GoogleTokenService {

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenServiceImpl(KIBeConfig config) {
        List<String> clientIds = config.getGoogle().getClientIds();
        List<String> issuers = config.getGoogle().getIssuers();
        verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                .setAudience(clientIds)
                .setIssuers(issuers)
                .build();
    }

    @Override
    public Optional<String> parseOpenIdToken(String openIDToken) {
        try {
            return Optional.ofNullable(verifier.verify(openIDToken))
                    .map(GoogleIdToken::getPayload)
                    .map(GoogleIdToken.Payload::getEmail);
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

}
