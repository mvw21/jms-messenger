package org.example;


import org.example.dtos.message.MessageDto;
import org.example.dtos.message.MessageRequestDto;
import org.example.dtos.message.MessagesFilterDto;
import org.example.dtos.message.MessagesWrapperDto;

import java.util.concurrent.CompletableFuture;

public interface ChatEndpoint extends Endpoint
{
    CompletableFuture<MessageDto> sendMessage(MessageRequestDto message);
    CompletableFuture<MessagesWrapperDto> loadMessages(MessagesFilterDto filter);

}
