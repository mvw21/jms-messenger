package com.core;

import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Optional;

/**
 *
 * @author martin on 11/4/25
 */
public class EnvConfigProviderImpl implements EnvironmentConfig{
    private final ExtensionContext ctx;

    public EnvConfigProviderImpl(ExtensionContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public Optional<String> getValue(String configurationKey) {
        return ctx.getConfigurationParameter(configurationKey);
    }

}
