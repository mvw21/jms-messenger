package com.core;

/**
 *
 * @author martin on 11/4/25
 */
public interface IntegrationTestEnvironment {
    void bootstrap();

    void resetDatabaseData();

    void shutdown();

    String getBaseRestUri();

    CsStoreAccessor getStoreAccessor();

}
