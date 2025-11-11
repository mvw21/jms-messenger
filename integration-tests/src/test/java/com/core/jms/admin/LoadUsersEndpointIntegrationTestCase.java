package com.core.jms.admin;

import com.core.jms.AbstractJmsSupport;
import org.example.DaoProvider;
import org.example.UserDao;
import org.example.dtos.ErrorCodesDto;
import org.example.dtos.ErrorDto;
import org.example.dtos.user.UsersFilterDto;
import org.example.dtos.user.UsersWrapperDto;
import org.example.entities.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

class LoadUsersEndpointIntegrationTestCase extends AbstractJmsSupport
{
    @Test
    void testLoadUsersSuccess()
    {
        User user1 = new User();
        user1.setUsername("AngelCho");
        user1.setEmail("angel@gmail.com");
        user1.setPassword("angel-pass");

        User user2 = new User();
        user2.setUsername("TinCho");
        user2.setEmail("tincho@gmail.com");
        user2.setPassword("tincho-pass");

        UsersFilterDto usersFilterDto =  new UsersFilterDto();
        usersFilterDto.setUsername("cho");
        usersFilterDto.setStart(0);
        usersFilterDto.setOffset(5);

        try (DaoProvider daoProvider = daoProviderFactory.createDaoProvider())
        {
            UserDao userDao = daoProvider.getUserDao();
            userDao.saveOrUpdate(user1);
            userDao.saveOrUpdate(user2);
            daoProvider.commit();
        }

        UsersWrapperDto usersWrapperDto = invokeForSuccess(usersFilterDto, getLoadUsersOperation());

        List<User> users;
        try (DaoProvider daoProvider = daoProviderFactory.createDaoProvider())
        {
            UserDao userDao = daoProvider.getUserDao();
            users = userDao.loadByName(usersFilterDto.getUsername(), 0, 5);
        }

        Assertions.assertEquals(users.size(), usersWrapperDto.getUsers().size());
        Assertions.assertEquals(users.get(0).getUsername(), usersWrapperDto.getUsers().get(0).getUsername());
        Assertions.assertEquals(2,users.size());
    }

    @Test
    void testLoadUsersNullArg()
    {
        final ErrorDto error = invokeForError(null, getLoadUsersOperation());
        Assertions.assertNotNull(error);
        Assertions.assertEquals(ErrorCodesDto.VALIDATION, error.getErrorCode());
        Assertions.assertEquals(1, error.getMessages().size());
        Assertions.assertEquals("Request can not be null.", error.getMessages().get(0));
    }

    private Function<UsersFilterDto, CompletableFuture<UsersWrapperDto>> getLoadUsersOperation()
    {
        return endpointRegistry.getAdministrationEndpoint()::loadUsers;
    }
}
