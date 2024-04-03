package com.application.cli;

import com.application.cli.lifecyclecli.config.CustomExceptionResolver;
import com.application.cli.lifecyclecli.util.LifeCycleValuesProvider;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.command.annotation.CommandScan;
import org.springframework.shell.completion.CompletionProvider;
import org.springframework.shell.jline.PromptProvider;

@SpringBootApplication
@CommandScan
public class LifecycleCliApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(LifecycleCliApplication.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(LifecycleCliApplication.class, args);
    }

    @Bean
    CustomExceptionResolver customExceptionResolver() {
        return new CustomExceptionResolver();
    }

    @Bean
    public PromptProvider myPromptProvider() {
        return () -> new AttributedString("third-party-application-lifecycle-manager:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
    }

    @Bean
    CompletionProvider lifecycleEventCompletionProvider() {
        return ctx -> {
            LifeCycleValuesProvider lifeCycleValuesProvider = new LifeCycleValuesProvider();
            return lifeCycleValuesProvider.complete(ctx);
        };
    }

}
