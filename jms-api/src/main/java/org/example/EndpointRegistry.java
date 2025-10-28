package org.example;

public interface EndpointRegistry extends AutoCloseable
{
    <T extends Endpoint> T getEndpoint(Class<T> endpointType);

    AdministrationEndpoint getAdministrationEndpoint();

    ChatEndpoint getChatEndpoint();
}
