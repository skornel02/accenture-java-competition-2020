package org.ajc2020.spring1.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import java.util.Optional;

public interface GoogleTokenService {

    Optional<String> parseOpenIdToken(String openIDToken);

}
