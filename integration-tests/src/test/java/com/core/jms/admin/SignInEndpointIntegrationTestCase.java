package com.core.jms.admin;

import com.core.jms.AbstractJmsSupport;
import com.core.jms.UserConstants;
import org.example.DaoProvider;
import org.example.UserDao;
import org.example.dtos.ErrorCodesDto;
import org.example.dtos.ErrorDto;
import org.example.dtos.user.SignInDto;
import org.example.dtos.user.UserDto;
import org.example.entities.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * @author martin on 24.02.22 г.
 */
class SignInEndpointIntegrationTestCase extends AbstractJmsSupport
{
    @Test
    void testSignInSuccess()
    {
        SignInDto request = new SignInDto(UserConstants.EMAIL, UserConstants.PASSWORD);

        User user = new User();
        user.setUsername("user1");
        user.setEmail(UserConstants.EMAIL);
        user.setPassword(UserConstants.PASSWORD);

        try (DaoProvider daoProvider = daoProviderFactory.createDaoProvider())
        {
            UserDao userDao = daoProvider.getUserDao();
            userDao.saveOrUpdate(user);
            daoProvider.commit();
        }

        invokeForSuccess(request, getSignInOperation());

        User dbUser;
        try (DaoProvider daoProvider = daoProviderFactory.createDaoProvider())
        {
            UserDao userDao = daoProvider.getUserDao();
            dbUser = userDao.loadByEmail(request.getEmail());
        }
        Assertions.assertNotNull(dbUser);
        Assertions.assertEquals(dbUser.getEmail(),dbUser.getEmail());

    }

    @Test
    void testSignInNullArg()
    {
        final ErrorDto error = invokeForError(null, getSignInOperation());
        Assertions.assertNotNull(error);
        Assertions.assertEquals(ErrorCodesDto.VALIDATION, error.getErrorCode());
        Assertions.assertEquals(1, error.getMessages().size());
        Assertions.assertEquals("Request can not be null.", error.getMessages().get(0));
    }

    private Function<SignInDto, CompletableFuture<UserDto>> getSignInOperation()
    {
        return endpointRegistry.getAdministrationEndpoint()::signIn;
    }
}
