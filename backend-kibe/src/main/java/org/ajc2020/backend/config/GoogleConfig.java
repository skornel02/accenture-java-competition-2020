package org.ajc2020.backend.config;

import lombok.Data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
public class GoogleConfig {

    private String authorizationType = "GoogleToken";

    private List<String> clientIds = Collections.singletonList("765993534694-p6c39vh4u187ld6v6gq11v5gnqal74b4.apps.googleusercontent.com");

    private List<String> issuers = Arrays.asList("https://accounts.google.com", "accounts.google.com");

}