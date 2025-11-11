package com.core.jms.chat;

import com.core.jms.AbstractJmsSupport;
import org.example.DaoProvider;
import org.example.MessageDao;
import org.example.UserDao;
import org.example.dtos.ErrorCodesDto;
import org.example.dtos.ErrorDto;
import org.example.dtos.message.MessageDto;
import org.example.dtos.message.MessageRequestDto;
import org.example.entities.Message;
import org.example.entities.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * @author martin on 24.02.22 г.
 */
class SendMessageEndpointIntegrationTestCase extends AbstractJmsSupport
{
    @Test
    void testSendMessageSuccess()
    {
        User author = new User(UUID.randomUUID());
        author.setUsername("author");
        author.setEmail("author@gmail.com");
        author.setPassword("112233");

        User recipient = new User(UUID.randomUUID());
        recipient.setUsername("recipient");
        recipient.setPassword("112233");
        recipient.setEmail("recipient@gmail.com");

        MessageRequestDto messageRequestDto = new MessageRequestDto();
        messageRequestDto.setContent("some content");
        messageRequestDto.setAuthorId(author.getId());
        messageRequestDto.setRecipientId(recipient.getId());

        try (DaoProvider daoProvider = daoProviderFactory.createDaoProvider())
        {
            UserDao userDao = daoProvider.getUserDao();
            userDao.saveOrUpdate(author);
            userDao.saveOrUpdate(recipient);
            daoProvider.commit();
        }

        invokeForSuccess(messageRequestDto, getSendMessageOperation());

        Message dbMessage;
        try (DaoProvider daoProvider = daoProviderFactory.createDaoProvider())
        {
            MessageDao messageDao = daoProvider.getMessageDao();
            dbMessage = messageDao.loadByAuthorId(messageRequestDto.getAuthorId());
        }
        Assertions.assertEquals(messageRequestDto.getAuthorId(),dbMessage.getAuthor().getId());
        Assertions.assertEquals(messageRequestDto.getRecipientId(),dbMessage.getRecipient().getId());
        Assertions.assertEquals(messageRequestDto.getContent(),dbMessage.getContent());
        Assertions.assertNotNull(dbMessage);

    }

    @Test
    void testSendMessageNullArg()
    {
        final ErrorDto error = invokeForError(null, getSendMessageOperation());
        Assertions.assertNotNull(error);
        Assertions.assertEquals(ErrorCodesDto.VALIDATION, error.getErrorCode());
        Assertions.assertEquals(1, error.getMessages().size());
        Assertions.assertEquals("Request can not be null.", error.getMessages().get(0));
    }

    private Function<MessageRequestDto, CompletableFuture<MessageDto>> getSendMessageOperation()
    {
        return endpointRegistry.getChatEndpoint()::sendMessage;
    }
}
