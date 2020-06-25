package org.ajc2020.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kibe")
@Data
public class KIBeConfig {

    @NestedConfigurationProperty
    AdminConfiguration admin = new AdminConfiguration();


    @NestedConfigurationProperty
    DeviceConfiguration device = new DeviceConfiguration();

    @NestedConfigurationProperty
    GoogleConfig google = new GoogleConfig();

}
