package org.ajc2020.backend.service;

import java.util.Optional;

public interface GoogleTokenService {

    Optional<String> parseOpenIdToken(String openIDToken);

}
