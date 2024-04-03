package com.application.cli.lifecyclecli;

import com.application.cli.lifecyclecli.commands.ComponentService;
import com.application.cli.lifecyclecli.util.ConfigurationResolver;
import org.springframework.shell.command.annotation.*;

import java.io.*;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.springframework.shell.command.invocation.InvocableShellMethod.log;

@Command
@EnableCommand
public class LifecycleCommands {

    ConfigurationResolver configurationResolver;
    ComponentService componentService;

    public LifecycleCommands(ConfigurationResolver configurationResolver, ComponentService componentService) {
        this.configurationResolver = configurationResolver;
        this.componentService = componentService;
    }

    @Command(command = "config", description = "Get Property from properties file", group = "Lifecycle")
    public String helloWorld(@Option(longNames = "key", shortNames = 'k', description = "Property Key") String key) {
        if(key == null){
            return configurationResolver.getProperties();
        }
        return configurationResolver.getProperty(key) != null ? configurationResolver.getProperty(key) : String.format("key '%s' is not present", key);
    }

    @Command(command = "server-components-config", description = "Get Property from properties file", group = "Lifecycle")
    public String listComponents() {
        return String.join("\n", configurationResolver.listAllCalypsoStartScripts());
    }

    @Command(command = "server-components-health", description = "Get Property from properties file", group = "Lifecycle")
    public String listComponentsHealth() {

        List<String> endpoints =  configurationResolver.serverHealthpoints().entrySet().stream().map(prop -> String.format("%-35s= %s%n", prop.getKey(), prop.getValue())).toList();

        return String.join("", endpoints);
    }

    @Command(command = "readiness")
    public String isUp() {
        return componentService.isUp();


    }

    @Command(command = "lifeness", description = "Checks all Processes PID running of Application", group = "Lifecycle")
    private static void infoOfLiveProcesses(
//            @Option(longNames = "server", shortNames = "s") String serverComponent,
//            @OptionValues(provider = MyValuesCompletionResolver.class)

    ) {
        Stream<ProcessHandle> liveProcesses = ProcessHandle.allProcesses();
        liveProcesses.filter(ProcessHandle::isAlive)
                .forEach(ph -> {
                    if(Arrays.stream(ph.info().arguments().orElse(new String[]{"N/A"})).anyMatch(e -> e.contains("springapp"))){
                        log.info(String.format("%d", ph.pid()));

                        System.out.println("PID: " + ph.pid());
                        System.out.println("Command: " + ph.info().command().orElse("N/A"));
                        System.out.println("CPU DURATIon " + ph.info().totalCpuDuration().orElse(Duration.ZERO));
                        System.out.println("Arguments: " + String.join(",", ph.info().arguments().orElse(new String[]{"N/A"}) ));
                        System.out.println("Instance: " + ph.info().startInstant().get());
                        System.out.println("User: " + ph.info().user().orElse("N/A"));
                    }
                });
    }


    @Command(command = "mainServer", group = "Lifecycle", description = "Allows the user to ")
    public void mainServer(
            @Option(longNames = "component", shortNames = 'c', required = true, description = "asdf")  String component,
            @Option(longNames = "lifecycle-event", shortNames = 'e', description = "Allows to start, stop and check status of component", required = true) @OptionValues(provider = "lifecycleEventCompletionProvider") String event,
            @Option(longNames = "debug-port", shortNames = 'd', description = "Enables debugging on port passed as argument", required = false) String debugPort
    ){
        componentService.start(component);
        configurationResolver.getProperty(String.format("%s_NAMING_PROVIDER_URL",component.toUpperCase()));

//        StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
//        Future<?> future = executorService.submit(streamGobbler);
////
//        int exitCode = process.waitFor();
////
//        assertDoesNotThrow(() -> future.get(10, TimeUnit.SECONDS));
//        assertEquals(0, exitCode);
    }



    public static void destroyingProcessCreatedByDifferentProcess(long pid) {
        // find out the process id of current running task by checking
        // task manager in windows and enter the integer value
        Optional<ProcessHandle> optionalProcessHandle = ProcessHandle.of(pid);
        optionalProcessHandle.ifPresent(ProcessHandle::destroy);

    }

    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .forEach(consumer);
        }
    }
}
