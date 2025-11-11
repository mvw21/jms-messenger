package com.core.jms.admin;

import com.core.jms.AbstractJmsSupport;
import com.core.jms.UserConstants;
import org.example.DaoProvider;
import org.example.UserDao;
import org.example.dtos.ErrorCodesDto;
import org.example.dtos.ErrorDto;
import org.example.dtos.user.SignUpDto;
import org.example.entities.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * @author kamen on 18.02.22 г.
 */

class SignUpEndpointIntegrationTestCase extends AbstractJmsSupport
{
    @Test
    void testSignUpSuccess()
    {
        SignUpDto request = new SignUpDto(UserConstants.EMAIL, UserConstants.USERNAME, UserConstants.PASSWORD);
        invokeForSuccess(request, getSignUpOperation());

        User dbUser;
        try (DaoProvider daoProvider = daoProviderFactory.createDaoProvider())
        {
            UserDao userDao = daoProvider.getUserDao();
            dbUser = userDao.loadByEmail(request.getEmail());
        }
        Assertions.assertNotNull(dbUser);

    }

    @Test
    void testSignUpNullArg()
    {
        final ErrorDto error = invokeForError(null, getSignUpOperation());
        Assertions.assertNotNull(error);
        Assertions.assertEquals(ErrorCodesDto.VALIDATION, error.getErrorCode());
        Assertions.assertEquals(1, error.getMessages().size());
        Assertions.assertEquals("Request can not be null.", error.getMessages().get(0));
    }

    private Function<SignUpDto, CompletableFuture<Void>> getSignUpOperation()
    {
        return endpointRegistry.getAdministrationEndpoint()::signUp;
    }
}
