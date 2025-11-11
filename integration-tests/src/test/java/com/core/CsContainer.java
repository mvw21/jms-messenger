package com.core;

import org.testcontainers.containers.GenericContainer;

/**
 *
 * @author martin on 11/4/25
 */
public interface CsContainer {
    GenericContainer<?> getGenericContainer();

    String getNetworkAlias();

    String getContainerIpAddress();

    Integer getMappedPort();

    void logInfo();

    void logInfo(String name, String offset);

    String execInContainer(String... command);

    default void init() {

    }

}
