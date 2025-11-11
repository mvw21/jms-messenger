package com.core;

import org.junit.jupiter.api.extension.ExtensionContext;

/**
 *
 * @author martin on 11/4/25
 */
public record TestEnvironmentStoreValue(IntegrationTestEnvironment environment) implements ExtensionContext.Store.CloseableResource, AutoCloseable {
    @Override
    public void close() {
        environment.shutdown();
    }

}
