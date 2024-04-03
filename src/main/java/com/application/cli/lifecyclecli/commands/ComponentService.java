package com.application.cli.lifecyclecli.commands;

import com.application.cli.lifecyclecli.util.ConfigurationResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

@Service
public class ComponentService {

    private final ConfigurationResolver configurationResolver;

    @Autowired
    public ComponentService(ConfigurationResolver configurationResolver) {
        this.configurationResolver = configurationResolver;
    }

    public String isUp(){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = null;
        try {
            request = HttpRequest
                    .newBuilder(new URI("https://crudcrud.com/api/fb77c29fecab4151b702d5f1ecd7616f"))
                    .timeout(Duration.of(10, SECONDS))
                    .GET()
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return response.body();
    }

    public long start(String component){

        ProcessBuilder builder = new ProcessBuilder();
        if (configurationResolver.isWindows()) {
            builder.command("cmd.exe", "/c", String.format("./%s.bat", component));
        } else {
            builder.command("sh", "-c", String.format("./%s.sh", component));
        }
        builder.directory(new File(configurationResolver.getFilePathDeployLocal()));
//        builder.directory(new File(System.getProperty("user.home") + File.separator + "IdeaProjects/third-party-application/springapp/springapp"));
        Process process = null;
        try {
            process = builder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return process.pid();
    }

    public long status(String component){

        ProcessBuilder builder = new ProcessBuilder();
        if (configurationResolver.isWindows()) {
            builder.command("cmd.exe", "/c", String.format("./%s.bat", component));
        } else {
            builder.command("sh", "-c", String.format("./%s.sh", component));
        }
        builder.directory(new File(configurationResolver.getFilePathDeployLocal()));
//        builder.directory(new File(System.getProperty("user.home") + File.separator + "IdeaProjects/third-party-application/springapp/springapp"));
        Process process = null;
        try {
            process = builder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return process.pid();
    }

    public void stop(String component){

    }

}
