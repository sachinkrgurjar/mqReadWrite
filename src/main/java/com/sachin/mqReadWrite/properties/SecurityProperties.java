package com.sachin.mqReadWrite.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "security")
@Getter
@Setter
public class SecurityProperties {
    private String keyTabPath;
    private String trustStorePath;
    private String trustStorePassword;
    private String ibmMqCipher;
    private String queueJks;


}
