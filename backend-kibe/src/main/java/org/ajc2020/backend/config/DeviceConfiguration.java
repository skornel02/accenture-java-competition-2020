package org.ajc2020.backend.config;

import lombok.Data;

@Data
public class DeviceConfiguration {

    private String authorizationType = "DeviceToken";
    private String token = "SecretDeviceToken";

}
