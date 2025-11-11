package com.core.docker;

public interface MsgContainerConstants
{
    interface Messenger
    {
        String IMAGE_NAME = "messenger";
        int HTTP_PORT = 8080;
        int DEBUG_PORT = 8787;
    }

    interface Artemis
    {
        String IMAGE_NAME   = "apache/activemq-artemis:latest";
        int    JMS_TCP_PORT = 61616;
    }
}
