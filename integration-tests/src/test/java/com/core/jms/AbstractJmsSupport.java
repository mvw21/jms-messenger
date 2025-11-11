package com.core.jms;

import com.core.IntegrationTest;
import com.core.injector.MsgDaoProviderFactory;
import com.core.injector.MsgEndpointRegistry;
import org.example.DaoProviderFactory;
import org.example.EndpointRegistry;
import org.example.dtos.ErrorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * @author kamen on 18.02.22 г.
 */
@IntegrationTest
public class AbstractJmsSupport
{
    private static final Logger logger          = LoggerFactory.getLogger(AbstractJmsSupport.class);

    protected static final int  TIMEOUT_SECONDS = 5; // Wait 5 seconds at most

    @MsgEndpointRegistry
    protected EndpointRegistry endpointRegistry;

    @MsgDaoProviderFactory
    protected DaoProviderFactory daoProviderFactory;

    protected <Req, Resp> Resp invokeForSuccess(Req req, Function<Req, CompletableFuture<Resp>> operation)
    {
        final var successListener = new AssertingChannelListener<Resp>();
        final CompletableFuture<Resp> completableFuture = operation.apply(req);
        successListener.waitForSuccess(completableFuture, TIMEOUT_SECONDS);
        return successListener.getResponse();
    }

    protected <Req, Resp> ErrorDto invokeForError(Req req, Function<Req, CompletableFuture<Resp>> operation)
    {
        final AssertingChannelListener<Resp> successListener = new AssertingChannelListener<>();
        final CompletableFuture<Resp> completableFuture = operation.apply(req);
        return successListener.waitForError(completableFuture, TIMEOUT_SECONDS);
    }
}
