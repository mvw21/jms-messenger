package org.example.app.jms.endpoints;

import org.example.ChatEndpoint;
import org.example.app.jms.OperationInvoker;
import org.example.constants.OperationsNames;
import org.example.dtos.message.MessageDto;
import org.example.dtos.message.MessageRequestDto;
import org.example.dtos.message.MessagesFilterDto;
import org.example.dtos.message.MessagesWrapperDto;

import java.util.concurrent.CompletableFuture;

public class ChatEndpointImpl extends AbstractEndpointImpl implements ChatEndpoint
{
    public ChatEndpointImpl(OperationInvoker operationInvoker)
    {
        super(operationInvoker);
    }

    @Override
    public CompletableFuture<MessageDto> sendMessage(MessageRequestDto message)
    {
        return  operationInvoker.invoke(message, OperationsNames.SEND_MESSAGE);
    }

    @Override
    public CompletableFuture<MessagesWrapperDto> loadMessages(MessagesFilterDto filter)
    {
      return operationInvoker.invoke(filter, OperationsNames.LOAD_MESSAGES);
    }

    @Override
    public void close()
    {

    }
}
