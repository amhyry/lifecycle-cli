package com.application.cli.lifecyclecli.util;

import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.standard.ValueProvider;

import java.util.Arrays;
import java.util.List;

public class LifeCycleValuesProvider implements ValueProvider {
    private static final String[] VALUES = new String[]{
            "start",
            "status",
            "stop",
            "restart"
    };

    @Override
    public List<CompletionProposal> complete(CompletionContext completionContext) {
        return Arrays.stream(VALUES)
                .map(CompletionProposal::new).toList();
    }
}
