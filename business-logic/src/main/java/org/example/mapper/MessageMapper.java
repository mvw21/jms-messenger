package org.example.mapper;


import org.example.dtos.message.MessageDto;
import org.example.dtos.message.MessageRequestDto;
import org.example.dtos.user.UserDescriptionDto;
import org.example.entities.Message;
import org.example.entities.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessageMapper
{
    public static final MessageMapper instance = new MessageMapper();

    public MessageMapper()
    {
    }

    public Message mapMessage(MessageRequestDto messageRequestDto, User author, User recipient)
    {
        Message message = new Message(UUID.randomUUID());
        message.setAuthor(author);
        message.setRecipient(recipient);
        message.setContent(messageRequestDto.getContent());
        message.setSentAt(LocalDateTime.now(ZoneOffset.UTC)
                .truncatedTo(ChronoUnit.SECONDS)
        );
        return message;
    }

    public MessageDto mapMessageToDtoMessage(Message message)
    {
        UserDescriptionDto author = new UserDescriptionDto();
        UserDescriptionDto recipient = new UserDescriptionDto();

        author.setId(message.getAuthor().getId());
        author.setUsername(message.getAuthor().getUsername());
        author.setEmail(message.getAuthor().getEmail());

        recipient.setId(message.getRecipient().getId());
        recipient.setUsername(message.getRecipient().getUsername());
        recipient.setEmail(message.getRecipient().getEmail());

        MessageDto messageDto = new MessageDto();
        messageDto.setId(message.getId());
        messageDto.setAuthor(author);
        messageDto.setRecipient(recipient);
        messageDto.setContent(message.getContent());
        messageDto.setSentAt(message.getSentAt());

        return messageDto;
    }

    public List<MessageDto> mapListOfMessagesToDtoMessages(List<Message> messages)
    {
        List<MessageDto> messagesDtos = new ArrayList<>();

        for (Message message : messages)
        {
            MessageDto messageDto = mapMessageToDtoMessage(message);
            messagesDtos.add(messageDto);
        }

        return messagesDtos;
    }
}
