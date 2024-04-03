package com.application.cli.lifecyclecli.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("third-party-application")
public class EnvironmentConfigurationProperties {
    private Paths paths;
    @Getter
    @Setter
    public static class Paths{
        String userFile;
        String deployLocal;
        String testApp;
    }
}
