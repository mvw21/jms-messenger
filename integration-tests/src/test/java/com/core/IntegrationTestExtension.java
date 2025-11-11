package com.core;

import com.core.docker.DockerContainersTestEnvironment;
import com.core.docker.MsgTestEnvironment;
import com.core.injector.BaseMsgUri;
import com.core.injector.MsgDaoProviderFactory;
import com.core.injector.MsgEndpointRegistry;
import org.example.DaoProviderFactory;
import org.example.EndpointRegistry;

import java.util.List;
import java.util.function.Function;

public class IntegrationTestExtension extends AbstractIntegrationTestExtension
{
    private static final Function<EnvironmentConfig, IntegrationTestEnvironment> ENVIRONMENT_SUPPLIER = DockerContainersTestEnvironment::new;

    private static final List<IntegrationTestValueDescriptor<?, ?>> TEST_VALUE_DESCRIPTORS = List.of(
            new IntegrationTestValueDescriptor<>(BaseMsgUri.class, String.class, (env, ann) -> env.getBaseRestUri()),
            new IntegrationTestValueDescriptor<>(MsgEndpointRegistry.class, EndpointRegistry.class, (env, ann) -> ((MsgTestEnvironment) env).getEndpointRegistry()),
            new IntegrationTestValueDescriptor<>(MsgDaoProviderFactory.class, DaoProviderFactory.class,(env, ann) -> ((MsgTestEnvironment) env).getDaoProviderFactory())
    );

    protected IntegrationTestExtension()
    {
        super(ENVIRONMENT_SUPPLIER);
    }

    @Override
    public List<IntegrationTestValueDescriptor<?, ?>> getTestValueDescriptors()
    {
        return TEST_VALUE_DESCRIPTORS;
    }
}
