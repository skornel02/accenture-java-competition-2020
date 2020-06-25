package org.ajc2020.backend.config;

import lombok.Data;

@Data
public class DeviceConfiguration {

    String authorizationType = "DeviceToken";
    String token = "SecretDeviceToken";

}
