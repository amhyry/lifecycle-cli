package com.application.cli.lifecyclecli.util;

import com.application.cli.lifecyclecli.config.EnvironmentConfigurationProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@Getter
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConfigurationResolver {
    String filePathUserPropertiesFile;
    String filePathDeployLocal;
    EnvironmentConfigurationProperties environmentConfigurationProperties;

    @Autowired
    public ConfigurationResolver(@Value("${third-party-application.paths.user-file}") String filePathUserPropertiesFile,
                                 @Value("${third-party-application.paths.deploy-local}") String filePathDeployLocal, EnvironmentConfigurationProperties environmentConfigurationProperties) {
        this.filePathUserPropertiesFile = filePathUserPropertiesFile;
        this.filePathDeployLocal = filePathDeployLocal;
        this.environmentConfigurationProperties = environmentConfigurationProperties;
    }

    public String getProperties(){
        var properties = new Properties();
        try (var stream = new FileInputStream(environmentConfigurationProperties.getPaths().getUserFile())) {
            properties.load(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<String> propList = properties.entrySet().stream().map(prop -> String.format("%s=%s%n", prop.getKey(), prop.getValue())).toList();
        return String.join("", propList);
    }
    public String getProperty(String propertyKey){
        var properties = new Properties();
        try (var stream = new FileInputStream(filePathUserPropertiesFile)) {
            properties.load(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties.getProperty(propertyKey);
    }


    public String getNamingProviderUriOf(String component){
        return getProperty(String.format("%s_NAMING_PROVIDER_URL",component.toUpperCase()));
    }

    public String getHealthEndpointOf(String component){
        String healthEndpoint = getNamingProviderUriOf(component);
        return healthEndpoint == null ? "N/A" : String.format("%s/api/v1/health",healthEndpoint);
    }

    public List<String> listAllCalypsoStartScripts(){
        try (Stream<Path> stream = Files.list(Paths.get(filePathDeployLocal))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
//                    .filter(e -> e.endsWith(isWindows() ? ".bat" : ".sh"))
                    .map(Path::toString)
                    .filter( e -> e.endsWith(isWindows() ? ".bat" : ".sh"))

                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public List<String> listAllServerHealthpoints(){

        List<String> scripts = listAllCalypsoStartScripts();

        return scripts.stream().map(e -> e.replace(isWindows()? ".bat" : ".sh", "")).map(e -> String.format("%-35s = %s", e, getHealthEndpointOf(e))).toList();

    }

    public Map<String, String> serverHealthpoints(){

        List<String> scripts = listAllCalypsoStartScripts();

        Map<String, String> endPointmap = new HashMap<>();
        scripts.stream().map(e -> e.replace(isWindows()? ".bat" : ".sh", "")).filter(e-> !getHealthEndpointOf(e).equalsIgnoreCase("N/A")).forEach(e -> endPointmap.put(e, getHealthEndpointOf(e)));
        return endPointmap;

    }

    public boolean isWindows(){
        return System.getProperty("os.name").toLowerCase().startsWith("windows");
    }
}
