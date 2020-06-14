package org.ajc2020.spring1.config;

import lombok.Data;

@Data
public class DeviceConfiguration {

    String authorizationType = "DeviceToken";
    String token = "SecretDeviceToken";

}
