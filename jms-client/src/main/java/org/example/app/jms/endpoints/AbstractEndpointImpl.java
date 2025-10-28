package org.example.app.jms.endpoints;

import org.example.app.jms.OperationInvoker;

public class AbstractEndpointImpl
{
    protected final OperationInvoker operationInvoker;

    public AbstractEndpointImpl(OperationInvoker operationInvoker)
    {
        this.operationInvoker = operationInvoker;
    }

}
