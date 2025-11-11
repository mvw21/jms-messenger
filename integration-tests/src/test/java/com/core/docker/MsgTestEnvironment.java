package com.core.docker;

import com.core.IntegrationTestEnvironment;
import org.example.DaoProviderFactory;
import org.example.EndpointRegistry;

/**
 * @author martin on 18.02.22 г.
 */
public interface MsgTestEnvironment extends IntegrationTestEnvironment
{
    EndpointRegistry getEndpointRegistry();

    DaoProviderFactory getDaoProviderFactory();

}
